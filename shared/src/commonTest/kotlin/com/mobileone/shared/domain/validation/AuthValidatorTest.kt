package com.mobileone.shared.domain.validation

import kotlin.test.Test
import kotlin.test.assertTrue

class AuthValidatorTest {

    private val validator = AuthValidator()

    @Test
    fun deveAceitarCpfValido() {
        assertTrue(validator.validateCpf("529.982.247-25").isSuccess)
    }

    @Test
    fun deveRejeitarCpfComDigitosVerificadoresErrados() {
        assertTrue(validator.validateCpf("529.982.247-26").isFailure)
    }

    @Test
    fun deveRejeitarCpfComTodosDigitosIguais() {
        assertTrue(validator.validateCpf("111.111.111-11").isFailure)
    }

    @Test
    fun deveRejeitarCpfComQuantidadeErradaDeDigitos() {
        assertTrue(validator.validateCpf("123456789").isFailure)
    }

    @Test
    fun deveAceitarSenhaComNumeroEEntre6E20Caracteres() {
        assertTrue(validator.validatePassword("senha123").isSuccess)
    }

    @Test
    fun deveRejeitarSenhaSemNumero() {
        assertTrue(validator.validatePassword("senhaSemNumero").isFailure)
    }

    @Test
    fun deveRejeitarSenhaMuitoCurta() {
        assertTrue(validator.validatePassword("ab1").isFailure)
    }
}
