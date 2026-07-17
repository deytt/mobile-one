package com.mobileone.android.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Tela placeholder da Fundação KMP — apenas prova que o androidApp consome o `shared` via
 * `Platform().name`. As telas reais (Login, Home, PIX, ...) chegam a partir do Figma por spec.
 */
@Composable
fun HelloScreen(platformName: String) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hello, mobile-one!",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = platformName,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HelloScreenPreview() {
    MaterialTheme {
        HelloScreen(platformName = "Android 36")
    }
}
