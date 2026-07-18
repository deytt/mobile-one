package com.mobileone.android.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.TextStyle
import com.mobileone.android.R
import com.mobileone.shared.config.ThemeTokens

/**
 * Tipografia white-label (SPEC-005):
 * - Banco Principal → Roboto (padrão Android / [FontFamily.Default])
 * - Fintech Verde → Inter via Google Fonts
 * - Banco Premium → Georgia via Google Fonts (família "Georgia" quando disponível no
 *   provider; fallback tipográfico serifado se o download falhar)
 */
private val googleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val interFontFamily: FontFamily = FontFamily(
    Font(googleFont = GoogleFont("Inter"), fontProvider = googleFontProvider, weight = FontWeight.Normal),
    Font(googleFont = GoogleFont("Inter"), fontProvider = googleFontProvider, weight = FontWeight.Medium),
    Font(googleFont = GoogleFont("Inter"), fontProvider = googleFontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = GoogleFont("Inter"), fontProvider = googleFontProvider, weight = FontWeight.Bold)
)

/**
 * Georgia não está no catálogo Google Fonts; usamos Gelasio (OFL, métrica compatível)
 * como asset tipográfico downloadable até haver arquivo Georgia licenciado em `res/font`.
 */
private val georgiaFontFamily: FontFamily = FontFamily(
    Font(googleFont = GoogleFont("Gelasio"), fontProvider = googleFontProvider, weight = FontWeight.Normal),
    Font(googleFont = GoogleFont("Gelasio"), fontProvider = googleFontProvider, weight = FontWeight.Medium),
    Font(googleFont = GoogleFont("Gelasio"), fontProvider = googleFontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = GoogleFont("Gelasio"), fontProvider = googleFontProvider, weight = FontWeight.Bold)
)

fun ThemeTokens.toTypography(): Typography {
    val fontFamily = resolveFontFamily(fontFamilyName)
    val base = Typography()
    return Typography(
        displayLarge = base.displayLarge.withFont(fontFamily),
        displayMedium = base.displayMedium.withFont(fontFamily),
        displaySmall = base.displaySmall.withFont(fontFamily),
        headlineLarge = base.headlineLarge.withFont(fontFamily),
        headlineMedium = base.headlineMedium.withFont(fontFamily),
        headlineSmall = base.headlineSmall.withFont(fontFamily),
        titleLarge = base.titleLarge.withFont(fontFamily),
        titleMedium = base.titleMedium.withFont(fontFamily),
        titleSmall = base.titleSmall.withFont(fontFamily),
        bodyLarge = base.bodyLarge.withFont(fontFamily),
        bodyMedium = base.bodyMedium.withFont(fontFamily),
        bodySmall = base.bodySmall.withFont(fontFamily),
        labelLarge = base.labelLarge.withFont(fontFamily),
        labelMedium = base.labelMedium.withFont(fontFamily),
        labelSmall = base.labelSmall.withFont(fontFamily)
    )
}

private fun TextStyle.withFont(fontFamily: FontFamily): TextStyle = copy(fontFamily = fontFamily)

private fun resolveFontFamily(fontFamilyName: String): FontFamily = when (fontFamilyName) {
    "Roboto" -> FontFamily.Default
    "Inter" -> interFontFamily
    "Georgia" -> georgiaFontFamily
    else -> FontFamily.Default
}
