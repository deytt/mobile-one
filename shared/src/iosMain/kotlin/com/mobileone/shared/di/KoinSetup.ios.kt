package com.mobileone.shared.di

import com.mobileone.shared.security.BiometricAuthenticator
import com.mobileone.shared.security.DeviceIntegrityChecker
import com.mobileone.shared.security.IosBiometricAuthenticator
import com.mobileone.shared.security.IosDeviceIntegrityChecker
import com.mobileone.shared.security.IosQRCodeScanner
import com.mobileone.shared.security.IosSecureStorage
import com.mobileone.shared.security.QRCodeScanner
import com.mobileone.shared.security.SecureStorage
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun securityPlatformModule(): Module = module {
    single<BiometricAuthenticator> { IosBiometricAuthenticator() }
    single<SecureStorage> { IosSecureStorage() }
    single<DeviceIntegrityChecker> { IosDeviceIntegrityChecker() }
    single<QRCodeScanner> { IosQRCodeScanner() }
}
