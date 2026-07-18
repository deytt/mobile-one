package com.mobileone.android.ui.screen.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobileone.android.ui.screen.home.HomeTab
import com.mobileone.android.ui.theme.BankTheme
import com.mobileone.shared.config.BrandCatalog

/**
 * Bottom bar da Home (SPEC-009): pílula Cartões/Conta + botão grade (Brand Switcher).
 */
@Composable
fun HomeTabBar(
    currentTab: HomeTab,
    onTabChange: (HomeTab) -> Unit,
    onBrandSwitcher: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabSwitcher(
                activeTab = currentTab,
                onTabChange = onTabChange,
                modifier = Modifier.weight(1f, fill = false)
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(16.dp))

            BrandSwitcherButton(onClick = onBrandSwitcher)
        }
    }
}

@Composable
private fun TabSwitcher(
    activeTab: HomeTab,
    onTabChange: (HomeTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HomeTab.entries.forEach { tab ->
            val selected = tab == activeTab
            val bg by animateColorAsState(
                targetValue = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                animationSpec = tween(durationMillis = 200),
                label = "tabBg"
            )
            val fg by animateColorAsState(
                targetValue = if (selected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                animationSpec = tween(durationMillis = 200),
                label = "tabFg"
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(bg)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onTabChange(tab) }
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab.title,
                    color = fg,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
private fun BrandSwitcherButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Apps,
            contentDescription = "Brand Switcher",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeTabBarCartoesPreview() {
    BankTheme(config = BrandCatalog.bancoPrincipal()) {
        HomeTabBar(
            currentTab = HomeTab.CARTOES,
            onTabChange = {},
            onBrandSwitcher = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeTabBarContaPreview() {
    BankTheme(config = BrandCatalog.fintechVerde()) {
        HomeTabBar(
            currentTab = HomeTab.CONTA,
            onTabChange = {},
            onBrandSwitcher = {}
        )
    }
}
