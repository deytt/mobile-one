package com.mobileone.shared.domain.usecase

import com.mobileone.shared.domain.entity.AuthToken
import com.mobileone.shared.domain.error.AuthDomainError
import com.mobileone.shared.domain.error.asFailure
import com.mobileone.shared.domain.repository.SessionRepository
import com.mobileone.shared.security.BiometricAuthenticator
import com.mobileone.shared.security.BiometricResult

/**
 * Login por biometria (SPEC-001/ADR-005): reautentica a sessão já persistida via biometria
 * nativa, sem exigir CPF/senha novamente.
 */
class LoginWithBiometricUseCase(
    private val biometricAuth: BiometricAuthenticator,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(): Result<AuthToken> {
        if (!biometricAuth.isAvailable()) {
            return AuthDomainError.BiometricNotAvailable.asFailure()
        }

        val session = sessionRepository.currentSession()
            ?: return AuthDomainError.BiometricFailed.asFailure()

        return when (biometricAuth.authenticate("Confirme sua identidade")) {
            is BiometricResult.Success -> {
                sessionRepository.resetFailedAttempts()
                sessionRepository.extendSession()
                Result.success(session)
            }
            is BiometricResult.TooManyAttempts -> {
                sessionRepository.endSession()
                AuthDomainError.BiometricBlocked.asFailure()
            }
            is BiometricResult.UserCancelled,
            is BiometricResult.Error -> AuthDomainError.BiometricFailed.asFailure()
        }
    }
}
