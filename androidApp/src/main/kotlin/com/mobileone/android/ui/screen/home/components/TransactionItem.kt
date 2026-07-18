package com.mobileone.android.ui.screen.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Pix
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobileone.android.ui.theme.BankTheme
import com.mobileone.shared.config.BrandCatalog
import com.mobileone.shared.domain.entity.TransactionCategory
import com.mobileone.shared.feature.home.TransactionDisplay

@Composable
fun TransactionItem(transaction: TransactionDisplay, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = transaction.category.toIcon(),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = transaction.dateFormatted,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = transaction.amountFormatted,
            style = MaterialTheme.typography.bodyMedium,
            color = if (transaction.isDebit) {
                MaterialTheme.colorScheme.error
            } else {
                Color(0xFF16A34A)
            }
        )
    }
}

@Composable
fun TransactionDateHeader(date: String, modifier: Modifier = Modifier) {
    Text(
        text = date,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    )
}

private fun TransactionCategory.toIcon(): ImageVector = when (this) {
    TransactionCategory.PIX -> Icons.Default.Pix
    TransactionCategory.TED -> Icons.Default.Sync
    TransactionCategory.BOLETO -> Icons.Default.Receipt
    TransactionCategory.CARD -> Icons.Default.CreditCard
    TransactionCategory.PURCHASE -> Icons.Default.ShoppingBag
    TransactionCategory.DEPOSIT -> Icons.Default.ArrowDownward
    TransactionCategory.FEE -> Icons.Default.ArrowUpward
    TransactionCategory.OTHER -> Icons.Default.AttachMoney
}

@Preview(showBackground = true)
@Composable
private fun TransactionItemPreview() {
    BankTheme(config = BrandCatalog.bancoPrincipal()) {
        Surface {
            TransactionItem(
                transaction = TransactionDisplay(
                    id = "1",
                    description = "PIX enviado - João",
                    amountFormatted = "- R$ 150,00",
                    isDebit = true,
                    dateFormatted = "Hoje",
                    category = TransactionCategory.PIX
                )
            )
        }
    }
}
