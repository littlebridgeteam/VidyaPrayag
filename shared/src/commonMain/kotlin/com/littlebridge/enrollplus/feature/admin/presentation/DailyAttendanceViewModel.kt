package com.littlebridge.enrollplus.feature.admin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.admin.domain.model.AttendanceEntryDto
import com.littlebridge.enrollplus.feature.admin.domain.model.AttendanceMarkDto
import com.littlebridge.enrollplus.feature.admin.domain.model.AttendanceSaveRequest
import com.littlebridge.enrollplus.feature.admin.domain.repository.AttendanceRepository
import com.littlebridge.enrollplus.feature.admin.domain.repository.SchoolClassesRepository
import com.littlebridge.enrollplus.feature.admin.domain.model.SchoolClassDto
import com.littlebridge.enrollplus.util.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class AttendanceStatus {
    PRESENT, ABSENT, LATE
}

data class Attendee(
    val id: String,
    val name: String,
    val initials: String,
    val status: AttendanceStatus,
    val imageUrl: String? = null
)

data class ClassChip(
    val displayName: String,
    val className: String,
    val section: String?,
)

data class DailyAttendanceState(
    val attendanceType: String = "Students", // "Faculty" or "Students"
    val selectedClass: ClassChip? = null,
    val availableClasses: List<ClassChip> = emptyList(),
    val attendees: List<Attendee> = emptyList(),
    val totalCount: Int = 0,
    val presentCount: Int = 0,
    val attendancePercentage: String = "0%",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val saveError: String? = null,
    val errorMessage: String? = null,
    val isStale: Boolean = false,
    val isOffline: Boolean = false,
)

class DailyAttendanceViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val schoolClassesRepository: SchoolClassesRepository,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DailyAttendanceState())
    val state: StateFlow<DailyAttendanceState> = _state.asStateFlow()

    init {
        loadClassesThenAttendance()
    }

    fun setAttendanceType(type: String) {
        val serverType = if (type.lowercase().contains("faculty")) "faculty" else "student"
        _state.value = _state.value.copy(attendanceType = type)
        val chip = _state.value.selectedClass
        loadAttendance(
            type = serverType,
            grade = if (serverType == "student") chip?.className else null,
            section = if (serverType == "student") chip?.section else null
        )
    }

    fun selectClass(chip: ClassChip) {
        _state.value = _state.value.copy(selectedClass = chip)
        loadAttendance(type = "student", grade = chip.className, section = chip.section)
    }

    private fun loadClassesThenAttendance() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val token = preferenceRepository.getUserToken().first()
                if (token.isNullOrBlank()) {
                    AppLogger.d("DailyAttendanceVM", "No auth token; skipping load")
                    return@launch
                }

                when (val result = schoolClassesRepository.listClasses(token)) {
                    is NetworkResult.Success -> {
                        val classes = result.data.data?.classes ?: emptyList()
                        val chips = buildClassChips(classes)
                        val firstChip = chips.firstOrNull()
                        _state.value = _state.value.copy(
                            availableClasses = chips,
                            selectedClass = firstChip,
                        )
                        if (firstChip != null) {
                            loadAttendance(type = "student", grade = firstChip.className, section = firstChip.section)
                        } else {
                            loadAttendance(type = "student", grade = null, section = null)
                        }
                    }
                    is NetworkResult.Error -> {
                        AppLogger.e("DailyAttendanceVM", "listClasses error: ${result.message}")
                        _state.value = _state.value.copy(isLoading = false, errorMessage = result.message)
                    }
                    is NetworkResult.ConnectionError -> {
                        AppLogger.e("DailyAttendanceVM", "listClasses connection error")
                        _state.value = _state.value.copy(isLoading = false, errorMessage = "Connection error. Check your internet.")
                    }
                }
            } catch (e: Exception) {
                AppLogger.e("DailyAttendanceVM", "loadClassesThenAttendance exception", e)
                _state.value = _state.value.copy(isLoading = false, errorMessage = e.message ?: "Failed to load classes")
            }
        }
    }

    private fun buildClassChips(classes: List<SchoolClassDto>): List<ClassChip> {
        return classes.flatMap { cls ->
            if (cls.sections.isEmpty()) {
                listOf(ClassChip(displayName = cls.name, className = cls.name, section = null))
            } else {
                cls.sections.map { sec ->
                    ClassChip(displayName = "${cls.name}-$sec", className = cls.name, section = sec)
                }
            }
        }
    }

    private fun loadAttendance(
        type: String = "student",
        grade: String? = null,
        section: String? = null
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val token = preferenceRepository.getUserToken().first()
                if (token.isNullOrBlank()) {
                    AppLogger.d("DailyAttendanceVM", "No auth token; skipping load")
                    _state.value = _state.value.copy(isLoading = false)
                    return@launch
                }

                when (val result = attendanceRepository.getDailyAttendance(token, type, grade, section)) {
                    is NetworkResult.Success -> {
                        val data = result.data.data
                        _state.value = _state.value.copy(
                            attendees = data?.attendanceList?.map { it.toUiModel() } ?: emptyList(),
                            totalCount = data?.totalCount ?: 0,
                            presentCount = data?.presentCount ?: 0,
                            attendancePercentage = data?.attendancePercentage ?: "0%",
                            isLoading = false,
                            errorMessage = null,
                            isStale = result.isStale,
                            isOffline = result.isOffline,
                        )
                    }
                    is NetworkResult.Error -> {
                        AppLogger.e("DailyAttendanceVM", "getDailyAttendance error: ${result.message}")
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                    is NetworkResult.ConnectionError -> {
                        AppLogger.e("DailyAttendanceVM", "getDailyAttendance connection error")
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = "Connection error. Check your internet."
                        )
                    }
                }
            } catch (e: Exception) {
                AppLogger.e("DailyAttendanceVM", "loadAttendance exception", e)
                _state.value = _state.value.copy(isLoading = false, errorMessage = e.message ?: "Failed to load attendance")
            }
        }
    }

    fun refresh() {
        loadClassesThenAttendance()
    }

    fun updateStatus(attendeeId: String, newStatus: AttendanceStatus) {
        val updated = _state.value.attendees.map {
            if (it.id == attendeeId) it.copy(status = newStatus) else it
        }
        _state.value = _state.value.copy(attendees = updated, saveSuccess = false)
    }

    fun save() {
        val current = _state.value
        if (current.attendees.isEmpty() || current.isSaving) return

        viewModelScope.launch {
            _state.value = current.copy(isSaving = true, saveError = null, saveSuccess = false)

            val token = preferenceRepository.getUserToken().first()
            if (token.isNullOrBlank()) {
                _state.value = _state.value.copy(isSaving = false, saveError = "Not signed in")
                return@launch
            }

            val serverType = if (current.attendanceType.lowercase().contains("faculty")) "faculty" else "student"
            val today = com.littlebridge.enrollplus.util.todayIso()
            val marks = current.attendees.map { a ->
                AttendanceMarkDto(
                    id = a.id,
                    status = when (a.status) {
                        AttendanceStatus.PRESENT -> "present"
                        AttendanceStatus.ABSENT -> "absent"
                        AttendanceStatus.LATE -> "late"
                    }
                )
            }
            val request = AttendanceSaveRequest(type = serverType, date = today, marks = marks)

            when (val result = attendanceRepository.saveDailyAttendance(token, request)) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(isSaving = false, saveSuccess = true, saveError = null)
                    val chip = current.selectedClass
                    loadAttendance(
                        type = serverType,
                        grade = if (serverType == "student") chip?.className else null,
                        section = if (serverType == "student") chip?.section else null
                    )
                }
                is NetworkResult.Error -> {
                    AppLogger.e("DailyAttendanceVM", "save error: ${result.message}")
                    _state.value = _state.value.copy(isSaving = false, saveError = result.message)
                }
                is NetworkResult.ConnectionError -> {
                    _state.value = _state.value.copy(isSaving = false, saveError = "Connection error")
                }
            }
        }
    }

    private fun AttendanceEntryDto.toUiModel(): Attendee {
        val uiStatus = when (status.lowercase()) {
            "present", "half_day" -> AttendanceStatus.PRESENT
            "late" -> AttendanceStatus.LATE
            "leave" -> AttendanceStatus.LATE
            else -> AttendanceStatus.ABSENT
        }
        val initials = name.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercaseChar().toString() }
        return Attendee(
            id = id,
            name = name,
            initials = initials,
            status = uiStatus,
            imageUrl = profilePic
        )
    }
}
