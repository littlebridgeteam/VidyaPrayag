package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.parent.presentation.NotificationsViewModel
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherObligationsViewModel
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherProfileViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.components.VBackOnlineBanner
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VNavItem
import com.littlebridge.enrollplus.ui.v2.components.VOfflineBanner
import com.littlebridge.enrollplus.ui.v2.components.VScreenScaffold
import com.littlebridge.enrollplus.ui.v2.navigation.DeepLinkTarget
import com.littlebridge.enrollplus.ui.v2.navigation.EntryRole
import com.littlebridge.enrollplus.ui.v2.navigation.parseDeepLink
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.screens.discovery.AcademicCalendarScreenV2
import com.littlebridge.enrollplus.ui.v2.screens.notifications.NotificationsScreenV2
import com.littlebridge.enrollplus.ui.v2.screens.notifications.NotificationPreferencesScreenV2
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.util.AnalyticsTracker

/** Full-screen overlays the teacher portal can push above its tab content. */
private enum class TeacherOverlay { None, Notifications, NotificationPreferences, HealthAlerts, TransportAttendance, Pews, ReportReview, ReportDraftEditor, Heatmap, DigitalIdCard, ScheduledMessages, EventRegistration, Messages, Calendar, AnnouncementList, AnnouncementDetail, LeaveRequests, ExamTimetableList, ExamTimetableUpload, ExamTimetableDetail, ExamSyllabusMapping, ExamMarksImport, Export, SalaryHistory }

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

    // Deep-link-driven state for timetable segment + announcement detail.
    var showRequestsSegment by remember { mutableStateOf(false) }
    var announcementId by remember { mutableStateOf<String?>(null) }

    // Exam ecosystem deep-link params.
    var examTimetableId by remember { mutableStateOf<String?>(null) }
    var examAssessmentId by remember { mutableStateOf<String?>(null) }

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
                    "announcements" -> {
                        announcementId = target.params["id"]
                        overlay = TeacherOverlay.AnnouncementDetail
                    }
                    "leave-requests", "leave" -> overlay = TeacherOverlay.LeaveRequests
                    "library" -> { tab = "home"; overlay = TeacherOverlay.None }
                    "messages" -> overlay = TeacherOverlay.Messages
                    "timetable-requests" -> { tab = "timetable"; showRequestsSegment = true; overlay = TeacherOverlay.None }
                    "timetable" -> { tab = "timetable"; showRequestsSegment = false; overlay = TeacherOverlay.None }
                    "calendar" -> overlay = TeacherOverlay.Calendar
                    "exam-timetable" -> overlay = TeacherOverlay.ExamTimetableList
                    "exam-syllabus" -> {
                        examAssessmentId = target.params["assessmentId"]
                        overlay = TeacherOverlay.ExamSyllabusMapping
                    }
                    "export" -> overlay = TeacherOverlay.Export
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
                    pathOnly.startsWith("announcements") -> {
                        val queryStr = target.path.substringAfter("?", "")
                        announcementId = queryStr.substringAfter("id=", "").substringBefore("&").takeIf { it.isNotBlank() }
                        overlay = TeacherOverlay.AnnouncementDetail
                    }
                    pathOnly.startsWith("leave") -> overlay = TeacherOverlay.LeaveRequests
                    pathOnly.startsWith("transport") -> overlay = TeacherOverlay.TransportAttendance
                    pathOnly.startsWith("tutor") -> overlay = TeacherOverlay.Heatmap
                    pathOnly.startsWith("events") -> overlay = TeacherOverlay.EventRegistration
                    pathOnly.startsWith("calendar") -> overlay = TeacherOverlay.Calendar
                    pathOnly.startsWith("exam-timetable") -> overlay = TeacherOverlay.ExamTimetableList
                    pathOnly.startsWith("exam-syllabus") -> {
                        val queryStr = target.path.substringAfter("?", "")
                        examAssessmentId = queryStr.substringAfter("assessmentId=", "").substringBefore("&").takeIf { it.isNotBlank() }
                        overlay = TeacherOverlay.ExamSyllabusMapping
                    }
                    pathOnly.startsWith("export") -> overlay = TeacherOverlay.Export
                    pathOnly.startsWith("timetable-requests") -> { tab = "timetable"; showRequestsSegment = true; overlay = TeacherOverlay.None }
                    pathOnly.startsWith("timetable") -> { tab = "timetable"; showRequestsSegment = false; overlay = TeacherOverlay.None }
                    else -> tab = "home"
                }
            }
            else -> Unit
        }
        localDeepLink = null
    }

    // Track teacher tab screen views
    LaunchedEffect(tab) {
        val screenName = "teacher_$tab"
        AnalyticsTracker.setCurrentScreenName(screenName)
        AnalyticsTracker.event("vp_screen_viewed", mapOf(
            "screen" to screenName,
            "portal" to "teacher",
        ))
    }

    // Track teacher overlay screen views
    LaunchedEffect(overlay) {
        if (overlay != TeacherOverlay.None) {
            val screenName = "teacher_${overlay.name.lowercase()}"
            AnalyticsTracker.setCurrentScreenName(screenName)
            AnalyticsTracker.event("vp_screen_viewed", mapOf(
                "screen" to screenName,
                "portal" to "teacher",
            ))
        }
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
    // At root (home + no overlay), consume back to prevent app exit.
    BackHandler(enabled = overlay == TeacherOverlay.None && tab == "home") {
        // No-op — prevents the system back gesture from killing the app.
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
                onOpenPreferences = { overlay = TeacherOverlay.NotificationPreferences },
                modifier = modifier,
            )
            return
        }
        TeacherOverlay.NotificationPreferences -> {
            NotificationPreferencesScreenV2(
                onBack = { overlay = TeacherOverlay.None },
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
        TeacherOverlay.AnnouncementList -> {
            TeacherAnnouncementListScreen(
                onBack = { overlay = TeacherOverlay.None },
                onOpenAnnouncement = { id ->
                    announcementId = id
                    overlay = TeacherOverlay.AnnouncementDetail
                },
            )
            return
        }
        TeacherOverlay.AnnouncementDetail -> {
            TeacherAnnouncementDetailScreen(
                announcementId = announcementId,
                onBack = { overlay = TeacherOverlay.None; announcementId = null },
                modifier = modifier,
            )
            return
        }
        TeacherOverlay.LeaveRequests -> {
            TeacherLeaveRequestsScreenV2(
                onBack = { overlay = TeacherOverlay.None },
                modifier = modifier,
            )
            return
        }
        TeacherOverlay.ExamTimetableList -> {
            com.littlebridge.enrollplus.ui.v2.screens.teacher.exam.ExamTimetableListScreen(
                onBack = { overlay = TeacherOverlay.None },
                onNew = { overlay = TeacherOverlay.ExamTimetableUpload },
                onOpenTimetable = { id ->
                    examTimetableId = id
                    overlay = TeacherOverlay.ExamTimetableDetail
                },
            )
            return
        }
        TeacherOverlay.ExamTimetableUpload -> {
            com.littlebridge.enrollplus.ui.v2.screens.teacher.exam.ExamTimetableUploadScreen(
                onBack = { overlay = TeacherOverlay.ExamTimetableList },
                onCreated = { id ->
                    examTimetableId = id
                    overlay = TeacherOverlay.ExamTimetableDetail
                },
            )
            return
        }
        TeacherOverlay.ExamTimetableDetail -> {
            com.littlebridge.enrollplus.ui.v2.screens.teacher.exam.ExamTimetableDetailScreen(
                timetableId = examTimetableId ?: "",
                onBack = { overlay = TeacherOverlay.ExamTimetableList },
                onMapSyllabus = { assessmentId ->
                    examAssessmentId = assessmentId
                    overlay = TeacherOverlay.ExamSyllabusMapping
                },
            )
            return
        }
        TeacherOverlay.ExamSyllabusMapping -> {
            com.littlebridge.enrollplus.ui.v2.screens.teacher.exam.ExamSyllabusMappingScreen(
                assessmentId = examAssessmentId ?: "",
                onBack = { overlay = TeacherOverlay.ExamTimetableDetail },
            )
            return
        }
        TeacherOverlay.ExamMarksImport -> {
            com.littlebridge.enrollplus.ui.v2.screens.teacher.exam.ExamMarksImportScreen(
                onBack = { overlay = TeacherOverlay.None },
            )
            return
        }
        TeacherOverlay.Export -> {
            com.littlebridge.enrollplus.ui.v2.screens.teacher.export.ExportScreen(
                onBack = { overlay = TeacherOverlay.None },
                modifier = modifier,
            )
            return
        }
        TeacherOverlay.SalaryHistory -> {
            TeacherSalaryOverlayScreen(
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

    // Shared identity — every tab's TeacherPremiumHeader greets with this name.
    val teacherName = profile.profile?.name.orEmpty()

    VScreenScaffold(
        modifier = modifier,
        // Every tab now renders the SAME shared TeacherPremiumHeader inside its own
        // scrolling content (Home · Update · Classes · Timetable · Profile), so the
        // portal shares one premium chrome and there is no separate top bar chrome.
        topBar = null,
        bottomBar = {
            TeacherDock(items = items, selected = tab, onSelect = {
                tab = it
                AnalyticsTracker.event("vp_teacher_tab_switch", mapOf("tab" to it))
            })
        },
    ) { _ ->
        // Paint the warm cream page canvas across the WHOLE tab area so the
        // lavender scaffold background never shows as a purple band behind the
        // floating dock. Each tab already reserves [TeacherDockClearance] at the
        // bottom of its own scroll content, so we intentionally do NOT re-apply
        // the scaffold's bottom inset here (that produced a double gap).
        Box(
            Modifier
                .fillMaxSize()
                .background(VColors.cream),
        ) {
            // Track offline→online transition for the "Back online" confirmation.
            val isPortalOffline = obligations.isOffline
            var wasOffline by remember { mutableStateOf(isPortalOffline) }
            var showBackOnline by remember { mutableStateOf(false) }
            LaunchedEffect(isPortalOffline) {
                if (wasOffline && !isPortalOffline) {
                    showBackOnline = true
                }
                wasOffline = isPortalOffline
            }
            LaunchedEffect(showBackOnline) {
                if (showBackOnline) {
                    kotlinx.coroutines.delay(2500L)
                    showBackOnline = false
                }
            }

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
                    onOpenLeaveRequests = { overlay = TeacherOverlay.LeaveRequests },
                    onOpenHealthAlerts = { overlay = TeacherOverlay.HealthAlerts },
                    onOpenTransportAttendance = { overlay = TeacherOverlay.TransportAttendance },
                    onOpenPews = { overlay = TeacherOverlay.Pews },
                    onOpenReportReview = {
                        if (reportClassName.isBlank()) {
                            reportClassName = profile.profile?.classes?.firstOrNull() ?: ""
                        }
                        overlay = TeacherOverlay.ReportReview
                    },
                    onOpenHeatmap = { overlay = TeacherOverlay.Heatmap },
                    onOpenIdCard = { overlay = TeacherOverlay.DigitalIdCard },
                    onOpenScheduledMessages = { overlay = TeacherOverlay.ScheduledMessages },
                    onOpenEvents = { overlay = TeacherOverlay.EventRegistration },
                    onOpenMessages = { overlay = TeacherOverlay.Messages },
                    onOpenNotifications = { overlay = TeacherOverlay.Notifications },
                    onOpenExamTimetable = { overlay = TeacherOverlay.ExamTimetableList },
                    onOpenExport = { overlay = TeacherOverlay.Export },
                    onOpenAnnouncements = { overlay = TeacherOverlay.AnnouncementList },
                    unreadCount = notifications.unreadCount,
                )

                "update" -> key(updateScopeNonce) {
                    TeacherUpdateScreenV2(
                        initialAssignmentId = updateAssignmentId,
                        initialScopeLabel = updateScopeLabel,
                        initialTool = updateInitialTool,
                        teacherName = teacherName,
                        unreadCount = notifications.unreadCount,
                        onOpenNotifications = { overlay = TeacherOverlay.Notifications },
                        onOpenMessages = { overlay = TeacherOverlay.Messages },
                        onImportMarks = { overlay = TeacherOverlay.ExamMarksImport },
                    )
                }

                "classes" -> TeacherClassesScreenV2(
                    teacherName = teacherName,
                    unreadCount = notifications.unreadCount,
                    onOpenNotifications = { overlay = TeacherOverlay.Notifications },
                )

                "timetable" -> TeacherTimetableScreenV2(
                    teacherName = teacherName,
                    unreadCount = notifications.unreadCount,
                    onOpenNotifications = { overlay = TeacherOverlay.Notifications },
                    initialShowRequests = showRequestsSegment,
                )

                "profile" -> TeacherProfileScreenV2(
                    onLogout = onLogout,
                    teacherName = teacherName,
                    unreadCount = notifications.unreadCount,
                    onOpenNotifications = { overlay = TeacherOverlay.Notifications },
                    onOpenSalary = { overlay = TeacherOverlay.SalaryHistory },
                )
            }

            // Offline indicator overlay — animated slide-in/out so it never jumps.
            AnimatedVisibility(
                visible = isPortalOffline,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 24.dp,
                        ),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    VOfflineBanner(isOffline = true)
                }
            }

            // "Back online" transient confirmation.
            AnimatedVisibility(
                visible = showBackOnline,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 24.dp,
                        ),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    VBackOnlineBanner()
                }
            }
        }
    }
}
