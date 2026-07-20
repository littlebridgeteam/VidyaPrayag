/*
 * File: TeacherProfileViewModel.kt
 * Module: feature.admin.presentation
 *
 * RA-45: drives the admin teacher profile screen (classes/subjects/coverage).
 * The id is supplied by the screen via load(id). Three states.
 */
package com.littlebridge.enrollplus.feature.admin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherProfileDto
import com.littlebridge.enrollplus.feature.admin.domain.model.UpdateTeacherRequest
import com.littlebridge.enrollplus.feature.admin.domain.repository.StudentsRepository
import com.littlebridge.enrollplus.feature.admin.domain.repository.TeachersRepository
import com.littlebridge.enrollplus.util.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class TeacherProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val profile: TeacherProfileDto? = null,
    // RA-S17: delete-from-profile (replaces the direct People-row Remove button)
    val isRemoving: Boolean = false,
    val removed: Boolean = false,
    val removeError: String? = null,
    val isStale: Boolean = false,
    val isOffline: Boolean = false,
    // Bug 11: edit-mode state
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val saveSuccess: Boolean = false,
)

class TeacherProfileViewModel(
    private val repository: StudentsRepository,
    private val teachersRepository: TeachersRepository,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TeacherProfileUiState())
    val state: StateFlow<TeacherProfileUiState> = _state.asStateFlow()

    private var lastId: String? = null

    fun load(teacherId: String) {
        lastId = teacherId
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val token = preferenceRepository.getUserToken().first()
            if (token.isNullOrBlank()) {
                _state.value = _state.value.copy(isLoading = false, error = "You are not signed in. Please log in again.")
                return@launch
            }
            when (val r = repository.getTeacherProfile(token, teacherId)) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(isLoading = false, error = null, profile = r.data.data, isStale = r.isStale, isOffline = r.isOffline)
                }
                is NetworkResult.Error -> {
                    AppLogger.e("TeacherProfileVM", "getTeacherProfile error: ${r.message}")
                    _state.value = _state.value.copy(isLoading = false, error = r.message)
                }
                is NetworkResult.ConnectionError -> {
                    _state.value = _state.value.copy(isLoading = false, error = "Connection error. Check your internet.")
                }
            }
        }
    }

    fun retry() { lastId?.let { load(it) } }

    /**
     * RA-S17: remove this teacher from the school (soft-delete) from inside the
     * profile, behind a confirm dialog on the screen. On success [removed] flips
     * true so the screen can pop back to People and refresh the roster.
     */
    fun remove(teacherId: String) {
        viewModelScope.launch {
            val token = preferenceRepository.getUserToken().first()
            if (token.isNullOrBlank()) {
                _state.value = _state.value.copy(removeError = "You are not signed in. Please log in again.")
                return@launch
            }
            _state.value = _state.value.copy(isRemoving = true, removeError = null)
            when (val r = teachersRepository.deleteTeacher(token, teacherId)) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(isRemoving = false, removed = true)
                    //load()
                }
                is NetworkResult.Error -> {
                    AppLogger.e("TeacherProfileVM", "deleteTeacher error: ${r.message}")
                    _state.value = _state.value.copy(isRemoving = false, removeError = r.message)
                }
                is NetworkResult.ConnectionError ->
                    _state.value = _state.value.copy(isRemoving = false, removeError = "Connection error. Check your internet.")
            }
        }
    }

    fun clearRemoveError() { _state.value = _state.value.copy(removeError = null) }

    fun startEdit() {
        _state.value = _state.value.copy(isEditing = true, saveError = null, saveSuccess = false)
    }

    fun cancelEdit() {
        _state.value = _state.value.copy(isEditing = false, saveError = null, isSaving = false)
    }

    fun updateTeacher(teacherId: String, name: String, email: String, phone: String, designation: String) {
        viewModelScope.launch {
            if (name.isBlank()) {
                _state.value = _state.value.copy(saveError = "Name cannot be blank")
                return@launch
            }
            _state.value = _state.value.copy(isSaving = true, saveError = null)
            val token = preferenceRepository.getUserToken().first()
            if (token.isNullOrBlank()) {
                _state.value = _state.value.copy(isSaving = false, saveError = "You are not signed in. Please log in again.")
                return@launch
            }
            val request = UpdateTeacherRequest(
                name = name.trim(),
                email = email.trim().ifBlank { null },
                phone = phone.trim().ifBlank { null },
                designation = designation.trim().ifBlank { null },
            )
            when (val r = teachersRepository.updateTeacher(token, teacherId, request)) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(isSaving = false, isEditing = false, saveSuccess = true)
                    // Reload the profile to reflect the updated data
                    load(teacherId)
                }
                is NetworkResult.Error -> {
                    AppLogger.e("TeacherProfileVM", "updateTeacher error: ${r.message}")
                    _state.value = _state.value.copy(isSaving = false, saveError = r.message)
                }
                is NetworkResult.ConnectionError -> {
                    _state.value = _state.value.copy(isSaving = false, saveError = "Connection error. Check your internet.")
                }
            }
        }
    }

    fun clearSaveMessage() { _state.value = _state.value.copy(saveError = null, saveSuccess = false) }
}
