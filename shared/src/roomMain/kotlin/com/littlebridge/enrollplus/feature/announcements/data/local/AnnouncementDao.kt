package com.littlebridge.enrollplus.feature.announcements.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM announcement_entity WHERE schoolId = :schoolId ORDER BY date DESC")
    suspend fun getForSchool(schoolId: String): List<AnnouncementEntity>

    @Query("SELECT * FROM announcement_entity WHERE id = :id")
    suspend fun getById(id: String): AnnouncementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(announcements: List<AnnouncementEntity>)

    @Query("DELETE FROM announcement_entity WHERE schoolId = :schoolId")
    suspend fun deleteForSchool(schoolId: String)

    @Query("DELETE FROM announcement_entity WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM announcement_entity WHERE schoolId = :schoolId")
    suspend fun countForSchool(schoolId: String): Int

    @Query("DELETE FROM announcement_entity WHERE id IN (SELECT id FROM announcement_entity WHERE schoolId = :schoolId ORDER BY date ASC LIMIT :limit)")
    suspend fun evictOldest(schoolId: String, limit: Int)
}

@androidx.room.Entity(tableName = "announcement_entity")
data class AnnouncementEntity(
    @androidx.room.PrimaryKey val id: String,
    val schoolId: String,
    val title: String,
    val body: String,
    val type: String,
    val date: Long,
    val audience: String,
    val isRead: Boolean,
    val cachedAt: Long,
)
