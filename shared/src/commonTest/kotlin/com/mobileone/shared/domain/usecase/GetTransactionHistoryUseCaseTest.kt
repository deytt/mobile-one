package com.mobileone.shared.domain.usecase

import com.mobileone.shared.data.repository.FakeAccountRepository
import com.mobileone.shared.data.repository.FakeTransactionRepository
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GetTransactionHistoryUseCaseTest {

    private val repository = FakeTransactionRepository()
    private val useCase = GetTransactionHistoryUseCase(repository)

    @Test
    fun deveRetornarPrimeiraPaginaCorretamente() = runBlocking {
        val result = useCase(FakeAccountRepository.DEMO_ACCOUNT_ID, page = 0, pageSize = 20)

        assertTrue(result.isSuccess)
        val page = result.getOrNull()!!
        assertEquals(0, page.page)
        assertEquals(20, page.items.size)
        assertTrue(page.hasMore)
    }

    @Test
    fun deveRetornarHasMoreFalseNaUltimaPagina() = runBlocking {
        val result = useCase(FakeAccountRepository.DEMO_ACCOUNT_ID, page = 1, pageSize = 20)

        assertTrue(result.isSuccess)
        val page = result.getOrNull()!!
        assertEquals(5, page.items.size)
        assertFalse(page.hasMore)
    }

    @Test
    fun deveRetornarListaVaziaAlemDaUltimaPagina() = runBlocking {
        val result = useCase(FakeAccountRepository.DEMO_ACCOUNT_ID, page = 99, pageSize = 20)

        assertTrue(result.isSuccess)
        val page = result.getOrNull()!!
        assertTrue(page.items.isEmpty())
        assertFalse(page.hasMore)
    }

    @Test
    fun deveAgruparTransacoesPorData() = runBlocking {
        val result = useCase(FakeAccountRepository.DEMO_ACCOUNT_ID, page = 0, pageSize = 20)

        val items = result.getOrNull()!!.items
        val grouped = items.groupBy { it.epochDay }
        // Deve haver mais de um dia distinto
        assertTrue(grouped.keys.size > 1, "Esperado múltiplos dias no extrato, mas obteve: ${grouped.keys}")
    }
}
