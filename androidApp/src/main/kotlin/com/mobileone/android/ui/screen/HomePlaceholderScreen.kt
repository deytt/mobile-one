package com.mobileone.android.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobileone.android.ui.theme.BankTheme
import com.mobileone.android.ui.theme.LocalWhiteLabelConfig
import com.mobileone.shared.config.BrandCatalog

/**
 * Destino final do fluxo de login (SPEC-001). A Home real chega na SPEC-002 — aqui apenas
 * confirmamos que a sessão foi estabelecida com sucesso.
 */
@Composable
fun HomePlaceholderScreen(onLogoutClick: () -> Unit) {
    val config = LocalWhiteLabelConfig.current
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bem-vindo, ${config.brandName}!",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "Login realizado com sucesso. A Home completa chega na SPEC-002.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            TextButton(modifier = Modifier.padding(top = 24.dp), onClick = onLogoutClick) {
                Text("Sair")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomePlaceholderScreenPreview() {
    BankTheme(config = BrandCatalog.bancoPrincipal()) {
        HomePlaceholderScreen(onLogoutClick = {})
    }
}
