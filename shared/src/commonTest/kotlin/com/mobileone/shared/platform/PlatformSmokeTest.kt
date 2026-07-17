package com.mobileone.shared.platform

import kotlin.test.Test
import kotlin.test.assertTrue

class PlatformSmokeTest {

    @Test
    fun platformNameNaoDeveSerVazio() {
        val platform = Platform()
        assertTrue(platform.name.isNotBlank())
    }

    @Test
    fun greetDeveConterNomeDaPlataforma() {
        val platform = Platform()
        assertTrue(greet().contains(platform.name))
    }
}
