package com.littlebridge.enrollplus.core.cache

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import com.littlebridge.enrollplus.util.AppLogger

class CacheManager(
    private val storage: CacheStorage,
    private val json: Json,
) {
    companion object {
        private const val TAG = "CacheManager"
        private const val EVICTION_THRESHOLD = 7 * 24 * 60 * 60 * 1000L // 7 days
    }

    suspend fun <T> read(key: String, serializer: KSerializer<T>): T? {
        val entry = storage.get(key) ?: return null
        if (entry.ttlMs > 0 && (System.currentTimeMillis() - entry.cachedAt) > entry.ttlMs) {
            AppLogger.d(TAG, "Cache expired for key=$key (ttl=${entry.ttlMs}ms)")
            storage.delete(key)
            return null
        }
        return try {
            json.decodeFromString(serializer, entry.dataJson)
        } catch (e: Exception) {
            AppLogger.d(TAG, "Cache deserialization failed for key=$key: ${e.message}")
            storage.delete(key)
            null
        }
    }

    suspend fun <T> write(key: String, data: T, serializer: KSerializer<T>, ttlMs: Long = 0) {
        try {
            val dataJson = json.encodeToString(serializer, data)
            storage.put(CacheEntry(key = key, dataJson = dataJson, cachedAt = System.currentTimeMillis(), ttlMs = ttlMs))
        } catch (e: Exception) {
            AppLogger.d(TAG, "Cache serialization failed for key=$key: ${e.message}")
        }
    }

    suspend fun delete(key: String) {
        storage.delete(key)
    }

    suspend fun evictAll() {
        storage.evictAll()
        AppLogger.d(TAG, "All cache evicted")
    }

    suspend fun cleanup() {
        val cutoff = System.currentTimeMillis() - EVICTION_THRESHOLD
        storage.evictOlderThan(cutoff)
    }

    suspend fun exists(key: String): Boolean {
        return storage.get(key) != null
    }

    suspend fun isFresh(key: String): Boolean {
        val entry = storage.get(key) ?: return false
        if (entry.ttlMs <= 0) return false
        return (System.currentTimeMillis() - entry.cachedAt) < entry.ttlMs
    }
}
