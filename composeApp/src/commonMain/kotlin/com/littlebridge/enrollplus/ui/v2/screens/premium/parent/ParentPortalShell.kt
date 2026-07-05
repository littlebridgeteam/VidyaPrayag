package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.School
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
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.notification.presentation.NotificationsViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardViewModel
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

/** Full-screen overlays the parent portal can push above its tab content. */
enum class ParentOverlay {
    None,
    Notifications,
    Calendar,
    Events,
    Transport,
    Library,
    Scholarships,
    Health,
    Pulse,
    IDCard,
    ReportCard,
    Tutor,
    TutorProgress,
    Timetable,
    Leave,
    AccountSettings,
    LinkChild,
    Discovery,
    SchoolDetail,
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ParentPortalShell(
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier,
    deepLinkTarget: DeepLinkTarget? = null,
    dashboardViewModel: ParentDashboardViewModel = koinViewModel(),
    notificationsViewModel: NotificationsViewModel = koinViewModel(),
) = PremiumTheme(isDark = false) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var overlay by remember { mutableStateOf(ParentOverlay.None) }
    var localDeepLink by remember { mutableStateOf<DeepLinkTarget?>(null) }
    var deepLinkThreadId by remember { mutableStateOf<String?>(null) }

    val state by dashboardViewModel.state.collectAsStateV2()
    val notifState by notificationsViewModel.state.collectAsStateV2()

    // ── Deep-link routing ───────────────────────────────────────────────────
    LaunchedEffect(deepLinkTarget, localDeepLink) {
        val target = localDeepLink ?: deepLinkTarget ?: return@LaunchedEffect
        when (target) {
            is DeepLinkTarget.ParentTab -> {
                tab = when (target.tab) {
                    "home" -> 0
                    "academics" -> 1
                    "fees" -> 2
                    "conversations" -> 3
                    "profile" -> 4
                    else -> 0
                }
                overlay = when (target.overlay) {
                    "notifications" -> ParentOverlay.Notifications
                    "calendar" -> ParentOverlay.Calendar
                    "events" -> ParentOverlay.Events
                    "transport" -> ParentOverlay.Transport
                    "library" -> ParentOverlay.Library
                    "scholarships" -> ParentOverlay.Scholarships
                    "health" -> ParentOverlay.Health
                    "pulse" -> ParentOverlay.Pulse
                    "id-card" -> ParentOverlay.IDCard
                    "report-card" -> ParentOverlay.ReportCard
                    "tutor" -> ParentOverlay.Tutor
                    "tutor-progress" -> ParentOverlay.TutorProgress
                    "timetable" -> ParentOverlay.Timetable
                    "leave" -> ParentOverlay.Leave
                    "account-settings" -> ParentOverlay.AccountSettings
                    "link-child" -> ParentOverlay.LinkChild
                    "discovery" -> ParentOverlay.Discovery
                    "school-detail" -> ParentOverlay.SchoolDetail
                    else -> ParentOverlay.None
                }
            }
            is DeepLinkTarget.Messages -> {
                tab = 3
                deepLinkThreadId = target.threadId
                overlay = ParentOverlay.None
            }
            is DeepLinkTarget.Generic -> {
                val pathOnly = target.path.substringBefore("?").removePrefix("/")
                when {
                    pathOnly.startsWith("messages") -> { tab = 3; overlay = ParentOverlay.None }
                    pathOnly.startsWith("notifications") -> { overlay = ParentOverlay.Notifications }
                    pathOnly.startsWith("calendar") -> { overlay = ParentOverlay.Calendar }
                    pathOnly.startsWith("events") -> { overlay = ParentOverlay.Events }
                    pathOnly.startsWith("transport") -> { overlay = ParentOverlay.Transport }
                    pathOnly.startsWith("library") -> { overlay = ParentOverlay.Library }
                    pathOnly.startsWith("scholarships") -> { overlay = ParentOverlay.Scholarships }
                    pathOnly.startsWith("health") -> { overlay = ParentOverlay.Health }
                    pathOnly.startsWith("pulse") -> { overlay = ParentOverlay.Pulse }
                    pathOnly.startsWith("id-card") -> { overlay = ParentOverlay.IDCard }
                    pathOnly.startsWith("report-card") -> { overlay = ParentOverlay.ReportCard }
                    pathOnly.startsWith("tutor-progress") -> { overlay = ParentOverlay.TutorProgress }
                    pathOnly.startsWith("tutor") -> { overlay = ParentOverlay.Tutor }
                    pathOnly.startsWith("timetable") -> { overlay = ParentOverlay.Timetable }
                    pathOnly.startsWith("leave") -> { overlay = ParentOverlay.Leave }
                    pathOnly.startsWith("account-settings") -> { overlay = ParentOverlay.AccountSettings }
                    pathOnly.startsWith("link-child") -> { overlay = ParentOverlay.LinkChild }
                    pathOnly.startsWith("discovery") -> { overlay = ParentOverlay.Discovery }
                    pathOnly.startsWith("school-detail") -> { overlay = ParentOverlay.SchoolDetail }
                    else -> { tab = 0; overlay = ParentOverlay.None }
                }
            }
            else -> Unit
        }
        localDeepLink = null
    }

