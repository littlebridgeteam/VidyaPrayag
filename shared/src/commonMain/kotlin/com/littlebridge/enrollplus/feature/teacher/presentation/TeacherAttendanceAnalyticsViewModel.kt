package com.littlebridge.enrollplus.feature.teacher.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.teacher.domain.model.AttendanceAnalyticsDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.StudentAnalyticsDto
import com.littlebridge.enrollplus.feature.teacher.domain.repository.TeacherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TeacherAttendanceAnalyticsState(
    val assignmentId: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val analytics: AttendanceAnalyticsDto? = null,
    val isStale: Boolean = false,
    val isOffline: Boolean = false,
    // Student detail drill-down
    val selectedStudentId: String? = null,
    val studentAnalytics: StudentAnalyticsDto? = null,
    val isStudentLoading: Boolean = false,
    val studentError: String? = null,
)

class TeacherAttendanceAnalyticsViewModel(
    private val repository: TeacherRepository,
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(TeacherAttendanceAnalyticsState())
    val state: StateFlow<TeacherAttendanceAnalyticsState> = _state.asStateFlow()

    fun load(assignmentId: String) {
        if (assignmentId.isBlank()) return
        viewModelScope.launch {
            _state.update {
                it.copy(
                    assignmentId = assignmentId,
                    isLoading = it.analytics == null,
                    isRefreshing = it.analytics != null,
                    error = null,
                )
            }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(isLoading = false, isRefreshing = false, error = "Not authenticated") }
                return@launch
            }
            when (val result = repository.getAttendanceAnalytics(token, assignmentId)) {
                is NetworkResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            analytics = result.data.data,
                            isStale = result.isStale,
                            isOffline = result.isOffline,
                        )
                    }
                }
                is NetworkResult.Error -> _state.update {
                    it.copy(isLoading = false, isRefreshing = false, error = result.message)
                }
                is NetworkResult.ConnectionError -> _state.update {
                    it.copy(isLoading = false, isRefreshing = false, error = "Connection error")
                }
            }
        }
    }

    fun retry() = load(_state.value.assignmentId)

    fun loadStudentAnalytics(studentId: String) {
        val s0 = _state.value
        if (s0.assignmentId.isBlank()) return
        viewModelScope.launch {
            _state.update {
                it.copy(selectedStudentId = studentId, isStudentLoading = true, studentError = null)
            }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(isStudentLoading = false, studentError = "Not authenticated") }
                return@launch
            }
            when (val result = repository.getStudentAnalytics(token, s0.assignmentId, studentId)) {
                is NetworkResult.Success -> {
                    _state.update {
                        it.copy(isStudentLoading = false, studentAnalytics = result.data.data)
                    }
                }
                is NetworkResult.Error -> _state.update {
                    it.copy(isStudentLoading = false, studentError = result.message)
                }
                is NetworkResult.ConnectionError -> _state.update {
                    it.copy(isStudentLoading = false, studentError = "Connection error")
                }
            }
        }
    }

    fun closeStudentDetail() {
        _state.update {
            it.copy(selectedStudentId = null, studentAnalytics = null, studentError = null)
        }
    }
}
