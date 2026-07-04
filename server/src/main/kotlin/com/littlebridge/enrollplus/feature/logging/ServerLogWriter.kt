/*
 * File: ServerLogWriter.kt
 * Module: feature.logging
 *
 * Singleton structured log writer — same pattern as AuditLogger.
 * Fire-and-forget via CoroutineScope(Dispatchers.IO).launch.
 *
 * Dual-write: structured DB row (server_logs) + SLF4J console/file log.
 * Auto-truncates message to 2000 chars; detailsJson to 8000 chars.
 * Rate-limited: max 1000 rows/minute (drops with SLF4J warn).
 * Table cap: 100,000 rows; oldest pruned first.
 */
package com.littlebridge.enrollplus.feature.logging

import com.littlebridge.enrollplus.db.AppConfigTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.ServerLogsTable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

object ServerLogWriter {

    private val logger = LoggerFactory.getLogger("ServerLogWriter")
    private val scope = CoroutineScope(Dispatchers.IO)

    private const val MAX_MESSAGE_LENGTH = 2000
    private const val MAX_DETAILS_LENGTH = 8000
    private const val MAX_ROWS_PER_MINUTE = 1000
    private const val TABLE_CAP = 100_000

    private val writeCount = AtomicInteger(0)
    private var windowStartMs = System.currentTimeMillis()
    private val rateSemaphore = Semaphore(1)

    /** Runtime toggle: when false, ALL log categories are skipped entirely
     *  (both SLF4J and DB). Toggled via the super-admin Log Viewer UI.
     *  Persisted in app_config so it survives server restarts. */
    private val loggingEnabled = AtomicBoolean(true)

    fun isLoggingEnabled(): Boolean = loggingEnabled.get()

    /** Load persisted toggle state from app_config on server startup. */
    suspend fun initFromConfig() {
        runCatching {
            dbQuery {
                AppConfigTable.selectAll()
                    .where { AppConfigTable.key eq "server_logging_enabled" }
                    .singleOrNull()?.get(AppConfigTable.value)
            }
        }.getOrNull()?.let {
            loggingEnabled.set(it.toBooleanStrictOrNull() ?: true)
        }
        logger.info("ServerLogWriter initialized: loggingEnabled={}", loggingEnabled.get())
    }

    /** Persist toggle state to app_config so it survives restarts. */
    private suspend fun persistEnabled(enabled: Boolean) {
        runCatching {
            dbQuery {
                val existing = AppConfigTable.selectAll()
                    .where { AppConfigTable.key eq "server_logging_enabled" }
                    .singleOrNull()
                if (existing != null) {
                    AppConfigTable.update({ AppConfigTable.key eq "server_logging_enabled" }) {
                        it[value] = enabled.toString()
                        it[updatedAt] = Instant.now()
                    }
                } else {
                    AppConfigTable.insert {
                        it[key] = "server_logging_enabled"
                        it[value] = enabled.toString()
                        it[updatedAt] = Instant.now()
                    }
                }
            }
        }.onFailure { e ->
            logger.error("Failed to persist logging toggle: {}", e.message)
        }
    }

    suspend fun setLoggingEnabled(enabled: Boolean) {
        loggingEnabled.set(enabled)
        persistEnabled(enabled)
        logger.info("ServerLogWriter toggle set: loggingEnabled={}", enabled)
    }

