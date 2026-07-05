package com.littlebridge.enrollplus.ui.v2.screens.premium.teacher

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.notification.presentation.NotificationsViewModel
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherTodayViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHandler
import com.littlebridge.enrollplus.ui.v2.components.navigation.NavItem
import com.littlebridge.enrollplus.ui.v2.components.navigation.VBottomNav
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.navigation.DeepLinkTarget
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

/** Full-screen overlays the teacher portal can push above its tab content. */
private enum class TeacherOverlay {
    None, Notifications, HealthAlerts, TransportAttendance, Pews,
    ReportReview, ReportDraftEditor, Messages, Calendar, Library,
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TeacherPortalShell(
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier,
    deepLinkTarget: DeepLinkTarget? = null,
    todayViewModel: TeacherTodayViewModel = koinViewModel(),
    notificationsViewModel: NotificationsViewModel = koinViewModel(),
) = PremiumTheme(isDark = false) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var overlay by remember { mutableStateOf(TeacherOverlay.None) }
    var localDeepLink by remember { mutableStateOf<DeepLinkTarget?>(null) }
    var deepLinkThreadId by remember { mutableStateOf<String?>(null) }

    val state by todayViewModel.state.collectAsStateV2()
    val notifState by notificationsViewModel.state.collectAsStateV2()

    // Apply deep-link routing: set tab + overlay from the typed target.
    LaunchedEffect(deepLinkTarget, localDeepLink) {
        val target = localDeepLink ?: deepLinkTarget ?: return@LaunchedEffect
        when (target) {
            is DeepLinkTarget.TeacherScreen -> {
                tab = when (target.screen) {
                    "home" -> 0
                    "classes" -> 1
                    "timetable" -> 2
                    "profile" -> 3
                    "transport" -> { overlay = TeacherOverlay.TransportAttendance; 0 }
                    "report-card", "report-review" -> { overlay = TeacherOverlay.ReportReview; 0 }
                    "pews" -> { overlay = TeacherOverlay.Pews; 0 }
                    "messages" -> { overlay = TeacherOverlay.Messages; 0 }
                    "calendar" -> { overlay = TeacherOverlay.Calendar; 0 }
                    "library" -> { overlay = TeacherOverlay.Library; 0 }
                    "announcements" -> { overlay = TeacherOverlay.Notifications; 0 }
                    else -> 0
                }
            }
            is DeepLinkTarget.Messages -> {
                deepLinkThreadId = target.threadId
                overlay = TeacherOverlay.Messages
            }
            is DeepLinkTarget.Generic -> {
                val pathOnly = target.path.substringBefore("?").removePrefix("/")
                when {
                    pathOnly.startsWith("messages") -> overlay = TeacherOverlay.Messages
                    pathOnly.startsWith("transport") -> overlay = TeacherOverlay.TransportAttendance
                    pathOnly.startsWith("calendar") -> overlay = TeacherOverlay.Calendar
                    pathOnly.startsWith("pews") -> overlay = TeacherOverlay.Pews
                    pathOnly.startsWith("library") -> overlay = TeacherOverlay.Library
                    pathOnly.startsWith("timetable") -> { tab = 2; overlay = TeacherOverlay.None }
                    else -> { tab = 0; overlay = TeacherOverlay.None }
                }
            }
            else -> Unit
        }
        localDeepLink = null
    }

    // Back handler: dismiss overlay first, then return to home tab.
    VBackHandler(enabled = overlay != TeacherOverlay.None) {
        when (overlay) {
            TeacherOverlay.ReportDraftEditor -> overlay = TeacherOverlay.ReportReview
            else -> {
                overlay = TeacherOverlay.None
                deepLinkThreadId = null
            }
        }
    }
    VBackHandler(enabled = overlay == TeacherOverlay.None && tab != 0) {
        tab = 0
    }

