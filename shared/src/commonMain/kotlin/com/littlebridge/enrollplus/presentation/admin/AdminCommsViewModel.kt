package com.littlebridge.enrollplus.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.feature.admin.domain.model.AnnouncementListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.MessageThread
import com.littlebridge.enrollplus.feature.admin.domain.model.PtmResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.LeaveRequestsResponse
import com.littlebridge.enrollplus.feature.admin.domain.repository.AnnouncementsRepository
import com.littlebridge.enrollplus.feature.admin.domain.repository.MessagesRepository
import com.littlebridge.enrollplus.feature.admin.domain.repository.PtmRepository
import com.littlebridge.enrollplus.feature.admin.domain.repository.LeaveRequestsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AdminCommsViewModel(
    private val announcementsRepository: AnnouncementsRepository,
    private val messagesRepository: MessagesRepository,
    private val ptmRepository: PtmRepository,
    private val leaveRequestsRepository: LeaveRequestsRepository,
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {

    private val _announcementsState = MutableStateFlow<UiState<AnnouncementListResponse>>(UiState.Loading)
    val announcementsState: StateFlow<UiState<AnnouncementListResponse>> = _announcementsState.asStateFlow()

    private val _messagesState = MutableStateFlow<UiState<List<MessageThread>>>(UiState.Loading)
    val messagesState: StateFlow<UiState<List<MessageThread>>> = _messagesState.asStateFlow()

    private val _ptmState = MutableStateFlow<UiState<PtmResponse>>(UiState.Loading)
    val ptmState: StateFlow<UiState<PtmResponse>> = _ptmState.asStateFlow()

    private val _leaveState = MutableStateFlow<UiState<LeaveRequestsResponse>>(UiState.Loading)
    val leaveState: StateFlow<UiState<LeaveRequestsResponse>> = _leaveState.asStateFlow()

    private suspend fun token(): String? = preferenceRepository.getUserToken().first()

    fun loadAnnouncements() {
        viewModelScope.launch {
            _announcementsState.value = UiState.Loading
            val t = token() ?: run {
                _announcementsState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = announcementsRepository.getAnnouncements(t)) {
                is NetworkResult.Success -> _announcementsState.value = res.data.data?.let { UiState.Success(it) } ?: UiState.Error("No data")
                is NetworkResult.Error -> _announcementsState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _announcementsState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadMessages() {
        viewModelScope.launch {
            _messagesState.value = UiState.Loading
            val t = token() ?: run {
                _messagesState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = messagesRepository.getThreads(t)) {
                is NetworkResult.Success -> _messagesState.value = UiState.Success(res.data)
                is NetworkResult.Error -> _messagesState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _messagesState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadPtm() {
        viewModelScope.launch {
            _ptmState.value = UiState.Loading
            val t = token() ?: run {
                _ptmState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = ptmRepository.getPtm(t)) {
                is NetworkResult.Success -> _ptmState.value = res.data.data?.let { UiState.Success(it) } ?: UiState.Error("No data")
                is NetworkResult.Error -> _ptmState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _ptmState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadLeaveRequests() {
        viewModelScope.launch {
            _leaveState.value = UiState.Loading
            val t = token() ?: run {
                _leaveState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = leaveRequestsRepository.getLeaveRequests(t)) {
                is NetworkResult.Success -> _leaveState.value = res.data.data?.let { UiState.Success(it) } ?: UiState.Error("No data")
                is NetworkResult.Error -> _leaveState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _leaveState.value = UiState.Error("Connection error")
            }
        }
    }

    fun refresh() {
        loadAnnouncements()
        loadMessages()
        loadPtm()
        loadLeaveRequests()
    }
}
