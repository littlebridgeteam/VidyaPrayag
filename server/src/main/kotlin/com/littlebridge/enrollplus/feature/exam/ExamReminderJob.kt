/*
 * File: ExamReminderJob.kt
 * Module: feature.exam
 *
 * Scheduled job that runs hourly and sends exam reminders to parents
 * on the evening before an exam (targeting 6 PM IST = 12:30 UTC).
 *
 * For each assessment with examDate == tomorrow and status in (scheduled, marks_pending),
 * and no existing reminder log entry, sends a push notification to all parents
 * of the class, then stamps exam_reminder_log to prevent duplicates.
 */
package com.littlebridge.enrollplus.feature.exam

import com.littlebridge.enrollplus.db.AssessmentsTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.ExamReminderLogTable
import com.littlebridge.enrollplus.feature.notifications.Notify
import com.littlebridge.enrollplus.feature.notifications.NotifyRecipients
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
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

object ExamReminderJob {
    private const val TAG = "ExamReminderJob"
    private val logger = LoggerFactory.getLogger(ExamReminderJob::class.java)

    // IST is UTC+5:30. 6 PM IST = 12:30 UTC.
    // We check every hour and only fire when the IST hour is >= 18 (6 PM)
    // and the exam is tomorrow (in IST).
    private val IST_ZONE = ZoneId.of("Asia/Kolkata")

    fun start(scope: CoroutineScope) {
        scope.launch {
            while (true) {
                delay(60 * 60 * 1000) // 1 hour
                runCatching { checkExamReminders() }
                    .onFailure { logger.error("[$TAG] checkExamReminders failed", it) }
            }
        }
    }

    suspend fun checkExamReminders() {
        val nowIst = ZonedDateTime.now(IST_ZONE)
        val istHour = nowIst.hour

        // Only fire between 6 PM and 7 PM IST
        if (istHour < 18 || istHour >= 19) return

        val tomorrowIst = nowIst.toLocalDate().plusDays(1)

        // Find assessments scheduled for tomorrow (IST) that haven't been reminded yet
        val pendingExams = dbQuery {
            AssessmentsTable.selectAll()
                .where {
                    (AssessmentsTable.examDate eq tomorrowIst) and
                        (AssessmentsTable.isActive eq true) and
                        (AssessmentsTable.status inList listOf("scheduled", "marks_pending"))
                }.toList()
        }

        if (pendingExams.isEmpty()) return

        var sentCount = 0
        for (row in pendingExams) {
            val assessmentId = row[AssessmentsTable.id].value
            val schoolId = row[AssessmentsTable.schoolId]
            val className = row[AssessmentsTable.className]
            val subject = row[AssessmentsTable.subject]
            val examName = row[AssessmentsTable.name]
            val examDate = row[AssessmentsTable.examDate]

            // Check if already reminded
            val alreadyReminded = dbQuery {
                ExamReminderLogTable.selectAll()
                    .where { ExamReminderLogTable.assessmentId eq assessmentId }
                    .any()
            }
            if (alreadyReminded) continue

            // Send reminder to parents of the class
            val parentIds = NotifyRecipients.parentsOfClass(schoolId, className)
            if (parentIds.isNotEmpty()) {
                Notify.toUsers(
                    userIds = parentIds,
                    category = "exam_reminder",
                    title = "Exam Tomorrow: $examName — $subject",
                    body = "Reminder: $examName for $subject is tomorrow (${examDate?.toString() ?: tomorrowIst.toString()}). Ensure your child is prepared.",
                    schoolId = schoolId,
                    deepLink = "/parent/academics?tab=exams",
                    refType = "assessment",
                    refId = assessmentId.toString(),
                )
            }

            // Stamp reminder log
            dbQuery {
                ExamReminderLogTable.insert {
                    it[ExamReminderLogTable.assessmentId] = assessmentId
                    it[ExamReminderLogTable.schoolId] = schoolId
                    it[remindedAt] = Instant.now()
                }
            }
            sentCount++
        }

        if (sentCount > 0) {
            logger.info("[$TAG] checkExamReminders: sent {} exam reminders", sentCount)
        }
    }
}