    suspend fun write(
        level: String,
        category: String,
        message: String,
        schoolId: UUID? = null,
        actorId: UUID? = null,
        endpoint: String? = null,
        statusCode: Int? = null,
        durationMs: Long? = null,
        details: Map<String, Any?> = emptyMap(),
    ) {
        // Runtime toggle: skip ALL categories when disabled
        if (!loggingEnabled.get()) return

        // Rate limiting: max 1000 rows/minute
        val now = System.currentTimeMillis()
        rateSemaphore.withPermit {
            if (now - windowStartMs > 60_000) {
                writeCount.set(0)
                windowStartMs = now
            }
        }
        if (writeCount.incrementAndGet() > MAX_ROWS_PER_MINUTE) {
            logger.warn("ServerLogWriter rate limit exceeded ({} rows/min), dropping log: {}", MAX_ROWS_PER_MINUTE, message.take(100))
            return
        }

        val truncatedMessage = if (message.length > MAX_MESSAGE_LENGTH) {
            message.take(MAX_MESSAGE_LENGTH)
        } else {
            message
        }

        val detailsJson = serializeDetails(details)
        val truncatedDetails = if (detailsJson.length > MAX_DETAILS_LENGTH) {
            detailsJson.take(MAX_DETAILS_LENGTH) + "...[truncated]"
        } else {
            detailsJson
        }

        // Dual-write: SLF4J + DB
        // Suppress repetitive "http" category logs from SLF4J console — they
        // still go to the DB for the Log Viewer, but don't spam the console.
        // Non-http categories (auth, ai, job, notification, pews, etc.) always
        // log to both SLF4J and DB. Errors/Warns always log regardless.
        val suppressSlf4j = category == "http" && level.uppercase() == "INFO"
        if (!suppressSlf4j) {
            val slf4jLevel = when (level.uppercase()) {
                "ERROR" -> org.slf4j.event.Level.ERROR
                "WARN" -> org.slf4j.event.Level.WARN
                "INFO" -> org.slf4j.event.Level.INFO
                "DEBUG" -> org.slf4j.event.Level.DEBUG
                "TRACE" -> org.slf4j.event.Level.TRACE
                else -> org.slf4j.event.Level.INFO
            }
            logger.atLevel(slf4jLevel).setMessage("[{}] {}").addArgument(category).addArgument(truncatedMessage).log()
        }

        // Fire-and-forget DB write
        scope.launch {
            runCatching {
                dbQuery {
                    // Table cap check: prune oldest if over 100k rows
                    val count = ServerLogsTable.selectAll().count().toInt()
                    if (count >= TABLE_CAP) {
                        val toDelete = count - TABLE_CAP + 1
                        val oldestIds = ServerLogsTable.selectAll()
                            .orderBy(ServerLogsTable.timestamp, SortOrder.ASC)
                            .limit(toDelete)
                            .map { it[ServerLogsTable.id] }
                        if (oldestIds.isNotEmpty()) {
                            ServerLogsTable.deleteWhere { ServerLogsTable.id inList oldestIds }
                        }
                    }

                    ServerLogsTable.insert {
                        it[ServerLogsTable.schoolId] = schoolId
                        it[ServerLogsTable.timestamp] = Instant.now()
                        it[ServerLogsTable.level] = level.uppercase()
                        it[ServerLogsTable.category] = category
                        it[ServerLogsTable.message] = truncatedMessage
                        it[ServerLogsTable.actorId] = actorId
                        it[ServerLogsTable.endpoint] = endpoint
                        it[ServerLogsTable.statusCode] = statusCode
                        it[ServerLogsTable.durationMs] = durationMs
                        it[ServerLogsTable.detailsJson] = truncatedDetails
                        it[ServerLogsTable.createdAt] = Instant.now()
                    }
                }
            }.onFailure { e ->
                logger.error("Failed to write server_log to DB: {}", e.message)
            }
        }
    }

    private fun serializeDetails(details: Map<String, Any?>): String {
        if (details.isEmpty()) return "{}"
        return runCatching {
            val json = kotlinx.serialization.json.Json
            val mapSerializer = kotlinx.serialization.builtins.MapSerializer(
                kotlinx.serialization.serializer<String>(),
                kotlinx.serialization.json.JsonElement.serializer(),
            )
            json.encodeToString(mapSerializer, details.mapValues { (_, v) ->
                when (v) {
                    null -> kotlinx.serialization.json.JsonNull
                    is String -> kotlinx.serialization.json.JsonPrimitive(v)
                    is Number -> kotlinx.serialization.json.JsonPrimitive(v)
                    is Boolean -> kotlinx.serialization.json.JsonPrimitive(v)
                    else -> kotlinx.serialization.json.JsonPrimitive(v.toString())
                }
            })
        }.getOrElse { "{}" }
    }
}
