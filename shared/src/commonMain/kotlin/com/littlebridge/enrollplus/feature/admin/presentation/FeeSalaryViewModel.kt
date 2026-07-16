package com.littlebridge.enrollplus.feature.admin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.admin.domain.model.BulkCreateFeeAdditionalChargeRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.CreateFeeAdditionalChargeRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.CreateFeeLateFeeTierRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.CreateFeeStructureRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeAdditionalChargeDto
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeClassOptionDto
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeLateFeeTierDto
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeReminderConfigDto
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeStructureDto
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeStudentDto
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeTeacherOptionDto
import com.littlebridge.enrollplus.feature.admin.domain.model.GenerateFeesRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.GenerateFeesResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.MarkPaidRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.SalaryRecordDto
import com.littlebridge.enrollplus.feature.admin.domain.model.SetSalaryRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.UpdateFeeLateFeeTierRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.UpdateFeeReminderConfigRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.UpdateFeeStructureRequest
import com.littlebridge.enrollplus.feature.admin.domain.repository.FeeSalaryRepository
import com.littlebridge.enrollplus.util.AppLogger
import com.littlebridge.enrollplus.util.todayIso
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class FeeSalaryTab { FEES, SALARY }
enum class FeeSubTab { STRUCTURE, PAYMENT_TRACKING, REMINDER_SETTINGS, LATE_FEE_TIERS, CHARGES }

data class FeeSalaryState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val activeTab: FeeSalaryTab = FeeSalaryTab.FEES,
    val activeFeeSubTab: FeeSubTab = FeeSubTab.STRUCTURE,

    // Fee Structure
    val structures: List<FeeStructureDto> = emptyList(),
    val selectedClassFilter: String? = null,

    // Payment Tracking
    val feeStudents: List<FeeStudentDto> = emptyList(),
    val totalDue: Double = 0.0,
    val totalPaid: Double = 0.0,
    val selectedMonth: String = "",
    val searchQuery: String = "",
    val selectedStudent: FeeStudentDto? = null,

    // Additional Charges
    val additionalCharges: List<FeeAdditionalChargeDto> = emptyList(),

    // Reminder Config
    val reminderConfig: FeeReminderConfigDto? = null,

    // Salary
    val salaryRecords: List<SalaryRecordDto> = emptyList(),
    val selectedTeacherId: String? = null,

    // Class & Teacher dropdowns
    val classes: List<FeeClassOptionDto> = emptyList(),
    val teachers: List<FeeTeacherOptionDto> = emptyList(),

    // Late Fee Tiers
    val lateFeeTiers: List<FeeLateFeeTierDto> = emptyList(),

    // Action feedback
    val actionMessage: String? = null,
    val isActionLoading: Boolean = false,
)

