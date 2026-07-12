package com.littlebridge.enrollplus.feature.exam.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.exam.data.remote.ApiResponseUnit
import com.littlebridge.enrollplus.feature.exam.data.repository.ExamRepository
import com.littlebridge.enrollplus.feature.exam.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ── States ───────────────────────────────────────────────────────────────────

data class ExamTimetableListState(
    val timetables: List<ExamTimetable> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

data class ExamTimetableDetailState(
    val timetable: ExamTimetable? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

data class ExamOcrState(
    val entries: List<ExamTimetableEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val rawAiOutput: String? = null,
)

data class ExamSyllabusState(
    val syllabus: ExamSyllabusResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaving: Boolean = false,
    val saveMessage: String? = null,
)

data class ExamPublishState(
    val isPublishing: Boolean = false,
    val message: String? = null,
    val error: String? = null,
)

// ── ViewModels ───────────────────────────────────────────────────────────────

class ExamTimetablesViewModel(
    private val repository: ExamRepository,
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {

    private val _listState = MutableStateFlow(ExamTimetableListState())
    val listState: StateFlow<ExamTimetableListState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(ExamTimetableDetailState())
    val detailState: StateFlow<ExamTimetableDetailState> = _detailState.asStateFlow()

    private val _ocrState = MutableStateFlow(ExamOcrState())
    val ocrState: StateFlow<ExamOcrState> = _ocrState.asStateFlow()

    private val _publishState = MutableStateFlow(ExamPublishState())
    val publishState: StateFlow<ExamPublishState> = _publishState.asStateFlow()

    fun loadTimetables(className: String? = null, status: String? = null) {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _listState.update { it.copy(isLoading = false, error = "Not authenticated") }
                return@launch
            }
            when (val result = repository.listTimetables(token, className, status)) {
                is NetworkResult.Success -> _listState.update {
                    it.copy(isLoading = false, timetables = result.data.data.timetables)
                }
                is NetworkResult.Error -> _listState.update { it.copy(isLoading = false, error = result.message) }
                is NetworkResult.ConnectionError -> _listState.update { it.copy(isLoading = false, error = "Connection error") }
            }
        }
    }

    fun loadTimetable(timetableId: String) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _detailState.update { it.copy(isLoading = false, error = "Not authenticated") }
                return@launch
            }
            when (val result = repository.getTimetable(token, timetableId)) {
                is NetworkResult.Success -> _detailState.update {
                    it.copy(isLoading = false, timetable = result.data.data)
                }
                is NetworkResult.Error -> _detailState.update { it.copy(isLoading = false, error = result.message) }
                is NetworkResult.ConnectionError -> _detailState.update { it.copy(isLoading = false, error = "Connection error") }
            }
        }
    }

    fun importOcr(image: String, mimeType: String, className: String, section: String) {
        viewModelScope.launch {
            _ocrState.update { it.copy(isLoading = true, error = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _ocrState.update { it.copy(isLoading = false, error = "Not authenticated") }
                return@launch
            }
            when (val result = repository.importOcr(token, image, mimeType, className, section)) {
                is NetworkResult.Success -> _ocrState.update {
                    it.copy(isLoading = false, entries = result.data.data.entries, rawAiOutput = result.data.data.rawAiOutput)
                }
                is NetworkResult.Error -> _ocrState.update { it.copy(isLoading = false, error = result.message) }
                is NetworkResult.ConnectionError -> _ocrState.update { it.copy(isLoading = false, error = "Connection error") }
            }
        }
    }

    fun importText(text: String, className: String, section: String) {
        viewModelScope.launch {
            _ocrState.update { it.copy(isLoading = true, error = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _ocrState.update { it.copy(isLoading = false, error = "Not authenticated") }
                return@launch
            }
            when (val result = repository.importText(token, text, className, section)) {
                is NetworkResult.Success -> _ocrState.update {
                    it.copy(isLoading = false, entries = result.data.data.entries, rawAiOutput = result.data.data.rawAiOutput)
                }
                is NetworkResult.Error -> _ocrState.update { it.copy(isLoading = false, error = result.message) }
                is NetworkResult.ConnectionError -> _ocrState.update { it.copy(isLoading = false, error = "Connection error") }
            }
        }
    }

    fun createTimetable(request: ExamTimetableCreateRequest, onCreated: (ExamTimetable) -> Unit = {}) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _detailState.update { it.copy(isLoading = false, error = "Not authenticated") }
                return@launch
            }
            when (val result = repository.createTimetable(token, request)) {
                is NetworkResult.Success -> {
                    _detailState.update { it.copy(isLoading = false, timetable = result.data.data) }
                    onCreated(result.data.data)
                }
                is NetworkResult.Error -> _detailState.update { it.copy(isLoading = false, error = result.message) }
                is NetworkResult.ConnectionError -> _detailState.update { it.copy(isLoading = false, error = "Connection error") }
            }
        }
    }

    fun publishTimetable(timetableId: String, onPublished: () -> Unit = {}) {
        viewModelScope.launch {
            _publishState.update { it.copy(isPublishing = true, error = null, message = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _publishState.update { it.copy(isPublishing = false, error = "Not authenticated") }
                return@launch
            }
            when (val result = repository.publishTimetable(token, timetableId)) {
                is NetworkResult.Success -> {
                    _publishState.update { it.copy(isPublishing = false, message = result.data.message.ifBlank { "Published" }) }
                    onPublished()
                }
                is NetworkResult.Error -> _publishState.update { it.copy(isPublishing = false, error = result.message) }
                is NetworkResult.ConnectionError -> _publishState.update { it.copy(isPublishing = false, error = "Connection error") }
            }
        }
    }

    fun clearOcrEntries() {
        _ocrState.update { ExamOcrState() }
    }
}

