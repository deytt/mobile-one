package com.mobileone.shared.security

import kotlinx.coroutines.delay

/**
 * Scanner de QR Code para Android (SPEC-003).
 * A implementação atual retorna um payload EMV fixo para validar o parser compartilhado.
 * A integração com câmera deve usar ML Kit Barcode Scanning ou ZXing.
 */
class AndroidQRCodeScanner : QRCodeScanner {

    override suspend fun scan(): Result<String> {
        delay(500)

        // Payload EMV estático com chave e-mail.
        val guiLabel = "br.gov.bcb.pix"  // length 14
        val keyLabel = "joao@email.com"   // length 14
        val merchantAccount =
            "00${guiLabel.length.toString().padStart(2, '0')}$guiLabel" +
            "01${keyLabel.length.toString().padStart(2, '0')}$keyLabel"
        val amount = "150.00"
        val name = "JOAO SILVA"

        val payload = buildEmv(
            "00" to "01",
            "26" to merchantAccount,
            "52" to "0000",
            "53" to "986",
            "54" to amount,
            "59" to name,
            "60" to "SAO PAULO"
        )
        return Result.success(payload)
    }

    private fun buildEmv(vararg fields: Pair<String, String>): String =
        fields.joinToString("") { (tag, value) ->
            "$tag${value.length.toString().padStart(2, '0')}$value"
        }
}
