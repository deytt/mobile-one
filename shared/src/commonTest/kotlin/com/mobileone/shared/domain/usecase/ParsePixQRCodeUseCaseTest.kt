package com.mobileone.shared.domain.usecase

import com.mobileone.shared.domain.error.PixError
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Testes do parser de QR Code PIX no padrão EMV (SPEC-003).
 * Rodam identicamente em Android e iOS — lógica 100% no shared.
 */
class ParsePixQRCodeUseCaseTest {

    private val parseQRCode = ParsePixQRCodeUseCase()

    /**
     * Payload EMV estático (sem valor) com chave e-mail:
     * 00 = payload format indicator
     * 26 = merchant account info (subcampo 01 = chave PIX)
     * 59 = merchant name
     * 60 = city
     * 63 = CRC (ignorado no parse)
     */
    @Test
    fun deveParsearPayloadEmvEstatico() {
        // Campo 26: length=36, subcampo 00 (gui) + subcampo 01 (chave)
        // 26 36 00 14 br.gov.bcb.pix 01 18 joao@email.com (comprimentos calculados)
        val gui = "br.gov.bcb.pix"      // length 14
        val key = "joao@email.com"       // length 14
        val merchantAccountInfo = "00${gui.length.toString().padStart(2,'0')}$gui" +
                                  "01${key.length.toString().padStart(2,'0')}$key"
        val name = "JOAO SILVA"

        val payload = buildEmvPayload(
            fields = mapOf(
                "00" to "01",
                "26" to merchantAccountInfo,
                "52" to "0000",
                "53" to "986",
                "59" to name,
                "60" to "SAO PAULO"
            )
        )

        val result = parseQRCode(payload)
        assertTrue(result.isSuccess)
        val data = result.getOrNull()!!
        assertTrue(data.pixKey.isNotBlank())
        assertNull(data.amountCents)  // QR estático sem valor
    }

    @Test
    fun deveParsearPayloadEmvDinamicoComValor() {
        val gui = "br.gov.bcb.pix"
        val key = "a1b2c3d4-e5f6-4789-a0b1-c2d3e4f50001"
        val merchantAccountInfo = "00${gui.length.toString().padStart(2,'0')}$gui" +
                                  "01${key.length.toString().padStart(2,'0')}$key"
        val amount = "150.00"

        val payload = buildEmvPayload(
            fields = mapOf(
                "00" to "01",
                "26" to merchantAccountInfo,
                "52" to "0000",
                "53" to "986",
                "54" to amount,
                "59" to "LOJA TESTE",
                "60" to "RIO DE JANEIRO"
            )
        )

        val result = parseQRCode(payload)
        assertTrue(result.isSuccess)
        val data = result.getOrNull()!!
        assertNotNull(data.amountCents)
        assertTrue(data.amountCents > 0)
    }

    @Test
    fun deveRejeitarPayloadVazio() {
        val result = parseQRCode("")
        assertTrue(result.isFailure)
        assertIs<PixError.QRCodeInvalid>(result.exceptionOrNull())
    }

    @Test
    fun deveRejeitarPayloadSemChavePix() {
        // Payload válido mas sem merchant account info
        val payload = buildEmvPayload(
            fields = mapOf(
                "00" to "01",
                "52" to "0000",
                "59" to "LOJA TESTE"
            )
        )
        val result = parseQRCode(payload)
        assertTrue(result.isFailure)
        assertIs<PixError.QRCodeInvalid>(result.exceptionOrNull())
    }

    /** Monta um payload EMV concatenando tag + length + value para cada campo. */
    private fun buildEmvPayload(fields: Map<String, String>): String =
        fields.entries.joinToString("") { (tag, value) ->
            "$tag${value.length.toString().padStart(2, '0')}$value"
        }
}
