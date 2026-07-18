package com.mobileone.shared.domain.usecase

import com.mobileone.shared.domain.error.AuthDomainError
import com.mobileone.shared.domain.error.toAuthDomainError
import com.mobileone.shared.security.IntegrityStatus
import com.mobileone.shared.testdouble.FakeDeviceIntegrityChecker
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ValidateDeviceIntegrityUseCaseTest {

    private val checker = FakeDeviceIntegrityChecker()
    private val validateDeviceIntegrity = ValidateDeviceIntegrityUseCase(checker)

    @Test
    fun deveRetornarSucessoParaDispositivoIntegro() = runBlocking<Unit> {
        val result = validateDeviceIntegrity()

        assertTrue(result.isSuccess)
    }

    @Test
    fun deveFalharParaDispositivoComRoot() = runBlocking<Unit> {
        checker.status = IntegrityStatus(isRooted = true, isEmulator = false, isDebuggable = false)

        val result = validateDeviceIntegrity()

        assertTrue(result.isFailure)
        assertIs<AuthDomainError.CompromisedDevice>(result.exceptionOrNull()!!.toAuthDomainError())
    }
}
