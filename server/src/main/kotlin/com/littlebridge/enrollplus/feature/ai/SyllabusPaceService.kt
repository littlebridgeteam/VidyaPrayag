/*
 * File: SyllabusPaceService.kt
 * Module: feature.ai
 *
 * Pace monitoring engine for the Agentic Syllabus Management System.
 * Computes expected vs actual coverage for each teacher_subject_assignment,
 * creates pace deviation alerts (with AI reconfirmation), and resolves them.
 *
 * Uses SyllabusAiService.reconfirmAlert() for the AI second-pass validation
 * before an alert is surfaced to teachers/admins.
 */
package com.littlebridge.enrollplus.feature.ai

import com.littlebridge.enrollplus.db.AcademicYearsTable
import com.littlebridge.enrollplus.db.CalendarEventsTable
import com.littlebridge.enrollplus.db.CurriculumUnitsTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.DailyClassLogTable
import com.littlebridge.enrollplus.db.SyllabusPaceAlertsTable
import com.littlebridge.enrollplus.db.SyllabusPacePlanTable
import com.littlebridge.enrollplus.db.SyllabusProgressTable
import com.littlebridge.enrollplus.db.TeacherPeriodsTable
import com.littlebridge.enrollplus.db.TeacherSubjectAssignmentsTable
import com.littlebridge.enrollplus.feature.notifications.Notify
import com.littlebridge.enrollplus.feature.notifications.NotifyRecipients
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

object SyllabusPaceService {
    private val log = LoggerFactory.getLogger("SyllabusPaceService")
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    @Serializable
    data class PaceSnapshot(
        @SerialName("assignment_id") val assignmentId: String,
        val subject: String,
        @SerialName("class_name") val className: String,
        val section: String,
        @SerialName("total_topics") val totalTopics: Int,
        @SerialName("covered_topics") val coveredTopics: Int,
        @SerialName("actual_pct") val actualPct: Int,
        @SerialName("expected_pct") val expectedPct: Int,
        @SerialName("deviation_pct") val deviationPct: Int,
        val level: String,  // ON_TRACK | BEHIND | CRITICAL | AHEAD
        @SerialName("needs_recalc") val needsRecalc: Boolean,
        @SerialName("weekly_periods") val weeklyPeriods: Int = 0,
        @SerialName("classes_elapsed") val classesElapsed: Int = 0,
        @SerialName("classes_remaining") val classesRemaining: Int = 0,
        @SerialName("estimated_completion_date") val estimatedCompletionDate: String = "",
        @SerialName("topics_per_class") val topicsPerClass: Double = 0.0,
        @SerialName("holiday_days_counted") val holidayDaysCounted: Int = 0,
        @SerialName("avg_coverage_per_class") val avgCoveragePerClass: Double = 0.0,
    )

    @Serializable
    data class AlertDto(
        val id: String,
        @SerialName("assignment_id") val assignmentId: String,
        @SerialName("alert_level") val alertLevel: String,
        @SerialName("expected_pct") val expectedPct: Int,
        @SerialName("actual_pct") val actualPct: Int,
        @SerialName("ai_confirmed") val aiConfirmed: Boolean,
        @SerialName("ai_reasoning") val aiReasoning: String,
        @SerialName("is_active") val isActive: Boolean,
        @SerialName("created_at") val createdAt: String,
    )

    @Serializable
    data class PaceSnapshotsDto(
        val snapshots: List<PaceSnapshot> = emptyList(),
    )

    @Serializable
    data class AlertsDto(
        val alerts: List<AlertDto> = emptyList(),
    )

