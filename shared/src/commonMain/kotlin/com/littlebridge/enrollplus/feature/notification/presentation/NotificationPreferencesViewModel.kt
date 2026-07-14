package com.littlebridge.enrollplus.feature.notification.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.notification.domain.model.NotificationPreferenceDto
import com.littlebridge.enrollplus.feature.notification.domain.model.UpdatePreferenceRequest
import com.littlebridge.enrollplus.feature.notification.domain.repository.NotificationPreferencesRepository
import com.littlebridge.enrollplus.util.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationPreferencesState(
    val preferences: List<NotificationPreferenceDto> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false,
)

class NotificationPreferencesViewModel(
    private val repository: NotificationPreferencesRepository,
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationPreferencesState())
    val state: StateFlow<NotificationPreferencesState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(isLoading = false, error = "Not signed in") }
                return@launch
            }
            when (val result = repository.getPreferences(token)) {
                is NetworkResult.Success -> {
                    val prefs = result.data.data?.preferences ?: emptyList()
                    _state.update { it.copy(isLoading = false, preferences = prefs) }
                }
                is NetworkResult.Error -> {
                    AppLogger.e("NotifPrefsVM", "getPreferences error: ${result.message}")
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isLoading = false, error = "Connection error") }
                }
            }
        }
    }

    fun updatePreference(
        category: String,
        enabled: Boolean,
        pushEnabled: Boolean? = null,
        inAppEnabled: Boolean? = null,
        emailEnabled: Boolean? = null,
        smsEnabled: Boolean? = null,
        sound: String? = null,
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, saveSuccess = false, error = null) }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(isSaving = false, error = "Not signed in") }
                return@launch
            }
            val request = UpdatePreferenceRequest(
                category = category,
                enabled = enabled,
                pushEnabled = pushEnabled,
                inAppEnabled = inAppEnabled,
                emailEnabled = emailEnabled,
                smsEnabled = smsEnabled,
                sound = sound,
            )
            when (val result = repository.updatePreference(token, request)) {
                is NetworkResult.Success -> {
                    val updated = result.data.data
                    if (updated != null) {
                        _state.update { currentState ->
                            val prefs = currentState.preferences.map {
                                if (it.category == category) updated else it
                            }
                            currentState.copy(isSaving = false, preferences = prefs, saveSuccess = true)
                        }
                    } else {
                        _state.update { it.copy(isSaving = false, saveSuccess = true) }
                    }
                }
                is NetworkResult.Error -> {
                    AppLogger.e("NotifPrefsVM", "updatePreference error: ${result.message}")
                    _state.update { it.copy(isSaving = false, error = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isSaving = false, error = "Connection error") }
                }
            }
        }
    }

    fun resetPreference(category: String) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(isSaving = false, error = "Not signed in") }
                return@launch
            }
            when (val result = repository.resetPreference(token, category)) {
                is NetworkResult.Success -> {
                    _state.update { currentState ->
                        val prefs = currentState.preferences.filter { it.category != category }
                        currentState.copy(isSaving = false, preferences = prefs)
                    }
                    load()
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isSaving = false, error = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isSaving = false, error = "Connection error") }
                }
            }
        }
    }
}
