package com.mobileone.android.ui.screen.home

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobileone.android.ui.screen.home.components.BalanceSkeleton
import com.mobileone.android.ui.screen.home.components.HomeGreetingHeader
import com.mobileone.android.ui.screen.home.components.HomeTabBar
import com.mobileone.android.ui.screen.home.components.StaleBanner
import com.mobileone.android.ui.theme.BankTheme
import com.mobileone.android.ui.theme.LocalWhiteLabelConfig
import com.mobileone.android.viewmodel.HomeViewModel
import com.mobileone.shared.config.BrandCatalog
import com.mobileone.shared.config.WhiteLabelConfig
import com.mobileone.shared.domain.entity.TransactionCategory
import com.mobileone.shared.feature.home.AccountDisplay
import com.mobileone.shared.feature.home.DataStatus
import com.mobileone.shared.feature.home.HomeUiState
import com.mobileone.shared.feature.home.TransactionDisplay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.koin.androidx.compose.koinViewModel

private val CreditGreen = Color(0xFF22C55E)

/**
 * Home de Conta (SPEC-010): saldo, quick actions, feature cards, transações e Bottom Tab Bar.
 */
@Composable
fun HomeContaScreen(
    onNavigateToCartoes: () -> Unit = {},
    onBrandSwitcherClick: () -> Unit,
    onPixClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContaContent(
        uiState = uiState,
        config = LocalWhiteLabelConfig.current,
        onRefresh = viewModel::onRefresh,
        onLoadMore = viewModel::onLoadMore,
        onToggleBalance = viewModel::onToggleBalance,
        onDismissError = viewModel::onDismissError,
        onNavigateToCartoes = onNavigateToCartoes,
        onBrandSwitcherClick = onBrandSwitcherClick,
        onPixClick = onPixClick,
        onLogoutClick = onLogoutClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContaContent(
    uiState: HomeUiState,
    config: WhiteLabelConfig,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onToggleBalance: () -> Unit,
    onDismissError: () -> Unit,
    onNavigateToCartoes: () -> Unit = {},
    onBrandSwitcherClick: () -> Unit,
    onPixClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val radius = config.theme.borderRadiusDp.dp
    val bottomShape = RoundedCornerShape(bottomStart = radius, bottomEnd = radius)

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

    HomePrimaryStatusBar()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            HomeTabBar(
                currentTab = HomeTab.CONTA,
                onTabChange = { tab ->
                    if (tab == HomeTab.CARTOES) onNavigateToCartoes()
                },
                onBrandSwitcher = onBrandSwitcherClick
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(snackbarData = data)
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading && uiState.account == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .padding(16.dp)
            ) {
                BalanceSkeleton()
                Spacer(Modifier.height(16.dp))
                BalanceSkeleton()
            }
            return@Scaffold
        }

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(bottom = 16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item(key = "hero") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(bottomShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .statusBarsPadding()
                    ) {
                        HomeGreetingHeader(
                            userName = uiState.account?.holderName ?: "Heitor Bastos"
                        )
                        HeroBalanceSection(
                            balanceFormatted = uiState.account?.balanceFormatted ?: "R$ ••••••",
                            isBalanceHidden = uiState.isBalanceHidden,
                            onToggleBalance = onToggleBalance,
                            onPagar = {},
                            onExtrato = {},
                            onPix = onPixClick
                        )
                    }
                }

                val staleStatus = uiState.dataStatus as? DataStatus.Stale
                if (staleStatus != null) {
                    item(key = "stale_banner") {
                        StaleBanner(
                            lastUpdatedAt = staleStatus.lastUpdatedAt,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }

                item(key = "feature_pix") {
                    FeaturePromoCard(
                        title = "PIX",
                        subtitle = "Transferências e pagamentos instantâneos, disponíveis 24h por dia",
                        icon = Icons.Default.Bolt,
                        shape = RoundedCornerShape(radius),
                        onClick = onPixClick,
                        modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)
                    )
                }

                item(key = "feature_open_finance") {
                    FeaturePromoCard(
                        title = "Open Finance",
                        subtitle = "Conecte suas contas de outros bancos e tenha uma visão completa",
                        icon = Icons.Default.AccountBalance,
                        shape = RoundedCornerShape(radius),
                        onClick = {},
                        modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)
                    )
                }

                item(key = "transactions_header") {
                    TransactionsSectionHeader(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 20.dp)
                    )
                }

                item(key = "transactions_list") {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 1.dp
                    ) {
                        Column {
                            uiState.transactions.forEachIndexed { index, tx ->
                                RecentTransactionRow(transaction = tx)
                                if (index < uiState.transactions.lastIndex) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .background(MaterialTheme.colorScheme.background)
                                    )
                                }
                            }
                        }
                    }
                }

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

                item(key = "logout") {
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = onLogoutClick, modifier = Modifier.fillMaxWidth()) {
                        Text("Sair", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroBalanceSection(
    balanceFormatted: String,
    isBalanceHidden: Boolean,
    onToggleBalance: () -> Unit,
    onPagar: () -> Unit,
    onExtrato: () -> Unit,
    onPix: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Text(
            text = "Saldo disponível",
            color = Color.White.copy(alpha = 0.70f),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal
            )
        )

        Row(
            modifier = Modifier.padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = balanceFormatted,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.56).sp,
                    lineHeight = 39.sp
                )
            )
            IconButton(
                onClick = onToggleBalance,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(28.dp)
            ) {
                Icon(
                    imageVector = if (isBalanceHidden) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (isBalanceHidden) "Mostrar saldo" else "Ocultar saldo",
                    tint = Color.White.copy(alpha = 0.80f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally)
        ) {
            ContaQuickAction(
                label = "Pagar",
                icon = Icons.AutoMirrored.Filled.OpenInNew,
                onClick = onPagar
            )
            ContaQuickAction(
                label = "Extrato",
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                onClick = onExtrato
            )
            ContaQuickAction(
                label = "PIX",
                icon = Icons.Default.QrCode2,
                onClick = onPix
            )
        }
    }
}

