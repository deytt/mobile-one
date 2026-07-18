package com.mobileone.shared.domain.usecase

import com.mobileone.shared.domain.entity.TransactionPage
import com.mobileone.shared.domain.repository.TransactionRepository

/** Retorna uma página do extrato de transações com paginação (SPEC-002). */
class GetTransactionHistoryUseCase(private val transactionRepository: TransactionRepository) {
    suspend operator fun invoke(
        accountId: String,
        page: Int,
        pageSize: Int = 20
    ): Result<TransactionPage> =
        transactionRepository.getTransactions(accountId, page, pageSize)
}
