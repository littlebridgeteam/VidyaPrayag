package com.littlebridge.enrollplus.ui.screens.teacher

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
// Custom icons from TeacherIcons.kt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.presentation.TeacherViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.tokens.VMotion
import com.littlebridge.enrollplus.ui.navigation.DeepLinkTarget
import com.littlebridge.enrollplus.ui.navigation.TeacherDeepLinkTab
import com.littlebridge.enrollplus.ui.navigation.parseDeepLink
import com.littlebridge.enrollplus.util.nowMinutesOfDay

enum class TeacherTab(val label: String, val icon: ImageVector) {
    Home("Home", TIHome),
    Update("Update", TIEdit),
    Classes("Classes", TIClasses),
    Timetable("Timetable", TICalendar),
    Profile("Profile", TIUser),
}

@Composable
fun TeacherPortalScreen(
    viewModel: TeacherViewModel,
    onLogout: () -> Unit = {},
    initialTab: TeacherTab? = null,
) {
    LaunchedEffect(Unit) { viewModel.loadAll() }

    val unreadCount by viewModel.unreadCount.collectAsState()
    val profileState by viewModel.profileState.collectAsState()

    val teacherName = when (val s = profileState) {
        is UiState.Success -> s.data.data.name
        else -> "Teacher"
    }

    var selectedTab by rememberSaveable { mutableStateOf(initialTab ?: TeacherTab.Home) }
    var showNotifications by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(initialTab) {
        if (initialTab != null) selectedTab = initialTab
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VColors.cream),
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VColors.cream),
    ) {
        // Portal Header
        PortalHeader(
            teacherName = teacherName,
            notificationCount = unreadCount,
            onNotificationsClick = { showNotifications = true },
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
        )

        // Tab Content
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                fadeIn(tween(VMotion.durDefault)) togetherWith fadeOut(tween(VMotion.durDefault))
            },
            label = "teacherTab",
            modifier = Modifier.weight(1f),
        ) { tab ->
            when (tab) {
                TeacherTab.Home -> TeacherHomeTab(
                    viewModel = viewModel,
                    onNavigateTab = { selectedTab = it },
                )
                TeacherTab.Update -> TeacherUpdateTab(viewModel = viewModel)
                TeacherTab.Classes -> TeacherClassesTab(viewModel = viewModel)
                TeacherTab.Timetable -> TeacherTimetableTab(viewModel = viewModel)
                TeacherTab.Profile -> TeacherProfileTab(
                    viewModel = viewModel,
                    onLogout = onLogout,
                )
            }
        }

        // Bottom Nav
        TeacherBottomNav(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
        )
    }

        // Notifications Overlay
        TeacherNotificationsOverlay(
            viewModel = viewModel,
            visible = showNotifications,
            onDismiss = { showNotifications = false },
            onDeepLink = { deepLinkString ->
                val target = parseDeepLink(deepLinkString, "teacher")
                if (target is DeepLinkTarget.TeacherTab) {
                    selectedTab = when (target.tab) {
                        TeacherDeepLinkTab.Home -> TeacherTab.Home
                        TeacherDeepLinkTab.Update -> TeacherTab.Update
                        TeacherDeepLinkTab.Classes -> TeacherTab.Classes
                        TeacherDeepLinkTab.Timetable -> TeacherTab.Timetable
                        TeacherDeepLinkTab.Profile -> TeacherTab.Profile
                    }
                }
            },
        )
    }
}

@Composable
private fun PortalHeader(
    teacherName: String,
    notificationCount: Int,
    onNotificationsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = greetingForHour(remember { currentHour() }),
                style = VTypography.caption.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.3.sp,
                ),
                color = VColors.ink3,
            )
            Text(
                text = teacherName,
                style = VTypography.h2.copy(fontSize = 20.sp),
                color = VColors.ink,
            )
        }
        Box {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .shadow(1.dp, CircleShape)
                    .background(VColors.white, CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onNotificationsClick() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = TIBell,
                    contentDescription = "Notifications",
                    tint = VColors.ink2,
                    modifier = Modifier.size(18.dp),
                )
            }
            if (notificationCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(17.dp)
                        .background(VColors.coral, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = notificationCount.toString(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = VColors.white,
                    )
                }
            }
        }
    }
}

@Composable
private fun TeacherBottomNav(
    selectedTab: TeacherTab,
    onTabSelected: (TeacherTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(VColors.white)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        TeacherTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            val iconColor = if (isSelected) VColors.violet else VColors.ink3
            val labelColor = if (isSelected) VColors.violet else VColors.ink3
            val labelWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onTabSelected(tab) }
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            if (isSelected) VColors.violetSoft else Color.Transparent,
                            VShapes.sm,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = iconColor,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Text(
                    text = tab.label,
                    fontSize = 11.sp,
                    fontWeight = labelWeight,
                    color = labelColor,
                )
            }
        }
    }
}

private fun currentHour(): Int = nowMinutesOfDay() / 60

internal fun greetingForHour(hour: Int): String = when (hour) {
    in 5..11 -> "Good morning"
    in 12..16 -> "Good afternoon"
    in 17..21 -> "Good evening"
    else -> "Good night"
}
