package com.mobileone.shared.data.repository

import com.mobileone.shared.domain.entity.AuthToken
import com.mobileone.shared.domain.error.AuthDomainError
import com.mobileone.shared.domain.error.asFailure
import com.mobileone.shared.domain.repository.AuthRepository

/**
 * Implementação em memória de [AuthRepository] para a POC (SPEC-001) — não há backend real.
 * Credenciais fixas de demonstração; [NETWORK_ERROR_CPF] simula indisponibilidade de rede para
 * validar esse caminho de erro sem precisar de um servidor.
 */
class FakeAuthRepository : AuthRepository {

    private var currentToken: AuthToken? = null

    override suspend fun login(cpf: String, password: String): Result<AuthToken> {
        val digits = cpf.filter { it.isDigit() }
        if (digits == NETWORK_ERROR_CPF) {
            return AuthDomainError.NetworkError.asFailure()
        }
        if (digits != DEMO_CPF || password != DEMO_PASSWORD) {
            return AuthDomainError.InvalidCredentials(attemptsRemaining = 0, failedAttempts = 0).asFailure()
        }

        val token = AuthToken(
            accessToken = "demo-access-token",
            refreshToken = "demo-refresh-token",
            userId = "demo-user-1",
            userName = DEMO_USER_NAME,
            expiresAtEpochMillis = 0L
        )
        currentToken = token
        return Result.success(token)
    }

    override suspend fun logout(): Result<Unit> {
        currentToken = null
        return Result.success(Unit)
    }

    override suspend fun refreshToken(refreshToken: String): Result<AuthToken> {
        val token = currentToken ?: return AuthDomainError.Unknown("Sessão inexistente").asFailure()
        return Result.success(token)
    }

    companion object {
        /** CPF válido de demonstração (checksum correto, não pertence a pessoa real). */
        const val DEMO_CPF = "76109277673"
        const val DEMO_PASSWORD = "111111"
        const val DEMO_USER_NAME = "Heitor Bastos"

        /** CPF válido de demonstração reservado para simular `AuthDomainError.NetworkError`. */
        const val NETWORK_ERROR_CPF = "11144477735"
    }
}
