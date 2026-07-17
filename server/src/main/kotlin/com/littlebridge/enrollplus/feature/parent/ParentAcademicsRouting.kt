/*
 * File: ParentAcademicsRouting.kt
 * Module: feature.parent
 *
 * RA-43 + RA-56: the parent academic read plane. Teachers WRITE attendance_records,
 * assessments + assessment_marks and syllabus_units; before this file NO parent
 * endpoint READ them, so ParentAcademicsScreenV2 rendered VComingSoon for
 * Attendance / Marks / Syllabus — the single most important parent value did not
 * exist. These endpoints are all CHILD-SCOPED (RA-56): a `child_id` path segment
 * resolves a child that must belong to the calling parent, then joins via
 * children.student_code → attendance_records.person_id / assessment_marks.student_id
 * and children.current_grade → syllabus_units.class_name.
 *
 * Endpoints (JWT, parent):
 *   GET /api/v1/parent/child/{id}/attendance  → monthly summary + day records
 *   GET /api/v1/parent/child/{id}/marks       → published assessments + child's score
 *   GET /api/v1/parent/child/{id}/syllabus    → per-subject coverage for the child's class
 */
package com.littlebridge.enrollplus.feature.parent

import com.littlebridge.enrollplus.core.fail
import com.littlebridge.enrollplus.core.ok
import com.littlebridge.enrollplus.core.principalUserId
import com.littlebridge.enrollplus.db.AppUsersTable
import com.littlebridge.enrollplus.db.AssessmentMarksTable
import com.littlebridge.enrollplus.db.AssessmentsTable
import com.littlebridge.enrollplus.db.AttendanceRecordsTable
import com.littlebridge.enrollplus.db.ChildrenTable
import com.littlebridge.enrollplus.db.CurriculumUnitsTable
import com.littlebridge.enrollplus.db.DailyClassLogTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.HolidayListTable
import com.littlebridge.enrollplus.db.StudentsTable
import com.littlebridge.enrollplus.db.SchoolDayConfigTable
import com.littlebridge.enrollplus.db.SchoolDaySlotsTable
import com.littlebridge.enrollplus.db.SYSTEM_SCHOOL_ID
import com.littlebridge.enrollplus.db.SyllabusProgressTable
import com.littlebridge.enrollplus.db.SyllabusPacePlanTable
import com.littlebridge.enrollplus.db.SyllabusUnitsTable
import com.littlebridge.enrollplus.db.SyllabusQuizzesTable
import com.littlebridge.enrollplus.db.SyllabusQuizQuestionsTable
import com.littlebridge.enrollplus.db.SyllabusQuizAnswersTable
import com.littlebridge.enrollplus.db.TeacherPeriodsTable
import com.littlebridge.enrollplus.db.TeacherSubjectAssignmentsTable
import com.littlebridge.enrollplus.feature.ai.SyllabusAiService
import com.littlebridge.enrollplus.feature.gamification.XpHooks
import com.littlebridge.enrollplus.feature.teacher.MatchPairSer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.request.receive
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

// T-101: teacher_periods.start_time/end_time are now typed `time` (LocalTime).
// Format back to the "HH:mm" wire contract this screen's DTOs expect.
private val PARENT_HHMM: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

// ── DTOs ─────────────────────────────────────────────────────────────────────

@Serializable
data class ParentAttendanceDayDto(
    val date: String,
    val status: String, // present | absent | late
)

/**
 * A non-instructional day for the child's school — the canonical representation of
 * "not a school day" so the parent dashboard can tell a holiday/vacation apart from a
 * genuine absence. Sourced from `holiday_list` (type Public|School, frequency
 * weekly|monthly|yearly). Weekly Sundays are derived client-side from the weekday, so
 * a school need not enumerate every Sunday for the calendar to read correctly.
 */
@Serializable
data class ParentHolidayDto(
    val date: String,           // "YYYY-MM-DD"; empty when it's a recurring (weekly) rule
    val title: String,
    val type: String,           // Public | School
    val frequency: String,      // weekly | monthly | yearly
)

@Serializable
data class ParentAttendanceData(
    @SerialName("child_name") val childName: String,
    @SerialName("present_days") val presentDays: Int,
    @SerialName("absent_days") val absentDays: Int,
    @SerialName("late_days") val lateDays: Int,
    @SerialName("total_days") val totalDays: Int,
    @SerialName("attendance_rate") val attendanceRate: Int, // 0..100
    val records: List<ParentAttendanceDayDto> = emptyList(),
    // RA-PP1: declared non-school days for the child's school, so the parent dashboard
    // can render holidays / vacations distinctly from real absences. Honest empty when a
    // school hasn't published a holiday list.
    val holidays: List<ParentHolidayDto> = emptyList(),
)

// ── Timetable (RA-PP1: parent read of the child's class weekly schedule) ──────

@Serializable
data class ParentPeriodDto(
    @SerialName("start_time") val startTime: String, // "HH:mm"
    @SerialName("end_time") val endTime: String,      // "HH:mm"
    val subject: String,
    val room: String,
    @SerialName("teacher_name") val teacherName: String, // "" when unassigned/unknown
)

@Serializable
data class ParentBellSlotDto(
    @SerialName("slot_index") val slotIndex: Int,
    @SerialName("slot_type") val slotType: String,
    val label: String,
    @SerialName("start_time") val startTime: String,
    @SerialName("end_time") val endTime: String,
)

@Serializable
data class ParentTimetableDayDto(
    val weekday: Int,                              // 1=Mon … 7=Sun
    val periods: List<ParentPeriodDto>,
    @SerialName("now_index") val nowIndex: Int? = null,
    @SerialName("next_index") val nextIndex: Int? = null,
)

@Serializable
data class ParentTimetableData(
    @SerialName("child_name") val childName: String,
    @SerialName("class_name") val className: String,
    val weekdays: List<ParentTimetableDayDto> = emptyList(),
    @SerialName("bell_schedule") val bellSchedule: List<ParentBellSlotDto> = emptyList(),
)

@Serializable
data class ParentMarkDto(
    @SerialName("exam_name") val examName: String,
    val subject: String,
    val marks: Double?, // null = not yet entered for this child
    @SerialName("max_marks") val maxMarks: Int,
    @SerialName("exam_date") val examDate: String? = null,
)

@Serializable
data class ParentMarksData(
    @SerialName("child_name") val childName: String,
    val results: List<ParentMarkDto> = emptyList(),
)

@Serializable
data class ParentSyllabusUnitDto(
    val title: String,
    @SerialName("is_covered") val isCovered: Boolean,
    @SerialName("covered_on") val coveredOn: String? = null,
)

@Serializable
data class ParentSyllabusSubjectDto(
    val subject: String,
    @SerialName("progress") val progress: Int, // 0..100
    val units: List<ParentSyllabusUnitDto> = emptyList(),
)

@Serializable
data class ParentSyllabusData(
    @SerialName("child_name") val childName: String,
    @SerialName("class_name") val className: String,
    val subjects: List<ParentSyllabusSubjectDto> = emptyList(),
)

// ── Daily Summary DTOs (Agentic Syllabus) ────────────────────────────────────

@Serializable
data class ParentDailyLogEntryDto(
    val date: String,
    val subject: String,
    @SerialName("summary_text") val summaryText: String,
    @SerialName("coverage_pct") val coveragePct: Int,
    @SerialName("is_ai_estimated") val isAiEstimated: Boolean,
)

@Serializable
data class ParentDailySummaryData(
    @SerialName("child_name") val childName: String,
    @SerialName("class_name") val className: String,
    val date: String,
    val entries: List<ParentDailyLogEntryDto> = emptyList(),
    @SerialName("ai_summary") val aiSummary: String? = null,
)

// ── Syllabus V2 DTOs (typed curriculum_units) ────────────────────────────────

@Serializable
data class ParentSyllabusV2UnitDto(
    val id: String,
    val title: String,
    val depth: Int,
    @SerialName("is_covered") val isCovered: Boolean,
    @SerialName("coverage_pct") val coveragePct: Int,
    @SerialName("covered_on") val coveredOn: String? = null,
    @SerialName("is_ai_estimated") val isAiEstimated: Boolean = false,
)

