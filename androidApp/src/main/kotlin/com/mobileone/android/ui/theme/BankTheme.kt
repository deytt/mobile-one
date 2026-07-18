package com.mobileone.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.mobileone.shared.config.WhiteLabelConfig

/**
 * Ponto único de aplicação do tema white-label (SPEC-004) na UI Android. Fornece dois
 * contratos para os composables descendentes:
 * - `MaterialTheme.colorScheme` / `MaterialTheme.shapes` — cores e formas já resolvidas
 *   (nunca ler `config.theme.colorPrimary` diretamente na UI).
 * - [LocalWhiteLabelConfig] — para ler `brandName`, `logoAsset`, `features` (feature flags),
 *   etc.
 */
@Composable
fun BankTheme(
    config: WhiteLabelConfig = LocalWhiteLabelConfig.current,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalWhiteLabelConfig provides config) {
        MaterialTheme(
            colorScheme = config.theme.toColorScheme(),
            shapes = config.theme.toShapes(),
            content = content
        )
    }
}
