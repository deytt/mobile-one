package com.mobileone.android.ui.screen.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Pix
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobileone.android.ui.theme.BankTheme
import com.mobileone.shared.config.BrandCatalog
import com.mobileone.shared.config.FeatureFlags

@Composable
fun QuickActionsRow(features: FeatureFlags, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (features.pixEnabled) {
            QuickActionButton(
                label = "PIX",
                icon = { Icon(Icons.Default.Pix, contentDescription = "PIX", modifier = Modifier.size(22.dp)) },
                modifier = Modifier.weight(1f)
            )
        }
        QuickActionButton(
            label = "Transferir",
            icon = { Icon(Icons.AutoMirrored.Filled.CompareArrows, contentDescription = "Transferir", modifier = Modifier.size(22.dp)) },
            modifier = Modifier.weight(1f)
        )
        if (features.creditCardEnabled) {
            QuickActionButton(
                label = "Pagar",
                icon = { Icon(Icons.Default.CreditCard, contentDescription = "Pagar", modifier = Modifier.size(22.dp)) },
                modifier = Modifier.weight(1f)
            )
        }
        QuickActionButton(
            label = "Recarga",
            icon = { Icon(Icons.Default.PhoneAndroid, contentDescription = "Recarga", modifier = Modifier.size(22.dp)) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionButton(label: String, icon: @Composable () -> Unit, modifier: Modifier = Modifier) {
    FilledTonalButton(onClick = {}, modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            icon()
            Text(text = label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Preview
@Composable
private fun QuickActionsRowPreview() {
    BankTheme(config = BrandCatalog.bancoPrincipal()) {
        QuickActionsRow(features = FeatureFlags())
    }
}
