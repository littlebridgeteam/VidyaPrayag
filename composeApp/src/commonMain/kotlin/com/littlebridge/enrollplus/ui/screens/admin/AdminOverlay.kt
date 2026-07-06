package com.littlebridge.enrollplus.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
// OverlayHeader — 317×41dp, gap:12, padding:8/24, bg:white
// shadow: bottom border rgb(240,234,224) 0px 1px 0px
// ═══════════════════════════════════════════════════════════════

@Composable
fun OverlayHeader(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(41.dp)
            .background(AdminColors.cardWhite)
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Back button — 27×27, bg:rgb(248,244,239), radius:10
        Box(
            modifier = Modifier
                .size(27.dp)
                .background(AdminColors.pillBg, RoundedCornerShape(10.dp))
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "‹",
                fontSize = 16.sp,
                color = AdminColors.inkPrimary
            )
        }

        Text(
            text = title,
            color = AdminColors.inkPrimary,
            style = AdminTypography.overlayTitle
        )
    }
    // Bottom border line
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(AdminColors.headerLine)
    )
}

// ═══════════════════════════════════════════════════════════════
// NotificationsOverlay — 317×703, bg:rgb(251,248,244)
// ═══════════════════════════════════════════════════════════════

@Composable
fun NotificationsOverlay(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(317.dp)
            .height(703.dp)
            .background(AdminColors.surfaceBase)
    ) {
        OverlayHeader(title = "Notifications", onBack = onBack)

        // Overlay body — padding:0/0/24
        Column(
            modifier = Modifier
                .padding(bottom = 24.dp)
        ) {
            // Notif filters — 277×32, gap:0, padding:3, margin:8/24/16
            NotifFilterRow()

            // Mark all button
            Box(
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                    .clickable { }
            ) {
                Text(
                    text = "Mark all as read",
                    color = AdminColors.sienna,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Notification items would go here
            // Placeholder for now
        }
    }
}

@Composable
private fun NotifFilterRow() {
    Row(
        modifier = Modifier
            .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp)
            .background(AdminColors.pillBg, AdminShapes.pillOuter)
            .padding(3.dp)
    ) {
        // Active "All"
        Box(
            modifier = Modifier
                .weight(1f)
                .height(27.dp)
                .shadow(1.dp, AdminShapes.pillInner, ambientColor = Color(0x0A1A1614), spotColor = Color(0x0F1A1614))
                .background(AdminColors.cardWhite, AdminShapes.pillInner),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "All",
                color = AdminColors.inkPrimary,
                style = AdminTypography.filterActive
            )
        }
        // Inactive "Unread"
        Box(
            modifier = Modifier
                .weight(1f)
                .height(27.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Unread",
                    color = AdminColors.inkSecondary,
                    style = AdminTypography.filterInactive
                )
                Text(
                    text = "3",
                    color = AdminColors.inkSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
