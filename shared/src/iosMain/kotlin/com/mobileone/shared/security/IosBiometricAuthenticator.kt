@file:OptIn(ExperimentalForeignApi::class)

package com.mobileone.shared.security

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics
import kotlin.coroutines.resume

/**
 * Biometria real via `LocalAuthentication` (Face ID/Touch ID) — ADR-005. Um novo `LAContext` é
 * criado a cada tentativa: reutilizar a mesma instância faz o sistema repetir um sucesso
 * anterior sem checar o sensor de novo.
 */
class IosBiometricAuthenticator : BiometricAuthenticator {

    override suspend fun isAvailable(): Boolean =
        LAContext().canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, null)

    override suspend fun authenticate(reason: String): BiometricResult =
        suspendCancellableCoroutine { continuation ->
            LAContext().evaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, reason) { success, error ->
                val result = when {
                    success -> BiometricResult.Success
                    error != null -> error.toBiometricResult()
                    else -> BiometricResult.Error("Falha desconhecida na biometria")
                }
                if (continuation.isActive) continuation.resume(result)
            }
        }

    /**
     * Códigos de `LAError.Code` (Apple `LocalAuthentication/LAError.h`), comparados pelo valor
     * bruto para não depender do binding exato gerado pelo cinterop para o enum.
     */
    private fun NSError.toBiometricResult(): BiometricResult = when (code.toInt()) {
        LA_ERROR_USER_CANCEL, LA_ERROR_USER_FALLBACK, LA_ERROR_SYSTEM_CANCEL, LA_ERROR_APP_CANCEL ->
            BiometricResult.UserCancelled
        LA_ERROR_BIOMETRY_LOCKOUT -> BiometricResult.TooManyAttempts
        else -> BiometricResult.Error(localizedDescription)
    }

    companion object {
        private const val LA_ERROR_USER_CANCEL = -2
        private const val LA_ERROR_USER_FALLBACK = -3
        private const val LA_ERROR_SYSTEM_CANCEL = -4
        private const val LA_ERROR_BIOMETRY_LOCKOUT = -8
        private const val LA_ERROR_APP_CANCEL = -9
    }
}
