package com.littlebridge.enrollplus.feature.parent.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ─────────────────────────────────────────────────────────────────────────────
// Skill Test System — AI-generated weekly MCQ tests for children.
// Mirrors server DTOs in SkillTestService.kt + SkillTestRouting.kt.
// These are SEPARATE from the teacher-generated quiz system (ParentQuizDto etc).
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class SkillTestEligibilityResponse(
    val success: Boolean = true,
    val data: SkillTestEligibilityData = SkillTestEligibilityData(),
)

@Serializable
data class SkillTestEligibilityData(
    val eligible: Boolean = false,
    val reason: String = "",
    @SerialName("next_eligible_at") val nextEligibleAt: String? = null,
    @SerialName("best_score") val bestScore: SkillTestBestScoreData? = null,
    @SerialName("has_questions") val hasQuestions: Boolean = false,
)

@Serializable
data class SkillTestBestScoreData(
    @SerialName("best_score") val bestScore: Int = 0,
    @SerialName("attempts_count") val attemptsCount: Int = 0,
    @SerialName("badge_earned") val badgeEarned: Boolean = false,
    @SerialName("last_attempt_at") val lastAttemptAt: String? = null,
    @SerialName("next_eligible_at") val nextEligibleAt: String? = null,
)

@Serializable
data class SkillTestStartResponse(
    val success: Boolean = true,
    val data: SkillTestStartData = SkillTestStartData(),
)

@Serializable
data class SkillTestStartData(
    @SerialName("attempt_id") val attemptId: String = "",
    val questions: List<SkillTestQuestionDto> = emptyList(),
)

@Serializable
data class SkillTestQuestionDto(
    val id: String,
    val subject: String = "",
    @SerialName("question_text") val questionText: String = "",
    val options: List<String> = emptyList(),
    val difficulty: String = "medium",
)

@Serializable
data class SkillTestAnswerRequest(
    @SerialName("question_id") val questionId: String,
    @SerialName("selected_answer") val selectedAnswer: String,
)

@Serializable
data class SkillTestAnswerResponse(
    val success: Boolean = true,
    val data: SkillTestAnswerResultData = SkillTestAnswerResultData(),
)

@Serializable
data class SkillTestAnswerResultData(
    @SerialName("question_id") val questionId: String = "",
    @SerialName("is_correct") val isCorrect: Boolean = false,
    @SerialName("correct_answer") val correctAnswer: String = "",
    val explanation: String = "",
    @SerialName("current_correct_count") val currentCorrectCount: Int = 0,
    @SerialName("questions_answered") val questionsAnswered: Int = 0,
    @SerialName("total_questions") val totalQuestions: Int = 0,
    @SerialName("attempt_completed") val attemptCompleted: Boolean = false,
    @SerialName("final_score") val finalScore: Int? = null,
    @SerialName("badge_earned") val badgeEarned: Boolean? = null,
)

@Serializable
data class SkillTestBestScoreResponse(
    val success: Boolean = true,
    val data: SkillTestBestScoreData = SkillTestBestScoreData(),
)

@Serializable
data class SkillTestHistoryResponse(
    val success: Boolean = true,
    val data: List<SkillTestAttemptDto> = emptyList(),
)

@Serializable
data class SkillTestAttemptDto(
    val id: String = "",
    val status: String = "",
    @SerialName("total_questions") val totalQuestions: Int = 0,
    @SerialName("correct_count") val correctCount: Int = 0,
    @SerialName("score_percentage") val scorePercentage: Int = 0,
    @SerialName("started_at") val startedAt: String = "",
    @SerialName("completed_at") val completedAt: String? = null,
    @SerialName("next_eligible_at") val nextEligibleAt: String? = null,
)

@Serializable
data class SkillTestReviewResponse(
    val success: Boolean = true,
    val data: List<SkillTestReviewQuestionDto> = emptyList(),
)

@Serializable
data class SkillTestReviewQuestionDto(
    val id: String = "",
    val subject: String = "",
    @SerialName("question_text") val questionText: String = "",
    val options: List<String> = emptyList(),
    @SerialName("correct_answer") val correctAnswer: String = "",
    val explanation: String = "",
    val difficulty: String = "medium",
)
