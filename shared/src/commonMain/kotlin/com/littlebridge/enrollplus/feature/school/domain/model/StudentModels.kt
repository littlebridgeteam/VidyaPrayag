package com.littlebridge.enrollplus.feature.school.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TodayItemDto(
    val color: String,
    val text: String
)

@Serializable
data class StudentDto(
    val id: String,
    @SerialName("student_code") val studentCode: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("class_name") val className: String,
    val section: String,
    @SerialName("roll_number") val rollNumber: String,
    @SerialName("parent_phone") val parentPhone: String? = null,
    @SerialName("profile_photo_url") val profilePhotoUrl: String? = null,
    @SerialName("attendance_percent") val attendancePercent: Float = 0f,
    @SerialName("teacher_count") val teacherCount: Int = 0,
    @SerialName("parent_count") val parentCount: Int = 0,
    @SerialName("is_new_admission") val isNewAdmission: Boolean = false,
    val status: String = "active",
    @SerialName("parent_name") val parentName: String? = null,
    @SerialName("homework_percent") val homeworkPercent: Float = 0f,
    @SerialName("fees_pending") val feesPending: Boolean = false,
    @SerialName("parent_meeting_scheduled") val parentMeetingScheduled: Boolean = false,
    @SerialName("parent_user_id") val parentUserId: String? = null,
    @SerialName("today_items") val todayItems: List<TodayItemDto> = emptyList()
)

@Serializable
data class StudentListResponse(val students: List<StudentDto>)

@Serializable
data class StudentPaginationDto(
    val page: Int,
    val pageSize: Int,
    val totalRecords: Int,
    val hasNext: Boolean
)

@Serializable
data class StudentListPaginatedResponse(
    val students: List<StudentDto>,
    val pagination: StudentPaginationDto
)

@Serializable
data class StudentTeacherDto(
    val id: String,
    val name: String,
    val subject: String,
    val designation: String? = null
)

@Serializable
data class StudentParentDto(
    val id: String,
    val name: String,
    val relation: String,
    @SerialName("is_primary_guardian") val isPrimaryGuardian: Boolean = false,
    val phone: String? = null
)

@Serializable
data class StudentActivityDto(
    val title: String,
    @SerialName("created_at") val createdAt: String,
    val type: String
)

@Serializable
data class CreateStudentRequest(
    @SerialName("full_name") val fullName: String,
    @SerialName("class_name") val className: String,
    val section: String? = null,
    @SerialName("roll_number") val rollNumber: String,
    @SerialName("parent_phone") val parentPhone: String? = null,
    @SerialName("student_code") val studentCode: String? = null,
    @SerialName("admission_date") val admissionDate: String? = null
)

@Serializable
data class UpdateStudentRequest(
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("class_name") val className: String? = null,
    val section: String? = null,
    @SerialName("roll_number") val rollNumber: String? = null,
    @SerialName("admission_date") val admissionDate: String? = null
)

@Serializable
data class BulkImportStudentsRequest(
    val students: List<CreateStudentRequest>? = null,
    val csv: String? = null
)

@Serializable
data class BulkImportRowResult(
    val row: Int,
    val success: Boolean,
    @SerialName("student_code") val studentCode: String? = null,
    val error: String? = null
)

@Serializable
data class BulkImportStudentsResponse(
    val total: Int,
    val inserted: Int,
    val failed: Int,
    val results: List<BulkImportRowResult> = emptyList()
)

@Serializable
data class AttendanceDayDto(val date: String, val status: String)

@Serializable
data class StudentMarkDto(
    val subject: String,
    @SerialName("assessment") val assessmentName: String,
    val marks: Double? = null,
    @SerialName("max_marks") val maxMarks: Int,
    @SerialName("exam_date") val examDate: String? = null
)

@Serializable
data class StudentLeaveDto(
    @SerialName("date_from") val dateFrom: String,
    @SerialName("date_to") val dateTo: String,
    val reason: String,
    val status: String
)

@Serializable
data class StudentFeeDto(
    val title: String,
    val amount: Double,
    val currency: String,
    val status: String,
    @SerialName("due_date") val dueDate: String? = null
)

@Serializable
data class StudentProfileDto(
    val student: StudentDto,
    @SerialName("present_days") val presentDays: Int,
    @SerialName("absent_days") val absentDays: Int,
    @SerialName("late_days") val lateDays: Int,
    @SerialName("attendance_rate") val attendanceRate: Int,
    @SerialName("recent_attendance") val recentAttendance: List<AttendanceDayDto>,
    val marks: List<StudentMarkDto>,
    val leave: List<StudentLeaveDto>,
    val fees: List<StudentFeeDto>,
    @SerialName("admission_date") val admissionDate: String? = null,
    @SerialName("attendance_percent") val attendancePercent: Float = 0f,
    @SerialName("teacher_count") val teacherCount: Int = 0,
    @SerialName("parent_count") val parentCount: Int = 0,
    @SerialName("subject_count") val subjectCount: Int = 0,
    @SerialName("academic_score") val academicScore: Float? = null,
    @SerialName("is_new_admission") val isNewAdmission: Boolean = false,
    val status: String = "active",
    val teachers: List<StudentTeacherDto> = emptyList(),
    val parents: List<StudentParentDto> = emptyList(),
    val insights: List<String> = emptyList(),
    val activities: List<StudentActivityDto> = emptyList()
)

@Serializable
data class TeacherAssignmentDto(
    @SerialName("class_name") val className: String,
    val section: String,
    val subject: String,
    @SerialName("student_count") val studentCount: Int = 0
)

@Serializable
data class TeacherAchievementDto(
    val title: String,
    val description: String
)

@Serializable
data class TeacherActivityDto(
    val title: String,
    @SerialName("created_at") val createdAt: String,
    val type: String
)

@Serializable
data class TeacherProfileDto(
    val id: String,
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val role: String,
    val designation: String? = null,
    @SerialName("joined_on") val joinedOn: String? = null,
    @SerialName("experience_years") val experienceYears: Int? = null,
    @SerialName("student_count") val studentCount: Int = 0,
    @SerialName("class_count") val classCount: Int,
    @SerialName("subject_count") val subjectCount: Int,
    @SerialName("attendance_percent") val attendancePercent: Float = 0f,
    @SerialName("assignment_completion_percent") val assignmentCompletionPercent: Float = 0f,
    @SerialName("parent_satisfaction_percent") val parentSatisfactionPercent: Float = 0f,
    val status: String = "active",
    val assignments: List<TeacherAssignmentDto>,
    val insights: List<String> = emptyList(),
    val achievements: List<TeacherAchievementDto> = emptyList(),
    @SerialName("recent_activities") val recentActivities: List<TeacherActivityDto> = emptyList()
)
