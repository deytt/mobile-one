package com.mobileone.android.ui.screen.auth

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobileone.android.ui.component.BrandLogo
import com.mobileone.android.ui.theme.BankTheme
import com.mobileone.android.ui.theme.LocalWhiteLabelConfig
import com.mobileone.android.viewmodel.AuthViewModel
import com.mobileone.shared.config.BrandCatalog
import com.mobileone.shared.config.WhiteLabelConfig
import com.mobileone.shared.data.repository.FakeAuthRepository
import com.mobileone.shared.feature.auth.AuthError
import com.mobileone.shared.feature.auth.AuthUiState
import org.koin.androidx.compose.koinViewModel

/**
 * Login por CPF + senha (SPEC-007 layout / SPEC-001 comportamento).
 * Separação stateful/stateless conforme `.cursor/rules/05-android-conventions.mdc`.
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
    // Valores iniciais para execução local com repositório em memória.
    var cpf by remember { mutableStateOf(maskCpf(FakeAuthRepository.DEMO_CPF)) }
    var password by remember { mutableStateOf(FakeAuthRepository.DEMO_PASSWORD) }
    var passwordVisible by remember { mutableStateOf(false) }

    val radius = config.theme.borderRadiusDp.dp
    val shape = RoundedCornerShape(radius)
    val isPremium = config.brandId == "banco_premium"
    val canSubmit = !uiState.isLoading &&
        !uiState.isAccountLocked &&
        cpf.isNotBlank() &&
        password.isNotBlank()
    val dividerColor = Color(0xFF6B7280).copy(alpha = 0.25f)

    LoginPrimaryStatusBar()

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
            LoginHeader(config = config)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp, bottom = 24.dp)
        ) {
            Text(
                text = "Bem-vindo de volta",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = if (isPremium) FontStyle.Italic else FontStyle.Normal,
                    letterSpacing = (-0.48).sp,
                    lineHeight = 36.sp
                )
            )
            Text(
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp),
                text = "Entre com seu CPF e senha",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 19.5.sp
                )
            )

            FieldLabel(text = "CPF")
            BrandTextField(
                value = cpf,
                onValueChange = { cpf = maskCpf(it) },
                placeholder = "000.000.000-00",
                shape = shape,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Dialpad,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            FieldLabel(text = "Senha")
            BrandTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "••••••",
                shape = shape,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    Icon(
                        imageVector = if (passwordVisible) {
                            Icons.Filled.VisibilityOff
                        } else {
                            Icons.Filled.Visibility
                        },
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { passwordVisible = !passwordVisible },
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "Esqueci minha senha",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp
                    ),
                    modifier = Modifier
                        .clickable { }
                        .padding(vertical = 4.dp)
                )
            }

            if (uiState.error != null) {
                Text(
                    modifier = Modifier.padding(bottom = 12.dp),
                    text = errorMessage(uiState),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                onClick = {
                    onDismissError()
                    onLoginClick(cpf, password)
                },
                enabled = canSubmit,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = Color(0xFF6B7280).copy(alpha = 0.3f),
                    disabledContentColor = Color.White
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Entrar",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 22.5.sp
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.isBiometricAvailable && uiState.isBiometricEnabled) {
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    onClick = onBiometricClick,
                    enabled = !uiState.isLoading,
                    shape = shape,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        modifier = Modifier.size(18.dp),
                        imageVector = Icons.Filled.Fingerprint,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Entrar com biometria",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 22.5.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = dividerColor)
                Text(
                    text = "ou",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = dividerColor)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Ainda não tem conta? ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 19.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Abra a sua grátis",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 19.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(onClick = onRegisterClick)
                )
            }
        }
    }
}

@Composable
private fun LoginHeader(config: WhiteLabelConfig) {
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

@Composable
private fun FieldLabel(text: String) {
    Text(
        modifier = Modifier.padding(bottom = 4.dp),
        text = text,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.labelMedium.copy(
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 18.sp
        )
    )
}

/** Campo 48dp com radius da marca, border 1dp e ícone a 15dp da borda direita (SPEC-007). */
@Composable
private fun BrandTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    shape: RoundedCornerShape,
    keyboardOptions: KeyboardOptions,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable () -> Unit
) {
    val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    val placeholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
    val textStyle = MaterialTheme.typography.bodyMedium.copy(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        color = MaterialTheme.colorScheme.onBackground
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, borderColor, shape)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = textStyle,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart)
                .padding(start = 15.dp, end = 45.dp),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = textStyle.copy(color = placeholderColor)
                        )
                    }
                    innerTextField()
                }
            }
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 15.dp)
                .size(16.dp),
            contentAlignment = Alignment.Center
        ) {
            trailingIcon()
        }
    }
}

/** Status bar com ícones claros sobre `colorPrimary` (SPEC-007). */
@Composable
private fun LoginPrimaryStatusBar() {
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
private fun LoginScreenFintechPreview() {
    val config = BrandCatalog.fintechVerde()
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
    val config = BrandCatalog.bancoPremium()
    BankTheme(config = config) {
        LoginContent(
            config = config,
            uiState = AuthUiState(error = AuthError.InvalidCredentials, failedAttempts = 2),
            onLoginClick = { _, _ -> },
            onBiometricClick = {}
        )
    }
}
