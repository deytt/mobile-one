package com.mobileone.android.ui.screen.auth

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobileone.android.ui.component.BrandLogo
import com.mobileone.android.ui.component.brandInitials
import com.mobileone.android.ui.theme.BankTheme
import com.mobileone.android.ui.theme.LocalWhiteLabelConfig
import com.mobileone.android.viewmodel.AuthViewModel
import com.mobileone.shared.config.BrandCatalog
import com.mobileone.shared.config.WhiteLabelConfig
import com.mobileone.shared.feature.auth.AuthError
import com.mobileone.shared.feature.auth.AuthUiState
import org.koin.androidx.compose.koinViewModel

/**
 * Boas-vindas + biometria (SPEC-008 layout / SPEC-001 comportamento).
 */
@Composable
fun BiometricWelcomeScreen(
    onUsePasswordClick: () -> Unit,
    viewModel: AuthViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onBiometricLoginClick()
    }

    BiometricWelcomeContent(
        config = LocalWhiteLabelConfig.current,
        uiState = uiState,
        onBiometricClick = viewModel::onBiometricLoginClick,
        onUsePasswordClick = onUsePasswordClick
    )
}

@Composable
fun BiometricWelcomeContent(
    config: WhiteLabelConfig,
    uiState: AuthUiState,
    onBiometricClick: () -> Unit,
    onUsePasswordClick: () -> Unit
) {
    val firstName = uiState.userName?.trim()?.split(" ")?.firstOrNull() ?: "você"
    val isPremium = config.brandId == "banco_premium"

    BiometricPrimaryStatusBar()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .statusBarsPadding()
        ) {
            BiometricHeader(config = config)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp)
                .padding(top = 40.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = CircleShape,
                        ambientColor = Color.Black.copy(alpha = 0.1f),
                        spotColor = Color.Black.copy(alpha = 0.1f)
                    )
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = brandInitials(uiState.userName ?: "?"),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = if (isPremium) FontStyle.Italic else FontStyle.Normal,
                        letterSpacing = (-0.44).sp,
                        lineHeight = 33.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Olá, $firstName!",
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = if (isPremium) FontStyle.Italic else FontStyle.Normal,
                    letterSpacing = (-0.44).sp,
                    lineHeight = 33.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier
                    .widthIn(max = 220.dp)
                    .padding(bottom = 48.dp),
                text = "Confirme sua identidade para acessar sua conta",
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 17.875.sp
                )
            )

            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clickable(enabled = !uiState.isLoading, onClick = onBiometricClick),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
                )
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        modifier = Modifier.size(52.dp),
                        imageVector = Icons.Filled.Fingerprint,
                        contentDescription = "Biometria",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Toque para usar biometria",
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 19.5.sp
                )
            )

            if (uiState.error != null) {
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "Não foi possível confirmar sua biometria. Tente novamente ou use CPF e senha.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                modifier = Modifier.clickable(onClick = onUsePasswordClick),
                text = "Usar CPF e senha",
                color = MaterialTheme.colorScheme.onSurface,
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 19.5.sp
                )
            )
        }
    }
}

@Composable
private fun BiometricHeader(config: WhiteLabelConfig) {
    val isPremium = config.brandId == "banco_premium"
    val isFintech = config.brandId == "fintech_verde"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.primary),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BrandLogo(config = config, size = 28.dp)
            Text(
                text = config.brandName,
                color = Color.White,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 13.sp,
                    fontWeight = if (isFintech) FontWeight.SemiBold else FontWeight.Bold,
                    fontStyle = if (isPremium) FontStyle.Italic else FontStyle.Normal,
                    letterSpacing = if (isPremium) 0.16.sp else (-0.26).sp,
                    lineHeight = 19.5.sp
                )
            )
        }
    }
}

/** Status bar com ícones claros sobre `colorPrimary` (SPEC-008 / SPEC-007). */
@Composable
private fun BiometricPrimaryStatusBar() {
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
private fun BiometricWelcomeScreenPreview() {
    val config = BrandCatalog.bancoPrincipal()
    BankTheme(config = config) {
        BiometricWelcomeContent(
            config = config,
            uiState = AuthUiState(userName = "Heitor Bastos"),
            onBiometricClick = {},
            onUsePasswordClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BiometricWelcomeScreenLoadingPreview() {
    val config = BrandCatalog.fintechVerde()
    BankTheme(config = config) {
        BiometricWelcomeContent(
            config = config,
            uiState = AuthUiState(userName = "Heitor Bastos", isLoading = true),
            onBiometricClick = {},
            onUsePasswordClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BiometricWelcomeScreenErrorPreview() {
    val config = BrandCatalog.bancoPremium()
    BankTheme(config = config) {
        BiometricWelcomeContent(
            config = config,
            uiState = AuthUiState(userName = "Heitor Bastos", error = AuthError.BiometricFailed),
            onBiometricClick = {},
            onUsePasswordClick = {}
        )
    }
}
