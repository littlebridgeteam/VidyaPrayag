/*
 * File: SkillTestJobScheduler.kt
 * Module: feature.skilltest
 *
 * Weekly background job for the Skill Test System. Two responsibilities:
 *
 *   1. Weekly Question Generation — every Sunday at 3 AM UTC, generates a
 *      fresh batch of 100+ MCQ questions for each grade level that has
 *      active children. Old questions are deactivated (is_active = false).
 *
 *   2. Old Question Purge — daily at midnight UTC, deletes questions that
 *      have been inactive for more than 14 days (grace period for in-flight
 *      attempts to finish).
 *
 * Follows the LibraryJobScheduler pattern: long-running coroutine launched
 * at application startup, checks every hour whether it's time to run.
 */
package com.littlebridge.enrollplus.feature.skilltest

import com.littlebridge.enrollplus.db.ChildrenTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.selectAll
import org.slf4j.LoggerFactory
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.concurrent.atomic.AtomicReference

object SkillTestJobScheduler {
    private const val TAG = "SkillTestJobScheduler"
    private val log = LoggerFactory.getLogger("SkillTestJobScheduler")

    private val lastWeeklyRunDate = AtomicReference<LocalDate?>(null)
    private val lastPurgeRunDate = AtomicReference<LocalDate?>(null)

    fun start(scope: CoroutineScope) {
        // Weekly question generation — hourly check, fires on Sunday at 3 AM UTC
        scope.launch {
            log.info("[$TAG] Weekly question generation job started — Sunday 3 AM UTC")
            while (true) {
                delay(60 * 60 * 1000) // 1 hour
                runCatching { checkAndRunWeeklyGeneration() }
                    .onFailure { log.warn("[$TAG] Weekly generation failed: {}", it.message) }
            }
        }

        // Daily purge — hourly check, fires at midnight UTC
        scope.launch {
            log.info("[$TAG] Daily purge job started — midnight UTC")
            while (true) {
                delay(60 * 60 * 1000) // 1 hour
                runCatching { checkAndRunPurge() }
                    .onFailure { log.warn("[$TAG] Purge failed: {}", it.message) }
            }
        }
    }

    // ── Weekly Generation ─────────────────────────────────────────────────

    private suspend fun checkAndRunWeeklyGeneration() {
        val now = ZonedDateTime.now(ZoneOffset.UTC)
        val today = now.toLocalDate()

        // Guard: don't run twice on the same day
        if (lastWeeklyRunDate.get() == today) return

        // Fire on Sunday at 3 AM UTC
        if (now.dayOfWeek != DayOfWeek.SUNDAY || now.hour != 3) return

        if (!lastWeeklyRunDate.compareAndSet(null, today)) return
        log.info("[$TAG] Running weekly question generation for {}", today)

        // Always generate for all 14 canonical grades (Nursery–Class 12)
        val allGrades = SkillTestService.ALL_GRADES

        // Also include any grades from active children that didn't normalize
        // to a canonical grade (edge case — child has a non-standard grade text)
        val childGrades = getActiveGradeLevels()
        val extraGrades = childGrades.filter { it !in allGrades }

        val gradesToGenerate = allGrades + extraGrades
        log.info("[$TAG] Generating questions for {} grade levels: {}", gradesToGenerate.size, gradesToGenerate)

        for (grade in gradesToGenerate) {
            runCatching { SkillTestService.generateWeeklyBatch(grade) }
                .onFailure { log.warn("[$TAG] Generation failed for grade {}: {}", grade, it.message) }
        }

        log.info("[$TAG] Weekly generation complete for {}", today)
    }

    private suspend fun getActiveGradeLevels(): List<String> = dbQuery {
        ChildrenTable.selectAll()
            .where { ChildrenTable.isActive eq true }
            .mapNotNull { it[ChildrenTable.currentGrade] }
            .filter { it.isNotBlank() }
            .distinct()
    }

    // ── Daily Purge ───────────────────────────────────────────────────────

    private suspend fun checkAndRunPurge() {
        val now = ZonedDateTime.now(ZoneOffset.UTC)
        val today = now.toLocalDate()

        if (lastPurgeRunDate.get() == today) return
        if (now.hour != 0) return

        if (!lastPurgeRunDate.compareAndSet(null, today)) return
        log.info("[$TAG] Running daily purge for {}", today)

        val purged = runCatching { SkillTestService.purgeOldQuestions() }
            .getOrElse { log.warn("[$TAG] Purge failed: {}", it.message); 0 }

        log.info("[$TAG] Purge complete: {} old questions deleted", purged)
    }
}
