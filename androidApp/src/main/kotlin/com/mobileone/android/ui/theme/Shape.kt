package com.mobileone.android.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp
import com.mobileone.shared.config.ThemeTokens

/**
 * Aplica [ThemeTokens.borderRadiusDp] da marca ativa nas três escalas de [Shapes]
 * (SPEC-005): small / medium / large usam o mesmo raio.
 *
 * - Banco Principal → 12dp
 * - Fintech Verde → 16dp
 * - Banco Premium → 4dp
 */
fun ThemeTokens.toShapes(): Shapes {
    val radius = RoundedCornerShape(borderRadiusDp.dp)
    return Shapes(
        small = radius,
        medium = radius,
        large = radius
    )
}
