package com.mobileone.shared.domain.usecase

import com.mobileone.shared.domain.entity.PixKeyType
import com.mobileone.shared.domain.error.PixError
import com.mobileone.shared.feature.pix.PixLimitsValidator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Testes dos validadores de chave PIX e dos limites (SPEC-003).
 * Rodam identicamente em Android e iOS — lógica 100% no shared.
 */
class ValidatePixKeyUseCaseTest {

    private val detectKeyType = DetectPixKeyTypeUseCase()
    private val validateKey = ValidatePixKeyUseCase()
    private val limitsValidator = PixLimitsValidator()

    // ── CPF ──────────────────────────────────────────────────────────────────

    @Test
    fun deveValidarCpfCorretoFormatado() {
        val result = validateKey("529.982.247-25", PixKeyType.CPF)
        assertTrue(result.isSuccess)
    }

    @Test
    fun deveValidarCpfCorretoDígitos() {
        val result = validateKey("52998224725", PixKeyType.CPF)
        assertTrue(result.isSuccess)
    }

    @Test
    fun deveRejeitarCpfComDigitosIguais() {
        val result = validateKey("111.111.111-11", PixKeyType.CPF)
        assertTrue(result.isFailure)
        assertIs<PixError.InvalidKey>(result.exceptionOrNull())
    }

    @Test
    fun deveRejeitarCpfComDigitoVerificadorErrado() {
        val result = validateKey("529.982.247-26", PixKeyType.CPF)
        assertTrue(result.isFailure)
    }

    @Test
    fun deveRejeitarCpfCurto() {
        val result = validateKey("123.456", PixKeyType.CPF)
        assertTrue(result.isFailure)
    }

    // ── CNPJ ─────────────────────────────────────────────────────────────────

    @Test
    fun deveValidarCnpjCorreto() {
        // CNPJ válido gerado para teste
        val result = validateKey("11.222.333/0001-81", PixKeyType.CNPJ)
        assertTrue(result.isSuccess)
    }

    @Test
    fun deveRejeitarCnpjComDigitosIguais() {
        val result = validateKey("11.111.111/1111-11", PixKeyType.CNPJ)
        assertTrue(result.isFailure)
    }

    // ── E-mail ────────────────────────────────────────────────────────────────

    @Test
    fun deveDetectarTipoEmailPeloArroba() {
        val detected = detectKeyType("usuario@banco.com.br")
        assertEquals(PixKeyType.Email, detected)
    }

    @Test
    fun deveValidarEmailCorreto() {
        val result = validateKey("usuario@banco.com.br", PixKeyType.Email)
        assertTrue(result.isSuccess)
    }

    @Test
    fun deveRejeitarEmailSemArroba() {
        val result = validateKey("usuariobanco.com.br", PixKeyType.Email)
        assertTrue(result.isFailure)
    }

    @Test
    fun deveRejeitarEmailSemDominio() {
        val result = validateKey("usuario@", PixKeyType.Email)
        assertTrue(result.isFailure)
    }

    // ── Chave aleatória (UUID v4) ─────────────────────────────────────────────

    @Test
    fun deveValidarChaveAleatoriaNoFormatoUuid() {
        val result = validateKey("a1b2c3d4-e5f6-4789-a0b1-c2d3e4f50001", PixKeyType.RandomKey)
        assertTrue(result.isSuccess)
    }

    @Test
    fun deveRejeitarUuidComTamanhoErrado() {
        val result = validateKey("a1b2c3d4-e5f6-4789-a0b1", PixKeyType.RandomKey)
        assertTrue(result.isFailure)
    }

    @Test
    fun deveRejeitarUuidSemHifens() {
        val result = validateKey("a1b2c3d4e5f64789a0b1c2d3e4f50001", PixKeyType.RandomKey)
        assertTrue(result.isFailure)
    }

    // ── Telefone ──────────────────────────────────────────────────────────────

    @Test
    fun deveValidarTelefoneComDddValido() {
        val result = validateKey("+5511999887766", PixKeyType.Phone)
        assertTrue(result.isSuccess)
    }

    @Test
    fun deveRejeitarTelefoneComDddInvalido() {
        val result = validateKey("+5500999887766", PixKeyType.Phone)
        assertTrue(result.isFailure)
    }

    // ── Limites diurno/noturno ────────────────────────────────────────────────

    @Test
    fun deveRejeitarValorAcimaDoLimiteNoturnoApos21h() {
        val result = limitsValidator.validate(amountCents = 1_500_00L, hourOfDay = 22) // R$ 1.500 às 22h
        assertTrue(result.isFailure)
        assertIs<PixError.LimitExceeded>(result.exceptionOrNull())
    }

    @Test
    fun devePermitirValorDentroDoLimiteDiurno() {
        val result = limitsValidator.validate(amountCents = 5_000_00L, hourOfDay = 10) // R$ 5.000 às 10h
        assertTrue(result.isSuccess)
    }

    @Test
    fun devePermitirValorDentroDoLimiteNoturno() {
        val result = limitsValidator.validate(amountCents = 500_00L, hourOfDay = 23) // R$ 500 às 23h
        assertTrue(result.isSuccess)
    }

    @Test
    fun deveRejeitarValorAcimaDoLimiteDiurno() {
        val result = limitsValidator.validate(amountCents = 25_000_00L, hourOfDay = 14) // R$ 25.000 às 14h
        assertTrue(result.isFailure)
    }
}
