package com.littlebridge.enrollplus.feature.teacher.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.teacher.domain.model.AttendanceAnalyticsDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.CreateSyllabusUnitRequest
import com.littlebridge.enrollplus.feature.teacher.domain.model.QuizDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.QuizGenerateRequest
import com.littlebridge.enrollplus.feature.teacher.domain.model.QuizUpdateQuestionRequest
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherQuizLeaderboardData
import com.littlebridge.enrollplus.feature.teacher.domain.model.SylAutoFillChapter
import com.littlebridge.enrollplus.feature.teacher.domain.model.SylAutoFillRequest
import com.littlebridge.enrollplus.feature.teacher.domain.model.SylAutoFillResponse
import com.littlebridge.enrollplus.feature.teacher.domain.model.SylApproveRequest
import com.littlebridge.enrollplus.feature.teacher.domain.model.SylDailyLogRequest
import com.littlebridge.enrollplus.feature.teacher.domain.model.SylDailyLogDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.SylParseConfirmRequest
import com.littlebridge.enrollplus.feature.teacher.domain.model.SylParseRequest
import com.littlebridge.enrollplus.feature.teacher.domain.model.SylParsedUnit
import com.littlebridge.enrollplus.feature.teacher.domain.model.SylPaceWarning
import com.littlebridge.enrollplus.feature.teacher.domain.model.SylPopupPrefsRequest
import com.littlebridge.enrollplus.feature.teacher.domain.model.SyllabusNodeDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.ToggleSyllabusProgressRequest
import com.littlebridge.enrollplus.feature.teacher.domain.model.UpdateSyllabusUnitRequest
import com.littlebridge.enrollplus.feature.teacher.domain.repository.TeacherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import com.littlebridge.enrollplus.util.todayIso
import kotlinx.coroutines.launch

/**
 * T-403 — the Planner › Syllabus state holder, rebuilt from scratch (DELETE-don't-patch)
 * on the typed, assignment-scoped plane (T-402). The core gesture is a SINGLE TAP on a unit
 * row → optimistic coverage toggle (no form, no save button — Doc 08 §2/F-SYL-1). Hierarchy
 * (chapter ▸ topic) comes pre-flattened from the server (each node carries depth/parentId).
 *
 * Reached PRE-SCOPED by [assignmentId] (X-1) — never a free-text class/subject (contrast the
 * legacy getSyllabus(classId, subject)). The legacy UpdateSyllabusRequest toggle is gone;
 * this uses the typed ToggleSyllabusProgressRequest with a server-stamped covered_on.
 */

/** A flattened syllabus node (chapter or topic) with this section's coverage state. */
data class SyllabusUnit(
    val id: String,
    val parentId: String?,
    val title: String,
    val depth: Int,
    val isChapter: Boolean,
    val isCovered: Boolean,
    val coveredOn: String?,
    val note: String?,
    val approvalStatus: String = "APPROVED",
)