class FeeSalaryViewModel(
    private val repository: FeeSalaryRepository,
    private val prefs: PreferenceRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(FeeSalaryState())
    val state: StateFlow<FeeSalaryState> = _state.asStateFlow()

    init {
        loadFeeStructures()
    }

    fun setTab(tab: FeeSalaryTab) {
        _state.update { it.copy(activeTab = tab) }
        when (tab) {
            FeeSalaryTab.FEES -> {
                if (_state.value.structures.isEmpty()) loadFeeStructures()
            }
            FeeSalaryTab.SALARY -> {
                if (_state.value.salaryRecords.isEmpty()) loadSalaryRecords()
            }
        }
    }

    fun setFeeSubTab(subTab: FeeSubTab) {
        _state.update { it.copy(activeFeeSubTab = subTab) }
        when (subTab) {
            FeeSubTab.STRUCTURE -> if (_state.value.structures.isEmpty()) loadFeeStructures()
            FeeSubTab.PAYMENT_TRACKING -> if (_state.value.feeStudents.isEmpty()) loadFeeStudents()
            FeeSubTab.REMINDER_SETTINGS -> if (_state.value.reminderConfig == null) loadReminderConfig()
            FeeSubTab.LATE_FEE_TIERS -> if (_state.value.lateFeeTiers.isEmpty()) loadLateFeeTiers()
            FeeSubTab.CHARGES -> {
                if (_state.value.additionalCharges.isEmpty()) loadAdditionalCharges()
                if (_state.value.feeStudents.isEmpty()) loadFeeStudents()
            }
        }
        if (_state.value.classes.isEmpty()) loadClasses()
        if (_state.value.teachers.isEmpty()) loadTeachers()
    }

    private suspend fun getToken(): String? = prefs.getUserToken().first()

    // ── Fee Structures ────────────────────────────────────────────────────────

    fun loadFeeStructures() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val token = getToken()
            if (token.isNullOrBlank()) {
                _state.update { it.copy(isLoading = false, errorMessage = "Not authenticated") }
                return@launch
            }
            when (val result = repository.getFeeStructures(token, _state.value.selectedClassFilter)) {
                is NetworkResult.Success -> {
                    val data = result.data.data
                    _state.update {
                        it.copy(
                            isLoading = false,
                            structures = data?.structures ?: emptyList(),
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isLoading = false, errorMessage = "Connection error") }
                }
            }
        }
    }

    fun createFeeStructure(title: String, amount: Double, description: String?, classId: String?, frequency: String = "MONTHLY") {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = getToken() ?: return@launch
            val req = CreateFeeStructureRequest(
                classId = classId,
                title = title,
                description = description,
                amount = amount,
                frequency = frequency,
            )
            when (val result = repository.createFeeStructure(token, req)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isActionLoading = false, actionMessage = "Fee structure created") }
                    loadFeeStructures()
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = "Connection error") }
                }
            }
        }
    }

    fun updateFeeStructure(id: String, request: UpdateFeeStructureRequest) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = getToken() ?: return@launch
            when (val result = repository.updateFeeStructure(token, id, request)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isActionLoading = false, actionMessage = "Fee structure updated") }
                    loadFeeStructures()
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = "Connection error") }
                }
            }
        }
    }

    fun deleteFeeStructure(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = getToken() ?: return@launch
            when (val result = repository.deleteFeeStructure(token, id)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isActionLoading = false, actionMessage = "Fee structure deleted") }
                    loadFeeStructures()
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = "Connection error") }
                }
            }
        }
    }

    // ── Fee Payment Tracking ──────────────────────────────────────────────────

    fun loadFeeStudents() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val token = getToken()
            if (token.isNullOrBlank()) {
                _state.update { it.copy(isLoading = false) }
                return@launch
            }
            val month = _state.value.selectedMonth.ifBlank {
                todayIso().substring(0, 7)
            }
            val classId = _state.value.selectedClassFilter
            when (val result = repository.getFeeStudents(token, classId = classId, month = month, search = _state.value.searchQuery.ifBlank { null })) {
                is NetworkResult.Success -> {
                    val data = result.data.data
                    _state.update {
                        it.copy(
                            isLoading = false,
                            feeStudents = data?.students ?: emptyList(),
                            totalDue = data?.totalDue ?: 0.0,
                            totalPaid = data?.totalPaid ?: 0.0,
                            selectedMonth = month,
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isLoading = false, errorMessage = "Connection error") }
                }
            }
        }
    }

    fun setMonth(month: String) {
        _state.update { it.copy(selectedMonth = month) }
        loadFeeStudents()
    }

    fun setClassFilter(classId: String?) {
        _state.update { it.copy(selectedClassFilter = classId, feeStudents = emptyList()) }
        loadFeeStudents()
    }

    fun setSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun selectStudent(student: FeeStudentDto) {
        _state.update { it.copy(selectedStudent = student) }
    }

    fun clearSelectedStudent() {
        _state.update { it.copy(selectedStudent = null) }
    }

    fun markFeesPaid(childId: String, months: List<String>) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = getToken() ?: return@launch
            when (val result = repository.markFeesPaid(token, MarkPaidRequest(childId, months))) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isActionLoading = false, actionMessage = "Fees marked as paid") }
                    loadFeeStudents()
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = "Connection error") }
                }
            }
        }
    }

    fun generateFees(month: String, classId: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = getToken() ?: return@launch
            when (val result = repository.generateFees(token, GenerateFeesRequest(month, classId))) {
                is NetworkResult.Success -> {
                    val data = result.data.data
                    val msg = "Generated ${data?.generated ?: 0} fees, skipped ${data?.skipped ?: 0}"
                    _state.update { it.copy(isActionLoading = false, actionMessage = msg) }
                    loadFeeStudents()
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = "Connection error") }
                }
            }
        }
    }

    // ── Additional Charges ────────────────────────────────────────────────────

    fun loadAdditionalCharges(childId: String? = null, month: String? = null) {
        viewModelScope.launch {
            val token = getToken() ?: return@launch
            when (val result = repository.getAdditionalCharges(token, childId, month)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(additionalCharges = result.data.data?.charges ?: emptyList()) }
                }
                is NetworkResult.Error -> {
                    AppLogger.e("FeeSalaryVM", "Failed to load charges: ${result.message}")
                }
                is NetworkResult.ConnectionError -> {
                    AppLogger.e("FeeSalaryVM", "Connection error loading charges")
                }
            }
        }
    }

    fun createAdditionalCharge(request: CreateFeeAdditionalChargeRequest) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = getToken() ?: return@launch
            when (val result = repository.createAdditionalCharge(token, request)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isActionLoading = false, actionMessage = "Additional charge added") }
                    loadAdditionalCharges()
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = "Connection error") }
                }
            }
        }
    }

    fun bulkCreateAdditionalCharge(request: BulkCreateFeeAdditionalChargeRequest) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = getToken() ?: return@launch
            when (val result = repository.bulkCreateAdditionalCharge(token, request)) {
                is NetworkResult.Success -> {
                    val created = result.data.data?.get("created") ?: 0
                    _state.update { it.copy(isActionLoading = false, actionMessage = "Charges created for $created students") }
                    loadAdditionalCharges()
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = "Connection error") }
                }
            }
        }
    }

    fun deleteAdditionalCharge(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = getToken() ?: return@launch
            when (val result = repository.deleteAdditionalCharge(token, id)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isActionLoading = false, actionMessage = "Charge removed") }
                    loadAdditionalCharges()
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = "Connection error") }
                }
            }
        }
    }

    // ── Reminder Config ───────────────────────────────────────────────────────

    fun loadReminderConfig() {
        viewModelScope.launch {
            val token = getToken() ?: return@launch
            when (val result = repository.getReminderConfig(token)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(reminderConfig = result.data.data) }
                }
                is NetworkResult.Error -> {
                    AppLogger.e("FeeSalaryVM", "Failed to load reminder config: ${result.message}")
                }
                is NetworkResult.ConnectionError -> {
                    AppLogger.e("FeeSalaryVM", "Connection error loading reminder config")
                }
            }
        }
    }

    fun updateReminderConfig(reminderDay: Int, isActive: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = getToken() ?: return@launch
            when (val result = repository.updateReminderConfig(token, UpdateFeeReminderConfigRequest(reminderDay, isActive))) {
                is NetworkResult.Success -> {
                    _state.update {
                        it.copy(
                            isActionLoading = false,
                            actionMessage = "Reminder config updated",
                            reminderConfig = result.data.data,
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = "Connection error") }
                }
            }
        }
    }

    // ── Salary ────────────────────────────────────────────────────────────────

    fun loadSalaryRecords(teacherId: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val token = getToken()
            if (token.isNullOrBlank()) {
                _state.update { it.copy(isLoading = false) }
                return@launch
            }
            when (val result = repository.getSalaryRecords(token, teacherId)) {
                is NetworkResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            salaryRecords = result.data.data?.records ?: emptyList(),
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isLoading = false, errorMessage = "Connection error") }
                }
            }
        }
    }

    fun setSalary(request: SetSalaryRequest) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = getToken() ?: return@launch
            when (val result = repository.setSalary(token, request)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isActionLoading = false, actionMessage = "Salary set") }
                    loadSalaryRecords()
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = "Connection error") }
                }
            }
        }
    }

    fun markSalaryPaid(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = getToken() ?: return@launch
            when (val result = repository.markSalaryPaid(token, id)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isActionLoading = false, actionMessage = "Salary marked as paid") }
                    loadSalaryRecords()
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = "Connection error") }
                }
            }
        }
    }

    fun clearActionMessage() {
        _state.update { it.copy(actionMessage = null, errorMessage = null) }
    }

    // ── Class & Teacher Lookups ──────────────────────────────────────────────

    fun loadClasses() {
        viewModelScope.launch {
            val token = getToken() ?: return@launch
            when (val result = repository.getFeeClasses(token)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(classes = result.data.data?.classes ?: emptyList()) }
                }
                is NetworkResult.Error -> AppLogger.e("FeeSalaryVM", "Failed to load classes: ${result.message}")
                is NetworkResult.ConnectionError -> AppLogger.e("FeeSalaryVM", "Connection error loading classes")
            }
        }
    }

    fun loadTeachers() {
        viewModelScope.launch {
            val token = getToken() ?: return@launch
            when (val result = repository.getFeeTeachers(token)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(teachers = result.data.data?.teachers ?: emptyList()) }
                }
                is NetworkResult.Error -> AppLogger.e("FeeSalaryVM", "Failed to load teachers: ${result.message}")
                is NetworkResult.ConnectionError -> AppLogger.e("FeeSalaryVM", "Connection error loading teachers")
            }
        }
    }

    // ── Late Fee Tiers ────────────────────────────────────────────────────────

    fun loadLateFeeTiers() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val token = getToken() ?: return@launch
            when (val result = repository.getLateFeeTiers(token)) {
                is NetworkResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            lateFeeTiers = result.data.data?.tiers ?: emptyList(),
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isLoading = false, errorMessage = "Connection error") }
                }
            }
        }
    }

    fun createLateFeeTier(daysAfterDue: Int, amount: Double) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = getToken() ?: return@launch
            val req = CreateFeeLateFeeTierRequest(daysAfterDue = daysAfterDue, amount = amount)
            when (val result = repository.createLateFeeTier(token, req)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isActionLoading = false, actionMessage = "Late fee tier created") }
                    loadLateFeeTiers()
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = "Connection error") }
                }
            }
        }
    }

    fun updateLateFeeTier(id: String, daysAfterDue: Int, amount: Double) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = getToken() ?: return@launch
            val req = UpdateFeeLateFeeTierRequest(daysAfterDue = daysAfterDue, amount = amount)
            when (val result = repository.updateLateFeeTier(token, id, req)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isActionLoading = false, actionMessage = "Late fee tier updated") }
                    loadLateFeeTiers()
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = "Connection error") }
                }
            }
        }
    }

    fun deleteLateFeeTier(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = getToken() ?: return@launch
            when (val result = repository.deleteLateFeeTier(token, id)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isActionLoading = false, actionMessage = "Late fee tier deleted") }
                    loadLateFeeTiers()
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isActionLoading = false, errorMessage = "Connection error") }
                }
            }
        }
    }
}
