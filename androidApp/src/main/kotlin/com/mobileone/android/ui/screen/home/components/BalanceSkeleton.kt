package com.mobileone.android.ui.screen.home.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobileone.android.ui.theme.BankTheme
import com.mobileone.shared.config.BrandCatalog

@Composable
fun BalanceSkeleton(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "shimmer"
    )
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            SkeletonLine(width = 100, height = 14, alpha = alpha)
            Spacer(Modifier.height(12.dp))
            SkeletonLine(width = 180, height = 28, alpha = alpha)
            Spacer(Modifier.height(8.dp))
            SkeletonLine(width = 120, height = 12, alpha = alpha)
        }
    }
}

@Composable
private fun SkeletonLine(width: Int, height: Int, alpha: Float) {
    Box(
        modifier = Modifier
            .width(width.dp)
            .height(height.dp)
            .alpha(alpha)
            .background(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                shape = MaterialTheme.shapes.small
            )
    )
}

@Preview
@Composable
private fun BalanceSkeletonPreview() {
    BankTheme(config = BrandCatalog.bancoPrincipal()) {
        BalanceSkeleton(Modifier.padding(16.dp))
    }
}
