/*
 * File: PinnedScreensViewModel.kt
 * Module: feature.admin.presentation
 *
 * Manages the admin's pinned home-screen shortcuts. Reads from the user
 * details payload on login and exposes optimistic pin/unpin/reorder.
 */
package com.littlebridge.enrollplus.feature.admin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.auth.domain.repository.AuthRepository
import com.littlebridge.enrollplus.util.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PinnedScreensViewModel(
    private val authRepository: AuthRepository,
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {

    private val _screens = MutableStateFlow<List<String>>(emptyList())
    val screens: StateFlow<List<String>> = _screens.asStateFlow()

    init {
        viewModelScope.launch {
            val token = preferenceRepository.getUserToken().first() ?: return@launch
            when (val result = authRepository.getUserDetails(token)) {
                is NetworkResult.Success -> {
                    result.data.data?.let { _screens.value = it.pinnedScreens }
                }
                is NetworkResult.Error -> {
                    AppLogger.e("PinnedScreensVM", "getUserDetails failed: ${result.message}")
                }
                is NetworkResult.ConnectionError -> {
                    AppLogger.e("PinnedScreensVM", "getUserDetails connection error")
                }
            }
        }
    }

    fun setInitial(screens: List<String>) {
        _screens.value = screens
    }

    fun pin(routeId: String) {
        if (routeId.isBlank() || _screens.value.contains(routeId)) return
        val next = _screens.value + routeId
        persist(next)
    }

    fun unpin(routeId: String) {
        val next = _screens.value.filterNot { it == routeId }
        persist(next)
    }

    fun toggle(routeId: String) {
        if (_screens.value.contains(routeId)) unpin(routeId) else pin(routeId)
    }

    fun reorder(fromIndex: Int, toIndex: Int) {
        if (fromIndex == toIndex) return
        val current = _screens.value.toMutableList()
        val item = current.removeAt(fromIndex)
        current.add(toIndex.coerceIn(0, current.size), item)
        persist(current)
    }

    private fun persist(next: List<String>) {
        val previous = _screens.value
        _screens.value = next
        viewModelScope.launch {
            when (val result = authRepository.updatePinnedScreens(next)) {
                is NetworkResult.Success -> {
                    result.data.data?.let { _screens.value = it.pinnedScreens }
                }
                is NetworkResult.Error -> {
                    AppLogger.e("PinnedScreensVM", "updatePinnedScreens failed: ${result.message}")
                    _screens.value = previous
                }
                is NetworkResult.ConnectionError -> {
                    AppLogger.e("PinnedScreensVM", "updatePinnedScreens connection error")
                    _screens.value = previous
                }
            }
        }
    }
}
