package com.littlebridge.enrollplus.feature.export

import com.littlebridge.enrollplus.db.AdmissionEnquiriesTable
import com.littlebridge.enrollplus.db.AlumniTable
import com.littlebridge.enrollplus.db.AppUsersTable
import com.littlebridge.enrollplus.db.AssessmentMarksTable
import com.littlebridge.enrollplus.db.AssessmentsTable
import com.littlebridge.enrollplus.db.AttendanceRecordsTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.EventRegistrationsTable
import com.littlebridge.enrollplus.db.EventSlotsTable
import com.littlebridge.enrollplus.db.FacultyTable
import com.littlebridge.enrollplus.db.FeeRecordsTable
import com.littlebridge.enrollplus.db.HomeworkSubmissionsTable
import com.littlebridge.enrollplus.db.HomeworkTable
import com.littlebridge.enrollplus.db.LeaveRequestsTable
import com.littlebridge.enrollplus.db.NonTeachingStaffTable
import com.littlebridge.enrollplus.db.SchoolClassesTable
import com.littlebridge.enrollplus.db.StudentsTable
import com.littlebridge.enrollplus.db.StudentHealthProfilesTable
import com.littlebridge.enrollplus.db.TeacherSubjectAssignmentsTable
import com.littlebridge.enrollplus.db.TransportAssignmentsTable
import com.littlebridge.enrollplus.db.TransportRoutesTable
import com.littlebridge.enrollplus.db.TransportStopsTable
import com.littlebridge.enrollplus.db.TransportVehiclesTable
import com.littlebridge.enrollplus.feature.media.SupabaseStorage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@Serializable
data class ExportRequest(
    val type: String,
    val format: String,
    @SerialName("class_id") val classId: String? = null,
    @SerialName("assessment_id") val assessmentId: String? = null,
    @SerialName("event_id") val eventId: String? = null,
    @SerialName("route_id") val routeId: String? = null,
    @SerialName("homework_id") val homeworkId: String? = null,
    val status: String? = null,
    @SerialName("date_from") val dateFrom: String? = null,
    @SerialName("date_to") val dateTo: String? = null,
)

@Serializable
data class ExportResponse(
    @SerialName("download_url") val downloadUrl: String? = null,
    @SerialName("file_name") val fileName: String? = null,
    @SerialName("file_size") val fileSize: Long = 0,
    val format: String,
    val message: String? = null,
    @SerialName("data_url") val dataUrl: String? = null,
)

@Serializable
data class ExportTypeDto(
    val type: String,
    val label: String,
    val category: String,
    val formats: List<String>,
    val filters: List<String>,
    val icon: String,
    @SerialName("admin_only") val adminOnly: Boolean,
)

@Serializable
data class ExportTypesResponse(
    val exports: List<ExportTypeDto>,
)

@Serializable
data class ExportAssessmentDto(
    val id: String,
    val name: String,
    val subject: String,
    @SerialName("class_name") val className: String,
    val section: String,
    @SerialName("max_marks") val maxMarks: Int,
    @SerialName("exam_date") val examDate: String? = null,
    val status: String,
)

@Serializable
data class ExportAssessmentsResponse(
    val assessments: List<ExportAssessmentDto>,
)

class ExportService {

    private val log = LoggerFactory.getLogger("ExportService")

    // ── Export type catalogue ──────────────────────────────────────────

    private val allExportTypes = listOf(
        ExportTypeDto("student_roster", "Student Roster", "Students", listOf("pdf", "csv"), listOf("classId"), "roster", false),
        ExportTypeDto("attendance_report", "Attendance Report", "Students", listOf("pdf", "csv"), listOf("classId", "dateFrom", "dateTo"), "attendance", false),
        ExportTypeDto("test_marks", "Test Marks / Gradebook", "Academic", listOf("pdf", "csv"), listOf("classId", "assessmentId"), "marks", false),
        ExportTypeDto("homework_report", "Homework Report", "Academic", listOf("csv"), listOf("homeworkId"), "homework", false),
        ExportTypeDto("leave_requests", "Leave Requests", "Academic", listOf("csv"), listOf("dateFrom", "dateTo"), "leave", false),
        ExportTypeDto("fee_report", "Fee Report", "Finance", listOf("pdf", "csv"), listOf("classId", "status", "dateFrom", "dateTo"), "fees", true),
        ExportTypeDto("staff_directory", "Staff Directory", "Staff", listOf("pdf", "csv"), emptyList(), "staff", true),
        ExportTypeDto("admission_enquiries", "Admission Enquiries", "Students", listOf("csv"), listOf("status"), "admissions", true),
        ExportTypeDto("event_registrations", "Event Registrations", "Operations", listOf("pdf", "csv"), listOf("eventId"), "events", true),
        ExportTypeDto("transport_assignments", "Transport Assignments", "Operations", listOf("pdf", "csv"), listOf("routeId"), "transport", true),
        ExportTypeDto("health_records", "Health Records", "Students", listOf("pdf", "csv"), listOf("classId"), "health", true),
        ExportTypeDto("alumni_directory", "Alumni Directory", "Operations", listOf("csv"), emptyList(), "alumni", true),
    )

