package com.mobileone.shared.data.repository

import com.mobileone.shared.domain.entity.AuthToken
import com.mobileone.shared.domain.repository.LoginAttemptStatus
import com.mobileone.shared.domain.repository.SessionRepository
import com.mobileone.shared.security.AuthSecureStorageKeys
import com.mobileone.shared.security.SecureStorage
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.TimeSource

/**
 * Sessão + tentativas/bloqueio de login (SPEC-001). Token/nome/flag de biometria persistem via
 * [SecureStorage]; o contador de tentativas e o bloqueio de 5 minutos ficam em memória — reset
 * ao reiniciar o processo é uma limitação aceitável para a POC.
 */
class SessionRepositoryImpl(
    private val secureStorage: SecureStorage
) : SessionRepository {

    private val mutex = Mutex()
    private var failedAttempts = 0
    private var lockedAt: TimeSource.Monotonic.ValueTimeMark? = null

    override suspend fun startSession(token: AuthToken) {
        secureStorage.put(AuthSecureStorageKeys.ACCESS_TOKEN, token.accessToken)
        secureStorage.put(AuthSecureStorageKeys.REFRESH_TOKEN, token.refreshToken)
        secureStorage.put(AuthSecureStorageKeys.USER_ID, token.userId)
        secureStorage.put(AuthSecureStorageKeys.USER_NAME, token.userName)
    }

    override suspend fun endSession() {
        secureStorage.delete(AuthSecureStorageKeys.ACCESS_TOKEN)
        secureStorage.delete(AuthSecureStorageKeys.REFRESH_TOKEN)
        secureStorage.delete(AuthSecureStorageKeys.USER_ID)
    }

    override suspend fun currentSession(): AuthToken? {
        val accessToken = secureStorage.get(AuthSecureStorageKeys.ACCESS_TOKEN) ?: return null
        val refreshToken = secureStorage.get(AuthSecureStorageKeys.REFRESH_TOKEN) ?: return null
        val userId = secureStorage.get(AuthSecureStorageKeys.USER_ID) ?: return null
        val userName = secureStorage.get(AuthSecureStorageKeys.USER_NAME).orEmpty()
        return AuthToken(accessToken, refreshToken, userId, userName, expiresAtEpochMillis = 0L)
    }

    override suspend fun extendSession() {
        // Ponto de extensão para renovação de token quando houver integração com backend.
    }

    override suspend fun currentUserName(): String? =
        secureStorage.get(AuthSecureStorageKeys.USER_NAME)

    override suspend fun isBiometricEnabled(): Boolean =
        secureStorage.get(AuthSecureStorageKeys.BIOMETRIC_ENABLED) == "true"

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        if (enabled) {
            secureStorage.put(AuthSecureStorageKeys.BIOMETRIC_ENABLED, "true")
        } else {
            secureStorage.delete(AuthSecureStorageKeys.BIOMETRIC_ENABLED)
        }
    }

    override suspend fun lockStatus(): LoginAttemptStatus = mutex.withLock { currentStatusLocked() }

    override suspend fun registerFailedAttempt(): LoginAttemptStatus = mutex.withLock {
        val status = currentStatusLocked()
        if (status is LoginAttemptStatus.Locked) return@withLock status

        failedAttempts += 1
        if (failedAttempts >= MAX_ATTEMPTS) {
            lockedAt = TimeSource.Monotonic.markNow()
            LoginAttemptStatus.Locked(LOCK_DURATION_SECONDS)
        } else {
            LoginAttemptStatus.Allowed(failedAttempts, MAX_ATTEMPTS - failedAttempts)
        }
    }

    override suspend fun resetFailedAttempts() = mutex.withLock {
        failedAttempts = 0
        lockedAt = null
    }

    private fun currentStatusLocked(): LoginAttemptStatus {
        val lockedMark = lockedAt
        if (lockedMark != null) {
            val remaining = LOCK_DURATION_SECONDS - lockedMark.elapsedNow().inWholeSeconds.toInt()
            if (remaining > 0) return LoginAttemptStatus.Locked(remaining)
            lockedAt = null
            failedAttempts = 0
        }
        return LoginAttemptStatus.Allowed(failedAttempts, MAX_ATTEMPTS - failedAttempts)
    }

    companion object {
        const val MAX_ATTEMPTS = 5
        const val LOCK_DURATION_SECONDS = 5 * 60
    }
}
