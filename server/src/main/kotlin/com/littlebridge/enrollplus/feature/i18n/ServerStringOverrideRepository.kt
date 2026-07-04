/*
 * File: ServerStringOverrideRepository.kt
 * Module: feature.i18n
 *
 * CRUD for server_string_overrides table (FR-018).
 * Loads DB overrides into ServerStrings in-memory cache at startup.
 * Updates cache immediately on upsert/delete — no restart needed.
 *
 * Spec ref: MULTI_LANGUAGE_SPEC.md §8.4
 */
package com.littlebridge.enrollplus.feature.i18n

import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.ServerStringOverridesTable
import com.littlebridge.enrollplus.db.ServerStringOverrideHistoryTable
import com.littlebridge.enrollplus.db.AppUsersTable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.SortOrder
import java.time.Instant
import java.util.UUID

@Serializable
data class ServerStringOverrideEntry(
    @SerialName("string_key") val stringKey: String,
    val lang: String,
    val value: String,
    @SerialName("updated_by") val updatedBy: String?,
    @SerialName("updated_at") val updatedAt: String,
)

object ServerStringOverrideRepository {

    suspend fun loadAllIntoCache() {
        dbQuery {
            ServerStringOverridesTable.selectAll().forEach { row ->
                ServerStrings.setOverride(
                    row[ServerStringOverridesTable.stringKey],
                    row[ServerStringOverridesTable.lang],
                    row[ServerStringOverridesTable.value],
                )
            }
        }
    }

    suspend fun upsert(key: String, lang: String, value: String, updatedBy: UUID) {
        val now = Instant.now()
        val existing = dbQuery {
            ServerStringOverridesTable
                .selectAll()
                .where {
                    (ServerStringOverridesTable.stringKey eq key) and
                        (ServerStringOverridesTable.lang eq lang)
                }
                .singleOrNull()
        }
        val oldValue = existing?.get(ServerStringOverridesTable.value)

        if (existing != null) {
            dbQuery {
                ServerStringOverridesTable.update({
                    (ServerStringOverridesTable.stringKey eq key) and
                        (ServerStringOverridesTable.lang eq lang)
                }) {
                    it[ServerStringOverridesTable.value] = value
                    it[ServerStringOverridesTable.updatedBy] = updatedBy
                    it[ServerStringOverridesTable.updatedAt] = now
                }
            }
        } else {
            dbQuery {
                ServerStringOverridesTable.insert {
                    it[ServerStringOverridesTable.stringKey] = key
                    it[ServerStringOverridesTable.lang] = lang
                    it[ServerStringOverridesTable.value] = value
                    it[ServerStringOverridesTable.updatedBy] = updatedBy
                    it[ServerStringOverridesTable.updatedAt] = now
                }
            }
        }

        // Audit log
        dbQuery {
            ServerStringOverrideHistoryTable.insert {
                it[ServerStringOverrideHistoryTable.stringKey] = key
                it[ServerStringOverrideHistoryTable.lang] = lang
                it[ServerStringOverrideHistoryTable.oldValue] = oldValue
                it[ServerStringOverrideHistoryTable.newValue] = value
                it[ServerStringOverrideHistoryTable.action] = "upsert"
                it[ServerStringOverrideHistoryTable.changedBy] = updatedBy
                it[ServerStringOverrideHistoryTable.changedAt] = now
            }
        }

        ServerStrings.setOverride(key, lang, value)
    }

    suspend fun delete(key: String, lang: String, deletedBy: UUID? = null): Boolean {
        val existing = dbQuery {
            ServerStringOverridesTable
                .selectAll()
                .where {
                    (ServerStringOverridesTable.stringKey eq key) and
                        (ServerStringOverridesTable.lang eq lang)
                }
                .singleOrNull()
        }
        val oldValue = existing?.get(ServerStringOverridesTable.value)

        val deleted = dbQuery {
            ServerStringOverridesTable.deleteWhere {
                (ServerStringOverridesTable.stringKey eq key) and
                    (ServerStringOverridesTable.lang eq lang)
            } > 0
        }
        if (deleted) {
            // Audit log
            dbQuery {
                ServerStringOverrideHistoryTable.insert {
                    it[ServerStringOverrideHistoryTable.stringKey] = key
                    it[ServerStringOverrideHistoryTable.lang] = lang
                    it[ServerStringOverrideHistoryTable.oldValue] = oldValue
                    it[ServerStringOverrideHistoryTable.newValue] = ""
                    it[ServerStringOverrideHistoryTable.action] = "delete"
                    it[ServerStringOverrideHistoryTable.changedBy] = deletedBy
                    it[ServerStringOverrideHistoryTable.changedAt] = Instant.now()
                }
            }
            ServerStrings.removeOverride(key, lang)
        }
        return deleted
    }

    suspend fun getAll(): List<ServerStringOverrideEntry> {
        return dbQuery {
            ServerStringOverridesTable.selectAll()
                .map {
                    ServerStringOverrideEntry(
                        stringKey = it[ServerStringOverridesTable.stringKey],
                        lang = it[ServerStringOverridesTable.lang],
                        value = it[ServerStringOverridesTable.value],
                        updatedBy = it[ServerStringOverridesTable.updatedBy]?.toString(),
                        updatedAt = it[ServerStringOverridesTable.updatedAt].toString(),
                    )
                }
        }
    }

    suspend fun getHistory(
        keyFilter: String? = null,
        langFilter: String? = null,
        limit: Int = 100,
    ): List<HistoryEntry> {
        return dbQuery {
            val query = ServerStringOverrideHistoryTable.selectAll()
            val filtered = when {
                keyFilter != null && langFilter != null ->
                    query.where {
                        (ServerStringOverrideHistoryTable.stringKey eq keyFilter) and
                            (ServerStringOverrideHistoryTable.lang eq langFilter)
                    }
                keyFilter != null ->
                    query.where { ServerStringOverrideHistoryTable.stringKey eq keyFilter }
                langFilter != null ->
                    query.where { ServerStringOverrideHistoryTable.lang eq langFilter }
                else -> query
            }
            filtered
                .orderBy(ServerStringOverrideHistoryTable.changedAt, SortOrder.DESC)
                .limit(limit)
                .map { row ->
                    HistoryEntry(
                        id = row[ServerStringOverrideHistoryTable.id].value.toString(),
                        stringKey = row[ServerStringOverrideHistoryTable.stringKey],
                        lang = row[ServerStringOverrideHistoryTable.lang],
                        oldValue = row[ServerStringOverrideHistoryTable.oldValue],
                        newValue = row[ServerStringOverrideHistoryTable.newValue],
                        action = row[ServerStringOverrideHistoryTable.action],
                        changedBy = row[ServerStringOverrideHistoryTable.changedBy]?.toString(),
                        changedAt = row[ServerStringOverrideHistoryTable.changedAt].toString(),
                    )
                }
        }
    }

    suspend fun resolveAdminName(userId: String): String? {
        return dbQuery {
            AppUsersTable.selectAll()
                .where { AppUsersTable.id eq UUID.fromString(userId) }
                .singleOrNull()
                ?.get(AppUsersTable.fullName)
        }
    }
}

@Serializable
data class HistoryEntry(
    val id: String,
    @SerialName("string_key") val stringKey: String,
    val lang: String,
    @SerialName("old_value") val oldValue: String?,
    @SerialName("new_value") val newValue: String,
    val action: String,
    @SerialName("changed_by") val changedBy: String?,
    @SerialName("changed_at") val changedAt: String,
)