    fun getExportTypes(role: String): ExportTypesResponse {
        val isAdmin = role in setOf("school_admin", "admin", "super_admin")
        val filtered = if (isAdmin) allExportTypes else allExportTypes.filter { !it.adminOnly }
        return ExportTypesResponse(filtered)
    }

    suspend fun listAssessmentsForExport(
        schoolId: UUID,
        classId: String?,
        role: String,
        userId: UUID,
    ): ExportAssessmentsResponse {
        val isAdmin = role in setOf("school_admin", "admin", "super_admin")
        val className = classId?.let { resolveClassName(it) }

        val rows = dbQuery {
            var q = AssessmentsTable.selectAll()
                .where {
                    (AssessmentsTable.schoolId eq schoolId) and
                        (AssessmentsTable.isActive eq true)
                }

            if (!isAdmin) {
                q = q.andWhere { AssessmentsTable.teacherId eq userId }
            }

            if (className != null) {
                q = q.andWhere { AssessmentsTable.className eq className }
            }

            q.orderBy(AssessmentsTable.createdAt, SortOrder.DESC)
                .map { row ->
                    ExportAssessmentDto(
                        id = row[AssessmentsTable.id].value.toString(),
                        name = row[AssessmentsTable.name],
                        subject = row[AssessmentsTable.subject],
                        className = row[AssessmentsTable.className],
                        section = row[AssessmentsTable.section],
                        maxMarks = row[AssessmentsTable.maxMarks],
                        examDate = row[AssessmentsTable.examDate]?.toString(),
                        status = row[AssessmentsTable.status],
                    )
                }
        }
        return ExportAssessmentsResponse(rows)
    }

    // ── Main orchestrator ──────────────────────────────────────────────

    suspend fun generateExport(
        schoolId: UUID,
        userId: UUID,
        role: String,
        request: ExportRequest,
    ): ExportResponse {
        val isAdmin = role in setOf("school_admin", "admin", "super_admin")
        val exportType = allExportTypes.find { it.type == request.type }
            ?: throw IllegalArgumentException("Unknown export type: ${request.type}")

        // Access control: teacher requesting admin-only export
        if (exportType.adminOnly && !isAdmin) {
            throw SecurityException("You don't have access to this export type")
        }

        // For teachers: verify class ownership if classId is provided
        if (!isAdmin && request.classId != null) {
            verifyTeacherClassAccess(schoolId, userId, request.classId)
        }

        // Fetch branding
        val branding = getExportBranding(schoolId)

        // Fetch data
        val data = fetchExportData(schoolId, request, isAdmin, userId)

        if (data.rows.isEmpty()) {
            return ExportResponse(format = request.format, message = "No data found for the selected filters")
        }

        // Generate file
        val (fileBytes, fileName, contentType) = when (request.format.lowercase()) {
            "pdf" -> {
                val pdf = BrandedPdfExporter.generate(
                    branding = branding,
                    title = data.title,
                    subtitle = data.subtitle,
                    columns = data.columns,
                    rows = data.rows,
                    summaryRows = data.summaryRows,
                )
                Triple(pdf, "${request.type}_${data.subtitle.toFileName()}_${today()}.pdf", "application/pdf")
            }
            "csv" -> {
                val csv = BrandedCsvExporter.generate(
                    branding = branding,
                    title = data.title,
                    subtitle = data.subtitle,
                    columns = data.columns,
                    rows = data.rows,
                )
                Triple(csv.toByteArray(), "${request.type}_${data.subtitle.toFileName()}_${today()}.csv", "text/csv")
            }
            else -> throw IllegalArgumentException("Unsupported format: ${request.format}")
        }

        // Upload to Supabase Storage
        val uploadResult = SupabaseStorage.upload(
            schoolId = schoolId,
            kind = "DOCUMENT",
            bytes = fileBytes,
            contentType = contentType,
        )

        if (uploadResult == null) {
            log.warn("Storage upload failed for export {} — returning base64 data URL fallback", request.type)
            val b64 = java.util.Base64.getEncoder().encodeToString(fileBytes)
            return ExportResponse(
                downloadUrl = null,
                fileName = fileName,
                fileSize = fileBytes.size.toLong(),
                format = request.format,
                dataUrl = "data:$contentType;base64,$b64",
            )
        }

        return ExportResponse(
            downloadUrl = uploadResult.url,
            fileName = fileName,
            fileSize = uploadResult.sizeBytes,
            format = request.format,
        )
    }

