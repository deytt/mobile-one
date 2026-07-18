package com.mobileone.android.ui.screen.pix

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pix
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobileone.android.ui.theme.BankTheme
import com.mobileone.shared.config.BrandCatalog
import com.mobileone.shared.domain.entity.PixKeyType
import com.mobileone.shared.feature.pix.PixKeyValidation
import com.mobileone.shared.feature.pix.PixTransferUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterKeyScreen(
    uiState: PixTransferUiState,
    onKeyChanged: (String) -> Unit,
    onContinue: () -> Unit,
    onScanQRCode: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transferência PIX") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = onScanQRCode) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear QR Code")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Qual é a chave PIX?",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Digite CPF, CNPJ, e-mail, telefone ou chave aleatória.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.keyInput,
                onValueChange = onKeyChanged,
                label = { Text("Chave PIX") },
                leadingIcon = {
                    Icon(
                        imageVector = uiState.detectedKeyType.toIcon(),
                        contentDescription = null
                    )
                },
                supportingText = {
                    when (val v = uiState.keyValidation) {
                        is PixKeyValidation.Invalid -> Text(
                            v.reason,
                            color = MaterialTheme.colorScheme.error
                        )
                        is PixKeyValidation.Valid -> Text(
                            uiState.detectedKeyType.toLabel(),
                            color = MaterialTheme.colorScheme.primary
                        )
                        else -> {}
                    }
                },
                isError = uiState.keyValidation is PixKeyValidation.Invalid,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(visible = uiState.keyValidation is PixKeyValidation.Valid) {
                Button(
                    onClick = onContinue,
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isLoading) {
                        Text("Consultando...")
                    } else {
                        Text("Continuar")
                    }
                }
            }
        }
    }
}

private fun PixKeyType?.toIcon() = when (this) {
    is PixKeyType.CPF, is PixKeyType.CNPJ -> Icons.Default.Person
    is PixKeyType.Email -> Icons.Default.Email
    is PixKeyType.Phone -> Icons.Default.Pix
    is PixKeyType.RandomKey -> Icons.Default.Pix
    is PixKeyType.QRCode -> Icons.Default.QrCodeScanner
    null -> Icons.Default.Pix
}

private fun PixKeyType?.toLabel() = when (this) {
    is PixKeyType.CPF -> "CPF detectado"
    is PixKeyType.CNPJ -> "CNPJ detectado"
    is PixKeyType.Email -> "E-mail detectado"
    is PixKeyType.Phone -> "Telefone detectado"
    is PixKeyType.RandomKey -> "Chave aleatória detectada"
    is PixKeyType.QRCode -> "QR Code"
    null -> ""
}

@Preview(showBackground = true)
@Composable
private fun EnterKeyScreenPreview() {
    BankTheme(config = BrandCatalog.bancoPrincipal()) {
        EnterKeyScreen(
            uiState = PixTransferUiState(
                keyInput = "joao@email.com",
                detectedKeyType = PixKeyType.Email,
                keyValidation = PixKeyValidation.Valid
            ),
            onKeyChanged = {},
            onContinue = {},
            onScanQRCode = {},
            onBack = {}
        )
    }
}