class ExamSyllabusViewModel(
    private val repository: ExamRepository,
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ExamSyllabusState())
    val state: StateFlow<ExamSyllabusState> = _state.asStateFlow()

    fun loadSyllabus(assessmentId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _state.update { it.copy(isLoading = false, error = "Not authenticated") }
                return@launch
            }
            when (val result = repository.getExamSyllabus(token, assessmentId)) {
                is NetworkResult.Success -> _state.update {
                    it.copy(isLoading = false, syllabus = result.data.data)
                }
                is NetworkResult.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(isLoading = false, error = "Connection error") }
            }
        }
    }

    fun updateSyllabus(assessmentId: String, unitIds: List<String>) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null, saveMessage = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _state.update { it.copy(isSaving = false, error = "Not authenticated") }
                return@launch
            }
            when (val result = repository.updateExamSyllabus(token, assessmentId, unitIds)) {
                is NetworkResult.Success -> _state.update {
                    it.copy(isSaving = false, saveMessage = result.data.message.ifBlank { "Saved" })
                }
                is NetworkResult.Error -> _state.update { it.copy(isSaving = false, error = result.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(isSaving = false, error = "Connection error") }
            }
        }
    }

    fun clearSaveMessage() {
        _state.update { it.copy(saveMessage = null) }
    }
}

class ParentExamViewModel(
    private val repository: ExamRepository,
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {

    private val _syllabusState = MutableStateFlow(ExamSyllabusState())
    val syllabusState: StateFlow<ExamSyllabusState> = _syllabusState.asStateFlow()

    private val _requestState = MutableStateFlow<ExamSyllabusRequestResponse?>(null)
    val requestState: StateFlow<ExamSyllabusRequestResponse?> = _requestState.asStateFlow()

    private val _isRequesting = MutableStateFlow(false)
    val isRequesting: StateFlow<Boolean> = _isRequesting.asStateFlow()

    fun loadExamSyllabus(childId: String, assessmentId: String) {
        viewModelScope.launch {
            _syllabusState.update { it.copy(isLoading = true, error = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _syllabusState.update { it.copy(isLoading = false, error = "Not authenticated") }
                return@launch
            }
            when (val result = repository.getParentExamSyllabus(token, childId, assessmentId)) {
                is NetworkResult.Success -> _syllabusState.update {
                    it.copy(isLoading = false, syllabus = result.data.data)
                }
                is NetworkResult.Error -> _syllabusState.update { it.copy(isLoading = false, error = result.message) }
                is NetworkResult.ConnectionError -> _syllabusState.update { it.copy(isLoading = false, error = "Connection error") }
            }
        }
    }

    fun requestSyllabus(assessmentId: String, message: String = "") {
        viewModelScope.launch {
            _isRequesting.value = true
            val token = preferenceRepository.getUserToken().first() ?: run {
                _isRequesting.value = false
                return@launch
            }
            when (val result = repository.requestSyllabus(token, assessmentId, message)) {
                is NetworkResult.Success -> _requestState.value = result.data.data
                is NetworkResult.Error -> _requestState.value = ExamSyllabusRequestResponse(success = false, message = result.message)
                is NetworkResult.ConnectionError -> _requestState.value = ExamSyllabusRequestResponse(success = false, message = "Connection error")
            }
            _isRequesting.value = false
        }
    }
}
