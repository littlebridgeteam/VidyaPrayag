package com.littlebridge.enrollplus.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.state.SelectedChildHolder
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.feature.parent.domain.model.*
import com.littlebridge.enrollplus.feature.event.domain.model.ParentEventDto
import com.littlebridge.enrollplus.feature.event.domain.model.ParentEventListResponse
import com.littlebridge.enrollplus.feature.event.domain.repository.EventRegistrationRepository
import com.littlebridge.enrollplus.feature.health.domain.model.ParentHealthResponse
import com.littlebridge.enrollplus.feature.health.domain.repository.HealthRepository
import com.littlebridge.enrollplus.feature.idcard.domain.model.IdCardDto
import com.littlebridge.enrollplus.feature.idcard.domain.repository.IdCardRepository
import com.littlebridge.enrollplus.feature.library.domain.model.LibraryBookDto
import com.littlebridge.enrollplus.feature.library.domain.model.LibraryIssueDto
import com.littlebridge.enrollplus.feature.library.domain.repository.LibraryRepository
import com.littlebridge.enrollplus.feature.parent.domain.repository.ParentRepository
import com.littlebridge.enrollplus.feature.schools.data.remote.KtorSchoolApi
import com.littlebridge.enrollplus.feature.schools.data.remote.DiscoveredSchoolDto
import com.littlebridge.enrollplus.feature.transport.domain.model.RouteProgress
import com.littlebridge.enrollplus.feature.transport.domain.model.TransportRoute
import com.littlebridge.enrollplus.feature.transport.domain.repository.TransportRepository
import com.littlebridge.enrollplus.feature.tutor.domain.model.DoubtRequest
import com.littlebridge.enrollplus.feature.tutor.domain.model.DoubtResponse
import com.littlebridge.enrollplus.feature.tutor.domain.model.ProgressCardResponse
import com.littlebridge.enrollplus.feature.tutor.domain.model.SubjectItemDto
import com.littlebridge.enrollplus.feature.tutor.domain.repository.TutorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private fun <T> NetworkResult<T>.toUiState(): UiState<T> = when (this) {
    is NetworkResult.Success -> UiState.Success(data)
    is NetworkResult.Error -> UiState.Error(message)
    is NetworkResult.ConnectionError -> UiState.Error("No internet connection")
}

