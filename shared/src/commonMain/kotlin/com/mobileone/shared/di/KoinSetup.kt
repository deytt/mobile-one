package com.mobileone.shared.di

import com.mobileone.shared.config.AppStateRepository
import com.mobileone.shared.config.AppStateRepositoryImpl
import com.mobileone.shared.config.InMemoryWhiteLabelConfigRepository
import com.mobileone.shared.config.WhiteLabelConfigRepository
import com.mobileone.shared.domain.usecase.SwitchBrandUseCase
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Módulo vazio por ora. Bindings de use cases/repositórios adicionais entram por spec
 * (SPEC-001 a SPEC-003), conforme o workflow de PRs pequenos e focados.
 */
val sharedModule = module {}

/**
 * Bindings da fundação de white-label (SPEC-004). Ver `com.mobileone.shared.config`.
 */
val whiteLabelModule = module {
    single<WhiteLabelConfigRepository> { InMemoryWhiteLabelConfigRepository() }
    single<AppStateRepository> { AppStateRepositoryImpl() }
    factory { SwitchBrandUseCase(get(), get()) }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}): KoinApplication =
    startKoin {
        appDeclaration()
        modules(sharedModule, whiteLabelModule)
    }
