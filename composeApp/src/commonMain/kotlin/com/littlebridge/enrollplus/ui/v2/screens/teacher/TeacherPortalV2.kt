package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.parent.presentation.NotificationsViewModel
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherObligationsViewModel
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherProfileViewModel
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VNavItem
import com.littlebridge.enrollplus.ui.v2.components.VScreenScaffold
import com.littlebridge.enrollplus.ui.v2.navigation.DeepLinkTarget
import com.littlebridge.enrollplus.ui.v2.navigation.EntryRole
import com.littlebridge.enrollplus.ui.v2.navigation.parseDeepLink
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.screens.discovery.AcademicCalendarScreenV2
import com.littlebridge.enrollplus.ui.v2.screens.notifications.NotificationsScreenV2
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/** Full-screen overlays the teacher portal can push above its tab content. */
private enum class TeacherOverlay { None, Notifications, HealthAlerts, TransportAttendance, Pews, ReportReview, ReportDraftEditor, Heatmap, DigitalIdCard, ScheduledMessages, EventRegistration, Messages, Calendar }

/**
 * TeacherPortalV2 — the teacher shell, rebuilt FROM SCRATCH on the Parents-Portal
 * design language (lavender canvas, white rounded cards, Canvas rings, floating
 * dock, brand violet reserved for active/brand moments).
 *
 * New 4-tab IA (replacing the old Today/Classes/Gradebook/Planner/Profile):
 *
 *   HOME · UPDATE · CLASSES · PROFILE
 *
 *   • HOME    — time-sensitive greeting, first-login fingerprint check-in popup,
 *               attendance clubbed into DB-backed summary cards, today's schedule,
 *               assignments/tests/reminders. Swipe cards expand in place.
 *   • UPDATE  — the write plane (Attendance · Marks · Syllabus · Homework) with a
 *               class/section/subject scope gate. Reached pre-scoped from HOME CTAs
 *               or picked fresh from the gate.
 *   • CLASSES — rich roster plane: class list → composite class detail → scoped
 *               student-profile drill-down (all self-contained in the tab).
 *   • PROFILE — identity, leave apply/status, password, theme switch, logout.
 *
 * The signature `TeacherPortalV2(onLogout, modifier)` is PRESERVED — it is the only
 * external reference (NavGraphV2 line 309).
 *
 * Theme: the portal now renders on the shared cream/violet token system
 * (com.littlebridge.enrollplus.ui.tokens — VColors / VTypography / VShapes) via
 * the VtC / VtT bridge, so every tab and overlay inherits the same warm cream
 * canvas and deep-violet accent. No legacy VTheme wrapper is used.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TeacherPortalV2(
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier,
    deepLinkTarget: DeepLinkTarget? = null,
    profileViewModel: TeacherProfileViewModel = koinViewModel(),
    obligationsViewModel: TeacherObligationsViewModel = koinViewModel(),
    notificationsViewModel: NotificationsViewModel = koinViewModel(),
    preferenceRepository: PreferenceRepository = koinInject(),
) {
    var tab by remember { mutableStateOf("home") }
    var overlay by remember { mutableStateOf(TeacherOverlay.None) }
    var localDeepLink by remember { mutableStateOf<DeepLinkTarget?>(null) }
    var deepLinkThreadId by remember { mutableStateOf<String?>(null) }

    // AI Report Card — review queue parameters (declared before LaunchedEffect
    // so the deep-link handler can write to them).
    var reportClassName by remember { mutableStateOf("") }
    var reportSection by remember { mutableStateOf("A") }
    var reportTerm by remember { mutableStateOf("Term 1") }
    var reportDraftId by remember { mutableStateOf("") }

    // Apply deep-link routing: set tab from the typed target.
    LaunchedEffect(deepLinkTarget, localDeepLink) {
        val target = localDeepLink ?: deepLinkTarget ?: return@LaunchedEffect
        when (target) {
            is DeepLinkTarget.TeacherScreen -> {
                when (target.screen) {
                    "transport" -> overlay = TeacherOverlay.TransportAttendance
                    "report-card", "report-review" -> {
                        target.params["className"]?.let { reportClassName = it }
                        target.params["section"]?.let { reportSection = it }
                        target.params["term"]?.let { reportTerm = it }
                        overlay = TeacherOverlay.ReportReview
                    }
                    "tutor" -> overlay = TeacherOverlay.Heatmap
                    "events" -> overlay = TeacherOverlay.EventRegistration
                    "announcements" -> { tab = "home"; overlay = TeacherOverlay.None }
                    "leave-requests", "leave" -> { tab = "profile"; overlay = TeacherOverlay.None }
                    "library" -> { tab = "home"; overlay = TeacherOverlay.None }
                    "messages" -> overlay = TeacherOverlay.Messages
                    "timetable-requests" -> { tab = "timetable"; overlay = TeacherOverlay.None }
                    "calendar" -> overlay = TeacherOverlay.Calendar
                    // Valid bottom-nav tabs
                    "home", "update", "classes", "timetable", "profile" -> tab = target.screen
                    else -> tab = "home"
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
                    pathOnly.startsWith("announcements") -> { tab = "home"; overlay = TeacherOverlay.None }
                    pathOnly.startsWith("leave") -> { tab = "profile"; overlay = TeacherOverlay.None }
                    pathOnly.startsWith("transport") -> overlay = TeacherOverlay.TransportAttendance
                    pathOnly.startsWith("tutor") -> overlay = TeacherOverlay.Heatmap
                    pathOnly.startsWith("events") -> overlay = TeacherOverlay.EventRegistration
                    pathOnly.startsWith("calendar") -> overlay = TeacherOverlay.Calendar
                    pathOnly.startsWith("timetable-requests") -> { tab = "timetable"; overlay = TeacherOverlay.None }
                    pathOnly.startsWith("timetable") -> { tab = "timetable"; overlay = TeacherOverlay.None }
                    else -> tab = "home"
                }
            }
            else -> Unit
        }
        localDeepLink = null
    }

    // The UPDATE tab can be entered pre-scoped from a HOME CTA. These hold the
    // pre-authorized scope; a bump on [updateScopeNonce] forces the Update screen
    // to re-read its initial* values (so a fresh HOME tap re-seeds the gate).
    var updateAssignmentId by remember { mutableStateOf<String?>(null) }
    var updateScopeLabel by remember { mutableStateOf("") }
    var updateInitialTool by remember { mutableStateOf(UpdateTool.Attendance) }
    var updateScopeNonce by remember { mutableStateOf(0) }

    val profile by profileViewModel.state.collectAsStateV2()
    val obligations by obligationsViewModel.state.collectAsStateV2()
    val notifications by notificationsViewModel.state.collectAsStateV2()

    BackHandler(enabled = overlay != TeacherOverlay.None) {
        overlay = TeacherOverlay.None
    }
    // From a non-home tab, Back returns to HOME (familiar app behaviour).
    BackHandler(enabled = overlay == TeacherOverlay.None && tab != "home") {
        tab = "home"
    }

    // ── Overlays sit above all tab content ──────────────────────────────────
    when (overlay) {
        TeacherOverlay.Notifications -> {
            NotificationsScreenV2(
                onBack = { overlay = TeacherOverlay.None },
                onDeepLink = { deepLinkString ->
                    localDeepLink = parseDeepLink(deepLinkString, EntryRole.Teacher)
                    overlay = TeacherOverlay.None
                },
                modifier = modifier,
            )
            return
        }
        TeacherOverlay.HealthAlerts -> {
            TeacherHealthAlertsScreenV2(onBack = { overlay = TeacherOverlay.None }, modifier = modifier)
            return
        }
        TeacherOverlay.TransportAttendance -> {
            TransportAttendanceScreenV2(
                routeId = "",
                onBack = { overlay = TeacherOverlay.None },
                modifier = modifier,
            )
            return
        }
        TeacherOverlay.Pews -> {
            // PEWS — the teacher's own-class "students needing attention".
            TeacherPewsScreenV2(onBack = { overlay = TeacherOverlay.None }, modifier = modifier)
            return
        }
        TeacherOverlay.ReportReview -> {
            TeacherReportReviewQueueScreen(
                className = reportClassName,
                section = reportSection,
                term = reportTerm,
                onBack = { overlay = TeacherOverlay.None },
                onEditDraft = { draftId ->
                    reportDraftId = draftId
                    overlay = TeacherOverlay.ReportDraftEditor
                },
            )
            return
        }
        TeacherOverlay.ReportDraftEditor -> {
            TeacherReportDraftEditorScreen(
                draftId = reportDraftId,
                onBack = { overlay = TeacherOverlay.ReportReview },
                onSaved = { overlay = TeacherOverlay.ReportReview },
            )
            return
        }
        TeacherOverlay.Heatmap -> {
            com.littlebridge.enrollplus.ui.v2.screens.tutor.TeacherHeatmapScreen(
                onBack = { overlay = TeacherOverlay.None },
                modifier = modifier,
            )
            return
        }
        TeacherOverlay.DigitalIdCard -> {
            com.littlebridge.enrollplus.ui.v2.screens.parent.DigitalIdCardScreen(
                isTeacher = true,
                onBack = { overlay = TeacherOverlay.None },
                modifier = modifier,
            )
            return
        }
        TeacherOverlay.ScheduledMessages -> {
            com.littlebridge.enrollplus.ui.v2.screens.school.ScheduledMessagesScreenV2(
                onBack = { overlay = TeacherOverlay.None },
                modifier = modifier,
            )
            return
        }
        TeacherOverlay.EventRegistration -> {
            TeacherPtmEventRegistrationScreenV2(
                onBack = { overlay = TeacherOverlay.None },
                modifier = modifier,
            )
            return
        }
        TeacherOverlay.Messages -> {
            TeacherMessagesScreenV2(
                onBack = { overlay = TeacherOverlay.None; deepLinkThreadId = null },
                modifier = modifier,
                initialThreadId = deepLinkThreadId,
            )
            return
        }
        TeacherOverlay.Calendar -> {
            AcademicCalendarScreenV2(
                onBack = { overlay = TeacherOverlay.None },
                modifier = modifier,
            )
            return
        }
        TeacherOverlay.None -> Unit
    }

    // ── Dock items. The Update badge rides the LIVE obligation count (hidden at 0). ──
    val items = listOf(
        VNavItem("home", "Home", VIcons.Home),
        VNavItem("update", "Update", VIcons.Edit3, badge = obligations.totalOutstanding),
        VNavItem("classes", "Classes", VIcons.Users),
        VNavItem("timetable", "Timetable", VIcons.Calendar),
        VNavItem("profile", "Profile", VIcons.User),
    )

    // Canonical header identity (hidden on HOME — HOME renders its own greeting hero).
    val teacherName = profile.profile?.name.orEmpty()
    val schoolName = profile.profile?.schoolName.orEmpty()
    val photoUrl = profile.profile?.photoUrl
    val subline = when (tab) {
        "update" -> "Mark & publish"
        "classes" -> "Your classes & students"
        "timetable" -> "Your weekly timetable"
        "profile" -> schoolName.ifBlank { "Your account" }
        else -> schoolName
    }

    VScreenScaffold(
        modifier = modifier,
        topBar = {
            // HOME owns its own greeting hero, so the slim canonical header only
            // mounts on the other three tabs (no double chrome).
            if (tab != "home") {
                TeacherHeader(
                    teacherName = teacherName.ifBlank { "Teacher" },
                    subline = subline,
                    photoUrl = photoUrl,
                    unreadCount = notifications.unreadCount,
                    onOpenProfile = { tab = "profile" },
                    onOpenNotifications = { overlay = TeacherOverlay.Notifications },
                )
            }
        },
        bottomBar = {
            TeacherDock(items = items, selected = tab, onSelect = { tab = it })
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(bottom = padding.calculateBottomPadding())) {
            when (tab) {
                "home" -> TeacherHomeScreenV2(
                    onOpenAttendanceForAssignment = { assignmentId, scope ->
                        updateAssignmentId = assignmentId
                        updateScopeLabel = scope
                        updateInitialTool = UpdateTool.Attendance
                        updateScopeNonce++
                        tab = "update"
                    },
                    onOpenLessonPlanForAssignment = { assignmentId, scope ->
                        updateAssignmentId = assignmentId
                        updateScopeLabel = scope
                        updateInitialTool = UpdateTool.LessonPlan
                        updateScopeNonce++
                        tab = "update"
                    },
                    onOpenUpdateTab = {
                        // Fresh, unscoped entry → the Update gate picks a class.
                        updateAssignmentId = null
                        updateScopeLabel = ""
                        updateInitialTool = UpdateTool.Attendance
                        updateScopeNonce++
                        tab = "update"
                    },
                    onOpenUpdateTool = { tool ->
                        updateAssignmentId = null
                        updateScopeLabel = ""
                        updateInitialTool = tool
                        updateScopeNonce++
                        tab = "update"
                    },
                    onOpenClasses = { tab = "classes" },
                    onOpenHealthAlerts = { overlay = TeacherOverlay.HealthAlerts },
                    onOpenTransportAttendance = { overlay = TeacherOverlay.TransportAttendance },
                    onOpenPews = { overlay = TeacherOverlay.Pews },
                    onOpenReportReview = { overlay = TeacherOverlay.ReportReview },
                    onOpenHeatmap = { overlay = TeacherOverlay.Heatmap },
                    onOpenIdCard = { overlay = TeacherOverlay.DigitalIdCard },
                    onOpenScheduledMessages = { overlay = TeacherOverlay.ScheduledMessages },
                    onOpenEvents = { overlay = TeacherOverlay.EventRegistration },
                    onOpenMessages = { overlay = TeacherOverlay.Messages },
                    onOpenNotifications = { overlay = TeacherOverlay.Notifications },
                )

                "update" -> key(updateScopeNonce) {
                    TeacherUpdateScreenV2(
                        initialAssignmentId = updateAssignmentId,
                        initialScopeLabel = updateScopeLabel,
                        initialTool = updateInitialTool,
                    )
                }

                "classes" -> TeacherClassesScreenV2()

                "timetable" -> TeacherTimetableScreenV2()

                "profile" -> TeacherProfileScreenV2(onLogout = onLogout)
            }
        }
    }
}
