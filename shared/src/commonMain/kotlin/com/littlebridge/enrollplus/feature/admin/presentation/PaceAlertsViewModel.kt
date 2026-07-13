package com.littlebridge.enrollplus.feature.admin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.admin.domain.repository.AdminDashboardRepository
import com.littlebridge.enrollplus.feature.teacher.domain.model.PaceAlertDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.PaceSnapshotDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PaceAlertsState(
    val snapshots: List<PaceSnapshotDto> = emptyList(),
    val alerts: List<PaceAlertDto> = emptyList(),
    val isLoading: Boolean = false,
    val isRecalculating: Boolean = false,
    val errorMessage: String? = null,
    val resolvingAlertId: String? = null,
    val isStale: Boolean = false,
    val isOffline: Boolean = false,
)

class PaceAlertsViewModel(
    private val repository: AdminDashboardRepository,
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(PaceAlertsState())
    val state: StateFlow<PaceAlertsState> = _state.asStateFlow()

    init {
        load()
    }

    private suspend fun token(): String? = preferenceRepository.getUserToken().first()

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val tkn = token() ?: run {
                _state.update { it.copy(isLoading = false, errorMessage = "Not authenticated") }
                return@launch
            }
            val snapshotsResult = repository.getPaceSnapshots(tkn)
            val alertsResult = repository.getPaceAlerts(tkn)
            val snapshots = (snapshotsResult as? NetworkResult.Success)?.data?.data?.snapshots ?: emptyList()
            val alerts = (alertsResult as? NetworkResult.Success)?.data?.data?.alerts ?: emptyList()
            val error = when {
                snapshotsResult is NetworkResult.Error -> snapshotsResult.message
                alertsResult is NetworkResult.Error -> alertsResult.message
                snapshotsResult is NetworkResult.ConnectionError -> "Connection error"
                alertsResult is NetworkResult.ConnectionError -> "Connection error"
                else -> null
            }
            _state.update {
                it.copy(
                    isLoading = false,
                    snapshots = snapshots,
                    alerts = alerts,
                    errorMessage = error,
                    isStale = (snapshotsResult as? NetworkResult.Success)?.isStale ?: false,
                    isOffline = (alertsResult as? NetworkResult.Success)?.isOffline ?: false,
                )
            }
        }
    }

    fun resolveAlert(alertId: String) {
        viewModelScope.launch {
            _state.update { it.copy(resolvingAlertId = alertId) }
            val tkn = token() ?: run {
                _state.update { it.copy(resolvingAlertId = null) }
                return@launch
            }
            when (val r = repository.resolvePaceAlert(tkn, alertId)) {
                is NetworkResult.Success -> {
                    _state.update {
                        it.copy(
                            resolvingAlertId = null,
                            alerts = it.alerts.filterNot { a -> a.id == alertId },
                        )
                    }
                }
                is NetworkResult.Error -> _state.update { it.copy(resolvingAlertId = null) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(resolvingAlertId = null) }
            }
        }
    }

    fun recalculate() {
        viewModelScope.launch {
            _state.update { it.copy(isRecalculating = true) }
            val tkn = token() ?: run {
                _state.update { it.copy(isRecalculating = false) }
                return@launch
            }
            when (val r = repository.recalculatePace(tkn)) {
                is NetworkResult.Success -> {
                    _state.update {
                        it.copy(
                            isRecalculating = false,
                            snapshots = r.data.data.snapshots,
                        )
                    }
                }
                is NetworkResult.Error -> _state.update { it.copy(isRecalculating = false) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(isRecalculating = false) }
            }
        }
    }
}
