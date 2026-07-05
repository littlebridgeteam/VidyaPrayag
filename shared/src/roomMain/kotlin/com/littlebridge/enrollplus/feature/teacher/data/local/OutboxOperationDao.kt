package com.littlebridge.enrollplus.feature.teacher.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OutboxOperationDao {
    @Query("SELECT * FROM outbox_operation_entity WHERE status = 'PENDING' ORDER BY createdAt ASC")
    suspend fun getPending(): List<OutboxOperationEntity>

    @Query("SELECT * FROM outbox_operation_entity WHERE id = :id")
    suspend fun getById(id: String): OutboxOperationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: OutboxOperationEntity)

    @Query("UPDATE outbox_operation_entity SET status = :status, attempts = :attempts, lastError = :lastError, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, attempts: Int, lastError: String?, updatedAt: Long)

    @Query("SELECT COUNT(*) FROM outbox_operation_entity WHERE status = 'PENDING'")
    suspend fun pendingCount(): Int

    @Query("SELECT MIN(createdAt) FROM outbox_operation_entity WHERE status = 'PENDING'")
    suspend fun oldestPendingCreatedAt(): Long?

    @Query("DELETE FROM outbox_operation_entity WHERE status = 'SYNCED' AND updatedAt < :before")
    suspend fun cleanSynced(before: Long)

    @Query("DELETE FROM outbox_operation_entity WHERE status = 'FAILED'")
    suspend fun cleanFailed()
}

@androidx.room.Entity(tableName = "outbox_operation_entity")
data class OutboxOperationEntity(
    @androidx.room.PrimaryKey val id: String,
    val operation: String,
    val payloadJson: String,
    val schoolId: String,
    val teacherId: String,
    val clientRequestId: String,
    val status: String,
    val attempts: Int,
    val lastError: String?,
    val createdAt: Long,
    val updatedAt: Long,
)