    // ── Teacher access verification ────────────────────────────────────

    private suspend fun verifyTeacherClassAccess(schoolId: UUID, teacherId: UUID, classId: String) = dbQuery {
        val cid = runCatching { UUID.fromString(classId) }.getOrNull()
            ?: throw SecurityException("Invalid class ID")

        val hasAccess = TeacherSubjectAssignmentsTable.selectAll()
            .where {
                (TeacherSubjectAssignmentsTable.schoolId eq schoolId) and
                (TeacherSubjectAssignmentsTable.teacherId eq teacherId) and
                (TeacherSubjectAssignmentsTable.classId eq cid) and
                (TeacherSubjectAssignmentsTable.isActive eq true)
            }
            .any()

        if (!hasAccess) {
            throw SecurityException("You don't have access to this class")
        }
    }

    // ── Data fetcher: dispatches to type-specific queries ──────────────

    private suspend fun fetchExportData(
        schoolId: UUID,
        request: ExportRequest,
        isAdmin: Boolean,
        userId: UUID,
    ): ExportData = dbQuery {
        when (request.type) {
            "student_roster" -> fetchStudentRoster(schoolId, request)
            "attendance_report" -> fetchAttendanceReport(schoolId, request)
            "test_marks" -> fetchTestMarks(schoolId, request)
            "fee_report" -> fetchFeeReport(schoolId, request)
            "staff_directory" -> fetchStaffDirectory(schoolId)
            "homework_report" -> fetchHomeworkReport(schoolId, request)
            "admission_enquiries" -> fetchAdmissionEnquiries(schoolId, request)
            "leave_requests" -> fetchLeaveRequests(schoolId, request)
            "transport_assignments" -> fetchTransportAssignments(schoolId, request)
            "health_records" -> fetchHealthRecords(schoolId, request)
            "alumni_directory" -> fetchAlumniDirectory(schoolId)
            "event_registrations" -> fetchEventRegistrations(schoolId, request)
            else -> throw IllegalArgumentException("Unknown export type: ${request.type}")
        }
    }

    // ── Type-specific queries ──────────────────────────────────────────

    private fun fetchStudentRoster(schoolId: UUID, req: ExportRequest): ExportData {
        var query = StudentsTable.selectAll()
            .where { StudentsTable.schoolId eq schoolId }

        val className = req.classId?.let { resolveClassName(it) }
        if (className != null) {
            query = query.andWhere { StudentsTable.className eq className }
        }

        val rows = query.orderBy(StudentsTable.rollNumber)
            .map { row ->
                listOf(
                    row[StudentsTable.studentCode] ?: "",
                    row[StudentsTable.fullName] ?: "",
                    row[StudentsTable.className] ?: "",
                    row[StudentsTable.section] ?: "",
                    row[StudentsTable.rollNumber] ?: "",
                    row[StudentsTable.parentPhone] ?: "",
                    if (row[StudentsTable.isActive]) "Active" else "Inactive",
                )
            }

        return ExportData(
            title = "Student Roster",
            subtitle = className ?: "All Classes",
            columns = listOf("Student Code", "Name", "Class", "Section", "Roll No", "Parent Phone", "Status"),
            rows = rows,
        )
    }

