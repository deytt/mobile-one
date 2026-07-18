package com.mobileone.shared.domain.usecase

import com.mobileone.shared.domain.entity.Account
import com.mobileone.shared.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow

/** Observa os dados de conta reativamente a partir do cache local (SPEC-002). */
class ObserveAccountUseCase(private val accountRepository: AccountRepository) {
    operator fun invoke(accountId: String): Flow<Account> =
        accountRepository.observeAccount(accountId)
}
