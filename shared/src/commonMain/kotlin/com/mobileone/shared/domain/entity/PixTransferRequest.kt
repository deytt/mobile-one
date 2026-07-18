package com.mobileone.shared.domain.entity

/**
 * Payload enviado ao banco para executar uma transferência PIX (SPEC-003).
 * Valores monetários em centavos para evitar aritmética de ponto flutuante.
 */
data class PixTransferRequest(
    val pixKey: String,
    val pixKeyType: PixKeyType,
    val amountCents: Long,
    val description: String,
    val recipientName: String,
    val recipientTaxId: String,
    val recipientInstitution: String
)
