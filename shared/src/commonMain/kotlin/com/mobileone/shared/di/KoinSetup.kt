package com.mobileone.shared.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Módulo vazio por ora. Bindings de use cases/repositórios entram por spec
 * (SPEC-001 a SPEC-004), conforme o workflow de PRs pequenos e focados.
 */
val sharedModule = module {}

fun initKoin(appDeclaration: KoinAppDeclaration = {}): KoinApplication =
    startKoin {
        appDeclaration()
        modules(sharedModule)
    }
