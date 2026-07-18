package com.mobileone.android.ui.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobileone.android.ui.theme.BankTheme
import com.mobileone.shared.config.BrandCatalog

@Composable
fun StaleBanner(lastUpdatedAt: Long, modifier: Modifier = Modifier) {
    val hour = (lastUpdatedAt / 3_600_000L) % 24
    val min = (lastUpdatedAt / 60_000L) % 60
    val time = "%02d:%02d".format(hour, min)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Dados desatualizados · Última atualização: $time",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Preview
@Composable
private fun StaleBannerPreview() {
    BankTheme(config = BrandCatalog.bancoPrincipal()) {
        StaleBanner(lastUpdatedAt = 1_700_000_000_000L)
    }
}
