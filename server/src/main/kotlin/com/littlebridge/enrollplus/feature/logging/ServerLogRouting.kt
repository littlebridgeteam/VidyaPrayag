/*
 * File: ServerLogRouting.kt
 * Module: feature.logging
 *
 * Super-admin-only endpoints for querying structured server logs.
 *
 *   GET  /api/v1/admin/dev/logs          — paginated log query with filtering
 *   GET  /api/v1/admin/dev/logs/stream   — SSE stream (real-time)
 *   GET  /api/v1/admin/dev/logs/stats    — aggregate stats (counts, top errors, AI token usage)
 *
 * All endpoints guarded by requireSuperAdmin() — same pattern as DevToolsRouting.
 */
package com.littlebridge.enrollplus.feature.logging

import com.littlebridge.enrollplus.core.ok
import com.littlebridge.enrollplus.core.principalUserUuid
import com.littlebridge.enrollplus.db.AppUsersTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.ServerLogsTable
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.sse.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import java.time.Instant
import java.util.UUID

@Serializable
data class ServerLogDto(
    val id: String,
    val timestamp: String,
    val level: String,
    val category: String,
    val message: String,
    @SerialName("actor_id") val actorId: String? = null,
    val endpoint: String? = null,
    @SerialName("status_code") val statusCode: Int? = null,
    @SerialName("duration_ms") val durationMs: Long? = null,
    val details: JsonObject = JsonObject(emptyMap()),
)

@Serializable
data class ServerLogsPageDto(
    val logs: List<ServerLogDto> = emptyList(),
    val total: Int = 0,
    val offset: Int = 0,
    val limit: Int = 100,
)

@Serializable
data class ServerLogStatsDto(
    @SerialName("by_level") val byLevel: Map<String, Int> = emptyMap(),
    @SerialName("by_category") val byCategory: Map<String, Int> = emptyMap(),
    @SerialName("total_last_24h") val totalLast24h: Int = 0,
    @SerialName("top_errors") val topErrors: List<ServerLogDto> = emptyList(),
    @SerialName("ai_token_usage") val aiTokenUsage: AiTokenUsageSummary = AiTokenUsageSummary(),
)

@Serializable
data class AiTokenUsageSummary(
    @SerialName("total_requests") val totalRequests: Int = 0,
    @SerialName("total_input_tokens") val totalInputTokens: Int = 0,
    @SerialName("total_output_tokens") val totalOutputTokens: Int = 0,
    @SerialName("total_errors") val totalErrors: Int = 0,
    @SerialName("avg_latency_ms") val avgLatencyMs: Double = 0.0,
    @SerialName("by_model") val byModel: Map<String, Int> = emptyMap(),
)

private val validLevels = setOf("TRACE", "DEBUG", "INFO", "WARN", "ERROR")
private val validCategories = setOf("http", "ai", "job", "auth", "notification", "pews", "sync", "general")

private suspend fun ApplicationCall.requireSuperAdminLog(): UUID? {
    val uid = principalUserUuid() ?: run {
        respond(HttpStatusCode.Unauthorized); return null
    }
    val role = dbQuery {
        AppUsersTable.selectAll().where { AppUsersTable.id eq uid }
            .singleOrNull()?.get(AppUsersTable.role)
    }
    if (role != "super_admin") {
        respond(HttpStatusCode.Forbidden); return null
    }
    return uid
}

private fun parseDetails(jsonStr: String): JsonObject {
    return runCatching {
        kotlinx.serialization.json.Json.parseToJsonElement(jsonStr) as? JsonObject
    }.getOrNull() ?: JsonObject(emptyMap())
}

private fun rowToDto(row: org.jetbrains.exposed.sql.ResultRow): ServerLogDto = ServerLogDto(
    id = row[ServerLogsTable.id].value.toString(),
    timestamp = row[ServerLogsTable.timestamp].toString(),
    level = row[ServerLogsTable.level],
    category = row[ServerLogsTable.category],
    message = row[ServerLogsTable.message],
    actorId = row[ServerLogsTable.actorId]?.toString(),
    endpoint = row[ServerLogsTable.endpoint],
    statusCode = row[ServerLogsTable.statusCode],
    durationMs = row[ServerLogsTable.durationMs],
    details = parseDetails(row[ServerLogsTable.detailsJson]),
)

