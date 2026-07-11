package com.littlebridge.enrollplus.feature.parent.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.core.state.SelectedChildHolder
import com.littlebridge.enrollplus.feature.parent.domain.model.*
import com.littlebridge.enrollplus.feature.parent.domain.repository.ParentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * SkillTestViewModel — backs the Skill Test card in the Academics Overview tab.
 *
 * SEPARATE from the teacher-generated quiz system (ParentAcademicsViewModel's
 * quiz fields). This ViewModel handles only the AI-generated weekly MCQ skill
 * test: eligibility check, start attempt, instant per-question evaluation,
 * best score tracking, and badge status.
 *
 * Flow:
 *   1. loadEligibility() — check if child can take a test
 *   2. startTest() — get questions (no correct answers sent)
 *   3. submitAnswer() — instant eval, auto-completes on last question
 *   4. Result screen shows score + badge earned
 */
data class SkillTestState(
    val eligible: Boolean = false,
    val eligibilityReason: String = "",
    val nextEligibleAt: String? = null,
    val bestScore: SkillTestBestScoreData? = null,
    val hasQuestions: Boolean = false,

    val isLoadingEligibility: Boolean = false,
    val eligibilityError: String? = null,

    // Active test session
    val attemptId: String? = null,
    val questions: List<SkillTestQuestionDto> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val correctCount: Int = 0,
    val answeredCount: Int = 0,
    val isStartingTest: Boolean = false,
    val startError: String? = null,

    // Per-question answer tracking
    val lastAnswerResult: SkillTestAnswerResultData? = null,
    val lastSelectedAnswer: String? = null,
    val answeredQuestions: Set<String> = emptySet(),
    val isSubmittingAnswer: Boolean = false,
    val answerError: String? = null,

    // Test completed
    val isCompleted: Boolean = false,
    val finalScore: Int? = null,
    val badgeEarned: Boolean? = null,

    // History
    val history: List<SkillTestAttemptDto> = emptyList(),
    val historyLoading: Boolean = false,
    val historyError: String? = null,
) {
    val totalQuestions: Int get() = questions.size
    val currentQuestion: SkillTestQuestionDto? get() = questions.getOrNull(currentQuestionIndex)
    val progressPct: Float get() = if (totalQuestions > 0) answeredCount.toFloat() / totalQuestions else 0f
}

