package com.littlebridge.enrollplus.feature.teacher.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.admin.domain.model.SalaryRecordDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.EscalateFeeRequest
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherFeeStudentDto
import com.littlebridge.enrollplus.feature.teacher.domain.repository.TeacherSalaryRepository
import com.littlebridge.enrollplus.util.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TeacherSalaryState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val records: List<SalaryRecordDto> = emptyList(),
    // Fee escalation
    val feeStudents: List<TeacherFeeStudentDto> = emptyList(),
    val totalDue: Double = 0.0,
    val isFeeLoading: Boolean = false,
    val isEscalating: Boolean = false,
    val escalationMessage: String? = null,
    val selectedMonth: String = "",
)

class TeacherSalaryViewModel(
    private val repository: TeacherSalaryRepository,
    private val prefs: PreferenceRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(TeacherSalaryState())
    val state: StateFlow<TeacherSalaryState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val token = prefs.getUserToken().first()
            if (token.isNullOrBlank()) {
                _state.update { it.copy(isLoading = false, errorMessage = "Not authenticated") }
                return@launch
            }
            when (val result = repository.getMySalary(token)) {
                is NetworkResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            records = result.data.data?.records ?: emptyList(),
                        )
                    }
                }
                is NetworkResult.Error -> {
                    AppLogger.e("TeacherSalaryVM", "Failed: ${result.message}")
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isLoading = false, errorMessage = "Connection error") }
                }
            }
        }
    }

    fun loadUnpaidFees() {
        viewModelScope.launch {
            _state.update { it.copy(isFeeLoading = true, errorMessage = null, escalationMessage = null) }
            val token = prefs.getUserToken().first()
            if (token.isNullOrBlank()) {
                _state.update { it.copy(isFeeLoading = false, errorMessage = "Not authenticated") }
                return@launch
            }
            when (val result = repository.getUnpaidFees(token)) {
                is NetworkResult.Success -> {
                    val data = result.data.data
                    _state.update {
                        it.copy(
                            isFeeLoading = false,
                            feeStudents = data?.students ?: emptyList(),
                            totalDue = data?.totalDue ?: 0.0,
                        )
                    }
                }
                is NetworkResult.Error -> {
                    AppLogger.e("TeacherFeeVM", "Failed: ${result.message}")
                    _state.update { it.copy(isFeeLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isFeeLoading = false, errorMessage = "Connection error") }
                }
            }
        }
    }

    fun escalateFees(childIds: List<String>, customMessage: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isEscalating = true, escalationMessage = null) }
            val token = prefs.getUserToken().first()
            if (token.isNullOrBlank()) {
                _state.update { it.copy(isEscalating = false, errorMessage = "Not authenticated") }
                return@launch
            }
            when (val result = repository.escalateFees(token, EscalateFeeRequest(childIds, customMessage))) {
                is NetworkResult.Success -> {
                    val notified = result.data.data?.get("notified") ?: 0
                    _state.update { it.copy(isEscalating = false, escalationMessage = "Reminder sent to $notified parent(s)") }
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isEscalating = false, errorMessage = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isEscalating = false, errorMessage = "Connection error") }
                }
            }
        }
    }
}
