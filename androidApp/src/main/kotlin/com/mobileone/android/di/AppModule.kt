package com.mobileone.android.di

import com.mobileone.android.viewmodel.AuthViewModel
import com.mobileone.android.viewmodel.BrandSwitcherViewModel
import com.mobileone.android.viewmodel.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Bindings específicos do `androidApp` (camada de apresentação) — os use cases/repositórios
 * consumidos aqui já vêm registrados pelos módulos do `shared` (ver `KoinSetup.kt`).
 */
val androidAppModule = module {
    viewModelOf(::AuthViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::BrandSwitcherViewModel)
}