    // ── Overlays sit above all tab content ──────────────────────────────────
    when (overlay) {
        TeacherOverlay.Notifications -> {
            TeacherNotificationsScreen(
                onBack = { overlay = TeacherOverlay.None },
                modifier = modifier,
            )
            return@PremiumTheme
        }
        TeacherOverlay.HealthAlerts -> {
            TeacherHealthAlertsScreen(
                onBack = { overlay = TeacherOverlay.None },
                modifier = modifier,
            )
            return@PremiumTheme
        }
        TeacherOverlay.TransportAttendance -> {
            TeacherTransportAttendanceScreen(
                onBack = { overlay = TeacherOverlay.None },
                modifier = modifier,
            )
            return@PremiumTheme
        }
        TeacherOverlay.Pews -> {
            TeacherPewsScreen(
                onBack = { overlay = TeacherOverlay.None },
                modifier = modifier,
            )
            return@PremiumTheme
        }
        TeacherOverlay.ReportReview -> {
            TeacherReportReviewScreen(
                onBack = { overlay = TeacherOverlay.None },
                modifier = modifier,
            )
            return@PremiumTheme
        }
        TeacherOverlay.ReportDraftEditor -> {
            TeacherReportDraftScreen(
                onBack = { overlay = TeacherOverlay.ReportReview },
                modifier = modifier,
            )
            return@PremiumTheme
        }
        TeacherOverlay.Messages -> {
            TeacherMessagesScreen(
                onBack = { overlay = TeacherOverlay.None; deepLinkThreadId = null },
                modifier = modifier,
            )
            return@PremiumTheme
        }
        TeacherOverlay.Calendar -> {
            TeacherCalendarScreen(
                onBack = { overlay = TeacherOverlay.None },
                modifier = modifier,
            )
            return@PremiumTheme
        }
        TeacherOverlay.Library -> {
            TeacherLibraryScreen(
                onBack = { overlay = TeacherOverlay.None },
                modifier = modifier,
            )
            return@PremiumTheme
        }
        TeacherOverlay.None -> Unit
    }

    val tabs = remember {
        listOf(
            NavItem("Home", icon = { TabIcon(Icons.Filled.Home) }),
            NavItem("Classes", icon = { TabIcon(Icons.Filled.MenuBook) }),
            NavItem("Schedule", icon = { TabIcon(Icons.Filled.CalendarMonth) }),
            NavItem("Profile", icon = { TabIcon(Icons.Filled.Person) }),
        )
    }

    Column(modifier = modifier.fillMaxSize().background(VColors.Surface)) {
        TeacherHeader(
            teacherName = state.teacherName,
            unreadCount = notifState.unreadCount,
            onBellClick = { overlay = TeacherOverlay.Notifications },
            onMessagesClick = { overlay = TeacherOverlay.Messages },
            modifier = Modifier.fillMaxWidth(),
        )

        Box(Modifier.weight(1f).fillMaxWidth()) {
            AnimatedContent(
                targetState = tab,
                transitionSpec = { fadeIn(tween(300)).togetherWith(fadeOut(tween(200))) },
                label = "teacherTab",
            ) { current ->
                when (current) {
                    0 -> TeacherHomeScreen()
                    1 -> TeacherClassesScreen()
                    2 -> TeacherTimetableScreen()
                    3 -> TeacherProfileScreen(onLogout = onLogout)
                }
            }
        }

        VBottomNav(
            items = tabs,
            activeIndex = tab,
            onItemClick = { tab = it },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun TeacherHeader(
    teacherName: String,
    unreadCount: Int,
    onBellClick: () -> Unit,
    onMessagesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(VColors.Surface)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier.size(40.dp).clip(CircleShape).background(VColors.PrimaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                teacherName.firstOrNull()?.toString() ?: "T",
                style = VTypography.SectionHeader.copy(color = VColors.OnPrimaryContainer),
            )
        }
        Column(Modifier.weight(1f)) {
            Text("Welcome back", style = VTypography.Eyebrow.copy(color = VColors.OnSurfaceVariant))
            Text(
                teacherName.ifBlank { "Teacher" },
                style = VTypography.GreetingTitle.copy(color = VColors.OnSurface),
            )
        }
        // Messages icon
        val msgInteraction = remember { MutableInteractionSource() }
        Box(
            Modifier.size(40.dp).clip(CircleShape).background(VColors.SurfaceContainerHigh)
                .pressScale(msgInteraction, pressedScale = 0.9f)
                .clickable(interactionSource = msgInteraction, indication = null, onClick = onMessagesClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Notifications, contentDescription = "Messages", tint = VColors.OnSurfaceVariant, modifier = Modifier.size(22.dp))
            if (unreadCount > 0) {
                Box(Modifier.align(Alignment.TopEnd).size(8.dp).clip(CircleShape).background(VColors.Error))
            }
        }
        // Notification bell
        val bellInteraction = remember { MutableInteractionSource() }
        Box(
            Modifier.size(40.dp).clip(CircleShape).background(VColors.SurfaceContainerHigh)
                .pressScale(bellInteraction, pressedScale = 0.9f)
                .clickable(interactionSource = bellInteraction, indication = null, onClick = onBellClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = VColors.OnSurfaceVariant, modifier = Modifier.size(22.dp))
            if (unreadCount > 0) {
                Box(Modifier.align(Alignment.TopEnd).size(8.dp).clip(CircleShape).background(VColors.Error))
            }
        }
    }
}

@Composable
private fun TabIcon(icon: ImageVector) {
    Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
}
