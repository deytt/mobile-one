package com.mobileone.shared.domain.entity

/** Página de transações retornada pela paginação do extrato (SPEC-002). */
data class TransactionPage(
    val items: List<Transaction>,
    val hasMore: Boolean,
    val page: Int
)
