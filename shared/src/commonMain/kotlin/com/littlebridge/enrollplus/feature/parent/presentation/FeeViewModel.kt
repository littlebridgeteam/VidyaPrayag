package com.littlebridge.enrollplus.feature.parent.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.core.state.SelectedChildHolder
import com.littlebridge.enrollplus.feature.parent.domain.model.MonthlyFeeSummary
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentFeeItemDto
import com.littlebridge.enrollplus.feature.parent.domain.repository.ParentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FeeAnnouncement(
    val id: String,
    val title: String,
    val time: String,
    val description: String,
    val openRate: String,
    val engagement: String,
    val type: String // "Campaign", "Emergency", "Payment"
)

data class FeeState(
    val totalCollected: String = "$0",
    val collectionProgress: Float = 0f,
    val outstandingFees: String = "$0",
    val overdueCount: Int = 0,
    val announcements: List<FeeAnnouncement> = emptyList(),
    val feeItems: List<ParentFeeItemDto> = emptyList(),
    val monthlySummary: List<MonthlyFeeSummary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isStale: Boolean = false,
    val isOffline: Boolean = false,
    val refreshEpoch: Int = 0,
)

class FeeViewModel(
    private val repository: ParentRepository,
    private val preferenceRepository: PreferenceRepository,
    // RA-S05: shared selected-child source of truth across all parent tabs.
    private val selectedChildHolder: SelectedChildHolder,
) : ViewModel() {
    private val _state = MutableStateFlow(FeeState())
    val state: StateFlow<FeeState> = _state.asStateFlow()

    init {
        // RA-S05: re-fetch the fee stats whenever the shared selected child
        // changes (including the initial null → first-child seed from another
        // tab), so the Fees tab always shows the same child as Home/Academics.
        viewModelScope.launch {
            selectedChildHolder.selectedChildId.collect { childId ->
                loadFees(childId)
            }
        }
    }

    /** Pull-to-refresh: re-fetch fees for the currently selected child without clearing existing data. */
    fun reload() {
        loadFees(selectedChildHolder.selectedChildId.value, isRefresh = true)
    }

    /** RA-S05: load fees scoped to [childId] (null = all of the parent's records). */
    private fun loadFees(childId: String?, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!isRefresh) _state.update { it.copy(isLoading = true, error = null) }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                if (!isRefresh) _state.update { it.copy(isLoading = false, error = "Not signed in") }
                return@launch
            }
            when (val result = repository.getFees(token, childId)) {
                is NetworkResult.Success -> {
                    val data = result.data.data
                    _state.update {
                        it.copy(
                            isLoading = false,
                            totalCollected = data.totalCollected,
                            collectionProgress = data.collectionProgress,
                            outstandingFees = data.outstandingFees,
                            overdueCount = data.overdueCount,
                            announcements = data.announcements.map { a ->
                                FeeAnnouncement(a.id, a.title, a.time, a.description, a.openRate, a.engagement, a.type)
                            },
                            feeItems = data.feeItems,
                            monthlySummary = data.monthlySummary,
                            isStale = result.isStale,
                            isOffline = result.isOffline,
                            refreshEpoch = it.refreshEpoch + 1,
                        )
                    }
                }
                is NetworkResult.Error -> {
                    if (isRefresh) {
                        _state.update { it.copy(isStale = true, isOffline = true, refreshEpoch = it.refreshEpoch + 1) }
                    } else {
                        _state.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
                is NetworkResult.ConnectionError -> {
                    if (isRefresh) {
                        _state.update { it.copy(isStale = true, isOffline = true, refreshEpoch = it.refreshEpoch + 1) }
                    } else {
                        _state.update { it.copy(isLoading = false, error = "Connection error") }
                    }
                }
            }
        }
    }

    private var payingFeeIds = mutableSetOf<String>()

    fun payFee(feeId: String) {
        if (feeId in payingFeeIds) return
        payingFeeIds.add(feeId)
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                payingFeeIds.remove(feeId)
                _state.update { it.copy(isLoading = false, error = "Not signed in") }
                return@launch
            }
            when (val result = repository.payFee(token, feeId)) {
                is NetworkResult.Success -> {
                    payingFeeIds.remove(feeId)
                    loadFees(selectedChildHolder.selectedChildId.value, isRefresh = true)
                }
                is NetworkResult.Error -> {
                    payingFeeIds.remove(feeId)
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    payingFeeIds.remove(feeId)
                    _state.update { it.copy(isLoading = false, error = "Connection error") }
                }
            }
        }
    }

    fun payAllForMonth(month: String) {
        val monthItems = _state.value.monthlySummary.find { it.month == month }?.items ?: return
        val unpaidIds = monthItems.filter { it.status in setOf("DUE", "OVERDUE") }.map { it.id }
        if (unpaidIds.isEmpty()) return
        unpaidIds.forEach { payFee(it) }
    }
}