class SkillTestViewModel(
    private val repository: ParentRepository,
    private val preferenceRepository: PreferenceRepository,
    private val selectedChildHolder: SelectedChildHolder,
) : ViewModel() {
    private val _state = MutableStateFlow(SkillTestState())
    val state: StateFlow<SkillTestState> = _state.asStateFlow()

    private suspend fun token(): String? = preferenceRepository.getUserToken().first()

    private fun currentChildId(): String? = selectedChildHolder.selectedChildId.value

    /**
     * Check if the child is eligible to take a skill test.
     * Call this when the Overview tab loads.
     */
    fun loadEligibility(childId: String? = null) {
        val resolvedChildId = childId ?: selectedChildHolder.selectedChildId.value ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoadingEligibility = true, eligibilityError = null) }
            val token = token() ?: run {
                _state.update { it.copy(isLoadingEligibility = false, eligibilityError = "Not authenticated") }
                return@launch
            }
            when (val r = repository.getSkillTestEligibility(token, resolvedChildId)) {
                is NetworkResult.Success -> {
                    val data = r.data.data
                    _state.update {
                        it.copy(
                            isLoadingEligibility = false,
                            eligible = data.eligible,
                            eligibilityReason = data.reason,
                            nextEligibleAt = data.nextEligibleAt,
                            bestScore = data.bestScore,
                            hasQuestions = data.hasQuestions,
                        )
                    }
                }
                is NetworkResult.Error -> _state.update {
                    it.copy(isLoadingEligibility = false, eligibilityError = r.message)
                }
                is NetworkResult.ConnectionError -> _state.update {
                    it.copy(isLoadingEligibility = false, eligibilityError = "Connection error")
                }
            }
        }
    }

    /**
     * Start a new skill test attempt. Returns the questions for the UI to render.
     */
    fun startTest(childId: String? = null) {
        val resolvedChildId = childId ?: selectedChildHolder.selectedChildId.value ?: return
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isStartingTest = true,
                    startError = null,
                    attemptId = null,
                    questions = emptyList(),
                    currentQuestionIndex = 0,
                    correctCount = 0,
                    answeredCount = 0,
                    answeredQuestions = emptySet(),
                    isCompleted = false,
                    finalScore = null,
                    badgeEarned = null,
                    lastAnswerResult = null,
                )
            }
            val token = token() ?: run {
                _state.update { it.copy(isStartingTest = false, startError = "Not authenticated") }
                return@launch
            }
            when (val r = repository.startSkillTest(token, resolvedChildId)) {
                is NetworkResult.Success -> {
                    val data = r.data.data
                    if (data.attemptId.isBlank() || data.questions.isEmpty()) {
                        _state.update {
                            it.copy(isStartingTest = false, startError = "No questions available")
                        }
                        return@launch
                    }
                    _state.update {
                        it.copy(
                            isStartingTest = false,
                            attemptId = data.attemptId,
                            questions = data.questions,
                            currentQuestionIndex = 0,
                        )
                    }
                }
                is NetworkResult.Error -> _state.update {
                    it.copy(isStartingTest = false, startError = r.message)
                }
                is NetworkResult.ConnectionError -> _state.update {
                    it.copy(isStartingTest = false, startError = "Connection error")
                }
            }
        }
    }

    /**
     * Submit a single answer. Instantly evaluates and advances to the next question.
     * On the last question, auto-completes the attempt with final score + badge.
     */
    fun submitAnswer(questionId: String, selectedAnswer: String) {
        val attemptId = _state.value.attemptId ?: return
        if (questionId in _state.value.answeredQuestions) return

        viewModelScope.launch {
            _state.update { it.copy(isSubmittingAnswer = true, answerError = null) }
            val token = token() ?: run {
                _state.update { it.copy(isSubmittingAnswer = false, answerError = "Not authenticated") }
                return@launch
            }
            val request = SkillTestAnswerRequest(
                questionId = questionId,
                selectedAnswer = selectedAnswer,
            )
            when (val r = repository.submitSkillTestAnswer(token, attemptId, request)) {
                is NetworkResult.Success -> {
                    val data = r.data.data
                    val newAnswered = _state.value.answeredQuestions + questionId
                    val nextIndex = _state.value.currentQuestionIndex + 1
                    val isLast = data.attemptCompleted

                    _state.update {
                        it.copy(
                            isSubmittingAnswer = false,
                            lastAnswerResult = data,
                            lastSelectedAnswer = selectedAnswer,
                            answeredQuestions = newAnswered,
                            answeredCount = data.questionsAnswered,
                            correctCount = data.currentCorrectCount,
                            currentQuestionIndex = if (isLast) it.currentQuestionIndex else nextIndex.coerceAtMost(it.questions.size - 1),
                            isCompleted = isLast,
                            finalScore = data.finalScore,
                            badgeEarned = data.badgeEarned,
                        )
                    }

                    // If completed, refresh eligibility + best score
                    if (isLast) {
                        loadEligibility()
                    }
                }
                is NetworkResult.Error -> _state.update {
                    it.copy(isSubmittingAnswer = false, answerError = r.message)
                }
                is NetworkResult.ConnectionError -> _state.update {
                    it.copy(isSubmittingAnswer = false, answerError = "Connection error")
                }
            }
        }
    }

    /**
     * Navigate to the next question manually (after viewing the answer result).
     */
    fun nextQuestion() {
        val current = _state.value.currentQuestionIndex
        val max = _state.value.questions.size - 1
        if (current < max) {
            _state.update {
                it.copy(currentQuestionIndex = current + 1, lastAnswerResult = null, lastSelectedAnswer = null)
            }
        }
    }

    /**
     * Navigate to the previous question.
     */
    fun previousQuestion() {
        val current = _state.value.currentQuestionIndex
        if (current > 0) {
            _state.update {
                it.copy(currentQuestionIndex = current - 1, lastAnswerResult = null)
            }
        }
    }

    /**
     * Reset the test session back to the eligibility view.
     */
    fun resetTest() {
        _state.update {
            it.copy(
                attemptId = null,
                questions = emptyList(),
                currentQuestionIndex = 0,
                correctCount = 0,
                answeredCount = 0,
                answeredQuestions = emptySet(),
                isCompleted = false,
                finalScore = null,
                badgeEarned = null,
                lastAnswerResult = null,
                startError = null,
                answerError = null,
            )
        }
        loadEligibility()
    }

    /**
     * Load attempt history for the child.
     */
    fun loadHistory(childId: String? = null) {
        val resolvedChildId = childId ?: selectedChildHolder.selectedChildId.value ?: return
        viewModelScope.launch {
            _state.update { it.copy(historyLoading = true, historyError = null) }
            val token = token() ?: run {
                _state.update { it.copy(historyLoading = false, historyError = "Not authenticated") }
                return@launch
            }
            when (val r = repository.getSkillTestHistory(token, resolvedChildId)) {
                is NetworkResult.Success -> _state.update {
                    it.copy(historyLoading = false, history = r.data.data)
                }
                is NetworkResult.Error -> _state.update {
                    it.copy(historyLoading = false, historyError = r.message)
                }
                is NetworkResult.ConnectionError -> _state.update {
                    it.copy(historyLoading = false, historyError = "Connection error")
                }
            }
        }
    }
}
