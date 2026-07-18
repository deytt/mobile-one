package com.mobileone.android.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobileone.android.ui.component.brandInitials
import com.mobileone.android.ui.theme.BankTheme
import com.mobileone.android.viewmodel.AuthViewModel
import com.mobileone.shared.config.BrandCatalog
import com.mobileone.shared.feature.auth.AuthError
import com.mobileone.shared.feature.auth.AuthUiState
import org.koin.androidx.compose.koinViewModel

/**
 * Boas-vindas + biometria (SPEC-001, node `29:20689`): oferece login rápido por biometria após
 * uma sessão previamente autenticada com biometria habilitada.
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
        uiState = uiState,
        onBiometricClick = viewModel::onBiometricLoginClick,
        onUsePasswordClick = onUsePasswordClick
    )
}

@Composable
fun BiometricWelcomeContent(
    uiState: AuthUiState,
    onBiometricClick: () -> Unit,
    onUsePasswordClick: () -> Unit
) {
    val firstName = uiState.userName?.trim()?.split(" ")?.firstOrNull() ?: "você"

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = brandInitials(uiState.userName ?: "?"),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                    text = "Olá, $firstName!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier.padding(bottom = 48.dp),
                    text = "Confirme sua identidade para acessar sua conta",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), CircleShape)
                        .clickable(enabled = !uiState.isLoading, onClick = onBiometricClick),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    } else {
                        Icon(
                            modifier = Modifier.size(40.dp),
                            imageVector = Icons.Filled.Fingerprint,
                            contentDescription = "Autenticar com biometria",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "Toque para usar biometria",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )

                if (uiState.error != null) {
                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text = "Não foi possível confirmar sua biometria. Tente novamente ou use CPF e senha.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.clickable(onClick = onUsePasswordClick),
                    text = "Usar CPF e senha",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BiometricWelcomeScreenPreview() {
    BankTheme(config = BrandCatalog.bancoPrincipal()) {
        BiometricWelcomeContent(
            uiState = AuthUiState(userName = "Heitor Bastos"),
            onBiometricClick = {},
            onUsePasswordClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BiometricWelcomeScreenLoadingPreview() {
    BankTheme(config = BrandCatalog.fintechVerde()) {
        BiometricWelcomeContent(
            uiState = AuthUiState(userName = "Heitor Bastos", isLoading = true),
            onBiometricClick = {},
            onUsePasswordClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BiometricWelcomeScreenErrorPreview() {
    BankTheme(config = BrandCatalog.bancoPremium()) {
        BiometricWelcomeContent(
            uiState = AuthUiState(userName = "Heitor Bastos", error = AuthError.BiometricFailed),
            onBiometricClick = {},
            onUsePasswordClick = {}
        )
    }
}
