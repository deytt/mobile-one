package com.mobileone.shared.data.repository

import com.mobileone.shared.domain.entity.AuthToken
import com.mobileone.shared.domain.repository.LoginAttemptStatus
import com.mobileone.shared.testdouble.InMemorySecureStorage
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SessionRepositoryImplTest {

    private val secureStorage = InMemorySecureStorage()
    private val sessionRepository = SessionRepositoryImpl(secureStorage)
    private val token = AuthToken("access", "refresh", "user-1", "Heitor Bastos", 0L)

    @Test
    fun startSessionDevePersistirDadosRecuperaveisEmCurrentSession() = runBlocking<Unit> {
        sessionRepository.startSession(token)

        val session = sessionRepository.currentSession()
        assertEquals(token.userName, session?.userName)
        assertEquals(token.accessToken, session?.accessToken)
    }

    @Test
    fun endSessionDeveLimparSessaoPersistida() = runBlocking<Unit> {
        sessionRepository.startSession(token)
        sessionRepository.endSession()

        assertNull(sessionRepository.currentSession())
    }

    @Test
    fun setBiometricEnabledDevePersistirELimparAFlag() = runBlocking<Unit> {
        assertFalse(sessionRepository.isBiometricEnabled())

        sessionRepository.setBiometricEnabled(true)
        assertTrue(sessionRepository.isBiometricEnabled())

        sessionRepository.setBiometricEnabled(false)
        assertFalse(sessionRepository.isBiometricEnabled())
    }

    @Test
    fun registerFailedAttemptDeveContarTentativasEBloquearNaQuinta() = runBlocking<Unit> {
        repeat(4) {
            val status = sessionRepository.registerFailedAttempt()
            assertIs<LoginAttemptStatus.Allowed>(status)
        }

        val status = sessionRepository.registerFailedAttempt()
        assertIs<LoginAttemptStatus.Locked>(status)
        assertEquals(SessionRepositoryImpl.LOCK_DURATION_SECONDS, status.remainingSeconds)
    }

    @Test
    fun resetFailedAttemptsDeveDesbloquearContador() = runBlocking<Unit> {
        repeat(5) { sessionRepository.registerFailedAttempt() }
        sessionRepository.resetFailedAttempts()

        val status = sessionRepository.lockStatus()
        assertIs<LoginAttemptStatus.Allowed>(status)
        assertEquals(0, status.failedAttempts)
    }
}
