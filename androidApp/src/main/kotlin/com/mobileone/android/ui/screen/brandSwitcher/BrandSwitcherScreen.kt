package com.mobileone.android.ui.screen.brandSwitcher

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobileone.android.R
import com.mobileone.android.ui.theme.BankTheme
import com.mobileone.android.ui.theme.fontFamilyFor
import com.mobileone.android.ui.theme.toComposeColor
import com.mobileone.android.viewmodel.BrandSwitcherUiState
import com.mobileone.android.viewmodel.BrandSwitcherViewModel
import com.mobileone.shared.config.BrandCatalog
import com.mobileone.shared.config.WhiteLabelConfig
import org.koin.androidx.compose.koinViewModel

private val HeaderBg = Color(0xFF1A1A2E)
private val BodyBg = Color(0xFFF4F4F6)
private val DevModeOrange = Color(0xFFF7941D)
private val SectionLabelColor = Color(0xFF6B7280)
private val BrandNameColor = Color(0xFF1A1A2E)
private val FooterNoteColor = Color(0xFF9CA3AF)
private val RadioUnselectedBorder = Color(0xFFD1D5DB)
private val InterFont = fontFamilyFor("Inter")
private val CardShape = RoundedCornerShape(12.dp)

/**
 * Brand Switcher (SPEC-011 layout / SPEC-004 comportamento).
 * Após aplicar a troca, sinaliza [onApplied] para que a navegação reinicie a Home com o novo tema.
 */
@Composable
fun BrandSwitcherScreen(
    onBack: () -> Unit,
    onApplied: () -> Unit,
    viewModel: BrandSwitcherViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler(onBack = onBack)

    LaunchedEffect(uiState.applied) {
        if (uiState.applied) onApplied()
    }

    BrandSwitcherContent(
        uiState = uiState,
        onSelectBrand = viewModel::onSelectBrand,
        onApply = viewModel::onApply
    )
}

@Composable
fun BrandSwitcherContent(
    uiState: BrandSwitcherUiState,
    onSelectBrand: (String) -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedBrand = uiState.brands.firstOrNull { it.brandId == uiState.selectedBrandId }
        ?: uiState.brands.firstOrNull()
        ?: BrandCatalog.bancoPrincipal()
    val selectedPrimary = selectedBrand.theme.colorPrimary.toComposeColor()

    BrandSwitcherStatusBar()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BodyBg)
    ) {
        BrandSwitcherHeader()

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 20.dp)
        ) {
            Text(
                text = "Selecionar marca".uppercase(),
                style = TextStyle(
                    fontFamily = InterFont,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SectionLabelColor,
                    letterSpacing = 0.77.sp,
                    lineHeight = 16.5.sp
                )
            )
            Spacer(Modifier.height(12.dp))

            uiState.brands.forEach { brand ->
                BrandOptionCard(
                    brand = brand,
                    isSelected = brand.brandId == uiState.selectedBrandId,
                    onSelect = { onSelectBrand(brand.brandId) }
                )
                Spacer(Modifier.height(12.dp))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = 32.dp)
        ) {
            Button(
                onClick = onApply,
                enabled = !uiState.isApplying,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = selectedPrimary,
                    contentColor = Color.White,
                    disabledContainerColor = selectedPrimary.copy(alpha = 0.7f),
                    disabledContentColor = Color.White
                )
            ) {
                if (uiState.isApplying) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = "Aplicar marca",
                    style = TextStyle(
                        fontFamily = InterFont,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.5.sp
                    )
                )
            }

            Text(
                text = "As mudanças são aplicadas instantaneamente em todo o app",
                style = TextStyle(
                    fontFamily = InterFont,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = FooterNoteColor,
                    lineHeight = 16.5.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
        }
    }
}

@Composable
private fun BrandSwitcherHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(HeaderBg)
            .statusBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Brand Switcher",
                style = TextStyle(
                    fontFamily = InterFont,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 27.sp,
                    letterSpacing = (-0.36).sp
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Modo de demonstração",
                style = TextStyle(
                    fontFamily = InterFont,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.45f),
                    lineHeight = 18.sp
                )
            )
        }

        Text(
            text = "DEV MODE",
            style = TextStyle(
                fontFamily = InterFont,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 0.6.sp,
                lineHeight = 15.sp
            ),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clip(RoundedCornerShape(4.dp))
                .background(DevModeOrange)
                .padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun BrandOptionCard(
    brand: WhiteLabelConfig,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = brand.theme.colorPrimary.toComposeColor()
    val secondaryColor = brand.theme.colorSecondary.toComposeColor()
    val brandFont = fontFamilyFor(brand.theme.fontFamilyName)
    val avatarRadius = brand.theme.borderRadiusDp.dp

    val cardModifier = if (isSelected) {
        modifier
            .fillMaxWidth()
            .shadow(
                elevation = 0.dp,
                shape = CardShape,
                ambientColor = primaryColor.copy(alpha = 0.13f),
                spotColor = primaryColor.copy(alpha = 0.13f)
            )
            .clip(CardShape)
            .background(Color.White)
            .border(BorderStroke(2.dp, primaryColor), CardShape)
            .clickable(onClick = onSelect)
            .padding(16.dp)
    } else {
        modifier
            .fillMaxWidth()
            .shadow(
                elevation = 1.5.dp,
                shape = CardShape,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clip(CardShape)
            .background(Color.White)
            .border(BorderStroke(2.dp, Color.Transparent), CardShape)
            .clickable(onClick = onSelect)
            .padding(16.dp)
    }

    Row(
        modifier = cardModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BrandRadio(selected = isSelected, color = primaryColor)
        Spacer(Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(avatarRadius))
                .background(primaryColor)
        )
        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = brand.brandName,
                style = TextStyle(
                    fontFamily = brandFont,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNameColor,
                    lineHeight = 21.sp
                )
            )
            Text(
                text = "${brand.theme.fontFamilyName} · ${brand.theme.borderRadiusDp}px radius",
                style = TextStyle(
                    fontFamily = InterFont,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = SectionLabelColor,
                    lineHeight = 16.5.sp
                ),
                modifier = Modifier.padding(top = 2.dp)
            )
            Row(
                modifier = Modifier.padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ColorPill(hex = brand.theme.colorPrimary, color = primaryColor)
                ColorPill(hex = brand.theme.colorSecondary, color = secondaryColor)
            }
        }

        if (isSelected) {
            Icon(
                painter = painterResource(R.drawable.ic_brand_check),
                contentDescription = null,
                tint = primaryColor,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun BrandRadio(
    selected: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(20.dp)
            .border(
                width = 2.dp,
                color = if (selected) color else RadioUnselectedBorder,
                shape = CircleShape
            )
            .background(
                color = if (selected) color else Color.Transparent,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}

@Composable
private fun ColorPill(
    hex: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(19.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(color.copy(alpha = 0.09f))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = hex.uppercase(),
            style = TextStyle(
                fontFamily = InterFont,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = color,
                lineHeight = 15.sp
            )
        )
    }
}

/** Status bar `#1A1A2E` com ícones brancos (SPEC-011). */
@Composable
private fun BrandSwitcherStatusBar() {
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

@Preview(showBackground = true, heightDp = 780)
@Composable
private fun BrandSwitcherContentPreview() {
    BankTheme(config = BrandCatalog.bancoPrincipal()) {
        BrandSwitcherContent(
            uiState = BrandSwitcherUiState(selectedBrandId = "banco_principal"),
            onSelectBrand = {},
            onApply = {}
        )
    }
}
