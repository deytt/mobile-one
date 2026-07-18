package com.mobileone.android.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobileone.android.ui.theme.BankTheme
import com.mobileone.shared.config.BrandCatalog
import com.mobileone.shared.config.WhiteLabelConfig

/**
 * Identidade visual da marca (SPEC-005 / SPEC-006).
 *
 * - Banco Principal → rounded rect (`borderRadiusDp`, tipicamente 12dp), fundo 18% branco
 * - Fintech Verde → círculo, fundo 18% branco
 * - Banco Premium → losango 45°, radius 4dp, fundo 15% branco
 */
@Composable
fun BrandLogo(
    config: WhiteLabelConfig,
    size: Dp = 64.dp,
    modifier: Modifier = Modifier
) {
    val initials = brandInitials(config.brandName)
    val isPremium = config.brandId == "banco_premium"
    val isFintech = config.brandId == "fintech_verde"
    val initialsSize = (size.value * 24f / 64f).sp

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        when {
            isPremium -> {
                val diamondSize = size * (51f / 64f)
                Box(
                    modifier = Modifier
                        .size(diamondSize)
                        .graphicsLayer { rotationZ = 45f }
                        .background(
                            Color.White.copy(alpha = 0.15f),
                            RoundedCornerShape(4.dp)
                        )
                )
            }
            isFintech -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.18f), CircleShape)
                )
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color.White.copy(alpha = 0.18f),
                            RoundedCornerShape(config.theme.borderRadiusDp.dp)
                        )
                )
            }
        }
        Text(
            text = initials,
            color = Color.White,
            fontSize = initialsSize,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = if (isFintech) FontWeight.SemiBold else FontWeight.Bold,
                fontStyle = if (isPremium) FontStyle.Italic else FontStyle.Normal,
                letterSpacing = when {
                    isFintech -> (-0.24).sp
                    isPremium -> 0.sp
                    else -> (-0.48).sp
                }
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF003B6F)
@Composable
private fun BrandLogoBancoPrincipalPreview() {
    BankTheme(config = BrandCatalog.bancoPrincipal()) {
        BrandLogo(config = BrandCatalog.bancoPrincipal())
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00A86B)
@Composable
private fun BrandLogoFintechPreview() {
    BankTheme(config = BrandCatalog.fintechVerde()) {
        BrandLogo(config = BrandCatalog.fintechVerde())
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF7B2D00)
@Composable
private fun BrandLogoPremiumPreview() {
    BankTheme(config = BrandCatalog.bancoPremium()) {
        BrandLogo(config = BrandCatalog.bancoPremium())
    }
}
