/*
 * File: DailySummaryAutoJob.kt
 * Module: feature.ai
 *
 * Scheduled job that runs at end of each school day (configurable, default 14:00 UTC ≈ 7:30 PM IST).
 * For every active teacher_subject_assignment that has NO daily class log for today,
 * generates an AI-estimated summary based on schedule + syllabus progress data.
 *
 * The generated log is marked source='AI' and is_ai_estimated=true so parents
 * can distinguish teacher-written summaries from AI-estimated ones.
 *
 * Follows the NotificationScheduler/PulseWeeklyJob pattern: hourly check, run-guard
 * prevents duplicate runs, resilient to server restarts.
 */
package com.littlebridge.enrollplus.feature.ai

import com.littlebridge.enrollplus.db.CurriculumUnitsTable
import com.littlebridge.enrollplus.db.DailyClassLogTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.SchoolClassesTable
import com.littlebridge.enrollplus.db.SchoolSubjectsTable
import com.littlebridge.enrollplus.db.SyllabusProgressTable
import com.littlebridge.enrollplus.db.TeacherSubjectAssignmentsTable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.concurrent.atomic.AtomicReference

object DailySummaryAutoJob {
    private const val TAG = "DailySummaryAutoJob"
    private val log = LoggerFactory.getLogger(TAG)

    private const val TARGET_HOUR_UTC = 14 // 7:30 PM IST ≈ 14:00 UTC
    private const val CHECK_INTERVAL_MS = 60 * 60 * 1000L // 1 hour

    private val lastRunDate = AtomicReference<LocalDate?>(null)

    fun start(scope: CoroutineScope) {
        scope.launch {
            log.info("[$TAG] Started — auto daily summary at hour {} UTC (hourly check)", TARGET_HOUR_UTC)
            while (true) {
                delay(CHECK_INTERVAL_MS)
                runCatching { checkAndRun() }
                    .onFailure { log.warn("[$TAG] checkAndRun failed: {}", it.message) }
            }
        }
    }

    private suspend fun checkAndRun() {
        val now = ZonedDateTime.now(ZoneOffset.UTC)
        val today = now.toLocalDate()
        if (now.hour != TARGET_HOUR_UTC) return
        if (lastRunDate.get() == today) return
        if (!lastRunDate.compareAndSet(null, today)) return
        runForDate(today)
    }

    /**
     * Manually trigger for a specific date (admin/dev tool).
     */
    suspend fun runForDate(date: LocalDate): Int {
        log.info("[$TAG] Running auto daily summary for {}", date)
        val assignments = dbQuery {
            TeacherSubjectAssignmentsTable.selectAll().where {
                TeacherSubjectAssignmentsTable.isActive eq true
            }.toList()
        }

        var generated = 0
        for (asg in assignments) {
            val assignmentId = asg[TeacherSubjectAssignmentsTable.id].value
            val schoolId = asg[TeacherSubjectAssignmentsTable.schoolId]
            val classId = asg[TeacherSubjectAssignmentsTable.classId]
            val subjectId = asg[TeacherSubjectAssignmentsTable.subjectId]
            val section = asg[TeacherSubjectAssignmentsTable.section]

            // Check if a log already exists for this assignment+date
            val existing = dbQuery {
                DailyClassLogTable.selectAll().where {
                    (DailyClassLogTable.assignmentId eq assignmentId) and
                        (DailyClassLogTable.date eq date)
                }.count()
            }
            if (existing > 0L) continue // Teacher already logged

            // Get class + subject names
            val className = classId?.let { cid ->
                dbQuery {
                    SchoolClassesTable.selectAll().where { SchoolClassesTable.id eq cid }
                        .firstOrNull()?.get(SchoolClassesTable.name)
                }
            } ?: asg[TeacherSubjectAssignmentsTable.className]

            val subjectName = subjectId?.let { sid ->
                dbQuery {
                    SchoolSubjectsTable.selectAll().where { SchoolSubjectsTable.id eq sid }
                        .firstOrNull()?.get(SchoolSubjectsTable.subName)
                }
            } ?: asg[TeacherSubjectAssignmentsTable.subject]

            // Get recently covered topics (last 7 days) for context
            val recentTopics = if (classId != null && subjectId != null) dbQuery {
                val units = CurriculumUnitsTable.selectAll().where {
                    (CurriculumUnitsTable.classId eq classId) and
                        (CurriculumUnitsTable.subjectId eq subjectId) and
                        (CurriculumUnitsTable.isActive eq true) and
                        (CurriculumUnitsTable.approvalStatus eq "APPROVED")
                }.associate { it[CurriculumUnitsTable.id].value to it[CurriculumUnitsTable.title] }

                val progress = SyllabusProgressTable.selectAll().where {
                    (SyllabusProgressTable.assignmentId eq assignmentId) and
                        (SyllabusProgressTable.isCovered eq true) and
                        (SyllabusProgressTable.coveredOn.isNotNull())
                }.toList()

                progress
                    .filter { it[SyllabusProgressTable.coveredOn]?.let { d ->
                        !d.isBefore(date.minusDays(7)) && !d.isAfter(date)
                    } ?: false }
                    .mapNotNull { units[it[SyllabusProgressTable.unitId]] }
                    .distinct()
                    .take(5)
            } else emptyList()

            // Generate AI summary
            val summary = if (recentTopics.isNotEmpty()) {
                runCatching {
                    SyllabusAiService.generateDailySummary(
                        topicTitles = recentTopics,
                        classLevel = className ?: "",
                        subject = subjectName ?: "",
                        schoolId = schoolId,
                    )
                }.getOrNull() ?: "Class covered: ${recentTopics.joinToString(", ")}"
            } else {
                "No specific topics were logged today. The class may have been engaged in revision or other activities."
            }

            // Calculate coverage percentage
            val coveragePct = if (classId != null && subjectId != null) dbQuery {
                val totalUnits = CurriculumUnitsTable.selectAll().where {
                    (CurriculumUnitsTable.classId eq classId) and
                        (CurriculumUnitsTable.subjectId eq subjectId) and
                        (CurriculumUnitsTable.isActive eq true) and
                        (CurriculumUnitsTable.approvalStatus eq "APPROVED") and
                        (CurriculumUnitsTable.depth greaterEq 1)
                }.count()

                if (totalUnits == 0L) 0
                else {
                    val coveredUnits = SyllabusProgressTable.selectAll().where {
                        (SyllabusProgressTable.assignmentId eq assignmentId) and
                            (SyllabusProgressTable.isCovered eq true)
                    }.count()
                    ((coveredUnits * 100) / totalUnits).toInt()
                }
            } else 0

            // Insert the AI-estimated daily log
            val now = Instant.now()
            dbQuery {
                DailyClassLogTable.insert {
                    it[DailyClassLogTable.schoolId] = schoolId
                    it[DailyClassLogTable.assignmentId] = assignmentId
                    it[DailyClassLogTable.date] = date
                    it[DailyClassLogTable.summaryText] = summary
                    it[DailyClassLogTable.coveragePct] = coveragePct
                    it[DailyClassLogTable.logSource] = "AI"
                    it[DailyClassLogTable.isAiEstimated] = true
                    it[DailyClassLogTable.topicIds] = "[]"
                    it[DailyClassLogTable.createdAt] = now
                    it[DailyClassLogTable.updatedAt] = now
                }
            }
            generated++

            // Recalculate pace after auto-summary
            runCatching {
                SyllabusPaceService.recalcForAssignment(assignmentId, schoolId)
            }
        }

        log.info("[$TAG] Generated {} auto daily summaries for {}", generated, date)
        return generated
    }
}
