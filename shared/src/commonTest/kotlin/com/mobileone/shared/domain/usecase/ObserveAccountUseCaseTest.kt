package com.mobileone.shared.domain.usecase

import com.mobileone.shared.data.repository.FakeAccountRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ObserveAccountUseCaseTest {

    private val repository = FakeAccountRepository()
    private val useCase = ObserveAccountUseCase(repository)

    @Test
    fun deveEmitirDadosDoCacheImediatamente() = runBlocking {
        val account = useCase(FakeAccountRepository.DEMO_ACCOUNT_ID).first()

        assertNotNull(account)
        assertEquals(FakeAccountRepository.DEMO_ACCOUNT.holderName, account.holderName)
        assertEquals(FakeAccountRepository.DEMO_ACCOUNT.maskedNumber, account.maskedNumber)
    }

    @Test
    fun deveRetornarBalanceCentsCorreto() = runBlocking {
        val account = useCase(FakeAccountRepository.DEMO_ACCOUNT_ID).first()

        assertEquals(FakeAccountRepository.DEMO_ACCOUNT.balanceCents, account.balanceCents)
    }
}
