package com.littlebridge.enrollplus.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.notification.NotificationFeedRepository
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.feature.health.domain.model.HealthAlertsResponse
import com.littlebridge.enrollplus.feature.health.domain.repository.HealthRepository
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentNotificationDto
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentNotificationsResponse
import com.littlebridge.enrollplus.feature.pews.domain.model.PewsInterventionDto
import com.littlebridge.enrollplus.feature.pews.domain.model.PewsStudentDto
import com.littlebridge.enrollplus.feature.pews.domain.repository.PewsRepository
import com.littlebridge.enrollplus.feature.teacher.domain.model.AssessmentListResponse
import com.littlebridge.enrollplus.feature.teacher.domain.model.AttendanceLoadResponse
import com.littlebridge.enrollplus.feature.teacher.domain.model.CheckInStatusResponse
import com.littlebridge.enrollplus.feature.teacher.domain.model.HomeworkListResponse
import com.littlebridge.enrollplus.feature.teacher.domain.model.LessonPlanListResponse
import com.littlebridge.enrollplus.feature.teacher.domain.model.ResolvedDayResponse
import com.littlebridge.enrollplus.feature.teacher.domain.model.ResolvedWeekResponse
import com.littlebridge.enrollplus.feature.teacher.domain.model.StudentProfileResponse
import com.littlebridge.enrollplus.feature.teacher.domain.model.SyllabusLoadResponse
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherClassesV2Response
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherClassSummaryDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherLeaveListResponse
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherMessageThreadsResponse
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherObligationsResponse
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherProfileResponse
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherSelfLeaveListResponse
import com.littlebridge.enrollplus.feature.teacher.domain.repository.TeacherRepository
import com.littlebridge.enrollplus.feature.admin.domain.model.ChangeRequestListResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TeacherViewModel(
    private val teacherRepository: TeacherRepository,
    private val healthRepository: HealthRepository,
    private val pewsRepository: PewsRepository,
    private val preferenceRepository: PreferenceRepository,
    private val notificationRepository: NotificationFeedRepository,
) : ViewModel() {

    // ── Today / Home ──────────────────────────────────────────────────────────
    private val _dayState = MutableStateFlow<UiState<ResolvedDayResponse>>(UiState.Loading)
    val dayState: StateFlow<UiState<ResolvedDayResponse>> = _dayState.asStateFlow()

    private val _obligationsState = MutableStateFlow<UiState<TeacherObligationsResponse>>(UiState.Loading)
    val obligationsState: StateFlow<UiState<TeacherObligationsResponse>> = _obligationsState.asStateFlow()

    private val _checkInState = MutableStateFlow<UiState<CheckInStatusResponse>>(UiState.Loading)
    val checkInState: StateFlow<UiState<CheckInStatusResponse>> = _checkInState.asStateFlow()

    // ── Classes ────────────────────────────────────────────────────────────────
    private val _classesState = MutableStateFlow<UiState<TeacherClassesV2Response>>(UiState.Loading)
    val classesState: StateFlow<UiState<TeacherClassesV2Response>> = _classesState.asStateFlow()

    // ── Timetable ──────────────────────────────────────────────────────────────
    private val _weekState = MutableStateFlow<UiState<ResolvedWeekResponse>>(UiState.Loading)
    val weekState: StateFlow<UiState<ResolvedWeekResponse>> = _weekState.asStateFlow()

    private val _changeRequestsState = MutableStateFlow<UiState<ChangeRequestListResponse>>(UiState.Loading)
    val changeRequestsState: StateFlow<UiState<ChangeRequestListResponse>> = _changeRequestsState.asStateFlow()

    // ── Profile ────────────────────────────────────────────────────────────────
    private val _profileState = MutableStateFlow<UiState<TeacherProfileResponse>>(UiState.Loading)
    val profileState: StateFlow<UiState<TeacherProfileResponse>> = _profileState.asStateFlow()

    private val _myLeaveState = MutableStateFlow<UiState<TeacherSelfLeaveListResponse>>(UiState.Loading)
    val myLeaveState: StateFlow<UiState<TeacherSelfLeaveListResponse>> = _myLeaveState.asStateFlow()

    // ── Update tab — per-class tools ────────────────────────────────────────────
    private val _attendanceState = MutableStateFlow<UiState<AttendanceLoadResponse>>(UiState.Loading)
    val attendanceState: StateFlow<UiState<AttendanceLoadResponse>> = _attendanceState.asStateFlow()

    private val _assessmentsState = MutableStateFlow<UiState<AssessmentListResponse>>(UiState.Loading)
    val assessmentsState: StateFlow<UiState<AssessmentListResponse>> = _assessmentsState.asStateFlow()

    private val _syllabusState = MutableStateFlow<UiState<SyllabusLoadResponse>>(UiState.Loading)
    val syllabusState: StateFlow<UiState<SyllabusLoadResponse>> = _syllabusState.asStateFlow()

    private val _homeworkState = MutableStateFlow<UiState<HomeworkListResponse>>(UiState.Loading)
    val homeworkState: StateFlow<UiState<HomeworkListResponse>> = _homeworkState.asStateFlow()

    private val _lessonPlansState = MutableStateFlow<UiState<LessonPlanListResponse>>(UiState.Loading)
    val lessonPlansState: StateFlow<UiState<LessonPlanListResponse>> = _lessonPlansState.asStateFlow()

    // ── Student profile drill-down ──────────────────────────────────────────────
    private val _studentProfileState = MutableStateFlow<UiState<StudentProfileResponse>>(UiState.Loading)
    val studentProfileState: StateFlow<UiState<StudentProfileResponse>> = _studentProfileState.asStateFlow()

    // ── Messages ────────────────────────────────────────────────────────────────
    private val _messageThreadsState = MutableStateFlow<UiState<TeacherMessageThreadsResponse>>(UiState.Loading)
    val messageThreadsState: StateFlow<UiState<TeacherMessageThreadsResponse>> = _messageThreadsState.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    // ── Notifications ───────────────────────────────────────────────────────────
    private val _notificationsState = MutableStateFlow<UiState<ParentNotificationsResponse>>(UiState.Loading)
    val notificationsState: StateFlow<UiState<ParentNotificationsResponse>> = _notificationsState.asStateFlow()

    // ── Leave approvals ─────────────────────────────────────────────────────────
    private val _leaveRequestsState = MutableStateFlow<UiState<TeacherLeaveListResponse>>(UiState.Loading)
    val leaveRequestsState: StateFlow<UiState<TeacherLeaveListResponse>> = _leaveRequestsState.asStateFlow()

    // ── Health alerts ───────────────────────────────────────────────────────────
    private val _healthAlertsState = MutableStateFlow<UiState<HealthAlertsResponse>>(UiState.Loading)
    val healthAlertsState: StateFlow<UiState<HealthAlertsResponse>> = _healthAlertsState.asStateFlow()

    // ── PEWS ─────────────────────────────────────────────────────────────────────
    private val _pewsStudentsState = MutableStateFlow<UiState<List<PewsStudentDto>>>(UiState.Loading)
    val pewsStudentsState: StateFlow<UiState<List<PewsStudentDto>>> = _pewsStudentsState.asStateFlow()

    private val _pewsInterventionsState = MutableStateFlow<UiState<List<PewsInterventionDto>>>(UiState.Loading)
    val pewsInterventionsState: StateFlow<UiState<List<PewsInterventionDto>>> = _pewsInterventionsState.asStateFlow()

    // ── Selected class for Update tab ───────────────────────────────────────────
    private val _selectedAssignmentId = MutableStateFlow<String?>(null)
    val selectedAssignmentId: StateFlow<String?> = _selectedAssignmentId.asStateFlow()

    private val _selectedClassSummary = MutableStateFlow<TeacherClassSummaryDto?>(null)
    val selectedClassSummary: StateFlow<TeacherClassSummaryDto?> = _selectedClassSummary.asStateFlow()

    // ─────────────────────────────────────────────────────────────────────────────
    // Token helper
    // ─────────────────────────────────────────────────────────────────────────────
    private suspend fun token(): String? = preferenceRepository.getUserToken().first()

    // ─────────────────────────────────────────────────────────────────────────────
    // Load functions
    // ─────────────────────────────────────────────────────────────────────────────

    fun loadDay(date: String? = null) {
        viewModelScope.launch {
            _dayState.value = UiState.Loading
            val t = token() ?: run {
                _dayState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = teacherRepository.getDay(t, date)) {
                is NetworkResult.Success -> _dayState.value = UiState.Success(res.data)
                is NetworkResult.Error -> _dayState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _dayState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadObligations() {
        viewModelScope.launch {
            _obligationsState.value = UiState.Loading
            val t = token() ?: run {
                _obligationsState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = teacherRepository.getObligations(t)) {
                is NetworkResult.Success -> _obligationsState.value = UiState.Success(res.data)
                is NetworkResult.Error -> _obligationsState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _obligationsState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadCheckInStatus(date: String? = null) {
        viewModelScope.launch {
            _checkInState.value = UiState.Loading
            val t = token() ?: run {
                _checkInState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = teacherRepository.getCheckInStatus(t, date)) {
                is NetworkResult.Success -> _checkInState.value = UiState.Success(res.data)
                is NetworkResult.Error -> _checkInState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _checkInState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadClasses() {
        viewModelScope.launch {
            _classesState.value = UiState.Loading
            val t = token() ?: run {
                _classesState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = teacherRepository.listClassesV2(t)) {
                is NetworkResult.Success -> _classesState.value = UiState.Success(res.data)
                is NetworkResult.Error -> _classesState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _classesState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadWeek(date: String? = null) {
        viewModelScope.launch {
            _weekState.value = UiState.Loading
            val t = token() ?: run {
                _weekState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = teacherRepository.getWeek(t, date)) {
                is NetworkResult.Success -> _weekState.value = UiState.Success(res.data)
                is NetworkResult.Error -> _weekState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _weekState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadChangeRequests() {
        viewModelScope.launch {
            _changeRequestsState.value = UiState.Loading
            val t = token() ?: run {
                _changeRequestsState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = teacherRepository.getTimetableChangeRequests(t)) {
                is NetworkResult.Success -> _changeRequestsState.value = UiState.Success(res.data)
                is NetworkResult.Error -> _changeRequestsState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _changeRequestsState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = UiState.Loading
            val t = token() ?: run {
                _profileState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = teacherRepository.getProfile(t)) {
                is NetworkResult.Success -> _profileState.value = UiState.Success(res.data)
                is NetworkResult.Error -> _profileState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _profileState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadMyLeave(status: String? = null) {
        viewModelScope.launch {
            _myLeaveState.value = UiState.Loading
            val t = token() ?: run {
                _myLeaveState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = teacherRepository.getMyLeave(t, status)) {
                is NetworkResult.Success -> _myLeaveState.value = UiState.Success(res.data)
                is NetworkResult.Error -> _myLeaveState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _myLeaveState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadLeaveRequests(status: String? = null) {
        viewModelScope.launch {
            _leaveRequestsState.value = UiState.Loading
            val t = token() ?: run {
                _leaveRequestsState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = teacherRepository.getLeaveRequests(t, status)) {
                is NetworkResult.Success -> _leaveRequestsState.value = UiState.Success(res.data)
                is NetworkResult.Error -> _leaveRequestsState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _leaveRequestsState.value = UiState.Error("Connection error")
            }
        }
    }

    // ── Update tab tools ──────────────────────────────────────────────────────────

    fun selectClass(assignmentId: String, summary: TeacherClassSummaryDto? = null) {
        _selectedAssignmentId.value = assignmentId
        _selectedClassSummary.value = summary
        loadAttendance(assignmentId)
        loadAssessments(assignmentId)
        loadSyllabus(assignmentId)
        loadHomework(assignmentId)
        loadLessonPlans(assignmentId)
    }

    fun clearSelectedClass() {
        _selectedAssignmentId.value = null
        _selectedClassSummary.value = null
    }

    fun loadAttendance(assignmentId: String, date: String? = null) {
        viewModelScope.launch {
            _attendanceState.value = UiState.Loading
            val t = token() ?: run {
                _attendanceState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = teacherRepository.loadAttendance(t, assignmentId, date)) {
                is NetworkResult.Success -> _attendanceState.value = UiState.Success(res.data)
                is NetworkResult.Error -> _attendanceState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _attendanceState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadAssessments(assignmentId: String, status: String? = null) {
        viewModelScope.launch {
            _assessmentsState.value = UiState.Loading
            val t = token() ?: run {
                _assessmentsState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = teacherRepository.listAssessments(t, assignmentId, status)) {
                is NetworkResult.Success -> _assessmentsState.value = UiState.Success(res.data)
                is NetworkResult.Error -> _assessmentsState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _assessmentsState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadSyllabus(assignmentId: String) {
        viewModelScope.launch {
            _syllabusState.value = UiState.Loading
            val t = token() ?: run {
                _syllabusState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = teacherRepository.loadSyllabus(t, assignmentId)) {
                is NetworkResult.Success -> _syllabusState.value = UiState.Success(res.data)
                is NetworkResult.Error -> _syllabusState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _syllabusState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadHomework(assignmentId: String) {
        viewModelScope.launch {
            _homeworkState.value = UiState.Loading
            val t = token() ?: run {
                _homeworkState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = teacherRepository.listHomework(t, assignmentId)) {
                is NetworkResult.Success -> _homeworkState.value = UiState.Success(res.data)
                is NetworkResult.Error -> _homeworkState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _homeworkState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadLessonPlans(assignmentId: String, status: String? = null) {
        viewModelScope.launch {
            _lessonPlansState.value = UiState.Loading
            val t = token() ?: run {
                _lessonPlansState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = teacherRepository.listLessonPlans(t, assignmentId, status)) {
                is NetworkResult.Success -> _lessonPlansState.value = UiState.Success(res.data)
                is NetworkResult.Error -> _lessonPlansState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _lessonPlansState.value = UiState.Error("Connection error")
            }
        }
    }

    // ── Student profile drill-down ────────────────────────────────────────────────

    fun loadStudentProfile(studentId: String) {
        viewModelScope.launch {
            _studentProfileState.value = UiState.Loading
            val t = token() ?: run {
                _studentProfileState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = teacherRepository.getStudentProfileV2(t, studentId)) {
                is NetworkResult.Success -> _studentProfileState.value = UiState.Success(res.data)
                is NetworkResult.Error -> _studentProfileState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _studentProfileState.value = UiState.Error("Connection error")
            }
        }
    }

    // ── Messages ──────────────────────────────────────────────────────────────────

    fun loadMessageThreads() {
        viewModelScope.launch {
            _messageThreadsState.value = UiState.Loading
            val t = token() ?: run {
                _messageThreadsState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = teacherRepository.getMessageThreads(t)) {
                is NetworkResult.Success -> _messageThreadsState.value = UiState.Success(res.data)
                is NetworkResult.Error -> _messageThreadsState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _messageThreadsState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadUnreadCount() {
        viewModelScope.launch {
            val t = token() ?: return@launch
            when (val res = teacherRepository.getUnreadCount(t)) {
                is NetworkResult.Success -> _unreadCount.value = res.data
                else -> {}
            }
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _notificationsState.value = UiState.Loading
            val t = token() ?: run {
                _notificationsState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = notificationRepository.getNotifications(t)) {
                is NetworkResult.Success -> {
                    _notificationsState.value = UiState.Success(res.data)
                    _unreadCount.value = res.data.data.unreadCount
                }
                is NetworkResult.Error -> _notificationsState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _notificationsState.value = UiState.Error("Connection error")
            }
        }
    }

    fun markNotificationRead(notificationId: String) {
        viewModelScope.launch {
            val t = token() ?: return@launch
            notificationRepository.markNotificationRead(t, notificationId)
            loadNotifications()
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            val t = token() ?: return@launch
            notificationRepository.markAllNotificationsRead(t)
            loadNotifications()
        }
    }

    // ── Health alerts ─────────────────────────────────────────────────────────────

    fun loadHealthAlerts() {
        viewModelScope.launch {
            _healthAlertsState.value = UiState.Loading
            val t = token() ?: run {
                _healthAlertsState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = healthRepository.getHealthAlerts(t)) {
                is NetworkResult.Success -> {
                    val alerts = res.data.data
                    if (alerts != null) _healthAlertsState.value = UiState.Success(alerts)
                    else _healthAlertsState.value = UiState.Error("No data")
                }
                is NetworkResult.Error -> _healthAlertsState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _healthAlertsState.value = UiState.Error("Connection error")
            }
        }
    }

    // ── PEWS ───────────────────────────────────────────────────────────────────────

    fun loadPewsStudents() {
        viewModelScope.launch {
            _pewsStudentsState.value = UiState.Loading
            val t = token() ?: run {
                _pewsStudentsState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = pewsRepository.getTeacherStudents(t)) {
                is NetworkResult.Success -> {
                    val students = res.data.data ?: emptyList()
                    _pewsStudentsState.value = UiState.Success(students)
                }
                is NetworkResult.Error -> _pewsStudentsState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _pewsStudentsState.value = UiState.Error("Connection error")
            }
        }
    }

    fun loadPewsInterventions(status: String? = null) {
        viewModelScope.launch {
            _pewsInterventionsState.value = UiState.Loading
            val t = token() ?: run {
                _pewsInterventionsState.value = UiState.Error("Not authenticated")
                return@launch
            }
            when (val res = pewsRepository.getTeacherInterventions(t, status)) {
                is NetworkResult.Success -> {
                    val interventions = res.data.data ?: emptyList()
                    _pewsInterventionsState.value = UiState.Success(interventions)
                }
                is NetworkResult.Error -> _pewsInterventionsState.value = UiState.Error(res.message)
                is NetworkResult.ConnectionError -> _pewsInterventionsState.value = UiState.Error("Connection error")
            }
        }
    }

    // ── Initial load ───────────────────────────────────────────────────────────────

    fun loadAll() {
        loadDay()
        loadObligations()
        loadCheckInStatus()
        loadClasses()
        loadProfile()
        loadUnreadCount()
    }
}