    private suspend fun fetchAttendanceReport(schoolId: UUID, req: ExportRequest): ExportData {
        var query = AttendanceRecordsTable.selectAll()
            .where {
                (AttendanceRecordsTable.schoolId eq schoolId) and
                (AttendanceRecordsTable.type eq "student")
            }

        val className = req.classId?.let { resolveClassName(it) }
        if (className != null) {
            query = query.andWhere { AttendanceRecordsTable.grade eq className }
        }

        val dateFrom = req.dateFrom?.let { LocalDate.parse(it) }
        val dateTo = req.dateTo?.let { LocalDate.parse(it) }
        if (dateFrom != null) {
            query = query.andWhere { AttendanceRecordsTable.date greaterEq dateFrom }
        }
        if (dateTo != null) {
            query = query.andWhere { AttendanceRecordsTable.date lessEq dateTo }
        }

        // Pre-fetch student names for the typed studentId FK
        val studentNameMap = mutableMapOf<UUID, String>()
        val studentCodeMap = mutableMapOf<String, String>()
        dbQuery {
            StudentsTable.selectAll()
                .where { StudentsTable.schoolId eq schoolId }
                .forEach { s ->
                    val sid = s[StudentsTable.id].value
                    studentNameMap[sid] = s[StudentsTable.fullName]
                    studentCodeMap[s[StudentsTable.studentCode]] = s[StudentsTable.fullName]
                }
        }

        val rawRows = query.orderBy(AttendanceRecordsTable.date).toList()

        val rows = rawRows.map { row ->
            val studentUid = row[AttendanceRecordsTable.studentId]
            val personId = row[AttendanceRecordsTable.personId]
            val studentName = studentUid?.let { studentNameMap[it] }
                ?: personId?.let { studentCodeMap[it] }
                ?: ""
            listOf(
                row[AttendanceRecordsTable.date].toString(),
                studentName,
                personId ?: "",
                row[AttendanceRecordsTable.grade] ?: "",
                row[AttendanceRecordsTable.status] ?: "",
                row[AttendanceRecordsTable.attSource] ?: "",
            )
        }

        // Summary: counts by status (only marked records — unmarked are NOT counted as present)
        val presentCount = rawRows.count { it[AttendanceRecordsTable.status] == "present" }
        val absentCount = rawRows.count { it[AttendanceRecordsTable.status] == "absent" }
        val lateCount = rawRows.count { it[AttendanceRecordsTable.status] == "late" }
        val leaveCount = rawRows.count { it[AttendanceRecordsTable.status] == "leave" }
        val totalMarked = rawRows.size

        val summaryRows = listOf(
            "Total Marked: $totalMarked | Present: $presentCount | Absent: $absentCount | Late: $lateCount | Leave: $leaveCount",
            "Note: Unmarked students are not included in this report.",
        )

        return ExportData(
            title = "Attendance Report",
            subtitle = listOfNotNull(className, req.dateFrom, req.dateTo).joinToString(" — "),
            columns = listOf("Date", "Student Name", "Student Code", "Class", "Status", "Source"),
            rows = rows,
            summaryRows = summaryRows,
        )
    }

    private fun fetchTestMarks(schoolId: UUID, req: ExportRequest): ExportData {
        val assessmentId = req.assessmentId?.let { runCatching { UUID.fromString(it) }.getOrNull() }
            ?: throw IllegalArgumentException("Assessment ID is required for test marks export")

        val assessment = AssessmentsTable.selectAll()
            .where { AssessmentsTable.id eq assessmentId }
            .singleOrNull()
            ?: throw IllegalArgumentException("Assessment not found")

        val maxMarks = assessment[AssessmentsTable.maxMarks]
        val passMarks = assessment[AssessmentsTable.passMarks] ?: 0

        val rows = AssessmentMarksTable.selectAll()
            .where { AssessmentMarksTable.assessmentId eq assessmentId }
            .map { row ->
                val marks = row[AssessmentMarksTable.marks] ?: 0.0
                val isAbsent = row[AssessmentMarksTable.isAbsent]
                val percentage = if (maxMarks > 0) (marks / maxMarks * 100).toInt() else 0
                val result = when {
                    isAbsent -> "Absent"
                    marks >= passMarks -> "Pass"
                    else -> "Fail"
                }
                listOf(
                    row[AssessmentMarksTable.studentId] ?: "",
                    row[AssessmentMarksTable.studentName] ?: "",
                    if (isAbsent) "Absent" else marks.toString(),
                    if (isAbsent) "-" else "$percentage%",
                    result,
                    row[AssessmentMarksTable.remark] ?: "",
                )
            }

        return ExportData(
            title = "Test Marks",
            subtitle = "${assessment[AssessmentsTable.name]} — ${assessment[AssessmentsTable.subject]} (${assessment[AssessmentsTable.className]}-${assessment[AssessmentsTable.section]})",
            columns = listOf("Student Code", "Student Name", "Marks", "Percentage", "Result", "Remark"),
            rows = rows,
            summaryRows = listOf(
                "Max Marks: $maxMarks | Pass Marks: $passMarks",
                "Total Students: ${rows.size}",
            ),
        )
    }

