package com.littlebridge.enrollplus.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardActivity
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardOverview
import com.littlebridge.enrollplus.feature.admin.domain.repository.AdminDashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AdminHomeViewModel(
    private val dashboardRepository: AdminDashboardRepository,
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {

    private val _overviewState = MutableStateFlow<UiState<AdminDashboardOverview>>(UiState.Loading)
    val overviewState: StateFlow<UiState<AdminDashboardOverview>> = _overviewState.asStateFlow()

    private val _activityState = MutableStateFlow<UiState<AdminDashboardActivity>>(UiState.Loading)
    val activityState: StateFlow<UiState<AdminDashboardActivity>> = _activityState.asStateFlow()

    private suspend fun token(): String? = preferenceRepository.getUserToken().first()

    fun loadOverview() {
        viewModelScope.launch {
            _overviewState.value = UiState.Loading
            val t = token() ?: run {
                _overviewState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = dashboardRepository.getOverview(t)) {
                is NetworkResult.Success -> _overviewState.value = res.data.data?.let { UiState.Success(it) } ?: UiState.Error("No data")
                is NetworkResult.Error -> _overviewState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _overviewState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadActivity() {
        viewModelScope.launch {
            _activityState.value = UiState.Loading
            val t = token() ?: run {
                _activityState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = dashboardRepository.getActivity(t)) {
                is NetworkResult.Success -> _activityState.value = res.data.data?.let { UiState.Success(it) } ?: UiState.Error("No data")
                is NetworkResult.Error -> _activityState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _activityState.value = UiState.Error("Connection error")
            }
        }
    }

    fun refresh() {
        loadOverview()
        loadActivity()
    }
}
