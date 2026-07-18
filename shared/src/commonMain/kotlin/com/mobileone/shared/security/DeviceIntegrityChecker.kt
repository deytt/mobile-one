package com.mobileone.shared.security

/** Resultado da checagem de integridade do dispositivo (ADR-005). */
data class IntegrityStatus(
    val isRooted: Boolean,
    val isEmulator: Boolean,
    val isDebuggable: Boolean
)

/**
 * Checagem simples de root/jailbreak — sem libs externas (ex: RootBeer), ver ADR-005 e decisão
 * registrada na SPEC-001. Interface para permitir fake em teste — ver nota em
 * [BiometricAuthenticator].
 */
interface DeviceIntegrityChecker {
    suspend fun check(): IntegrityStatus
}