    /**
     * Recalculate the pace plan for a single assignment.
     * Computes:
     *   - total_topics: count of active curriculum units (topics only, depth >= 1)
     *   - total_classes_expected: weekly periods * academic year weeks
     *   - classes_elapsed: periods from academic year start to today
     *   - expected_coverage_pct: linear projection of classes_elapsed / total_classes_expected
     *   - actual_coverage_pct: covered topics / total topics * 100
     * Stores the result in syllabus_pace_plan (upsert).
     */
    suspend fun recalcForAssignment(assignmentId: UUID, schoolId: UUID): PaceSnapshot? {
        val asgRow = dbQuery {
            TeacherSubjectAssignmentsTable.selectAll().where {
                (TeacherSubjectAssignmentsTable.id eq assignmentId) and
                    (TeacherSubjectAssignmentsTable.schoolId eq schoolId) and
                    (TeacherSubjectAssignmentsTable.isActive eq true)
            }.singleOrNull()
        } ?: return null

        val classId = asgRow[TeacherSubjectAssignmentsTable.classId] ?: return null
        val subjectId = asgRow[TeacherSubjectAssignmentsTable.subjectId] ?: return null
        val className = asgRow[TeacherSubjectAssignmentsTable.className]
        val section = asgRow[TeacherSubjectAssignmentsTable.section]
        val subject = asgRow[TeacherSubjectAssignmentsTable.subject]

        val totalTopics = dbQuery {
            CurriculumUnitsTable.selectAll().where {
                (CurriculumUnitsTable.classId eq classId) and
                    (CurriculumUnitsTable.subjectId eq subjectId) and
                    (CurriculumUnitsTable.isActive eq true) and
                    (CurriculumUnitsTable.depth greaterEq 1)
            }.count()
        }.toInt()

        // Plan §5.3: actual_coverage_pct = avg coverage_percent across all units
        val progressRows = dbQuery {
            SyllabusProgressTable.selectAll().where {
                (SyllabusProgressTable.assignmentId eq assignmentId)
            }.toList()
        }
        val coveredTopics = progressRows.count { it[SyllabusProgressTable.isCovered] }
        val actualPct = if (totalTopics > 0) {
            // Use avg coverage_percent for units that have progress rows;
            // units without progress rows contribute 0
            val sumPct = progressRows.sumOf { it[SyllabusProgressTable.coveragePercent] }
            sumPct / totalTopics
        } else 0

        val academicYear = dbQuery {
            AcademicYearsTable.selectAll().where {
                (AcademicYearsTable.schoolId eq schoolId) and
                    (AcademicYearsTable.isActive eq true)
            }.singleOrNull()
        }

        val yearStart: LocalDate = academicYear?.let {
            runCatching { LocalDate.parse(it[AcademicYearsTable.startDate]) }.getOrNull()
        } ?: LocalDate.now().withMonth(4).withDayOfMonth(1)

        val yearEnd: LocalDate = academicYear?.let {
            runCatching { LocalDate.parse(it[AcademicYearsTable.endDate]) }.getOrNull()
        } ?: yearStart.plusYears(1).minusDays(1)

        val academicDays = academicYear?.get(AcademicYearsTable.academicDays) ?: 220
        val holidayDays = academicYear?.get(AcademicYearsTable.holidayDays) ?: 0
        val totalAcademicDays = (academicDays - holidayDays).coerceAtLeast(1)

        // Count actual holidays from CalendarEventsTable (HOLIDAY + PUBLISHED, overlapping year range)
        val actualHolidayDates = dbQuery {
            CalendarEventsTable.selectAll().where {
                (CalendarEventsTable.schoolId eq schoolId) and
                    (CalendarEventsTable.type eq "HOLIDAY") and
                    (CalendarEventsTable.status eq "PUBLISHED") and
                    (CalendarEventsTable.isActive eq true)
            }.filter { row ->
                val sDate = row[CalendarEventsTable.startDate]
                val eDate = row[CalendarEventsTable.endDate]
                sDate != null && eDate != null
            }.flatMap { row ->
                val sDate = row[CalendarEventsTable.startDate]
                val eDate = row[CalendarEventsTable.endDate]
                val dates = mutableListOf<LocalDate>()
                var d = sDate
                while (d != null && !d.isAfter(eDate)) {
                    if (!d.isBefore(yearStart) && !d.isAfter(yearEnd)) dates.add(d)
                    d = d.plusDays(1)
                }
                dates
            }.toSet()
        }
        val actualHolidayCount = actualHolidayDates.size

        // Get weekly scheduled periods for this assignment
        val weeklyPeriods = dbQuery {
            TeacherPeriodsTable.selectAll().where {
                (TeacherPeriodsTable.assignmentId eq assignmentId) and
                    (TeacherPeriodsTable.isActive eq true)
            }.count()
        }.toInt()

        // Collect the weekdays this assignment has periods on
        val scheduledWeekdays = dbQuery {
            TeacherPeriodsTable.selectAll().where {
                (TeacherPeriodsTable.assignmentId eq assignmentId) and
                    (TeacherPeriodsTable.isActive eq true)
            }.map { it[TeacherPeriodsTable.weekday] }.toSet()
        }

        // Count actual elapsed class sessions: weekdays from yearStart to today
        // that match scheduled weekdays, minus holidays
        val today = LocalDate.now()
        val classesElapsed = if (scheduledWeekdays.isNotEmpty()) {
            var count = 0
            var d = yearStart
            while (!d.isAfter(today)) {
                if (d.dayOfWeek.value in scheduledWeekdays && d !in actualHolidayDates) {
                    count++
                }
                d = d.plusDays(1)
            }
            count
        } else 0

        // Count remaining class sessions: weekdays from today+1 to yearEnd
        // that match scheduled weekdays, minus holidays
        val classesRemaining = if (scheduledWeekdays.isNotEmpty()) {
            var count = 0
            var d = today.plusDays(1)
            while (!d.isAfter(yearEnd)) {
                if (d.dayOfWeek.value in scheduledWeekdays && d !in actualHolidayDates) {
                    count++
                }
                d = d.plusDays(1)
            }
            count
        } else 0

        val totalClassesExpected = classesElapsed + classesRemaining

        val expectedPct = if (totalClassesExpected > 0) {
            (classesElapsed * 100 / totalClassesExpected).coerceIn(0, 100)
        } else 0

        val deviationPct = actualPct - expectedPct
        val level = when {
            deviationPct >= 15 -> "AHEAD"
            deviationPct <= -30 -> "CRITICAL"
            deviationPct <= -15 -> "BEHIND"
            else -> "ON_TRACK"
        }

        val now = Instant.now()
        val topicsPerClass = if (classesElapsed > 0) totalTopics.toDouble() / classesElapsed else 0.0
        val avgCoveragePerClass = if (classesElapsed > 0) actualPct.toDouble() / classesElapsed else 0.0
        // Estimate completion date: if classesRemaining > 0, project from today
        // by counting forward the scheduled weekdays (minus holidays) needed
        val estimatedCompletionDate = if (classesRemaining > 0 && scheduledWeekdays.isNotEmpty() && actualPct < 100) {
            val remainingPct = 100 - actualPct
            val classesNeeded = if (avgCoveragePerClass > 0) (remainingPct / avgCoveragePerClass).toInt() else classesRemaining
            var counted = 0
            var d = today.plusDays(1)
            while (counted < classesNeeded && !d.isAfter(yearEnd.plusMonths(2))) {
                if (d.dayOfWeek.value in scheduledWeekdays && d !in actualHolidayDates) {
                    counted++
                }
                if (counted < classesNeeded) d = d.plusDays(1)
            }
            d.toString()
        } else if (actualPct >= 100) {
            today.toString()
        } else ""

        val aiEstimateJson = json.encodeToString(
            SyllabusAiService.PacePlanEstimate.serializer(),
            SyllabusAiService.PacePlanEstimate(
                perClassPct = if (classesElapsed > 0) actualPct.toDouble() / classesElapsed else 0.0,
                estimatedCompletionWeek = if (weeklyPeriods > 0) totalTopics / weeklyPeriods else 0,
                reasoning = "Computed from $totalTopics topics, $weeklyPeriods weekly periods, $classesElapsed classes elapsed, $actualHolidayCount holiday days, $classesRemaining classes remaining.",
            )
        )

        dbQuery {
            val existing = SyllabusPacePlanTable.selectAll().where {
                SyllabusPacePlanTable.assignmentId eq assignmentId
            }.singleOrNull()

            if (existing != null) {
                SyllabusPacePlanTable.update({
                    SyllabusPacePlanTable.id eq existing[SyllabusPacePlanTable.id]
                }) {
                    it[SyllabusPacePlanTable.totalTopics] = totalTopics
                    it[SyllabusPacePlanTable.totalClassesExpected] = totalClassesExpected
                    it[SyllabusPacePlanTable.classesElapsed] = classesElapsed
                    it[SyllabusPacePlanTable.expectedCoveragePct] = expectedPct
                    it[SyllabusPacePlanTable.actualCoveragePct] = actualPct
                    it[SyllabusPacePlanTable.aiEstimateJson] = aiEstimateJson
                    it[SyllabusPacePlanTable.needsRecalc] = false
                    it[SyllabusPacePlanTable.lastRecalcAt] = now
                    it[SyllabusPacePlanTable.updatedAt] = now
                }
            } else {
                SyllabusPacePlanTable.insert {
                    it[id] = UUID.randomUUID()
                    it[SyllabusPacePlanTable.schoolId] = schoolId
                    it[SyllabusPacePlanTable.assignmentId] = assignmentId
                    it[SyllabusPacePlanTable.academicYearId] = academicYear?.get(AcademicYearsTable.id)?.value
                    it[SyllabusPacePlanTable.totalTopics] = totalTopics
                    it[SyllabusPacePlanTable.totalClassesExpected] = totalClassesExpected
                    it[SyllabusPacePlanTable.classesElapsed] = classesElapsed
                    it[SyllabusPacePlanTable.expectedCoveragePct] = expectedPct
                    it[SyllabusPacePlanTable.actualCoveragePct] = actualPct
                    it[SyllabusPacePlanTable.aiEstimateJson] = aiEstimateJson
                    it[SyllabusPacePlanTable.needsRecalc] = false
                    it[SyllabusPacePlanTable.lastRecalcAt] = now
                    it[SyllabusPacePlanTable.createdAt] = now
                    it[SyllabusPacePlanTable.updatedAt] = now
                }
            }
        }

        if (level == "BEHIND" || level == "CRITICAL" || level == "AHEAD") {
            checkAndCreateAlert(assignmentId, schoolId, level, expectedPct, actualPct, subject, className)
        } else {
            // Pace recovered — auto-resolve any existing active alert
            resolveActiveAlertIfAny(assignmentId, schoolId)
        }

        return PaceSnapshot(
            assignmentId = assignmentId.toString(),
            subject = subject,
            className = className,
            section = section,
            totalTopics = totalTopics,
            coveredTopics = coveredTopics,
            actualPct = actualPct,
            expectedPct = expectedPct,
            deviationPct = deviationPct,
            level = level,
            needsRecalc = false,
            weeklyPeriods = weeklyPeriods,
            classesElapsed = classesElapsed,
            classesRemaining = classesRemaining,
            estimatedCompletionDate = estimatedCompletionDate,
            topicsPerClass = topicsPerClass,
            holidayDaysCounted = actualHolidayCount,
            avgCoveragePerClass = avgCoveragePerClass,
        )
    }

