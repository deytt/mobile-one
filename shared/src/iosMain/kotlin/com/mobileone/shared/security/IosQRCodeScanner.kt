package com.mobileone.shared.security

import kotlinx.coroutines.delay

/**
 * Scanner de QR Code para iOS (SPEC-003).
 * POC: retorna payload EMV fixo para demonstrar o parsing no shared sem depender de câmera real.
 * Produção: substituir por integração com AVFoundation + AVCaptureSession, apresentando
 * um UIViewController de câmera e devolvendo o payload via continuation.
 */
class IosQRCodeScanner : QRCodeScanner {

    override suspend fun scan(): Result<String> {
        delay(500)

        val guiLabel = "br.gov.bcb.pix"
        val keyLabel = "joao@email.com"
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
