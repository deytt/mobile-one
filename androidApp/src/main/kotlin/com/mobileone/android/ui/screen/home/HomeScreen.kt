package com.mobileone.android.ui.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobileone.android.ui.screen.home.components.AccountCard
import com.mobileone.android.ui.screen.home.components.BalanceSkeleton
import com.mobileone.android.ui.screen.home.components.QuickActionsRow
import com.mobileone.android.ui.screen.home.components.StaleBanner
import com.mobileone.android.ui.screen.home.components.TransactionDateHeader
import com.mobileone.android.ui.screen.home.components.TransactionItem
import com.mobileone.android.ui.theme.BankTheme
import com.mobileone.android.ui.theme.LocalWhiteLabelConfig
import com.mobileone.android.viewmodel.HomeViewModel
import com.mobileone.shared.config.BrandCatalog
import com.mobileone.shared.domain.entity.TransactionCategory
import com.mobileone.shared.feature.home.AccountDisplay
import com.mobileone.shared.feature.home.DataStatus
import com.mobileone.shared.feature.home.HomeUiState
import com.mobileone.shared.feature.home.TransactionDisplay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.koin.androidx.compose.koinViewModel

/**
 * Tela Home stateful (SPEC-002): consome [HomeViewModel] e delega a renderização ao
 * [HomeContent] stateless — segue `.cursor/rules/05-android-conventions.mdc`.
 */
@Composable
fun HomeScreen(
    onBrandSwitcherClick: () -> Unit,
    onPixClick: () -> Unit = {},
    onLogoutClick: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(
        uiState = uiState,
        onRefresh = viewModel::onRefresh,
        onLoadMore = viewModel::onLoadMore,
        onToggleBalance = viewModel::onToggleBalance,
        onDismissError = viewModel::onDismissError,
        onBrandSwitcherClick = onBrandSwitcherClick,
        onPixClick = onPixClick,
        onLogoutClick = onLogoutClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    uiState: HomeUiState,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onToggleBalance: () -> Unit,
    onDismissError: () -> Unit,
    onBrandSwitcherClick: () -> Unit,
    onPixClick: () -> Unit = {},
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val config = LocalWhiteLabelConfig.current
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    // Detecta quando o scroll chega em 80% para paginação infinita
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= (total * 0.8).toInt()
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { shouldLoadMore }
            .distinctUntilChanged()
            .filter { it }
            .collect { onLoadMore() }
    }

    uiState.error?.let { error ->
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar("Erro ao carregar dados. Tente novamente.")
            onDismissError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(config.brandName) },
                actions = {
                    IconButton(onClick = onBrandSwitcherClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Trocar marca")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(snackbarData = data)
            }
        },
        modifier = modifier
    ) { innerPadding ->
        if (uiState.isLoading && uiState.account == null) {
            LoadingContent(innerPadding)
            return@Scaffold
        }

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Stale banner
                val staleStatus = uiState.dataStatus as? DataStatus.Stale
                if (staleStatus != null) {
                    item(key = "stale_banner") {
                        StaleBanner(
                            lastUpdatedAt = staleStatus.lastUpdatedAt,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }

                // Card de conta
                item(key = "account_card") {
                    uiState.account?.let { account ->
                        AccountCard(
                            account = account,
                            isBalanceHidden = uiState.isBalanceHidden,
                            onToggleBalance = onToggleBalance,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    } ?: BalanceSkeleton(Modifier.padding(bottom = 16.dp))
                }

                // Ações rápidas
                item(key = "quick_actions") {
                    QuickActionsRow(
                        features = config.features,
                        onPixClick = onPixClick,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                }

                // Header "Extrato"
                item(key = "header_extrato") {
                    Text(
                        text = "Extrato",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                // Transações agrupadas por data
                val grouped = uiState.transactions.groupBy { it.dateFormatted }
                grouped.forEach { (date, txs) ->
                    item(key = "date_$date") {
                        TransactionDateHeader(date)
                    }
                    items(txs, key = { it.id }) { tx ->
                        TransactionItem(transaction = tx)
                    }
                }

                // Spinner de carregamento de mais itens
                if (uiState.isLoadingMoreTransactions) {
                    item(key = "loading_more") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }

                // Botão de logout (dev mode)
                item(key = "logout") {
                    Spacer(Modifier.height(16.dp))
                    TextButton(onClick = onLogoutClick, modifier = Modifier.fillMaxWidth()) {
                        Text("Sair", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingContent(innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        BalanceSkeleton()
        Spacer(Modifier.height(16.dp))
        BalanceSkeleton()
        Spacer(Modifier.height(16.dp))
        BalanceSkeleton()
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    BankTheme(config = BrandCatalog.bancoPrincipal()) {
        HomeContent(
            uiState = HomeUiState(
                account = AccountDisplay(
                    holderName = "Heitor Bastos",
                    maskedNumber = "•••• 4521",
                    balanceFormatted = "R$ 1.234,56",
                    availableLimitFormatted = "R$ 5.000,00"
                ),
                transactions = listOf(
                    TransactionDisplay("1", "PIX - João", "- R$ 150,00", true, "Hoje", TransactionCategory.PIX),
                    TransactionDisplay("2", "Salário", "+ R$ 2.000,00", false, "Hoje", TransactionCategory.DEPOSIT),
                    TransactionDisplay("3", "Netflix", "- R$ 12,00", true, "Ontem", TransactionCategory.PURCHASE)
                )
            ),
            onRefresh = {},
            onLoadMore = {},
            onToggleBalance = {},
            onDismissError = {},
            onBrandSwitcherClick = {},
            onLogoutClick = {}
        )
    }
}
