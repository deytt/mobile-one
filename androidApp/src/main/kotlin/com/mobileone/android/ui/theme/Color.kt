package com.mobileone.android.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.mobileone.shared.config.ThemeTokens

/**
 * Mapeia os 8 tokens de cor de [ThemeTokens] (SPEC-005) para um [ColorScheme] Material 3.
 *
 * - `colorOnSurface` → textos secundários (`onSurface`)
 * - `colorBackground` → `background`
 * - `colorSurface` → `surface`
 *
 * Tokens auxiliares (`onSecondary`, `onError`, containers) usam defaults derivados até o
 * design system trazer valores explícitos.
 */
fun ThemeTokens.toColorScheme(): ColorScheme {
    val primary = colorPrimary.toComposeColor()
    val primaryVariant = colorPrimaryVariant.toComposeColor()
    val secondary = colorSecondary.toComposeColor()
    val background = colorBackground.toComposeColor()
    val surface = colorSurface.toComposeColor()
    val error = colorError.toComposeColor()
    val onPrimary = colorOnPrimary.toComposeColor()
    val onBackground = colorOnBackground.toComposeColor()
    val onSurface = colorOnSurface.toComposeColor()

    return lightColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryVariant,
        onPrimaryContainer = onPrimary,
        secondary = secondary,
        onSecondary = onPrimary,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        error = error,
        onError = onPrimary
    )
}

internal fun String.toComposeColor(): Color = Color(android.graphics.Color.parseColor(this))
