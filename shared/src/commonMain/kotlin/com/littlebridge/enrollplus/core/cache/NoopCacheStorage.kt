package com.littlebridge.enrollplus.core.cache

class NoopCacheStorage : CacheStorage {
    override suspend fun get(key: String): CacheEntry? = null
    override suspend fun put(entry: CacheEntry) {}
    override suspend fun delete(key: String) {}
    override suspend fun evictAll() {}
    override suspend fun evictOlderThan(timestamp: Long) {}
    override suspend fun count(): Int = 0
}