fun Route.serverLogRouting() {
    authenticate("jwt") {
        route("/api/v1/admin/dev/logs") {

            // -------- paginated log query --------
            get {
                if (call.requireSuperAdminLog() == null) return@get

                val level = call.request.queryParameters["level"]?.uppercase()
                val category = call.request.queryParameters["category"]?.lowercase()
                val search = call.request.queryParameters["search"]
                val schoolId = call.request.queryParameters["schoolId"]?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                }
                val since = call.request.queryParameters["since"]?.let {
                    runCatching { Instant.parse(it) }.getOrNull()
                }
                val until = call.request.queryParameters["until"]?.let {
                    runCatching { Instant.parse(it) }.getOrNull()
                }
                val limit = (call.request.queryParameters["limit"]?.toIntOrNull() ?: 100).coerceAtMost(500).coerceAtLeast(1)
                val offset = (call.request.queryParameters["offset"]?.toIntOrNull() ?: 0).coerceAtLeast(0)

                if (level != null && level !in validLevels) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid level. Valid: $validLevels"))
                    return@get
                }
                if (category != null && category !in validCategories) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid category. Valid: $validCategories"))
                    return@get
                }

                val page = dbQuery {
                    val query = ServerLogsTable.selectAll().where {
                        val conditions = mutableListOf<org.jetbrains.exposed.sql.Op<Boolean>>()
                        if (level != null) conditions += ServerLogsTable.level eq level
                        if (category != null) conditions += ServerLogsTable.category eq category
                        if (schoolId != null) conditions += ServerLogsTable.schoolId eq schoolId
                        if (since != null) conditions += ServerLogsTable.timestamp greater since
                        if (until != null) conditions += ServerLogsTable.timestamp less until
                        if (search != null) conditions += ServerLogsTable.message like "%$search%"
                        if (conditions.isEmpty()) org.jetbrains.exposed.sql.Op.TRUE
                        else conditions.reduce { acc, op -> acc and op }
                    }

                    val total = query.count().toInt()

                    val logs = query
                        .orderBy(ServerLogsTable.timestamp, SortOrder.DESC)
                        .limit(limit, offset = offset.toLong())
                        .map { row -> rowToDto(row) }

                    ServerLogsPageDto(logs = logs, total = total, offset = offset, limit = limit)
                }

                call.ok(page, message = "Logs fetched")
            }

            // -------- SSE stream (real-time) --------
            sse("/stream") {
                val uid = call.principalUserUuid() ?: run {
                    call.respond(HttpStatusCode.Unauthorized); return@sse
                }
                val role = dbQuery {
                    AppUsersTable.selectAll().where { AppUsersTable.id eq uid }
                        .singleOrNull()?.get(AppUsersTable.role)
                }
                if (role != "super_admin") {
                    call.respond(HttpStatusCode.Forbidden); return@sse
                }

                val level = call.request.queryParameters["level"]?.uppercase()
                val category = call.request.queryParameters["category"]?.lowercase()

                val initLogs = dbQuery {
                    val query = ServerLogsTable.selectAll().where {
                        val conditions = mutableListOf<org.jetbrains.exposed.sql.Op<Boolean>>()
                        if (level != null) conditions += ServerLogsTable.level eq level
                        if (category != null) conditions += ServerLogsTable.category eq category
                        if (conditions.isEmpty()) org.jetbrains.exposed.sql.Op.TRUE
                        else conditions.reduce { acc, op -> acc and op }
                    }
                    query.orderBy(ServerLogsTable.timestamp, SortOrder.DESC).limit(50)
                        .map { row -> rowToDto(row) }
                }
                val initEvent = ServerLogsPageDto(logs = initLogs, total = initLogs.size, offset = 0, limit = 50)
                val json = kotlinx.serialization.json.Json { encodeDefaults = true }

                send(json.encodeToString(ServerLogsPageDto.serializer(), initEvent))
                while (true) {
                    kotlinx.coroutines.delay(15_000)
                    send("heartbeat")
                }
            }

            // -------- aggregate stats --------
            get("/stats") {
                if (call.requireSuperAdminLog() == null) return@get

                val stats = dbQuery {
                    val now = Instant.now()
                    val oneDayAgo = now.minusSeconds(86_400)

                    val last24h = ServerLogsTable.selectAll().where { ServerLogsTable.timestamp greater oneDayAgo }

                    val byLevel = mutableMapOf<String, Int>()
                    val byCategory = mutableMapOf<String, Int>()
                    for (row in last24h) {
                        val lvl = row[ServerLogsTable.level]
                        val cat = row[ServerLogsTable.category]
                        byLevel[lvl] = (byLevel[lvl] ?: 0) + 1
                        byCategory[cat] = (byCategory[cat] ?: 0) + 1
                    }

                    val totalLast24h = last24h.count().toInt()

                    val topErrors = ServerLogsTable.selectAll()
                        .where { ServerLogsTable.level eq "ERROR" }
                        .orderBy(ServerLogsTable.timestamp, SortOrder.DESC)
                        .limit(10)
                        .map { row -> rowToDto(row) }

                    // AI token usage summary from logs with category "ai"
                    val aiLogs = ServerLogsTable.selectAll().where { (ServerLogsTable.timestamp greater oneDayAgo) and (ServerLogsTable.category eq "ai") }
                    var totalInputTokens = 0
                    var totalOutputTokens = 0
                    var totalErrors = 0
                    var totalLatency = 0L
                    val byModel = mutableMapOf<String, Int>()
                    var aiCount = 0

                    for (row in aiLogs) {
                        aiCount++
                        val details = parseDetails(row[ServerLogsTable.detailsJson])
                        details["input_tokens"]?.let { (it as? JsonPrimitive)?.content?.toIntOrNull() }?.let { totalInputTokens += it }
                        details["output_tokens"]?.let { (it as? JsonPrimitive)?.content?.toIntOrNull() }?.let { totalOutputTokens += it }
                        details["model"]?.let { (it as? JsonPrimitive)?.content }?.let { byModel[it] = (byModel[it] ?: 0) + 1 }
                        row[ServerLogsTable.durationMs]?.let { totalLatency += it }
                        if (row[ServerLogsTable.statusCode]?.let { it in 400..599 } == true) totalErrors++
                    }

                    ServerLogStatsDto(
                        byLevel = byLevel,
                        byCategory = byCategory,
                        totalLast24h = totalLast24h,
                        topErrors = topErrors,
                        aiTokenUsage = AiTokenUsageSummary(
                            totalRequests = aiCount,
                            totalInputTokens = totalInputTokens,
                            totalOutputTokens = totalOutputTokens,
                            totalErrors = totalErrors,
                            avgLatencyMs = if (aiCount > 0) totalLatency.toDouble() / aiCount else 0.0,
                            byModel = byModel,
                        ),
                    )
                }

                call.ok(stats, message = "Stats fetched")
            }

            // -------- toggle HTTP request logging --------
            get("/http-logging-toggle") {
                if (call.requireSuperAdminLog() == null) return@get
                call.ok(mapOf("enabled" to ServerLogWriter.isHttpLoggingEnabled()), message = "HTTP logging status")
            }

            post("/http-logging-toggle") {
                if (call.requireSuperAdminLog() == null) return@post
                val enabled = call.request.queryParameters["enabled"]?.toBooleanStrictOrNull()
                if (enabled == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing or invalid 'enabled' query param"))
                    return@post
                }
                ServerLogWriter.setHttpLoggingEnabled(enabled)
                call.ok(mapOf("enabled" to enabled), message = if (enabled) "HTTP logging enabled" else "HTTP logging disabled")
            }
        }
    }
}
