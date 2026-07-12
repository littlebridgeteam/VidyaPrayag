/*
 * File: XpHooks.kt
 * Module: feature/gamification
 *
 * Lightweight XP awarding hooks designed to be called from existing routes
 * after key student actions. All hooks are fire-and-forget — failures are
 * logged but never propagated, so gamification can never break core flows.
 *
 * Usage:
 *   XpHooks.onAttendancePresent(studentId, schoolId)
 *   XpHooks.onQuizCompleted(studentId, schoolId, correct, total)
 *   XpHooks.onAssessmentMarked(studentId, schoolId, marks, maxMarks)
 *   XpHooks.onHomeworkReviewed(studentId, schoolId, grade)
 *   XpHooks.onTeacherEncourage(studentId, schoolId, amount, reason)
 *
 * Spec ref: GAMIFICATION_SYSTEM_SPEC.md §5, §27
 */
package com.littlebridge.enrollplus.feature.gamification

import org.slf4j.LoggerFactory
import java.util.UUID

object XpHooks {

    private val logger = LoggerFactory.getLogger("XpHooks")

    // XP amounts per spec §5
    private const val XP_ATTENDANCE_PRESENT = 5
    private const val XP_QUIZ_BASE = 10
    private const val XP_QUIZ_BONUS_PER_CORRECT = 2
    private const val XP_ASSESSMENT_BASE = 15
    private const val XP_ASSESSMENT_BONUS_HIGH = 10  // >80% score
    private const val XP_HOMEWORK_REVIEWED = 8
    private const val XP_HOMEWORK_EXCELLENT = 5  // bonus for A grade
    private const val XP_SYLLABUS_TOPIC_COVERED = 3
    private const val XP_DAILY_LOGIN = 2

    suspend fun onAttendancePresent(studentId: UUID, schoolId: UUID) {
        try {
            GamificationService.awardXp(
                studentId = studentId,
                schoolId = schoolId,
                amount = XP_ATTENDANCE_PRESENT,
                reason = "Present in class",
                source = "attendance",
                category = "ACADEMIC"
            )
            BadgeCriteriaEvaluator.evaluateBadges(studentId, schoolId)
        } catch (e: Exception) {
            logger.warn("XpHooks.onAttendancePresent failed: ${e.message}")
        }
    }

    suspend fun onQuizCompleted(studentId: UUID, schoolId: UUID, correct: Int, total: Int) {
        try {
            val amount = XP_QUIZ_BASE + (correct * XP_QUIZ_BONUS_PER_CORRECT)
            GamificationService.awardXp(
                studentId = studentId,
                schoolId = schoolId,
                amount = amount,
                reason = "Quiz completed: $correct/$total correct",
                source = "quiz",
                category = "ACADEMIC"
            )
            BadgeCriteriaEvaluator.evaluateBadges(studentId, schoolId)
        } catch (e: Exception) {
            logger.warn("XpHooks.onQuizCompleted failed: ${e.message}")
        }
    }

    suspend fun onAssessmentMarked(studentId: UUID, schoolId: UUID, marks: Double?, maxMarks: Double) {
        try {
            if (marks == null) return  // absent — no XP
            val percentage = if (maxMarks > 0) (marks / maxMarks) * 100 else 0.0
            val amount = XP_ASSESSMENT_BASE + if (percentage >= 80) XP_ASSESSMENT_BONUS_HIGH else 0
            GamificationService.awardXp(
                studentId = studentId,
                schoolId = schoolId,
                amount = amount,
                reason = "Assessment marked: ${"%.1f".format(percentage)}%",
                source = "assessment",
                category = "ACADEMIC"
            )
            BadgeCriteriaEvaluator.evaluateBadges(studentId, schoolId)
        } catch (e: Exception) {
            logger.warn("XpHooks.onAssessmentMarked failed: ${e.message}")
        }
    }

    suspend fun onHomeworkReviewed(studentId: UUID, schoolId: UUID, grade: String? = null) {
        try {
            val amount = XP_HOMEWORK_REVIEWED + if (grade?.startsWith("A", ignoreCase = true) == true) XP_HOMEWORK_EXCELLENT else 0
            GamificationService.awardXp(
                studentId = studentId,
                schoolId = schoolId,
                amount = amount,
                reason = "Homework reviewed" + (grade?.let { " (Grade: $it)" } ?: ""),
                source = "homework",
                category = "ACADEMIC"
            )
            BadgeCriteriaEvaluator.evaluateBadges(studentId, schoolId)
        } catch (e: Exception) {
            logger.warn("XpHooks.onHomeworkReviewed failed: ${e.message}")
        }
    }

    suspend fun onTeacherEncourage(studentId: UUID, schoolId: UUID, amount: Int = 10, reason: String = "Keep up the great work!") {
        try {
            GamificationService.awardXp(
                studentId = studentId,
                schoolId = schoolId,
                amount = amount,
                reason = reason,
                source = "teacher_encourage",
                category = "CHARACTER"
            )
            BadgeCriteriaEvaluator.evaluateBadges(studentId, schoolId)
        } catch (e: Exception) {
            logger.warn("XpHooks.onTeacherEncourage failed: ${e.message}")
        }
    }

    suspend fun onSyllabusTopicCovered(studentId: UUID, schoolId: UUID) {
        try {
            GamificationService.awardXp(
                studentId = studentId,
                schoolId = schoolId,
                amount = XP_SYLLABUS_TOPIC_COVERED,
                reason = "Syllabus topic covered",
                source = "syllabus",
                category = "ACADEMIC"
            )
            BadgeCriteriaEvaluator.evaluateBadges(studentId, schoolId)
        } catch (e: Exception) {
            logger.warn("XpHooks.onSyllabusTopicCovered failed: ${e.message}")
        }
    }

    suspend fun onDailyLogin(studentId: UUID, schoolId: UUID) {
        try {
            GamificationService.awardXp(
                studentId = studentId,
                schoolId = schoolId,
                amount = XP_DAILY_LOGIN,
                reason = "Daily login",
                source = "login",
                category = "ENGAGEMENT"
            )
        } catch (e: Exception) {
            logger.warn("XpHooks.onDailyLogin failed: ${e.message}")
        }
    }
}