    // ── Back handler: overlay → tab → home tab ──────────────────────────────
    VBackHandler(enabled = overlay != ParentOverlay.None) {
        overlay = ParentOverlay.None
        deepLinkThreadId = null
    }
    VBackHandler(enabled = overlay == ParentOverlay.None && tab != 0) {
        tab = 0
    }

    // ── Overlays sit above all tab content ──────────────────────────────────
    if (overlay != ParentOverlay.None) {
        val overlayTitle = when (overlay) {
            ParentOverlay.Notifications -> "Notifications"
            ParentOverlay.Calendar -> "Calendar"
            ParentOverlay.Events -> "Events"
            ParentOverlay.Transport -> "Transport"
            ParentOverlay.Library -> "Library"
            ParentOverlay.Scholarships -> "Scholarships"
            ParentOverlay.Health -> " Health"
            ParentOverlay.Pulse -> "Pulse"
            ParentOverlay.IDCard -> "ID Card"
            ParentOverlay.ReportCard -> "Report Card"
            ParentOverlay.Tutor -> "Tutor"
            ParentOverlay.TutorProgress -> "Tutor Progress"
            ParentOverlay.Timetable -> "Timetable"
            ParentOverlay.Leave -> "Leave Application"
            ParentOverlay.AccountSettings -> "Account Settings"
            ParentOverlay.LinkChild -> "Link Child"
            ParentOverlay.Discovery -> "Discover Schools"
            ParentOverlay.SchoolDetail -> "School Detail"
            ParentOverlay.None -> ""
        }
        val onOverlayBack = {
            overlay = ParentOverlay.None
            deepLinkThreadId = null
        }
        when (overlay) {
            ParentOverlay.Notifications -> NotificationsOverlay(
                onBack = onOverlayBack,
                onDeepLink = { /* TODO: parse deep link */ },
                modifier = modifier,
            )
            ParentOverlay.Leave -> LeaveOverlay(
                onBack = onOverlayBack,
                modifier = modifier,
            )
            ParentOverlay.Health -> HealthOverlay(
                onBack = onOverlayBack,
                modifier = modifier,
            )
            ParentOverlay.Transport -> TransportOverlay(
                onBack = onOverlayBack,
                modifier = modifier,
            )
            ParentOverlay.Tutor -> TutorChatOverlay(
                onBack = onOverlayBack,
                modifier = modifier,
            )
            ParentOverlay.Calendar -> CalendarOverlay(
                onBack = onOverlayBack,
                modifier = modifier,
            )
            ParentOverlay.Scholarships -> ScholarshipsOverlay(
                onBack = onOverlayBack,
                modifier = modifier,
            )
            ParentOverlay.AccountSettings -> AccountSettingsOverlay(
                onBack = onOverlayBack,
                onLogout = onLogout,
                modifier = modifier,
            )
            ParentOverlay.TutorProgress -> TutorProgressOverlay(
                onBack = onOverlayBack,
                modifier = modifier,
            )
            ParentOverlay.IDCard -> DigitalIdCardOverlay(
                onBack = onOverlayBack,
                modifier = modifier,
            )
            ParentOverlay.Library -> LibraryOverlay(
                onBack = onOverlayBack,
                modifier = modifier,
            )
            ParentOverlay.Events -> EventsOverlay(
                onBack = onOverlayBack,
                modifier = modifier,
            )
            ParentOverlay.Discovery -> DiscoveryOverlay(
                onBack = onOverlayBack,
                onOpenSchool = { overlay = ParentOverlay.SchoolDetail },
                modifier = modifier,
            )
            ParentOverlay.SchoolDetail -> SchoolDetailOverlay(
                onBack = onOverlayBack,
                modifier = modifier,
            )
            else -> ParentOverlayScaffold(
                title = overlayTitle,
                onBack = onOverlayBack,
                modifier = modifier,
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = overlayTitle,
                        style = VTypography.BodyLarge.copy(color = VColors.OnSurfaceVariant),
                    )
                }
            }
        }
        return@PremiumTheme
    }

    // ── Tab content ─────────────────────────────────────────────────────────
    val tabs = remember {
        listOf(
            NavItem("Home", icon = { TabIcon(Icons.Filled.Home) }),
            NavItem("Academics", icon = { TabIcon(Icons.Filled.School) }),
            NavItem("Fees", icon = { TabIcon(Icons.Filled.Payments) }),
            NavItem("Messages", icon = { TabIcon(Icons.AutoMirrored.Filled.Chat) }),
            NavItem("Profile", icon = { TabIcon(Icons.Filled.Person) }),
        )
    }

    Column(modifier = modifier.fillMaxSize().background(VColors.Surface)) {
        ParentHeader(
            greeting = state.greeting,
            childName = state.selectedChild?.name ?: "",
            unreadCount = notifState.unreadCount,
            onBellClick = { overlay = ParentOverlay.Notifications },
            onMessagesClick = { tab = 3 },
            modifier = Modifier.fillMaxWidth(),
        )

        // Layout safety Rule 5: weight(1f) content area, no fillMaxSize in scroll
        Box(Modifier.weight(1f).fillMaxWidth()) {
            AnimatedContent(
                targetState = tab,
                transitionSpec = { fadeIn(tween(300)).togetherWith(fadeOut(tween(200))) },
                label = "parentTab",
            ) { current ->
                when (current) {
                    0 -> ParentHomeScreen(
                        onOpenOverlay = { overlay = it },
                        onSwitchTab = { tab = it },
                    )
                    1 -> ParentAcademicsScreen(
                        onOpenOverlay = { overlay = it },
                        onSwitchTab = { tab = it },
                    )
                    2 -> ParentFeesScreen(
                        onOpenOverlay = { overlay = it },
                        onSwitchTab = { tab = it },
                    )
                    3 -> ParentConversationsScreen(
                        onOpenOverlay = { overlay = it },
                        onSwitchTab = { tab = it },
                    )
                    4 -> ParentProfileScreen(
                        onLogout = onLogout,
                        onOpenOverlay = { overlay = it },
                        onSwitchTab = { tab = it },
                    )
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
private fun ParentHeader(
    greeting: String,
    childName: String,
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
        Column(Modifier.weight(1f)) {
            Text(
                text = greeting.ifBlank { "Welcome" },
                style = VTypography.Eyebrow.copy(color = VColors.OnSurfaceVariant),
            )
            Text(
                text = childName.ifBlank { "Parent" },
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
            Icon(
                Icons.AutoMirrored.Filled.Chat,
                contentDescription = "Messages",
                tint = VColors.OnSurfaceVariant,
                modifier = Modifier.size(22.dp),
            )
        }

        // Notification bell
        val bellInteraction = remember { MutableInteractionSource() }
        Box(
            Modifier.size(40.dp).clip(CircleShape).background(VColors.SurfaceContainerHigh)
                .pressScale(bellInteraction, pressedScale = 0.9f)
                .clickable(interactionSource = bellInteraction, indication = null, onClick = onBellClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Notifications,
                contentDescription = "Notifications",
                tint = VColors.OnSurfaceVariant,
                modifier = Modifier.size(22.dp),
            )
            if (unreadCount > 0) {
                Box(
                    Modifier.align(Alignment.TopEnd)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(VColors.Error),
                )
            }
        }
    }
}

@Composable
private fun TabIcon(icon: ImageVector) {
    Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
}
