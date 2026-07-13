package com.littlebridge.enrollplus.core.cache

import com.littlebridge.enrollplus.core.database.CacheDao
import com.littlebridge.enrollplus.core.database.CacheEntity

class RoomCacheStorage(
    private val dao: CacheDao,
) : CacheStorage {
    override suspend fun get(key: String): CacheEntry? {
        val entity = dao.get(key) ?: return null
        return CacheEntry(
            key = entity.key,
            dataJson = entity.dataJson,
            cachedAt = entity.cachedAt,
            ttlMs = entity.ttlMs,
        )
    }

    override suspend fun put(entry: CacheEntry) {
        dao.put(
            CacheEntity(
                key = entry.key,
                dataJson = entry.dataJson,
                cachedAt = entry.cachedAt,
                ttlMs = entry.ttlMs,
            )
        )
    }

    override suspend fun delete(key: String) {
        dao.delete(key)
    }

    override suspend fun evictAll() {
        dao.deleteAll()
    }

    override suspend fun evictOlderThan(timestamp: Long) {
        dao.evictOlderThan(timestamp)
    }

    override suspend fun count(): Int {
        return dao.count()
    }
}
