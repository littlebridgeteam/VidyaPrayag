package com.littlebridge.enrollplus.ui.screens.admin.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminColors
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminShapes
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminTypography
import com.littlebridge.enrollplus.ui.screens.admin.components.Avatar
import com.littlebridge.enrollplus.ui.screens.admin.components.CardSurface
import com.littlebridge.enrollplus.ui.screens.admin.components.IconBox
import com.littlebridge.enrollplus.ui.screens.admin.components.SettingIconTint

// ═══════════════════════════════════════════════════════════════
// SettingsScreen
// ═══════════════════════════════════════════════════════════════

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Profile card — 277×94, padding:20, margin:8/24/16, radius:18
        SettingsProfileCard(
            name = "Priya Mehta",
            role = "Principal · Delhi Public School",
            pct = 85,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp)
        )

        // Setting rows
        val settings = listOf(
            SettingRowData("Academic Year", "Manage term dates & holidays", SettingIconTint.CALENDAR, "📅"),
            SettingRowData("Classes & Subjects", "Classes, subjects, bell schedule & timetable", SettingIconTint.CLASSES, "📚"),
            SettingRowData("Transport Management", "Routes, vehicles & student assignments", SettingIconTint.TRANSPORT, "🚌"),
            SettingRowData("Scholarship Management", "Schemes, applications & renewals", SettingIconTint.SCHOLAR, "🏆"),
            SettingRowData("Branding Kit", "Logo, colors & custom subdomain", SettingIconTint.BRANDING, "🎨"),
            SettingRowData("ID Cards", "Templates, generation & PDF export", SettingIconTint.ID_CARDS, "🪪"),
            SettingRowData("Library Management", "Catalog, issues, returns & fines", SettingIconTint.LIBRARY, "📖"),
            SettingRowData("Fee Structure", "Edit heads & amounts for next cycle", SettingIconTint.FEE, "💰"),
            SettingRowData("Notifications", "Channels & quiet hours", SettingIconTint.NOTIF, "🔔"),
            SettingRowData("Data Export", "CSV / PDF / UDISE", SettingIconTint.EXPORT, "📥"),
            SettingRowData("Help & Support", "Email support@enrollplus.in", SettingIconTint.HELP, "💬")
        )
        settings.forEach { row ->
            SettingsRow(
                title = row.title,
                sub = row.sub,
                iconTint = row.tint,
                iconChar = row.iconChar,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 6.dp)
            )
        }

        // Theme selector
        SettingsThemeSelector(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp)
        )

        // Logout row
        SettingsRow(
            title = "Logout",
            sub = "Sign out of the admin console",
            iconTint = SettingIconTint.NOTIF,
            iconChar = "🚪",
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 6.dp)
        )
    }
}

data class SettingRowData(
    val title: String,
    val sub: String,
    val tint: SettingIconTint,
    val iconChar: String
)

// ═══════════════════════════════════════════════════════════════
// SettingsProfileCard — 277×94, padding:20, radius:18
// ═══════════════════════════════════════════════════════════════

@Composable
fun SettingsProfileCard(
    name: String,
    role: String,
    pct: Int,
    modifier: Modifier = Modifier
) {
    CardSurface(
        modifier = modifier,
        padding = PaddingValues(20.dp),
        radius = 18
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Avatar — 48×48, bg:rgb(254,243,199), color:rgb(180,83,9), 18sp w800
                Avatar(
                    text = name.take(2).uppercase(),
                    size = 48,
                    bg = AdminColors.siennaBg,
                    color = AdminColors.sienna,
                    fontSize = 18
                )

                // Info
                Column {
                    Text(
                        text = name,
                        color = AdminColors.inkPrimary,
                        style = AdminTypography.profileName
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = role,
                        color = AdminColors.inkSecondary,
                        style = AdminTypography.profileRole
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Profile ${pct}% complete",
                        color = AdminColors.sienna,
                        style = AdminTypography.profilePct
                    )
                }
            }

            // Progress bar — 243×6, bg:rgb(245,240,232), radius:9999, margin-top:14
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(AdminColors.trackBg, RoundedCornerShape(50))
            ) {
                // Fill — 85% of 243, bg:rgb(180,83,9)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(pct / 100f)
                        .height(6.dp)
                        .background(AdminColors.sienna, RoundedCornerShape(50))
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// SettingsRow — 277×53, gap:12, padding:14/16, radius:14
// ═══════════════════════════════════════════════════════════════

@Composable
fun SettingsRow(
    title: String,
    sub: String,
    iconTint: SettingIconTint,
    iconChar: String,
    modifier: Modifier = Modifier
) {
    CardSurface(
        modifier = modifier,
        padding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        radius = 14
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon — 34×34, radius:10
            IconBox(size = 34, bg = iconTint.bg, radius = 10) {
                Text(text = iconChar, fontSize = 14.sp)
            }

            // Body
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = AdminColors.inkPrimary,
                    style = AdminTypography.rowTitle
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = sub,
                    color = AdminColors.inkSecondary,
                    style = AdminTypography.rowSub
                )
            }

            // Chevron right
            Text(
                text = "›",
                color = AdminColors.inkSecondary,
                fontSize = 14.sp
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// SettingsThemeSelector — Light/Dark/High Contrast
// ═══════════════════════════════════════════════════════════════

@Composable
fun SettingsThemeSelector(
    modifier: Modifier = Modifier
) {
    var selected by remember { mutableIntStateOf(0) }
    val themes = listOf("Light", "Dark", "High Contrast")

    CardSurface(
        modifier = modifier,
        padding = PaddingValues(16.dp),
        radius = 14
    ) {
        Column {
            Text(
                text = "Theme",
                color = AdminColors.inkPrimary,
                style = AdminTypography.feeTitle
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                themes.forEachIndexed { index, theme ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selected = index }
                            .border(
                                2.dp,
                                if (selected == index) AdminColors.sienna else AdminColors.headerLine,
                                RoundedCornerShape(10.dp)
                            )
                            .background(
                                if (selected == index) AdminColors.siennaBg else AdminColors.surfaceBase,
                                RoundedCornerShape(10.dp)
                            )
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = theme,
                            color = if (selected == index) AdminColors.sienna else AdminColors.inkSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (selected == index) FontWeight.ExtraBold else FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
