package com.mobileone.shared.testdouble

import com.mobileone.shared.security.BiometricAuthenticator
import com.mobileone.shared.security.BiometricResult
import com.mobileone.shared.security.DeviceIntegrityChecker
import com.mobileone.shared.security.IntegrityStatus
import com.mobileone.shared.security.SecureStorage

/**
 * Fakes de segurança (SPEC-001) usados nos testes do shared — os use cases de autenticação
 * rodam sem dispositivo físico, ver ADR-005.
 */
class InMemorySecureStorage : SecureStorage {
    private val storage = mutableMapOf<String, String>()

    override suspend fun put(key: String, value: String) {
        storage[key] = value
    }

    override suspend fun get(key: String): String? = storage[key]

    override suspend fun delete(key: String) {
        storage.remove(key)
    }

    override suspend fun clear() {
        storage.clear()
    }
}

class FakeBiometricAuthenticator(
    var available: Boolean = true,
    var nextResult: BiometricResult = BiometricResult.Success
) : BiometricAuthenticator {
    override suspend fun isAvailable(): Boolean = available
    override suspend fun authenticate(reason: String): BiometricResult = nextResult
}

class FakeDeviceIntegrityChecker(
    var status: IntegrityStatus = IntegrityStatus(isRooted = false, isEmulator = false, isDebuggable = false)
) : DeviceIntegrityChecker {
    override suspend fun check(): IntegrityStatus = status
}
