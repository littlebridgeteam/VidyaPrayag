package com.littlebridge.enrollplus.core.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "cache_entity")
data class CacheEntity(
    @PrimaryKey val key: String,
    val dataJson: String,
    val cachedAt: Long,
    val ttlMs: Long,
)

@Dao
interface CacheDao {
    @Query("SELECT * FROM cache_entity WHERE `key` = :key")
    suspend fun get(key: String): CacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entity: CacheEntity)

    @Query("DELETE FROM cache_entity WHERE `key` = :key")
    suspend fun delete(key: String)

    @Query("DELETE FROM cache_entity")
    suspend fun deleteAll()

    @Query("DELETE FROM cache_entity WHERE cachedAt < :before")
    suspend fun evictOlderThan(before: Long)

    @Query("SELECT COUNT(*) FROM cache_entity")
    suspend fun count(): Int
}
