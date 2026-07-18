package com.mobileone.android.ui.screen.home

import androidx.compose.runtime.Composable
import com.mobileone.android.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Alias de compatibilidade (SPEC-002 → SPEC-010): a Home de Conta vive em [HomeContaScreen].
 */
@Composable
fun HomeScreen(
    onNavigateToCartoes: () -> Unit = {},
    onBrandSwitcherClick: () -> Unit,
    onPixClick: () -> Unit = {},
    onLogoutClick: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    HomeContaScreen(
        onNavigateToCartoes = onNavigateToCartoes,
        onBrandSwitcherClick = onBrandSwitcherClick,
        onPixClick = onPixClick,
        onLogoutClick = onLogoutClick,
        viewModel = viewModel
    )
}