data class TeacherSyllabusState(
    val assignmentId: String = "",
    val className: String = "",
    val section: String = "",
    val subject: String = "",
    val units: List<SyllabusUnit> = emptyList(),
    val coveredCount: Int = 0,
    val totalCount: Int = 0,
    val isLoading: Boolean = false,
    // The unit currently being persisted (drives a row spinner / disables re-tap).
    val updatingUnitId: String? = null,
    val error: String? = null,
    // Edit mode surfaces the rare, deliberate affordances (add / rename / delete) behind a toggle.
    val isEditing: Boolean = false,
    // Add-unit composer: null = closed; "" = a chapter, else a topic under that chapter id.
    val addingUnderParentId: String? = null,
    val addTitle: String = "",
    val isAdding: Boolean = false,
    val addError: String? = null,
    // ── Agentic: parse syllabus ──
    val isParsing: Boolean = false,
    val parsedUnits: List<SylParsedUnit> = emptyList(),
    val parseError: String? = null,
    val showParsePreview: Boolean = false,
    val parseRawText: String = "",
    // ── Agentic: daily log popup ──
    val showDailyLogPopup: Boolean = false,
    val dailyLogAssignmentId: String = "",
    val dailyLogClassName: String = "",
    val dailyLogSubject: String = "",
    val isSavingDailyLog: Boolean = false,
    val dailyLogCoveragePct: Int = 0,
    val dailyLogSelectedTopicIds: Set<String> = emptySet(),
    val dailyLogSummary: String = "",
    val dailyLogError: String? = null,
    val dailyLogs: List<SylDailyLogDto> = emptyList(),
    // ── Agentic: quiz generation ──
    val quizzes: List<QuizDto> = emptyList(),
    val isGeneratingQuiz: Boolean = false,
    val quizError: String? = null,
    val showQuizSheet: Boolean = false,
    val quizUnitId: String = "",
    val quizSelectedUnitIds: Set<String> = emptySet(),
    val quizNumQuestions: Int = 5,
    val quizDifficulty: String = "MEDIUM",
    val quizQuestionTypes: Set<String> = setOf("MCQ"),
    // ── Quiz preview (after AI generation, before publishing) ──
    val showQuizPreview: Boolean = false,
    val generatedQuiz: QuizDto? = null,
    val editingQuestionId: String? = null,
    val isRegenerating: Boolean = false,
    val isPublishingQuiz: Boolean = false,
    val quizPreviewError: String? = null,
    // ── Quiz add question ──
    val showAddQuestion: Boolean = false,
    // ── Quiz leaderboard ──
    val showLeaderboard: Boolean = false,
    val leaderboardQuizId: String = "",
    val leaderboard: TeacherQuizLeaderboardData? = null,
    val leaderboardLoading: Boolean = false,
    val leaderboardError: String? = null,
    // ── Compare attendance toggle ──
    val compareAttendance: Boolean = false,
    val attendanceAnalytics: AttendanceAnalyticsDto? = null,
    val attendanceAnalyticsLoading: Boolean = false,
    // ── Agentic: NCERT auto-fill ──
    val isAutoFilling: Boolean = false,
    val autoFillChapters: List<SylAutoFillChapter> = emptyList(),
    val autoFillError: String? = null,
    val showAutoFillPreview: Boolean = false,
    val autoFillSource: String = "",
    // ── Agentic: pace warning ──
    val paceWarning: SylPaceWarning? = null,
    val isLoadingPace: Boolean = false,
    // ── Agentic: approval ──
    val isApproving: Boolean = false,
    val approveError: String? = null,
    val hasDraftUnits: Boolean = false,
    val isStale: Boolean = false,
    val isOffline: Boolean = false,
) {
    /** 0..1; 0 when nothing to cover yet (honest, never NaN). */
    val progress: Float get() = if (totalCount == 0) 0f else coveredCount.toFloat() / totalCount
    val hasUnits: Boolean get() = units.isNotEmpty()
    /** Chapters only (for the "add topic under…" parent choices). */
    val chapters: List<SyllabusUnit> get() = units.filter { it.isChapter }
    /** Topics (depth >= 1) — selectable for daily log. */
    val topics: List<SyllabusUnit> get() = units.filter { !it.isChapter }
    /** Draft units (pending teacher approval). */
    val draftUnits: List<SyllabusUnit> get() = units.filter { it.approvalStatus == "DRAFT" }
    /** Approved units (visible to parents). */
    val approvedUnits: List<SyllabusUnit> get() = units.filter { it.approvalStatus == "APPROVED" }
}

