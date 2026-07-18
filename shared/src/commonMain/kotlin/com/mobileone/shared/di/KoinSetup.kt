package com.mobileone.shared.di

import com.mobileone.shared.config.AppStateRepository
import com.mobileone.shared.config.AppStateRepositoryImpl
import com.mobileone.shared.config.InMemoryWhiteLabelConfigRepository
import com.mobileone.shared.config.WhiteLabelConfigRepository
import com.mobileone.shared.data.repository.FakeAuthRepository
import com.mobileone.shared.data.repository.SessionRepositoryImpl
import com.mobileone.shared.domain.repository.AuthRepository
import com.mobileone.shared.domain.repository.SessionRepository
import com.mobileone.shared.domain.usecase.LoginWithBiometricUseCase
import com.mobileone.shared.domain.usecase.LoginWithCredentialsUseCase
import com.mobileone.shared.domain.usecase.SetupBiometricLoginUseCase
import com.mobileone.shared.domain.usecase.SwitchBrandUseCase
import com.mobileone.shared.domain.usecase.ValidateDeviceIntegrityUseCase
import com.mobileone.shared.domain.validation.AuthValidator
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Módulo vazio por ora. Bindings de use cases/repositórios adicionais entram por spec
 * (SPEC-002/SPEC-003), conforme o workflow de PRs pequenos e focados.
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

/**
 * Bindings do fluxo de login/biometria (SPEC-001). `BiometricAuthenticator`/`SecureStorage`/
 * `DeviceIntegrityChecker` vêm de [securityPlatformModule], resolvido por plataforma.
 */
val authModule = module {
    single<AuthRepository> { FakeAuthRepository() }
    single<SessionRepository> { SessionRepositoryImpl(get()) }
    factory { AuthValidator() }
    factory { ValidateDeviceIntegrityUseCase(get()) }
    factory { LoginWithCredentialsUseCase(get(), get(), get(), get()) }
    factory { LoginWithBiometricUseCase(get(), get()) }
    factory { SetupBiometricLoginUseCase(get(), get()) }
}

/**
 * Implementações nativas de segurança (SPEC-001/ADR-005) — Android resolve `Context` via
 * `androidContext()`; iOS instancia direto, sem parâmetros de plataforma.
 */
expect fun securityPlatformModule(): Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}): KoinApplication =
    startKoin {
        appDeclaration()
        modules(sharedModule, whiteLabelModule, authModule, securityPlatformModule())
    }
