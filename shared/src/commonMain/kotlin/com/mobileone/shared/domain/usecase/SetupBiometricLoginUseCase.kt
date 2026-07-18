package com.mobileone.shared.domain.usecase

import com.mobileone.shared.domain.error.AuthDomainError
import com.mobileone.shared.domain.error.asFailure
import com.mobileone.shared.security.AuthSecureStorageKeys
import com.mobileone.shared.security.BiometricAuthenticator
import com.mobileone.shared.security.BiometricResult
import com.mobileone.shared.security.SecureStorage

/**
 * Ativa o login por biometria (SPEC-001): confirma a biometria uma vez e persiste a flag em
 * [SecureStorage] — lida depois por `SessionRepository.isBiometricEnabled()`.
 */
class SetupBiometricLoginUseCase(
    private val biometricAuth: BiometricAuthenticator,
    private val secureStorage: SecureStorage
) {
    suspend operator fun invoke(): Result<Unit> {
        if (!biometricAuth.isAvailable()) {
            return AuthDomainError.BiometricNotAvailable.asFailure()
        }
        return when (biometricAuth.authenticate("Ative o login por biometria")) {
            is BiometricResult.Success -> {
                secureStorage.put(AuthSecureStorageKeys.BIOMETRIC_ENABLED, "true")
                Result.success(Unit)
            }
            is BiometricResult.TooManyAttempts -> AuthDomainError.BiometricBlocked.asFailure()
            is BiometricResult.UserCancelled,
            is BiometricResult.Error -> AuthDomainError.BiometricFailed.asFailure()
        }
    }
}
