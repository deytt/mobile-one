package com.mobileone.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.mobileone.shared.config.WhiteLabelConfig

/**
 * Ponto único de aplicação do tema white-label (SPEC-004 / SPEC-005) na UI Android.
 * Fornece aos composables descendentes:
 * - `MaterialTheme.colorScheme` / `shapes` / `typography` — tokens já resolvidos da marca
 *   ativa (nunca ler `config.theme.colorPrimary` diretamente na UI).
 * - [LocalWhiteLabelConfig] — para `brandName`, `logoAsset`, `features`, etc.
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
            typography = config.theme.toTypography(),
            content = content
        )
    }
}
