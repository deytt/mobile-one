package com.mobileone.shared.domain.usecase

import com.mobileone.shared.data.repository.SessionRepositoryImpl
import com.mobileone.shared.domain.entity.AuthToken
import com.mobileone.shared.domain.error.AuthDomainError
import com.mobileone.shared.domain.error.toAuthDomainError
import com.mobileone.shared.security.BiometricResult
import com.mobileone.shared.testdouble.FakeBiometricAuthenticator
import com.mobileone.shared.testdouble.InMemorySecureStorage
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LoginWithBiometricUseCaseTest {

    private val secureStorage = InMemorySecureStorage()
    private val sessionRepository = SessionRepositoryImpl(secureStorage)
    private val biometricAuth = FakeBiometricAuthenticator()
    private val loginWithBiometric = LoginWithBiometricUseCase(biometricAuth, sessionRepository)

    private val token = AuthToken("access", "refresh", "user-1", "Heitor Bastos", 0L)

    @Test
    fun deveRetornarSucessoQuandoBiometriaConfirmaComSessaoExistente() = runBlocking<Unit> {
        sessionRepository.startSession(token)

        val result = loginWithBiometric()

        assertTrue(result.isSuccess)
        assertEquals(token.userName, result.getOrNull()?.userName)
    }

    @Test
    fun deveFalharSeNaoHouverSessaoPersistida() = runBlocking<Unit> {
        val result = loginWithBiometric()

        assertTrue(result.isFailure)
        assertIs<AuthDomainError.BiometricFailed>(result.exceptionOrNull()!!.toAuthDomainError())
    }

    @Test
    fun deveFalharSeBiometriaNaoDisponivel() = runBlocking<Unit> {
        sessionRepository.startSession(token)
        biometricAuth.available = false

        val result = loginWithBiometric()

        assertTrue(result.isFailure)
        assertIs<AuthDomainError.BiometricNotAvailable>(result.exceptionOrNull()!!.toAuthDomainError())
    }

    @Test
    fun deveBloquearAposMuitasTentativasEEncerrarSessao() = runBlocking<Unit> {
        sessionRepository.startSession(token)
        biometricAuth.nextResult = BiometricResult.TooManyAttempts

        val result = loginWithBiometric()

        assertTrue(result.isFailure)
        assertIs<AuthDomainError.BiometricBlocked>(result.exceptionOrNull()!!.toAuthDomainError())
        assertNull(sessionRepository.currentSession())
    }
}
