package com.mobileone.shared.feature.home

import com.mobileone.shared.domain.entity.TransactionCategory

/**
 * Estado imutável da tela Home (SPEC-002), consumido pelo `HomeViewModel` nativo
 * (Android/iOS) através de `StateFlow`. Todas as propriedades já são strings pré-formatadas
 * para eliminar lógica de apresentação nas UIs nativas.
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val account: AccountDisplay? = null,
    val isBalanceHidden: Boolean = false,
    val transactions: List<TransactionDisplay> = emptyList(),
    val hasMoreTransactions: Boolean = true,
    val isLoadingMoreTransactions: Boolean = false,
    val dataStatus: DataStatus = DataStatus.Fresh,
    val error: HomeError? = null
)

data class AccountDisplay(
    val holderName: String,
    val maskedNumber: String,
    val balanceFormatted: String,
    val availableLimitFormatted: String
)

data class TransactionDisplay(
    val id: String,
    val description: String,
    val amountFormatted: String,
    val isDebit: Boolean,
    val dateFormatted: String,
    val category: TransactionCategory
)

sealed class DataStatus {
    data object Fresh : DataStatus()
    data class Stale(val lastUpdatedAt: Long) : DataStatus()
}

sealed class HomeError {
    data object NetworkError : HomeError()
    data class Unknown(val message: String) : HomeError()
}
