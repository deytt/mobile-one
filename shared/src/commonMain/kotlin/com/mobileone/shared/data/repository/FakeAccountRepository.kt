package com.mobileone.shared.data.repository

import com.mobileone.shared.domain.entity.Account
import com.mobileone.shared.domain.repository.AccountRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

/**
 * Implementação em memória de [AccountRepository] para validação local da SPEC-002.
 * Uma implementação integrada deve usar SQLDelight + Ktor.
 */
class FakeAccountRepository : AccountRepository {

    private val _account = MutableStateFlow<Account?>(null)

    init {
        _account.value = DEMO_ACCOUNT
    }

    override fun observeAccount(accountId: String): Flow<Account> = _account.filterNotNull()

    override suspend fun refreshFromRemote(accountId: String): Result<Unit> {
        delay(800)
        _account.value = DEMO_ACCOUNT.copy(balanceCents = DEMO_ACCOUNT.balanceCents, updatedAt = 0L)
        return Result.success(Unit)
    }

    companion object {
        const val DEMO_ACCOUNT_ID = "acc_001"

        val DEMO_ACCOUNT = Account(
            id = DEMO_ACCOUNT_ID,
            holderId = "usr_heitor",
            holderName = "Heitor Bastos",
            maskedNumber = "•••• 4521",
            balanceCents = 1_234_56L,
            availableLimitCents = 5_000_00L,
            updatedAt = 0L
        )
    }
}
