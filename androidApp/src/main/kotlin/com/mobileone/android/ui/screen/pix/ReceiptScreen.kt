package com.mobileone.android.ui.screen.pix

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobileone.android.ui.theme.BankTheme
import com.mobileone.shared.config.BrandCatalog
import com.mobileone.shared.feature.pix.PixReceipt
import com.mobileone.shared.feature.pix.PixTransferUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen(
    uiState: PixTransferUiState,
    onShare: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val receipt = uiState.receipt ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comprovante") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Transferência realizada",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(72.dp)
            )

            Text(
                text = "Transferência realizada!",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = receipt.amountFormatted,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReceiptRow(label = "Para", value = receipt.recipientName)
                    ReceiptRow(label = "Data/hora", value = receipt.dateTimeFormatted)
                    HorizontalDivider()
                    ReceiptRow(label = "ID E2E", value = receipt.e2eId)
                    ReceiptRow(label = "Autenticação", value = receipt.authenticationCode)
                }
            }

            Spacer(Modifier.weight(1f))

            OutlinedButton(
                onClick = onShare,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Compartilhar comprovante")
            }

            Button(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Concluir")
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReceiptScreenPreview() {
    BankTheme(config = BrandCatalog.bancoPrincipal()) {
        ReceiptScreen(
            uiState = PixTransferUiState(
                receipt = PixReceipt(
                    transactionId = "TXN-00000001",
                    e2eId = "E0000000202607181200000123456789",
                    recipientName = "João da Silva",
                    amountFormatted = "R$ 150,00",
                    dateTimeFormatted = "Hoje às 12:00",
                    authenticationCode = "ABC123"
                )
            ),
            onShare = {},
            onClose = {}
        )
    }
}