@Serializable
data class ParentSyllabusV2SubjectDto(
    val subject: String,
    @SerialName("assignment_id") val assignmentId: String?,
    val progress: Int,
    @SerialName("is_ai_estimated") val isAiEstimated: Boolean = false,
    @SerialName("estimated_pct") val estimatedPct: Int = 0,
    val units: List<ParentSyllabusV2UnitDto> = emptyList(),
)

@Serializable
data class ParentSyllabusV2Data(
    @SerialName("child_name") val childName: String,
    @SerialName("class_name") val className: String,
    val subjects: List<ParentSyllabusV2SubjectDto> = emptyList(),
)

// ── Quiz DTOs (server-side, mirrors shared module) ──────────────────────────

@Serializable
data class ParentMatchPairDto(
    val left: String = "",
    val right: String = "",
)

@Serializable
data class ParentQuizDto(
    val id: String,
    val title: String = "",
    val subject: String = "",
    @SerialName("unit_title") val unitTitle: String = "",
    @SerialName("num_questions") val numQuestions: Int = 0,
    @SerialName("total_marks") val totalMarks: Int = 0,
    val status: String = "DRAFT",
    @SerialName("published_at") val publishedAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class ParentQuizListData(
    val quizzes: List<ParentQuizDto> = emptyList(),
)

@Serializable
data class ParentQuizQuestionDto(
    val id: String,
    val question: String,
    val options: List<String> = emptyList(),
    val marks: Int = 1,
    @SerialName("question_type") val questionType: String = "MCQ",
    @SerialName("match_pairs") val matchPairs: List<ParentMatchPairDto> = emptyList(),
)

@Serializable
data class ParentQuizDetailData(
    val id: String,
    val title: String = "",
    val subject: String = "",
    val questions: List<ParentQuizQuestionDto> = emptyList(),
    @SerialName("total_marks") val totalMarks: Int = 0,
)

@Serializable
data class QuizAnswerDto(
    @SerialName("question_id") val questionId: String,
    @SerialName("selected_index") val selectedIndex: Int = -1,
    @SerialName("answer_text") val answerText: String? = null,
)

@Serializable
data class QuizSubmitRequest(
    @SerialName("quiz_id") val quizId: String,
    val answers: List<QuizAnswerDto> = emptyList(),
    @SerialName("child_id") val childId: String? = null,
)

@Serializable
data class QuizQuestionResultDto(
    @SerialName("question_id") val questionId: String,
    val question: String,
    @SerialName("selected_index") val selectedIndex: Int,
    @SerialName("correct_index") val correctIndex: Int,
    val correct: Boolean,
    val explanation: String? = null,
    @SerialName("selected_answer") val selectedAnswer: String = "",
    @SerialName("correct_answer") val correctAnswer: String = "",
    @SerialName("question_type") val questionType: String = "MCQ",
)

@Serializable
data class QuizResultDto(
    val id: String,
    @SerialName("quiz_id") val quizId: String,
    val score: Int,
    @SerialName("total_marks") val totalMarks: Int,
    val percentage: Int,
    @SerialName("submitted_at") val submittedAt: String? = null,
    @SerialName("question_results") val questionResults: List<QuizQuestionResultDto> = emptyList(),
)

@Serializable
data class QuizSubmitResponse(
    val success: Boolean = true,
    val data: QuizResultDto,
)

// ── Leaderboard DTOs ────────────────────────────────────────────────────────

@Serializable
data class QuizLeaderboardEntryDto(
    val rank: Int,
    val studentName: String = "",
    val score: Int,
    @SerialName("total_marks") val totalMarks: Int,
    val percentage: Int,
    @SerialName("submitted_at") val submittedAt: String? = null,
    @SerialName("is_current_student") val isCurrentStudent: Boolean = false,
)

@Serializable
data class QuizLeaderboardData(
    val quizId: String,
    val quizTitle: String = "",
    val subject: String = "",
    val entries: List<QuizLeaderboardEntryDto> = emptyList(),
    @SerialName("total_participants") val totalParticipants: Int = 0,
)

@Serializable
data class QuizLeaderboardResponse(
    val success: Boolean = true,
    val data: QuizLeaderboardData,
)

// ── Resolved + authorized child (RA-56 ownership guard) ───────────────────────

private data class ResolvedChild(
    val childName: String,
    val schoolId: UUID?,
    val studentCode: String?,
    val studentId: UUID?,
    val grade: String?,
    val section: String,
    // RA-S19: true only when `section` came from a linked `students` row. When the
    // child is linked but the students row is missing/mismatched we fall back to
    // "A" — a section filter on that fallback silently hides marks. Callers MUST
    // relax the section filter to class-level when this is false.
    val sectionResolved: Boolean,
)

/**
 * Resolve the {id} child, asserting it belongs to the calling parent. Responds
 * with the right error envelope and returns null on any failure. This is the
 * RA-56 ownership gate — a parent can never read another family's child.
 */
private suspend fun ApplicationCall.requireOwnedChild(): ResolvedChild? {
    val uid = principalUserId()?.let { runCatching { UUID.fromString(it) }.getOrNull() } ?: run {
        fail("Invalid token", HttpStatusCode.Unauthorized); return null
    }
    val childId = parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() } ?: run {
        fail("A valid child id is required", HttpStatusCode.BadRequest, "BAD_CHILD_ID"); return null
    }
    val row = dbQuery {
        ChildrenTable.selectAll().where {
            (ChildrenTable.id eq childId) and
                (ChildrenTable.parentId eq uid) and
                (ChildrenTable.isActive eq true)
        }.singleOrNull()
    } ?: run {
        fail("Child not found", HttpStatusCode.NotFound, "CHILD_NOT_FOUND"); return null
    }
    // The student row (if linked) carries the canonical class+section, which is
    // more authoritative than the parent-typed currentGrade for syllabus joins.
    val studentCode = row[ChildrenTable.studentCode]
    val student = if (studentCode != null) dbQuery {
        StudentsTable.selectAll().where { StudentsTable.studentCode eq studentCode }.singleOrNull()
    } else null
    val linkedSection = student?.get(StudentsTable.section)
    return ResolvedChild(
        childName = row[ChildrenTable.childName],
        schoolId = row[ChildrenTable.schoolId],
        studentCode = studentCode,
        studentId = student?.get(StudentsTable.id)?.value,
        grade = student?.get(StudentsTable.className) ?: row[ChildrenTable.currentGrade],
        section = linkedSection ?: "A",
        sectionResolved = linkedSection != null,
    )
}

private fun resolveParentBellSchedule(schoolId: UUID, weekday: Int): List<ParentBellSlotDto> {
    fun fetchForSchool(sid: UUID): List<ParentBellSlotDto>? {
        val configs = SchoolDayConfigTable.selectAll()
            .where {
                (SchoolDayConfigTable.schoolId eq sid) and
                    (SchoolDayConfigTable.isActive eq true)
            }
            .toList()
        if (configs.isEmpty()) return null
        val matching = configs.firstOrNull { r ->
            val days = r[SchoolDayConfigTable.applicableDays]
                .split(",").map { it.trim().toIntOrNull() }.filterNotNull()
            weekday in days
        } ?: return null
        val cid = matching[SchoolDayConfigTable.id].value
        return SchoolDaySlotsTable.selectAll()
            .where { (SchoolDaySlotsTable.configId eq cid) and (SchoolDaySlotsTable.schoolId eq sid) }
            .orderBy(SchoolDaySlotsTable.slotIndex)
            .map { s ->
                ParentBellSlotDto(
                    slotIndex = s[SchoolDaySlotsTable.slotIndex],
                    slotType = s[SchoolDaySlotsTable.slotType],
                    label = s[SchoolDaySlotsTable.label],
                    startTime = s[SchoolDaySlotsTable.startTime].format(PARENT_HHMM),
                    endTime = s[SchoolDaySlotsTable.endTime].format(PARENT_HHMM),
                )
            }
    }
    return fetchForSchool(schoolId) ?: fetchForSchool(SYSTEM_SCHOOL_ID) ?: emptyList()
}

