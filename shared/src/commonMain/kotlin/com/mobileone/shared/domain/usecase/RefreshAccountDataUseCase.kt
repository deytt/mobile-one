package com.mobileone.shared.domain.usecase

import com.mobileone.shared.domain.repository.AccountRepository
import com.mobileone.shared.domain.repository.TransactionRepository

/** Força a busca de dados frescos da API para conta e extrato (SPEC-002). Usado no pull-to-refresh. */
class RefreshAccountDataUseCase(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(accountId: String): Result<Unit> =
        accountRepository.refreshFromRemote(accountId)
}
