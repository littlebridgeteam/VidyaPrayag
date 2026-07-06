package com.littlebridge.enrollplus.ui.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.presentation.ParentViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes

@Composable
fun ParentProfileTab(
    viewModel: ParentViewModel,
    onSettingsClick: () -> Unit,
    onLinkChildClick: () -> Unit,
    onDiscoverSchoolsClick: () -> Unit,
    onLogout: () -> Unit,
) {
    val dashboardState by viewModel.dashboardState.collectAsState()
    val attendanceState by viewModel.attendanceState.collectAsState()
    val selectedChildId by viewModel.selectedChildId.collectAsState()

    val child = when (val s = dashboardState) {
        is UiState.Success -> s.data.children.firstOrNull { it.id == selectedChildId }
            ?: s.data.children.firstOrNull()
            ?: s.data.childSummary
        else -> null
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(VColors.cream)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
        // Hero — white card with decorative circles and avatar ring
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .background(VColors.white, VShapes.lg)
                .shadow(1.dp, VShapes.lg),
        ) {
            // Decorative circles
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(120.dp)
                    .background(VColors.violetSoft.copy(alpha = 0.4f), CircleShape)
                    .offset(x = 20.dp, y = (-40).dp),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(80.dp)
                    .background(VColors.coralSoft.copy(alpha = 0.3f), CircleShape)
                    .offset(x = (-20).dp, y = 30.dp),
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
            ) {
                // Avatar with white ring + violet-soft outer ring
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(VColors.white, CircleShape)
                        .padding(3.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .background(VColors.violetSoft, CircleShape)
                            .padding(3.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            child?.name?.take(2)?.uppercase() ?: "?",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = VColors.violet,
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    child?.name ?: "—",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = VColors.ink,
                    letterSpacing = (-0.4).sp,
                )
                Text(
                    "Level ${child?.currentLevel ?: 0} · ${child?.attendanceStatus ?: "—"}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = VColors.ink3,
                    modifier = Modifier.padding(top = 4.dp),
                )
                // House badge
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .background(VColors.coralSoft, VShapes.full)
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                ) {
                    Text("Red House", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VColors.coral, letterSpacing = 0.3.sp)
                }
            }
        }

        // Stats grid
        val attRate = (attendanceState as? UiState.Success)?.data?.attendanceRate ?: 0
        val presentDays = (attendanceState as? UiState.Success)?.data?.presentDays ?: 0
        val totalDays = (attendanceState as? UiState.Success)?.data?.totalDays ?: 0

        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ProfileStat(
                value = "$attRate%", label = "Attendance",
                trend = if (totalDays > 0) "$presentDays/$totalDays days" else "—",
                modifier = Modifier.weight(1f),
            )
            ProfileStat(
                value = "${(child?.overallProgress?.times(100))?.toInt() ?: 0}%",
                label = "Progress",
                trend = "Overall",
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ProfileStat(
                value = "${child?.currentLevel ?: 0}",
                label = "Level",
                trend = "Current",
                modifier = Modifier.weight(1f),
            )
            ProfileStat(
                value = child?.attendanceStatus ?: "—",
                label = "Status",
                trend = "Today",
                modifier = Modifier.weight(1f),
            )
        }

        // Account section
        Text(
            "Account",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = VColors.ink3,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 10.dp),
        )

        ProfileRow(
            icon = Icons.Rounded.Settings,
            iconBg = VColors.surfaceTint, iconTint = VColors.ink2,
            label = "Account Settings",
            onClick = onSettingsClick,
        )
        ProfileRow(
            icon = Icons.Rounded.Link,
            iconBg = VColors.violetSoft, iconTint = VColors.violet,
            label = "Link Another Child",
            onClick = onLinkChildClick,
        )
        ProfileRow(
            icon = Icons.Rounded.Explore,
            iconBg = VColors.skySoft, iconTint = VColors.sky,
            label = "Discover Schools",
            onClick = onDiscoverSchoolsClick,
        )

        // Logout
        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .background(VColors.coralSoft, VShapes.md)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onLogout() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null, tint = VColors.coral, modifier = Modifier.size(18.dp))
                Text("Logout", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VColors.coral, letterSpacing = (-0.2).sp)
            }
        }

        Spacer(Modifier.height(100.dp))
    }
}

@Composable
private fun ProfileStat(value: String, label: String, trend: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .background(VColors.white, VShapes.md)
            .shadow(1.dp, VShapes.md)
            .padding(14.dp),
    ) {
        Column {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = VColors.ink, letterSpacing = (-0.3).sp)
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = VColors.ink3, modifier = Modifier.padding(top = 4.dp), letterSpacing = 0.5.sp)
            Text(trend, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = VColors.ink3, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
private fun ProfileRow(icon: ImageVector, iconBg: Color, iconTint: Color, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .background(VColors.white, VShapes.md)
            .shadow(1.dp, VShapes.md)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(32.dp).background(iconBg, VShapes.sm),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VColors.ink, letterSpacing = (-0.2).sp, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(16.dp))
    }
    Spacer(Modifier.height(6.dp))
}