@Composable
private fun ContaQuickAction(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = label,
            modifier = Modifier.padding(top = 6.dp),
            color = Color.White.copy(alpha = 0.90f),
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun FeaturePromoCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    shape: RoundedCornerShape,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = shape,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = subtitle,
                    modifier = Modifier.padding(top = 2.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun TransactionsSectionHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Transações recentes",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = {}) {
            Text(
                text = "Ver todas",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun RecentTransactionRow(transaction: TransactionDisplay) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = transaction.category.toContaIcon(),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = transaction.description,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${transaction.dateFormatted} · ${transaction.category.toContaLabel()}",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
            )
        }

        Text(
            text = transaction.displayAmount(),
            color = if (transaction.isDebit) {
                MaterialTheme.colorScheme.onBackground
            } else {
                CreditGreen
            },
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

/** Débito sem prefixo "-"; crédito no formato Figma "+R$ …". */
private fun TransactionDisplay.displayAmount(): String {
    if (isDebit) {
        return amountFormatted.removePrefix("- ").trimStart()
    }
    val value = amountFormatted
        .removePrefix("+ ")
        .removePrefix("+")
        .trimStart()
    return if (value.startsWith("R$")) "+$value" else "+R$ $value"
}

private fun TransactionCategory.toContaIcon(): ImageVector = when (this) {
    TransactionCategory.PIX -> Icons.Default.Bolt
    TransactionCategory.TED -> Icons.Outlined.Sync
    TransactionCategory.BOLETO -> Icons.AutoMirrored.Filled.ReceiptLong
    TransactionCategory.CARD -> Icons.Outlined.CreditCard
    TransactionCategory.PURCHASE -> Icons.Outlined.ShoppingBag
    TransactionCategory.DEPOSIT -> Icons.Outlined.AttachMoney
    TransactionCategory.FEE -> Icons.AutoMirrored.Filled.ReceiptLong
    TransactionCategory.OTHER -> Icons.Default.ShoppingCart
}

private fun TransactionCategory.toContaLabel(): String = when (this) {
    TransactionCategory.PIX -> "Transferência"
    TransactionCategory.TED -> "Transferência"
    TransactionCategory.BOLETO -> "Pagamento"
    TransactionCategory.CARD -> "Cartão"
    TransactionCategory.PURCHASE -> "Compras"
    TransactionCategory.DEPOSIT -> "Depósito"
    TransactionCategory.FEE -> "Taxa"
    TransactionCategory.OTHER -> "Outros"
}

/** Status bar com ícones claros sobre `colorPrimary` (SPEC-010). */
@Composable
private fun HomePrimaryStatusBar() {
    val view = LocalView.current
    DisposableEffect(Unit) {
        val window = (view.context as? Activity)?.window ?: return@DisposableEffect onDispose { }
        val controller = WindowCompat.getInsetsController(window, view)
        val previousLight = controller.isAppearanceLightStatusBars
        controller.isAppearanceLightStatusBars = false
        onDispose {
            controller.isAppearanceLightStatusBars = previousLight
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContaPreview() {
    val config = BrandCatalog.bancoPrincipal()
    BankTheme(config = config) {
        HomeContaContent(
            uiState = previewUiState(),
            config = config,
            onRefresh = {},
            onLoadMore = {},
            onToggleBalance = {},
            onDismissError = {},
            onBrandSwitcherClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContaFintechPreview() {
    val config = BrandCatalog.fintechVerde()
    BankTheme(config = config) {
        HomeContaContent(
            uiState = previewUiState(),
            config = config,
            onRefresh = {},
            onLoadMore = {},
            onToggleBalance = {},
            onDismissError = {},
            onBrandSwitcherClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContaPremiumPreview() {
    val config = BrandCatalog.bancoPremium()
    BankTheme(config = config) {
        HomeContaContent(
            uiState = previewUiState(),
            config = config,
            onRefresh = {},
            onLoadMore = {},
            onToggleBalance = {},
            onDismissError = {},
            onBrandSwitcherClick = {}
        )
    }
}

private fun previewUiState() = HomeUiState(
    account = AccountDisplay(
        holderName = "Heitor Bastos",
        maskedNumber = "•••• 4521",
        balanceFormatted = "R$ 3.547,80",
        availableLimitFormatted = "R$ 5.000,00"
    ),
    transactions = listOf(
        TransactionDisplay("1", "Netflix", "- R$ 45,90", true, "15 Jul", TransactionCategory.PURCHASE),
        TransactionDisplay("2", "Supermercado Extra", "- R$ 234,50", true, "14 Jul", TransactionCategory.PURCHASE),
        TransactionDisplay("3", "PIX — João Silva", "+ R$ 500,00", false, "13 Jul", TransactionCategory.PIX),
        TransactionDisplay("4", "Farmácia São Paulo", "- R$ 67,80", true, "12 Jul", TransactionCategory.PURCHASE),
        TransactionDisplay("5", "Uber", "- R$ 28,50", true, "11 Jul", TransactionCategory.PURCHASE)
    )
)
