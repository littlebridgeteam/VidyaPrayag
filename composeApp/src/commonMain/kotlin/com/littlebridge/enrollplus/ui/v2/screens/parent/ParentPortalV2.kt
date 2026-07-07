package com.littlebridge.enrollplus.ui.v2.screens.parent

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.parent.presentation.NotificationsViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.ParentMessageViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.TrackProgressViewModel
import com.littlebridge.enrollplus.ui.v2.components.VAvatar
import com.littlebridge.enrollplus.ui.v2.components.VDivider
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VNavItem
import com.littlebridge.enrollplus.ui.v2.components.VScreenScaffold
import com.littlebridge.enrollplus.ui.v2.components.VStatusDot
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.navigation.DeepLinkTarget
import com.littlebridge.enrollplus.ui.v2.navigation.EntryRole
import com.littlebridge.enrollplus.ui.v2.navigation.parseDeepLink
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.screens.auth.ParentLinkChildScreenV2
import com.littlebridge.enrollplus.ui.v2.screens.discovery.AcademicCalendarScreenV2
import com.littlebridge.enrollplus.ui.v2.screens.discovery.DiscoveryScreenV2
import com.littlebridge.enrollplus.ui.v2.screens.notifications.NotificationsScreenV2
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import org.koin.core.qualifier.named
import com.littlebridge.enrollplus.ui.v2.theme.colored
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.ui.v2.screens.parent.ParentHomeScreenV2

/** Full-screen overlays a portal can push above its tab content (back returns to the tabs). */
private enum class ParentOverlay { None, Notifications, Calendar, Scholarships, Profile, Leave, Messages, LinkChild, Discovery, Health, Pulse, Transport, TutorChat, TutorProgress, DigitalIdCard, Library, EventRegistration, FeePayment, FeeHistory }

