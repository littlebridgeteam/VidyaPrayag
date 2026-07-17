package com.littlebridge.enrollplus.feature.tutor.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.tutor.domain.model.SafetyFlagDto
import com.littlebridge.enrollplus.feature.tutor.domain.repository.TutorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TeacherSafetyFlagsState(
    val isLoading: Boolean = false,
    val flags: List<SafetyFlagDto> = emptyList(),
    val error: String? = null,
)

class TeacherSafetyFlagsViewModel(
    private val repository: TutorRepository,
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(TeacherSafetyFlagsState())
    val state: StateFlow<TeacherSafetyFlagsState> = _state.asStateFlow()

    fun loadFlags() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _state.update { it.copy(isLoading = false, error = "Not signed in") }
                return@launch
            }
            when (val result = repository.getSafetyFlags(token)) {
                is NetworkResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            flags = result.data.data ?: emptyList(),
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isLoading = false, error = "Connection error") }
                }
            }
        }
    }
}
