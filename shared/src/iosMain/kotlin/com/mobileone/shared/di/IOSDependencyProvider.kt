package com.mobileone.shared.di

import com.mobileone.shared.domain.entity.Account
import com.mobileone.shared.domain.entity.AuthToken
import com.mobileone.shared.domain.entity.TransactionPage
import com.mobileone.shared.domain.error.AuthDomainError
import com.mobileone.shared.domain.error.toAuthDomainError
import com.mobileone.shared.domain.repository.SessionRepository
import com.mobileone.shared.domain.usecase.GetTransactionHistoryUseCase
import com.mobileone.shared.domain.usecase.LoginWithBiometricUseCase
import com.mobileone.shared.domain.usecase.LoginWithCredentialsUseCase
import com.mobileone.shared.domain.usecase.ObserveAccountUseCase
import com.mobileone.shared.domain.usecase.RefreshAccountDataUseCase
import com.mobileone.shared.domain.usecase.SetupBiometricLoginUseCase
import com.mobileone.shared.domain.usecase.SwitchBrandUseCase
import com.mobileone.shared.domain.usecase.ToggleBalanceVisibilityUseCase
import com.mobileone.shared.data.repository.FakeAccountRepository
import com.mobileone.shared.config.AppStateRepository
import com.mobileone.shared.config.BrandCatalog
import com.mobileone.shared.config.WhiteLabelConfig
import com.mobileone.shared.security.BiometricAuthenticator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.mp.KoinPlatformTools

/**
 * Ponte de DI + apresentação para o iosApp (SwiftUI). Não existia bootstrap de Koin no iOS
 * antes da SPEC-001; [doInitKoin] deve ser chamado uma única vez em `iosAppApp.swift`.
 *
 * `Result<T>` (e genéricos em geral) degradam para `Any?` ao cruzar a ponte Objective-C — uma
 * limitação conhecida do interop Kotlin/Native/Swift. Por isso as funções abaixo nunca expõem
 * `Result`/use cases crus: devolvem [AuthTokenOutcome]/[AuthActionOutcome], tipos concretos
 * (sem generics) que o Swift consegue padrão-casar normalmente com `if`/`as?`.
 */
object IOSDependencyProvider : KoinComponent {

    fun doInitKoin() {
        if (KoinPlatformTools.defaultContext().getOrNull() != null) return
        initKoin()
    }

    private val loginWithCredentialsUseCase: LoginWithCredentialsUseCase get() = get()
    private val loginWithBiometricUseCase: LoginWithBiometricUseCase get() = get()
    private val setupBiometricLoginUseCase: SetupBiometricLoginUseCase get() = get()
    private val sessionRepository: SessionRepository get() = get()
    private val biometricAuthenticator: BiometricAuthenticator get() = get()

    suspend fun loginWithCredentials(cpf: String, password: String): AuthTokenOutcome =
        loginWithCredentialsUseCase(cpf, password).toOutcome()

    suspend fun loginWithBiometric(): AuthTokenOutcome =
        loginWithBiometricUseCase().toOutcome()

    suspend fun setupBiometricLogin(): AuthActionOutcome =
        setupBiometricLoginUseCase().fold(
            onSuccess = { AuthActionOutcome.success() },
            onFailure = { AuthActionOutcome.failure(it.toAuthDomainError()) }
        )

    suspend fun isBiometricAvailable(): Boolean = biometricAuthenticator.isAvailable()

    suspend fun isBiometricEnabled(): Boolean = sessionRepository.isBiometricEnabled()

    suspend fun setBiometricEnabled(enabled: Boolean) = sessionRepository.setBiometricEnabled(enabled)

    suspend fun currentUserName(): String? = sessionRepository.currentUserName()

    suspend fun logout() {
        sessionRepository.endSession()
        sessionRepository.resetFailedAttempts()
    }

    // ── Home / Extrato (SPEC-002) ─────────────────────────────────────────────────

