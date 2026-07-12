package com.littlebridge.enrollplus.core.cache

interface CacheStorage {
    suspend fun get(key: String): CacheEntry?
    suspend fun put(entry: CacheEntry)
    suspend fun delete(key: String)
    suspend fun evictAll()
    suspend fun evictOlderThan(timestamp: Long)
    suspend fun count(): Int
}

data class CacheEntry(
    val key: String,
    val dataJson: String,
    val cachedAt: Long,
    val ttlMs: Long,
)
