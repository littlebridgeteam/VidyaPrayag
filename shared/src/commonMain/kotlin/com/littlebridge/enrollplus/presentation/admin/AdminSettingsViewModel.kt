package com.littlebridge.enrollplus.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.feature.admin.domain.model.SchoolProfileDto
import com.littlebridge.enrollplus.feature.admin.domain.model.UserProfileResponse
import com.littlebridge.enrollplus.feature.admin.domain.repository.SchoolProfileRepository
import com.littlebridge.enrollplus.feature.admin.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminSettingsViewModel(
    private val schoolProfileRepository: SchoolProfileRepository,
    private val userProfileRepository: UserProfileRepository,
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {

    private val _schoolProfileState = MutableStateFlow<UiState<SchoolProfileDto>>(UiState.Loading)
    val schoolProfileState: StateFlow<UiState<SchoolProfileDto>> = _schoolProfileState.asStateFlow()

    private val _userProfileState = MutableStateFlow<UiState<UserProfileResponse>>(UiState.Loading)
    val userProfileState: StateFlow<UiState<UserProfileResponse>> = _userProfileState.asStateFlow()

    val themeName: StateFlow<String> = preferenceRepository.getThemeName()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "LIGHT")

    val themeMode: StateFlow<String> = preferenceRepository.getThemeMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")

    private suspend fun token(): String? = preferenceRepository.getUserToken().first()

    fun loadSchoolProfile() {
        viewModelScope.launch {
            _schoolProfileState.value = UiState.Loading
            val t = token() ?: run {
                _schoolProfileState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = schoolProfileRepository.getProfile(t)) {
                is NetworkResult.Success -> _schoolProfileState.value = res.data.data?.let { UiState.Success(it) } ?: UiState.Error("No data")
                is NetworkResult.Error -> _schoolProfileState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _schoolProfileState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _userProfileState.value = UiState.Loading
            val t = token() ?: run {
                _userProfileState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = userProfileRepository.getProfile(t)) {
                is NetworkResult.Success -> _userProfileState.value = res.data.data?.let { UiState.Success(it) } ?: UiState.Error("No data")
                is NetworkResult.Error -> _userProfileState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _userProfileState.value = UiState.Error("Connection error")
            }
        }
    }

    fun setTheme(name: String) {
        viewModelScope.launch { preferenceRepository.setThemeName(name) }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch { preferenceRepository.setThemeMode(mode) }
    }

    fun logout() {
        viewModelScope.launch { preferenceRepository.setUserToken(null) }
    }

    fun refresh() {
        loadSchoolProfile()
        loadUserProfile()
    }
}
