package com.littlebridge.enrollplus.domain.util

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(
        val data: T,
        val isStale: Boolean = false,
        val isOffline: Boolean = false,
    ) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
