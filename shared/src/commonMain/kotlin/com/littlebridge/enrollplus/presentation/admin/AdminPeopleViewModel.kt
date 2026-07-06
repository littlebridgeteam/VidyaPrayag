package com.littlebridge.enrollplus.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherCardListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.StudentListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.StaffListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.LinkRequestsResponse
import com.littlebridge.enrollplus.feature.admin.domain.repository.TeachersRepository
import com.littlebridge.enrollplus.feature.admin.domain.repository.StudentsRepository
import com.littlebridge.enrollplus.feature.admin.domain.repository.StaffRepository
import com.littlebridge.enrollplus.feature.admin.domain.repository.LinkRequestsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AdminPeopleViewModel(
    private val teachersRepository: TeachersRepository,
    private val studentsRepository: StudentsRepository,
    private val staffRepository: StaffRepository,
    private val linkRequestsRepository: LinkRequestsRepository,
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {

    private val _teachersState = MutableStateFlow<UiState<TeacherCardListResponse>>(UiState.Loading)
    val teachersState: StateFlow<UiState<TeacherCardListResponse>> = _teachersState.asStateFlow()

    private val _studentsState = MutableStateFlow<UiState<StudentListResponse>>(UiState.Loading)
    val studentsState: StateFlow<UiState<StudentListResponse>> = _studentsState.asStateFlow()

    private val _staffState = MutableStateFlow<UiState<StaffListResponse>>(UiState.Loading)
    val staffState: StateFlow<UiState<StaffListResponse>> = _staffState.asStateFlow()

    private val _linkRequestsState = MutableStateFlow<UiState<LinkRequestsResponse>>(UiState.Loading)
    val linkRequestsState: StateFlow<UiState<LinkRequestsResponse>> = _linkRequestsState.asStateFlow()

    private suspend fun token(): String? = preferenceRepository.getUserToken().first()

    fun loadTeachers() {
        viewModelScope.launch {
            _teachersState.value = UiState.Loading
            val t = token() ?: run {
                _teachersState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = teachersRepository.getTeachers(t)) {
                is NetworkResult.Success -> _teachersState.value = res.data.data?.let { UiState.Success(it) } ?: UiState.Error("No data")
                is NetworkResult.Error -> _teachersState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _teachersState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadStudents() {
        viewModelScope.launch {
            _studentsState.value = UiState.Loading
            val t = token() ?: run {
                _studentsState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = studentsRepository.getStudents(t)) {
                is NetworkResult.Success -> _studentsState.value = res.data.data?.let { UiState.Success(it) } ?: UiState.Error("No data")
                is NetworkResult.Error -> _studentsState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _studentsState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadStaff() {
        viewModelScope.launch {
            _staffState.value = UiState.Loading
            val t = token() ?: run {
                _staffState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = staffRepository.getStaff(t)) {
                is NetworkResult.Success -> _staffState.value = res.data.data?.let { UiState.Success(it) } ?: UiState.Error("No data")
                is NetworkResult.Error -> _staffState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _staffState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadLinkRequests() {
        viewModelScope.launch {
            _linkRequestsState.value = UiState.Loading
            val t = token() ?: run {
                _linkRequestsState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = linkRequestsRepository.getLinkRequests(t)) {
                is NetworkResult.Success -> _linkRequestsState.value = res.data.data?.let { UiState.Success(it) } ?: UiState.Error("No data")
                is NetworkResult.Error -> _linkRequestsState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _linkRequestsState.value = UiState.Error("Connection error")
            }
        }
    }

    fun refresh() {
        loadTeachers()
        loadStudents()
        loadStaff()
        loadLinkRequests()
    }
}
