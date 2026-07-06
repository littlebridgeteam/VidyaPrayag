package com.littlebridge.enrollplus.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.feature.admin.domain.model.AttendanceSummaryDto
import com.littlebridge.enrollplus.feature.admin.domain.model.MarksSummaryDto
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeLedgerDto
import com.littlebridge.enrollplus.feature.admin.domain.repository.RecordsRepository
import com.littlebridge.enrollplus.feature.admin.domain.repository.AdminDashboardRepository
import com.littlebridge.enrollplus.feature.teacher.domain.model.PaceSnapshotsResponse
import com.littlebridge.enrollplus.feature.teacher.domain.model.PaceAlertsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AdminRecordsViewModel(
    private val recordsRepository: RecordsRepository,
    private val dashboardRepository: AdminDashboardRepository,
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {

    private val _coverageState = MutableStateFlow<UiState<PaceSnapshotsResponse>>(UiState.Loading)
    val coverageState: StateFlow<UiState<PaceSnapshotsResponse>> = _coverageState.asStateFlow()

    private val _paceState = MutableStateFlow<UiState<PaceAlertsResponse>>(UiState.Loading)
    val paceState: StateFlow<UiState<PaceAlertsResponse>> = _paceState.asStateFlow()

    private val _attendanceState = MutableStateFlow<UiState<AttendanceSummaryDto>>(UiState.Loading)
    val attendanceState: StateFlow<UiState<AttendanceSummaryDto>> = _attendanceState.asStateFlow()

    private val _marksState = MutableStateFlow<UiState<MarksSummaryDto>>(UiState.Loading)
    val marksState: StateFlow<UiState<MarksSummaryDto>> = _marksState.asStateFlow()

    private val _feeState = MutableStateFlow<UiState<FeeLedgerDto>>(UiState.Loading)
    val feeState: StateFlow<UiState<FeeLedgerDto>> = _feeState.asStateFlow()

    private suspend fun token(): String? = preferenceRepository.getUserToken().first()

    fun loadCoverage() {
        viewModelScope.launch {
            _coverageState.value = UiState.Loading
            val t = token() ?: run {
                _coverageState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = dashboardRepository.getPaceCoverage(t)) {
                is NetworkResult.Success -> _coverageState.value = UiState.Success(res.data)
                is NetworkResult.Error -> _coverageState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _coverageState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadPace() {
        viewModelScope.launch {
            _paceState.value = UiState.Loading
            val t = token() ?: run {
                _paceState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = dashboardRepository.getPaceAlerts(t)) {
                is NetworkResult.Success -> _paceState.value = UiState.Success(res.data)
                is NetworkResult.Error -> _paceState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _paceState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadAttendance() {
        viewModelScope.launch {
            _attendanceState.value = UiState.Loading
            val t = token() ?: run {
                _attendanceState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = recordsRepository.getAttendanceSummary(t)) {
                is NetworkResult.Success -> _attendanceState.value = res.data.data?.let { UiState.Success(it) } ?: UiState.Error("No data")
                is NetworkResult.Error -> _attendanceState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _attendanceState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadMarks() {
        viewModelScope.launch {
            _marksState.value = UiState.Loading
            val t = token() ?: run {
                _marksState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = recordsRepository.getMarksSummary(t)) {
                is NetworkResult.Success -> _marksState.value = res.data.data?.let { UiState.Success(it) } ?: UiState.Error("No data")
                is NetworkResult.Error -> _marksState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _marksState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadFees() {
        viewModelScope.launch {
            _feeState.value = UiState.Loading
            val t = token() ?: run {
                _feeState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = recordsRepository.getFeeLedger(t)) {
                is NetworkResult.Success -> _feeState.value = res.data.data?.let { UiState.Success(it) } ?: UiState.Error("No data")
                is NetworkResult.Error -> _feeState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _feeState.value = UiState.Error("Connection error")
            }
        }
    }

    fun refresh() {
        loadCoverage()
        loadPace()
        loadAttendance()
        loadMarks()
        loadFees()
    }
}