    /**
     * Check for an existing active alert for this assignment. If none, run AI
     * reconfirmation. If AI confirms (or is unavailable → err on side of caution),
     * create the alert and notify teachers + admins.
     */
    private suspend fun checkAndCreateAlert(
        assignmentId: UUID,
        schoolId: UUID,
        level: String,
        expectedPct: Int,
        actualPct: Int,
        subject: String,
        className: String,
    ) {
        val existingActive = dbQuery {
            SyllabusPaceAlertsTable.selectAll().where {
                (SyllabusPaceAlertsTable.assignmentId eq assignmentId) and
                    (SyllabusPaceAlertsTable.resolvedAt.isNull())
            }.singleOrNull()
        }
        if (existingActive != null) return

        val recentLogs = dbQuery {
            DailyClassLogTable.selectAll().where {
                DailyClassLogTable.assignmentId eq assignmentId
            }.orderBy(DailyClassLogTable.date, org.jetbrains.exposed.sql.SortOrder.DESC)
                .limit(5)
                .map { "${it[DailyClassLogTable.date]}: ${it[DailyClassLogTable.coveragePct]}%" }
        }

        val reconfirm = SyllabusAiService.reconfirmAlert(
            alertLevel = level,
            expectedPct = expectedPct,
            actualPct = actualPct,
            subject = subject,
            className = className,
            recentLogs = recentLogs,
            schoolId = schoolId,
        )

        val aiConfirmed = reconfirm?.confirmed ?: true
        val aiReasoning = reconfirm?.reasoning ?: "AI unavailable — defaulting to confirmed"

        if (!aiConfirmed) {
            log.info("Pace alert for $assignmentId suppressed by AI: $aiReasoning")
            return
        }

        val now = Instant.now()
        val reconfirmJson = json.encodeToString(
            SyllabusAiService.AlertReconfirmation.serializer(),
            SyllabusAiService.AlertReconfirmation(confirmed = aiConfirmed, reasoning = aiReasoning),
        )

        dbQuery {
            SyllabusPaceAlertsTable.insert {
                it[id] = UUID.randomUUID()
                it[SyllabusPaceAlertsTable.schoolId] = schoolId
                it[SyllabusPaceAlertsTable.assignmentId] = assignmentId
                it[SyllabusPaceAlertsTable.alertLevel] = level
                it[SyllabusPaceAlertsTable.expectedPct] = expectedPct
                it[SyllabusPaceAlertsTable.actualPct] = actualPct
                it[SyllabusPaceAlertsTable.aiConfirmed] = aiConfirmed
                it[SyllabusPaceAlertsTable.aiReconfirmJson] = reconfirmJson
                it[SyllabusPaceAlertsTable.notifiedRoles] = "[\"teacher\",\"admin\"]"
                it[SyllabusPaceAlertsTable.createdAt] = now
                it[SyllabusPaceAlertsTable.resolvedAt] = null
            }
        }

        // Notify only the teacher who owns this assignment + school admins
        val teacherId = dbQuery {
            TeacherSubjectAssignmentsTable.selectAll().where {
                TeacherSubjectAssignmentsTable.id eq assignmentId
            }.singleOrNull()?.get(TeacherSubjectAssignmentsTable.teacherId)
        }
        val adminIds = NotifyRecipients.adminsInSchool(schoolId)
        val recipients = (listOfNotNull(teacherId) + adminIds).distinct()

        Notify.toUsers(
            userIds = recipients,
            category = "syllabus_pace",
            title = "Syllabus pace alert: $subject ($className)",
            body = "Coverage is $actualPct% vs expected $expectedPct%. Level: $level.",
            schoolId = schoolId,
            deepLink = "/admin/pace-alerts",
            refType = "pace_alert",
            refId = assignmentId.toString(),
        )
    }

