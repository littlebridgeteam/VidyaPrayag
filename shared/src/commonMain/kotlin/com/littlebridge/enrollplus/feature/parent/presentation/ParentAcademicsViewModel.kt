package com.littlebridge.enrollplus.feature.parent.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.core.state.SelectedChildHolder
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardChildSummary
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentAttendanceData
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentDailySummaryData
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentMarksData
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentQuizDetailData
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentQuizDto
import com.littlebridge.enrollplus.feature.parent.domain.model.QuizLeaderboardData
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentSyllabusData
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentSyllabusV2Data
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentSyllabusV2Response
import com.littlebridge.enrollplus.feature.parent.domain.repository.ParentRepository
import com.littlebridge.enrollplus.feature.teacher.domain.model.QuizSubmitRequest
import com.littlebridge.enrollplus.feature.teacher.domain.model.QuizSubmitResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * RA-43 + RA-56: backs ParentAcademicsScreenV2's Attendance / Marks / Syllabus
 * tabs with REAL child-scoped data. Holds the children list + the selected child
 * (RA-56 child switcher) and the three academic datasets for that child. Loads
 * lazily per tab so we never fetch data the parent isn't looking at.
 */
data class ParentAcademicsState(
    val children: List<DashboardChildSummary> = emptyList(),
    val selectedChildId: String? = null,
    val childrenLoading: Boolean = false,
    val childrenError: String? = null,
    val childrenStale: Boolean = false,

    val attendance: ParentAttendanceData? = null,
    val attendanceLoading: Boolean = false,
    val attendanceError: String? = null,
    val attendanceStale: Boolean = false,

    val marks: ParentMarksData? = null,
    val marksLoading: Boolean = false,
    val marksError: String? = null,
    val marksStale: Boolean = false,

    val syllabus: ParentSyllabusData? = null,
    val syllabusLoading: Boolean = false,
    val syllabusError: String? = null,
    val syllabusStale: Boolean = false,

    // ── Agentic: syllabus V2 (typed curriculum_units with AI estimation) ──
    val syllabusV2: ParentSyllabusV2Data? = null,
    val syllabusV2Loading: Boolean = false,
    val syllabusV2Error: String? = null,
    val syllabusV2Stale: Boolean = false,

    // ── Agentic: daily summary ──
    val dailySummary: ParentDailySummaryData? = null,
    val dailySummaryLoading: Boolean = false,
    val dailySummaryError: String? = null,
    val dailySummaryStale: Boolean = false,

    // ── Agentic: quizzes ──
    val quizzes: List<ParentQuizDto> = emptyList(),
    val quizzesLoading: Boolean = false,
    val quizzesError: String? = null,
    val quizzesStale: Boolean = false,
    val quizDetail: ParentQuizDetailData? = null,
    val quizDetailLoading: Boolean = false,
    val quizDetailError: String? = null,
    val quizDetailStale: Boolean = false,
    val quizResult: QuizSubmitResponse? = null,
    val isSubmittingQuiz: Boolean = false,
    val quizSubmitError: String? = null,
    val leaderboard: QuizLeaderboardData? = null,
    val leaderboardLoading: Boolean = false,
    val leaderboardError: String? = null,
    val leaderboardStale: Boolean = false,
) {
    val selectedChild: DashboardChildSummary?
        get() = children.firstOrNull { it.id == selectedChildId } ?: children.firstOrNull()
}

