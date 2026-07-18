package com.mobileone.android.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.mobileone.shared.config.WhiteLabelConfig

/**
 * Gradiente radial da Splash (SPEC-006 / Figma).
 * Centro horizontal; eixo vertical em ~40% da altura.
 */
fun brandRadialGradient(
    config: WhiteLabelConfig,
    center: Offset,
    radius: Float
): Brush = Brush.radialGradient(
    colorStops = splashGradientStops(config),
    center = center,
    radius = radius
)

private fun splashGradientStops(config: WhiteLabelConfig): Array<Pair<Float, Color>> =
    when (config.brandId) {
        "fintech_verde" -> arrayOf(
            0.2f to Color(0xFF00A86B),
            0.6f to Color(0xFF008A58),
            1.0f to Color(0xFF006B44)
        )
        "banco_premium" -> arrayOf(
            0.2f to Color(0xFF7B2D00),
            1.0f to Color(0xFF4A1A00)
        )
        else -> arrayOf(
            0.2f to Color(0xFF003B6F),
            0.6f to Color(0xFF002D57),
            1.0f to Color(0xFF001F3F)
        )
    }
