package com.mobileone.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileone.shared.data.repository.FakeAccountRepository
import com.mobileone.shared.domain.usecase.GetTransactionHistoryUseCase
import com.mobileone.shared.domain.usecase.ObserveAccountUseCase
import com.mobileone.shared.domain.usecase.RefreshAccountDataUseCase
import com.mobileone.shared.domain.usecase.ToggleBalanceVisibilityUseCase
import com.mobileone.shared.feature.home.AccountDisplay
import com.mobileone.shared.feature.home.DataStatus
import com.mobileone.shared.feature.home.HomeError
import com.mobileone.shared.feature.home.HomeUiState
import com.mobileone.shared.feature.home.TransactionDisplay
import com.mobileone.shared.domain.entity.Account
import com.mobileone.shared.domain.entity.Transaction
import com.mobileone.shared.util.CurrencyFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel da Home (SPEC-002). Consome os use cases do shared e expõe [HomeUiState]
 * via [StateFlow], seguindo as convenções de `.cursor/rules/05-android-conventions.mdc`.
 */
class HomeViewModel(
    private val observeAccount: ObserveAccountUseCase,
    private val getTransactionHistory: GetTransactionHistoryUseCase,
    private val refreshAccountData: RefreshAccountDataUseCase,
    private val toggleBalanceVisibility: ToggleBalanceVisibilityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentPage = 0
    private val accountId = FakeAccountRepository.DEMO_ACCOUNT_ID

    init {
        observeData()
        loadNextPage()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                observeAccount(accountId),
                toggleBalanceVisibility.observe()
            ) { account, isHidden ->
                account to isHidden
            }.collect { (account, isHidden) ->
                val staleThresholdMs = 5 * 60 * 1000L
                val age = System.currentTimeMillis() - account.updatedAt
                val dataStatus = if (account.updatedAt == 0L || age <= staleThresholdMs) {
                    DataStatus.Fresh
                } else {
                    DataStatus.Stale(account.updatedAt)
                }
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        account = account.toDisplay(isHidden),
                        isBalanceHidden = isHidden,
                        dataStatus = dataStatus
                    )
                }
            }
        }
    }

    fun onRefresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            currentPage = 0
            refreshAccountData(accountId)
                .onFailure {
                    _uiState.update { current ->
                        current.copy(isRefreshing = false, error = HomeError.NetworkError)
                    }
                }
                .onSuccess {
                    _uiState.update { it.copy(isRefreshing = false, transactions = emptyList(), hasMoreTransactions = true) }
                    loadNextPage()
                }
        }
    }

    fun onLoadMore() {
        if (_uiState.value.isLoadingMoreTransactions || !_uiState.value.hasMoreTransactions) return
        loadNextPage()
    }

    fun onToggleBalance() {
        viewModelScope.launch { toggleBalanceVisibility() }
    }

    fun onDismissError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun loadNextPage() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMoreTransactions = true) }
            val todayEpochDay = todayEpochDay()
            getTransactionHistory(accountId, currentPage)
                .onSuccess { page ->
                    val newItems = page.items.map { it.toDisplay(todayEpochDay) }
                    _uiState.update { current ->
                        current.copy(
                            isLoadingMoreTransactions = false,
                            transactions = current.transactions + newItems,
                            hasMoreTransactions = page.hasMore
                        )
                    }
                    if (page.hasMore) currentPage++
                }
                .onFailure {
                    _uiState.update { current ->
                        current.copy(
                            isLoadingMoreTransactions = false,
                            error = HomeError.NetworkError
                        )
                    }
                }
        }
    }

    private fun Account.toDisplay(isHidden: Boolean) = AccountDisplay(
        holderName = holderName,
        maskedNumber = maskedNumber,
        balanceFormatted = CurrencyFormatter.formatBalance(balanceCents, isHidden),
        availableLimitFormatted = CurrencyFormatter.formatBalance(availableLimitCents, isHidden)
    )

    private fun Transaction.toDisplay(todayEpochDay: Int) = TransactionDisplay(
        id = id,
        description = description,
        amountFormatted = CurrencyFormatter.format(amountCents, isDebit),
        isDebit = isDebit,
        dateFormatted = CurrencyFormatter.formatEpochDay(epochDay, todayEpochDay),
        category = category
    )

    private fun todayEpochDay(): Int {
        val msPerDay = 86_400_000L
        return (System.currentTimeMillis() / msPerDay).toInt()
    }
}
