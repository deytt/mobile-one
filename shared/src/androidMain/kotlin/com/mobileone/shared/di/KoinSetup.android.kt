package com.mobileone.shared.di

import com.mobileone.shared.security.AndroidBiometricAuthenticator
import com.mobileone.shared.security.AndroidDeviceIntegrityChecker
import com.mobileone.shared.security.AndroidSecureStorage
import com.mobileone.shared.security.BiometricAuthenticator
import com.mobileone.shared.security.DeviceIntegrityChecker
import com.mobileone.shared.security.SecureStorage
import org.koin.core.module.Module
import org.koin.dsl.module

/** `Context` resolvido pelo Koin via `androidContext()`, já configurado em `MobileOneApplication`. */
actual fun securityPlatformModule(): Module = module {
    single<BiometricAuthenticator> { AndroidBiometricAuthenticator(get()) }
    single<SecureStorage> { AndroidSecureStorage(get()) }
    single<DeviceIntegrityChecker> { AndroidDeviceIntegrityChecker() }
}
