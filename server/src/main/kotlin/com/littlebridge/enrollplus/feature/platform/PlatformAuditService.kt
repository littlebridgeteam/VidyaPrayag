package com.littlebridge.enrollplus.feature.platform

import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.PlatformAuditLogTable
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.UUID
import java.time.Instant

object PlatformAuditService {

    suspend fun log(
        actorId: UUID?,
        action: String,
        entityType: String,
        entityId: UUID? = null,
        oldSnapshot: String? = null,
        newSnapshot: String? = null,
        call: ApplicationCall? = null,
    ) {
        dbQuery {
            PlatformAuditLogTable.insert {
                it[PlatformAuditLogTable.actorId] = actorId
                it[PlatformAuditLogTable.action] = action
                it[PlatformAuditLogTable.entityType] = entityType
                it[PlatformAuditLogTable.entityId] = entityId
                it[PlatformAuditLogTable.oldSnapshot] = oldSnapshot
                it[PlatformAuditLogTable.newSnapshot] = newSnapshot
                it[PlatformAuditLogTable.ipAddress] = call?.request?.local?.remoteHost
                it[PlatformAuditLogTable.userAgent] = call?.request?.headers?.get("User-Agent")
                it[PlatformAuditLogTable.createdAt] = Instant.now()
            }
        }
    }

    suspend fun list(
        actorId: UUID? = null,
        entityType: String? = null,
        action: String? = null,
        page: Int = 1,
        pageSize: Int = 25,
    ): Pair<List<AuditLogDto>, Long> = dbQuery {
        val conditions = Op.build {
            (if (actorId != null) PlatformAuditLogTable.actorId eq actorId else Op.TRUE) and
            (if (entityType != null) PlatformAuditLogTable.entityType eq entityType else Op.TRUE) and
            (if (action != null) PlatformAuditLogTable.action eq action else Op.TRUE)
        }
        val total = PlatformAuditLogTable.selectAll().where { conditions }.count()
        val items = PlatformAuditLogTable.selectAll().where { conditions }
            .orderBy(PlatformAuditLogTable.createdAt, SortOrder.DESC)
            .limit(pageSize, ((page - 1) * pageSize).toLong())
            .map { row ->
                AuditLogDto(
                    id = row[PlatformAuditLogTable.id].value.toString(),
                    actor_id = row[PlatformAuditLogTable.actorId]?.toString(),
                    actor_name = null,
                    action = row[PlatformAuditLogTable.action],
                    entity_type = row[PlatformAuditLogTable.entityType],
                    entity_id = row[PlatformAuditLogTable.entityId]?.toString(),
                    old_snapshot = row[PlatformAuditLogTable.oldSnapshot],
                    new_snapshot = row[PlatformAuditLogTable.newSnapshot],
                    ip_address = row[PlatformAuditLogTable.ipAddress],
                    user_agent = row[PlatformAuditLogTable.userAgent],
                    created_at = row[PlatformAuditLogTable.createdAt].toString(),
                )
            }
        items to total
    }
}
