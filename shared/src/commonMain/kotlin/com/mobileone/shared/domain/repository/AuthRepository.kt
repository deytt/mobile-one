package com.mobileone.shared.domain.repository

import com.mobileone.shared.domain.entity.AuthToken

/**
 * Fonte de autenticação por CPF/senha (SPEC-001).
 * A implementação atual em memória deve ser substituída por integração com backend.
 */
interface AuthRepository {
    suspend fun login(cpf: String, password: String): Result<AuthToken>
    suspend fun logout(): Result<Unit>
    suspend fun refreshToken(refreshToken: String): Result<AuthToken>
}