    /**
     * Auto-resolve any active alert for this assignment when pace recovers.
     * Notifies the teacher that their syllabus is back on track.
     */
    private suspend fun resolveActiveAlertIfAny(assignmentId: UUID, schoolId: UUID) {
        val now = Instant.now()
        val resolved = dbQuery {
            SyllabusPaceAlertsTable.update({
                (SyllabusPaceAlertsTable.assignmentId eq assignmentId) and
                    (SyllabusPaceAlertsTable.schoolId eq schoolId) and
                    (SyllabusPaceAlertsTable.resolvedAt.isNull())
            }) {
                it[SyllabusPaceAlertsTable.resolvedAt] = now
            }
        }
        if (resolved > 0) {
            val teacherId = dbQuery {
                TeacherSubjectAssignmentsTable.selectAll().where {
                    TeacherSubjectAssignmentsTable.id eq assignmentId
                }.singleOrNull()?.get(TeacherSubjectAssignmentsTable.teacherId)
            }
            val adminIds = NotifyRecipients.adminsInSchool(schoolId)
            val recipients = (listOfNotNull(teacherId) + adminIds).distinct()
            Notify.toUsers(
                userIds = recipients,
                category = "syllabus_pace",
                title = "Syllabus pace back on track",
                body = "Coverage has recovered to expected levels.",
                schoolId = schoolId,
                deepLink = "/admin/pace-alerts",
                refType = "pace_alert",
                refId = assignmentId.toString(),
            )
        }
    }

