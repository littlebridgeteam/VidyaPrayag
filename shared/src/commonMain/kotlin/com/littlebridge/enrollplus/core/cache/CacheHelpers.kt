package com.littlebridge.enrollplus.core.cache

import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.util.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer

private const val TAG = "CacheHelpers"
const val TTL_ALWAYS_REFRESH = 0L
const val TTL_1_HOUR = 60 * 60 * 1000L
const val TTL_24_HOURS = 24 * 60 * 60 * 1000L

/**
 * Loads data with cache-first pattern.
 *
 * 1. Read cache → if exists, emit UiState.Success(isStale=true) immediately
 * 2. If no cache, emit UiState.Loading
 * 3. Call network → on success: write cache, emit UiState.Success(isStale=false)
 * 4. On network failure + cache exists: emit UiState.Success(isStale=true, isOffline=true)
 * 5. On network failure + no cache: emit UiState.Error
 *
 * @param cache CacheManager instance
 * @param cacheKey Unique cache key for this data
 * @param serializer kotlinx.serialization serializer for type T
 * @param ttlMs Time-to-live (0 = always refresh but show cache first)
 * @param state MutableStateFlow to emit UiState into
 * @param networkCall Suspend lambda that returns NetworkResult<T>
 */
suspend fun <T> loadWithCache(
    cache: CacheManager,
    cacheKey: String,
    serializer: KSerializer<T>,
    state: MutableStateFlow<UiState<T>>,
    ttlMs: Long = TTL_ALWAYS_REFRESH,
    networkCall: suspend () -> NetworkResult<T>,
) {
    val cached = cache.read(cacheKey, serializer)

    if (cached != null) {
        state.value = UiState.Success(cached, isStale = true, isOffline = false)
    } else {
        state.value = UiState.Loading
    }

    when (val result = networkCall()) {
        is NetworkResult.Success -> {
            cache.write(cacheKey, result.data, serializer, ttlMs)
            state.value = UiState.Success(result.data, isStale = false, isOffline = false)
        }
        is NetworkResult.Error -> {
            if (cached != null) {
                AppLogger.d(TAG, "Network error for key=$cacheKey, serving from cache: ${result.message}")
                state.value = UiState.Success(cached, isStale = true, isOffline = true)
            } else {
                state.value = UiState.Error(result.message)
            }
        }
        is NetworkResult.ConnectionError -> {
            if (cached != null) {
                AppLogger.d(TAG, "Connection error for key=$cacheKey, serving from cache")
                state.value = UiState.Success(cached, isStale = true, isOffline = true)
            } else {
                state.value = UiState.Error("Connection error. Check your internet.")
            }
        }
    }
}

/**
 * Refresh data with cache — used by retry() or pull-to-refresh.
 * Does NOT show cached data first (user already has data on screen).
 * Always hits network, updates cache on success.
 */
suspend fun <T> refreshWithCache(
    cache: CacheManager,
    cacheKey: String,
    serializer: KSerializer<T>,
    state: MutableStateFlow<UiState<T>>,
    ttlMs: Long = TTL_ALWAYS_REFRESH,
    networkCall: suspend () -> NetworkResult<T>,
) {
    when (val result = networkCall()) {
        is NetworkResult.Success -> {
            cache.write(cacheKey, result.data, serializer, ttlMs)
            state.value = UiState.Success(result.data, isStale = false, isOffline = false)
        }
        is NetworkResult.Error -> {
            val current = state.value
            if (current is UiState.Success) {
                state.value = UiState.Success(current.data, isStale = true, isOffline = true)
            } else {
                state.value = UiState.Error(result.message)
            }
        }
        is NetworkResult.ConnectionError -> {
            val current = state.value
            if (current is UiState.Success) {
                state.value = UiState.Success(current.data, isStale = true, isOffline = true)
            } else {
                state.value = UiState.Error("Connection error. Check your internet.")
            }
        }
    }
}

/**
 * Loads data with cache for custom UiState classes (not using UiState<T> sealed class).
 * Returns a CacheResult that the caller can map into their custom state.
 */
sealed class CacheResult<out T> {
    data class Cached<T>(val data: T, val isOffline: Boolean) : CacheResult<T>()
    data class Fresh<T>(val data: T) : CacheResult<T>()
    data class Error(val message: String) : CacheResult<Nothing>()
    data object ConnectionError : CacheResult<Nothing>()
    data object NoCacheNoNetwork : CacheResult<Nothing>()
}

/**
 * Cache-first helper that returns NetworkResult<T> directly (with isStale/isOffline flags).
 * Use this in repository implementations to wrap GET methods with minimal boilerplate.
 *
 * 1. Read cache → if exists, remember it
 * 2. Call network → on success: write cache, return fresh NetworkResult.Success
 * 3. On network failure + cache exists: return NetworkResult.Success(isStale=true, isOffline=true)
 * 4. On network failure + no cache: return the original error result
 */
suspend fun <T> cacheFirstNetworkResult(
    cache: CacheManager,
    cacheKey: String,
    serializer: KSerializer<T>,
    networkCall: suspend () -> NetworkResult<T>,
): NetworkResult<T> {
    val cached = cache.read(cacheKey, serializer)
    val result = networkCall()
    if (result is NetworkResult.Success) {
        cache.write(cacheKey, result.data, serializer)
        return result
    }
    if (cached != null) {
        return NetworkResult.Success(cached, isStale = true, isOffline = true)
    }
    return result
}

suspend fun <T> cacheFirst(
    cache: CacheManager,
    cacheKey: String,
    serializer: KSerializer<T>,
    ttlMs: Long = TTL_ALWAYS_REFRESH,
    networkCall: suspend () -> NetworkResult<T>,
): CacheResult<T> {
    val cached = cache.read(cacheKey, serializer)

    when (val result = networkCall()) {
        is NetworkResult.Success -> {
            cache.write(cacheKey, result.data, serializer, ttlMs)
            return CacheResult.Fresh(result.data)
        }
        is NetworkResult.Error -> {
            if (cached != null) {
                return CacheResult.Cached(cached, isOffline = true)
            }
            return CacheResult.Error(result.message)
        }
        is NetworkResult.ConnectionError -> {
            if (cached != null) {
                return CacheResult.Cached(cached, isOffline = true)
            }
            return CacheResult.NoCacheNoNetwork
        }
    }
}
