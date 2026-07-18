package com.mobileone.shared.config

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WhiteLabelConfigRepositoryTest {

    private val repository: WhiteLabelConfigRepository = InMemoryWhiteLabelConfigRepository()

    @Test
    fun loadConfigDeveRetornarSucessoParaMarcaConhecida() = runBlocking {
        val result = repository.loadConfig("fintech_verde")

        assertTrue(result.isSuccess)
        assertEquals("fintech_verde", result.getOrNull()?.brandId)
    }

    @Test
    fun loadConfigDeveRetornarFalhaParaMarcaDesconhecida() = runBlocking {
        val result = repository.loadConfig("marca_inexistente")

        assertTrue(result.isFailure)
    }
}