    private fun fetchFeeReport(schoolId: UUID, req: ExportRequest): ExportData {
        var query = FeeRecordsTable.selectAll()
            .where { FeeRecordsTable.schoolId eq schoolId }

        val className = req.classId?.let { resolveClassName(it) }
        if (className != null) {
            val studentCodes = StudentsTable.selectAll()
                .where {
                    (StudentsTable.schoolId eq schoolId) and
                    (StudentsTable.className eq className)
                }
                .map { it[StudentsTable.studentCode] }

            val childIds = if (studentCodes.isNotEmpty()) {
                com.littlebridge.enrollplus.db.ChildrenTable.selectAll()
                    .where { com.littlebridge.enrollplus.db.ChildrenTable.studentCode inList studentCodes }
                    .map { it[com.littlebridge.enrollplus.db.ChildrenTable.id].value }
            } else emptyList()

            if (childIds.isNotEmpty()) {
                query = query.andWhere { FeeRecordsTable.childId inList childIds }
            } else {
                query = query.andWhere { FeeRecordsTable.childId eq UUID.randomUUID() }
            }
        }

        if (!req.status.isNullOrBlank()) {
            query = query.andWhere { FeeRecordsTable.status eq req.status }
        }

        if (!req.dateFrom.isNullOrBlank()) {
            val df = req.dateFrom!!
            query = query.andWhere { FeeRecordsTable.dueDate greaterEq df }
        }
        if (!req.dateTo.isNullOrBlank()) {
            val dt = req.dateTo!!
            query = query.andWhere { FeeRecordsTable.dueDate lessEq dt }
        }

        val rows = query.map { row ->
                val amount = row[FeeRecordsTable.amount]
                val scholarship = row[FeeRecordsTable.scholarshipAmount] ?: 0.0
                val netPayable = amount - scholarship
                listOf(
                    row[FeeRecordsTable.childId]?.toString() ?: "",
                    row[FeeRecordsTable.title] ?: "",
                    row[FeeRecordsTable.category] ?: "",
                    amount.toString(),
                    scholarship.toString(),
                    netPayable.toString(),
                    row[FeeRecordsTable.status] ?: "",
                    row[FeeRecordsTable.dueDate] ?: "",
                )
            }

        return ExportData(
            title = "Fee Report",
            subtitle = listOfNotNull(className, req.status, req.dateFrom, req.dateTo).joinToString(" — "),
            columns = listOf("Student Code", "Title", "Category", "Amount", "Scholarship", "Net Payable", "Status", "Due Date"),
            rows = rows,
        )
    }

    private fun fetchStaffDirectory(schoolId: UUID): ExportData {
        val teachingRows = FacultyTable.selectAll()
            .where { FacultyTable.schoolId eq schoolId }
            .map { row ->
                listOf(
                    "Teaching",
                    row[FacultyTable.name] ?: "",
                    row[FacultyTable.department] ?: "",
                    "Teacher",
                    "",
                    "",
                    if (row[FacultyTable.isActive]) "Active" else "Inactive",
                )
            }

        val nonTeachingRows = NonTeachingStaffTable.selectAll()
            .where { NonTeachingStaffTable.schoolId eq schoolId }
            .map { row ->
                listOf(
                    "Non-Teaching",
                    row[NonTeachingStaffTable.fullName] ?: "",
                    row[NonTeachingStaffTable.department] ?: "",
                    row[NonTeachingStaffTable.role] ?: "",
                    row[NonTeachingStaffTable.phone] ?: "",
                    row[NonTeachingStaffTable.email] ?: "",
                    if (row[NonTeachingStaffTable.isActive]) "Active" else "Inactive",
                )
            }

        return ExportData(
            title = "Staff Directory",
            subtitle = "All Staff",
            columns = listOf("Staff Type", "Name", "Department", "Role", "Phone", "Email", "Status"),
            rows = teachingRows + nonTeachingRows,
        )
    }

