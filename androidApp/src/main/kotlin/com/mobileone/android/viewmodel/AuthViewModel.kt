package com.mobileone.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileone.shared.domain.error.AuthDomainError
import com.mobileone.shared.domain.error.toAuthDomainError
import com.mobileone.shared.domain.repository.SessionRepository
import com.mobileone.shared.domain.usecase.LoginWithBiometricUseCase
import com.mobileone.shared.domain.usecase.LoginWithCredentialsUseCase
import com.mobileone.shared.domain.usecase.SetupBiometricLoginUseCase
import com.mobileone.shared.feature.auth.AuthNavigation
import com.mobileone.shared.feature.auth.AuthUiState
import com.mobileone.shared.feature.auth.toUiError
import com.mobileone.shared.security.BiometricAuthenticator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Consome os use cases de login/biometria do `shared` (SPEC-001) e expõe [AuthUiState] — o
 * mesmo contrato consumido pelo ViewModel equivalente do iOS — via `StateFlow`, seguindo
 * `.cursor/rules/05-android-conventions.mdc`.
 */
class AuthViewModel(
    private val loginWithCredentials: LoginWithCredentialsUseCase,
    private val loginWithBiometric: LoginWithBiometricUseCase,
    private val setupBiometricLogin: SetupBiometricLoginUseCase,
    private val sessionRepository: SessionRepository,
    private val biometricAuthenticator: BiometricAuthenticator
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        refreshBiometricState()
    }

    fun refreshBiometricState() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isBiometricAvailable = biometricAuthenticator.isAvailable(),
                    isBiometricEnabled = sessionRepository.isBiometricEnabled()
                )
            }
        }
    }

    fun onLoginClick(cpf: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            loginWithCredentials(cpf, password)
                .onSuccess { onAuthenticated(offerBiometricSetup = true) }
                .onFailure { onLoginFailed(it) }
        }
    }

    fun onBiometricLoginClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            loginWithBiometric()
                .onSuccess { onAuthenticated(offerBiometricSetup = false) }
                .onFailure { failure ->
                    _uiState.update {
                        it.copy(isLoading = false, error = failure.toAuthDomainError().toUiError())
                    }
                }
        }
    }

    fun onSetupBiometricConfirm() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            setupBiometricLogin()
                .onSuccess {
                    sessionRepository.setBiometricEnabled(true)
                    _uiState.update {
                        it.copy(isLoading = false, isBiometricEnabled = true, navigation = AuthNavigation.ToHome)
                    }
                }
                .onFailure { failure ->
                    _uiState.update {
                        it.copy(isLoading = false, error = failure.toAuthDomainError().toUiError())
                    }
                }
        }
    }

    fun onSkipBiometricSetup() {
        _uiState.update { it.copy(navigation = AuthNavigation.ToHome) }
    }

    fun onConsumeNavigation() {
        _uiState.update { it.copy(navigation = null) }
    }

    fun onDismissError() {
        _uiState.update { it.copy(error = null) }
    }

    /** Encerra a sessão local. */
    fun onLogoutClick() {
        viewModelScope.launch {
            sessionRepository.endSession()
            sessionRepository.resetFailedAttempts()
            _uiState.value = AuthUiState(
                isBiometricAvailable = _uiState.value.isBiometricAvailable,
                isBiometricEnabled = sessionRepository.isBiometricEnabled()
            )
        }
    }

    private suspend fun onAuthenticated(offerBiometricSetup: Boolean) {
        val shouldOfferSetup = offerBiometricSetup &&
            _uiState.value.isBiometricAvailable &&
            !_uiState.value.isBiometricEnabled
        val userName = sessionRepository.currentUserName()
        _uiState.update {
            it.copy(
                isLoading = false,
                failedAttempts = 0,
                isAccountLocked = false,
                lockRemainingSeconds = 0,
                userName = userName,
                navigation = if (shouldOfferSetup) AuthNavigation.ToBiometricSetup else AuthNavigation.ToHome
            )
        }
    }

    private fun onLoginFailed(failure: Throwable) {
        val domainError = failure.toAuthDomainError()
        _uiState.update { current ->
            when (domainError) {
                is AuthDomainError.InvalidCredentials -> current.copy(
                    isLoading = false,
                    error = domainError.toUiError(),
                    failedAttempts = domainError.failedAttempts,
                    isAccountLocked = false
                )
                is AuthDomainError.AccountLocked -> current.copy(
                    isLoading = false,
                    error = domainError.toUiError(),
                    isAccountLocked = true,
                    lockRemainingSeconds = domainError.remainingSeconds
                )
                else -> current.copy(isLoading = false, error = domainError.toUiError())
            }
        }
    }
}
