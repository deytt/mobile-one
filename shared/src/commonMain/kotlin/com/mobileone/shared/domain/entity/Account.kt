package com.mobileone.shared.domain.entity

/**
 * Entidade de conta corrente (SPEC-002). Valores monetários em centavos para evitar
 * aritmética de ponto flutuante — a formatação para exibição ocorre no [com.mobileone.shared.util.CurrencyFormatter].
 */
data class Account(
    val id: String,
    val holderId: String,
    val holderName: String,
    val maskedNumber: String,
    val balanceCents: Long,
    val availableLimitCents: Long,
    val updatedAt: Long
)
