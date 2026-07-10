package com.littlebridge.enrollplus.core.notification.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.notification.NotificationFeedRepository
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationItem(
    val id: String,
    val category: String,
    val title: String,
    val body: String,
    val time: String,
    val unread: Boolean,
    val deepLink: String? = null,
    val refType: String? = null,
    val refId: String? = null,
)

data class NotificationsState(
    val notifications: List<NotificationItem> = emptyList(),
    val unreadCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isStale: Boolean = false,
    val isOffline: Boolean = false,
)

class NotificationsViewModel(
    private val repository: NotificationFeedRepository,
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(NotificationsState())
    val state: StateFlow<NotificationsState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(isLoading = false, error = "Not signed in") }
                return@launch
            }
            when (val result = repository.getNotifications(token)) {
                is NetworkResult.Success -> {
                    val data = result.data.data
                    _state.update {
                        it.copy(
                            isLoading = false,
                            notifications = data.notifications.map { n ->
                                NotificationItem(
                                    id = n.id,
                                    category = n.category,
                                    title = n.title,
                                    body = n.body,
                                    time = n.time,
                                    unread = n.unread,
                                    deepLink = n.deepLink,
                                    refType = n.refType,
                                    refId = n.refId,
                                )
                            },
                            unreadCount = data.unreadCount,
                            isStale = result.isStale,
                            isOffline = result.isOffline,
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(isLoading = false, error = "Connection error") }
                }
            }
        }
    }

    fun markAllRead() {
        _state.update { s ->
            s.copy(
                notifications = s.notifications.map { it.copy(unread = false) },
                unreadCount = 0,
            )
        }
        viewModelScope.launch {
            val token = preferenceRepository.getUserToken().first() ?: return@launch
            repository.markAllNotificationsRead(token)
        }
    }

    fun markRead(id: String) {
        _state.update { s ->
            val updated = s.notifications.map { if (it.id == id) it.copy(unread = false) else it }
            s.copy(notifications = updated, unreadCount = updated.count { it.unread })
        }
        if (!id.startsWith("ann_") && !id.startsWith("fee_")) {
            viewModelScope.launch {
                val token = preferenceRepository.getUserToken().first() ?: return@launch
                repository.markNotificationRead(token, id)
            }
        }
    }

    fun markByRef(refType: String?, refId: String?) {
        if (refType.isNullOrBlank() || refId.isNullOrBlank()) return
        _state.update { s ->
            val updated = s.notifications.map {
                if (it.refType == refType && it.refId == refId) it.copy(unread = false) else it
            }
            s.copy(notifications = updated, unreadCount = updated.count { it.unread })
        }
        viewModelScope.launch {
            val token = preferenceRepository.getUserToken().first() ?: return@launch
            repository.markNotificationByRef(token, refType, refId)
        }
    }

    fun clearAll() {
        _state.update { s ->
            s.copy(notifications = emptyList(), unreadCount = 0)
        }
        viewModelScope.launch {
            val token = preferenceRepository.getUserToken().first() ?: return@launch
            repository.clearAllNotifications(token)
            load()
        }
    }
}
