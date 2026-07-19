package com.littlebridge.enrollplus.ui.v2.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DeepLinkParserTest {

    // ── Empty / generic paths ──────────────────────────────────────────────

    @Test
    fun parseDeepLink_emptyPath_returnsGeneric() {
        val result = parseDeepLink("", EntryRole.Parent)
        assertIs<DeepLinkTarget.Generic>(result)
    }

    @Test
    fun parseDeepLink_slashOnly_returnsGeneric() {
        val result = parseDeepLink("/", EntryRole.Parent)
        assertIs<DeepLinkTarget.Generic>(result)
    }

    @Test
    fun parseDeepLink_unknownSegment_returnsGeneric() {
        val result = parseDeepLink("/xyz", EntryRole.Parent)
        assertIs<DeepLinkTarget.Generic>(result)
    }

    // ── Parent deep links ──────────────────────────────────────────────────

    @Test
    fun parseDeepLink_parent_home_returnsParentTabHome() {
        val result = parseDeepLink("/parent/home", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("home", result.tab)
        assertNull(result.overlay)
    }

    @Test
    fun parseDeepLink_parent_academics_returnsParentTabAcademics() {
        val result = parseDeepLink("/parent/academics", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("academics", result.tab)
    }

    @Test
    fun parseDeepLink_parent_fees_returnsParentTabFees() {
        val result = parseDeepLink("/parent/fees", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("fees", result.tab)
    }

    @Test
    fun parseDeepLink_parent_conversations_returnsParentTabConversations() {
        val result = parseDeepLink("/parent/conversations", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("conversations", result.tab)
    }

    @Test
    fun parseDeepLink_parent_profile_returnsParentTabProfile() {
        val result = parseDeepLink("/parent/profile", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("profile", result.tab)
    }

    @Test
    fun parseDeepLink_parent_home_attendance_overlay() {
        val result = parseDeepLink("/parent/home/attendance", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("home", result.tab)
        assertEquals("attendance", result.overlay)
    }

    @Test
    fun parseDeepLink_parent_home_transport_overlay() {
        val result = parseDeepLink("/parent/home/transport", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("transport", result.overlay)
    }

    @Test
    fun parseDeepLink_parent_messages_withThreadId_returnsMessages() {
        val result = parseDeepLink("/parent/messages/thread123", EntryRole.Parent)
        assertIs<DeepLinkTarget.Messages>(result)
        assertEquals("thread123", result.threadId)
    }

    @Test
    fun parseDeepLink_parent_reportCard_withDraftId() {
        val result = parseDeepLink("/parent/academics/report-card/draft456", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("academics", result.tab)
        assertEquals("report-card", result.overlay)
        assertEquals("draft456", result.params["draftId"])
    }

    @Test
    fun parseDeepLink_parent_fees_withUuid_asFeeId() {
        val result = parseDeepLink("/parent/fees/abc-123-xyz", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("fees", result.tab)
        assertEquals("abc-123-xyz", result.params["feeId"])
    }

    @Test
    fun parseDeepLink_parent_linkChild_overlay() {
        val result = parseDeepLink("/parent/link-child", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("profile", result.tab)
        assertEquals("link-child", result.overlay)
    }

    @Test
    fun parseDeepLink_parent_announcements_overlay() {
        val result = parseDeepLink("/parent/announcements", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("conversations", result.tab)
        assertEquals("announcements", result.overlay)
    }

    @Test
    fun parseDeepLink_parent_announcements_withId() {
        val result = parseDeepLink("/parent/announcements/ann789", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("ann789", result.params["announcementId"])
    }

    // ── Query params ───────────────────────────────────────────────────────

    @Test
    fun parseDeepLink_parent_withQueryParams_extractsParams() {
        val result = parseDeepLink("/parent/home/leave?leaveId=lvl123", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("leave", result.overlay)
        assertEquals("lvl123", result.params["leaveId"])
    }

    @Test
    fun parseDeepLink_teacher_reportReview_withQueryParams() {
        val result = parseDeepLink(
            "/teacher/report-review?className=8&section=A&term=Term+1",
            EntryRole.Teacher,
        )
        assertIs<DeepLinkTarget.TeacherScreen>(result)
        assertEquals("report-review", result.screen)
        assertEquals("8", result.params["className"])
        assertEquals("A", result.params["section"])
        assertEquals("Term 1", result.params["term"])
    }

    // ── Teacher deep links ─────────────────────────────────────────────────

    @Test
    fun parseDeepLink_teacher_home_returnsTeacherScreen() {
        val result = parseDeepLink("/teacher/home", EntryRole.Teacher)
        assertIs<DeepLinkTarget.TeacherScreen>(result)
        assertEquals("home", result.screen)
    }

    @Test
    fun parseDeepLink_teacher_attendance_returnsTeacherScreen() {
        val result = parseDeepLink("/teacher/attendance", EntryRole.Teacher)
        assertIs<DeepLinkTarget.TeacherScreen>(result)
        assertEquals("attendance", result.screen)
    }

    @Test
    fun parseDeepLink_teacher_messages_withThreadId_returnsMessages() {
        val result = parseDeepLink("/teacher/messages/t456", EntryRole.Teacher)
        assertIs<DeepLinkTarget.Messages>(result)
        assertEquals("t456", result.threadId)
    }

    @Test
    fun parseDeepLink_teacher_salary_returnsTeacherScreen() {
        val result = parseDeepLink("/teacher/salary", EntryRole.Teacher)
        assertIs<DeepLinkTarget.TeacherScreen>(result)
        assertEquals("salary", result.screen)
    }

    // ── School / Admin deep links ──────────────────────────────────────────

    @Test
    fun parseDeepLink_school_home_returnsSchoolScreen() {
        val result = parseDeepLink("/school/home", EntryRole.SchoolAdmin)
        assertIs<DeepLinkTarget.SchoolScreen>(result)
        assertEquals("home", result.screen)
    }

    @Test
    fun parseDeepLink_admin_home_returnsSchoolScreen() {
        val result = parseDeepLink("/admin/home", EntryRole.SchoolAdmin)
        assertIs<DeepLinkTarget.SchoolScreen>(result)
        assertEquals("home", result.screen)
    }

    @Test
    fun parseDeepLink_school_pews_withStudentCode() {
        val result = parseDeepLink("/school/pews/student/STU001", EntryRole.SchoolAdmin)
        assertIs<DeepLinkTarget.SchoolScreen>(result)
        assertEquals("pews", result.screen)
        assertEquals("STU001", result.params["studentCode"])
    }

    @Test
    fun parseDeepLink_school_messages_withThreadId_returnsMessages() {
        val result = parseDeepLink("/school/messages/m789", EntryRole.SchoolAdmin)
        assertIs<DeepLinkTarget.Messages>(result)
        assertEquals("m789", result.threadId)
    }

    @Test
    fun parseDeepLink_superAdmin_school_route() {
        val result = parseDeepLink("/school/fees", EntryRole.SuperAdmin)
        assertIs<DeepLinkTarget.SchoolScreen>(result)
        assertEquals("fees", result.screen)
    }

    // ── Shared deep-link roots (role-aware) ────────────────────────────────

    @Test
    fun parseDeepLink_announcements_parentRole() {
        val result = parseDeepLink("/announcements/ann100", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("conversations", result.tab)
        assertEquals("announcements", result.overlay)
        assertEquals("ann100", result.params["announcementId"])
    }

    @Test
    fun parseDeepLink_announcements_teacherRole() {
        val result = parseDeepLink("/announcements/ann100", EntryRole.Teacher)
        assertIs<DeepLinkTarget.TeacherScreen>(result)
        assertEquals("announcements", result.screen)
        assertEquals("ann100", result.params["id"])
    }

    @Test
    fun parseDeepLink_announcements_schoolAdminRole() {
        val result = parseDeepLink("/announcements/ann100", EntryRole.SchoolAdmin)
        assertIs<DeepLinkTarget.SchoolScreen>(result)
        assertEquals("announcements", result.screen)
        assertEquals("ann100", result.params["id"])
    }

    @Test
    fun parseDeepLink_calendar_parentRole() {
        val result = parseDeepLink("/calendar", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("home", result.tab)
        assertEquals("calendar", result.overlay)
    }

    @Test
    fun parseDeepLink_calendar_teacherRole() {
        val result = parseDeepLink("/calendar", EntryRole.Teacher)
        assertIs<DeepLinkTarget.TeacherScreen>(result)
        assertEquals("calendar", result.screen)
    }

    @Test
    fun parseDeepLink_messages_parentRole() {
        val result = parseDeepLink("/messages/thread999", EntryRole.Parent)
        assertIs<DeepLinkTarget.Messages>(result)
        assertEquals("thread999", result.threadId)
    }

    @Test
    fun parseDeepLink_fees_parentRole() {
        val result = parseDeepLink("/fees/fee-001", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("fees", result.tab)
        assertEquals("fee-001", result.params["feeId"])
    }

    @Test
    fun parseDeepLink_fees_schoolAdminRole() {
        val result = parseDeepLink("/fees/fee-001", EntryRole.SchoolAdmin)
        assertIs<DeepLinkTarget.SchoolScreen>(result)
        assertEquals("fees", result.screen)
        assertEquals("fee-001", result.params["id"])
    }

    @Test
    fun parseDeepLink_leave_parentRole() {
        val result = parseDeepLink("/leave/lv1", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("home", result.tab)
        assertEquals("leave", result.overlay)
        assertEquals("lv1", result.params["leaveId"])
    }

    @Test
    fun parseDeepLink_leave_teacherRole() {
        val result = parseDeepLink("/leave", EntryRole.Teacher)
        assertIs<DeepLinkTarget.TeacherScreen>(result)
        assertEquals("leave-requests", result.screen)
    }

    @Test
    fun parseDeepLink_transport_parentRole() {
        val result = parseDeepLink("/transport", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("home", result.tab)
        assertEquals("transport", result.overlay)
    }

    @Test
    fun parseDeepLink_transport_schoolAdminRole() {
        val result = parseDeepLink("/transport", EntryRole.SchoolAdmin)
        assertIs<DeepLinkTarget.SchoolScreen>(result)
        assertEquals("transport", result.screen)
    }

    @Test
    fun parseDeepLink_reportCard_parentRole() {
        val result = parseDeepLink("/report-card/draft001", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("academics", result.tab)
        assertEquals("report-card", result.overlay)
        assertEquals("draft001", result.params["draftId"])
    }

    @Test
    fun parseDeepLink_reportCard_teacherRole() {
        val result = parseDeepLink("/report-card", EntryRole.Teacher)
        assertIs<DeepLinkTarget.TeacherScreen>(result)
        assertEquals("report-card", result.screen)
    }

    @Test
    fun parseDeepLink_tutor_parentRole() {
        val result = parseDeepLink("/tutor", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("academics", result.tab)
        assertEquals("tutor", result.overlay)
    }

    @Test
    fun parseDeepLink_library_parentRole() {
        val result = parseDeepLink("/library", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("home", result.tab)
        assertEquals("library", result.overlay)
    }

    @Test
    fun parseDeepLink_library_schoolAdminRole() {
        val result = parseDeepLink("/library", EntryRole.SchoolAdmin)
        assertIs<DeepLinkTarget.SchoolScreen>(result)
        assertEquals("library", result.screen)
    }

    @Test
    fun parseDeepLink_events_parentRole() {
        val result = parseDeepLink("/events", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("home", result.tab)
        assertEquals("events", result.overlay)
    }

    @Test
    fun parseDeepLink_salary_teacherRole() {
        val result = parseDeepLink("/salary", EntryRole.Teacher)
        assertIs<DeepLinkTarget.TeacherScreen>(result)
        assertEquals("salary", result.screen)
    }

    @Test
    fun parseDeepLink_salary_parentRole_returnsGeneric() {
        val result = parseDeepLink("/salary", EntryRole.Parent)
        assertIs<DeepLinkTarget.Generic>(result)
    }

    @Test
    fun parseDeepLink_linkRequests_schoolAdminRole() {
        val result = parseDeepLink("/link-requests", EntryRole.SchoolAdmin)
        assertIs<DeepLinkTarget.SchoolScreen>(result)
        assertEquals("link-requests", result.screen)
    }

    @Test
    fun parseDeepLink_linkRequests_parentRole() {
        val result = parseDeepLink("/link-requests", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("link-child", result.tab)
    }

    @Test
    fun parseDeepLink_timetable_parentRole() {
        val result = parseDeepLink("/timetable", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("home", result.tab)
        assertEquals("timetable", result.overlay)
    }

    @Test
    fun parseDeepLink_timetable_teacherRole() {
        val result = parseDeepLink("/timetable", EntryRole.Teacher)
        assertIs<DeepLinkTarget.TeacherScreen>(result)
        assertEquals("timetable", result.screen)
    }

    @Test
    fun parseDeepLink_alumni_route() {
        val result = parseDeepLink("/alumni/directory/alum123", EntryRole.SchoolAdmin)
        assertIs<DeepLinkTarget.AlumniScreen>(result)
        assertEquals("directory", result.screen)
        assertEquals("alum123", result.alumniId)
    }

    @Test
    fun parseDeepLink_student_parentRole() {
        val result = parseDeepLink("/student/library", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("home", result.tab)
        assertEquals("library", result.overlay)
    }

    @Test
    fun parseDeepLink_student_nonParentRole_returnsGeneric() {
        val result = parseDeepLink("/student/library", EntryRole.Teacher)
        assertIs<DeepLinkTarget.Generic>(result)
    }

    @Test
    fun parseDeepLink_unknownRole_announcements_returnsGeneric() {
        val result = parseDeepLink("/announcements/ann1", EntryRole.Unknown)
        assertIs<DeepLinkTarget.Generic>(result)
    }

    @Test
    fun parseDeepLink_scholarships_parentRole() {
        val result = parseDeepLink("/scholarships", EntryRole.Parent)
        assertIs<DeepLinkTarget.ParentTab>(result)
        assertEquals("scholarships", result.tab)
    }

    @Test
    fun parseDeepLink_scholarships_schoolAdminRole() {
        val result = parseDeepLink("/scholarships", EntryRole.SchoolAdmin)
        assertIs<DeepLinkTarget.SchoolScreen>(result)
        assertEquals("scholarships", result.screen)
    }
}