class TeacherSyllabusViewModel(
    private val repository: TeacherRepository,
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(TeacherSyllabusState())
    val state: StateFlow<TeacherSyllabusState> = _state.asStateFlow()

    /** Load the hierarchical units + this assignment's coverage. The screen supplies the assignmentId. */
    fun load(assignmentId: String) {
        if (assignmentId.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(assignmentId = assignmentId, isLoading = true, error = null) }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(isLoading = false, error = "Not authenticated") }
                return@launch
            }
            when (val result = repository.loadSyllabus(token, assignmentId)) {
                is NetworkResult.Success -> {
                    val d = result.data.data
                    val uiUnits = d.units.map { u -> u.toUi() }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            className = d.className,
                            section = d.section,
                            subject = d.subject,
                            units = uiUnits,
                            coveredCount = d.coveredCount,
                            totalCount = d.totalCount,
                            hasDraftUnits = uiUnits.any { it.approvalStatus == "DRAFT" },
                            isStale = result.isStale,
                            isOffline = result.isOffline,
                        )
                    }
                }
                is NetworkResult.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(isLoading = false, error = "Connection error") }
            }
        }
    }

    fun retry() = load(_state.value.assignmentId)

    /**
     * The one-tap toggle. Optimistically flips local coverage + the covered count, then persists
     * via the typed PATCH /progress (server stamps covered_on=today, covered_by=me); reverts on
     * failure. Idempotent — a re-tap un-covers (clears covered_on).
     */
    fun toggleUnit(unitId: String) {
        val s0 = _state.value
        if (s0.assignmentId.isBlank()) return
        val target = s0.units.firstOrNull { it.id == unitId } ?: return
        val newCovered = !target.isCovered

        viewModelScope.launch {
            val before = s0.units
            val beforeCovered = s0.coveredCount

            // Optimistic local flip (covered_on shown once the server confirms).
            _state.update { s ->
                s.copy(
                    updatingUnitId = unitId,
                    error = null,
                    units = s.units.map { if (it.id == unitId) it.copy(isCovered = newCovered) else it },
                    coveredCount = (s.coveredCount + if (newCovered) 1 else -1).coerceIn(0, s.totalCount),
                )
            }

            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(updatingUnitId = null, units = before, coveredCount = beforeCovered, error = "Not authenticated") }
                return@launch
            }
            val request = ToggleSyllabusProgressRequest(
                assignmentId = s0.assignmentId,
                unitId = unitId,
                isCovered = newCovered,
            )
            when (val result = repository.toggleSyllabusProgress(token, request)) {
                is NetworkResult.Success -> {
                    // Reload full syllabus to reflect parent-child propagation
                    // (marking a chapter covers all its topics; unmarking a topic
                    // unmarks its parent chapter).
                    val reloadResult = repository.loadSyllabus(token, s0.assignmentId)
                    _state.update { s ->
                        when (reloadResult) {
                            is NetworkResult.Success -> {
                                val d = reloadResult.data.data
                                val uiUnits = d.units.map { u -> u.toUi() }
                                s.copy(
                                    updatingUnitId = null,
                                    units = uiUnits,
                                    coveredCount = d.coveredCount,
                                    totalCount = d.totalCount,
                                    hasDraftUnits = uiUnits.any { it.approvalStatus == "DRAFT" },
                                )
                            }
                            else -> {
                                // Fallback: update just the toggled node
                                val node = result.data.data?.toUi()
                                s.copy(
                                    updatingUnitId = null,
                                    units = if (node != null) s.units.map { if (it.id == unitId) node else it } else s.units,
                                )
                            }
                        }
                    }
                }
                is NetworkResult.Error ->
                    _state.update { it.copy(updatingUnitId = null, units = before, coveredCount = beforeCovered, error = result.message) }
                is NetworkResult.ConnectionError ->
                    _state.update { it.copy(updatingUnitId = null, units = before, coveredCount = beforeCovered, error = "Connection error") }
            }
        }
    }

    // ── Edit mode (deliberate, behind a toggle) ──────────────────────────────

    fun toggleEditing() = _state.update { it.copy(isEditing = !it.isEditing, addingUnderParentId = null, addError = null) }

    /** Open the add composer. parentId "" / null → a chapter; a chapter id → a topic under it. */
    fun openAdd(parentId: String?) = _state.update {
        it.copy(addingUnderParentId = parentId ?: "", addTitle = "", addError = null)
    }

    fun closeAdd() = _state.update { it.copy(addingUnderParentId = null, addTitle = "", addError = null) }

    fun setAddTitle(value: String) = _state.update { it.copy(addTitle = value, addError = null) }

    /** Create a unit through the typed POST. On success reloads so positions/hierarchy stay authoritative. */
    fun submitAdd() {
        val s0 = _state.value
        if (s0.assignmentId.isBlank()) return
        val title = s0.addTitle.trim()
        if (title.isBlank()) {
            _state.update { it.copy(addError = "Give the unit a title") }
            return
        }
        val parentRaw = s0.addingUnderParentId
        val parentId = parentRaw?.takeIf { it.isNotBlank() }

        viewModelScope.launch {
            _state.update { it.copy(isAdding = true, addError = null) }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(isAdding = false, addError = "Not authenticated") }
                return@launch
            }
            val request = CreateSyllabusUnitRequest(
                assignmentId = s0.assignmentId,
                title = title,
                parentId = parentId,
            )
            when (val result = repository.createSyllabusUnit(token, request)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isAdding = false, addingUnderParentId = null, addTitle = "") }
                    load(s0.assignmentId)   // reload for authoritative ordering/hierarchy
                }
                is NetworkResult.Error -> _state.update { it.copy(isAdding = false, addError = result.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(isAdding = false, addError = "Connection error") }
            }
        }
    }

    /** Rename a unit (edit mode). Optimistic; reverts on failure. */
    fun renameUnit(unitId: String, newTitle: String) {
        val s0 = _state.value
        val title = newTitle.trim()
        if (s0.assignmentId.isBlank() || title.isBlank()) return
        val before = s0.units

        viewModelScope.launch {
            _state.update { s ->
                s.copy(units = s.units.map { if (it.id == unitId) it.copy(title = title) else it })
            }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(units = before, error = "Not authenticated") }
                return@launch
            }
            val result = repository.updateSyllabusUnit(
                token, s0.assignmentId, unitId, UpdateSyllabusUnitRequest(title = title),
            )
            when (result) {
                is NetworkResult.Success -> Unit
                is NetworkResult.Error -> _state.update { it.copy(units = before, error = result.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(units = before, error = "Connection error") }
            }
        }
    }

    // ── Agentic: Parse syllabus (AI text/image → structured units) ────────────

    fun setParseRawText(value: String) = _state.update { it.copy(parseRawText = value) }

    fun openParseSheet() = _state.update { it.copy(showParsePreview = true, parseRawText = "", parsedUnits = emptyList(), parseError = null) }

    fun closeParseSheet() = _state.update { it.copy(showParsePreview = false, parsedUnits = emptyList(), parseError = null, parseRawText = "") }

    fun parseSyllabus() {
        val s0 = _state.value
        if (s0.assignmentId.isBlank()) return
        val rawText = s0.parseRawText.trim()
        if (rawText.isBlank()) {
            _state.update { it.copy(parseError = "Paste some syllabus text first") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isParsing = true, parseError = null) }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(isParsing = false, parseError = "Not authenticated") }
                return@launch
            }
            val request = SylParseRequest(assignmentId = s0.assignmentId, sourceType = "TEXT", rawText = rawText)
            when (val result = repository.parseSyllabus(token, request)) {
                is NetworkResult.Success -> {
                    val units = result.data.data.units
                    _state.update { it.copy(isParsing = false, parsedUnits = units) }
                }
                is NetworkResult.Error -> _state.update { it.copy(isParsing = false, parseError = result.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(isParsing = false, parseError = "Connection error") }
            }
        }
    }

    fun confirmParsedSyllabus() {
        val s0 = _state.value
        if (s0.assignmentId.isBlank() || s0.parsedUnits.isEmpty()) return
        viewModelScope.launch {
            _state.update { it.copy(isParsing = true, parseError = null) }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(isParsing = false, parseError = "Not authenticated") }
                return@launch
            }
            val request = SylParseConfirmRequest(assignmentId = s0.assignmentId, units = s0.parsedUnits)
            when (val result = repository.confirmParsedSyllabus(token, request)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isParsing = false, showParsePreview = false, parsedUnits = emptyList(), parseRawText = "") }
                    load(s0.assignmentId)
                }
                is NetworkResult.Error -> _state.update { it.copy(isParsing = false, parseError = result.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(isParsing = false, parseError = "Connection error") }
            }
        }
    }

    // ── Agentic: Delete unit (cascade soft-delete) ────────────────────────────

    fun deleteUnit(unitId: String) {
        val s0 = _state.value
        if (s0.assignmentId.isBlank()) return
        viewModelScope.launch {
            val before = s0.units
            val beforeCovered = s0.coveredCount
            val beforeTotal = s0.totalCount
            // Optimistic: remove the unit and its children from local state
            val toRemove = mutableSetOf(unitId)
            s0.units.filter { it.parentId == unitId }.forEach { toRemove.add(it.id) }
            s0.units.filter { it.parentId in toRemove }.forEach { toRemove.add(it.id) }
            _state.update { s ->
                val newUnits = s.units.filter { it.id !in toRemove }
                val newCovered = newUnits.count { it.isCovered }
                s.copy(units = newUnits, coveredCount = newCovered, totalCount = newUnits.size)
            }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(units = before, coveredCount = beforeCovered, totalCount = beforeTotal, error = "Not authenticated") }
                return@launch
            }
            when (val result = repository.deleteSyllabusUnit(token, s0.assignmentId, unitId)) {
                is NetworkResult.Success -> Unit
                is NetworkResult.Error -> _state.update { it.copy(units = before, coveredCount = beforeCovered, totalCount = beforeTotal, error = result.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(units = before, coveredCount = beforeCovered, totalCount = beforeTotal, error = "Connection error") }
            }
        }
    }

    // ── Agentic: Daily class log popup ────────────────────────────────────────

    fun openDailyLogPopup() {
        val s0 = _state.value
        _state.update {
            it.copy(
                showDailyLogPopup = true,
                dailyLogAssignmentId = s0.assignmentId,
                dailyLogClassName = s0.className,
                dailyLogSubject = s0.subject,
                dailyLogSelectedTopicIds = emptySet(),
                dailyLogCoveragePct = 0,
                dailyLogSummary = "",
                dailyLogError = null,
            )
        }
    }

    fun closeDailyLogPopup() = _state.update { it.copy(showDailyLogPopup = false, dailyLogError = null) }

    fun toggleDailyLogTopic(topicId: String) = _state.update { s ->
        val newSet = if (topicId in s.dailyLogSelectedTopicIds) s.dailyLogSelectedTopicIds - topicId else s.dailyLogSelectedTopicIds + topicId
        s.copy(dailyLogSelectedTopicIds = newSet)
    }

    fun setDailyLogCoveragePct(value: Int) = _state.update { it.copy(dailyLogCoveragePct = value.coerceIn(0, 100)) }

    fun setDailyLogSummary(value: String) = _state.update { it.copy(dailyLogSummary = value) }

    fun saveDailyLog() {
        val s0 = _state.value
        if (s0.dailyLogAssignmentId.isBlank()) return
        val today = todayIso()
        viewModelScope.launch {
            _state.update { it.copy(isSavingDailyLog = true, dailyLogError = null) }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(isSavingDailyLog = false, dailyLogError = "Not authenticated") }
                return@launch
            }
            val request = SylDailyLogRequest(
                assignmentId = s0.dailyLogAssignmentId,
                date = today,
                topicIds = s0.dailyLogSelectedTopicIds.toList(),
                summaryText = s0.dailyLogSummary,
                coveragePct = s0.dailyLogCoveragePct,
            )
            when (val result = repository.createDailyLog(token, request)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isSavingDailyLog = false, showDailyLogPopup = false) }
                    load(s0.assignmentId)
                }
                is NetworkResult.Error -> _state.update { it.copy(isSavingDailyLog = false, dailyLogError = result.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(isSavingDailyLog = false, dailyLogError = "Connection error") }
            }
        }
    }

    fun dismissDailyLogPopup() {
        val s0 = _state.value
        if (s0.dailyLogAssignmentId.isBlank()) return
        val today = todayIso()
        viewModelScope.launch {
            val token = preferenceRepository.getUserToken().first()
            if (token != null) {
                repository.setPopupPrefs(token, SylPopupPrefsRequest(assignmentId = s0.dailyLogAssignmentId, dismissedOn = today))
            }
            _state.update { it.copy(showDailyLogPopup = false) }
        }
    }

    fun loadDailyLogs() {
        val s0 = _state.value
        if (s0.assignmentId.isBlank()) return
        viewModelScope.launch {
            val token = preferenceRepository.getUserToken().first()
            if (token == null) return@launch
            when (val result = repository.listDailyLogs(token, s0.assignmentId)) {
                is NetworkResult.Success -> _state.update { it.copy(dailyLogs = result.data.data.logs) }
                else -> Unit
            }
        }
    }

    // ── Agentic: Quiz generation ──────────────────────────────────────────────

    fun openQuizSheet(unitId: String) = _state.update {
        it.copy(showQuizSheet = true, quizUnitId = unitId, quizSelectedUnitIds = setOf(unitId), quizNumQuestions = 5, quizDifficulty = "MEDIUM", quizQuestionTypes = setOf("MCQ"), quizError = null)
    }

    fun openQuizSheetFromButton() = _state.update {
        val allUnitIds = it.units.map { u -> u.id }.toSet()
        it.copy(showQuizSheet = true, quizUnitId = "", quizSelectedUnitIds = emptySet(), quizNumQuestions = 5, quizDifficulty = "MEDIUM", quizQuestionTypes = setOf("MCQ"), quizError = null)
    }

    fun openQuizSheetMulti(unitIds: Set<String>) = _state.update {
        it.copy(showQuizSheet = true, quizUnitId = unitIds.firstOrNull() ?: "", quizSelectedUnitIds = unitIds, quizNumQuestions = 5, quizDifficulty = "MEDIUM", quizQuestionTypes = setOf("MCQ"), quizError = null)
    }

    fun closeQuizSheet() = _state.update { it.copy(showQuizSheet = false, quizError = null) }

    fun setQuizNumQuestions(value: Int) = _state.update { it.copy(quizNumQuestions = value.coerceIn(1, 20)) }

    fun setQuizDifficulty(value: String) = _state.update { it.copy(quizDifficulty = value) }

    fun toggleQuizUnit(unitId: String) = _state.update {
        val newSet = if (unitId in it.quizSelectedUnitIds) it.quizSelectedUnitIds - unitId else it.quizSelectedUnitIds + unitId
        it.copy(quizSelectedUnitIds = newSet)
    }

    fun toggleQuizQuestionType(type: String) = _state.update {
        val newSet = if (type in it.quizQuestionTypes) it.quizQuestionTypes - type else it.quizQuestionTypes + type
        it.copy(quizQuestionTypes = if (newSet.isEmpty()) setOf("MCQ") else newSet)
    }

    fun generateQuiz() {
        val s0 = _state.value
        if (s0.assignmentId.isBlank() || s0.quizSelectedUnitIds.isEmpty()) return
        viewModelScope.launch {
            _state.update { it.copy(isGeneratingQuiz = true, quizError = null) }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(isGeneratingQuiz = false, quizError = "Not authenticated") }
                return@launch
            }
            val request = QuizGenerateRequest(
                assignmentId = s0.assignmentId,
                unitIds = s0.quizSelectedUnitIds.toList(),
                unitId = s0.quizSelectedUnitIds.firstOrNull() ?: "",
                numQuestions = s0.quizNumQuestions,
                difficulty = s0.quizDifficulty,
                questionTypes = s0.quizQuestionTypes.toList(),
            )
            when (val result = repository.generateQuiz(token, request)) {
                is NetworkResult.Success -> {
                    val quiz = result.data.data
                    if (quiz != null) {
                        _state.update {
                            it.copy(
                                isGeneratingQuiz = false,
                                showQuizSheet = false,
                                showQuizPreview = true,
                                generatedQuiz = quiz,
                                quizPreviewError = null,
                            )
                        }
                    } else {
                        _state.update { it.copy(isGeneratingQuiz = false, quizError = "Quiz generation failed. Please try again.") }
                    }
                }
                is NetworkResult.Error -> _state.update { it.copy(isGeneratingQuiz = false, quizError = result.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(isGeneratingQuiz = false, quizError = "Connection error") }
            }
        }
    }

    fun closeQuizPreview() = _state.update {
        it.copy(showQuizPreview = false, generatedQuiz = null, editingQuestionId = null, quizPreviewError = null)
    }

    fun startEditingQuestion(questionId: String) = _state.update { it.copy(editingQuestionId = questionId) }

    fun cancelEditingQuestion() = _state.update { it.copy(editingQuestionId = null) }

    fun openAddQuestion() = _state.update { it.copy(showAddQuestion = true, quizPreviewError = null) }
    fun cancelAddQuestion() = _state.update { it.copy(showAddQuestion = false) }

    fun updateGeneratedQuestion(quizId: String, questionId: String, question: String, options: List<String>, correctAnswer: String, explanation: String?, questionType: String) {
        viewModelScope.launch {
            _state.update { it.copy(isPublishingQuiz = true, quizPreviewError = null) }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(isPublishingQuiz = false, quizPreviewError = "Not authenticated") }
                return@launch
            }
            val request = QuizUpdateQuestionRequest(
                question = question,
                options = options,
                correctAnswer = correctAnswer,
                explanation = explanation,
                questionType = questionType,
            )
            when (val result = repository.updateQuizQuestion(token, quizId, questionId, request)) {
                is NetworkResult.Success -> {
                    val s = _state.value
                    val quiz = s.generatedQuiz
                    if (quiz != null) {
                        val updatedQuestions = quiz.questions.map { q ->
                            if (q.id == questionId) q.copy(
                                question = question,
                                options = options,
                                correctAnswer = correctAnswer,
                                explanation = explanation,
                                questionType = questionType,
                            ) else q
                        }
                        _state.update {
                            it.copy(
                                isPublishingQuiz = false,
                                editingQuestionId = null,
                                generatedQuiz = quiz.copy(questions = updatedQuestions),
                            )
                        }
                    } else {
                        _state.update { it.copy(isPublishingQuiz = false, editingQuestionId = null) }
                    }
                }
                is NetworkResult.Error -> _state.update { it.copy(isPublishingQuiz = false, quizPreviewError = result.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(isPublishingQuiz = false, quizPreviewError = "Connection error") }
            }
        }
    }

    fun addQuestion(quizId: String, question: String, options: List<String>, correctAnswer: String, explanation: String?, questionType: String) {
        viewModelScope.launch {
            _state.update { it.copy(isPublishingQuiz = true, quizPreviewError = null) }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(isPublishingQuiz = false, quizPreviewError = "Not authenticated") }
                return@launch
            }
            val request = QuizUpdateQuestionRequest(
                question = question,
                options = options,
                correctAnswer = correctAnswer,
                explanation = explanation,
                questionType = questionType,
            )
            when (val result = repository.addQuizQuestion(token, quizId, request)) {
                is NetworkResult.Success -> {
                    val s = _state.value
                    val quiz = s.generatedQuiz
                    val newQ = result.data.data
                    if (quiz != null && newQ != null) {
                        _state.update {
                            it.copy(
                                isPublishingQuiz = false,
                                showAddQuestion = false,
                                generatedQuiz = quiz.copy(questions = quiz.questions + newQ),
                            )
                        }
                    } else {
                        _state.update { it.copy(isPublishingQuiz = false, showAddQuestion = false) }
                    }
                }
                is NetworkResult.Error -> _state.update { it.copy(isPublishingQuiz = false, quizPreviewError = result.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(isPublishingQuiz = false, quizPreviewError = "Connection error") }
            }
        }
    }

    fun regenerateQuizQuestions() {
        val s0 = _state.value
        val quiz = s0.generatedQuiz ?: return
        viewModelScope.launch {
            _state.update { it.copy(isRegenerating = true, quizPreviewError = null) }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(isRegenerating = false, quizPreviewError = "Not authenticated") }
                return@launch
            }
            when (val result = repository.regenerateQuiz(token, quiz.id)) {
                is NetworkResult.Success -> {
                    val newQuiz = result.data.data
                    if (newQuiz != null) {
                        _state.update { it.copy(isRegenerating = false, generatedQuiz = newQuiz) }
                    } else {
                        _state.update { it.copy(isRegenerating = false, quizPreviewError = "Regeneration failed. Please try again.") }
                    }
                }
                is NetworkResult.Error -> _state.update { it.copy(isRegenerating = false, quizPreviewError = result.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(isRegenerating = false, quizPreviewError = "Connection error") }
            }
        }
    }

    fun publishGeneratedQuiz() {
        val s0 = _state.value
        val quiz = s0.generatedQuiz ?: return
        viewModelScope.launch {
            _state.update { it.copy(isPublishingQuiz = true, quizPreviewError = null) }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(isPublishingQuiz = false, quizPreviewError = "Not authenticated") }
                return@launch
            }
            when (val result = repository.publishQuiz(token, quiz.id)) {
                is NetworkResult.Success -> {
                    _state.update {
                        it.copy(
                            isPublishingQuiz = false,
                            showQuizPreview = false,
                            generatedQuiz = null,
                            editingQuestionId = null,
                        )
                    }
                    loadQuizzes()
                }
                is NetworkResult.Error -> _state.update { it.copy(isPublishingQuiz = false, quizPreviewError = result.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(isPublishingQuiz = false, quizPreviewError = "Connection error") }
            }
        }
    }

    fun loadQuizzes() {
        val s0 = _state.value
        if (s0.assignmentId.isBlank()) return
        viewModelScope.launch {
            val token = preferenceRepository.getUserToken().first()
            if (token == null) return@launch
            when (val result = repository.listQuizzes(token, s0.assignmentId)) {
                is NetworkResult.Success -> _state.update { it.copy(quizzes = result.data.data.quizzes) }
                else -> Unit
            }
        }
    }

    fun publishQuiz(quizId: String) {
        viewModelScope.launch {
            val token = preferenceRepository.getUserToken().first()
            if (token == null) return@launch
            when (val result = repository.publishQuiz(token, quizId)) {
                is NetworkResult.Success -> loadQuizzes()
                else -> Unit
            }
        }
    }

    fun loadLeaderboard(quizId: String) {
        _state.update { it.copy(showLeaderboard = true, leaderboardQuizId = quizId, leaderboard = null, leaderboardLoading = true, leaderboardError = null) }
        viewModelScope.launch {
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(leaderboardLoading = false, leaderboardError = "Not authenticated") }
                return@launch
            }
            when (val result = repository.getQuizLeaderboard(token, quizId)) {
                is NetworkResult.Success -> _state.update { it.copy(leaderboardLoading = false, leaderboard = result.data.data) }
                is NetworkResult.Error -> _state.update { it.copy(leaderboardLoading = false, leaderboardError = result.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(leaderboardLoading = false, leaderboardError = "Connection error") }
            }
        }
    }

    fun closeLeaderboard() = _state.update { it.copy(showLeaderboard = false, leaderboard = null, leaderboardError = null, leaderboardQuizId = "", compareAttendance = false, attendanceAnalytics = null) }

    fun toggleCompareAttendance() {
        val current = _state.value
        val newValue = !current.compareAttendance
        _state.update { it.copy(compareAttendance = newValue) }
        if (newValue && current.attendanceAnalytics == null && !current.attendanceAnalyticsLoading) {
            loadAttendanceForComparison()
        }
    }

    private fun loadAttendanceForComparison() {
        val assignmentId = _state.value.assignmentId
        if (assignmentId.isBlank()) return
        _state.update { it.copy(attendanceAnalyticsLoading = true) }
        viewModelScope.launch {
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(attendanceAnalyticsLoading = false) }
                return@launch
            }
            when (val result = repository.getAttendanceAnalytics(token, assignmentId)) {
                is NetworkResult.Success -> _state.update { it.copy(attendanceAnalyticsLoading = false, attendanceAnalytics = result.data.data) }
                is NetworkResult.Error -> _state.update { it.copy(attendanceAnalyticsLoading = false) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(attendanceAnalyticsLoading = false) }
            }
        }
    }

    // ── NCERT Auto-fill ──────────────────────────────────────────────────

    fun autoFill() {
        val s0 = _state.value
        if (s0.assignmentId.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isAutoFilling = true, autoFillError = null) }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(isAutoFilling = false, autoFillError = "Not authenticated") }
                return@launch
            }
            val request = SylAutoFillRequest(assignmentId = s0.assignmentId)
            when (val result = repository.autoFillSyllabus(token, request)) {
                is NetworkResult.Success -> {
                    val d = result.data.data
                    if (d.found && d.chapters.isNotEmpty()) {
                        _state.update {
                            it.copy(
                                isAutoFilling = false,
                                autoFillChapters = d.chapters,
                                autoFillSource = d.source,
                                showAutoFillPreview = true,
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                isAutoFilling = false,
                                autoFillError = "No NCERT reference found for ${s0.className} ${s0.subject}. Try pasting syllabus text or adding manually.",
                            )
                        }
                    }
                }
                is NetworkResult.Error -> _state.update { it.copy(isAutoFilling = false, autoFillError = result.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(isAutoFilling = false, autoFillError = "Connection error") }
            }
        }
    }

    fun confirmAutoFill() {
        val s0 = _state.value
        if (s0.assignmentId.isBlank() || s0.autoFillChapters.isEmpty()) return
        viewModelScope.launch {
            _state.update { it.copy(isAutoFilling = true, autoFillError = null) }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(isAutoFilling = false, autoFillError = "Not authenticated") }
                return@launch
            }
            when (val result = repository.confirmAutoFillSyllabus(token, s0.assignmentId, s0.autoFillChapters)) {
                is NetworkResult.Success -> {
                    _state.update {
                        it.copy(
                            isAutoFilling = false,
                            showAutoFillPreview = false,
                            autoFillChapters = emptyList(),
                            autoFillSource = "",
                        )
                    }
                    load(s0.assignmentId)
                    loadPaceWarning()
                }
                is NetworkResult.Error -> _state.update { it.copy(isAutoFilling = false, autoFillError = result.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(isAutoFilling = false, autoFillError = "Connection error") }
            }
        }
    }

    fun dismissAutoFillPreview() {
        _state.update { it.copy(showAutoFillPreview = false, autoFillChapters = emptyList(), autoFillSource = "") }
    }

    // ── Approval workflow ────────────────────────────────────────────────

    fun approveAllDrafts() {
        val s0 = _state.value
        if (s0.assignmentId.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isApproving = true, approveError = null) }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(isApproving = false, approveError = "Not authenticated") }
                return@launch
            }
            val request = SylApproveRequest(assignmentId = s0.assignmentId)
            when (val result = repository.approveSyllabus(token, request)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isApproving = false) }
                    load(s0.assignmentId)
                    loadPaceWarning()
                }
                is NetworkResult.Error -> _state.update { it.copy(isApproving = false, approveError = result.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(isApproving = false, approveError = "Connection error") }
            }
        }
    }

    fun rejectAllDrafts() {
        val s0 = _state.value
        if (s0.assignmentId.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isApproving = true, approveError = null) }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(isApproving = false, approveError = "Not authenticated") }
                return@launch
            }
            when (val result = repository.rejectSyllabus(token, SylApproveRequest(assignmentId = s0.assignmentId))) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isApproving = false) }
                    load(s0.assignmentId)
                }
                is NetworkResult.Error -> _state.update { it.copy(isApproving = false, approveError = result.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(isApproving = false, approveError = "Connection error") }
            }
        }
    }

    // ── Pace warning ─────────────────────────────────────────────────────

    fun loadPaceWarning() {
        val s0 = _state.value
        if (s0.assignmentId.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoadingPace = true) }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(isLoadingPace = false) }
                return@launch
            }
            when (val result = repository.getPaceWarning(token, s0.assignmentId)) {
                is NetworkResult.Success -> {
                    val w = result.data.data
                    _state.update { it.copy(isLoadingPace = false, paceWarning = w) }
                }
                else -> _state.update { it.copy(isLoadingPace = false) }
            }
        }
    }
}

private fun SyllabusNodeDto.toUi() = SyllabusUnit(
    id = id,
    parentId = parentId,
    title = title,
    depth = depth,
    isChapter = isChapter,
    isCovered = isCovered,
    coveredOn = coveredOn,
    note = note,
    approvalStatus = approvalStatus,
)
