package com.mobileone.shared.domain.repository

import com.mobileone.shared.domain.entity.Account
import kotlinx.coroutines.flow.Flow

/** Contrato de acesso aos dados de conta (SPEC-002). */
interface AccountRepository {
    /** Emite a conta do cache local imediatamente, depois atualiza conforme o banco de dados. */
    fun observeAccount(accountId: String): Flow<Account>
    /** Busca dados frescos da API e persiste no cache — lança [com.mobileone.shared.domain.error.HomeDomainError] em falha. */
    suspend fun refreshFromRemote(accountId: String): Result<Unit>
}
