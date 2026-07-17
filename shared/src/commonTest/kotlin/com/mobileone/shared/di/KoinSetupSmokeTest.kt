package com.mobileone.shared.di

import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.Test

class KoinSetupSmokeTest {

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun deveInicializarKoinComModuloSharedSemErros() {
        initKoin()
    }
}
