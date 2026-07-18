package com.mobileone.android.ui.theme

/**
 * Camada de mapeamento white-label (SPEC-005): converte [com.mobileone.shared.config.ThemeTokens]
 * agnósticos de plataforma nos tipos Compose.
 *
 * Implementação por arquivo:
 * - [toColorScheme] → [Color.kt]
 * - [toShapes] → [Shape.kt]
 * - [toTypography] → [Typography.kt]
 *
 * Consumido por [BankTheme]; a UI nunca lê hex/`borderRadiusDp` diretamente.
 */
