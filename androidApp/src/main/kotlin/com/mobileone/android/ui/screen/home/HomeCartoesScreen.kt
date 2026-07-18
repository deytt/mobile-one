package com.mobileone.android.ui.screen.home

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.mobileone.android.ui.screen.home.components.HomeGreetingHeader
import com.mobileone.android.ui.screen.home.components.HomeTabBar
import com.mobileone.android.ui.theme.BankTheme
import com.mobileone.android.ui.theme.LocalWhiteLabelConfig
import com.mobileone.shared.config.BrandCatalog
import com.mobileone.shared.config.WhiteLabelConfig

/**
 * Home de Cartões (SPEC-009): fatura, limite, compras e Bottom Tab Bar.
 */
@Composable
fun HomeCartoesScreen(
    onNavigateToConta: () -> Unit,
    onBrandSwitcherClick: () -> Unit,
    userName: String = "Heitor Bastos"
) {
    HomeCartoesContent(
        config = LocalWhiteLabelConfig.current,
        userName = userName,
        onTabChange = { tab ->
            if (tab == HomeTab.CONTA) onNavigateToConta()
        },
        onBrandSwitcherClick = onBrandSwitcherClick
    )
}

@Composable
fun HomeCartoesContent(
    config: WhiteLabelConfig,
    userName: String,
    onTabChange: (HomeTab) -> Unit,
    onBrandSwitcherClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val radius = config.theme.borderRadiusDp.dp
    val shape = RoundedCornerShape(radius)

    HomePrimaryStatusBar()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            HomeTabBar(
                currentTab = HomeTab.CARTOES,
                onTabChange = onTabChange,
                onBrandSwitcher = onBrandSwitcherClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .statusBarsPadding()
            ) {
                HomeGreetingHeader(userName = userName)
                InvoiceCard(shape = shape)
                Spacer(modifier = Modifier.height(20.dp))
            }

            ActionButtonsRow(shape = shape)

            LimitSection(shape = shape)

            PurchasesSection(shape = shape)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InvoiceCard(shape: RoundedCornerShape) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(shape)
            .background(Color.White.copy(alpha = 0.10f))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Fatura aberta",
                    color = Color.White.copy(alpha = 0.70f),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        letterSpacing = 0.3.sp
                    )
                )
                Text(
                    text = "R$ 487,40",
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.56).sp,
                        lineHeight = 39.sp
                    )
                )
                Text(
                    text = buildAnnotatedString {
                        append("Vencimento ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                            append("25 JUL")
                        }
                    },
                    modifier = Modifier.padding(top = 4.dp),
                    color = Color.White.copy(alpha = 0.80f),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
                )
                Text(
                    text = buildAnnotatedString {
                        append("Melhor dia de compra ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                            append("20 JUL")
                        }
                    },
                    modifier = Modifier.padding(top = 2.dp),
                    color = Color.White.copy(alpha = 0.80f),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.60f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun ActionButtonsRow(shape: RoundedCornerShape) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = {},
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Payments,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Pagar fatura",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        OutlinedButton(
            onClick = {},
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
            shape = shape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.CreditCard,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Meus cartões",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
private fun LimitSection(shape: RoundedCornerShape) {
    val used = 750f
    val total = 2750f
    val progress = used / total

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
        shape = shape,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Meu limite",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(16.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Utilizado",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                    )
                    Text(
                        text = "R$ 750,00",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Disponível",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                    )
                    Text(
                        text = "R$ 2.000,00",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.20f),
                strokeCap = StrokeCap.Round
            )

            Text(
                text = "Limite total: R$ 2.750,00",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        }
    }
}

private data class PurchaseItemData(
    val name: String,
    val subtitle: String,
    val amount: String,
    val icon: ImageVector,
    val badge: String? = null
)

@Composable
private fun PurchasesSection(shape: RoundedCornerShape) {
    val purchases = listOf(
        PurchaseItemData("Amazon", "15 Jul · Compras online", "R$ 189,90", Icons.Default.ShoppingBag, "1/3"),
        PurchaseItemData("iFood", "14 Jul · Alimentação", "R$ 67,50", Icons.Default.Restaurant),
        PurchaseItemData("Posto Shell", "13 Jul · Transporte", "R$ 150,00", Icons.Default.LocalGasStation),
        PurchaseItemData("Cinemark", "12 Jul · Entretenimento", "R$ 80,00", Icons.Default.Movie)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Minhas compras",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = {}) {
                Text(
                    text = "Ver todas",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp)
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            shape = shape,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column {
                purchases.forEachIndexed { index, item ->
                    PurchaseRow(item = item)
                    if (index < purchases.lastIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.background)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PurchaseRow(item: PurchaseItemData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = item.name,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = item.subtitle,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
            )
            item.badge?.let { badge ->
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(horizontal = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badge,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 12.sp
                        )
                    )
                }
            }
        }

        Text(
            text = item.amount,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

/** Status bar com ícones claros sobre `colorPrimary` (SPEC-009). */
@Composable
private fun HomePrimaryStatusBar() {
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
private fun HomeCartoesPreview() {
    val config = BrandCatalog.bancoPrincipal()
    BankTheme(config = config) {
        HomeCartoesContent(
            config = config,
            userName = "Heitor Bastos",
            onTabChange = {},
            onBrandSwitcherClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeCartoesFintechPreview() {
    val config = BrandCatalog.fintechVerde()
    BankTheme(config = config) {
        HomeCartoesContent(
            config = config,
            userName = "Heitor Bastos",
            onTabChange = {},
            onBrandSwitcherClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeCartoesPremiumPreview() {
    val config = BrandCatalog.bancoPremium()
    BankTheme(config = config) {
        HomeCartoesContent(
            config = config,
            userName = "Heitor Bastos",
            onTabChange = {},
            onBrandSwitcherClick = {}
        )
    }
}
