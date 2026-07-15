package com.littlebridge.enrollplus.feature.teacher.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.admin.domain.model.SalaryRecordDto
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
}
