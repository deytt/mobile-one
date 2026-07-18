package com.mobileone.shared.domain.usecase

import com.mobileone.shared.config.AppStateRepository
import com.mobileone.shared.config.AppStateRepositoryImpl
import com.mobileone.shared.config.InMemoryWhiteLabelConfigRepository
import com.mobileone.shared.config.WhiteLabelConfigRepository
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SwitchBrandUseCaseTest {

    private val configRepository: WhiteLabelConfigRepository = InMemoryWhiteLabelConfigRepository()
    private val appStateRepository: AppStateRepository = AppStateRepositoryImpl()
    private val switchBrand = SwitchBrandUseCase(configRepository, appStateRepository)

    @Test
    fun invokeComMarcaValidaDeveAtualizarAppStateRepository() = runBlocking {
        val result = switchBrand("banco_premium")

        assertTrue(result.isSuccess)
        assertEquals("banco_premium", appStateRepository.currentConfig.value.brandId)
    }

    @Test
    fun invokeComMarcaInvalidaDeveRetornarFalhaSemAlterarOEstadoAtual() = runBlocking {
        val estadoAnterior = appStateRepository.currentConfig.value.brandId

        val result = switchBrand("marca_inexistente")

        assertTrue(result.isFailure)
        assertEquals(estadoAnterior, appStateRepository.currentConfig.value.brandId)
    }
}
