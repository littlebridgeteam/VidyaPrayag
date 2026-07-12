package com.littlebridge.enrollplus.feature.platform

import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.PlatformNotificationsTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SortOrder
import java.util.UUID

object PlatformNotificationService {

    suspend fun toUser(
        userId: UUID,
        category: String,
        title: String,
        body: String = "",
        entityType: String? = null,
        entityId: UUID? = null,
        deepLink: String? = null,
    ) {
        dbQuery {
            PlatformNotificationsTable.insert {
                it[PlatformNotificationsTable.userId] = userId
                it[PlatformNotificationsTable.category] = category
                it[PlatformNotificationsTable.title] = title
                it[PlatformNotificationsTable.body] = body
                it[PlatformNotificationsTable.entityType] = entityType
                it[PlatformNotificationsTable.entityId] = entityId
                it[PlatformNotificationsTable.deepLink] = deepLink
                it[PlatformNotificationsTable.isRead] = false
                it[PlatformNotificationsTable.createdAt] = java.time.Instant.now()
            }
        }
    }

    suspend fun listForUser(userId: UUID): List<PlatformNotificationDto> = dbQuery {
        PlatformNotificationsTable.selectAll().where { PlatformNotificationsTable.userId eq userId }
            .orderBy(PlatformNotificationsTable.createdAt, SortOrder.DESC)
            .limit(50)
            .map { row ->
                PlatformNotificationDto(
                    id = row[PlatformNotificationsTable.id].value.toString(),
                    category = row[PlatformNotificationsTable.category],
                    title = row[PlatformNotificationsTable.title],
                    body = row[PlatformNotificationsTable.body],
                    entity_type = row[PlatformNotificationsTable.entityType],
                    entity_id = row[PlatformNotificationsTable.entityId]?.toString(),
                    deep_link = row[PlatformNotificationsTable.deepLink],
                    is_read = row[PlatformNotificationsTable.isRead],
                    created_at = row[PlatformNotificationsTable.createdAt].toString(),
                )
            }
    }

    suspend fun unreadCount(userId: UUID): Long = dbQuery {
        PlatformNotificationsTable.selectAll()
            .where { (PlatformNotificationsTable.userId eq userId) and (PlatformNotificationsTable.isRead eq false) }
            .count()
    }

    suspend fun markRead(id: UUID, userId: UUID) = dbQuery {
        PlatformNotificationsTable.update(
            where = { (PlatformNotificationsTable.id eq id) and (PlatformNotificationsTable.userId eq userId) }
        ) { it[PlatformNotificationsTable.isRead] = true }
    }

    suspend fun markAllRead(userId: UUID) = dbQuery {
        PlatformNotificationsTable.update(
            where = { PlatformNotificationsTable.userId eq userId }
        ) { it[PlatformNotificationsTable.isRead] = true }
    }
}
