package com.littlebridge.enrollplus.ui.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
// Custom icons from TeacherIcons.kt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes

@Composable
fun TeacherProfileTab(
    onLogout: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        ProfileHero()
        ProfileStats()
        ProfileQuickAccess()
        ProfileLeaveSection()
        ProfileSettingsSection()
        ProfileLogout(onLogout)
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ProfileHero() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(VColors.violetSoft, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "PS",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = VColors.violet,
            )
        }
        Column {
            Text(
                text = "Priya Sharma",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp,
                color = VColors.ink,
            )
            Text(
                text = "Mathematics Teacher",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink2,
                modifier = Modifier.padding(top = 2.dp),
            )
            Row(
                modifier = Modifier.padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                ProfileTag("EMP-0421")
                ProfileTag("Science Dept")
            }
        }
    }
}

@Composable
private fun ProfileTag(text: String) {
    Box(
        modifier = Modifier
            .background(VColors.surfaceTint, VShapes.sm)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = VColors.ink2,
        )
    }
}

@Composable
private fun ProfileStats() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatBlock("5", "Classes", Modifier.weight(1f))
        StatBlock("142", "Students", Modifier.weight(1f))
        StatBlock("7", "Years", Modifier.weight(1f))
    }
}

@Composable
private fun StatBlock(num: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .shadow(1.dp, VShapes.md)
            .background(VColors.white, VShapes.md)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = num,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp,
            color = VColors.ink,
        )
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = VColors.ink3,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun ProfileQuickAccess() {
    val tiles = remember {
        listOf(
            QuickTile("Notifications", "3 unread", TIBell, 3),
            QuickTile("Messages", "3 unread", TIEdit, 3),
            QuickTile("Calendar", "View events", TICalendar, null),
            QuickTile("Digital ID", "Show QR code", TIUser, null),
            QuickTile("Transport", "Route attendance", TIMap, null),
            QuickTile("Health Alerts", "2 active", TIAlert, 2),
            QuickTile("PEWS", "2 at risk", TIBell, 2),
            QuickTile("Reports", "3 pending", TIBook, 3),
            QuickTile("Heatmap", "Learning insights", TIBook, null),
            QuickTile("Scheduled", "2 upcoming", TIClock, 2),
            QuickTile("Events", "Registration", TICalendar, null),
        )
    }
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            text = "Quick Access",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = VColors.ink,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        // Grid: 2 columns
        val rows = tiles.chunked(2)
        rows.forEach { rowTiles ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowTiles.forEach { tile ->
                    QuickTileItem(tile, Modifier.weight(1f))
                }
                if (rowTiles.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

private data class QuickTile(
    val label: String,
    val sub: String,
    val icon: ImageVector,
    val badge: Int?,
)

@Composable
private fun QuickTileItem(tile: QuickTile, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .shadow(1.dp, VShapes.md)
            .background(VColors.white, VShapes.md)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {}
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(VColors.surfaceTint, VShapes.full),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = tile.icon,
                contentDescription = tile.label,
                tint = VColors.ink2,
                modifier = Modifier.size(19.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tile.label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = VColors.ink,
            )
            Text(
                text = tile.sub,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
            )
        }
        if (tile.badge != null && tile.badge > 0) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(VColors.coral, VShapes.full),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = tile.badge.toString(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = VColors.white,
                )
            }
        }
    }
}

@Composable
private fun ProfileLeaveSection() {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            text = "Leave Management",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = VColors.ink,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        Column(
            modifier = Modifier
                .shadow(1.dp, VShapes.md)
                .background(VColors.white, VShapes.md),
        ) {
            ProfileRow(TICalendar, "Apply for leave", "Casual · 7 left")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(1.dp)
                    .background(VColors.lineSoft),
            )
            ProfileRow(TIClock, "Leave history", "3 applications")
        }
    }
}

@Composable
private fun ProfileSettingsSection() {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            text = "Settings",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = VColors.ink,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        Column(
            modifier = Modifier
                .shadow(1.dp, VShapes.md)
                .background(VColors.white, VShapes.md),
        ) {
            ProfileRow(TILock, "Change password", null)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(1.dp)
                    .background(VColors.lineSoft),
            )
            ProfileRow(TIPalette, "Theme", "System")
        }
    }
}

@Composable
private fun ProfileRow(icon: ImageVector, label: String, value: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {}
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(VColors.surfaceTint, VShapes.full),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = VColors.ink2,
                    modifier = Modifier.size(17.dp),
                )
            }
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (value != null) {
                Text(
                    text = value,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = VColors.ink3,
                )
            }
            Icon(
                imageVector = TIChevronRight,
                contentDescription = null,
                tint = VColors.ink3,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun ProfileLogout(onLogout: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onLogout() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = TILogout,
            contentDescription = "Log out",
            tint = VColors.error,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = "Log out",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = VColors.error,
        )
    }
}
