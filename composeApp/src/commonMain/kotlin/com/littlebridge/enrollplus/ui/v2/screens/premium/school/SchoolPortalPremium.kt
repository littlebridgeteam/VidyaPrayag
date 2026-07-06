package com.littlebridge.enrollplus.ui.v2.screens.premium.school

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.VBackHandler
import com.littlebridge.enrollplus.feature.admin.presentation.MessagesViewModel
import com.littlebridge.enrollplus.feature.alumni.presentation.AlumniViewModel
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.navigation.NavItem
import com.littlebridge.enrollplus.ui.v2.components.navigation.VBottomNav
import com.littlebridge.enrollplus.ui.v2.components.navigation.VScreenScaffoldPremium
import com.littlebridge.enrollplus.ui.v2.navigation.DeepLinkTarget
import com.littlebridge.enrollplus.ui.v2.navigation.EntryRole
import com.littlebridge.enrollplus.ui.v2.navigation.parseDeepLink
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.screens.premium.school.SchoolAcademicCalendarScreen
import com.littlebridge.enrollplus.ui.v2.screens.premium.school.SchoolNotificationsScreen
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.util.AppLogger
import org.koin.compose.viewmodel.koinViewModel

private enum class SchoolOverlayPremium {
    None, Notifications, Calendar, AcademicCalendarPlatform, CreateEvent, AcademicYear,
    Messages, LeaveRequests, LinkRequests, AdmissionsCRM, Results, SchedulePTM,
    DailyAttendance, ClassPerformance, TeacherPerformance, AnalyticsDashboard,
    EditProfile, StudentRoster, StudentProfile, PewsCohort, PewsStudentDetail,
    TeacherProfile, TeacherAssignments, Staff, HealthRecords,
    Alumni, AlumniDetail, AlumniCampaign, TransportManagement,
    ReportPublish, ReportEffectiveness, ScholarshipManagement,
    BrandingKit, IdCards, Library, ScheduledMessages, EventRegistration,
    ClassesSubjects, ClassDetail, Tutor, PaceAlerts,
}

@Composable
fun SchoolPortalPremium(
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier,
    deepLinkTarget: DeepLinkTarget? = null,
    isDark: Boolean = false,
    messagesViewModel: MessagesViewModel = koinViewModel(),
) {
    PremiumTheme(isDark = isDark) {
        SchoolPortalPremiumContent(
            onLogout = onLogout,
            modifier = modifier,
            deepLinkTarget = deepLinkTarget,
            messagesViewModel = messagesViewModel,
        )
    }
}

