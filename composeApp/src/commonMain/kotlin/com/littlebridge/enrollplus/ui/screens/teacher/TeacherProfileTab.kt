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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherProfileData
import com.littlebridge.enrollplus.presentation.TeacherViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes

@Composable
fun TeacherProfileTab(
    viewModel: TeacherViewModel,
    onLogout: () -> Unit = {},
    onOpenOverlay: (TeacherOverlayType) -> Unit = {},
) {
    LaunchedEffect(Unit) {
        viewModel.loadProfile()
        viewModel.loadMyLeave()
        viewModel.loadUnreadCount()
        viewModel.loadHealthAlerts()
        viewModel.loadPewsStudents()
    }

    val profileState by viewModel.profileState.collectAsState()
    val myLeaveState by viewModel.myLeaveState.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val healthAlertsState by viewModel.healthAlertsState.collectAsState()
    val pewsStudentsState by viewModel.pewsStudentsState.collectAsState()

    val profile = (profileState as? UiState.Success)?.data?.data
    val leaveData = (myLeaveState as? UiState.Success)?.data?.data
    val healthAlerts = (healthAlertsState as? UiState.Success)?.data
    val pewsStudents = (pewsStudentsState as? UiState.Success)?.data
    val classesState by viewModel.classesState.collectAsState()
    val classes = (classesState as? UiState.Success)?.data?.data?.classes ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        ProfileHero(profile)
        ProfileStats(profile, classes)
        ProfileQuickAccess(
            unreadCount = unreadCount,
            healthAlertCount = healthAlerts?.alerts?.size ?: 0,
            pewsCount = pewsStudents?.size ?: 0,
            leavePending = leaveData?.pendingCount ?: 0,
            onOpenOverlay = onOpenOverlay,
        )
        ProfileLeaveSection(
            leavePending = leaveData?.pendingCount ?: 0,
            leaveTotal = leaveData?.requests?.size ?: 0,
            onOpenOverlay = onOpenOverlay,
        )
        ProfileSettingsSection(onOpenOverlay = onOpenOverlay)
        ProfileLogout(onLogout)
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ProfileHero(profile: TeacherProfileData?) {
    val name = profile?.name ?: "Teacher"
    val initials = name.take(2).uppercase()
    val subjects = profile?.subjects?.joinToString(", ") ?: "Teacher"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .shadow(1.dp, VShapes.md)
            .background(VColors.white, VShapes.md)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(VColors.violetSoft, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initials,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = VColors.violet,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.4).sp,
                color = VColors.ink,
            )
            Text(
                text = subjects,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
                modifier = Modifier.padding(top = 2.dp),
            )
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                profile?.username?.let { ProfileTag(it) }
                profile?.schoolName?.let { ProfileTag(it) }
            }
        }
    }
}

@Composable
private fun ProfileTag(text: String) {
    Box(
        modifier = Modifier
            .background(VColors.surfaceTint, VShapes.full)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp,
            color = VColors.ink2,
        )
    }
}

@Composable
private fun ProfileStats(profile: TeacherProfileData?, classes: List<com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherClassSummaryDto>) {
    val classCount = profile?.classes?.size ?: 0
    val subjectCount = profile?.subjects?.size ?: 0
    val totalStudents = classes.sumOf { it.studentCount }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatBlock(classCount.toString(), "Classes", Modifier.weight(1f))
        StatBlock(totalStudents.toString(), "Students", Modifier.weight(1f))
        StatBlock(subjectCount.toString(), "Subjects", Modifier.weight(1f))
    }
}