/**
 * ParentPortalV2 — the 5-tab parent shell, a faithful copy of `Parent.tsx → ParentApp`.
 *
 * Owns the header (child identity from the real [TrackProgressViewModel]) and the bottom nav
 * (Home · Academics · Fees · Conversations · Profile). Each leaf is now wired to its own real
 * ViewModel via `koinViewModel()` — no MockV2 in any production path. Notifications & Calendar are
 * pushed as full-screen overlays; the flagship collectible player card lives on the Profile tab.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ParentPortalV2(
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier,
    deepLinkTarget: DeepLinkTarget? = null,
    // RA-PP-FIX: child identity in the shared header now comes from the real
    // /parent/dashboard child_summary (the single source of truth the rest of the
    // dashboard already uses), NOT from /track-progress — which never returned a
    // child name and used to crash on the EI field. `headerViewModel` (track-progress)
    // still supplies the holistic level/journey copy as a graceful enrichment.
    dashboardViewModel: ParentDashboardViewModel = koinViewModel(),
    headerViewModel: TrackProgressViewModel = koinViewModel(),
    // RA-S06: drives the header bell's unread dot from the real notifications feed.
    notificationsViewModel: NotificationsViewModel = koinViewModel(),
    // Shared with ParentConversationsScreenV2 so the portal can hide the floating
    // dock when a conversation or compose-new is open (WhatsApp pattern).
    messageViewModel: ParentMessageViewModel = koinViewModel(),
) {
    var tab by remember { mutableStateOf("home") }
    var overlay by remember { mutableStateOf(ParentOverlay.None) }
    var localDeepLink by remember { mutableStateOf<DeepLinkTarget?>(null) }
    var deepLinkThreadId by remember { mutableStateOf<String?>(null) }
    var deepLinkAcademicsTab by remember { mutableStateOf<String?>(null) }
    var deepLinkSegment by remember { mutableStateOf<ConversationsSegment?>(null) }
    var deepLinkReportDraftId by remember { mutableStateOf<String?>(null) }

    // Apply deep-link routing: set tab + overlay from the typed target.
    LaunchedEffect(deepLinkTarget, localDeepLink) {
        val target = localDeepLink ?: deepLinkTarget ?: return@LaunchedEffect
        when (target) {
            is DeepLinkTarget.ParentTab -> {
                // Map deep link tab to a valid bottom-nav tab.
                // Tabs that don't exist as bottom-nav items are redirected to
                // the closest valid tab, with an overlay if applicable.
                tab = when (target.tab) {
                    "home", "academics", "fees", "conversations", "profile" -> target.tab
                    "scholarships" -> { overlay = ParentOverlay.Scholarships; "home" }
                    "link-child" -> { overlay = ParentOverlay.LinkChild; "profile" }
                    else -> "home"
                }
                // Map overlay string to ParentOverlay enum.
                when (target.overlay) {
                    "leave" -> overlay = ParentOverlay.Leave
                    "messages" -> overlay = ParentOverlay.Messages
                    "notifications" -> overlay = ParentOverlay.Notifications
                    "calendar" -> overlay = ParentOverlay.Calendar
                    "transport" -> overlay = ParentOverlay.Transport
                    "library" -> overlay = ParentOverlay.Library
                    "events" -> overlay = ParentOverlay.EventRegistration
                    "announcements" -> { tab = "conversations"; overlay = ParentOverlay.None; deepLinkSegment = ConversationsSegment.Announcements }
                    "report-card" -> { tab = "academics"; overlay = ParentOverlay.None; deepLinkAcademicsTab = "Report"; target.params["draftId"]?.let { deepLinkReportDraftId = it } }
                    "tutor" -> { overlay = ParentOverlay.TutorChat }
                    "timetable" -> { overlay = ParentOverlay.Calendar }
                    "marks" -> { tab = "academics"; overlay = ParentOverlay.None; deepLinkAcademicsTab = "Marks" }
                    "attendance" -> { tab = "academics"; overlay = ParentOverlay.None; deepLinkAcademicsTab = "Attendance" }
                    "homework" -> { tab = "academics"; overlay = ParentOverlay.None; deepLinkAcademicsTab = "Homework" }
                    "quizzes" -> { tab = "academics"; overlay = ParentOverlay.None; deepLinkAcademicsTab = "Quizzes" }
                    "syllabus" -> { tab = "academics"; overlay = ParentOverlay.None; deepLinkAcademicsTab = "Syllabus" }
                    "scholarships" -> overlay = ParentOverlay.Scholarships
                    "health" -> overlay = ParentOverlay.Health
                    "pulse" -> overlay = ParentOverlay.Pulse
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
                // Try to extract a meaningful overlay from the path.
                val pathOnly = target.path.substringBefore("?").removePrefix("/")
                when {
                    pathOnly.startsWith("announcements") -> { tab = "conversations"; overlay = ParentOverlay.None; deepLinkSegment = ConversationsSegment.Announcements }
                    pathOnly.startsWith("fees") -> { tab = "fees"; overlay = ParentOverlay.None }
                    pathOnly.startsWith("scholarships") -> { tab = "home"; overlay = ParentOverlay.Scholarships }
                    pathOnly.startsWith("transport") -> { tab = "home"; overlay = ParentOverlay.Transport }
                    pathOnly.startsWith("library") -> { tab = "home"; overlay = ParentOverlay.Library }
                    pathOnly.startsWith("events") -> { tab = "home"; overlay = ParentOverlay.EventRegistration }
                    pathOnly.startsWith("leave") -> { tab = "home"; overlay = ParentOverlay.Leave }
                    pathOnly.startsWith("messages") -> { overlay = ParentOverlay.Messages }
                    pathOnly.startsWith("health") -> { tab = "home"; overlay = ParentOverlay.Health }
                    pathOnly.startsWith("pulse") -> { tab = "home"; overlay = ParentOverlay.Pulse }
                    pathOnly.startsWith("calendar") -> { tab = "home"; overlay = ParentOverlay.Calendar }
                    pathOnly.startsWith("report-card") -> { tab = "academics"; overlay = ParentOverlay.None; deepLinkAcademicsTab = "Report" }
                    pathOnly.startsWith("tutor") -> { overlay = ParentOverlay.TutorChat }
                    pathOnly.startsWith("timetable") -> { overlay = ParentOverlay.Calendar }
                    pathOnly.startsWith("marks") -> { tab = "academics"; overlay = ParentOverlay.None; deepLinkAcademicsTab = "Marks" }
                    pathOnly.startsWith("attendance") -> { tab = "academics"; overlay = ParentOverlay.None; deepLinkAcademicsTab = "Attendance" }
                    pathOnly.startsWith("homework") -> { tab = "academics"; overlay = ParentOverlay.None; deepLinkAcademicsTab = "Homework" }
                    pathOnly.startsWith("syllabus") -> { tab = "academics"; overlay = ParentOverlay.None; deepLinkAcademicsTab = "Syllabus" }
                    pathOnly.startsWith("link-child") -> { tab = "profile"; overlay = ParentOverlay.LinkChild }
                }
            }
            else -> Unit
        }
        localDeepLink = null
    }
    val dashboard by dashboardViewModel.state.collectAsStateV2()
    val progress by headerViewModel.state.collectAsStateV2()
    val notifications by notificationsViewModel.state.collectAsStateV2()
    val messageState by messageViewModel.state.collectAsStateV2()

    // ── Unlinked-parent gate ────────────────────────────────────────────────────
    // A parent with NO child linked yet shouldn't land in the 5-tab portal where every tab is an
    // empty state. Once the dashboard has resolved (not loading, no error) and reports zero
    // children, we hand off to the focused two-tab landing — Link a child / Explore schools.
    // (While the very first load is still in flight we fall through to the portal's own skeletons,
    // so the unlinked screen never flashes for an existing parent.)
    val hasResolved = !dashboard.isLoading && dashboard.error == null
    if (hasResolved && dashboard.children.isEmpty()) {
        ParentUnlinkedScreenV2(
            // After a successful link request the dashboard reloads — once the school approves and
            // a child appears, this gate falls through to the full portal automatically.
            onLinked = { dashboardViewModel.load() },
            modifier = modifier,
        )
        return
    }

    // §11 cross-platform — predictive back / edge-swipe dismisses the full-screen overlay back to
    // the tabs, not the portal.
    BackHandler(enabled = overlay != ParentOverlay.None) {
        overlay = ParentOverlay.None
    }

    when (overlay) {
        ParentOverlay.Notifications -> {
            NotificationsScreenV2(
                onBack = { overlay = ParentOverlay.None },
                onDeepLink = { deepLinkString ->
                    localDeepLink = parseDeepLink(deepLinkString, EntryRole.Parent)
                    overlay = ParentOverlay.None
                },
                modifier = modifier,
            )
            return
        }
        ParentOverlay.Calendar -> {
            AcademicCalendarScreenV2(onBack = { overlay = ParentOverlay.None }, modifier = modifier, viewModelQualifier = named("parentCalendar"))
            return
        }
        ParentOverlay.Scholarships -> {
            ScholarshipWorkflowScreenV2(onBack = { overlay = ParentOverlay.None }, modifier = modifier)
            return
        }
        ParentOverlay.Profile -> {
            // §7 finding K — the avatar opens the real profile screen; logout now lives
            // on an explicit button inside it (no more "tap your photo = logout").
            // RA-S04 (directive): linking a child is opt-in from the profile, never forced
            // after signup/login — the "Linked children" row opens the link flow as an overlay.
            ParentProfileScreenV2(
                onBack = { overlay = ParentOverlay.None },
                onLogout = onLogout,
                onLinkChild = { overlay = ParentOverlay.LinkChild },
                onDiscoverSchools = { overlay = ParentOverlay.Discovery },
                modifier = modifier,
            )
            return
        }
        ParentOverlay.LinkChild -> {
            // RA-S04 (directive): parent-initiated child linking — reached only from the
            // Profile screen, never pushed automatically after auth. Done/back both return
            // to the tabs (the link request is a PENDING admin approval server-side).
            ParentLinkChildScreenV2(
                onDone = { overlay = ParentOverlay.None },
                onBack = { overlay = ParentOverlay.Profile },
                modifier = modifier,
            )
            return
        }
        ParentOverlay.Leave -> {
            // RA-44: the parent leg of the leave workflow.
            ParentLeaveScreenV2(onBack = { overlay = ParentOverlay.None }, modifier = modifier)
            return
        }
        ParentOverlay.Messages -> {
            // RA-51: parent ↔ teacher/admin messaging.
            ParentMessagesScreenV2(
                onBack = { overlay = ParentOverlay.None; deepLinkThreadId = null },
                modifier = modifier,
                initialThreadId = deepLinkThreadId,
            )
            return
        }
        ParentOverlay.Discovery -> {
            // Marketplace browsing for AUTHENTICATED parents — the same DiscoveryScreenV2 that
            // serves the unauth flow, hosted as a portal overlay. Reached from the Profile's
            // "Discover schools" row and Home's "View all" featured-schools link. The header
            // "Exit" + system back both pop the overlay back to the tabs.
            DiscoveryScreenV2(
                onExit = { overlay = ParentOverlay.None },
                modifier = modifier,
            )
            return
        }
        ParentOverlay.Health -> {
            val child = dashboard.selectedChild
            if (child == null) { overlay = ParentOverlay.None; return }
            ParentHealthScreenV2(
                childId = child.id,
                onBack = { overlay = ParentOverlay.None },
                modifier = modifier,
            )
            return
        }
        ParentOverlay.Pulse -> {
            ParentPulseScreen(onBack = { overlay = ParentOverlay.None }, modifier = modifier)
            return
        }
        ParentOverlay.Transport -> {
            val child = dashboard.selectedChild
            if (child == null) { overlay = ParentOverlay.None; return }
            BusTrackingScreenV2(
                childId = child.id,
                onBack = { overlay = ParentOverlay.None },
                modifier = modifier,
            )
            return
        }
        ParentOverlay.TutorChat -> {
            com.littlebridge.enrollplus.ui.v2.screens.tutor.TutorChatScreen(
                onBack = { overlay = ParentOverlay.None },
                modifier = modifier,
            )
            return
        }
        ParentOverlay.TutorProgress -> {
            com.littlebridge.enrollplus.ui.v2.screens.tutor.ParentProgressScreen(
                onBack = { overlay = ParentOverlay.None },
                modifier = modifier,
            )
            return
        }
        ParentOverlay.DigitalIdCard -> {
            val child = dashboard.selectedChild
            DigitalIdCardScreen(
                childId = child?.id,
                isTeacher = false,
                onBack = { overlay = ParentOverlay.None },
                modifier = modifier,
            )
            return
        }
        ParentOverlay.Library -> {
            ParentLibraryScreenV2(
                onBack = { overlay = ParentOverlay.None },
                modifier = modifier,
            )
            return
        }
        ParentOverlay.EventRegistration -> {
            ParentEventRegistrationScreenV2(
                onBack = { overlay = ParentOverlay.None },
                modifier = modifier,
            )
            return
        }
        ParentOverlay.FeePayment -> {
            ParentFeePaymentScreenV2(
                onBack = { overlay = ParentOverlay.None },
                modifier = modifier,
                onPay = { /* TODO: invoke real payment gateway via FeeViewModel */ },
            )
            return
        }
        ParentOverlay.FeeHistory -> {
            ParentFeeHistoryScreenV2(
                onBack = { overlay = ParentOverlay.None },
                modifier = modifier,
            )
            return
        }
        ParentOverlay.None -> Unit
    }

    val items = listOf(
        VNavItem("home", "Home", VIcons.HomePremium),
        VNavItem("academics", "Academics", VIcons.Academic),
        VNavItem("fees", "Fees", VIcons.WalletPremium),
        VNavItem("conversations", "Chat", VIcons.ChatPremium, badge = notifications.unreadCount),
        VNavItem("profile", "Profile", VIcons.UserPremium),
    )

    // The Parents Portal's signature premium FLOATING DOCK (ParentDock) — a detached glass
    // bar with a liquid violet active-lozenge. The shared VBottomNav stays in place for the
    // Admin/Teacher portals; this bespoke dock is exclusive to the parent experience.
    //
    // HIDDEN when the Conversations tab has an open thread or compose-new active —
    // the conversation/compose surface needs the full screen height for its compose bar
    // (WhatsApp pattern: no bottom nav inside a chat).
    val hideDock = tab == "conversations" &&
        (messageState.openThreadId != null || messageState.composeOpen)

    VScreenScaffold(
        modifier = modifier,
        bottomBar = if (!hideDock) {
            {
                ParentDock(
                    items = items,
                    selected = tab,
                    onSelect = { tab = it },
                )
            }
        } else {
            null
        },
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            when (tab) {
                "home" -> ParentHomeScreenV2(
                    onDiscoverSchools = { overlay = ParentOverlay.Discovery },
                    onOpenNotifications = { overlay = ParentOverlay.Notifications },
                    onOpenFees = { tab = "fees" },
                    onOpenAcademics = { tab = "academics" },
                    onOpenMessages = { overlay = ParentOverlay.Messages },
                    onOpenPulse = { overlay = ParentOverlay.Pulse },
                    onOpenTransport = { overlay = ParentOverlay.Transport },
                    onOpenTutor = { overlay = ParentOverlay.TutorChat },
                    onOpenScholarships = { overlay = ParentOverlay.Scholarships },
                    onOpenIdCard = { overlay = ParentOverlay.DigitalIdCard },
                    onOpenLibrary = { overlay = ParentOverlay.Library },
                    onOpenEvents = { overlay = ParentOverlay.EventRegistration },
                    unreadNotificationsCount = notifications.unreadCount,
                )
                "academics" -> ParentAcademicsScreenV2(
                    parentName = progress.accountName,
                    children = dashboard.children,
                    selectedChild = dashboard.selectedChild,
                    onSelectChild = { dashboardViewModel.selectChild(it) },
                    onOpenLeave = { overlay = ParentOverlay.Leave },
                    onOpenHealth = { overlay = ParentOverlay.Health },
                    onOpenNotifications = { overlay = ParentOverlay.Notifications },
                    initialTab = deepLinkAcademicsTab,
                    onTabConsumed = { deepLinkAcademicsTab = null },
                    initialReportDraftId = deepLinkReportDraftId,
                    onReportDraftIdConsumed = { deepLinkReportDraftId = null },
                    unreadNotificationsCount = notifications.unreadCount,
                )
                "fees" -> ParentFeesScreenV2(
                    parentName = progress.accountName,
                    children = dashboard.children,
                    selectedChild = dashboard.selectedChild,
                    onSelectChild = { dashboardViewModel.selectChild(it) },
                    onOpenNotifications = { overlay = ParentOverlay.Notifications },
                    unreadNotificationsCount = notifications.unreadCount,
                    onPayNow = { overlay = ParentOverlay.FeePayment },
                    onFeeHistory = { overlay = ParentOverlay.FeeHistory },
                )
                // Phase 3 (commit 9): the Conversations hub — messaging-first, announcements second.
                "conversations" -> ParentConversationsScreenV2(
                    parentName = progress.accountName,
                    children = dashboard.children,
                    selectedChild = dashboard.selectedChild,
                    onSelectChild = { dashboardViewModel.selectChild(it) },
                    onOpenNotifications = { overlay = ParentOverlay.Notifications },
                    unreadNotificationsCount = notifications.unreadCount,
                    messageViewModel = messageViewModel,
                    initialSegment = deepLinkSegment,
                    onSegmentConsumed = { deepLinkSegment = null },
                )
                // Phase 4 (commits 10–11): the flagship collectible player card, with a
                // swipe-down account-options reveal (logout / link child / discover schools).
                "profile" -> ParentProfileCardScreenV2(
                    parentName = progress.accountName,
                    children = dashboard.children,
                    selectedChild = dashboard.selectedChild,
                    onSelectChild = { dashboardViewModel.selectChild(it) },
                    onLogout = onLogout,
                    onLinkChild = { overlay = ParentOverlay.LinkChild },
                    onDiscoverSchools = { overlay = ParentOverlay.Discovery },
                    onOpenAccountSettings = { overlay = ParentOverlay.Profile },
                )
            }
        }
    }
}