@Composable
private fun SchoolPortalPremiumContent(
    onLogout: () -> Unit,
    modifier: Modifier,
    deepLinkTarget: DeepLinkTarget?,
    messagesViewModel: MessagesViewModel,
) {
        var tab by rememberSaveable { mutableStateOf("home") }
        var overlay by remember { mutableStateOf(SchoolOverlayPremium.None) }
        var localDeepLink by remember { mutableStateOf<DeepLinkTarget?>(null) }
        var deepLinkThreadId by remember { mutableStateOf<String?>(null) }
        var selectedPewsStudentCode by remember { mutableStateOf<String?>(null) }
        var createEventOrigin by remember { mutableStateOf(SchoolOverlayPremium.AcademicCalendarPlatform) }
        var recordsInitialTab by remember { mutableStateOf("Coverage") }

        val alumniViewModel: AlumniViewModel = koinViewModel()

        LaunchedEffect(deepLinkTarget, localDeepLink) {
            val target = localDeepLink ?: deepLinkTarget ?: return@LaunchedEffect
            when (target) {
                is DeepLinkTarget.SchoolScreen -> {
                    when (target.screen) {
                        "transport" -> overlay = SchoolOverlayPremium.TransportManagement
                        "report-card", "report-review" -> overlay = SchoolOverlayPremium.ReportPublish
                        "library" -> overlay = SchoolOverlayPremium.Library
                        "events" -> overlay = SchoolOverlayPremium.EventRegistration
                        "scholarships" -> overlay = SchoolOverlayPremium.ScholarshipManagement
                        "branding" -> overlay = SchoolOverlayPremium.BrandingKit
                        "id-cards" -> overlay = SchoolOverlayPremium.IdCards
                        "classes", "classes-subjects" -> overlay = SchoolOverlayPremium.ClassesSubjects
                        "scheduled-messages" -> overlay = SchoolOverlayPremium.ScheduledMessages
                        "ptm" -> overlay = SchoolOverlayPremium.SchedulePTM
                        "link-requests" -> overlay = SchoolOverlayPremium.LinkRequests
                        "admissions" -> overlay = SchoolOverlayPremium.AdmissionsCRM
                        "health-records" -> overlay = SchoolOverlayPremium.HealthRecords
                        "leave-requests", "leave" -> overlay = SchoolOverlayPremium.LeaveRequests
                        "pews" -> {
                            val code = target.params["studentCode"]
                            if (code != null) {
                                selectedPewsStudentCode = code
                                overlay = SchoolOverlayPremium.PewsStudentDetail
                            } else {
                                overlay = SchoolOverlayPremium.PewsCohort
                            }
                        }
                        "messages" -> { tab = "comms"; overlay = SchoolOverlayPremium.Messages }
                        "announcements" -> { tab = "comms"; overlay = SchoolOverlayPremium.None }
                        "calendar" -> overlay = SchoolOverlayPremium.AcademicCalendarPlatform
                        "fees" -> { tab = "records"; overlay = SchoolOverlayPremium.None; recordsInitialTab = "Fee" }
                        "scholarship" -> overlay = SchoolOverlayPremium.ScholarshipManagement
                        "tutor" -> overlay = SchoolOverlayPremium.Tutor
                        "timetable" -> overlay = SchoolOverlayPremium.ClassesSubjects
                        "timetable-requests" -> overlay = SchoolOverlayPremium.ClassesSubjects
                        "pace-alerts", "pace" -> overlay = SchoolOverlayPremium.PaceAlerts
                        "alumni" -> overlay = SchoolOverlayPremium.Alumni
                        "analytics", "intelligence" -> overlay = SchoolOverlayPremium.AnalyticsDashboard
                        "daily-attendance" -> overlay = SchoolOverlayPremium.DailyAttendance
                        "class-performance" -> overlay = SchoolOverlayPremium.ClassPerformance
                        "teacher-performance" -> overlay = SchoolOverlayPremium.TeacherPerformance
                        "student-roster" -> overlay = SchoolOverlayPremium.StudentRoster
                        "edit-profile" -> overlay = SchoolOverlayPremium.EditProfile
                        "staff" -> overlay = SchoolOverlayPremium.Staff
                        "report-effectiveness" -> overlay = SchoolOverlayPremium.ReportEffectiveness
                        "home", "people", "records", "comms", "settings" -> tab = target.screen
                        else -> tab = "home"
                    }
                }
                is DeepLinkTarget.Messages -> {
                    deepLinkThreadId = target.threadId
                    tab = "comms"
                    overlay = SchoolOverlayPremium.Messages
                }
                is DeepLinkTarget.Generic -> {
                    val pathOnly = target.path.substringBefore("?").removePrefix("/")
                    when {
                        pathOnly.startsWith("messages") -> { tab = "comms"; overlay = SchoolOverlayPremium.Messages }
                        pathOnly.startsWith("announcements") -> { tab = "comms"; overlay = SchoolOverlayPremium.None }
                        pathOnly.startsWith("fees") -> { tab = "records"; overlay = SchoolOverlayPremium.None }
                        pathOnly.startsWith("transport") -> overlay = SchoolOverlayPremium.TransportManagement
                        pathOnly.startsWith("library") -> overlay = SchoolOverlayPremium.Library
                        pathOnly.startsWith("scholarships") -> overlay = SchoolOverlayPremium.ScholarshipManagement
                        pathOnly.startsWith("events") -> overlay = SchoolOverlayPremium.EventRegistration
                        pathOnly.startsWith("leave") -> overlay = SchoolOverlayPremium.LeaveRequests
                        pathOnly.startsWith("pews") -> overlay = SchoolOverlayPremium.PewsCohort
                        pathOnly.startsWith("link-requests") -> overlay = SchoolOverlayPremium.LinkRequests
                        pathOnly.startsWith("admissions") -> overlay = SchoolOverlayPremium.AdmissionsCRM
                        pathOnly.startsWith("calendar") -> overlay = SchoolOverlayPremium.AcademicCalendarPlatform
                        pathOnly.startsWith("timetable-requests") -> overlay = SchoolOverlayPremium.ClassesSubjects
                        pathOnly.startsWith("timetable") -> overlay = SchoolOverlayPremium.ClassesSubjects
                        pathOnly.startsWith("report-card") -> overlay = SchoolOverlayPremium.ReportPublish
                        pathOnly.startsWith("tutor") -> overlay = SchoolOverlayPremium.Tutor
                        pathOnly.startsWith("ptm") -> overlay = SchoolOverlayPremium.SchedulePTM
                        pathOnly.startsWith("health-records") -> overlay = SchoolOverlayPremium.HealthRecords
                        pathOnly.startsWith("scheduled-messages") -> overlay = SchoolOverlayPremium.ScheduledMessages
                        pathOnly.startsWith("alumni") -> overlay = SchoolOverlayPremium.Alumni
                        pathOnly.startsWith("scholarship") -> overlay = SchoolOverlayPremium.ScholarshipManagement
                        pathOnly.startsWith("analytics") -> overlay = SchoolOverlayPremium.AnalyticsDashboard
                        pathOnly.startsWith("intelligence") -> overlay = SchoolOverlayPremium.AnalyticsDashboard
                        pathOnly.startsWith("daily-attendance") -> overlay = SchoolOverlayPremium.DailyAttendance
                        pathOnly.startsWith("class-performance") -> overlay = SchoolOverlayPremium.ClassPerformance
                        pathOnly.startsWith("teacher-performance") -> overlay = SchoolOverlayPremium.TeacherPerformance
                        pathOnly.startsWith("student-roster") -> overlay = SchoolOverlayPremium.StudentRoster
                        pathOnly.startsWith("edit-profile") -> overlay = SchoolOverlayPremium.EditProfile
                        pathOnly.startsWith("staff") -> overlay = SchoolOverlayPremium.Staff
                        pathOnly.startsWith("report-effectiveness") -> overlay = SchoolOverlayPremium.ReportEffectiveness
                        pathOnly.startsWith("fee-reminder") -> { tab = "records"; overlay = SchoolOverlayPremium.None; recordsInitialTab = "Fee" }
                        else -> tab = "home"
                    }
                }
                else -> Unit
            }
            localDeepLink = null
        }

        var selectedStudentId by remember { mutableStateOf<String?>(null) }
        var selectedTeacherId by remember { mutableStateOf<String?>(null) }
        var selectedStaffId by remember { mutableStateOf<String?>(null) }
        var healthStudentId by remember { mutableStateOf<String?>(null) }
        var healthStudentName by remember { mutableStateOf<String?>(null) }
        var selectedAlumniId by remember { mutableStateOf<String?>(null) }
        var selectedCampaignId by remember { mutableStateOf<String?>(null) }
        var selectedClassId by remember { mutableStateOf<String?>(null) }
        var selectedClassName by remember { mutableStateOf<String?>(null) }
        var profileReturnOverlay by remember { mutableStateOf(SchoolOverlayPremium.None) }

        val messagesState by messagesViewModel.state.collectAsStateV2()
        val commsBadge by remember {
            derivedStateOf { messagesState.threads.count { it.unreadCount > 0 } }
        }
        var peopleRefreshKey by remember { mutableIntStateOf(0) }
        var studentRefreshKey by remember { mutableIntStateOf(0) }

        VBackHandler(enabled = overlay != SchoolOverlayPremium.None) {
            when (overlay) {
                SchoolOverlayPremium.StudentProfile, SchoolOverlayPremium.TeacherProfile -> {
                    val returnTo = profileReturnOverlay
                    profileReturnOverlay = SchoolOverlayPremium.None
                    overlay = returnTo
                }
                SchoolOverlayPremium.ClassDetail -> overlay = SchoolOverlayPremium.ClassesSubjects
                SchoolOverlayPremium.PewsStudentDetail -> { selectedPewsStudentCode = null; overlay = SchoolOverlayPremium.PewsCohort }
                SchoolOverlayPremium.AlumniDetail, SchoolOverlayPremium.AlumniCampaign -> { selectedAlumniId = null; selectedCampaignId = null; overlay = SchoolOverlayPremium.Alumni }
                SchoolOverlayPremium.CreateEvent -> overlay = createEventOrigin
                else -> { deepLinkThreadId = null; selectedPewsStudentCode = null; overlay = SchoolOverlayPremium.None }
            }
        }

        when (overlay) {
            SchoolOverlayPremium.Notifications -> {
                SchoolNotificationsScreen(onBack = { overlay = SchoolOverlayPremium.None }, onDeepLink = { dl -> localDeepLink = parseDeepLink(dl, EntryRole.SchoolAdmin); overlay = SchoolOverlayPremium.None }, modifier = modifier)
                return
            }
            SchoolOverlayPremium.Calendar -> {
                SchoolAcademicCalendarScreen(onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier, viewModelQualifier = org.koin.core.qualifier.named("schoolCalendar"))
                return
            }
            SchoolOverlayPremium.AcademicCalendarPlatform -> { AcademicCalendarPlatformPremium(onBack = { overlay = SchoolOverlayPremium.None }, onCreateEvent = { createEventOrigin = SchoolOverlayPremium.AcademicCalendarPlatform; overlay = SchoolOverlayPremium.CreateEvent }, onOpenEvent = {}, modifier = modifier); return }
            SchoolOverlayPremium.CreateEvent -> { UnifiedCreateEventPremium(onBack = { overlay = createEventOrigin }, onCreated = { overlay = createEventOrigin }, modifier = modifier); return }
            SchoolOverlayPremium.AcademicYear -> { AcademicYearManagementPremium(onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier); return }
            SchoolOverlayPremium.Messages -> { MessagesPremium(onBack = { overlay = SchoolOverlayPremium.None; deepLinkThreadId = null }, onOpenThread = { _ -> }, modifier = modifier); return }
            SchoolOverlayPremium.LeaveRequests -> { LeaveRequestsPremium(onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier); return }
            SchoolOverlayPremium.LinkRequests -> { LinkRequestsPremium(onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier); return }
            SchoolOverlayPremium.AdmissionsCRM -> { AdmissionsCrmPremium(onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier); return }
            SchoolOverlayPremium.Results -> { ResultsPublishPremium(onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier); return }
            SchoolOverlayPremium.SchedulePTM -> { SchedulePtmPremium(onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier); return }
            SchoolOverlayPremium.DailyAttendance -> { DailyAttendancePremium(onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier); return }
            SchoolOverlayPremium.ClassPerformance -> { ClassPerformancePremium(onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier); return }
            SchoolOverlayPremium.TeacherPerformance -> { TeacherPerformancePremium(onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier); return }
            SchoolOverlayPremium.AnalyticsDashboard -> { AnalyticsDashboardPremium(onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier); return }
            SchoolOverlayPremium.EditProfile -> { EditSchoolProfilePremium(onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier); return }
            SchoolOverlayPremium.StudentRoster -> {
                StudentRosterPremium(onBack = { overlay = SchoolOverlayPremium.None }, onOpenStudent = { id -> selectedStudentId = id; overlay = SchoolOverlayPremium.StudentProfile }, modifier = modifier)
                return
            }
            SchoolOverlayPremium.StudentProfile -> {
                val id = selectedStudentId
                if (id == null) { AppLogger.e("SchoolPortal", "StudentProfile overlay opened with null id"); overlay = SchoolOverlayPremium.None; return }
                val returnTo = profileReturnOverlay
                StudentProfilePremium(studentId = id, onBack = { overlay = returnTo; profileReturnOverlay = SchoolOverlayPremium.None }, onRemoved = { overlay = returnTo; profileReturnOverlay = SchoolOverlayPremium.None; studentRefreshKey++ }, onOpenHealth = { sid, sname -> healthStudentId = sid; healthStudentName = sname; overlay = SchoolOverlayPremium.HealthRecords }, modifier = modifier)
                return
            }
            SchoolOverlayPremium.PewsCohort -> { PewsCohortPremium(onBack = { overlay = SchoolOverlayPremium.None }, onOpenStudent = { code -> selectedPewsStudentCode = code; overlay = SchoolOverlayPremium.PewsStudentDetail }, modifier = modifier); return }
            SchoolOverlayPremium.PewsStudentDetail -> {
                val code = selectedPewsStudentCode
                if (code == null) { AppLogger.e("SchoolPortal", "PewsStudentDetail overlay opened with null code"); overlay = SchoolOverlayPremium.PewsCohort; return }
                PewsStudentDetailPremium(studentCode = code, onBack = { overlay = SchoolOverlayPremium.PewsCohort }, modifier = modifier)
                return
            }
            SchoolOverlayPremium.TeacherProfile -> {
                val id = selectedTeacherId
                if (id == null) { AppLogger.e("SchoolPortal", "TeacherProfile overlay opened with null id"); overlay = SchoolOverlayPremium.None; return }
                val returnTo = profileReturnOverlay
                TeacherProfilePremium(teacherId = id, onBack = { overlay = returnTo; profileReturnOverlay = SchoolOverlayPremium.None }, onRemoved = { overlay = returnTo; profileReturnOverlay = SchoolOverlayPremium.None; peopleRefreshKey++ }, onOpenAssignments = { overlay = SchoolOverlayPremium.TeacherAssignments }, modifier = modifier)
                return
            }
            SchoolOverlayPremium.TeacherAssignments -> {
                val id = selectedTeacherId
                if (id == null) { AppLogger.e("SchoolPortal", "TeacherAssignments overlay opened with null id"); overlay = SchoolOverlayPremium.None; return }
                TeacherAssignmentPremium(teacherId = id, onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier)
                return
            }
            SchoolOverlayPremium.Staff -> {
                val id = selectedStaffId
                if (id == null) { AppLogger.e("SchoolPortal", "Staff overlay opened with null id"); overlay = SchoolOverlayPremium.None; return }
                StaffProfilePremium(staffId = id, onBack = { overlay = SchoolOverlayPremium.None }, onRemoved = { overlay = SchoolOverlayPremium.None }, modifier = modifier)
                return
            }
            SchoolOverlayPremium.HealthRecords -> {
                val id = healthStudentId
                val name = healthStudentName ?: "Student"
                if (id == null) { AppLogger.e("SchoolPortal", "HealthRecords overlay opened with null id"); overlay = SchoolOverlayPremium.None; return }
                HealthRecordsPremium(studentId = id, studentName = name, onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier)
                return
            }
            SchoolOverlayPremium.Alumni -> { AlumniPremium(onBack = { overlay = SchoolOverlayPremium.None }, onOpenAlumni = { id -> selectedAlumniId = id; overlay = SchoolOverlayPremium.AlumniDetail }, onOpenCampaign = { id -> selectedCampaignId = id; overlay = SchoolOverlayPremium.AlumniCampaign }, modifier = modifier); return }
            SchoolOverlayPremium.AlumniDetail -> {
                val id = selectedAlumniId
                if (id == null) { AppLogger.e("SchoolPortal", "AlumniDetail overlay opened with null id"); overlay = SchoolOverlayPremium.None; return }
                AlumniDetailPremium(alumniId = id, onBack = { overlay = SchoolOverlayPremium.Alumni }, modifier = modifier)
                return
            }
            SchoolOverlayPremium.AlumniCampaign -> {
                val id = selectedCampaignId
                if (id == null) { AppLogger.e("SchoolPortal", "AlumniCampaign overlay opened with null id"); overlay = SchoolOverlayPremium.None; return }
                AlumniCampaignPremium(campaignId = id, onBack = { overlay = SchoolOverlayPremium.Alumni }, modifier = modifier)
                return
            }
            SchoolOverlayPremium.TransportManagement -> { TransportManagementPremium(onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier); return }
            SchoolOverlayPremium.ReportPublish -> { AdminReportPublishPremium(onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier); return }
            SchoolOverlayPremium.ReportEffectiveness -> { AdminReportingEffectivenessPremium(onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier); return }
            SchoolOverlayPremium.ScholarshipManagement -> { ScholarshipManagementPremium(onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier); return }
            SchoolOverlayPremium.BrandingKit -> { BrandingSettingsPremium(onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier); return }
            SchoolOverlayPremium.IdCards -> { IdCardPremium(onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier); return }
            SchoolOverlayPremium.Library -> { SchoolLibraryPremium(onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier); return }
            SchoolOverlayPremium.ScheduledMessages -> { ScheduledMessagesPremium(onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier); return }
            SchoolOverlayPremium.EventRegistration -> { AdminEventRegistrationPremium(onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier); return }
            SchoolOverlayPremium.ClassesSubjects -> {
                ClassesSubjectsPremium(onBack = { overlay = SchoolOverlayPremium.None }, onOpenClassDetail = { cls -> selectedClassId = cls.id; selectedClassName = cls.name; overlay = SchoolOverlayPremium.ClassDetail }, modifier = modifier)
                return
            }
            SchoolOverlayPremium.ClassDetail -> {
                val id = selectedClassId
                val name = selectedClassName
                if (id == null || name == null) { overlay = SchoolOverlayPremium.None; return }
                ClassDetailPremium(classId = id, className = name, onBack = { overlay = SchoolOverlayPremium.ClassesSubjects }, onOpenStudent = { sid -> selectedStudentId = sid; profileReturnOverlay = SchoolOverlayPremium.ClassDetail; overlay = SchoolOverlayPremium.StudentProfile }, onOpenTeacher = { tid -> selectedTeacherId = tid; profileReturnOverlay = SchoolOverlayPremium.ClassDetail; overlay = SchoolOverlayPremium.TeacherProfile }, modifier = modifier)
                return
            }
            SchoolOverlayPremium.Tutor -> { TutorManagementPremium(onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier); return }
            SchoolOverlayPremium.PaceAlerts -> { PaceAlertsPremium(onBack = { overlay = SchoolOverlayPremium.None }, modifier = modifier); return }
            SchoolOverlayPremium.None -> Unit
        }

        val items = listOf(
            NavItem("Home") { Icon(VIcons.Home, contentDescription = null, modifier = Modifier.size(24.dp), tint = VColors.OnSurfaceVariant) },
            NavItem("People") { Icon(VIcons.Users, contentDescription = null, modifier = Modifier.size(24.dp), tint = VColors.OnSurfaceVariant) },
            NavItem("Records") { Icon(VIcons.Bookmark, contentDescription = null, modifier = Modifier.size(24.dp), tint = VColors.OnSurfaceVariant) },
            NavItem("Comms", badgeCount = commsBadge) { Icon(VIcons.Megaphone, contentDescription = null, modifier = Modifier.size(24.dp), tint = VColors.OnSurfaceVariant) },
            NavItem("Settings") { Icon(VIcons.Settings, contentDescription = null, modifier = Modifier.size(24.dp), tint = VColors.OnSurfaceVariant) },
        )

        val tabToIndex = mapOf("home" to 0, "people" to 1, "records" to 2, "comms" to 3, "settings" to 4)

        VScreenScaffoldPremium(
            modifier = modifier,
            bottomBar = {
                VBottomNav(items = items, activeIndex = tabToIndex[tab] ?: 0, onItemClick = { index ->
                    val newTab = listOf("home", "people", "records", "comms", "settings")[index]
                    if (tab != newTab) {
                        createEventOrigin = SchoolOverlayPremium.AcademicCalendarPlatform
                        profileReturnOverlay = SchoolOverlayPremium.None
                        if (newTab != "records") recordsInitialTab = "Coverage"
                    }
                    tab = newTab
                })
            },
        ) { padding ->
            Box(Modifier.fillMaxSize()) {
                when (tab) {
                    "home" -> SchoolHomePremium(
                        onOpenNotifications = { overlay = SchoolOverlayPremium.Notifications },
                        onOpenCalendar = { overlay = SchoolOverlayPremium.AcademicCalendarPlatform },
                        onOpenAnalytics = { overlay = SchoolOverlayPremium.AnalyticsDashboard },
                        onOpenPews = { overlay = SchoolOverlayPremium.PewsCohort },
                        onOpenTransport = { overlay = SchoolOverlayPremium.TransportManagement },
                        onOpenReportPublish = { overlay = SchoolOverlayPremium.ReportPublish },
                        onOpenReportEffectiveness = { overlay = SchoolOverlayPremium.ReportEffectiveness },
                        onOpenEvents = { overlay = SchoolOverlayPremium.EventRegistration },
                        onCreateEvent = { createEventOrigin = SchoolOverlayPremium.None; overlay = SchoolOverlayPremium.CreateEvent },
                        onExit = { tab = "settings" },
                    )
                    "people" -> SchoolPeoplePremium(
                        teacherRefreshKey = peopleRefreshKey,
                        studentRefreshKey = studentRefreshKey,
                        onOpenLinkRequests = { overlay = SchoolOverlayPremium.LinkRequests },
                        onOpenStudent = { id -> selectedStudentId = id; overlay = SchoolOverlayPremium.StudentProfile },
                        onOpenTeacher = { id -> selectedTeacherId = id; overlay = SchoolOverlayPremium.TeacherProfile },
                        onAssignClasses = { id -> selectedTeacherId = id; overlay = SchoolOverlayPremium.TeacherAssignments },
                        onOpenStaff = { id -> selectedStaffId = id; overlay = SchoolOverlayPremium.Staff },
                        onOpenAlumni = { overlay = SchoolOverlayPremium.Alumni },
                        onGraduateStudents = { studentIds, year -> alumniViewModel.graduateStudents(studentIds, year) },
                    )
                    "records" -> SchoolRecordsPremium(initialTab = recordsInitialTab)
                    "comms" -> SchoolCommsPremium(
                        onOpenMessages = { overlay = SchoolOverlayPremium.Messages },
                        onOpenPtm = { overlay = SchoolOverlayPremium.SchedulePTM },
                        onOpenScheduledMessages = { overlay = SchoolOverlayPremium.ScheduledMessages },
                        onOpenNotifications = { overlay = SchoolOverlayPremium.Notifications },
                        onCreateEvent = { createEventOrigin = SchoolOverlayPremium.None; overlay = SchoolOverlayPremium.CreateEvent },
                    )
                    "settings" -> SchoolSettingsPremium(
                        onLogout = onLogout,
                        onOpenTeachers = { tab = "people" },
                        onOpenProfile = { overlay = SchoolOverlayPremium.EditProfile },
                        onOpenAcademicYear = { overlay = SchoolOverlayPremium.AcademicYear },
                        onOpenTransport = { overlay = SchoolOverlayPremium.TransportManagement },
                        onOpenScholarships = { overlay = SchoolOverlayPremium.ScholarshipManagement },
                        onOpenBranding = { overlay = SchoolOverlayPremium.BrandingKit },
                        onOpenIdCards = { overlay = SchoolOverlayPremium.IdCards },
                        onOpenLibrary = { overlay = SchoolOverlayPremium.Library },
                        onOpenClassesSubjects = { overlay = SchoolOverlayPremium.ClassesSubjects },
                        onOpenFees = { recordsInitialTab = "Fee"; tab = "records" },
                        onOpenNotifications = { overlay = SchoolOverlayPremium.Notifications },
                    )
                }
            }
        }
}