    /**
     * Resolve an active alert (mark resolved_at = now).
     */
    suspend fun resolveAlert(alertId: UUID, schoolId: UUID): Boolean {
        val now = Instant.now()
        val updated = dbQuery {
            SyllabusPaceAlertsTable.update({
                (SyllabusPaceAlertsTable.id eq alertId) and
                    (SyllabusPaceAlertsTable.schoolId eq schoolId) and
                    (SyllabusPaceAlertsTable.resolvedAt.isNull())
            }) {
                it[SyllabusPaceAlertsTable.resolvedAt] = now
            }
        }
        return updated > 0
    }

    /**
     * Recalculate pace plans for all active assignments in a school.
     * Returns the resulting snapshots.
     */
    suspend fun recalcForSchool(schoolId: UUID): List<PaceSnapshot> {
        val assignments = dbQuery {
            TeacherSubjectAssignmentsTable.selectAll().where {
                (TeacherSubjectAssignmentsTable.schoolId eq schoolId) and
                    (TeacherSubjectAssignmentsTable.isActive eq true)
            }.map { it[TeacherSubjectAssignmentsTable.id].value }
        }
        return assignments.mapNotNull { recalcForAssignment(it, schoolId) }
    }

    /**
     * Get all active alerts for a school.
     */
    suspend fun activeAlertsForSchool(schoolId: UUID): List<AlertDto> = dbQuery {
        SyllabusPaceAlertsTable.selectAll().where {
            (SyllabusPaceAlertsTable.schoolId eq schoolId) and
                (SyllabusPaceAlertsTable.resolvedAt.isNull())
        }.orderBy(SyllabusPaceAlertsTable.createdAt, org.jetbrains.exposed.sql.SortOrder.DESC).map { row ->
            AlertDto(
                id = row[SyllabusPaceAlertsTable.id].value.toString(),
                assignmentId = row[SyllabusPaceAlertsTable.assignmentId].toString(),
                alertLevel = row[SyllabusPaceAlertsTable.alertLevel],
                expectedPct = row[SyllabusPaceAlertsTable.expectedPct],
                actualPct = row[SyllabusPaceAlertsTable.actualPct],
                aiConfirmed = row[SyllabusPaceAlertsTable.aiConfirmed],
                aiReasoning = try {
                    json.parseToJsonElement(row[SyllabusPaceAlertsTable.aiReconfirmJson])
                        .let { it.jsonObject["reasoning"]?.jsonPrimitive?.content ?: "" }
                } catch (e: Exception) {
                    log.warn("Failed to parse aiReconfirmJson for syllabus pace alert ${row[SyllabusPaceAlertsTable.id]}; returning empty reasoning", e)
                    ""
                },
                isActive = row[SyllabusPaceAlertsTable.resolvedAt] == null,
                createdAt = row[SyllabusPaceAlertsTable.createdAt].toString(),
            )
        }
    }

