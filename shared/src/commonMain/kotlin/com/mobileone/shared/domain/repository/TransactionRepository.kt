package com.mobileone.shared.domain.repository

import com.mobileone.shared.domain.entity.Transaction
import com.mobileone.shared.domain.entity.TransactionPage
import kotlinx.coroutines.flow.Flow

/** Contrato de acesso ao extrato de transações (SPEC-002). */
interface TransactionRepository {
    /** Retorna uma página de transações; [pageSize] padrão de 20 conforme a spec. */
    suspend fun getTransactions(
        accountId: String,
        page: Int,
        pageSize: Int = 20
    ): Result<TransactionPage>

    /** Emite as transações mais recentes do cache local, útil para o card de resumo na Home. */
    fun observeRecentTransactions(accountId: String, limit: Int = 5): Flow<List<Transaction>>
}
