package com.mobileone.shared.di

import com.mobileone.shared.domain.entity.AuthToken
import com.mobileone.shared.domain.error.AuthDomainError
import com.mobileone.shared.domain.error.toAuthDomainError
import com.mobileone.shared.domain.repository.SessionRepository
import com.mobileone.shared.domain.usecase.LoginWithBiometricUseCase
import com.mobileone.shared.domain.usecase.LoginWithCredentialsUseCase
import com.mobileone.shared.domain.usecase.SetupBiometricLoginUseCase
import com.mobileone.shared.security.BiometricAuthenticator
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

    private fun Result<AuthToken>.toOutcome(): AuthTokenOutcome = fold(
        onSuccess = { AuthTokenOutcome.success(it) },
        onFailure = { AuthTokenOutcome.failure(it.toAuthDomainError()) }
    )
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
