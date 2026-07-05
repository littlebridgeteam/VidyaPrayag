package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.notification.presentation.NotificationsViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHandler
import com.littlebridge.enrollplus.ui.v2.components.navigation.NavItem
import com.littlebridge.enrollplus.ui.v2.components.navigation.VBottomNav
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.navigation.DeepLinkTarget
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.screens.premium.auth.ParentLinkChildScreen
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
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
            NavItem("Messages", icon = { NavIcon(Icons.AutoMirrored.Filled.Message) }, badgeCount = notifState.unreadCount),
            NavItem("Profile", icon = { NavIcon(Icons.Filled.AccountCircle) }),
        )
    }

    var fabExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize().background(VColors.Surface),
    ) {
        // Top bar
        ParentTopBar(
            title = "VidyaSetu",
            unreadCount = notifState.unreadCount,
            onMenuClick = { },
            onSearchClick = { },
            onBellClick = { overlay = ParentOverlay.Notifications },
        )

        // Search field
        ParentSearchField(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            childInitial = state.selectedChild?.name?.firstOrNull()?.toString() ?: "P",
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

            // FAB with expandable menu
            FabMenu(
                expanded = fabExpanded,
                onToggle = { fabExpanded = !fabExpanded },
                onAiTutor = { fabExpanded = false; overlay = ParentOverlay.TutorChat },
                onMessageTeacher = { fabExpanded = false; overlay = ParentOverlay.Messages },
                onApplyLeave = { fabExpanded = false; overlay = ParentOverlay.Leave },
            )
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
private fun ParentTopBar(
    title: String,
    unreadCount: Int,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onBellClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(VColors.Surface)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Menu icon button
        IconButton44(onClick = onMenuClick) {
            Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = VColors.OnSurfaceVariant, modifier = Modifier.size(24.dp))
        }
        // Title
        Text(title, style = VTypography.TopBarTitle.copy(color = VColors.OnSurface), modifier = Modifier.weight(1f))
        // Search icon button
        IconButton44(onClick = onSearchClick) {
            Icon(Icons.Filled.Search, contentDescription = "Search", tint = VColors.OnSurfaceVariant, modifier = Modifier.size(24.dp))
        }
        // Notification bell
        IconButton44(onClick = onBellClick) {
            Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = VColors.OnSurfaceVariant, modifier = Modifier.size(24.dp))
            if (unreadCount > 0) {
                Box(
                    Modifier.align(Alignment.TopEnd).offset(x = 2.dp, y = 2.dp)
                        .size(10.dp).clip(CircleShape)
                        .background(VColors.Error)
                        .border(2.dp, VColors.Surface, CircleShape),
                )
            }
        }
    }
}

@Composable
private fun IconButton44(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable androidx.compose.foundation.layout.BoxScope.() -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .pressScale(interaction, pressedScale = 0.9f)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

@Composable
private fun ParentSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    childInitial: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 0.dp)
            .padding(bottom = 16.dp)
            .clip(VShapes.Full)
            .background(VColors.SurfaceContainerHigh)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(Icons.Filled.Search, contentDescription = null, tint = VColors.OnSurfaceVariant, modifier = Modifier.size(22.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            textStyle = VTypography.SearchInput.copy(color = VColors.OnSurface),
            cursorBrush = SolidColor(VColors.Primary),
            singleLine = true,
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text("Search students, fees, events…", style = VTypography.SearchInput.copy(color = VColors.OnSurfaceVariant))
                }
                inner()
            },
        )
        // Search avatar
        Box(
            Modifier.size(36.dp).clip(CircleShape)
                .background(Brush.linearGradient(listOf(VColors.Primary, VColors.PrimaryFixedDim))),
            contentAlignment = Alignment.Center,
        ) {
            Text(childInitial, style = VTypography.SchoolOptionLogo.copy(color = VColors.OnPrimary))
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.BoxScope.FabMenu(
    expanded: Boolean,
    onToggle: () -> Unit,
    onAiTutor: () -> Unit,
    onMessageTeacher: () -> Unit,
    onApplyLeave: () -> Unit,
) {
    Box(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 20.dp, bottom = 20.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Menu items
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(tween(VMotion.DurMedium2)) + slideInVertically(tween(VMotion.DurMedium2)) { it / 2 },
                exit = fadeOut(tween(VMotion.DurMedium2)) + slideOutVertically(tween(VMotion.DurMedium2)) { it / 2 },
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    FabMenuItem(icon = Icons.Filled.School, label = "AI Tutor", onClick = onAiTutor)
                    FabMenuItem(icon = Icons.AutoMirrored.Filled.Message, label = "Message Teacher", onClick = onMessageTeacher)
                    FabMenuItem(icon = Icons.Filled.CalendarToday, label = "Apply Leave", onClick = onApplyLeave)
                }
            }
            // FAB button
            val fabInteraction = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(if (expanded) CircleShape else VShapes.Xl)
                    .background(VColors.Primary)
                    .shadow(8.dp, if (expanded) CircleShape else VShapes.Xl)
                    .pressScale(fabInteraction, pressedScale = 0.92f)
                    .clickable(interactionSource = fabInteraction, indication = null, onClick = onToggle),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (expanded) Icons.Filled.Close else Icons.Filled.Add,
                    contentDescription = "FAB",
                    tint = VColors.OnPrimary,
                    modifier = Modifier.size(26.dp),
                )
            }
        }
    }
}

@Composable
private fun FabMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLowest)
            .shadow(4.dp, VShapes.Lg)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(icon, contentDescription = null, tint = VColors.Primary, modifier = Modifier.size(22.dp))
        Text(label, style = VTypography.ButtonText.copy(color = VColors.OnSurface))
    }
}

@Composable
private fun NavIcon(icon: ImageVector) {
    Icon(icon, contentDescription = null, modifier = Modifier.size(26.dp))
}