    private fun fetchHomeworkReport(schoolId: UUID, req: ExportRequest): ExportData {
        val homeworkId = req.homeworkId?.let { runCatching { UUID.fromString(it) }.getOrNull() }
            ?: throw IllegalArgumentException("Homework ID is required for homework report export")

        val homework = HomeworkTable.selectAll()
            .where { HomeworkTable.id eq homeworkId }
            .singleOrNull()
            ?: throw IllegalArgumentException("Homework not found")

        val rows = HomeworkSubmissionsTable.selectAll()
            .where { HomeworkSubmissionsTable.homeworkId eq homeworkId }
            .map { row ->
                listOf(
                    row[HomeworkSubmissionsTable.studentId] ?: "",
                    row[HomeworkSubmissionsTable.studentUuid]?.toString() ?: "",
                    row[HomeworkSubmissionsTable.status] ?: "",
                    row[HomeworkSubmissionsTable.submittedAt]?.toString() ?: "",
                    row[HomeworkSubmissionsTable.grade]?.toString() ?: "",
                )
            }

        return ExportData(
            title = "Homework Report",
            subtitle = "${homework[HomeworkTable.title]} — ${homework[HomeworkTable.subject]} (${homework[HomeworkTable.className]}-${homework[HomeworkTable.section]})",
            columns = listOf("Student Code", "Student UUID", "Status", "Submitted At", "Grade"),
            rows = rows,
        )
    }

    private fun fetchAdmissionEnquiries(schoolId: UUID, req: ExportRequest): ExportData {
        var query = AdmissionEnquiriesTable.selectAll()
            .where { AdmissionEnquiriesTable.schoolId eq schoolId }

        if (!req.status.isNullOrBlank()) {
            query = query.andWhere { AdmissionEnquiriesTable.status eq req.status }
        }

        val rows = query.orderBy(AdmissionEnquiriesTable.createdAt, SortOrder.DESC)
            .map { row ->
                listOf(
                    row[AdmissionEnquiriesTable.studentName] ?: "",
                    row[AdmissionEnquiriesTable.parentName] ?: "",
                    row[AdmissionEnquiriesTable.parentPhone] ?: "",
                    row[AdmissionEnquiriesTable.parentEmail] ?: "",
                    row[AdmissionEnquiriesTable.className] ?: "",
                    row[AdmissionEnquiriesTable.date] ?: "",
                    row[AdmissionEnquiriesTable.status] ?: "",
                    row[AdmissionEnquiriesTable.admissionSource] ?: "",
                    row[AdmissionEnquiriesTable.notes] ?: "",
                )
            }

        return ExportData(
            title = "Admission Enquiries",
            subtitle = req.status ?: "All Status",
            columns = listOf("Student Name", "Parent Name", "Parent Phone", "Parent Email", "Class", "Date", "Status", "Source", "Notes"),
            rows = rows,
        )
    }

    private fun fetchLeaveRequests(schoolId: UUID, req: ExportRequest): ExportData {
        var query = LeaveRequestsTable.selectAll()
            .where { LeaveRequestsTable.schoolId eq schoolId }

        if (!req.dateFrom.isNullOrBlank()) {
            val df = req.dateFrom!!
            query = query.andWhere { LeaveRequestsTable.dateFrom greaterEq df }
        }
        if (!req.dateTo.isNullOrBlank()) {
            val dt = req.dateTo!!
            query = query.andWhere { LeaveRequestsTable.dateTo lessEq dt }
        }

        val rows = query.orderBy(LeaveRequestsTable.createdAt, SortOrder.DESC)
            .map { row ->
                listOf(
                    row[LeaveRequestsTable.requesterName] ?: "",
                    row[LeaveRequestsTable.requesterRole] ?: "",
                    row[LeaveRequestsTable.className] ?: "",
                    row[LeaveRequestsTable.section] ?: "",
                    row[LeaveRequestsTable.dateFrom] ?: "",
                    row[LeaveRequestsTable.dateTo] ?: "",
                    row[LeaveRequestsTable.reason] ?: "",
                    row[LeaveRequestsTable.status] ?: "",
                )
            }

        return ExportData(
            title = "Leave Requests",
            subtitle = listOfNotNull(req.dateFrom, req.dateTo).joinToString(" — "),
            columns = listOf("Name", "Role", "Class", "Section", "From", "To", "Reason", "Status"),
            rows = rows,
        )
    }

