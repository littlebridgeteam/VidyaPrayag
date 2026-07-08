package com.littlebridge.enrollplus.feature.teacher.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TeacherDayCacheDao {
    @Query("SELECT * FROM teacher_day_cache_entity WHERE teacherId = :teacherId AND date = :date")
    suspend fun get(teacherId: String, date: String): TeacherDayCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TeacherDayCacheEntity)

    @Query("DELETE FROM teacher_day_cache_entity WHERE teacherId = :teacherId AND date = :date")
    suspend fun delete(teacherId: String, date: String)

    @Query("SELECT COUNT(*) FROM teacher_day_cache_entity WHERE teacherId = :teacherId")
    suspend fun countForTeacher(teacherId: String): Int

    @Query("DELETE FROM teacher_day_cache_entity WHERE id IN (SELECT id FROM teacher_day_cache_entity WHERE teacherId = :teacherId ORDER BY cachedAt ASC LIMIT :limit)")
    suspend fun evictOldest(teacherId: String, limit: Int)
}

@androidx.room.Entity(tableName = "teacher_day_cache_entity")
data class TeacherDayCacheEntity(
    @androidx.room.PrimaryKey val id: String,
    val teacherId: String,
    val date: String,
    val dataJson: String,
    val cachedAt: Long,
)
