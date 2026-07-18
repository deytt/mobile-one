package com.mobileone.android.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import com.mobileone.shared.config.BrandCatalog
import com.mobileone.shared.config.WhiteLabelConfig

/**
 * Contrato de presentation para a SPEC-004: expõe o [WhiteLabelConfig] ativo para qualquer
 * composable — feature flags, `brandName`, `logoAsset`, etc. — sem que a UI precise conhecer
 * `AppStateRepository`/Koin diretamente. Cores e formas são consumidas via
 * `MaterialTheme.colorScheme`/`MaterialTheme.shapes`/`MaterialTheme.typography` (ver
 * [BankTheme]), nunca lendo `theme.colorPrimary` (hex) diretamente na UI.
 *
 * O valor default (`BrandCatalog.bancoPrincipal()`) só é usado em `@Preview` ou caso algum
 * composable seja renderizado fora de [BankTheme] por engano — em produção o valor real é
 * sempre fornecido por [BankTheme] a partir do `AppStateRepository`.
 */
val LocalWhiteLabelConfig = staticCompositionLocalOf { BrandCatalog.bancoPrincipal() }
