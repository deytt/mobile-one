package com.mobileone.shared.domain.entity

/**
 * Entidade de transação financeira (SPEC-002). [epochDay] é o número de dias desde a
 * epoch Unix — usado para agrupar transações por data sem depender de APIs de locale nativas.
 */
data class Transaction(
    val id: String,
    val accountId: String,
    val amountCents: Long,
    val isDebit: Boolean,
    val description: String,
    val category: TransactionCategory,
    val epochDay: Int
)

enum class TransactionCategory {
    PIX, TED, BOLETO, CARD, PURCHASE, DEPOSIT, FEE, OTHER
}