@Composable
private fun StatBlock(num: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .shadow(1.dp, VShapes.md)
            .background(VColors.white, VShapes.md)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = num,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.4).sp,
            color = VColors.ink,
        )
        Text(
            text = label.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
            color = VColors.ink3,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun ProfileQuickAccess(
    unreadCount: Int,
    healthAlertCount: Int,
    pewsCount: Int,
    leavePending: Int,
    onOpenOverlay: (TeacherOverlayType) -> Unit,
) {
    val tiles = listOf(
        QuickTile("Notifications", "$unreadCount unread", TIBell, unreadCount, TeacherOverlayType.ComingSoon),
        QuickTile("Messages", "$unreadCount unread", TIEdit, unreadCount, TeacherOverlayType.Messages),
        QuickTile("Calendar", "View events", TICalendar, null, TeacherOverlayType.ComingSoon),
        QuickTile("Digital ID", "Show QR code", TIUser, null, TeacherOverlayType.ComingSoon),
        QuickTile("Transport", "Route attendance", TIMap, null, TeacherOverlayType.ComingSoon),
        QuickTile("Health Alerts", "$healthAlertCount active", TIAlert, healthAlertCount, TeacherOverlayType.HealthAlerts),
        QuickTile("PEWS", "$pewsCount at risk", TIBell, pewsCount, TeacherOverlayType.PEWS),
        QuickTile("Reports", "Review", TIBook, null, TeacherOverlayType.ComingSoon),
        QuickTile("Heatmap", "Learning insights", TIBook, null, TeacherOverlayType.ComingSoon),
        QuickTile("Scheduled", "Upcoming", TIClock, null, TeacherOverlayType.ComingSoon),
        QuickTile("Events", "Registration", TICalendar, null, TeacherOverlayType.ComingSoon),
    )
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            text = "Quick Access",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = VColors.ink3,
            modifier = Modifier.padding(bottom = 8.dp).padding(start = 4.dp),
        )
        // Grid: 2 columns
        val rows = tiles.chunked(2)
        rows.forEach { rowTiles ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowTiles.forEach { tile ->
                    QuickTileItem(tile, Modifier.weight(1f), onClick = { onOpenOverlay(tile.overlayType) })
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
    val overlayType: TeacherOverlayType,
)

@Composable
private fun QuickTileItem(tile: QuickTile, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Column(
        modifier = modifier
            .shadow(1.dp, VShapes.md)
            .background(VColors.white, VShapes.md)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Top row: icon + badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(VColors.surfaceTint, VShapes.sm),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = tile.icon,
                    contentDescription = tile.label,
                    tint = VColors.ink2,
                    modifier = Modifier.size(16.dp),
                )
            }
            if (tile.badge != null && tile.badge > 0) {
                Box(
                    modifier = Modifier
                        .background(VColors.coralSoft, VShapes.full)
                        .padding(horizontal = 7.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = tile.badge.toString(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.3.sp,
                        color = VColors.coral,
                    )
                }
            }
        }
        // Bottom: label + sub
        Text(
            text = tile.label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.2).sp,
            color = VColors.ink,
        )
        Text(
            text = tile.sub,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = VColors.ink3,
        )
    }
}

@Composable
private fun ProfileLeaveSection(
    leavePending: Int,
    leaveTotal: Int,
    onOpenOverlay: (TeacherOverlayType) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            text = "Leave Management",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = VColors.ink3,
            modifier = Modifier.padding(bottom = 8.dp).padding(start = 4.dp),
        )
        Column(
            modifier = Modifier
                .shadow(1.dp, VShapes.md)
                .background(VColors.white, VShapes.md),
        ) {
            ProfileRow(TICalendar, "Apply for leave", if (leavePending > 0) "$leavePending pending" else null) {
                onOpenOverlay(TeacherOverlayType.Leave)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(1.dp)
                    .background(VColors.lineSoft),
            )
            ProfileRow(TIClock, "Leave history", if (leaveTotal > 0) "$leaveTotal applications" else null) {
                onOpenOverlay(TeacherOverlayType.Leave)
            }
        }
    }
}

@Composable
private fun ProfileSettingsSection(onOpenOverlay: (TeacherOverlayType) -> Unit = {}) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            text = "Settings",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = VColors.ink3,
            modifier = Modifier.padding(bottom = 8.dp).padding(start = 4.dp),
        )
        Column(
            modifier = Modifier
                .shadow(1.dp, VShapes.md)
                .background(VColors.white, VShapes.md),
        ) {
            ProfileRow(TILock, "Change password", null) {
                onOpenOverlay(TeacherOverlayType.ComingSoon)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(1.dp)
                    .background(VColors.lineSoft),
            )
            ProfileRow(TIPalette, "Theme", "System") {
                onOpenOverlay(TeacherOverlayType.ComingSoon)
            }
        }
    }
}

@Composable
private fun ProfileRow(icon: ImageVector, label: String, value: String?, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
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
                    .background(VColors.surfaceTint, VShapes.sm),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = VColors.ink2,
                    modifier = Modifier.size(15.dp),
                )
            }
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = VColors.ink,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (value != null) {
                Text(
                    text = value,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = VColors.ink3,
                )
            }
            Icon(
                imageVector = TIChevronRight,
                contentDescription = null,
                tint = VColors.ink3,
                modifier = Modifier.size(14.dp),
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
            .shadow(1.dp, VShapes.md)
            .background(VColors.white, VShapes.md)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onLogout() }
            .padding(vertical = 14.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = TILogout,
            contentDescription = "Log out",
            tint = VColors.coral,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = "Log out",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = VColors.coral,
        )
    }
}
