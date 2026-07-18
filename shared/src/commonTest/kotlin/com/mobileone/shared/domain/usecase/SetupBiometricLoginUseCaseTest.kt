package com.mobileone.shared.domain.usecase

import com.mobileone.shared.security.AuthSecureStorageKeys
import com.mobileone.shared.security.BiometricResult
import com.mobileone.shared.testdouble.FakeBiometricAuthenticator
import com.mobileone.shared.testdouble.InMemorySecureStorage
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SetupBiometricLoginUseCaseTest {

    private val secureStorage = InMemorySecureStorage()
    private val biometricAuth = FakeBiometricAuthenticator()
    private val setupBiometricLogin = SetupBiometricLoginUseCase(biometricAuth, secureStorage)

    @Test
    fun deveAtivarBiometriaEPersistirFlagAoConfirmar() = runBlocking<Unit> {
        val result = setupBiometricLogin()

        assertTrue(result.isSuccess)
        assertEquals("true", secureStorage.get(AuthSecureStorageKeys.BIOMETRIC_ENABLED))
    }

    @Test
    fun naoDevePersistirFlagSeBiometriaNaoDisponivel() = runBlocking<Unit> {
        biometricAuth.available = false

        val result = setupBiometricLogin()

        assertTrue(result.isFailure)
        assertNull(secureStorage.get(AuthSecureStorageKeys.BIOMETRIC_ENABLED))
    }

    @Test
    fun naoDevePersistirFlagSeUsuarioCancelar() = runBlocking<Unit> {
        biometricAuth.nextResult = BiometricResult.UserCancelled

        val result = setupBiometricLogin()

        assertTrue(result.isFailure)
        assertNull(secureStorage.get(AuthSecureStorageKeys.BIOMETRIC_ENABLED))
    }
}