    private fun fetchTransportAssignments(schoolId: UUID, req: ExportRequest): ExportData {
        val routeId = req.routeId?.let { runCatching { UUID.fromString(it) }.getOrNull() }

        var query = TransportAssignmentsTable.selectAll()
            .where {
                (TransportAssignmentsTable.schoolId eq schoolId) and
                (TransportAssignmentsTable.isActive eq true)
            }

        if (routeId != null) {
            query = query.andWhere { TransportAssignmentsTable.routeId eq routeId }
        }

        val rows = query.map { row ->
            val routeName = TransportRoutesTable.selectAll()
                .where { TransportRoutesTable.id eq row[TransportAssignmentsTable.routeId] }
                .singleOrNull()?.get(TransportRoutesTable.name) ?: ""

            val vehicleNumber = TransportVehiclesTable.selectAll()
                .where { TransportVehiclesTable.id eq row[TransportAssignmentsTable.vehicleId] }
                .singleOrNull()?.get(TransportVehiclesTable.busNumber) ?: ""

            val stopName = TransportStopsTable.selectAll()
                .where { TransportStopsTable.id eq row[TransportAssignmentsTable.stopId] }
                .singleOrNull()?.get(TransportStopsTable.name) ?: ""

            val student = StudentsTable.selectAll()
                .where { StudentsTable.id eq row[TransportAssignmentsTable.studentId] }
                .singleOrNull()

            listOf(
                routeName,
                vehicleNumber,
                stopName,
                student?.get(StudentsTable.fullName) ?: "",
                student?.get(StudentsTable.className) ?: "",
                student?.get(StudentsTable.section) ?: "",
                student?.get(StudentsTable.parentPhone) ?: "",
            )
        }

        val routeLabel = routeId?.let {
            TransportRoutesTable.selectAll()
                .where { TransportRoutesTable.id eq it }
                .singleOrNull()?.get(TransportRoutesTable.name)
        } ?: "All Routes"

        return ExportData(
            title = "Transport Assignments",
            subtitle = routeLabel,
            columns = listOf("Route", "Vehicle", "Stop", "Student Name", "Class", "Section", "Parent Phone"),
            rows = rows,
        )
    }

    private fun fetchHealthRecords(schoolId: UUID, req: ExportRequest): ExportData {
        var query = StudentHealthProfilesTable.selectAll()
            .where { StudentHealthProfilesTable.schoolId eq schoolId }

        val className = req.classId?.let { resolveClassName(it) }
        if (className != null) {
            val studentIds = StudentsTable.selectAll()
                .where {
                    (StudentsTable.schoolId eq schoolId) and
                    (StudentsTable.className eq className)
                }
                .map { it[StudentsTable.id].value }
            if (studentIds.isNotEmpty()) {
                query = query.andWhere { StudentHealthProfilesTable.studentId inList studentIds }
            }
        }

        val rows = query.map { row ->
            val student = StudentsTable.selectAll()
                .where { StudentsTable.id eq row[StudentHealthProfilesTable.studentId] }
                .singleOrNull()

            listOf(
                student?.get(StudentsTable.fullName) ?: "",
                student?.get(StudentsTable.className) ?: "",
                student?.get(StudentsTable.section) ?: "",
                row[StudentHealthProfilesTable.bloodGroup] ?: "",
                row[StudentHealthProfilesTable.allergies] ?: "",
                row[StudentHealthProfilesTable.chronicConditions] ?: "",
                row[StudentHealthProfilesTable.emergencyContactName] ?: "",
                row[StudentHealthProfilesTable.emergencyContactPhone] ?: "",
                row[StudentHealthProfilesTable.doctorName] ?: "",
                row[StudentHealthProfilesTable.doctorPhone] ?: "",
            )
        }

        return ExportData(
            title = "Health Records Summary",
            subtitle = className ?: "All Classes",
            columns = listOf("Student Name", "Class", "Section", "Blood Group", "Allergies", "Chronic Conditions", "Emergency Contact", "Emergency Phone", "Doctor", "Doctor Phone"),
            rows = rows,
        )
    }

