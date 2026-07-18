package com.mobileone.shared.data.repository

import com.mobileone.shared.domain.error.AuthDomainError
import com.mobileone.shared.domain.error.toAuthDomainError
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class FakeAuthRepositoryTest {

    private val repository = FakeAuthRepository()

    @Test
    fun deveAutenticarComCredenciaisDeDemonstracao() = runBlocking<Unit> {
        val result = repository.login(FakeAuthRepository.DEMO_CPF, FakeAuthRepository.DEMO_PASSWORD)

        assertTrue(result.isSuccess)
    }

    @Test
    fun deveFalharComSenhaIncorreta() = runBlocking<Unit> {
        val result = repository.login(FakeAuthRepository.DEMO_CPF, "outraSenha1")

        assertTrue(result.isFailure)
        assertIs<AuthDomainError.InvalidCredentials>(result.exceptionOrNull()!!.toAuthDomainError())
    }

    @Test
    fun deveSimularErroDeRedeParaCpfReservado() = runBlocking<Unit> {
        val result = repository.login(FakeAuthRepository.NETWORK_ERROR_CPF, "qualquerSenha1")

        assertTrue(result.isFailure)
        assertIs<AuthDomainError.NetworkError>(result.exceptionOrNull()!!.toAuthDomainError())
    }

    @Test
    fun logoutDeveLimparSessaoParaRefreshToken() = runBlocking<Unit> {
        repository.login(FakeAuthRepository.DEMO_CPF, FakeAuthRepository.DEMO_PASSWORD)
        repository.logout()

        val refreshResult = repository.refreshToken("qualquer")

        assertTrue(refreshResult.isFailure)
    }
}
