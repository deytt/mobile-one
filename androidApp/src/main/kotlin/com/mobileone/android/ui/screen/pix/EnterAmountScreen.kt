package com.mobileone.android.ui.screen.pix

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobileone.android.ui.theme.BankTheme
import com.mobileone.shared.config.BrandCatalog
import com.mobileone.shared.feature.pix.PixTransferUiState

/** Limite noturno para aviso na tela — corresponde ao valor em centavos de R$ 1.000,00. */
private const val NIGHT_LIMIT_CENTS = 1_000_00L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterAmountScreen(
    uiState: PixTransferUiState,
    onAmountChanged: (Long) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Texto local do campo; persiste como string durante a digitação
    var amountText by remember { mutableStateOf(if (uiState.amount > 0) (uiState.amount / 100.0).toString() else "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Valor") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
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
                text = "Quanto você quer enviar?",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { input ->
                    amountText = input
                    val parsed = input.replace(",", ".").toDoubleOrNull()
                    val cents = if (parsed != null) (parsed * 100).toLong() else 0L
                    onAmountChanged(cents)
                },
                label = { Text("Valor (R$)") },
                prefix = { Text("R$ ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    textAlign = TextAlign.End
                ),
                supportingText = {
                    Text(
                        "Limite diurno: R$ 20.000,00 · Limite noturno: R$ 1.000,00",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange = onDescriptionChanged,
                label = { Text("Descrição (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.weight(1f))

            // Aviso de limite noturno
            if (uiState.amount in 1..NIGHT_LIMIT_CENTS) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "⚠ Entre 21h e 6h, o limite é R$ 1.000,00 por transferência.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = onContinue,
                enabled = uiState.amount > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continuar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EnterAmountScreenPreview() {
    BankTheme(config = BrandCatalog.bancoPrincipal()) {
        EnterAmountScreen(
            uiState = PixTransferUiState(amount = 15000L, amountFormatted = "R$ 150,00"),
            onAmountChanged = {},
            onDescriptionChanged = {},
            onContinue = {},
            onBack = {}
        )
    }
}