    private fun fetchAlumniDirectory(schoolId: UUID): ExportData {
        val rows = AlumniTable.selectAll()
            .where { AlumniTable.schoolId eq schoolId }
            .orderBy(AlumniTable.graduationYear, SortOrder.DESC)
            .map { row ->
                listOf(
                    row[AlumniTable.name],
                    row[AlumniTable.graduationYear].toString(),
                    row[AlumniTable.lastClass] ?: "",
                    row[AlumniTable.currentProfession] ?: "",
                    row[AlumniTable.company] ?: "",
                    row[AlumniTable.city] ?: "",
                    row[AlumniTable.email] ?: "",
                    row[AlumniTable.phone] ?: "",
                    row[AlumniTable.linkedinUrl] ?: "",
                    if (row[AlumniTable.isMentor]) "Yes" else "No",
                    row[AlumniTable.mentorExpertise] ?: "",
                    row[AlumniTable.verificationStatus] ?: "",
                )
            }

        return ExportData(
            title = "Alumni Directory",
            subtitle = "All Alumni",
            columns = listOf("Name", "Graduation Year", "Last Class", "Profession", "Company", "City", "Email", "Phone", "LinkedIn", "Mentor", "Mentor Expertise", "Verification Status"),
            rows = rows,
        )
    }

    private fun fetchEventRegistrations(schoolId: UUID, req: ExportRequest): ExportData {
        val eventId = req.eventId?.let { runCatching { UUID.fromString(it) }.getOrNull() }
            ?: throw IllegalArgumentException("Valid Event ID is required for event registration export")

        val rows = EventRegistrationsTable.selectAll()
            .where {
                (EventRegistrationsTable.schoolId eq schoolId) and
                (EventRegistrationsTable.eventId eq eventId) and
                (EventRegistrationsTable.status neq "CANCELLED")
            }
            .orderBy(EventRegistrationsTable.registeredAt, SortOrder.DESC)
            .map { row ->
                val parent = AppUsersTable.selectAll()
                    .where { AppUsersTable.id eq row[EventRegistrationsTable.parentUserId] }
                    .singleOrNull()

                val slotTime = row[EventRegistrationsTable.slotId]?.let { sid ->
                    EventSlotsTable.selectAll()
                        .where { EventSlotsTable.id eq sid }
                        .singleOrNull()
                        ?.let { "${it[EventSlotsTable.startTime]} - ${it[EventSlotsTable.endTime]}" }
                } ?: "Open Event"

                listOf(
                    parent?.get(AppUsersTable.fullName) ?: "",
                    parent?.get(AppUsersTable.phone) ?: "",
                    row[EventRegistrationsTable.attendeeCount].toString(),
                    slotTime,
                    row[EventRegistrationsTable.studentId]?.toString() ?: "",
                    row[EventRegistrationsTable.status],
                    row[EventRegistrationsTable.registeredAt].toString(),
                )
            }

        return ExportData(
            title = "Event Registrations",
            subtitle = "Event: $eventId",
            columns = listOf("Parent Name", "Parent Mobile", "Attendee Count", "Slot Time", "Student ID", "Status", "Registered At"),
            rows = rows,
            summaryRows = if (rows.isEmpty()) listOf("No registrations found for this event") else null,
        )
    }

    // ── Helpers ────────────────────────────────────────────────────────

    private fun resolveClassName(classId: String): String? {
        val cid = runCatching { UUID.fromString(classId) }.getOrNull() ?: return null
        return SchoolClassesTable.selectAll()
            .where { SchoolClassesTable.id eq cid }
            .singleOrNull()
            ?.get(SchoolClassesTable.name)
    }

    private fun today(): String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    private fun String.toFileName(): String =
        replace(Regex("[^a-zA-Z0-9]"), "_").take(30)

    // ── Internal data holder ───────────────────────────────────────────

    private data class ExportData(
        val title: String,
        val subtitle: String,
        val columns: List<String>,
        val rows: List<List<String>>,
        val summaryRows: List<String>? = null,
    )
}
