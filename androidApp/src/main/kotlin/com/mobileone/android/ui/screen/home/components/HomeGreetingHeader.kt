package com.mobileone.android.ui.screen.home.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobileone.android.ui.component.brandInitials
import com.mobileone.android.ui.theme.BankTheme
import com.mobileone.shared.config.BrandCatalog

/**
 * Header da Home (SPEC-009): avatar + saudação + sino, sobre `colorPrimary`.
 */
@Composable
fun HomeGreetingHeader(
    userName: String,
    onNotificationsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val firstName = userName.trim().split(" ").firstOrNull().orEmpty()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .size(40.dp)
                .border(2.dp, Color.White, CircleShape),
            color = Color.White,
            shape = CircleShape
        ) {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = brandInitials(userName),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Text(
            text = "Olá, $firstName",
            modifier = Modifier.padding(start = 12.dp),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = onNotificationsClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notificações",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF003B6F)
@Composable
private fun HomeGreetingHeaderPreview() {
    BankTheme(config = BrandCatalog.bancoPrincipal()) {
        HomeGreetingHeader(userName = "Heitor Bastos")
    }
}
