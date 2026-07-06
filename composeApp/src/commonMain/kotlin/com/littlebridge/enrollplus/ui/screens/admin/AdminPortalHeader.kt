package com.littlebridge.enrollplus.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminColors
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminTypography
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminShapes

// ═══════════════════════════════════════════════════════════════
// PortalHeader — 317×53dp, space-between, padding:8/24/16
// Left: school name (11sp w700) + dashboard title (17sp w800) + chevron
// Right: bell icon button (32×32) + badge
// ═══════════════════════════════════════════════════════════════

@Composable
fun PortalHeader(
    schoolName: String,
    dashboardTitle: String,
    badgeCount: Int,
    onBellClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(53.dp)
            .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: school + title
        Column {
            Text(
                text = schoolName,
                color = AdminColors.inkSecondary,
                style = AdminTypography.heroLabel
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = dashboardTitle,
                    color = AdminColors.inkPrimary,
                    style = AdminTypography.headerName
                )
                // Chevron down — simple text char
                Text(
                    text = "▾",
                    color = AdminColors.inkSecondary,
                    fontSize = 10.sp
                )
            }
        }

        // Right: bell icon + badge
        HeaderIconButton(
            badgeCount = badgeCount,
            onClick = onBellClick
        )
    }
}

@Composable
fun HeaderIconButton(
    badgeCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(38.dp)
            .shadow(1.dp, RoundedCornerShape(50), ambientColor = Color(0x0A1A1614), spotColor = Color(0x0F1A1614))
            .background(AdminColors.cardWhite, RoundedCornerShape(50))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Bell icon (text placeholder for SVG)
        Text(
            text = "🔔",
            fontSize = 18.sp
        )
        if (badgeCount > 0) {
            HeaderBadge(
                count = badgeCount,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

@Composable
fun HeaderBadge(
    count: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(17.dp)
            .height(17.dp)
            .background(AdminColors.alertRed, RoundedCornerShape(50))
            .border(2.dp, AdminColors.cardWhite, RoundedCornerShape(50))
            .padding(horizontal = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = count.toString(),
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
