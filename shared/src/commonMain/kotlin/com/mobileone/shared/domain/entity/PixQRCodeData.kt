package com.mobileone.shared.domain.entity

/**
 * Dados extraídos de um QR Code PIX no padrão EMV do Banco Central (SPEC-003).
 * O campo [amount] é null para QR Codes estáticos sem valor pré-definido.
 */
data class PixQRCodeData(
    val pixKey: String,
    val pixKeyType: PixKeyType,
    val merchantName: String,
    val amountCents: Long?,           // null = QR estático sem valor
    val description: String,
    val txId: String                  // txid do payload EMV (campo 62/05)
)
