package com.mobileone.shared.domain.repository

import com.mobileone.shared.domain.entity.AuthToken

/** Estado do contador de tentativas de login (SPEC-001 — máximo 5, bloqueio de 5 minutos). */
sealed class LoginAttemptStatus {
    data class Allowed(val failedAttempts: Int, val attemptsRemaining: Int) : LoginAttemptStatus()
    data class Locked(val remainingSeconds: Int) : LoginAttemptStatus()
}

/**
 * Sessão do usuário autenticado + contador de tentativas/bloqueio (SPEC-001). A implementação
 * persiste o necessário via [com.mobileone.shared.security.SecureStorage] — ver
 * `com.mobileone.shared.data.repository.SessionRepositoryImpl`.
 */
interface SessionRepository {
    suspend fun startSession(token: AuthToken)
    suspend fun endSession()
    suspend fun currentSession(): AuthToken?
    suspend fun extendSession()
    suspend fun currentUserName(): String?
    suspend fun isBiometricEnabled(): Boolean
    suspend fun setBiometricEnabled(enabled: Boolean)
    suspend fun lockStatus(): LoginAttemptStatus
    suspend fun registerFailedAttempt(): LoginAttemptStatus
    suspend fun resetFailedAttempts()
}
