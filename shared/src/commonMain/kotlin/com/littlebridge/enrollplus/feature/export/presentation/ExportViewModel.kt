package com.littlebridge.enrollplus.feature.export.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.export.domain.model.*
import com.littlebridge.enrollplus.feature.export.domain.repository.ExportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ExportState(
    val exportTypes: List<ExportTypeDto> = emptyList(),
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val downloadUrl: String? = null,
    val fileName: String? = null,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
)

class ExportViewModel(
    private val repository: ExportRepository,
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ExportState())
    val state: StateFlow<ExportState> = _state.asStateFlow()

    fun loadExportTypes() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            val token = preferenceRepository.getUserToken().first()
            if (token.isNullOrBlank()) {
                _state.value = _state.value.copy(isLoading = false)
                return@launch
            }
            when (val result = repository.getExportTypes(token)) {
                is NetworkResult.Success -> {
                    val types = result.data.data?.exports ?: emptyList()
                    _state.value = _state.value.copy(
                        exportTypes = types,
                        isLoading = false,
                    )
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(isLoading = false, errorMessage = result.message)
                }
                is NetworkResult.ConnectionError -> {
                    _state.value = _state.value.copy(isLoading = false, errorMessage = "Connection error. Check your internet.")
                }
            }
        }
    }

    fun generateExport(
        type: String,
        format: String,
        classId: String? = null,
        assessmentId: String? = null,
        eventId: String? = null,
        routeId: String? = null,
        homeworkId: String? = null,
        status: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null,
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isGenerating = true, errorMessage = null, downloadUrl = null, infoMessage = null)
            val token = preferenceRepository.getUserToken().first()
            if (token.isNullOrBlank()) {
                _state.value = _state.value.copy(isGenerating = false)
                return@launch
            }
            val request = ExportRequest(
                type = type,
                format = format,
                classId = classId,
                assessmentId = assessmentId,
                eventId = eventId,
                routeId = routeId,
                homeworkId = homeworkId,
                status = status,
                dateFrom = dateFrom,
                dateTo = dateTo,
            )
            when (val result = repository.generateExport(token, request)) {
                is NetworkResult.Success -> {
                    val data = result.data.data
                    _state.value = _state.value.copy(
                        isGenerating = false,
                        downloadUrl = data?.downloadUrl,
                        fileName = data?.fileName,
                        infoMessage = result.data.message,
                    )
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(isGenerating = false, errorMessage = result.message)
                }
                is NetworkResult.ConnectionError -> {
                    _state.value = _state.value.copy(isGenerating = false, errorMessage = "Connection error. Check your internet.")
                }
            }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(errorMessage = null, infoMessage = null, downloadUrl = null, fileName = null)
    }
}
