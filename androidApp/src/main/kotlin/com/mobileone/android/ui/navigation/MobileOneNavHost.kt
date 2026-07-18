package com.mobileone.android.ui.navigation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mobileone.android.ui.screen.auth.BiometricWelcomeScreen
import com.mobileone.android.ui.screen.auth.LoginScreen
import com.mobileone.android.ui.screen.auth.SplashScreen
import com.mobileone.android.ui.screen.brandSwitcher.BrandSwitcherScreen
import com.mobileone.android.ui.screen.home.HomeCartoesScreen
import com.mobileone.android.ui.screen.home.HomeScreen
import com.mobileone.android.ui.screen.pix.PixFlowScreen
import com.mobileone.android.viewmodel.AuthViewModel
import com.mobileone.shared.feature.auth.AuthNavigation
import org.koin.androidx.compose.koinViewModel

private object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val BIOMETRIC_WELCOME = "biometricWelcome"
    const val HOME_CARTOES = "homeCartoes"
    const val HOME_CONTA = "homeConta"
    const val BRAND_SWITCHER = "brandSwitcher"
    const val PIX = "pix"
}

/**
 * Rotas do app (SPEC-001 + SPEC-002 + SPEC-009):
 * - Splash decide entre Login e Boas-vindas com biometria (SPEC-001)
 * - Autenticação converge para Home de Cartões (SPEC-009)
 * - Bottom Tab Bar alterna Cartões ↔ Conta; botão grade abre BrandSwitcher
 */
@Composable
fun MobileOneNavHost(navController: NavHostController = rememberNavController()) {
    val authViewModel: AuthViewModel = koinViewModel()
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.navigation) {
        if (uiState.navigation == AuthNavigation.ToHome) {
            navController.navigate(Routes.HOME_CARTOES) {
                popUpTo(0) { inclusive = true }
            }
            authViewModel.onConsumeNavigation()
        }
    }

    if (uiState.navigation == AuthNavigation.ToBiometricSetup) {
        BiometricSetupDialog(
            isLoading = uiState.isLoading,
            onConfirm = authViewModel::onSetupBiometricConfirm,
            onSkip = authViewModel::onSkipBiometricSetup
        )
    }

    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = {
                    val destination = if (uiState.isBiometricEnabled) Routes.BIOMETRIC_WELCOME else Routes.LOGIN
                    navController.navigate(destination) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.LOGIN) {
            LoginScreen(viewModel = authViewModel)
        }
        composable(Routes.BIOMETRIC_WELCOME) {
            BiometricWelcomeScreen(
                onUsePasswordClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.BIOMETRIC_WELCOME) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }
        composable(Routes.HOME_CARTOES) {
            HomeCartoesScreen(
                onNavigateToConta = {
                    navController.navigate(Routes.HOME_CONTA) {
                        popUpTo(Routes.HOME_CARTOES) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBrandSwitcherClick = {
                    navController.navigate(Routes.BRAND_SWITCHER)
                }
            )
        }
        composable(Routes.HOME_CONTA) {
            HomeScreen(
                onNavigateToCartoes = {
                    navController.navigate(Routes.HOME_CARTOES) {
                        popUpTo(Routes.HOME_CONTA) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBrandSwitcherClick = {
                    navController.navigate(Routes.BRAND_SWITCHER)
                },
                onPixClick = {
                    navController.navigate(Routes.PIX)
                },
                onLogoutClick = {
                    authViewModel.onLogoutClick()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.BRAND_SWITCHER) {
            BrandSwitcherScreen(
                onBack = { navController.popBackStack() },
                onApplied = {
                    navController.navigate(Routes.HOME_CARTOES) {
                        popUpTo(Routes.HOME_CARTOES) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.PIX) {
            PixFlowScreen(
                onClose = { navController.popBackStack() }
            )
        }
    }
}

/**
 * Interstício "Configurar biometria?" do fluxo da SPEC-001 (nó [Setup] do diagrama do plano) —
 * sem tela dedicada no Figma, resolvido como confirmação simples após o primeiro login por senha.
 */
@Composable
private fun BiometricSetupDialog(
    isLoading: Boolean,
    onConfirm: () -> Unit,
    onSkip: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onSkip,
        title = { Text("Ativar login por biometria?") },
        text = { Text("Use sua digital ou reconhecimento facial para entrar mais rápido nas próximas vezes.") },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !isLoading) {
                Text("Ativar")
            }
        },
        dismissButton = {
            TextButton(onClick = onSkip, enabled = !isLoading) {
                Text("Agora não")
            }
        }
    )
}
