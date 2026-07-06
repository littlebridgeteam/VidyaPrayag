package com.littlebridge.enrollplus.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminShapes
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminTab

// ═══════════════════════════════════════════════════════════════
// BottomNav — 290×68dp, space-between, gap:2, padding:6
// margin:0/16/8, bg:white, radius:24, shadow:elevation
// ═══════════════════════════════════════════════════════════════

@Composable
fun BottomNav(
    activeTab: AdminTab,
    onTabSelect: (AdminTab) -> Unit,
    commsBadge: Int = 0,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .width(290.dp)
            .height(68.dp)
            .shadow(3.dp, RoundedCornerShape(24.dp), ambientColor = Color(0x141A1614), spotColor = Color(0x0A1A1614))
            .background(AdminColors.cardWhite, RoundedCornerShape(24.dp))
            .padding(6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AdminTab.entries.forEach { tab ->
            BottomNavItem(
                label = tab.label,
                active = tab == activeTab,
                badge = if (tab == AdminTab.COMMS && commsBadge > 0) commsBadge else null,
                onClick = { onTabSelect(tab) }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    active: Boolean,
    badge: Int? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Active item: 56×58, inactive: 54×55
    val itemWidth = if (active) 56.dp else 54.dp
    val itemHeight = if (active) 58.dp else 55.dp

    Box(
        modifier = modifier
            .width(itemWidth)
            .height(itemHeight)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Icon container — 34×34
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        if (active) AdminColors.sienna else Color.Transparent,
                        RoundedCornerShape(14.dp)
                    )
                    .then(
                        if (active) Modifier.shadow(
                            4.dp, RoundedCornerShape(14.dp),
                            ambientColor = Color(0x59B45309), spotColor = Color(0x59B45309)
                        ) else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Icon placeholder — using text for now
                val iconChar = when (label) {
                    "Home" -> "🏠"
                    "People" -> "👥"
                    "Records" -> "📊"
                    "Comms" -> "💬"
                    "Settings" -> "⚙"
                    else -> "•"
                }
                Text(
                    text = iconChar,
                    fontSize = 14.sp,
                    color = if (active) Color.White else AdminColors.inkSecondary
                )
            }

            Text(
                text = label,
                color = if (active) AdminColors.sienna else AdminColors.inkSecondary,
                fontSize = 10.sp,
                fontWeight = if (active) FontWeight.ExtraBold else FontWeight.SemiBold
            )
        }

        // Badge (for Comms) — 17×17, 2px white border
        if (badge != null && badge > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .width(17.dp)
                    .height(17.dp)
                    .background(AdminColors.alertRed, RoundedCornerShape(50))
                    .border(2.dp, AdminColors.cardWhite, RoundedCornerShape(50))
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badge.toString(),
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
