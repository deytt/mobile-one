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
import com.mobileone.android.ui.screen.home.HomeScreen
import com.mobileone.android.viewmodel.AuthViewModel
import com.mobileone.shared.feature.auth.AuthNavigation
import org.koin.androidx.compose.koinViewModel

private object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val BIOMETRIC_WELCOME = "biometricWelcome"
    const val HOME = "home"
    const val BRAND_SWITCHER = "brandSwitcher"
}

/**
 * Rotas do app (SPEC-001 + SPEC-002):
 * - Splash decide entre Login e Boas-vindas com biometria (SPEC-001)
 * - Autenticação converge para Home real (SPEC-002)
 * - Home expõe ícone ⚙ que navega para BrandSwitcher (SPEC-004)
 */
@Composable
fun MobileOneNavHost(navController: NavHostController = rememberNavController()) {
    val authViewModel: AuthViewModel = koinViewModel()
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.navigation) {
        if (uiState.navigation == AuthNavigation.ToHome) {
            navController.navigate(Routes.HOME) {
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
        composable(Routes.HOME) {
            HomeScreen(
                onBrandSwitcherClick = {
                    navController.navigate(Routes.BRAND_SWITCHER)
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
                    // Tema já foi atualizado via AppStateRepository (observado na MainActivity).
                    // Volta para Home e recria o stack para refletir o novo tema.
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
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