    private val observeAccountUseCase: ObserveAccountUseCase get() = get()
    private val getTransactionHistoryUseCase: GetTransactionHistoryUseCase get() = get()
    private val refreshAccountDataUseCase: RefreshAccountDataUseCase get() = get()
    private val toggleBalanceVisibilityUseCase: ToggleBalanceVisibilityUseCase get() = get()
    private val appStateRepository: AppStateRepository get() = get()
    private val switchBrandUseCase: SwitchBrandUseCase get() = get()

    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    /**
     * Observa a conta via Flow e dispara [onUpdate] a cada nova emissão.
     * Retorna um [FlowCanceller] — chame [FlowCanceller.cancel] para parar a coleta.
     */
    fun watchAccount(onUpdate: (Account) -> Unit): FlowCanceller {
        val job: Job = ioScope.launch {
            observeAccountUseCase(FakeAccountRepository.DEMO_ACCOUNT_ID).collect { onUpdate(it) }
        }
        return FlowCanceller { job.cancel() }
    }

    /**
     * Observa o estado de visibilidade do saldo e dispara [onUpdate] a cada mudança.
     */
    fun watchBalanceHidden(onUpdate: (Boolean) -> Unit): FlowCanceller {
        val job: Job = ioScope.launch {
            toggleBalanceVisibilityUseCase.observe().collect { onUpdate(it) }
        }
        return FlowCanceller { job.cancel() }
    }

    /**
     * Observa a configuração de marca ativa e dispara [onUpdate] a cada troca.
     */
    fun watchCurrentConfig(onUpdate: (WhiteLabelConfig) -> Unit): FlowCanceller {
        val job: Job = ioScope.launch {
            appStateRepository.currentConfig.collect { onUpdate(it) }
        }
        return FlowCanceller { job.cancel() }
    }

    suspend fun getTransactions(accountId: String, page: Int, pageSize: Int): TransactionPageOutcome =
        getTransactionHistoryUseCase(accountId, page, pageSize).fold(
            onSuccess = { TransactionPageOutcome.success(it) },
            onFailure = { TransactionPageOutcome.failure(it.message ?: "Erro desconhecido") }
        )

    suspend fun refreshAccountData(accountId: String): Boolean =
        refreshAccountDataUseCase(accountId).isSuccess

    suspend fun toggleBalanceVisibility() = toggleBalanceVisibilityUseCase()

    // ── Brand Switcher (SPEC-004) ──────────────────────────────────────────────

    fun currentBrandId(): String = appStateRepository.currentConfig.value.brandId

    fun allBrands(): List<WhiteLabelConfig> = BrandCatalog.all()

    suspend fun switchBrand(brandId: String) = switchBrandUseCase(brandId)

    // ──────────────────────────────────────────────────────────────────────────

    private fun Result<AuthToken>.toOutcome(): AuthTokenOutcome = fold(
        onSuccess = { AuthTokenOutcome.success(it) },
        onFailure = { AuthTokenOutcome.failure(it.toAuthDomainError()) }
    )
}

/** Handle para cancelar a coleta de um Flow em Swift (SPEC-002). */
class FlowCanceller(private val cancel: () -> Unit) {
    fun cancel() = cancel.invoke()
}

/** Wrapper concreto (sem generics) para o desfecho de login, cruzando a ponte Swift. */
class AuthTokenOutcome private constructor(
    val token: AuthToken?,
    val error: AuthDomainError?
) {
    val isSuccess: Boolean get() = token != null

    companion object {
        fun success(token: AuthToken) = AuthTokenOutcome(token, null)
        fun failure(error: AuthDomainError) = AuthTokenOutcome(null, error)
    }
}

/** Wrapper concreto para ações sem valor de retorno (ex: `setupBiometricLogin`). */
class AuthActionOutcome private constructor(
    val isSuccess: Boolean,
    val error: AuthDomainError?
) {
    companion object {
        fun success() = AuthActionOutcome(true, null)
        fun failure(error: AuthDomainError) = AuthActionOutcome(false, error)
    }
}

/** Wrapper concreto para o resultado de uma página de transações (SPEC-002). */
class TransactionPageOutcome private constructor(
    val page: TransactionPage?,
    val errorMessage: String?
) {
    val isSuccess: Boolean get() = page != null

    companion object {
        fun success(page: TransactionPage) = TransactionPageOutcome(page, null)
        fun failure(message: String) = TransactionPageOutcome(null, message)
    }
}