class ParentAcademicsViewModel(
    private val repository: ParentRepository,
    private val preferenceRepository: PreferenceRepository,
    // RA-S05: shared selected-child source of truth across all parent tabs.
    private val selectedChildHolder: SelectedChildHolder,
) : ViewModel() {
    private val _state = MutableStateFlow(ParentAcademicsState())
    val state: StateFlow<ParentAcademicsState> = _state.asStateFlow()

    init {
        loadChildren()
        // RA-S05: when another tab switches the child, reflect it here and
        // refresh this tab's datasets for the newly-selected child.
        viewModelScope.launch {
            selectedChildHolder.selectedChildId.collect { shared ->
                if (shared != null && shared != _state.value.selectedChildId) {
                    applyExternalSelection(shared)
                }
            }
        }
    }

    /** RA-S05: adopt a child selection that originated on another tab. */
    private fun applyExternalSelection(childId: String) {
        _state.update {
            it.copy(
                selectedChildId = childId,
                attendance = null, marks = null, syllabus = null,
            )
        }
        loadAttendance(childId)
    }

    private suspend fun token(): String? = preferenceRepository.getUserToken().first()

    /** Load the children list (RA-56 switcher) from the dashboard endpoint. */
    fun loadChildren() {
        viewModelScope.launch {
            _state.update { it.copy(childrenLoading = true, childrenError = null) }
            val token = token() ?: run {
                _state.update { it.copy(childrenLoading = false, childrenError = "Not authenticated") }
                return@launch
            }
            when (val result = repository.getDashboard(token)) {
                is NetworkResult.Success -> {
                    val data = result.data.data
                    val children = data.children.ifEmpty { listOfNotNull(data.childSummary) }
                    _state.update {
                        // RA-S05: prefer the shared selection (set by whichever tab
                        // loaded first), then the local one, then the first child.
                        val sharedSel = selectedChildHolder.selectedChildId.value
                            ?.takeIf { id -> children.any { c -> c.id == id } }
                        val keep = sharedSel
                            ?: it.selectedChildId?.takeIf { id -> children.any { c -> c.id == id } }
                        it.copy(
                            childrenLoading = false,
                            children = children,
                            selectedChildId = keep ?: children.firstOrNull()?.id,
                            childrenStale = result.isStale,
                        )
                    }
                    // RA-S05: seed the shared holder so other tabs converge.
                    selectedChildHolder.selectIfUnset(_state.value.selectedChildId)
                    // Eagerly load the first tab the parent is most likely to open.
                    _state.value.selectedChild?.id?.let { loadAttendance(it) }
                }
                is NetworkResult.Error -> _state.update { it.copy(childrenLoading = false, childrenError = result.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(childrenLoading = false, childrenError = "Connection error") }
            }
        }
    }

    /** RA-56: switch the active child and refresh all loaded datasets for them. */
    fun selectChild(childId: String) {
        if (childId == _state.value.selectedChildId) return
        _state.update {
            it.copy(
                selectedChildId = childId,
                // Drop the previous child's data so stale rows never flash.
                attendance = null, marks = null, syllabus = null,
            )
        }
        // RA-S05: broadcast so Home/Fees/Leave follow this selection.
        selectedChildHolder.select(childId)
        loadAttendance(childId)
    }

    fun loadAttendance(childId: String? = null) {
        val resolvedChildId = childId ?: currentChildId() ?: return
        viewModelScope.launch {
            _state.update { it.copy(attendanceLoading = true, attendanceError = null) }
            val token = token() ?: run {
                _state.update { it.copy(attendanceLoading = false, attendanceError = "Not authenticated") }; return@launch
            }
            when (val r = repository.getChildAttendance(token, resolvedChildId)) {
                is NetworkResult.Success -> _state.update { it.copy(attendanceLoading = false, attendance = r.data.data, attendanceStale = r.isStale) }
                is NetworkResult.Error -> _state.update { it.copy(attendanceLoading = false, attendanceError = r.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(attendanceLoading = false, attendanceError = "Connection error") }
            }
        }
    }

    fun loadMarks(childId: String? = null) {
        val resolvedChildId = childId ?: currentChildId() ?: return
        viewModelScope.launch {
            _state.update { it.copy(marksLoading = true, marksError = null) }
            val token = token() ?: run {
                _state.update { it.copy(marksLoading = false, marksError = "Not authenticated") }; return@launch
            }
            when (val r = repository.getChildMarks(token, resolvedChildId)) {
                is NetworkResult.Success -> _state.update { it.copy(marksLoading = false, marks = r.data.data, marksStale = r.isStale) }
                is NetworkResult.Error -> _state.update { it.copy(marksLoading = false, marksError = r.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(marksLoading = false, marksError = "Connection error") }
            }
        }
    }

    fun loadSyllabus(childId: String? = null) {
        val resolvedChildId = childId ?: currentChildId() ?: return
        viewModelScope.launch {
            _state.update { it.copy(syllabusLoading = true, syllabusError = null) }
            val token = token() ?: run {
                _state.update { it.copy(syllabusLoading = false, syllabusError = "Not authenticated") }; return@launch
            }
            when (val r = repository.getChildSyllabus(token, resolvedChildId)) {
                is NetworkResult.Success -> _state.update { it.copy(syllabusLoading = false, syllabus = r.data.data, syllabusStale = r.isStale) }
                is NetworkResult.Error -> _state.update { it.copy(syllabusLoading = false, syllabusError = r.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(syllabusLoading = false, syllabusError = "Connection error") }
            }
        }
    }

    fun loadSyllabusV2(childId: String? = null) {
        val resolvedChildId = childId ?: currentChildId() ?: return
        viewModelScope.launch {
            _state.update { it.copy(syllabusV2Loading = true, syllabusV2Error = null) }
            val token = token() ?: run {
                _state.update { it.copy(syllabusV2Loading = false, syllabusV2Error = "Not authenticated") }; return@launch
            }
            when (val r = repository.getSyllabusV2(token, resolvedChildId)) {
                is NetworkResult.Success -> _state.update { it.copy(syllabusV2Loading = false, syllabusV2 = r.data.data, syllabusV2Stale = r.isStale) }
                is NetworkResult.Error -> _state.update { it.copy(syllabusV2Loading = false, syllabusV2Error = r.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(syllabusV2Loading = false, syllabusV2Error = "Connection error") }
            }
        }
    }

    private fun currentChildId(): String? = _state.value.selectedChild?.id

    // ── Agentic: Daily summary ─────────────────────────────────────────────

    fun loadDailySummary(childId: String? = null, date: String? = null) {
        val resolvedChildId = childId ?: currentChildId() ?: return
        viewModelScope.launch {
            _state.update { it.copy(dailySummaryLoading = true, dailySummaryError = null) }
            val token = token() ?: run {
                _state.update { it.copy(dailySummaryLoading = false, dailySummaryError = "Not authenticated") }; return@launch
            }
            when (val r = repository.getDailySummary(token, resolvedChildId, date)) {
                is NetworkResult.Success -> _state.update { it.copy(dailySummaryLoading = false, dailySummary = r.data.data, dailySummaryStale = r.isStale) }
                is NetworkResult.Error -> _state.update { it.copy(dailySummaryLoading = false, dailySummaryError = r.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(dailySummaryLoading = false, dailySummaryError = "Connection error") }
            }
        }
    }

    // ── Agentic: Quizzes ───────────────────────────────────────────────────

    fun loadQuizzes(childId: String? = null) {
        val resolvedChildId = childId ?: currentChildId() ?: return
        viewModelScope.launch {
            _state.update { it.copy(quizzesLoading = true, quizzesError = null) }
            val token = token() ?: run {
                _state.update { it.copy(quizzesLoading = false, quizzesError = "Not authenticated") }; return@launch
            }
            when (val r = repository.getQuizList(token, resolvedChildId)) {
                is NetworkResult.Success -> _state.update { it.copy(quizzesLoading = false, quizzes = r.data.data.quizzes, quizzesStale = r.isStale) }
                is NetworkResult.Error -> _state.update { it.copy(quizzesLoading = false, quizzesError = r.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(quizzesLoading = false, quizzesError = "Connection error") }
            }
        }
    }

    fun loadQuizDetail(quizId: String) {
        viewModelScope.launch {
            _state.update { it.copy(quizDetailLoading = true, quizDetailError = null, quizDetail = null, quizResult = null) }
            val token = token() ?: run {
                _state.update { it.copy(quizDetailLoading = false, quizDetailError = "Not authenticated") }; return@launch
            }
            when (val r = repository.getQuizDetail(token, quizId)) {
                is NetworkResult.Success -> _state.update { it.copy(quizDetailLoading = false, quizDetail = r.data.data, quizDetailStale = r.isStale) }
                is NetworkResult.Error -> _state.update { it.copy(quizDetailLoading = false, quizDetailError = r.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(quizDetailLoading = false, quizDetailError = "Connection error") }
            }
        }
    }

    fun submitQuiz(quizId: String, answers: List<Pair<String, Int>>, textAnswers: Map<String, String> = emptyMap()) {
        viewModelScope.launch {
            _state.update { it.copy(isSubmittingQuiz = true, quizSubmitError = null) }
            val token = token() ?: run {
                _state.update { it.copy(isSubmittingQuiz = false, quizSubmitError = "Not authenticated") }; return@launch
            }
            val request = QuizSubmitRequest(
                quizId = quizId,
                answers = answers.map { (qid, idx) ->
                    com.littlebridge.enrollplus.feature.teacher.domain.model.QuizAnswerDto(
                        questionId = qid,
                        selectedIndex = idx,
                        answerText = textAnswers[qid],
                    )
                },
            )
            when (val r = repository.submitQuiz(token, request)) {
                is NetworkResult.Success -> _state.update { it.copy(isSubmittingQuiz = false, quizResult = r.data) }
                is NetworkResult.Error -> _state.update { it.copy(isSubmittingQuiz = false, quizSubmitError = r.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(isSubmittingQuiz = false, quizSubmitError = "Connection error") }
            }
        }
    }

    fun loadLeaderboard(quizId: String) {
        val resolvedChildId = currentChildId() ?: return
        viewModelScope.launch {
            _state.update { it.copy(leaderboardLoading = true, leaderboardError = null, leaderboard = null) }
            val token = token() ?: run {
                _state.update { it.copy(leaderboardLoading = false, leaderboardError = "Not authenticated") }; return@launch
            }
            when (val r = repository.getQuizLeaderboard(token, resolvedChildId, quizId)) {
                is NetworkResult.Success -> _state.update { it.copy(leaderboardLoading = false, leaderboard = r.data.data, leaderboardStale = r.isStale) }
                is NetworkResult.Error -> _state.update { it.copy(leaderboardLoading = false, leaderboardError = r.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(leaderboardLoading = false, leaderboardError = "Connection error") }
            }
        }
    }

    fun loadQuizResult(quizId: String) {
        val resolvedChildId = currentChildId() ?: return
        viewModelScope.launch {
            _state.update { it.copy(quizDetailLoading = true, quizDetailError = null, quizResult = null, leaderboard = null) }
            val token = token() ?: run {
                _state.update { it.copy(quizDetailLoading = false, quizDetailError = "Not authenticated") }; return@launch
            }
            when (val r = repository.getQuizResult(token, resolvedChildId, quizId)) {
                is NetworkResult.Success -> _state.update { it.copy(quizDetailLoading = false, quizResult = r.data) }
                is NetworkResult.Error -> _state.update { it.copy(quizDetailLoading = false, quizDetailError = r.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(quizDetailLoading = false, quizDetailError = "Connection error") }
            }
        }
    }

    fun clearQuizResult() = _state.update { it.copy(quizResult = null, quizDetail = null, leaderboard = null) }
}
