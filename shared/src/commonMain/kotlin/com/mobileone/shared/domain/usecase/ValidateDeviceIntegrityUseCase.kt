package com.mobileone.shared.domain.usecase

import com.mobileone.shared.domain.error.AuthDomainError
import com.mobileone.shared.domain.error.asFailure
import com.mobileone.shared.security.DeviceIntegrityChecker

/**
 * Bloqueia o fluxo de autenticação em dispositivos comprometidos (root/emulador), ver ADR-005.
 */
class ValidateDeviceIntegrityUseCase(
    private val checker: DeviceIntegrityChecker
) {
    suspend operator fun invoke(): Result<Unit> {
        val status = checker.check()
        return if (status.isRooted) {
            AuthDomainError.CompromisedDevice("Dispositivo com root detectado").asFailure()
        } else {
            Result.success(Unit)
        }
    }
}
