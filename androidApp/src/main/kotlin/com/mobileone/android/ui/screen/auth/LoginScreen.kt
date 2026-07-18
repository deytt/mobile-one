package com.mobileone.android.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
 * Login por CPF + senha (SPEC-001, node `29:20015`). Segue a separação stateful/stateless de
 * `.cursor/rules/05-android-conventions.mdc`.
 */
@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit = {},
    viewModel: AuthViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LoginContent(
        config = LocalWhiteLabelConfig.current,
        uiState = uiState,
        onLoginClick = viewModel::onLoginClick,
        onBiometricClick = viewModel::onBiometricLoginClick,
        onRegisterClick = onRegisterClick,
        onDismissError = viewModel::onDismissError
    )
}

@Composable
fun LoginContent(
    config: WhiteLabelConfig,
    uiState: AuthUiState,
    onLoginClick: (String, String) -> Unit,
    onBiometricClick: () -> Unit,
    onRegisterClick: () -> Unit = {},
    onDismissError: () -> Unit = {}
) {
    var cpf by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            LoginTopBar(config = config)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 32.dp, bottom = 24.dp)
            ) {
                Text(
                    text = "Bem-vindo de volta",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp),
                    text = "Entre com seu CPF e senha",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                FieldLabel(text = "CPF")
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    value = cpf,
                    onValueChange = { input -> cpf = maskCpf(input) },
                    placeholder = { Text("000.000.000-00") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    trailingIcon = { Icon(Icons.Filled.Dialpad, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    colors = loginFieldColors()
                )

                FieldLabel(text = "Senha")
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("••••••") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = loginFieldColors()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {}) {
                        Text(
                            text = "Esqueci minha senha",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (uiState.error != null) {
                    Text(
                        modifier = Modifier.padding(bottom = 16.dp),
                        text = errorMessage(uiState),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .padding(bottom = 16.dp),
                    onClick = {
                        onDismissError()
                        onLoginClick(cpf, password)
                    },
                    enabled = !uiState.isLoading && !uiState.isAccountLocked && cpf.isNotBlank() && password.isNotBlank(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(text = "Entrar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }

                if (uiState.isBiometricAvailable && uiState.isBiometricEnabled) {
                    OutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .padding(bottom = 24.dp),
                        onClick = onBiometricClick,
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true)
                    ) {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            imageVector = Icons.Filled.Fingerprint,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = "Entrar com biometria",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(
                        text = "ou",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Ainda não tem conta? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Abra a sua grátis",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable(onClick = onRegisterClick)
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginTopBar(config: WhiteLabelConfig) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.primary),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = brandInitials(config.brandName),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = config.brandName,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        modifier = Modifier.padding(bottom = 4.dp),
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun loginFieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
    focusedBorderColor = MaterialTheme.colorScheme.primary
)

private fun errorMessage(uiState: AuthUiState): String = when (uiState.error) {
    is AuthError.InvalidCredentials -> "CPF ou senha incorretos. Tentativa ${uiState.failedAttempts} de 5."
    is AuthError.AccountLocked -> "Conta bloqueada por ${uiState.lockRemainingSeconds}s após muitas tentativas."
    is AuthError.BiometricFailed -> "Não foi possível confirmar a biometria."
    is AuthError.NetworkError -> "Falha de conexão. Tente novamente."
    is AuthError.UnknownError -> (uiState.error as AuthError.UnknownError).message
    null -> ""
}

private fun maskCpf(raw: String): String {
    val digits = raw.filter { it.isDigit() }.take(11)
    val builder = StringBuilder()
    digits.forEachIndexed { index, c ->
        when (index) {
            3, 6 -> builder.append('.')
            9 -> builder.append('-')
        }
        builder.append(c)
    }
    return builder.toString()
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    val config = BrandCatalog.bancoPrincipal()
    BankTheme(config = config) {
        LoginContent(
            config = config,
            uiState = AuthUiState(isBiometricAvailable = true, isBiometricEnabled = true),
            onLoginClick = { _, _ -> },
            onBiometricClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenErrorPreview() {
    val config = BrandCatalog.fintechVerde()
    BankTheme(config = config) {
        LoginContent(
            config = config,
            uiState = AuthUiState(error = AuthError.InvalidCredentials, failedAttempts = 2),
            onLoginClick = { _, _ -> },
            onBiometricClick = {}
        )
    }
}
