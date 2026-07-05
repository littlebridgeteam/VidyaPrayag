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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
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
import com.littlebridge.enrollplus.ui.v2.screens.premium.auth.ParentLinkChildScreen
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

/** Full-screen overlays the parent portal can push above its tab content. */
private enum class ParentOverlay {
    None, Notifications, Calendar, Scholarships, Leave, Messages,
    LinkChild, Discovery, Health, Pulse, Transport, TutorChat,
    TutorProgress, DigitalIdCard, Library, EventRegistration,
}

/**
 * Premium parent portal shell — 5-tab bottom navigation with full overlay routing.
 *
 * Tabs: Home · Academics · Fees · Conversations · Profile
 * Each tab renders its premium screen. The shell owns the header (child identity +
 * notification bell) and the bottom nav bar. Full-screen overlays are pushed for
 * notifications, calendar, scholarships, messages, health, transport, library, etc.
 * Deep-link routing sets tab + overlay from typed [DeepLinkTarget]s.
 */
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

    // Apply deep-link routing: set tab + overlay from the typed target.
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
                    "scholarships" -> { overlay = ParentOverlay.Scholarships; 0 }
                    "link-child" -> { overlay = ParentOverlay.LinkChild; 4 }
                    else -> 0
                }
                when (target.overlay) {
                    "leave" -> overlay = ParentOverlay.Leave
                    "messages" -> overlay = ParentOverlay.Messages
                    "notifications" -> overlay = ParentOverlay.Notifications
                    "calendar", "timetable" -> overlay = ParentOverlay.Calendar
                    "transport" -> overlay = ParentOverlay.Transport
                    "library" -> overlay = ParentOverlay.Library
                    "events" -> overlay = ParentOverlay.EventRegistration
                    "scholarships" -> overlay = ParentOverlay.Scholarships
                    "health" -> overlay = ParentOverlay.Health
                    "pulse" -> overlay = ParentOverlay.Pulse
                    "tutor" -> overlay = ParentOverlay.TutorChat
                    "tutor-progress" -> overlay = ParentOverlay.TutorProgress
                    "id-card", "digital-id" -> overlay = ParentOverlay.DigitalIdCard
                    "link-child" -> overlay = ParentOverlay.LinkChild
                    else -> overlay = ParentOverlay.None
                }
            }
            is DeepLinkTarget.Messages -> {
                deepLinkThreadId = target.threadId
                overlay = ParentOverlay.Messages
            }
            is DeepLinkTarget.Generic -> {
                val pathOnly = target.path.substringBefore("?").removePrefix("/")
                when {
                    pathOnly.startsWith("fees") -> { tab = 2; overlay = ParentOverlay.None }
                    pathOnly.startsWith("scholarships") -> { tab = 0; overlay = ParentOverlay.Scholarships }
                    pathOnly.startsWith("transport") -> { tab = 0; overlay = ParentOverlay.Transport }
                    pathOnly.startsWith("library") -> { tab = 0; overlay = ParentOverlay.Library }
                    pathOnly.startsWith("events") -> { tab = 0; overlay = ParentOverlay.EventRegistration }
                    pathOnly.startsWith("leave") -> { tab = 0; overlay = ParentOverlay.Leave }
                    pathOnly.startsWith("messages") -> { overlay = ParentOverlay.Messages }
                    pathOnly.startsWith("health") -> { tab = 0; overlay = ParentOverlay.Health }
                    pathOnly.startsWith("pulse") -> { tab = 0; overlay = ParentOverlay.Pulse }
                    pathOnly.startsWith("calendar") -> { tab = 0; overlay = ParentOverlay.Calendar }
                    pathOnly.startsWith("tutor-progress") -> { overlay = ParentOverlay.TutorProgress }
                    pathOnly.startsWith("tutor") -> { overlay = ParentOverlay.TutorChat }
                    pathOnly.startsWith("timetable") -> { overlay = ParentOverlay.Calendar }
                    pathOnly.startsWith("link-child") -> { tab = 4; overlay = ParentOverlay.LinkChild }
                    else -> { tab = 0; overlay = ParentOverlay.None }
                }
            }
            else -> Unit
        }
        localDeepLink = null
    }

    // Unlinked-parent gate: no children → focused link/explore screen.
    val hasResolved = !state.isLoading && state.error == null
    if (hasResolved && state.children.isEmpty()) {
        ParentUnlinkedScreen(
            onLinked = { dashboardViewModel.load() },
            modifier = modifier,
        )
        return@PremiumTheme
    }

    // Back handler: dismiss overlay first, then return to home tab.
    VBackHandler(enabled = overlay != ParentOverlay.None) {
        overlay = ParentOverlay.None
        deepLinkThreadId = null
    }
    VBackHandler(enabled = overlay == ParentOverlay.None && tab != 0) {
        tab = 0
    }

    // ── Overlays sit above all tab content ──────────────────────────────────
    when (overlay) {
        ParentOverlay.Notifications -> {
            ParentNotificationsScreen(
                onBack = { overlay = ParentOverlay.None },
                modifier = modifier,
            )
            return@PremiumTheme
        }
        ParentOverlay.Calendar -> {
            ParentCalendarScreen(
                onBack = { overlay = ParentOverlay.None },
                modifier = modifier,
            )
            return@PremiumTheme
        }
        ParentOverlay.Scholarships -> {
            ParentScholarshipScreen(
                onBack = { overlay = ParentOverlay.None },
                modifier = modifier,
            )
            return@PremiumTheme
        }
        ParentOverlay.Leave -> {
            ParentLeaveScreen(
                onBack = { overlay = ParentOverlay.None },
                modifier = modifier,
            )
            return@PremiumTheme
        }
        ParentOverlay.Messages -> {
            ParentMessagesScreen(
                onBack = { overlay = ParentOverlay.None; deepLinkThreadId = null },
                modifier = modifier,
                initialThreadId = deepLinkThreadId,
            )
            return@PremiumTheme
        }
        ParentOverlay.LinkChild -> {
            ParentLinkChildScreen(
                onDone = { overlay = ParentOverlay.None },
                onBack = { overlay = ParentOverlay.None },
                modifier = modifier,
            )
            return@PremiumTheme
        }
        ParentOverlay.Discovery -> {
            ParentDiscoveryScreen(
                onExit = { overlay = ParentOverlay.None },
                modifier = modifier,
            )
            return@PremiumTheme
        }
        ParentOverlay.Health -> {
            val child = state.selectedChild
            if (child == null) { overlay = ParentOverlay.None } else {
                ParentHealthScreen(
                    childId = child.id,
                    onBack = { overlay = ParentOverlay.None },
                    modifier = modifier,
                )
                return@PremiumTheme
            }
        }
        ParentOverlay.Pulse -> {
            ParentPulseScreen(
                onBack = { overlay = ParentOverlay.None },
                modifier = modifier,
            )
            return@PremiumTheme
        }
        ParentOverlay.Transport -> {
            val child = state.selectedChild
            if (child == null) { overlay = ParentOverlay.None } else {
                ParentTransportScreen(
                    childId = child.id,
                    onBack = { overlay = ParentOverlay.None },
                    modifier = modifier,
                )
                return@PremiumTheme
            }
        }
        ParentOverlay.TutorChat -> {
            ParentTutorChatScreen(
                onBack = { overlay = ParentOverlay.None },
                modifier = modifier,
            )
            return@PremiumTheme
        }
        ParentOverlay.TutorProgress -> {
            ParentTutorProgressScreen(
                onBack = { overlay = ParentOverlay.None },
                modifier = modifier,
            )
            return@PremiumTheme
        }
        ParentOverlay.DigitalIdCard -> {
            val child = state.selectedChild
            if (child == null) { overlay = ParentOverlay.None } else {
                ParentDigitalIdCardScreen(
                    childId = child.id,
                    onBack = { overlay = ParentOverlay.None },
                    modifier = modifier,
                )
                return@PremiumTheme
            }
        }
        ParentOverlay.Library -> {
            ParentLibraryScreen(
                onBack = { overlay = ParentOverlay.None },
                modifier = modifier,
            )
            return@PremiumTheme
        }
        ParentOverlay.EventRegistration -> {
            ParentEventsScreen(
                onBack = { overlay = ParentOverlay.None },
                modifier = modifier,
            )
            return@PremiumTheme
        }
        ParentOverlay.None -> Unit
    }

    val tabs = remember {
        listOf(
            NavItem("Home", icon = { NavIcon(Icons.Filled.Home) }),
            NavItem("Academics", icon = { NavIcon(Icons.Filled.School) }),
            NavItem("Fees", icon = { NavIcon(Icons.Filled.Payments) }),
            NavItem("Messages", icon = { NavIcon(Icons.Filled.Message) }, badgeCount = notifState.unreadCount),
            NavItem("Profile", icon = { NavIcon(Icons.Filled.AccountCircle) }),
        )
    }

    Column(
        modifier = modifier.fillMaxSize().background(VColors.Surface),
    ) {
        // Header
        ParentHeader(
            greeting = state.greeting,
            childName = state.selectedChild?.name,
            childInitial = state.selectedChild?.name?.firstOrNull()?.toString() ?: "",
            unreadCount = notifState.unreadCount,
            onBellClick = { overlay = ParentOverlay.Notifications },
            onMessagesClick = { overlay = ParentOverlay.Messages },
            modifier = Modifier.fillMaxWidth(),
        )

        // Tab content
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            AnimatedContent(
                targetState = tab,
                transitionSpec = {
                    fadeIn(tween(300)).togetherWith(fadeOut(tween(200)))
                },
                label = "parentTab",
            ) { current ->
                when (current) {
                    0 -> ParentHomeScreen(
                        onOpenPulse = { overlay = ParentOverlay.Pulse },
                        onOpenTransport = { overlay = ParentOverlay.Transport },
                        onOpenNotifications = { overlay = ParentOverlay.Notifications },
                        onSwitchTab = { tab = it },
                    )
                    1 -> ParentAcademicsScreen(
                        onOpenLeave = { overlay = ParentOverlay.Leave },
                        onOpenHealth = { overlay = ParentOverlay.Health },
                    )
                    2 -> ParentFeesScreen()
                    3 -> ParentConversationsScreen(
                        onOpenThread = { threadId -> overlay = ParentOverlay.Messages },
                    )
                    4 -> ParentProfileScreen(
                        onLogout = {
                            overlay = ParentOverlay.None
                            deepLinkThreadId = null
                            onLogout()
                        },
                        onLinkChild = { overlay = ParentOverlay.LinkChild },
                    )
                }
            }
        }

        // Bottom nav
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
    childName: String?,
    childInitial: String,
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
        // Child avatar
        Box(
            Modifier.size(40.dp).clip(CircleShape).background(VColors.PrimaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            if (childInitial.isNotEmpty()) {
                Text(childInitial, style = VTypography.SectionHeader.copy(color = VColors.OnPrimaryContainer))
            } else {
                Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = VColors.OnPrimaryContainer, modifier = Modifier.size(24.dp))
            }
        }
        Column(Modifier.weight(1f)) {
            if (greeting.isNotBlank()) {
                Text(greeting, style = VTypography.Eyebrow.copy(color = VColors.OnSurfaceVariant))
            }
            Text(
                childName ?: "Loading...",
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
            Icon(Icons.Filled.Message, contentDescription = "Messages", tint = VColors.OnSurfaceVariant, modifier = Modifier.size(22.dp))
            if (unreadCount > 0) {
                Box(
                    Modifier.align(Alignment.TopEnd).size(8.dp).clip(CircleShape).background(VColors.Error),
                )
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
                Box(
                    Modifier.align(Alignment.TopEnd).size(8.dp).clip(CircleShape).background(VColors.Error),
                )
            }
        }
    }
}

@Composable
private fun NavIcon(icon: ImageVector) {
    Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
}
