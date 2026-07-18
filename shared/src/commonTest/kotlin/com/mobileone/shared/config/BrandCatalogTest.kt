package com.mobileone.shared.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BrandCatalogTest {

    @Test
    fun deveExpor3MarcasComBrandIdsUnicos() {
        val brandIds = BrandCatalog.all().map { it.brandId }
        assertEquals(3, brandIds.toSet().size)
    }

    @Test
    fun bancoPrincipalDeveUsarTokensDaRegraDeArquitetura() {
        val config = BrandCatalog.bancoPrincipal()
        assertEquals("banco_principal", config.brandId)
        assertEquals("#003B6F", config.theme.colorPrimary)
        assertEquals("#F7941D", config.theme.colorSecondary)
        assertTrue(config.features.investmentsEnabled)
        assertFalse(config.features.isVipClient)
    }

    @Test
    fun fintechVerdeDeveDesabilitarInvestimentos() {
        val config = BrandCatalog.fintechVerde()
        assertEquals("fintech_verde", config.brandId)
        assertEquals("#00A86B", config.theme.colorPrimary)
        assertFalse(config.features.investmentsEnabled)
        assertTrue(config.features.pixEnabled)
    }

    @Test
    fun bancoPremiumDeveHabilitarTodasAsFeaturesEFlagVip() {
        val config = BrandCatalog.bancoPremium()
        assertEquals("banco_premium", config.brandId)
        assertEquals("#782D00", config.theme.colorPrimary)
        assertTrue(config.features.investmentsEnabled)
        assertTrue(config.features.isVipClient)
    }

    @Test
    fun byIdDeveRetornarNullParaMarcaDesconhecida() {
        assertEquals(null, BrandCatalog.byId("marca_inexistente"))
    }

    @Test
    fun defaultBrandIdDeveApontarParaBancoPrincipal() {
        assertEquals("banco_principal", BrandCatalog.DEFAULT_BRAND_ID)
    }
}