class ParentViewModel(
    private val parentRepository: ParentRepository,
    private val selectedChildHolder: SelectedChildHolder,
    private val healthRepository: HealthRepository,
    private val transportRepository: TransportRepository,
    private val idCardRepository: IdCardRepository,
    private val eventRegistrationApi: EventRegistrationRepository,
    private val libraryRepository: LibraryRepository,
    private val tutorRepository: TutorRepository,
    private val schoolApi: KtorSchoolApi,
) : ViewModel() {

    val selectedChildId: StateFlow<String?> = selectedChildHolder.selectedChildId

    // ── Dashboard ──
    private val _dashboardState = MutableStateFlow<UiState<ParentDashboardData>>(UiState.Loading)
    val dashboardState: StateFlow<UiState<ParentDashboardData>> = _dashboardState.asStateFlow()

    // ── Children list (from dashboard) ──
    private val _children = MutableStateFlow<List<DashboardChildSummary>>(emptyList())
    val children: StateFlow<List<DashboardChildSummary>> = _children.asStateFlow()

    // ── Unlinked state ──
    private val _isUnlinked = MutableStateFlow(false)
    val isUnlinked: StateFlow<Boolean> = _isUnlinked.asStateFlow()

    // ── Fees ──
    private val _feesState = MutableStateFlow<UiState<FeeData>>(UiState.Loading)
    val feesState: StateFlow<UiState<FeeData>> = _feesState.asStateFlow()

    // ── Attendance ──
    private val _attendanceState = MutableStateFlow<UiState<ParentAttendanceData>>(UiState.Loading)
    val attendanceState: StateFlow<UiState<ParentAttendanceData>> = _attendanceState.asStateFlow()

    // ── Marks ──
    private val _marksState = MutableStateFlow<UiState<ParentMarksData>>(UiState.Loading)
    val marksState: StateFlow<UiState<ParentMarksData>> = _marksState.asStateFlow()

    // ── Syllabus ──
    private val _syllabusState = MutableStateFlow<UiState<ParentSyllabusData>>(UiState.Loading)
    val syllabusState: StateFlow<UiState<ParentSyllabusData>> = _syllabusState.asStateFlow()

    // ── Daily Summary ──
    private val _dailySummaryState = MutableStateFlow<UiState<ParentDailySummaryData>>(UiState.Loading)
    val dailySummaryState: StateFlow<UiState<ParentDailySummaryData>> = _dailySummaryState.asStateFlow()

    // ── Timetable ──
    private val _timetableState = MutableStateFlow<UiState<ParentTimetableData>>(UiState.Loading)
    val timetableState: StateFlow<UiState<ParentTimetableData>> = _timetableState.asStateFlow()

    // ── Announcements ──
    private val _announcementsState = MutableStateFlow<UiState<ParentAnnouncementsData>>(UiState.Loading)
    val announcementsState: StateFlow<UiState<ParentAnnouncementsData>> = _announcementsState.asStateFlow()

    // ── Notifications ──
    private val _notificationsState = MutableStateFlow<UiState<ParentNotificationsData>>(UiState.Loading)
    val notificationsState: StateFlow<UiState<ParentNotificationsData>> = _notificationsState.asStateFlow()

    // ── Unread message count ──
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    // ── Message threads ──
    private val _threadsState = MutableStateFlow<UiState<ParentMessageThreadsData>>(UiState.Loading)
    val threadsState: StateFlow<UiState<ParentMessageThreadsData>> = _threadsState.asStateFlow()

    // ── Thread messages ──
    private val _threadMessagesState = MutableStateFlow<UiState<ParentThreadMessagesData>>(UiState.Loading)
    val threadMessagesState: StateFlow<UiState<ParentThreadMessagesData>> = _threadMessagesState.asStateFlow()

    // ── Leave requests ──
    private val _leaveState = MutableStateFlow<UiState<ParentLeaveListData>>(UiState.Loading)
    val leaveState: StateFlow<UiState<ParentLeaveListData>> = _leaveState.asStateFlow()

    // ── Track progress ──
    private val _trackProgressState = MutableStateFlow<UiState<TrackProgressData>>(UiState.Loading)
    val trackProgressState: StateFlow<UiState<TrackProgressData>> = _trackProgressState.asStateFlow()

    // ── Scholarships ──
    private val _scholarshipsState = MutableStateFlow<UiState<ScholarshipsData>>(UiState.Loading)
    val scholarshipsState: StateFlow<UiState<ScholarshipsData>> = _scholarshipsState.asStateFlow()

    // ── Pulse ──
    private val _pulseState = MutableStateFlow<UiState<PulseDto>>(UiState.Loading)
    val pulseState: StateFlow<UiState<PulseDto>> = _pulseState.asStateFlow()

    // ── School search ──
    private val _schoolSearchState = MutableStateFlow<UiState<SchoolSearchData>>(UiState.Loading)
    val schoolSearchState: StateFlow<UiState<SchoolSearchData>> = _schoolSearchState.asStateFlow()

    // ── Quiz list ──
    private val _quizListState = MutableStateFlow<UiState<ParentQuizListData>>(UiState.Loading)
    val quizListState: StateFlow<UiState<ParentQuizListData>> = _quizListState.asStateFlow()

    // ── Syllabus V2 ──
    private val _syllabusV2State = MutableStateFlow<UiState<ParentSyllabusV2Data>>(UiState.Loading)
    val syllabusV2State: StateFlow<UiState<ParentSyllabusV2Data>> = _syllabusV2State.asStateFlow()

    // ── Health ──
    private val _healthState = MutableStateFlow<UiState<ParentHealthResponse>>(UiState.Loading)
    val healthState: StateFlow<UiState<ParentHealthResponse>> = _healthState.asStateFlow()

    // ── Transport ──
    private val _transportLiveState = MutableStateFlow<UiState<RouteProgress>>(UiState.Loading)
    val transportLiveState: StateFlow<UiState<RouteProgress>> = _transportLiveState.asStateFlow()
    private val _transportRouteState = MutableStateFlow<UiState<TransportRoute>>(UiState.Loading)
    val transportRouteState: StateFlow<UiState<TransportRoute>> = _transportRouteState.asStateFlow()

    // ── ID Card ──
    private val _idCardState = MutableStateFlow<UiState<IdCardDto>>(UiState.Loading)
    val idCardState: StateFlow<UiState<IdCardDto>> = _idCardState.asStateFlow()

    // ── Events ──
    private val _eventsState = MutableStateFlow<UiState<List<ParentEventDto>>>(UiState.Loading)
    val eventsState: StateFlow<UiState<List<ParentEventDto>>> = _eventsState.asStateFlow()

    // ── Library ──
    private val _librarySearchState = MutableStateFlow<UiState<List<LibraryBookDto>>>(UiState.Loading)
    val librarySearchState: StateFlow<UiState<List<LibraryBookDto>>> = _librarySearchState.asStateFlow()
    private val _libraryIssuedState = MutableStateFlow<UiState<List<LibraryIssueDto>>>(UiState.Loading)
    val libraryIssuedState: StateFlow<UiState<List<LibraryIssueDto>>> = _libraryIssuedState.asStateFlow()

    // ── Tutor ──
    private val _tutorSubjectsState = MutableStateFlow<UiState<List<SubjectItemDto>>>(UiState.Loading)
    val tutorSubjectsState: StateFlow<UiState<List<SubjectItemDto>>> = _tutorSubjectsState.asStateFlow()
    private val _tutorDoubtState = MutableStateFlow<UiState<DoubtResponse>>(UiState.Loading)
    val tutorDoubtState: StateFlow<UiState<DoubtResponse>> = _tutorDoubtState.asStateFlow()
    private val _tutorProgressState = MutableStateFlow<UiState<ProgressCardResponse>>(UiState.Loading)
    val tutorProgressState: StateFlow<UiState<ProgressCardResponse>> = _tutorProgressState.asStateFlow()

    // ── School Discovery ──
    private val _schoolDiscoveryState = MutableStateFlow<UiState<List<DiscoveredSchoolDto>>>(UiState.Loading)
    val schoolDiscoveryState: StateFlow<UiState<List<DiscoveredSchoolDto>>> = _schoolDiscoveryState.asStateFlow()

    private var token: String? = null

    fun setToken(t: String) {
        token = t
    }

    private fun requireToken(): String = token ?: throw IllegalStateException("ParentViewModel: token not set")

    fun loadDashboard() {
        viewModelScope.launch {
            _dashboardState.value = UiState.Loading
            val result = parentRepository.getDashboard(requireToken())
            if (result is NetworkResult.Success) {
                val data = result.data.data
                val kids = data.children.ifEmpty { listOfNotNull(data.childSummary) }
                _children.value = kids
                _isUnlinked.value = kids.isEmpty()
                if (kids.isNotEmpty()) {
                    selectedChildHolder.selectIfUnset(kids.first().id)
                }
                _dashboardState.value = UiState.Success(data)
            } else {
                _dashboardState.value = result.toUiState().let { it as UiState<ParentDashboardData> }
            }
        }
    }

    fun selectChild(childId: String) {
        selectedChildHolder.select(childId)
        loadChildData()
    }

    fun loadChildData() {
        val childId = selectedChildHolder.selectedChildId.value ?: return
        loadAttendance(childId)
        loadMarks(childId)
        loadSyllabus(childId)
        loadDailySummary(childId)
        loadTimetable(childId)
        loadFees(childId)
        loadTrackProgress()
        loadQuizList(childId)
        loadSyllabusV2(childId)
    }

    fun loadAll() {
        viewModelScope.launch {
            loadDashboard()
            loadAnnouncements()
            loadNotifications()
            loadUnreadCount()
            loadLeaveRequests()
            loadScholarships()
            loadMessageThreads()
        }
    }

    fun loadFees(childId: String? = selectedChildHolder.selectedChildId.value) {
        viewModelScope.launch {
            _feesState.value = UiState.Loading
            val result = parentRepository.getFees(requireToken(), childId)
            _feesState.value = if (result is NetworkResult.Success) UiState.Success(result.data.data) else result.toUiState().let { it as UiState<FeeData> }
        }
    }

    fun loadAttendance(childId: String) {
        viewModelScope.launch {
            _attendanceState.value = UiState.Loading
            val result = parentRepository.getChildAttendance(requireToken(), childId)
            _attendanceState.value = if (result is NetworkResult.Success) UiState.Success(result.data.data) else result.toUiState().let { it as UiState<ParentAttendanceData> }
        }
    }

    fun loadMarks(childId: String) {
        viewModelScope.launch {
            _marksState.value = UiState.Loading
            val result = parentRepository.getChildMarks(requireToken(), childId)
            _marksState.value = if (result is NetworkResult.Success) UiState.Success(result.data.data) else result.toUiState().let { it as UiState<ParentMarksData> }
        }
    }

    fun loadSyllabus(childId: String) {
        viewModelScope.launch {
            _syllabusState.value = UiState.Loading
            val result = parentRepository.getChildSyllabus(requireToken(), childId)
            _syllabusState.value = if (result is NetworkResult.Success) UiState.Success(result.data.data) else result.toUiState().let { it as UiState<ParentSyllabusData> }
        }
    }

    fun loadSyllabusV2(childId: String) {
        viewModelScope.launch {
            _syllabusV2State.value = UiState.Loading
            val result = parentRepository.getSyllabusV2(requireToken(), childId)
            _syllabusV2State.value = if (result is NetworkResult.Success) UiState.Success(result.data.data) else result.toUiState().let { it as UiState<ParentSyllabusV2Data> }
        }
    }

    fun loadDailySummary(childId: String, date: String? = null) {
        viewModelScope.launch {
            _dailySummaryState.value = UiState.Loading
            val result = parentRepository.getDailySummary(requireToken(), childId, date)
            _dailySummaryState.value = if (result is NetworkResult.Success) UiState.Success(result.data.data) else result.toUiState().let { it as UiState<ParentDailySummaryData> }
        }
    }

    fun loadTimetable(childId: String) {
        viewModelScope.launch {
            _timetableState.value = UiState.Loading
            val result = parentRepository.getChildTimetable(requireToken(), childId)
            _timetableState.value = if (result is NetworkResult.Success) UiState.Success(result.data.data) else result.toUiState().let { it as UiState<ParentTimetableData> }
        }
    }

    fun loadAnnouncements() {
        viewModelScope.launch {
            _announcementsState.value = UiState.Loading
            val result = parentRepository.getAnnouncements(requireToken())
            _announcementsState.value = if (result is NetworkResult.Success) UiState.Success(result.data.data) else result.toUiState().let { it as UiState<ParentAnnouncementsData> }
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _notificationsState.value = UiState.Loading
            val result = parentRepository.getNotifications(requireToken())
            _notificationsState.value = if (result is NetworkResult.Success) UiState.Success(result.data.data) else result.toUiState().let { it as UiState<ParentNotificationsData> }
        }
    }

    fun loadUnreadCount() {
        viewModelScope.launch {
            val result = parentRepository.getUnreadCount(requireToken())
            if (result is NetworkResult.Success) _unreadCount.value = result.data
        }
    }

    fun loadMessageThreads() {
        viewModelScope.launch {
            _threadsState.value = UiState.Loading
            val result = parentRepository.getMessageThreads(requireToken())
            _threadsState.value = if (result is NetworkResult.Success) UiState.Success(result.data.data) else result.toUiState().let { it as UiState<ParentMessageThreadsData> }
        }
    }

    fun loadThreadMessages(threadId: String) {
        viewModelScope.launch {
            _threadMessagesState.value = UiState.Loading
            val result = parentRepository.getThreadMessages(requireToken(), threadId)
            if (result is NetworkResult.Success) {
                result.data.data?.let { _threadMessagesState.value = UiState.Success(it) }
            } else {
                _threadMessagesState.value = UiState.Error(if (result is NetworkResult.Error) result.message else "No internet connection")
            }
            parentRepository.markThreadRead(requireToken(), threadId)
            loadUnreadCount()
        }
    }

    fun sendMessage(threadId: String?, body: String, recipientUserId: String? = null) {
        viewModelScope.launch {
            val request = ParentSendMessageRequest(
                threadId = threadId,
                recipientUserId = recipientUserId,
                body = body,
            )
            parentRepository.sendMessage(requireToken(), request)
            if (threadId != null) loadThreadMessages(threadId)
        }
    }

    fun loadLeaveRequests() {
        viewModelScope.launch {
            _leaveState.value = UiState.Loading
            val result = parentRepository.getLeaveRequests(requireToken())
            _leaveState.value = if (result is NetworkResult.Success) UiState.Success(result.data.data) else result.toUiState().let { it as UiState<ParentLeaveListData> }
        }
    }

    fun applyLeave(childId: String, dateFrom: String, dateTo: String, reason: String) {
        viewModelScope.launch {
            val request = CreateParentLeaveRequest(
                childId = childId,
                dateFrom = dateFrom,
                dateTo = dateTo,
                reason = reason,
            )
            parentRepository.applyLeave(requireToken(), request)
            loadLeaveRequests()
        }
    }

    fun loadTrackProgress() {
        viewModelScope.launch {
            _trackProgressState.value = UiState.Loading
            val result = parentRepository.getTrackProgress(requireToken())
            _trackProgressState.value = if (result is NetworkResult.Success) UiState.Success(result.data.data) else result.toUiState().let { it as UiState<TrackProgressData> }
        }
    }

    fun loadScholarships() {
        viewModelScope.launch {
            _scholarshipsState.value = UiState.Loading
            val result = parentRepository.getScholarships(requireToken())
            _scholarshipsState.value = if (result is NetworkResult.Success) UiState.Success(result.data.data) else result.toUiState().let { it as UiState<ScholarshipsData> }
        }
    }

    fun loadPulse(childId: String) {
        viewModelScope.launch {
            _pulseState.value = UiState.Loading
            val result = parentRepository.getLatestPulse(requireToken(), childId)
            _pulseState.value = if (result is NetworkResult.Success) UiState.Success(result.data.data) else result.toUiState().let { it as UiState<PulseDto> }
        }
    }

    fun searchSchools(query: String) {
        viewModelScope.launch {
            _schoolSearchState.value = UiState.Loading
            val result = parentRepository.searchSchools(requireToken(), query)
            _schoolSearchState.value = if (result is NetworkResult.Success) UiState.Success(result.data.data) else result.toUiState().let { it as UiState<SchoolSearchData> }
        }
    }

    fun linkChild(schoolId: String, rollNumber: String, className: String?, section: String?, childName: String?, parentPhone: String?, parentName: String?, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val request = LinkChildRequest(
                schoolId = schoolId,
                rollNumber = rollNumber,
                className = className,
                section = section,
                childName = childName,
                parentPhone = parentPhone,
                parentName = parentName,
            )
            val result = parentRepository.linkChild(requireToken(), request)
            if (result is NetworkResult.Success) {
                onResult(true, result.data.data.childName)
                loadDashboard()
            } else {
                onResult(false, if (result is NetworkResult.Error) result.message else "No internet connection")
            }
        }
    }

    fun loadQuizList(childId: String) {
        viewModelScope.launch {
            _quizListState.value = UiState.Loading
            val result = parentRepository.getQuizList(requireToken(), childId)
            _quizListState.value = if (result is NetworkResult.Success) UiState.Success(result.data.data) else result.toUiState().let { it as UiState<ParentQuizListData> }
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            parentRepository.markNotificationRead(requireToken(), id)
            loadNotifications()
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            parentRepository.markAllNotificationsRead(requireToken())
            loadNotifications()
        }
    }

    fun clearReadNotifications() {
        viewModelScope.launch {
            parentRepository.clearReadNotifications(requireToken())
            loadNotifications()
        }
    }

    // ── Cross-feature overlay data ──

    fun loadHealth(childId: String) {
        viewModelScope.launch {
            _healthState.value = UiState.Loading
            val result = healthRepository.getChildHealth(requireToken(), childId)
            _healthState.value = if (result is NetworkResult.Success) result.data.data?.let { UiState.Success(it) } ?: UiState.Error("No data") else result.toUiState().let { it as UiState<ParentHealthResponse> }
        }
    }

    fun loadTransportLive(childId: String) {
        viewModelScope.launch {
            _transportLiveState.value = UiState.Loading
            val result = transportRepository.getLiveLocation(requireToken(), childId)
            _transportLiveState.value = if (result is NetworkResult.Success) result.data.data?.let { UiState.Success(it) } ?: UiState.Error("No data") else result.toUiState().let { it as UiState<RouteProgress> }
        }
    }

    fun loadTransportRoute(childId: String) {
        viewModelScope.launch {
            _transportRouteState.value = UiState.Loading
            val result = transportRepository.getRouteForChild(requireToken(), childId)
            _transportRouteState.value = if (result is NetworkResult.Success) result.data.data?.let { UiState.Success(it) } ?: UiState.Error("No data") else result.toUiState().let { it as UiState<TransportRoute> }
        }
    }

    fun loadIdCard(childId: String) {
        viewModelScope.launch {
            _idCardState.value = UiState.Loading
            val result = idCardRepository.getChildIdCard(requireToken(), childId)
            _idCardState.value = if (result is NetworkResult.Success) result.data.data?.let { UiState.Success(it) } ?: UiState.Error("No data") else result.toUiState().let { it as UiState<IdCardDto> }
        }
    }

    fun loadEvents() {
        viewModelScope.launch {
            _eventsState.value = UiState.Loading
            val result = eventRegistrationApi.listParentEvents(requireToken())
            _eventsState.value = if (result is NetworkResult.Success) result.data.data?.let { UiState.Success(it.events) } ?: UiState.Error("No data") else result.toUiState().let { it as UiState<List<ParentEventDto>> }
        }
    }

    // ── Library ──

    fun searchLibraryBooks(query: String) {
        viewModelScope.launch {
            _librarySearchState.value = UiState.Loading
            val result = libraryRepository.parentSearchBooks(requireToken(), query, page = 1, limit = 20)
            _librarySearchState.value = if (result is NetworkResult.Success) UiState.Success(result.data.data) else result.toUiState().let { it as UiState<List<LibraryBookDto>> }
        }
    }

    fun loadLibraryIssued(childId: String) {
        viewModelScope.launch {
            _libraryIssuedState.value = UiState.Loading
            val result = libraryRepository.parentGetIssuedForChild(requireToken(), childId)
            _libraryIssuedState.value = if (result is NetworkResult.Success) UiState.Success(result.data.data) else result.toUiState().let { it as UiState<List<LibraryIssueDto>> }
        }
    }

    // ── Tutor ──

    fun loadTutorSubjects(childId: String) {
        viewModelScope.launch {
            _tutorSubjectsState.value = UiState.Loading
            val result = tutorRepository.getSubjects(requireToken(), childId)
            _tutorSubjectsState.value = when (result) {
                is NetworkResult.Success -> UiState.Success(result.data.data ?: emptyList())
                is NetworkResult.Error -> UiState.Error(result.message)
                is NetworkResult.ConnectionError -> UiState.Error("No internet connection")
            }
        }
    }

    fun askDoubt(childId: String, subjectId: String, question: String) {
        viewModelScope.launch {
            _tutorDoubtState.value = UiState.Loading
            val result = tutorRepository.askDoubt(requireToken(), DoubtRequest(childId = childId, subjectId = subjectId, question = question))
            _tutorDoubtState.value = when (result) {
                is NetworkResult.Success -> UiState.Success(result.data)
                is NetworkResult.Error -> UiState.Error(result.message)
                is NetworkResult.ConnectionError -> UiState.Error("No internet connection")
            }
        }
    }

    fun loadTutorProgress(childId: String, subjectId: String) {
        viewModelScope.launch {
            _tutorProgressState.value = UiState.Loading
            val result = tutorRepository.getProgressCard(requireToken(), childId, subjectId)
            _tutorProgressState.value = when (result) {
                is NetworkResult.Success -> UiState.Success(result.data)
                is NetworkResult.Error -> UiState.Error(result.message)
                is NetworkResult.ConnectionError -> UiState.Error("No internet connection")
            }
        }
    }

    // ── School Discovery ──

    fun discoverSchools() {
        viewModelScope.launch {
            _schoolDiscoveryState.value = UiState.Loading
            val result = schoolApi.discoverSchools(requireToken())
            _schoolDiscoveryState.value = if (result is NetworkResult.Success) UiState.Success(result.data.data?.schools ?: emptyList()) else result.toUiState().let { it as UiState<List<DiscoveredSchoolDto>> }
        }
    }
}
