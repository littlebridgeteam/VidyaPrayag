/*
 * File: SkillTestService.kt
 * Module: feature.skilltest
 *
 * Core engine for the Skill Test System. Three responsibilities:
 *
 *   1. generateWeeklyBatch() — AI-generates 100+ MCQ questions for a grade
 *      level using Mistral / Gemini / NVIDIA (BATCH lane, non-PII content).
 *      Old questions are deactivated (purged by the job scheduler).
 *
 *   2. startAttempt() / submitAnswer() / completeAttempt() — instant
 *      evaluation flow. Each answer is evaluated against the stored
 *      correct_answer immediately. When all questions are answered, the
 *      attempt is scored and best_score is updated (best-of-all-attempts).
 *
 *   3. awardBadgeOnPass() — if score >= 60 and the child hasn't already
 *      earned the "skill_test_first_pass" badge, award it via the existing
 *      gamification system (GameStudentBadgesTable).
 *
 * The child can retake the test 7 days after their last completed attempt.
 * The 7-day lock is enforced via next_eligible_at on both the attempt and
 * best_scores rows.
 *
 * All AI calls go through AiService.complete() — never direct to LlmClient.
 */
package com.littlebridge.enrollplus.feature.skilltest

import com.littlebridge.enrollplus.db.ChildHolisticMetricsTable
import com.littlebridge.enrollplus.db.ChildrenTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.GameBadgeDefinitionsTable
import com.littlebridge.enrollplus.db.GameStudentBadgesTable
import com.littlebridge.enrollplus.db.SkillTestAnswersTable
import com.littlebridge.enrollplus.db.SkillTestAttemptsTable
import com.littlebridge.enrollplus.db.SkillTestBestScoresTable
import com.littlebridge.enrollplus.db.SkillTestQuestionsTable
import com.littlebridge.enrollplus.feature.ai.AiLane
import com.littlebridge.enrollplus.feature.ai.AiResult
import com.littlebridge.enrollplus.feature.ai.AiService
import com.littlebridge.enrollplus.feature.ai.LlmMessage
import com.littlebridge.enrollplus.feature.gamification.GamificationService
import com.littlebridge.enrollplus.feature.gamification.XpHooks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object SkillTestService {
    private val log = LoggerFactory.getLogger("SkillTestService")
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    // ── Config ────────────────────────────────────────────────────────────
    private const val QUESTIONS_PER_BATCH = 100    // generated weekly per grade
    private const val QUESTIONS_PER_ATTEMPT = 10   // randomly picked from the batch for each child
    private const val PASS_THRESHOLD = 60          // 60% to earn the badge
    private const val RETAKE_LOCK_DAYS = 7L
    private const val XP_SKILL_TEST_BASE = 20
    private const val XP_SKILL_TEST_BONUS_PER_CORRECT = 1
    private const val BADGE_CODE = "skill_test_first_pass"

    /** All 14 supported grade levels, in order from youngest to oldest. */
    val ALL_GRADES: List<String> = listOf(
        "Nursery", "LKG", "UKG",
        "Class 1", "Class 2", "Class 3", "Class 4", "Class 5",
        "Class 6", "Class 7", "Class 8", "Class 9", "Class 10",
        "Class 11", "Class 12",
    )

    /** Per-grade mutex so concurrent eligibility checks don't all fire AI generation. */
    private val generationLocks = ConcurrentHashMap<String, Mutex>()

    /** Per-grade cooldown: after a failed generation, don't retry for 1 hour. */
    private val generationCooldowns = ConcurrentHashMap<String, Instant>()
    private val GENERATION_COOLDOWN_MINUTES = 60L

    /**
     * Background scope for fire-and-forget immediate generation. Tied to the
     * JVM lifecycle because this is an object singleton; use SupervisorJob so a
     * single failed grade doesn't cancel the whole scope.
     */
    private val generationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Fire-and-forget trigger for immediate question generation. Used by the
     * eligibility endpoint so the HTTP response returns quickly while the AI
     * generation runs in the background. The grade-level lock prevents duplicate
     * concurrent runs.
     */
    fun triggerImmediateGeneration(gradeLevel: String) {
        generationScope.launch {
            runCatching { ensureQuestionsForGrade(gradeLevel) }
                .onFailure { log.warn("Background immediate generation failed for grade {}: {}", gradeLevel, it.message) }
        }
    }

    /**
     * Ensure a grade has active questions. If none exist, immediately generate
     * a fresh weekly batch. Used by the eligibility endpoint so parents never
     * see "Questions are being generated" without the backend actually trying.
     * The weekly scheduled job still runs on top of this.
     *
     * Returns true if active questions exist (or were just generated).
     */
    suspend fun ensureQuestionsForGrade(gradeLevel: String): Boolean {
        // Fast path: already has questions
        val hasQuestions = dbQuery {
            SkillTestQuestionsTable.selectAll()
                .where {
                    (SkillTestQuestionsTable.isActive eq true) and
                    (SkillTestQuestionsTable.gradeLevel eq gradeLevel)
                }
                .count() > 0
        }
        if (hasQuestions) return true

        // Serialize generation per grade. If another caller is already
        // generating, wait for it and then re-check.
        val lock = generationLocks.computeIfAbsent(gradeLevel) { Mutex() }
        return lock.withLock {
            val stillHasQuestions = dbQuery {
                SkillTestQuestionsTable.selectAll()
                    .where {
                        (SkillTestQuestionsTable.isActive eq true) and
                        (SkillTestQuestionsTable.gradeLevel eq gradeLevel)
                    }
                    .count() > 0
            }
            if (stillHasQuestions) return@withLock true

            // Cooldown: if generation for this grade failed recently, don't retry.
            val cooldownUntil = generationCooldowns[gradeLevel]
            if (cooldownUntil != null && Instant.now().isBefore(cooldownUntil)) {
                log.info("Skipping immediate generation for grade {} — in cooldown until {}", gradeLevel, cooldownUntil)
                return@withLock false
            }

            log.info("No active questions for grade {} — triggering immediate generation", gradeLevel)
            val generatedCount = generateWeeklyBatch(gradeLevel)
            val success = generatedCount > 0
            if (!success) {
                log.warn("Immediate generation produced no questions for grade {} — entering 1h cooldown", gradeLevel)
                generationCooldowns[gradeLevel] = Instant.now().plus(GENERATION_COOLDOWN_MINUTES, ChronoUnit.MINUTES)
            } else {
                generationCooldowns.remove(gradeLevel)
            }
            success
        }
    }

    /**
     * Normalize a free-text grade string (from children.current_grade) to one
     * of the 14 canonical grades. Returns null if the grade cannot be recognized.
     */
    fun normalizeGrade(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val trimmed = raw.trim()

        // Direct match (case-insensitive)
        ALL_GRADES.forEach { if (trimmed.equals(it, ignoreCase = true)) return it }

        val lower = trimmed.lowercase().replace(".", "").replace("-", " ").replace("_", " ").trim()

        // Pre-primary
        if (lower in setOf("nursery", "pre nursery", "playgroup", "play group", "prekg", "pre kg")) return "Nursery"
        if (lower in setOf("lkg", "junior kg", "jkg", "lower kg", "jk")) return "LKG"
        if (lower in setOf("ukg", "senior kg", "skg", "upper kg", "sk")) return "UKG"

        // Class 1–12: match "class N", "grade n", "nth", "n std", "std n", or just "n"
        val classMatch = Regex("""^(?:class|grade|std|standard)\s*(\d{1,2})$""").find(lower)
        if (classMatch != null) {
            val n = classMatch.groupValues[1].toIntOrNull()
            if (n in 1..12) return "Class $n"
        }
        val ordinalMatch = Regex("""^(\d{1,2})(?:st|nd|rd|th)?$""").find(lower)
        if (ordinalMatch != null) {
            val n = ordinalMatch.groupValues[1].toIntOrNull()
            if (n in 1..12) return "Class $n"
        }

        return null
    }

    // ── DTOs ──────────────────────────────────────────────────────────────

    @Serializable
    data class QuestionDto(
        val id: String,
        val subject: String,
        @SerialName("question_text") val questionText: String,
        val options: List<String>,
        val difficulty: String,
    )

    @Serializable
    data class QuestionWithAnswerDto(
        val id: String,
        val subject: String,
        @SerialName("question_text") val questionText: String,
        val options: List<String>,
        @SerialName("correct_answer") val correctAnswer: String,
        val explanation: String,
        val difficulty: String,
    )

    @Serializable
    data class AttemptDto(
        val id: String,
        val status: String,
        @SerialName("total_questions") val totalQuestions: Int,
        @SerialName("correct_count") val correctCount: Int,
        @SerialName("score_percentage") val scorePercentage: Int,
        @SerialName("started_at") val startedAt: String,
        @SerialName("completed_at") val completedAt: String? = null,
        @SerialName("next_eligible_at") val nextEligibleAt: String? = null,
    )

    @Serializable
    data class BestScoreDto(
        @SerialName("best_score") val bestScore: Int,
        @SerialName("attempts_count") val attemptsCount: Int,
        @SerialName("badge_earned") val badgeEarned: Boolean,
        @SerialName("last_attempt_at") val lastAttemptAt: String? = null,
        @SerialName("next_eligible_at") val nextEligibleAt: String? = null,
    )

    @Serializable
    data class AnswerResultDto(
        @SerialName("question_id") val questionId: String,
        @SerialName("is_correct") val isCorrect: Boolean,
        @SerialName("correct_answer") val correctAnswer: String,
        val explanation: String,
        @SerialName("current_correct_count") val currentCorrectCount: Int,
        @SerialName("questions_answered") val questionsAnswered: Int,
        @SerialName("total_questions") val totalQuestions: Int,
        @SerialName("attempt_completed") val attemptCompleted: Boolean,
        @SerialName("final_score") val finalScore: Int? = null,
        @SerialName("badge_earned") val badgeEarned: Boolean? = null,
    )

    @Serializable
    data class EligibilityDto(
        val eligible: Boolean,
        val reason: String,
        @SerialName("next_eligible_at") val nextEligibleAt: String? = null,
        @SerialName("best_score") val bestScore: BestScoreDto? = null,
        @SerialName("has_questions") val hasQuestions: Boolean,
        @SerialName("grade_level") val gradeLevel: String? = null,
    )

    // ── 1. AI Question Generation ─────────────────────────────────────────

    /**
     * Generate a fresh batch of QUESTIONS_PER_BATCH MCQ questions for a grade
     * level. Uses the BATCH lane (Mistral / Gemini / NVIDIA — non-PII content
     * so training-opt-in providers are fine). Old active questions for the
     * same grade are deactivated (is_active = false) before the new batch
     * is inserted.
     *
     * Returns the number of questions generated, or 0 on failure.
     */
    suspend fun generateWeeklyBatch(gradeLevel: String): Int {
        log.info("Generating weekly skill test batch for grade: {}", gradeLevel)

        // Pre-primary (Nursery, LKG, UKG) gets age-appropriate subjects
        val isPrePrimary = gradeLevel in setOf("Nursery", "LKG", "UKG")
        val subjects = if (isPrePrimary) {
            listOf("English", "Mathematics", "General Knowledge", "Environmental Awareness")
        } else {
            listOf("Mathematics", "English", "Science", "General Knowledge")
        }
        val questionsPerSubject = QUESTIONS_PER_BATCH / subjects.size // 25 each
        val batchId = UUID.randomUUID()
        var totalGenerated = 0

        for (subject in subjects) {
            val generated = generateQuestionsForSubject(
                batchId = batchId,
                gradeLevel = gradeLevel,
                subject = subject,
                count = questionsPerSubject,
            )
            if (generated != null) {
                persistQuestions(batchId, gradeLevel, subject, generated)
                totalGenerated += generated.size
            } else {
                log.warn("AI generation failed for {} / {} — skipping", gradeLevel, subject)
            }
        }

        if (totalGenerated > 0) {
            // Deactivate old questions for this grade (they'll be purged later)
            dbQuery {
                SkillTestQuestionsTable.update({
                    (SkillTestQuestionsTable.gradeLevel eq gradeLevel) and
                    (SkillTestQuestionsTable.batchId neq batchId) and
                    (SkillTestQuestionsTable.isActive eq true)
                }) {
                    it[isActive] = false
                }
            }
            log.info("Weekly batch complete: {} questions for grade {}", totalGenerated, gradeLevel)
        }

        return totalGenerated
    }

    private suspend fun generateQuestionsForSubject(
        batchId: UUID,
        gradeLevel: String,
        subject: String,
        count: Int,
    ): List<GeneratedMcq>? {
        val isPrePrimary = gradeLevel in setOf("Nursery", "LKG", "UKG")
        val ageGuidance = if (isPrePrimary) {
            """- The student is 3-5 years old. Use very simple language and picture-based concepts.
            - Focus on: colors, shapes, counting 1-20, alphabet recognition, rhymes, animals, fruits, good habits.
            - Keep questions very short and easy to understand."""
        } else {
            """- The student is in $gradeLevel (CBSE/NCERT curriculum).
            - Cover age-appropriate topics from the NCERT syllabus for this grade level.
            - For higher classes (9-12), include application-based and conceptual questions."""
        }

        val prompt = """
            Generate $count multiple-choice questions (MCQs) for a $gradeLevel student in $subject.
            These are for an Indian school student (CBSE/NCERT aligned).

            These questions will be stored in a weekly pool. Each child will be asked a small,
            random sample of them (5-10 questions). Therefore every question must be:
            - Self-contained and unambiguous
            - Diagnostic of the student's grasp of this subject
            - Balanced across conceptual, procedural, and application-level thinking
            so that the subject score accurately reflects the child's Literacy, Numeracy,
            or Creativity competency on the parent dashboard.

            Requirements:
            - Each question must have exactly 4 options labeled A, B, C, D
            - Only one correct answer per question
            - Mix of difficulty: ~40% easy, ~40% medium, ~20% hard
            - Include a brief explanation for why the correct answer is right
            $ageGuidance

            Return ONLY a JSON array:
            [
              {
                "question_text": "What is 5 + 3?",
                "options": ["A) 6", "B) 7", "C) 8", "D) 9"],
                "correct_answer": "C",
                "explanation": "5 + 3 = 8",
                "difficulty": "easy"
              }
            ]

            Generate exactly $count questions. Do not include any text outside the JSON array.
        """.trimIndent()

        val result: AiResult = AiService.complete(
            feature = "skill_test_gen",
            lane = AiLane.BATCH,
            messages = listOf(LlmMessage(role = "user", content = prompt)),
            containsPii = false,
            temperature = 0.6,
            maxTokens = 8192,
            cache = false, // fresh questions each week — don't cache
        )

        if (!result.ok || result.content.isNullOrBlank()) {
            log.warn("AI question generation failed for {} / {}: {}", gradeLevel, subject, result.errorMessage)
            return null
        }

        return parseMcqJson(result.content)
    }

    private fun parseMcqJson(content: String): List<GeneratedMcq>? {
        val cleaned = content.trim()
            .removePrefix("```json").removePrefix("```")
            .removeSuffix("```").trim()

        val arr = try {
            json.parseToJsonElement(cleaned).jsonArray
        } catch (e: Exception) {
            // Fallback 1: extract the first JSON array from the response.
            val firstOpen = cleaned.indexOf('[')
            val lastClose = cleaned.lastIndexOf(']')
            if (firstOpen == -1 || lastClose == -1 || lastClose < firstOpen) {
                // Fallback 2: LLM output was truncated mid-array (no closing ]).
                // Try to salvage complete objects from the truncated text.
                if (firstOpen != -1) {
                    val truncated = cleaned.substring(firstOpen)
                    // Find the last complete object (ends with }) followed by a comma or whitespace
                    val lastCompleteObj = truncated.lastIndexOf("}")
                    if (lastCompleteObj > 0) {
                        val salvaged = truncated.substring(0, lastCompleteObj + 1) + "]"
                        try {
                            json.parseToJsonElement(salvaged).jsonArray
                        } catch (e3: Exception) {
                            log.warn("Failed to parse MCQ JSON (truncated salvage): {}", e3.message)
                            return null
                        }
                    } else {
                        log.warn("Failed to parse MCQ JSON: no complete objects in truncated response")
                        return null
                    }
                } else {
                    log.warn("Failed to parse MCQ JSON and could not locate JSON array: {}", e.message)
                    return null
                }
            } else {
                val extracted = cleaned.substring(firstOpen, lastClose + 1)
                try {
                    json.parseToJsonElement(extracted).jsonArray
                } catch (e2: Exception) {
                    // Fallback 2: try truncation salvage on the extracted substring
                    val lastObj = extracted.lastIndexOf("}")
                    if (lastObj > 0) {
                        val salvaged = extracted.substring(0, lastObj + 1) + "]"
                        try {
                            json.parseToJsonElement(salvaged).jsonArray
                        } catch (e3: Exception) {
                            log.warn("Failed to parse extracted MCQ JSON array: {}", e2.message)
                            return null
                        }
                    } else {
                        log.warn("Failed to parse extracted MCQ JSON array: {}", e2.message)
                        return null
                    }
                }
            }
        }

        val parsed = arr.mapNotNull { el ->
            val obj = try { el.jsonObject } catch (e: Exception) { return@mapNotNull null }
            GeneratedMcq(
                questionText = obj["question_text"]?.jsonPrimitive?.contentOrNull ?: "",
                options = obj["options"]?.jsonArray?.map { it.jsonPrimitive.contentOrNull ?: "" } ?: emptyList(),
                correctAnswer = obj["correct_answer"]?.jsonPrimitive?.contentOrNull ?: "A",
                explanation = obj["explanation"]?.jsonPrimitive?.contentOrNull ?: "",
                difficulty = obj["difficulty"]?.jsonPrimitive?.contentOrNull ?: "medium",
            )
        }.filter { it.questionText.isNotBlank() && it.options.size == 4 }

        if (parsed.isEmpty()) {
            log.warn("MCQ JSON parsed but 0 valid questions extracted (raw length={})", cleaned.length)
        }
        return parsed
    }

    private suspend fun persistQuestions(
        batchId: UUID,
        gradeLevel: String,
        subject: String,
        questions: List<GeneratedMcq>,
    ) = dbQuery {
        for (q in questions) {
            SkillTestQuestionsTable.insert {
                it[SkillTestQuestionsTable.batchId] = batchId
                it[SkillTestQuestionsTable.gradeLevel] = gradeLevel
                it[SkillTestQuestionsTable.subject] = subject
                it[SkillTestQuestionsTable.questionText] = q.questionText
                it[SkillTestQuestionsTable.options] = json.encodeToString(
                    ListSerializer(serializer<String>()),
                    q.options,
                )
                it[SkillTestQuestionsTable.correctAnswer] = q.correctAnswer
                it[SkillTestQuestionsTable.explanation] = q.explanation
                it[SkillTestQuestionsTable.difficulty] = q.difficulty
                it[SkillTestQuestionsTable.isActive] = true
                it[SkillTestQuestionsTable.aiProvider] = "batch"
                it[SkillTestQuestionsTable.createdAt] = Instant.now()
            }
        }
    }

    // ── 2. Test Flow: Start, Answer, Complete ─────────────────────────────

    /**
     * Check if a child is eligible to take a skill test.
     * Eligible if: no prior attempt OR 7 days have passed since last completed attempt.
     */
    suspend fun checkEligibility(childId: UUID): EligibilityDto = dbQuery {
        // Look up the child's grade
        val child = ChildrenTable.selectAll()
            .where { ChildrenTable.id eq childId }
            .firstOrNull()
        val rawGrade = child?.get(ChildrenTable.currentGrade)
        val gradeLevel = normalizeGrade(rawGrade)

        if (gradeLevel == null) {
            return@dbQuery EligibilityDto(
                eligible = false,
                reason = "Please update your child's class in the profile to access skill tests.",
                hasQuestions = false,
                gradeLevel = null,
            )
        }

        val bestScore = SkillTestBestScoresTable.selectAll()
            .firstOrNull { it[SkillTestBestScoresTable.childId] == childId }

        val hasQuestions = SkillTestQuestionsTable.selectAll()
            .where {
                (SkillTestQuestionsTable.isActive eq true) and
                (SkillTestQuestionsTable.gradeLevel eq gradeLevel)
            }
            .count() > 0

        if (bestScore == null) {
            return@dbQuery EligibilityDto(
                eligible = hasQuestions,
                reason = if (hasQuestions) "Ready to take your first test!" else "Questions are being generated for $gradeLevel. Please check back soon.",
                hasQuestions = hasQuestions,
                gradeLevel = gradeLevel,
            )
        }

        val nextEligible = bestScore[SkillTestBestScoresTable.nextEligibleAt]
        val now = Instant.now()
        val eligible = nextEligible == null || !now.isBefore(nextEligible)

        EligibilityDto(
            eligible = eligible && hasQuestions,
            reason = when {
                !hasQuestions -> "Questions are being generated for $gradeLevel. Please check back soon."
                eligible -> "Ready for your next attempt!"
                else -> "You can retake the test after the cooldown period."
            },
            nextEligibleAt = nextEligible?.toString(),
            bestScore = BestScoreDto(
                bestScore = bestScore[SkillTestBestScoresTable.bestScore],
                attemptsCount = bestScore[SkillTestBestScoresTable.attemptsCount],
                badgeEarned = bestScore[SkillTestBestScoresTable.badgeEarned],
                lastAttemptAt = bestScore[SkillTestBestScoresTable.lastAttemptAt]?.toString(),
                nextEligibleAt = nextEligible?.toString(),
            ),
            hasQuestions = hasQuestions,
            gradeLevel = gradeLevel,
        )
    }

    /**
     * Start a new test attempt for a child. Returns the attempt ID + the
     * list of questions (without correct answers).
     */
    suspend fun startAttempt(
        childId: UUID,
        parentId: UUID,
        schoolId: UUID? = null,
    ): Pair<String, List<QuestionDto>>? = dbQuery {
        // Look up the child's grade
        val child = ChildrenTable.selectAll()
            .where { ChildrenTable.id eq childId }
            .firstOrNull() ?: return@dbQuery null
        val gradeLevel = normalizeGrade(child[ChildrenTable.currentGrade]) ?: return@dbQuery null

        // Check eligibility
        val bestScore = SkillTestBestScoresTable.selectAll()
            .firstOrNull { it[SkillTestBestScoresTable.childId] == childId }
        val nextEligible = bestScore?.get(SkillTestBestScoresTable.nextEligibleAt)
        if (nextEligible != null && Instant.now().isBefore(nextEligible)) {
            return@dbQuery null // not eligible yet
        }

        // Get active questions for this child's grade only
        val questions = SkillTestQuestionsTable.selectAll()
            .where {
                (SkillTestQuestionsTable.isActive eq true) and
                (SkillTestQuestionsTable.gradeLevel eq gradeLevel)
            }
            .toList()

        if (questions.isEmpty()) return@dbQuery null

        // Randomly pick QUESTIONS_PER_ATTEMPT questions, evenly spread across
        // subjects so every metric (Literacy/Numeracy/Creativity) gets sampled.
        val questionsBySubject = questions.groupBy { it[SkillTestQuestionsTable.subject] }
        val subjects = questionsBySubject.keys.shuffled()
        val base = QUESTIONS_PER_ATTEMPT / subjects.size
        val remainder = QUESTIONS_PER_ATTEMPT % subjects.size
        val selected = mutableListOf<ResultRow>()
        subjects.forEachIndexed { index, subject ->
            val count = base + if (index < remainder) 1 else 0
            selected += questionsBySubject[subject]?.shuffled()?.take(count) ?: emptyList()
        }
        val finalQuestions = selected.shuffled()

        if (finalQuestions.isEmpty()) return@dbQuery null

        val batchId = finalQuestions.first()[SkillTestQuestionsTable.batchId]

        // Create the attempt
        val attemptId = UUID.randomUUID()
        SkillTestAttemptsTable.insert {
            it[SkillTestAttemptsTable.id] = attemptId
            it[SkillTestAttemptsTable.childId] = childId
            it[SkillTestAttemptsTable.parentId] = parentId
            it[SkillTestAttemptsTable.schoolId] = schoolId
            it[SkillTestAttemptsTable.batchId] = batchId
            it[SkillTestAttemptsTable.gradeLevel] = gradeLevel
            it[SkillTestAttemptsTable.totalQuestions] = finalQuestions.size
            it[SkillTestAttemptsTable.correctCount] = 0
            it[SkillTestAttemptsTable.scorePercentage] = 0
            it[SkillTestAttemptsTable.status] = "in_progress"
            it[SkillTestAttemptsTable.startedAt] = Instant.now()
            it[SkillTestAttemptsTable.completedAt] = null
            it[SkillTestAttemptsTable.nextEligibleAt] = null
            it[SkillTestAttemptsTable.createdAt] = Instant.now()
        }

        val questionDtos = finalQuestions.map { row ->
            QuestionDto(
                id = row[SkillTestQuestionsTable.id].value.toString(),
                subject = row[SkillTestQuestionsTable.subject],
                questionText = row[SkillTestQuestionsTable.questionText],
                options = parseOptionsJson(row[SkillTestQuestionsTable.options]),
                difficulty = row[SkillTestQuestionsTable.difficulty],
            )
        }

        attemptId.toString() to questionDtos
    }

    /**
     * Submit a single answer. Instantly evaluates against the stored
     * correct_answer. If this was the last unanswered question, auto-completes
     * the attempt (scoring + best score update + badge check + XP award).
     */
    suspend fun submitAnswer(
        attemptId: UUID,
        questionId: UUID,
        selectedAnswer: String,
    ): AnswerResultDto? = dbQuery {
        // Fetch the question to check correctness
        val question = SkillTestQuestionsTable.selectAll()
            .firstOrNull { it[SkillTestQuestionsTable.id] == questionId }
            ?: return@dbQuery null

        val correctAnswer = question[SkillTestQuestionsTable.correctAnswer]
        val isCorrect = selectedAnswer.equals(correctAnswer, ignoreCase = true)

        // Insert the answer (unique constraint prevents duplicates)
        try {
            SkillTestAnswersTable.insert {
                it[SkillTestAnswersTable.attemptId] = attemptId
                it[SkillTestAnswersTable.questionId] = questionId
                it[SkillTestAnswersTable.selectedAnswer] = selectedAnswer.uppercase()
                it[SkillTestAnswersTable.isCorrect] = isCorrect
                it[SkillTestAnswersTable.answeredAt] = Instant.now()
            }
        } catch (e: Exception) {
            // Already answered — return existing result
            val existing = SkillTestAnswersTable.selectAll()
                .where {
                    (SkillTestAnswersTable.attemptId eq attemptId) and
                    (SkillTestAnswersTable.questionId eq questionId)
                }.firstOrNull() ?: return@dbQuery null
            return@dbQuery buildAnswerResult(existing[SkillTestAnswersTable.isCorrect], question, attemptId)
        }

        // Check if all questions answered
        val attempt = SkillTestAttemptsTable.selectAll()
            .firstOrNull { it[SkillTestAttemptsTable.id] == attemptId }
            ?: return@dbQuery null

        val totalQuestions = attempt[SkillTestAttemptsTable.totalQuestions]
        val answersCount = SkillTestAnswersTable.selectAll()
            .where { SkillTestAnswersTable.attemptId eq attemptId }
            .count().toInt()

        val correctSoFar = SkillTestAnswersTable.selectAll()
            .where {
                (SkillTestAnswersTable.attemptId eq attemptId) and
                (SkillTestAnswersTable.isCorrect eq true)
            }
            .count().toInt()

        val attemptCompleted = answersCount >= totalQuestions
        var finalScore: Int? = null
        var badgeEarned: Boolean? = null

        if (attemptCompleted) {
            finalScore = if (totalQuestions > 0) (correctSoFar * 100) / totalQuestions else 0
            val completedAt = Instant.now()
            val nextEligible = completedAt.plus(RETAKE_LOCK_DAYS, ChronoUnit.DAYS)
            val childId = attempt[SkillTestAttemptsTable.childId]
            val schoolId = attempt[SkillTestAttemptsTable.schoolId]

            // Complete the attempt
            SkillTestAttemptsTable.update({
                SkillTestAttemptsTable.id eq attemptId
            }) {
                it[SkillTestAttemptsTable.correctCount] = correctSoFar
                it[SkillTestAttemptsTable.scorePercentage] = finalScore
                it[SkillTestAttemptsTable.status] = "completed"
                it[SkillTestAttemptsTable.completedAt] = completedAt
                it[SkillTestAttemptsTable.nextEligibleAt] = nextEligible
            }

            // Update best score
            badgeEarned = updateBestScore(
                childId = childId,
                attemptId = attemptId,
                finalScore = finalScore,
                completedAt = completedAt,
                nextEligible = nextEligible,
            )

            // Award XP via gamification
            if (schoolId != null) {
                try {
                    XpHooks.onQuizCompleted(childId, schoolId, correctSoFar, totalQuestions)
                } catch (e: Exception) {
                    log.warn("XP award failed for skill test: {}", e.message)
                }
            }

            // Update per-child academic competencies + emotional intelligence
            // shown on the parent Track Progress / Academics Overview tab.
            try {
                updateHolisticMetrics(attemptId, childId)
            } catch (e: Exception) {
                log.warn("Holistic metrics update failed for skill test: {}", e.message)
            }
        }

        AnswerResultDto(
            questionId = questionId.toString(),
            isCorrect = isCorrect,
            correctAnswer = correctAnswer,
            explanation = question[SkillTestQuestionsTable.explanation],
            currentCorrectCount = correctSoFar,
            questionsAnswered = answersCount,
            totalQuestions = totalQuestions,
            attemptCompleted = attemptCompleted,
            finalScore = finalScore,
            badgeEarned = badgeEarned,
        )
    }

    // ── 3. Best Score + Badge ─────────────────────────────────────────────

    /**
     * Update the best score for a child. Only updates if the new score is
     * higher than the existing best. Awards the badge on first 60+ score.
     * Returns true if badge was newly earned.
     */
    private suspend fun updateBestScore(
        childId: UUID,
        attemptId: UUID,
        finalScore: Int,
        completedAt: Instant,
        nextEligible: Instant,
    ): Boolean = dbQuery {
        val existing = SkillTestBestScoresTable.selectAll()
            .firstOrNull { it[SkillTestBestScoresTable.childId] == childId }

        val isNewBest = existing == null || finalScore > existing!![SkillTestBestScoresTable.bestScore]
        val attemptsCount = (existing?.get(SkillTestBestScoresTable.attemptsCount) ?: 0) + 1
        val badgeAlreadyEarned = existing?.get(SkillTestBestScoresTable.badgeEarned) ?: false
        val shouldAwardBadge = finalScore >= PASS_THRESHOLD && !badgeAlreadyEarned

        if (existing == null) {
            SkillTestBestScoresTable.insert {
                it[SkillTestBestScoresTable.id] = UUID.randomUUID()
                it[SkillTestBestScoresTable.childId] = childId
                it[SkillTestBestScoresTable.bestScore] = finalScore
                it[SkillTestBestScoresTable.bestAttemptId] = attemptId
                it[SkillTestBestScoresTable.attemptsCount] = attemptsCount
                it[SkillTestBestScoresTable.badgeEarned] = shouldAwardBadge
                it[SkillTestBestScoresTable.lastAttemptAt] = completedAt
                it[SkillTestBestScoresTable.nextEligibleAt] = nextEligible
                it[SkillTestBestScoresTable.updatedAt] = Instant.now()
            }
        } else {
            SkillTestBestScoresTable.update({
                SkillTestBestScoresTable.childId eq childId
            }) {
                if (isNewBest) {
                    it[SkillTestBestScoresTable.bestScore] = finalScore
                    it[SkillTestBestScoresTable.bestAttemptId] = attemptId
                }
                it[SkillTestBestScoresTable.attemptsCount] = attemptsCount
                if (shouldAwardBadge) it[SkillTestBestScoresTable.badgeEarned] = true
                it[SkillTestBestScoresTable.lastAttemptAt] = completedAt
                it[SkillTestBestScoresTable.nextEligibleAt] = nextEligible
                it[SkillTestBestScoresTable.updatedAt] = Instant.now()
            }
        }

        if (shouldAwardBadge) {
            awardSkillTestBadge(childId)
            log.info("Skill test badge awarded to child {} (score={})", childId, finalScore)
        }

        shouldAwardBadge
    }

    /**
     * Award the "skill_test_first_pass" badge via the gamification system.
     */
    private suspend fun awardSkillTestBadge(childId: UUID) = dbQuery {
        val badgeDef = GameBadgeDefinitionsTable.selectAll()
            .where { GameBadgeDefinitionsTable.code eq BADGE_CODE }
            .firstOrNull() ?: return@dbQuery

        val badgeId = badgeDef[GameBadgeDefinitionsTable.id].value

        // Check if already earned
        val alreadyEarned = GameStudentBadgesTable.selectAll()
            .where {
                (GameStudentBadgesTable.studentId eq childId) and
                (GameStudentBadgesTable.badgeId eq badgeId)
            }.firstOrNull() != null

        if (!alreadyEarned) {
            GameStudentBadgesTable.insert {
                it[GameStudentBadgesTable.studentId] = childId
                it[GameStudentBadgesTable.badgeId] = badgeId
                it[GameStudentBadgesTable.earnedAt] = Instant.now()
                it[GameStudentBadgesTable.awardedBy] = null
            }
        }
    }

    /**
     * Update per-child academic competencies and emotional-intelligence metrics
     * after a completed skill test attempt. Progress is derived from the
     * subject-wise score breakdown of the attempt.
     *
     * Competencies:
     *   - Literacy  = English score %
     *   - Numeracy  = Mathematics score %
     *   - Creativity = Science + General Knowledge (or Environmental Awareness) avg %
     *
     * Emotional intelligence:
     *   - Empathy    = baseline 0.70
     *   - Resilience = grows with repeated attempts (capped at 0.95)
     *   - Social     = baseline 0.70
     *   - Confidence = overall score %
     */
    private suspend fun updateHolisticMetrics(attemptId: UUID, childId: UUID) = dbQuery {
        val answers = SkillTestAnswersTable
            .join(SkillTestQuestionsTable, org.jetbrains.exposed.sql.JoinType.INNER, SkillTestAnswersTable.questionId, SkillTestQuestionsTable.id)
            .selectAll()
            .where { SkillTestAnswersTable.attemptId eq attemptId }
            .toList()

        if (answers.isEmpty()) return@dbQuery

        val totalAnswered = answers.size
        val correctOverall = answers.count { it[SkillTestAnswersTable.isCorrect] }
        val confidence = if (totalAnswered > 0) correctOverall.toFloat() / totalAnswered.toFloat() else 0f

        val subjectStats = answers
            .groupBy { it[SkillTestQuestionsTable.subject] }
            .mapValues { (_, rows) ->
                val correct = rows.count { it[SkillTestAnswersTable.isCorrect] }
                val total = rows.size
                if (total > 0) correct.toFloat() / total.toFloat() else 0f
            }

        val literacy = subjectStats["English"] ?: 0f
        val numeracy = subjectStats["Mathematics"] ?: 0f
        val science = subjectStats["Science"] ?: 0f
        val gk = subjectStats["General Knowledge"] ?: 0f
        val envAwareness = subjectStats["Environmental Awareness"] ?: 0f

        val creativity = when {
            science > 0f && gk > 0f -> (science + gk) / 2f
            science > 0f -> science
            gk > 0f -> gk
            envAwareness > 0f -> envAwareness
            else -> 0f
        }

        val attemptsCount = SkillTestBestScoresTable.selectAll()
            .firstOrNull { it[SkillTestBestScoresTable.childId] == childId }
            ?.get(SkillTestBestScoresTable.attemptsCount) ?: 1

        val empathy = 0.70f
        val resilience = (0.50f + 0.10f * attemptsCount).coerceAtMost(0.95f)
        val social = 0.70f

        val now = Instant.now()
        val existing = ChildHolisticMetricsTable.selectAll()
            .firstOrNull { it[ChildHolisticMetricsTable.childId] == childId }

        if (existing == null) {
            ChildHolisticMetricsTable.insert {
                it[ChildHolisticMetricsTable.id] = UUID.randomUUID()
                it[ChildHolisticMetricsTable.childId] = childId
                it[ChildHolisticMetricsTable.literacy] = literacy
                it[ChildHolisticMetricsTable.numeracy] = numeracy
                it[ChildHolisticMetricsTable.creativity] = creativity
                it[ChildHolisticMetricsTable.empathy] = empathy
                it[ChildHolisticMetricsTable.resilience] = resilience
                it[ChildHolisticMetricsTable.social] = social
                it[ChildHolisticMetricsTable.confidence] = confidence
                it[ChildHolisticMetricsTable.lastAttemptId] = attemptId
                it[ChildHolisticMetricsTable.updatedAt] = now
            }
        } else {
            ChildHolisticMetricsTable.update({
                ChildHolisticMetricsTable.childId eq childId
            }) {
                it[ChildHolisticMetricsTable.literacy] = literacy
                it[ChildHolisticMetricsTable.numeracy] = numeracy
                it[ChildHolisticMetricsTable.creativity] = creativity
                it[ChildHolisticMetricsTable.empathy] = empathy
                it[ChildHolisticMetricsTable.resilience] = resilience
                it[ChildHolisticMetricsTable.social] = social
                it[ChildHolisticMetricsTable.confidence] = confidence
                it[ChildHolisticMetricsTable.lastAttemptId] = attemptId
                it[ChildHolisticMetricsTable.updatedAt] = now
            }
        }

        log.info(
            "Updated holistic metrics for child {}: literacy={:.2f}, numeracy={:.2f}, creativity={:.2f}, confidence={:.2f}",
            childId, literacy, numeracy, creativity, confidence
        )
    }

    // ── 4. Read Queries ───────────────────────────────────────────────────

    /**
     * Get the best score + status for a child.
     */
    suspend fun getBestScore(childId: UUID): BestScoreDto? = dbQuery {
        val row = SkillTestBestScoresTable.selectAll()
            .firstOrNull { it[SkillTestBestScoresTable.childId] == childId }
            ?: return@dbQuery null

        BestScoreDto(
            bestScore = row[SkillTestBestScoresTable.bestScore],
            attemptsCount = row[SkillTestBestScoresTable.attemptsCount],
            badgeEarned = row[SkillTestBestScoresTable.badgeEarned],
            lastAttemptAt = row[SkillTestBestScoresTable.lastAttemptAt]?.toString(),
            nextEligibleAt = row[SkillTestBestScoresTable.nextEligibleAt]?.toString(),
        )
    }

    /**
     * Get all attempts for a child (history).
     */
    suspend fun getAttemptHistory(childId: UUID): List<AttemptDto> = dbQuery {
        SkillTestAttemptsTable.selectAll()
            .where { SkillTestAttemptsTable.childId eq childId }
            .orderBy(SkillTestAttemptsTable.createdAt, SortOrder.DESC)
            .map { row ->
                AttemptDto(
                    id = row[SkillTestAttemptsTable.id].value.toString(),
                    status = row[SkillTestAttemptsTable.status],
                    totalQuestions = row[SkillTestAttemptsTable.totalQuestions],
                    correctCount = row[SkillTestAttemptsTable.correctCount],
                    scorePercentage = row[SkillTestAttemptsTable.scorePercentage],
                    startedAt = row[SkillTestAttemptsTable.startedAt].toString(),
                    completedAt = row[SkillTestAttemptsTable.completedAt]?.toString(),
                    nextEligibleAt = row[SkillTestAttemptsTable.nextEligibleAt]?.toString(),
                )
            }
    }

    /**
     * Get questions with answers for review (after test completion).
     */
    suspend fun getAttemptReview(attemptId: UUID): List<QuestionWithAnswerDto> = dbQuery {
        val answers = SkillTestAnswersTable.selectAll()
            .where { SkillTestAnswersTable.attemptId eq attemptId }
            .toList()

        answers.map { ans ->
            val question = SkillTestQuestionsTable.selectAll()
                .firstOrNull { it[SkillTestQuestionsTable.id] == ans[SkillTestAnswersTable.questionId] }
                ?: return@map null

            QuestionWithAnswerDto(
                id = question[SkillTestQuestionsTable.id].value.toString(),
                subject = question[SkillTestQuestionsTable.subject],
                questionText = question[SkillTestQuestionsTable.questionText],
                options = parseOptionsJson(question[SkillTestQuestionsTable.options]),
                correctAnswer = question[SkillTestQuestionsTable.correctAnswer],
                explanation = question[SkillTestQuestionsTable.explanation],
                difficulty = question[SkillTestQuestionsTable.difficulty],
            )
        }.filterNotNull()
    }

    // ── 5. Cleanup ────────────────────────────────────────────────────────

    /**
     * Purge old deactivated questions (called by the job scheduler).
     * Deletes questions that have been inactive for more than 14 days.
     */
    suspend fun purgeOldQuestions(): Int = dbQuery {
        val cutoff = Instant.now().minus(14, ChronoUnit.DAYS)
        val count = SkillTestQuestionsTable.selectAll()
            .where {
                (SkillTestQuestionsTable.isActive eq false) and
(SkillTestQuestionsTable.createdAt less cutoff)
            }
            .count().toInt()

        if (count > 0) {
            SkillTestQuestionsTable.deleteWhere {
                (SkillTestQuestionsTable.isActive eq false) and
(SkillTestQuestionsTable.createdAt less cutoff)
            }
            log.info("Purged {} old skill test questions", count)
        }

        count
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private fun parseOptionsJson(jsonStr: String): List<String> {
        return try {
            val arr = json.parseToJsonElement(jsonStr).jsonArray
            arr.map { it.jsonPrimitive.contentOrNull ?: "" }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun buildAnswerResult(
        isCorrect: Boolean,
        questionRow: ResultRow,
        attemptId: UUID,
    ): AnswerResultDto {
        val answersCount = SkillTestAnswersTable.selectAll()
            .where { SkillTestAnswersTable.attemptId eq attemptId }
            .count().toInt()
        val correctSoFar = SkillTestAnswersTable.selectAll()
            .where {
                (SkillTestAnswersTable.attemptId eq attemptId) and
                (SkillTestAnswersTable.isCorrect eq true)
            }
            .count().toInt()
        val attempt = SkillTestAttemptsTable.selectAll()
            .firstOrNull { it[SkillTestAttemptsTable.id] == attemptId }
        val total = attempt?.get(SkillTestAttemptsTable.totalQuestions) ?: 0

        return AnswerResultDto(
            questionId = questionRow[SkillTestQuestionsTable.id].value.toString(),
            isCorrect = isCorrect,
            correctAnswer = questionRow[SkillTestQuestionsTable.correctAnswer],
            explanation = questionRow[SkillTestQuestionsTable.explanation],
            currentCorrectCount = correctSoFar,
            questionsAnswered = answersCount,
            totalQuestions = total,
            attemptCompleted = false,
        )
    }

    // ── Internal model ────────────────────────────────────────────────────

    private data class GeneratedMcq(
        val questionText: String,
        val options: List<String>,
        val correctAnswer: String,
        val explanation: String,
        val difficulty: String,
    )
}
