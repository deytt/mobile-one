package com.mobileone.shared.config

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class AppStateRepositoryTest {

    @Test
    fun currentConfigDeveComecarComABancoPrincipalPorDefault() {
        val repository: AppStateRepository = AppStateRepositoryImpl()

        assertEquals("banco_principal", repository.currentConfig.value.brandId)
    }

    @Test
    fun setCurrentConfigDeveAtualizarOStateFlow() = runBlocking {
        val repository: AppStateRepository = AppStateRepositoryImpl()

        repository.setCurrentConfig(BrandCatalog.fintechVerde())

        assertEquals("fintech_verde", repository.currentConfig.value.brandId)
    }
}
