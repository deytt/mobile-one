package com.mobileone.android.ui.screen.auth

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.mobileone.android.ui.component.BrandLogo
import com.mobileone.android.ui.theme.BankTheme
import com.mobileone.android.ui.theme.LocalWhiteLabelConfig
import com.mobileone.android.ui.theme.brandRadialGradient
import com.mobileone.shared.config.BrandCatalog
import com.mobileone.shared.config.WhiteLabelConfig
import kotlinx.coroutines.delay
import kotlin.math.max

/**
 * Splash (SPEC-006): marca centralizada sobre gradiente radial, page dots e texto
 * regulatório no rodapé. Decide o próximo destino após [minDurationMillis] (SPEC-001).
 */
@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    minDurationMillis: Long = 900L
) {
    LaunchedEffect(Unit) {
        delay(minDurationMillis)
        onFinished()
    }
    SplashContent(config = LocalWhiteLabelConfig.current)
}

@Composable
fun SplashContent(config: WhiteLabelConfig) {
    SplashLightStatusBar()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()
        val center = Offset(widthPx / 2f, heightPx * 0.4f)
        val radius = max(widthPx, heightPx)
        val brush = brandRadialGradient(config = config, center = center, radius = radius)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BrandLogo(config = config, size = 64.dp)
                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text = config.brandName,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        style = splashBrandNameStyle(config)
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .padding(bottom = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SplashPageDots()
                    Text(
                        text = "Seguro e regulado pelo Banco Central do Brasil",
                        color = Color.White.copy(alpha = 0.45f),
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun splashBrandNameStyle(config: WhiteLabelConfig) =
    MaterialTheme.typography.titleLarge.copy(
        fontSize = 22.sp,
        fontWeight = if (config.brandId == "banco_premium") FontWeight.Bold else FontWeight.SemiBold,
        fontStyle = if (config.brandId == "banco_premium") FontStyle.Italic else FontStyle.Normal,
        letterSpacing = if (config.brandId == "banco_premium") 0.88.sp else (-0.22).sp
    )

/**
 * Três pontos estáticos (SPEC-006): tamanhos ~4.8 / 5.1 / 6.4dp e opacidades 31% / 40% / 76%
 * sobre `rgba(255,255,255,0.4)`.
 */
@Composable
private fun SplashPageDots() {
    val sizes = listOf(4.8.dp, 5.1.dp, 6.4.dp)
    val opacities = listOf(0.31f, 0.40f, 0.76f)
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        sizes.forEachIndexed { index, size ->
            Box(
                modifier = Modifier
                    .size(size)
                    .graphicsLayer { alpha = opacities[index] }
                    .background(Color.White.copy(alpha = 0.4f), CircleShape)
            )
        }
    }
}

/** Status bar transparente com ícones claros (SPEC-006). */
@Composable
private fun SplashLightStatusBar() {
    val view = LocalView.current
    DisposableEffect(Unit) {
        val window = (view.context as? Activity)?.window ?: return@DisposableEffect onDispose { }
        val controller = WindowCompat.getInsetsController(window, view)
        val previousLight = controller.isAppearanceLightStatusBars
        controller.isAppearanceLightStatusBars = false
        onDispose {
            controller.isAppearanceLightStatusBars = previousLight
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    BankTheme(config = BrandCatalog.bancoPrincipal()) {
        SplashContent(config = BrandCatalog.bancoPrincipal())
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenFintechPreview() {
    BankTheme(config = BrandCatalog.fintechVerde()) {
        SplashContent(config = BrandCatalog.fintechVerde())
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPremiumPreview() {
    BankTheme(config = BrandCatalog.bancoPremium()) {
        SplashContent(config = BrandCatalog.bancoPremium())
    }
}
