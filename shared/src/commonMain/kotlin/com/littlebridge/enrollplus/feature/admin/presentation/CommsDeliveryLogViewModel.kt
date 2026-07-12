package com.littlebridge.enrollplus.feature.admin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.admin.domain.model.DeliveryLogItem
import com.littlebridge.enrollplus.feature.admin.domain.usecase.GetDeliveryLogUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class CommsDeliveryLogState(
    val items: List<DeliveryLogItem> = emptyList(),
    val nextCursor: String? = null,
    val hasMore: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

class CommsDeliveryLogViewModel(
    private val getDeliveryLogUseCase: GetDeliveryLogUseCase,
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CommsDeliveryLogState())
    val state: StateFlow<CommsDeliveryLogState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        load(cursor = null, replace = true)
    }

    fun loadMore() {
        val cursor = _state.value.nextCursor ?: return
        load(cursor = cursor, replace = false)
    }

    private fun load(cursor: String?, replace: Boolean) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            val token = preferenceRepository.getUserToken().first()
            if (token.isNullOrBlank()) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Not signed in",
                )
                return@launch
            }
            when (val result = getDeliveryLogUseCase(token, limit = 20, cursor = cursor)) {
                is NetworkResult.Success -> {
                    val response = result.data
                    _state.value = _state.value.copy(
                        items = if (replace) response.items else _state.value.items + response.items,
                        nextCursor = response.nextCursor,
                        hasMore = response.hasMore,
                        isLoading = false,
                        errorMessage = null,
                    )
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.message,
                    )
                }
                is NetworkResult.ConnectionError -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "No internet connection",
                    )
                }
            }
        }
    }
}
