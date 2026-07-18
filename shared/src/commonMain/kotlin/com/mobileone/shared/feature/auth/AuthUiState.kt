package com.mobileone.shared.feature.auth

import com.mobileone.shared.domain.error.AuthDomainError

/**
 * Estado de UI do fluxo de login/biometria (SPEC-001), consumido igualmente pelo
 * `AuthViewModel` do Android (Compose) e do iOS (SwiftUI).
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val isBiometricAvailable: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val failedAttempts: Int = 0,
    val isAccountLocked: Boolean = false,
    val lockRemainingSeconds: Int = 0,
    val userName: String? = null,
    val error: AuthError? = null,
    val navigation: AuthNavigation? = null
)

sealed class AuthError {
    object InvalidCredentials : AuthError()
    object AccountLocked : AuthError()
    object BiometricFailed : AuthError()
    object NetworkError : AuthError()
    data class UnknownError(val message: String) : AuthError()
}

sealed class AuthNavigation {
    object ToHome : AuthNavigation()
    object ToBiometricSetup : AuthNavigation()
}

/** Reduz o [AuthDomainError] (rico, interno) para o [AuthError] exposto à UI (SPEC-001). */
fun AuthDomainError.toUiError(): AuthError = when (this) {
    is AuthDomainError.InvalidCredentials -> AuthError.InvalidCredentials
    is AuthDomainError.AccountLocked -> AuthError.AccountLocked
    is AuthDomainError.NetworkError -> AuthError.NetworkError
    is AuthDomainError.BiometricNotAvailable,
    is AuthDomainError.BiometricBlocked,
    is AuthDomainError.BiometricFailed -> AuthError.BiometricFailed
    is AuthDomainError.CompromisedDevice -> AuthError.UnknownError(reason)
    is AuthDomainError.Validation -> AuthError.UnknownError(reason)
    is AuthDomainError.Unknown -> AuthError.UnknownError(message)
}
