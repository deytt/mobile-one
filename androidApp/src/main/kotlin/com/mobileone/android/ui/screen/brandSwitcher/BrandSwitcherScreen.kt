package com.mobileone.android.ui.screen.brandSwitcher

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobileone.android.ui.theme.BankTheme
import com.mobileone.android.viewmodel.BrandSwitcherUiState
import com.mobileone.android.viewmodel.BrandSwitcherViewModel
import com.mobileone.shared.config.BrandCatalog
import com.mobileone.shared.config.WhiteLabelConfig
import org.koin.androidx.compose.koinViewModel

/**
 * Tela Dev Mode de troca de marca (SPEC-004 / ponto de entrada SPEC-002).
 * Após aplicar a troca, sinaliza [onApplied] para que a navegação reinicie
 * a Home com o novo tema.
 */
@Composable
fun BrandSwitcherScreen(
    onBack: () -> Unit,
    onApplied: () -> Unit,
    viewModel: BrandSwitcherViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.applied) {
        if (uiState.applied) onApplied()
    }

    BrandSwitcherContent(
        uiState = uiState,
        onBack = onBack,
        onSelectBrand = viewModel::onSelectBrand,
        onApply = viewModel::onApply
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandSwitcherContent(
    uiState: BrandSwitcherUiState,
    onBack: () -> Unit,
    onSelectBrand: (String) -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dev Mode · Trocar Marca") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Selecione a configuração de marca para demonstrar o white-label ao vivo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(16.dp))

            uiState.brands.forEach { brand ->
                BrandOptionCard(
                    brand = brand,
                    isSelected = brand.brandId == uiState.selectedBrandId,
                    onSelect = { onSelectBrand(brand.brandId) },
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onApply,
                enabled = !uiState.isApplying,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isApplying) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                }
                Text("Aplicar")
            }
        }
    }
}

@Composable
private fun BrandOptionCard(
    brand: WhiteLabelConfig,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = runCatching {
        Color(android.graphics.Color.parseColor(brand.theme.colorPrimary))
    }.getOrElse { MaterialTheme.colorScheme.primary }

    val secondaryColor = runCatching {
        Color(android.graphics.Color.parseColor(brand.theme.colorSecondary))
    }.getOrElse { MaterialTheme.colorScheme.secondary }

    Card(
        onClick = onSelect,
        modifier = modifier.fillMaxWidth(),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = isSelected, onClick = onSelect)
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(brand.brandName, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = "Raio: ${brand.theme.borderRadiusDp}dp · ${brand.theme.fontFamilyName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            // Preview de cores
            Row {
                ColorDot(primaryColor)
                Spacer(Modifier.width(4.dp))
                ColorDot(secondaryColor)
            }
        }
    }
}

@Composable
private fun ColorDot(color: Color) {
    androidx.compose.foundation.Canvas(modifier = Modifier.size(18.dp)) {
        drawCircle(color = color)
    }
}

@Preview(showBackground = true)
@Composable
private fun BrandSwitcherContentPreview() {
    BankTheme(config = BrandCatalog.bancoPrincipal()) {
        BrandSwitcherContent(
            uiState = BrandSwitcherUiState(selectedBrandId = "banco_principal"),
            onBack = {},
            onSelectBrand = {},
            onApply = {}
        )
    }
}
