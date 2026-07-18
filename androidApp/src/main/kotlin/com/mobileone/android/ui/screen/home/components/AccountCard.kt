package com.mobileone.android.ui.screen.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobileone.android.ui.theme.BankTheme
import com.mobileone.shared.config.BrandCatalog
import com.mobileone.shared.feature.home.AccountDisplay

@Composable
fun AccountCard(
    account: AccountDisplay,
    isBalanceHidden: Boolean,
    onToggleBalance: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = account.holderName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "Conta ${account.maskedNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
                    )
                }
                IconButton(onClick = onToggleBalance, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (isBalanceHidden) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isBalanceHidden) "Mostrar saldo" else "Ocultar saldo",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Saldo disponível",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
            )
            Text(
                text = account.balanceFormatted,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Limite: ${account.availableLimitFormatted}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.65f)
            )
        }
    }
}

@Preview
@Composable
private fun AccountCardPreview() {
    BankTheme(config = BrandCatalog.bancoPrincipal()) {
        AccountCard(
            account = AccountDisplay(
                holderName = "Heitor Bastos",
                maskedNumber = "•••• 4521",
                balanceFormatted = "R$ 1.234,56",
                availableLimitFormatted = "R$ 5.000,00"
            ),
            isBalanceHidden = false,
            onToggleBalance = {}
        )
    }
}