    /**
     * Get pace snapshots for all assignments in a school.
     */
    suspend fun snapshotsForSchool(schoolId: UUID): List<PaceSnapshot> = dbQuery {
        val assignments = TeacherSubjectAssignmentsTable.selectAll().where {
            (TeacherSubjectAssignmentsTable.schoolId eq schoolId) and
                (TeacherSubjectAssignmentsTable.isActive eq true)
        }.toList()

        assignments.mapNotNull { asgRow ->
            val assignmentId = asgRow[TeacherSubjectAssignmentsTable.id].value
            val planRow = SyllabusPacePlanTable.selectAll().where {
                SyllabusPacePlanTable.assignmentId eq assignmentId
            }.singleOrNull()

            if (planRow != null) {
                val covered = dbQuery {
                    SyllabusProgressTable.selectAll().where {
                        (SyllabusProgressTable.assignmentId eq assignmentId) and
                            (SyllabusProgressTable.isCovered eq true)
                    }.count()
                }.toInt()
                PaceSnapshot(
                    assignmentId = assignmentId.toString(),
                    subject = asgRow[TeacherSubjectAssignmentsTable.subject],
                    className = asgRow[TeacherSubjectAssignmentsTable.className],
                    section = asgRow[TeacherSubjectAssignmentsTable.section],
                    totalTopics = planRow[SyllabusPacePlanTable.totalTopics],
                    coveredTopics = covered,
                    actualPct = planRow[SyllabusPacePlanTable.actualCoveragePct],
                    expectedPct = planRow[SyllabusPacePlanTable.expectedCoveragePct],
                    deviationPct = planRow[SyllabusPacePlanTable.actualCoveragePct] - planRow[SyllabusPacePlanTable.expectedCoveragePct],
                    level = when {
                        planRow[SyllabusPacePlanTable.actualCoveragePct] - planRow[SyllabusPacePlanTable.expectedCoveragePct] >= 15 -> "AHEAD"
                        planRow[SyllabusPacePlanTable.actualCoveragePct] - planRow[SyllabusPacePlanTable.expectedCoveragePct] <= -30 -> "CRITICAL"
                        planRow[SyllabusPacePlanTable.actualCoveragePct] - planRow[SyllabusPacePlanTable.expectedCoveragePct] <= -15 -> "BEHIND"
                        else -> "ON_TRACK"
                    },
                    needsRecalc = planRow[SyllabusPacePlanTable.needsRecalc],
                    weeklyPeriods = 0,
                    classesElapsed = planRow[SyllabusPacePlanTable.classesElapsed],
                    classesRemaining = planRow[SyllabusPacePlanTable.totalClassesExpected] - planRow[SyllabusPacePlanTable.classesElapsed],
                    estimatedCompletionDate = "",
                    topicsPerClass = 0.0,
                    holidayDaysCounted = 0,
                    avgCoveragePerClass = 0.0,
                )
            } else null
        }
    }
}