fun Route.parentAcademicsRouting() {
    authenticate("jwt") {
        route("/api/v1/parent/child/{id}") {

            // ── Attendance — month summary + per-day records ─────────────────
            get("/attendance") {
                val child = call.requireOwnedChild() ?: return@get
                if (child.schoolId == null || child.studentCode == null) {
                    // Child not yet linked to a school/student — honest empty, not an error.
                    call.ok(
                        ParentAttendanceData(
                            childName = child.childName,
                            presentDays = 0, absentDays = 0, lateDays = 0,
                            totalDays = 0, attendanceRate = 0, records = emptyList(),
                        ),
                        message = "No attendance feed yet",
                    )
                    return@get
                }
                val rows = dbQuery {
                    // T-201: teacher attendance writes use the typed studentId (UUID FK
                    // to students.id), not the legacy personId text column. Resolve the
                    // child's studentCode → students.id UUID first, then query by it.
                    val studentUuid = StudentsTable.selectAll().where {
                        (StudentsTable.schoolId eq child.schoolId) and
                            (StudentsTable.studentCode eq child.studentCode)
                    }.firstOrNull()?.get(StudentsTable.id)?.value

                    val query = if (studentUuid != null) {
                        AttendanceRecordsTable.selectAll().where {
                            (AttendanceRecordsTable.schoolId eq child.schoolId) and
                                (AttendanceRecordsTable.type eq "student") and
                                (AttendanceRecordsTable.studentId eq studentUuid)
                        }
                    } else {
                        // Fallback: legacy personId path (for pre-T-201 data)
                        AttendanceRecordsTable.selectAll().where {
                            (AttendanceRecordsTable.schoolId eq child.schoolId) and
                                (AttendanceRecordsTable.type eq "student") and
                                (AttendanceRecordsTable.personId eq child.studentCode)
                        }
                    }
                    query.orderBy(AttendanceRecordsTable.date, SortOrder.DESC).map {
                        ParentAttendanceDayDto(
                            date = it[AttendanceRecordsTable.date].toString(),
                            status = it[AttendanceRecordsTable.status].lowercase(),
                        )
                    }
                }
                val present = rows.count { it.status == "present" }
                val absent = rows.count { it.status == "absent" }
                val late = rows.count { it.status == "late" }
                val total = rows.size
                val rate = if (total > 0) (((present + late) * 100) / total) else 0
                // RA-PP1: the child's school holiday list — lets the parent dashboard tell a
                // declared non-school day apart from a real absence. Honest empty when none.
                val holidays = dbQuery {
                    HolidayListTable.selectAll().where {
                        HolidayListTable.schoolId eq child.schoolId
                    }.map {
                        ParentHolidayDto(
                            date = it[HolidayListTable.date],
                            title = it[HolidayListTable.title],
                            type = it[HolidayListTable.type],
                            frequency = it[HolidayListTable.frequency],
                        )
                    }
                }
                call.ok(
                    ParentAttendanceData(
                        childName = child.childName,
                        presentDays = present,
                        absentDays = absent,
                        lateDays = late,
                        totalDays = total,
                        attendanceRate = rate,
                        records = rows,
                        holidays = holidays,
                    ),
                    message = "Attendance loaded",
                )
            }

            // ── Marks — published assessments + this child's score ───────────
            get("/marks") {
                val child = call.requireOwnedChild() ?: return@get
                if (child.schoolId == null || child.studentCode == null) {
                    call.ok(ParentMarksData(childName = child.childName, results = emptyList()), message = "No published results yet")
                    return@get
                }
                val results = dbQuery {
                    // Only PUBLISHED assessments for the child's class are parent-visible (RA-43).
                    // RA-S19: the section filter is applied ONLY when the section came from a
                    // linked `students` row. When section is the "A" fallback (child linked but
                    // students row missing/mismatched), filtering on it silently hides every
                    // mark; we relax to class-level so the parent still sees their child's
                    // published results instead of an empty screen.
                    val assessments = AssessmentsTable.selectAll().where {
                        var cond = (AssessmentsTable.schoolId eq child.schoolId) and
                            (AssessmentsTable.className eq (child.grade ?: "")) and
                            (AssessmentsTable.isPublished eq true) and
                            (AssessmentsTable.isActive eq true)
                        if (child.sectionResolved) {
                            cond = cond and (AssessmentsTable.section eq child.section)
                        }
                        cond
                    }.orderBy(AssessmentsTable.createdAt, SortOrder.DESC).toList()

                    assessments.map { a ->
                        val aId = a[AssessmentsTable.id].value
                        val mark = AssessmentMarksTable.selectAll().where {
                            (AssessmentMarksTable.assessmentId eq aId) and
                                (AssessmentMarksTable.studentId eq child.studentCode)
                        }.singleOrNull()?.get(AssessmentMarksTable.marks)
                        ParentMarkDto(
                            examName = a[AssessmentsTable.name],
                            subject = a[AssessmentsTable.subject],
                            marks = mark,
                            maxMarks = a[AssessmentsTable.maxMarks],
                            examDate = a[AssessmentsTable.examDate]?.toString(),
                        )
                    }
                }
                call.ok(ParentMarksData(childName = child.childName, results = results), message = "Marks loaded")
            }

            // ── Syllabus — per-subject coverage for the child's class ────────
            get("/syllabus") {
                val child = call.requireOwnedChild() ?: return@get
                if (child.schoolId == null || child.grade == null) {
                    call.ok(ParentSyllabusData(childName = child.childName, className = child.grade ?: "", subjects = emptyList()), message = "No syllabus feed yet")
                    return@get
                }
                val subjects = dbQuery {
                    // New typed path FIRST: read from CurriculumUnitsTable + SyllabusProgressTable
                    // (where the teacher's one-tap toggle actually writes).
                    val assignments = TeacherSubjectAssignmentsTable.selectAll().where {
                        (TeacherSubjectAssignmentsTable.schoolId eq child.schoolId) and
                            (TeacherSubjectAssignmentsTable.className eq child.grade) and
                            (TeacherSubjectAssignmentsTable.isActive eq true)
                    }.let { rows ->
                        if (child.sectionResolved) {
                            rows.filter { it[TeacherSubjectAssignmentsTable.section] == child.section }
                        } else rows
                    }

                    val typedSubjects = assignments.map { asgRow ->
                        val assignmentId = asgRow[TeacherSubjectAssignmentsTable.id].value
                        val subjectName = asgRow[TeacherSubjectAssignmentsTable.subject]
                        val classId = asgRow[TeacherSubjectAssignmentsTable.classId]
                        val subjectId = asgRow[TeacherSubjectAssignmentsTable.subjectId]

                        val curUnits = if (classId != null && subjectId != null) {
                            CurriculumUnitsTable.selectAll().where {
                                (CurriculumUnitsTable.classId eq classId) and
                                    (CurriculumUnitsTable.subjectId eq subjectId) and
                                    (CurriculumUnitsTable.isActive eq true) and
                                    (CurriculumUnitsTable.approvalStatus eq "APPROVED")
                            }.orderBy(CurriculumUnitsTable.position, SortOrder.ASC).toList()
                        } else emptyList()

                        val progressRows = if (curUnits.isNotEmpty()) {
                            SyllabusProgressTable.selectAll().where {
                                (SyllabusProgressTable.assignmentId eq assignmentId) and
                                    (SyllabusProgressTable.isCovered eq true)
                            }.toList()
                        } else emptyList()

                        val coveredUnitIds = progressRows.map { it[SyllabusProgressTable.unitId] }.toSet()
                        val coveredCount = curUnits.count { it[CurriculumUnitsTable.id].value in coveredUnitIds }
                        val progressPct = if (curUnits.isNotEmpty()) (coveredCount * 100) / curUnits.size else 0

                        ParentSyllabusSubjectDto(
                            subject = subjectName,
                            progress = progressPct,
                            units = curUnits.map { uRow ->
                                val unitId = uRow[CurriculumUnitsTable.id].value
                                val progRow = progressRows.find { it[SyllabusProgressTable.unitId] == unitId }
                                ParentSyllabusUnitDto(
                                    title = uRow[CurriculumUnitsTable.title],
                                    isCovered = progRow?.get(SyllabusProgressTable.isCovered) ?: false,
                                    coveredOn = progRow?.get(SyllabusProgressTable.coveredOn)?.toString(),
                                )
                            },
                        )
                    }

                    if (typedSubjects.isNotEmpty() && typedSubjects.any { it.units.isNotEmpty() }) {
                        typedSubjects
                    } else {
                        // Fallback: legacy SyllabusUnitsTable (for pre-T-403 data)
                        val units = SyllabusUnitsTable.selectAll().where {
                            var cond = (SyllabusUnitsTable.schoolId eq child.schoolId) and
                                (SyllabusUnitsTable.className eq child.grade)
                            if (child.sectionResolved) {
                                cond = cond and (SyllabusUnitsTable.section eq child.section)
                            }
                            cond
                        }.orderBy(SyllabusUnitsTable.position, SortOrder.ASC).toList()

                        if (units.isNotEmpty()) {
                            units.groupBy { it[SyllabusUnitsTable.subject] }.map { (subject, list) ->
                                val covered = list.count { it[SyllabusUnitsTable.isCovered] }
                                val progress = if (list.isNotEmpty()) (covered * 100) / list.size else 0
                                ParentSyllabusSubjectDto(
                                    subject = subject,
                                    progress = progress,
                                    units = list.map {
                                        ParentSyllabusUnitDto(
                                            title = it[SyllabusUnitsTable.title],
                                            isCovered = it[SyllabusUnitsTable.isCovered],
                                            coveredOn = it[SyllabusUnitsTable.coveredOn]?.toString(),
                                        )
                                    },
                                )
                            }
                        } else {
                            typedSubjects
                        }
                    }
                }
                call.ok(
                    ParentSyllabusData(childName = child.childName, className = child.grade, subjects = subjects),
                    message = "Syllabus loaded",
                )
            }

            // ── Timetable — the child's class weekly schedule (recurring) ────
            // RA-PP1: the parent dashboard's "today's schedule" + "weekly timetable"
            // cards read REAL rows from teacher_periods, scoped to the child's class.
            // teacher_periods is a recurring weekly pattern keyed by weekday (1=Mon…7=Sun);
            // the client paints today's column live and reveals the six-day grid on swipe.
            // Honest empty payload when the school hasn't entered a timetable — never faked.
            get("/timetable") {
                val child = call.requireOwnedChild() ?: return@get
                if (child.schoolId == null || child.grade == null) {
                    call.ok(
                        ParentTimetableData(childName = child.childName, className = child.grade ?: "", weekdays = emptyList()),
                        message = "No timetable feed yet",
                    )
                    return@get
                }
                val today = LocalDate.now()
                val todayWeekday = today.dayOfWeek.value
                val now = LocalTime.now()

                val data = dbQuery {
                    // Teacher display names for this school (id → full name).
                    val teacherNames = AppUsersTable.selectAll()
                        .where { AppUsersTable.schoolId eq child.schoolId }
                        .associate { it[AppUsersTable.id].value to it[AppUsersTable.fullName] }

                    val rows = TeacherPeriodsTable.selectAll().where {
                        var cond = (TeacherPeriodsTable.schoolId eq child.schoolId) and
                            (TeacherPeriodsTable.className eq child.grade)
                        // RA-S19: only constrain to the section when it's authoritative
                        // (came from a linked students row); else relax to class-level.
                        if (child.sectionResolved) {
                            cond = cond and (TeacherPeriodsTable.section eq child.section)
                        }
                        cond
                    }.map { r ->
                        val tId = r[TeacherPeriodsTable.teacherId]
                        Triple(
                            r[TeacherPeriodsTable.weekday],
                            r[TeacherPeriodsTable.startTime],
                            ParentPeriodDto(
                                startTime = r[TeacherPeriodsTable.startTime].format(PARENT_HHMM),
                                endTime = r[TeacherPeriodsTable.endTime].format(PARENT_HHMM),
                                subject = r[TeacherPeriodsTable.subject],
                                room = r[TeacherPeriodsTable.room],
                                teacherName = teacherNames[tId] ?: "",
                            ),
                        )
                    }

                    // C-3: bell schedule from school_day_config
                    val bellSchedule = resolveParentBellSchedule(child.schoolId, todayWeekday)

                    val weekdays = rows.groupBy { it.first }
                        .map { (weekday, list) ->
                            val sortedPeriods = list.sortedWith(compareBy({ it.second }, { it.third.endTime }))
                                .map { it.third }

                            // H-1: server-authoritative now/next for today's weekday
                            val nowIdx: Int?
                            val nextIdx: Int?
                            if (weekday == todayWeekday) {
                                nowIdx = sortedPeriods.indexOfFirst { p ->
                                    val start = LocalTime.parse(p.startTime, PARENT_HHMM)
                                    val end = LocalTime.parse(p.endTime, PARENT_HHMM)
                                    !now.isBefore(start) && now.isBefore(end)
                                }.takeIf { it >= 0 }
                                nextIdx = sortedPeriods.indexOfFirst { p ->
                                    val start = LocalTime.parse(p.startTime, PARENT_HHMM)
                                    now.isBefore(start)
                                }.takeIf { it >= 0 }
                            } else {
                                nowIdx = null
                                nextIdx = null
                            }

                            ParentTimetableDayDto(
                                weekday = weekday,
                                periods = sortedPeriods,
                                nowIndex = nowIdx,
                                nextIndex = nextIdx,
                            )
                        }
                        .sortedBy { it.weekday }

                    ParentTimetableData(
                        childName = child.childName,
                        className = child.grade,
                        weekdays = weekdays,
                        bellSchedule = bellSchedule,
                    )
                }
                call.ok(data, message = "Timetable loaded")
            }

            // ── Daily Summary — what was taught today/recently (Agentic Syllabus) ──
            get("/daily-summary") {
                val child = call.requireOwnedChild() ?: return@get
                if (child.schoolId == null || child.grade == null) {
                    call.ok(
                        ParentDailySummaryData(
                            childName = child.childName,
                            className = child.grade ?: "",
                            date = LocalDate.now().toString(),
                        ),
                        message = "No daily summary yet",
                    )
                    return@get
                }

                val dateStr = call.request.queryParameters["date"] ?: LocalDate.now().toString()
                val targetDate = runCatching { LocalDate.parse(dateStr) }.getOrNull() ?: LocalDate.now()

                val assignments = dbQuery {
                    TeacherSubjectAssignmentsTable.selectAll().where {
                        (TeacherSubjectAssignmentsTable.schoolId eq child.schoolId) and
                            (TeacherSubjectAssignmentsTable.className eq child.grade) and
                            (TeacherSubjectAssignmentsTable.isActive eq true)
                    }.let { rows ->
                        if (child.sectionResolved) {
                            rows.filter { it[TeacherSubjectAssignmentsTable.section] == child.section }
                        } else rows
                    }
                }

                val assignmentIds = assignments.map { it[TeacherSubjectAssignmentsTable.id].value }
                val subjectById = assignments.associateBy { it[TeacherSubjectAssignmentsTable.id].value }

                val logs = if (assignmentIds.isNotEmpty()) dbQuery {
                    DailyClassLogTable.selectAll().where {
                        (DailyClassLogTable.assignmentId inList assignmentIds) and
                            (DailyClassLogTable.date eq targetDate)
                    }.orderBy(DailyClassLogTable.date, SortOrder.DESC).map { row ->
                        val asgId = row[DailyClassLogTable.assignmentId]
                        val subject = subjectById[asgId]?.get(TeacherSubjectAssignmentsTable.subject) ?: ""
                        ParentDailyLogEntryDto(
                            date = row[DailyClassLogTable.date].toString(),
                            subject = subject,
                            summaryText = row[DailyClassLogTable.summaryText],
                            coveragePct = row[DailyClassLogTable.coveragePct],
                            isAiEstimated = row[DailyClassLogTable.isAiEstimated],
                        )
                    }
                } else emptyList()

                val aiSummary = if (logs.isNotEmpty()) {
                    val topicTitles = logs.flatMap { it.summaryText.split(", ").take(3) }
                    SyllabusAiService.generateDailySummary(
                        topicTitles = topicTitles,
                        classLevel = child.grade ?: "",
                        subject = "all",
                        schoolId = child.schoolId,
                    )
                } else null

                call.ok(
                    ParentDailySummaryData(
                        childName = child.childName,
                        className = child.grade,
                        date = targetDate.toString(),
                        entries = logs,
                        aiSummary = aiSummary,
                    ),
                    message = "Daily summary loaded",
                )
            }

            // ── Syllabus V2 — typed curriculum_units with coverage (Agentic Syllabus) ──
            get("/syllabus-v2") {
                val child = call.requireOwnedChild() ?: return@get
                if (child.schoolId == null || child.grade == null) {
                    call.ok(
                        ParentSyllabusV2Data(childName = child.childName, className = child.grade ?: ""),
                        message = "No syllabus feed yet",
                    )
                    return@get
                }

                val assignments = dbQuery {
                    TeacherSubjectAssignmentsTable.selectAll().where {
                        (TeacherSubjectAssignmentsTable.schoolId eq child.schoolId) and
                            (TeacherSubjectAssignmentsTable.className eq child.grade) and
                            (TeacherSubjectAssignmentsTable.isActive eq true)
                    }.let { rows ->
                        if (child.sectionResolved) {
                            rows.filter { it[TeacherSubjectAssignmentsTable.section] == child.section }
                        } else rows
                    }
                }

                val subjects = assignments.map { asgRow ->
                    val assignmentId = asgRow[TeacherSubjectAssignmentsTable.id].value
                    val subjectName = asgRow[TeacherSubjectAssignmentsTable.subject]
                    val classId = asgRow[TeacherSubjectAssignmentsTable.classId]
                    val subjectId = asgRow[TeacherSubjectAssignmentsTable.subjectId]

                    val units = if (classId != null && subjectId != null) dbQuery {
                        CurriculumUnitsTable.selectAll().where {
                            (CurriculumUnitsTable.classId eq classId) and
                                (CurriculumUnitsTable.subjectId eq subjectId) and
                                (CurriculumUnitsTable.isActive eq true) and
                                (CurriculumUnitsTable.approvalStatus eq "APPROVED")
                        }.orderBy(CurriculumUnitsTable.position, SortOrder.ASC).toList()
                    } else emptyList()

                    val progressRows = if (units.isNotEmpty()) dbQuery {
                        SyllabusProgressTable.selectAll().where {
                            (SyllabusProgressTable.assignmentId eq assignmentId) and
                                (SyllabusProgressTable.isCovered eq true)
                        }.toList()
                    } else emptyList()

                    val coveredUnitIds = progressRows.map { it[SyllabusProgressTable.unitId] }.toSet()
                    val coveredCount = units.count { it[CurriculumUnitsTable.id].value in coveredUnitIds }
                    val progressPct = if (units.isNotEmpty()) (coveredCount * 100) / units.size else 0

                    // AI estimation: if teacher hasn't updated progress, use pace plan expected coverage
                    var isAiEstimated = false
                    var estimatedPct = 0
                    if (coveredCount == 0 && units.isNotEmpty()) {
                        val pacePlan = dbQuery {
                            SyllabusPacePlanTable.selectAll().where {
                                SyllabusPacePlanTable.assignmentId eq assignmentId
                            }.singleOrNull()
                        }
                        if (pacePlan != null) {
                            estimatedPct = pacePlan[SyllabusPacePlanTable.expectedCoveragePct]
                            isAiEstimated = estimatedPct > 0
                        }
                    }

                    ParentSyllabusV2SubjectDto(
                        subject = subjectName,
                        assignmentId = assignmentId.toString(),
                        progress = progressPct,
                        isAiEstimated = isAiEstimated,
                        estimatedPct = estimatedPct,
                        units = units.map { uRow ->
                            val unitId = uRow[CurriculumUnitsTable.id].value
                            val progRow = progressRows.find { it[SyllabusProgressTable.unitId] == unitId }
                            val unitCovered = progRow?.get(SyllabusProgressTable.isCovered) ?: false
                            // If AI-estimated, mark top-level units (depth 0/1) as estimated-covered
                            // proportionally to the estimated percentage
                            val aiEstimatedCovered = isAiEstimated && !unitCovered &&
                                uRow[CurriculumUnitsTable.depth] <= 1 &&
                                units.indexOf(uRow) < (units.size * estimatedPct / 100)
                            ParentSyllabusV2UnitDto(
                                id = unitId.toString(),
                                title = uRow[CurriculumUnitsTable.title],
                                depth = uRow[CurriculumUnitsTable.depth],
                                isCovered = unitCovered || aiEstimatedCovered,
                                coveragePct = progRow?.get(SyllabusProgressTable.coveragePercent) ?: if (aiEstimatedCovered) estimatedPct else 0,
                                coveredOn = progRow?.get(SyllabusProgressTable.coveredOn)?.toString(),
                                isAiEstimated = aiEstimatedCovered,
                            )
                        },
                    )
                }

                call.ok(
                    ParentSyllabusV2Data(
                        childName = child.childName,
                        className = child.grade,
                        subjects = subjects,
                    ),
                    message = "Syllabus loaded",
                )
            }

            // ── Quizzes — published quizzes for the child's class ────────────
            get("/quizzes") {
                val child = call.requireOwnedChild() ?: return@get
                if (child.schoolId == null || child.grade == null) {
                    call.ok(ParentQuizListData(quizzes = emptyList()), message = "No quizzes yet")
                    return@get
                }

                val quizzes = dbQuery {
                    val assignments = TeacherSubjectAssignmentsTable.selectAll().where {
                        (TeacherSubjectAssignmentsTable.schoolId eq child.schoolId) and
                            (TeacherSubjectAssignmentsTable.className eq child.grade) and
                            (TeacherSubjectAssignmentsTable.isActive eq true)
                    }.let { rows ->
                        if (child.sectionResolved) {
                            rows.filter { it[TeacherSubjectAssignmentsTable.section] == child.section }
                        } else rows
                    }

                    val assignmentIds = assignments.map { it[TeacherSubjectAssignmentsTable.id].value }
                    val subjectMap = assignments.associate { it[TeacherSubjectAssignmentsTable.id].value to it[TeacherSubjectAssignmentsTable.subject] }

                    SyllabusQuizzesTable.selectAll().where {
                        (SyllabusQuizzesTable.schoolId eq child.schoolId) and
                            (SyllabusQuizzesTable.status eq "PUBLISHED") and
                            (SyllabusQuizzesTable.assignmentId inList assignmentIds)
                    }.orderBy(SyllabusQuizzesTable.publishedAt, SortOrder.DESC).toList()
                        .map { qRow ->
                            val asgId = qRow[SyllabusQuizzesTable.assignmentId]
                            val qId = qRow[SyllabusQuizzesTable.id].value
                            val questionCount = dbQuery {
                                SyllabusQuizQuestionsTable.selectAll().where {
                                    SyllabusQuizQuestionsTable.quizId eq qId
                                }.count()
                            }
                            val alreadySubmitted = child.studentCode?.let { sc ->
                                dbQuery {
                                    SyllabusQuizAnswersTable.selectAll().where {
                                        (SyllabusQuizAnswersTable.quizId eq qId) and
                                            (SyllabusQuizAnswersTable.studentId eq sc)
                                    }.firstOrNull()
                                } != null
                            } ?: false

                            ParentQuizDto(
                                id = qId.toString(),
                                title = qRow[SyllabusQuizzesTable.title],
                                subject = subjectMap[asgId] ?: "",
                                numQuestions = questionCount.toInt(),
                                totalMarks = questionCount.toInt(),
                                status = if (alreadySubmitted) "SUBMITTED" else "PUBLISHED",
                                publishedAt = qRow[SyllabusQuizzesTable.publishedAt]?.toString(),
                            )
                        }
                }

                call.ok(ParentQuizListData(quizzes = quizzes), message = "Quizzes loaded")
            }

            // ── Quiz detail — questions without correct answers ──────────────
            get("/quiz/{quizId}") {
                val child = call.requireOwnedChild() ?: return@get
                val quizIdStr = call.parameters["quizId"]
                if (quizIdStr.isNullOrBlank()) {
                    call.fail("Quiz ID is required", HttpStatusCode.BadRequest, "MISSING_PARAM"); return@get
                }
                val quizId = UUID.fromString(quizIdStr)

                val quizRow = dbQuery {
                    SyllabusQuizzesTable.selectAll().where {
                        (SyllabusQuizzesTable.id eq quizId) and
                            (SyllabusQuizzesTable.status eq "PUBLISHED")
                    }.singleOrNull()
                }

                if (quizRow == null) {
                    call.fail("Quiz not found or not published", HttpStatusCode.NotFound, "QUIZ_NOT_FOUND"); return@get
                }

                val subjectName = dbQuery {
                    TeacherSubjectAssignmentsTable.selectAll().where {
                        TeacherSubjectAssignmentsTable.id eq quizRow[SyllabusQuizzesTable.assignmentId]
                    }.firstOrNull()?.get(TeacherSubjectAssignmentsTable.subject) ?: ""
                }

                val questions = dbQuery {
                    SyllabusQuizQuestionsTable.selectAll().where {
                        SyllabusQuizQuestionsTable.quizId eq quizId
                    }.orderBy(SyllabusQuizQuestionsTable.position, SortOrder.ASC).toList()
                }

                val questionDtos = questions.map { qr ->
                    val rawOpts = runCatching {
                        Json.decodeFromString(
                            ListSerializer(serializer<String>()),
                            qr[SyllabusQuizQuestionsTable.optionsJson]
                        )
                    }.getOrDefault(emptyList())
                    val qType = qr[SyllabusQuizQuestionsTable.questionType]
                    val opts = if (qType == "TRUE_FALSE" && rawOpts.isEmpty()) listOf("True", "False") else rawOpts

                    val matchPairs = runCatching {
                        Json.decodeFromString(
                            ListSerializer(serializer<MatchPairSer>()),
                            qr[SyllabusQuizQuestionsTable.matchPairsJson]
                        )
                    }.getOrDefault(emptyList())

                    ParentQuizQuestionDto(
                        id = qr[SyllabusQuizQuestionsTable.id].value.toString(),
                        question = qr[SyllabusQuizQuestionsTable.questionText],
                        options = opts,
                        marks = 1,
                        questionType = qr[SyllabusQuizQuestionsTable.questionType],
                        matchPairs = matchPairs.map { ParentMatchPairDto(left = it.left, right = it.right) },
                    )
                }

                call.ok(
                    ParentQuizDetailData(
                        id = quizId.toString(),
                        title = quizRow[SyllabusQuizzesTable.title],
                        subject = subjectName,
                        questions = questionDtos,
                        totalMarks = questionDtos.size,
                    ),
                    message = "Quiz loaded",
                )
            }

            // ── Quiz submit — submit answers and get results ─────────────────
            post("/quiz/submit") {
                val child = call.requireOwnedChild() ?: return@post
                val req = runCatching {
                    call.receive<QuizSubmitRequest>()
                }.getOrNull()
                if (req == null) {
                    call.fail("Invalid request body", HttpStatusCode.BadRequest, "BAD_REQUEST"); return@post
                }

                val quizId = UUID.fromString(req.quizId)
                val quizRow = dbQuery {
                    SyllabusQuizzesTable.selectAll().where {
                        (SyllabusQuizzesTable.id eq quizId) and
                            (SyllabusQuizzesTable.status eq "PUBLISHED")
                    }.singleOrNull()
                }

                if (quizRow == null) {
                    call.fail("Quiz not found or not published", HttpStatusCode.NotFound, "QUIZ_NOT_FOUND"); return@post
                }

                val studentId = child.studentCode ?: "unknown"

                // One attempt per student
                val alreadyAttempted = dbQuery {
                    SyllabusQuizAnswersTable.selectAll().where {
                        (SyllabusQuizAnswersTable.quizId eq quizId) and
                            (SyllabusQuizAnswersTable.studentId eq studentId)
                    }.firstOrNull()
                } != null
                if (alreadyAttempted) {
                    call.fail("You have already attempted this quiz", HttpStatusCode.Conflict, "QUIZ_ALREADY_SUBMITTED"); return@post
                }

                val questions = dbQuery {
                    SyllabusQuizQuestionsTable.selectAll().where {
                        SyllabusQuizQuestionsTable.quizId eq quizId
                    }.orderBy(SyllabusQuizQuestionsTable.position, SortOrder.ASC).toList()
                }

                val now = Instant.now()
                var correctCount = 0
                val questionResults = mutableListOf<QuizQuestionResultDto>()

                req.answers.forEach { ans ->
                    val qId = UUID.fromString(ans.questionId)
                    val qRow = questions.find { it[SyllabusQuizQuestionsTable.id].value == qId }
                    if (qRow != null) {
                        val opts = runCatching {
                            Json.decodeFromString(
                                ListSerializer(serializer<String>()),
                                qRow[SyllabusQuizQuestionsTable.optionsJson]
                            )
                        }.getOrDefault(emptyList())
                        val qType = qRow[SyllabusQuizQuestionsTable.questionType]
                        val correctAnswer = qRow[SyllabusQuizQuestionsTable.correctAnswer]

                        val (selectedAnswer, isCorrect) = when (qType) {
                            "FILL_BLANK" -> {
                                val text = (ans.answerText ?: "").trim()
                                text to text.equals(correctAnswer, ignoreCase = true)
                            }
                            "TRUE_FALSE" -> {
                                val text = ans.answerText ?: opts.getOrNull(ans.selectedIndex) ?: ""
                                text to text.equals(correctAnswer, ignoreCase = true)
                            }
                            else -> {
                                val selAns = opts.getOrNull(ans.selectedIndex) ?: ""
                                selAns to (correctAnswer.equals(selAns, ignoreCase = true) ||
                                    correctAnswer.equals(ans.selectedIndex.toString(), ignoreCase = true))
                            }
                        }
                        if (isCorrect) correctCount++
                        val correctIdx = opts.indexOfFirst { it.startsWith(correctAnswer) }.takeIf { it >= 0 } ?: 0

                        questionResults.add(
                            QuizQuestionResultDto(
                                questionId = qId.toString(),
                                question = qRow[SyllabusQuizQuestionsTable.questionText],
                                selectedIndex = ans.selectedIndex,
                                correctIndex = correctIdx,
                                correct = isCorrect,
                                explanation = qRow[SyllabusQuizQuestionsTable.explanation],
                                selectedAnswer = selectedAnswer,
                                correctAnswer = correctAnswer,
                                questionType = qType,
                            )
                        )

                        dbQuery {
                            SyllabusQuizAnswersTable.insert {
                                it[SyllabusQuizAnswersTable.id] = UUID.randomUUID()
                                it[SyllabusQuizAnswersTable.quizId] = quizId
                                it[SyllabusQuizAnswersTable.studentId] = studentId
                                it[SyllabusQuizAnswersTable.questionId] = qId
                                it[SyllabusQuizAnswersTable.answerText] = selectedAnswer
                                it[SyllabusQuizAnswersTable.isCorrect] = isCorrect
                                it[SyllabusQuizAnswersTable.createdAt] = now
                            }
                        }
                    }
                }

                val totalMarks = questions.size
                val percentage = if (totalMarks > 0) (correctCount * 100) / totalMarks else 0

                // Gamification XP hook — quiz completed (GAM-020: use studentId, not childId)
                val studentUuid = child.studentId
                if (studentUuid != null && child.schoolId != null) {
                    XpHooks.onQuizCompleted(studentUuid, child.schoolId, correctCount, totalMarks)
                }

                call.ok(
                    QuizResultDto(
                        id = UUID.randomUUID().toString(),
                        quizId = req.quizId,
                        score = correctCount,
                        totalMarks = totalMarks,
                        percentage = percentage,
                        submittedAt = now.toString(),
                        questionResults = questionResults,
                    ),
                )
            }

            // ── Quiz leaderboard — per-quiz ranking ─────────────────────────
            get("/quiz/{quizId}/leaderboard") {
                val child = call.requireOwnedChild() ?: return@get
                val quizIdStr = call.parameters["quizId"]
                if (quizIdStr.isNullOrBlank()) {
                    call.fail("Quiz ID is required", HttpStatusCode.BadRequest, "MISSING_PARAM"); return@get
                }
                val quizId = UUID.fromString(quizIdStr)

                val quizRow = dbQuery {
                    SyllabusQuizzesTable.selectAll().where {
                        SyllabusQuizzesTable.id eq quizId
                    }.singleOrNull()
                }

                if (quizRow == null) {
                    call.fail("Quiz not found", HttpStatusCode.NotFound, "QUIZ_NOT_FOUND"); return@get
                }

                val subjectName = dbQuery {
                    TeacherSubjectAssignmentsTable.selectAll().where {
                        TeacherSubjectAssignmentsTable.id eq quizRow[SyllabusQuizzesTable.assignmentId]
                    }.firstOrNull()?.get(TeacherSubjectAssignmentsTable.subject) ?: ""
                }

                val totalQuestions = dbQuery {
                    SyllabusQuizQuestionsTable.selectAll().where {
                        SyllabusQuizQuestionsTable.quizId eq quizId
                    }.count().toInt()
                }

                // Aggregate per student: correct count + first submission time
                val answers = dbQuery {
                    SyllabusQuizAnswersTable.selectAll().where {
                        SyllabusQuizAnswersTable.quizId eq quizId
                    }.orderBy(SyllabusQuizAnswersTable.createdAt, SortOrder.ASC).toList()
                }

                val studentScores = mutableMapOf<String, Pair<Int, String>>()
                answers.forEach { aRow ->
                    val sid = aRow[SyllabusQuizAnswersTable.studentId]
                    val isCorrect = aRow[SyllabusQuizAnswersTable.isCorrect]
                    val submittedAt = aRow[SyllabusQuizAnswersTable.createdAt].toString()
                    val existing = studentScores[sid]
                    if (existing == null) {
                        studentScores[sid] = (if (isCorrect) 1 else 0) to submittedAt
                    } else {
                        studentScores[sid] = (existing.first + if (isCorrect) 1 else 0) to existing.second
                    }
                }

                // Resolve student names from ChildrenTable
                val studentNames = dbQuery {
                    ChildrenTable.selectAll().where {
                        ChildrenTable.studentCode inList studentScores.keys
                    }.associate { it[ChildrenTable.studentCode] to (it[ChildrenTable.childName]) }
                }

                val currentStudentId = child.studentCode

                val entries = studentScores.entries
                    .map { (sid, scoreTime) ->
                        QuizLeaderboardEntryDto(
                            rank = 0,
                            studentName = studentNames[sid] ?: "Student",
                            score = scoreTime.first,
                            totalMarks = totalQuestions,
                            percentage = if (totalQuestions > 0) (scoreTime.first * 100) / totalQuestions else 0,
                            submittedAt = scoreTime.second,
                            isCurrentStudent = sid == currentStudentId,
                        )
                    }
                    .sortedWith(compareByDescending<QuizLeaderboardEntryDto> { it.score }.thenBy { it.submittedAt })
                    .mapIndexed { idx, e -> e.copy(rank = idx + 1) }

                call.ok(QuizLeaderboardData(
                    quizId = quizId.toString(),
                    quizTitle = quizRow[SyllabusQuizzesTable.title],
                    subject = subjectName,
                    entries = entries,
                    totalParticipants = entries.size,
                ))
            }

            // ── Quiz result — view past results for a submitted quiz ─────────
            get("/quiz/{quizId}/result") {
                val child = call.requireOwnedChild() ?: return@get
                val quizIdStr = call.parameters["quizId"]
                if (quizIdStr.isNullOrBlank()) {
                    call.fail("Quiz ID is required", HttpStatusCode.BadRequest, "MISSING_PARAM"); return@get
                }
                val quizId = UUID.fromString(quizIdStr)
                val studentId = child.studentCode ?: "unknown"

                var answers = dbQuery {
                    SyllabusQuizAnswersTable.selectAll().where {
                        (SyllabusQuizAnswersTable.quizId eq quizId) and
                            (SyllabusQuizAnswersTable.studentId eq studentId)
                    }.toList()
                }

                // Fallback for legacy submissions where the old parent-level submit
                // endpoint resolved the first child instead of the selected child.
                // If this quiz was submitted under a sibling's student_code, still
                // return the result to the parent (they own both children).
                if (answers.isEmpty()) {
                    val parentId = call.principalUserId()?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    val siblingCodes = if (parentId != null) dbQuery {
                        ChildrenTable.selectAll().where {
                            (ChildrenTable.parentId eq parentId) and
                                (ChildrenTable.isActive eq true)
                        }.mapNotNull { it[ChildrenTable.studentCode] }
                    } else emptyList()
                    if (siblingCodes.isNotEmpty()) {
                        answers = dbQuery {
                            SyllabusQuizAnswersTable.selectAll().where {
                                (SyllabusQuizAnswersTable.quizId eq quizId) and
                                    (SyllabusQuizAnswersTable.studentId inList siblingCodes)
                            }.toList()
                        }
                    }
                }

                if (answers.isEmpty()) {
                    call.fail("No submission found for this quiz", HttpStatusCode.NotFound, "NO_SUBMISSION"); return@get
                }

                val questions = dbQuery {
                    SyllabusQuizQuestionsTable.selectAll().where {
                        SyllabusQuizQuestionsTable.quizId eq quizId
                    }.orderBy(SyllabusQuizQuestionsTable.position, SortOrder.ASC).toList()
                }

                val answerByQuestionId = answers.associateBy { it[SyllabusQuizAnswersTable.questionId] }

                val questionResults = mutableListOf<QuizQuestionResultDto>()
                var correctCount = 0
                questions.forEach { qRow ->
                    val qId = qRow[SyllabusQuizQuestionsTable.id].value
                    val opts = runCatching {
                        Json.decodeFromString(
                            ListSerializer(serializer<String>()),
                            qRow[SyllabusQuizQuestionsTable.optionsJson]
                        )
                    }.getOrDefault(emptyList())
                    val qType = qRow[SyllabusQuizQuestionsTable.questionType]
                    val correctAnswer = qRow[SyllabusQuizQuestionsTable.correctAnswer]
                    val ansRow = answerByQuestionId[qId]
                    val selectedAnswer = ansRow?.get(SyllabusQuizAnswersTable.answerText) ?: ""
                    val isCorrect = ansRow?.get(SyllabusQuizAnswersTable.isCorrect) ?: false
                    if (isCorrect) correctCount++
                    val correctIdx = opts.indexOfFirst { it.startsWith(correctAnswer) }.takeIf { it >= 0 } ?: 0
                    val selectedIdx = opts.indexOfFirst { it == selectedAnswer }.takeIf { it >= 0 } ?: -1

                    questionResults.add(
                        QuizQuestionResultDto(
                            questionId = qId.toString(),
                            question = qRow[SyllabusQuizQuestionsTable.questionText],
                            selectedIndex = selectedIdx,
                            correctIndex = correctIdx,
                            correct = isCorrect,
                            explanation = qRow[SyllabusQuizQuestionsTable.explanation],
                            selectedAnswer = selectedAnswer,
                            correctAnswer = correctAnswer,
                            questionType = qType,
                        )
                    )
                }

                val totalMarks = questions.size
                val percentage = if (totalMarks > 0) (correctCount * 100) / totalMarks else 0

                call.ok(QuizResultDto(
                    id = UUID.randomUUID().toString(),
                    quizId = quizId.toString(),
                    score = correctCount,
                    totalMarks = totalMarks,
                    percentage = percentage,
                    submittedAt = answers.firstOrNull()?.get(SyllabusQuizAnswersTable.createdAt)?.toString(),
                    questionResults = questionResults,
                ))
            }
        }

        // ── Quiz endpoints at parent level (matching client API URLs) ──────
        route("/api/v1/parent/quiz") {
            get("/{id}") {
                val quizIdStr = call.parameters["id"]
                if (quizIdStr.isNullOrBlank()) {
                    call.fail("Quiz ID is required", HttpStatusCode.BadRequest, "MISSING_PARAM"); return@get
                }
                val quizId = UUID.fromString(quizIdStr)

                val quizRow = dbQuery {
                    SyllabusQuizzesTable.selectAll().where {
                        (SyllabusQuizzesTable.id eq quizId) and
                            (SyllabusQuizzesTable.status eq "PUBLISHED")
                    }.singleOrNull()
                }

                if (quizRow == null) {
                    call.fail("Quiz not found or not published", HttpStatusCode.NotFound, "QUIZ_NOT_FOUND"); return@get
                }

                val subjectName = dbQuery {
                    TeacherSubjectAssignmentsTable.selectAll().where {
                        TeacherSubjectAssignmentsTable.id eq quizRow[SyllabusQuizzesTable.assignmentId]
                    }.firstOrNull()?.get(TeacherSubjectAssignmentsTable.subject) ?: ""
                }

                val questions = dbQuery {
                    SyllabusQuizQuestionsTable.selectAll().where {
                        SyllabusQuizQuestionsTable.quizId eq quizId
                    }.orderBy(SyllabusQuizQuestionsTable.position, SortOrder.ASC).toList()
                }

                val questionDtos = questions.map { qr ->
                    val rawOpts = runCatching {
                        Json.decodeFromString(
                            ListSerializer(serializer<String>()),
                            qr[SyllabusQuizQuestionsTable.optionsJson]
                        )
                    }.getOrDefault(emptyList())
                    val qType = qr[SyllabusQuizQuestionsTable.questionType]
                    val opts = if (qType == "TRUE_FALSE" && rawOpts.isEmpty()) listOf("True", "False") else rawOpts

                    val matchPairs = runCatching {
                        Json.decodeFromString(
                            ListSerializer(serializer<MatchPairSer>()),
                            qr[SyllabusQuizQuestionsTable.matchPairsJson]
                        )
                    }.getOrDefault(emptyList())

                    ParentQuizQuestionDto(
                        id = qr[SyllabusQuizQuestionsTable.id].value.toString(),
                        question = qr[SyllabusQuizQuestionsTable.questionText],
                        options = opts,
                        marks = 1,
                        questionType = qr[SyllabusQuizQuestionsTable.questionType],
                        matchPairs = matchPairs.map { ParentMatchPairDto(left = it.left, right = it.right) },
                    )
                }

                call.ok(
                    ParentQuizDetailData(
                        id = quizId.toString(),
                        title = quizRow[SyllabusQuizzesTable.title],
                        subject = subjectName,
                        questions = questionDtos,
                        totalMarks = questionDtos.size,
                    ),
                    message = "Quiz loaded",
                )
            }

            post("/submit") {
                val req = runCatching {
                    call.receive<QuizSubmitRequest>()
                }.getOrNull()
                if (req == null) {
                    call.fail("Invalid request body", HttpStatusCode.BadRequest, "BAD_REQUEST"); return@post
                }

                val quizId = UUID.fromString(req.quizId)
                val quizRow = dbQuery {
                    SyllabusQuizzesTable.selectAll().where {
                        (SyllabusQuizzesTable.id eq quizId) and
                            (SyllabusQuizzesTable.status eq "PUBLISHED")
                    }.singleOrNull()
                }

                if (quizRow == null) {
                    call.fail("Quiz not found or not published", HttpStatusCode.NotFound, "QUIZ_NOT_FOUND"); return@post
                }

                val questions = dbQuery {
                    SyllabusQuizQuestionsTable.selectAll().where {
                        SyllabusQuizQuestionsTable.quizId eq quizId
                    }.orderBy(SyllabusQuizQuestionsTable.position, SortOrder.ASC).toList()
                }

                val now = Instant.now()
                var correctCount = 0
                val questionResults = mutableListOf<QuizQuestionResultDto>()

                // Resolve student ID from parent's token + child_id in request body
                val uid = call.principalUserId()?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                val childIdUuid = req.childId?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                if (childIdUuid == null) {
                    call.fail("child_id is required", HttpStatusCode.BadRequest, "MISSING_CHILD_ID"); return@post
                }
                val childRow = if (uid != null) {
                    dbQuery {
                        ChildrenTable.selectAll().where {
                            (ChildrenTable.id eq childIdUuid) and
                                (ChildrenTable.parentId eq uid) and
                                (ChildrenTable.isActive eq true)
                        }.singleOrNull()
                    }
                } else null
                val studentId = childRow?.get(ChildrenTable.studentCode) ?: "unknown"

                // One attempt per student
                val alreadyAttempted = dbQuery {
                    SyllabusQuizAnswersTable.selectAll().where {
                        (SyllabusQuizAnswersTable.quizId eq quizId) and
                            (SyllabusQuizAnswersTable.studentId eq studentId)
                    }.firstOrNull()
                } != null
                if (alreadyAttempted) {
                    call.fail("You have already attempted this quiz", HttpStatusCode.Conflict, "QUIZ_ALREADY_SUBMITTED"); return@post
                }

                req.answers.forEach { ans ->
                    val qId = UUID.fromString(ans.questionId)
                    val qRow = questions.find { it[SyllabusQuizQuestionsTable.id].value == qId }
                    if (qRow != null) {
                        val opts = runCatching {
                            Json.decodeFromString(
                                ListSerializer(serializer<String>()),
                                qRow[SyllabusQuizQuestionsTable.optionsJson]
                            )
                        }.getOrDefault(emptyList())
                        val qType = qRow[SyllabusQuizQuestionsTable.questionType]
                        val correctAnswer = qRow[SyllabusQuizQuestionsTable.correctAnswer]

                        val (selectedAnswer, isCorrect) = when (qType) {
                            "FILL_BLANK" -> {
                                val text = (ans.answerText ?: "").trim()
                                text to text.equals(correctAnswer, ignoreCase = true)
                            }
                            "TRUE_FALSE" -> {
                                val text = ans.answerText ?: opts.getOrNull(ans.selectedIndex) ?: ""
                                text to text.equals(correctAnswer, ignoreCase = true)
                            }
                            else -> {
                                val selAns = opts.getOrNull(ans.selectedIndex) ?: ""
                                selAns to (correctAnswer.equals(selAns, ignoreCase = true) ||
                                    correctAnswer.equals(ans.selectedIndex.toString(), ignoreCase = true))
                            }
                        }
                        if (isCorrect) correctCount++
                        val correctIdx = opts.indexOfFirst { it.startsWith(correctAnswer) }.takeIf { it >= 0 } ?: 0

                        questionResults.add(
                            QuizQuestionResultDto(
                                questionId = qId.toString(),
                                question = qRow[SyllabusQuizQuestionsTable.questionText],
                                selectedIndex = ans.selectedIndex,
                                correctIndex = correctIdx,
                                correct = isCorrect,
                                explanation = qRow[SyllabusQuizQuestionsTable.explanation],
                                selectedAnswer = selectedAnswer,
                                correctAnswer = correctAnswer,
                                questionType = qType,
                            )
                        )

                        dbQuery {
                            SyllabusQuizAnswersTable.insert {
                                it[SyllabusQuizAnswersTable.id] = UUID.randomUUID()
                                it[SyllabusQuizAnswersTable.quizId] = quizId
                                it[SyllabusQuizAnswersTable.studentId] = studentId
                                it[SyllabusQuizAnswersTable.questionId] = qId
                                it[SyllabusQuizAnswersTable.answerText] = selectedAnswer
                                it[SyllabusQuizAnswersTable.isCorrect] = isCorrect
                                it[SyllabusQuizAnswersTable.createdAt] = now
                            }
                        }
                    }
                }

                val totalMarks = questions.size
                val percentage = if (totalMarks > 0) (correctCount * 100) / totalMarks else 0

                call.ok(
                    QuizResultDto(
                        id = UUID.randomUUID().toString(),
                        quizId = req.quizId,
                        score = correctCount,
                        totalMarks = totalMarks,
                        percentage = percentage,
                        submittedAt = now.toString(),
                        questionResults = questionResults,
                    ),
                )
            }
        }
    }
}
