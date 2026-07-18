package com.mobileone.shared.domain.usecase

import com.mobileone.shared.data.repository.FakeAuthRepository
import com.mobileone.shared.data.repository.SessionRepositoryImpl
import com.mobileone.shared.domain.error.AuthDomainError
import com.mobileone.shared.domain.error.toAuthDomainError
import com.mobileone.shared.security.IntegrityStatus
import com.mobileone.shared.testdouble.FakeDeviceIntegrityChecker
import com.mobileone.shared.testdouble.InMemorySecureStorage
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class LoginWithCredentialsUseCaseTest {

    private val authRepository = FakeAuthRepository()
    private val sessionRepository = SessionRepositoryImpl(InMemorySecureStorage())
    private val integrityChecker = FakeDeviceIntegrityChecker()
    private val deviceIntegrity = ValidateDeviceIntegrityUseCase(integrityChecker)
    private val loginWithCredentials =
        LoginWithCredentialsUseCase(authRepository, sessionRepository, deviceIntegrity)

    @Test
    fun deveRetornarSucessoComCredenciaisValidas() = runBlocking<Unit> {
        val result = loginWithCredentials(FakeAuthRepository.DEMO_CPF, FakeAuthRepository.DEMO_PASSWORD)

        assertTrue(result.isSuccess)
        assertEquals(FakeAuthRepository.DEMO_USER_NAME, result.getOrNull()?.userName)
    }

    @Test
    fun deveRetornarInvalidCredentialsComSenhaErrada() = runBlocking<Unit> {
        val result = loginWithCredentials(FakeAuthRepository.DEMO_CPF, "senhaErrada1")

        assertTrue(result.isFailure)
        val error = result.exceptionOrNull()!!.toAuthDomainError()
        assertIs<AuthDomainError.InvalidCredentials>(error)
        assertEquals(4, error.attemptsRemaining)
    }

    @Test
    fun deveBloquearContaApos5Tentativas() = runBlocking<Unit> {
        repeat(5) {
            loginWithCredentials(FakeAuthRepository.DEMO_CPF, "senhaErrada1")
        }

        val result = loginWithCredentials(FakeAuthRepository.DEMO_CPF, FakeAuthRepository.DEMO_PASSWORD)

        assertTrue(result.isFailure)
        val error = result.exceptionOrNull()!!.toAuthDomainError()
        assertIs<AuthDomainError.AccountLocked>(error)
        assertEquals(SessionRepositoryImpl.LOCK_DURATION_SECONDS, error.remainingSeconds)
    }

    @Test
    fun deveFalharSeCpfInvalido() = runBlocking<Unit> {
        val result = loginWithCredentials("12345678900", FakeAuthRepository.DEMO_PASSWORD)

        assertTrue(result.isFailure)
        val error = result.exceptionOrNull()!!.toAuthDomainError()
        assertIs<AuthDomainError.Validation>(error)
        assertEquals("cpf", error.field)
    }

    @Test
    fun deveFalharSeDispositivoComprometidoRoot() = runBlocking<Unit> {
        integrityChecker.status = IntegrityStatus(isRooted = true, isEmulator = false, isDebuggable = false)

        val result = loginWithCredentials(FakeAuthRepository.DEMO_CPF, FakeAuthRepository.DEMO_PASSWORD)

        assertTrue(result.isFailure)
        assertIs<AuthDomainError.CompromisedDevice>(result.exceptionOrNull()!!.toAuthDomainError())
    }
}
