package com.littlebridge.enrollplus.feature.platform

import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.PlatformDiscoveredApisTable
import com.littlebridge.enrollplus.db.PlatformApiHealthChecksTable
import com.littlebridge.enrollplus.core.EnvConfig
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable

/**
 * Runtime API health check service.
 *
 * Pings discovered API endpoints and records response status, latency, and alive status.
 * Can be triggered manually or run as a background job.
 */
object ApiHealthCheckService {

    private const val TIMEOUT_MS = 5000
    private const val SLOW_THRESHOLD_MS = 2000

    @Serializable
    data class HealthSummary(
        val total: Long,
        val alive: Long,
        val down: Long,
        val slow: Long,
        val untested: Long,
    )

    /**
     * Check a single discovered API endpoint.
     */
    suspend fun checkApi(discoveredApiId: UUID): HealthCheckDto? = withContext(Dispatchers.IO) {
        val api = dbQuery {
            PlatformDiscoveredApisTable.selectAll()
                .where { PlatformDiscoveredApisTable.id eq discoveredApiId }
                .singleOrNull()
        } ?: return@withContext null

        val method = api[PlatformDiscoveredApisTable.method]
        val path = api[PlatformDiscoveredApisTable.path]
        val baseUrl = EnvConfig.get("API_BASE_URL") ?: "http://localhost:8080"
        val fullUrl = "$baseUrl$path"

        val result = pingUrl(fullUrl, method)

        val checkId = dbQuery {
            PlatformApiHealthChecksTable.insert {
                it[PlatformApiHealthChecksTable.discoveredApiId] = discoveredApiId
                it[PlatformApiHealthChecksTable.checkedAt] = Instant.now()
                it[PlatformApiHealthChecksTable.statusCode] = result?.statusCode
                it[PlatformApiHealthChecksTable.responseMs] = result?.responseMs
                it[PlatformApiHealthChecksTable.isAlive] = result?.isAlive
                it[PlatformApiHealthChecksTable.errorMessage] = result?.errorMessage
            }[PlatformApiHealthChecksTable.id].value
        }

        // Update discovered API with latest check results
        dbQuery {
            PlatformDiscoveredApisTable.update(
                where = { PlatformDiscoveredApisTable.id eq discoveredApiId }
            ) {
                it[PlatformDiscoveredApisTable.isAlive] = result?.isAlive
                it[PlatformDiscoveredApisTable.statusCode] = result?.statusCode
                it[PlatformDiscoveredApisTable.responseMs] = result?.responseMs
                it[PlatformDiscoveredApisTable.lastCheckedAt] = Instant.now()
            }
        }

        HealthCheckDto(
            id = checkId.toString(),
            discovered_api_id = discoveredApiId.toString(),
            checked_at = Instant.now().toString(),
            status_code = result?.statusCode,
            response_ms = result?.responseMs,
            is_alive = result?.isAlive,
            error_message = result?.errorMessage,
        )
    }

    /**
     * Check all discovered APIs. Runs concurrently with limited parallelism.
     */
    suspend fun checkAll(): ScanResult = withContext(Dispatchers.IO) {
        val apis = dbQuery {
            PlatformDiscoveredApisTable.selectAll()
                .map { it[PlatformDiscoveredApisTable.id].value }
        }

        var checked = 0
        var failed = 0
        val errors = mutableListOf<String>()

        // Check in batches of 10 to avoid overwhelming the server
        for (batch in apis.chunked(10)) {
            val results = batch.map { id ->
                async {
                    try {
                        val result = checkApi(id)
                        if (result != null) checked++ else failed++
                    } catch (e: Exception) {
                        errors.add("API $id: ${e.message}")
                        failed++
                    }
                }
            }
            results.awaitAll()
        }

        ScanResult(checked, 0, failed, errors)
    }

    @Serializable
    data class ScanResult(
        val discovered: Int,
        val updated: Int,
        val stale: Int,
        val errors: List<String>,
    )

    private data class PingResult(
        val statusCode: Int?,
        val responseMs: Int?,
        val isAlive: Boolean,
        val errorMessage: String? = null,
    )

    private fun pingUrl(urlStr: String, method: String): PingResult {
        return try {
            val url = URL(urlStr)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = method
                connectTimeout = TIMEOUT_MS
                readTimeout = TIMEOUT_MS
                setRequestProperty("User-Agent", "PlatformHealthCheck/1.0")
                instanceFollowRedirects = false
            }
            val startTime = System.currentTimeMillis()
            conn.connect()
            val statusCode = conn.responseCode
            val responseMs = (System.currentTimeMillis() - startTime).toInt()
            conn.disconnect()

            val isAlive = statusCode in 200..499  // 4xx is "alive" (endpoint exists, just rejected)
            PingResult(statusCode, responseMs, isAlive)
        } catch (e: Exception) {
            PingResult(null, null, false, e.message)
        }
    }

    suspend fun summary(): HealthSummary = dbQuery {
        val total = PlatformDiscoveredApisTable.selectAll().count()
        val alive = PlatformDiscoveredApisTable.selectAll()
            .where { (PlatformDiscoveredApisTable.isAlive eq true) and PlatformDiscoveredApisTable.lastCheckedAt.isNotNull() }
            .count()
        val down = PlatformDiscoveredApisTable.selectAll()
            .where { (PlatformDiscoveredApisTable.isAlive eq false) and PlatformDiscoveredApisTable.lastCheckedAt.isNotNull() }
            .count()
        val slow = PlatformDiscoveredApisTable.selectAll()
            .where { (PlatformDiscoveredApisTable.responseMs greater SLOW_THRESHOLD_MS) and PlatformDiscoveredApisTable.lastCheckedAt.isNotNull() }
            .count()
        val untested = PlatformDiscoveredApisTable.selectAll()
            .where { PlatformDiscoveredApisTable.lastCheckedAt.isNull() }
            .count()
        HealthSummary(total, alive, down, slow, untested)
    }

    suspend fun recentChecks(limit: Int = 50): List<HealthCheckDto> = dbQuery {
        PlatformApiHealthChecksTable.selectAll()
            .orderBy(PlatformApiHealthChecksTable.checkedAt, SortOrder.DESC)
            .limit(limit)
            .map { row ->
                HealthCheckDto(
                    id = row[PlatformApiHealthChecksTable.id].value.toString(),
                    discovered_api_id = row[PlatformApiHealthChecksTable.discoveredApiId].toString(),
                    checked_at = row[PlatformApiHealthChecksTable.checkedAt].toString(),
                    status_code = row[PlatformApiHealthChecksTable.statusCode],
                    response_ms = row[PlatformApiHealthChecksTable.responseMs],
                    is_alive = row[PlatformApiHealthChecksTable.isAlive],
                    error_message = row[PlatformApiHealthChecksTable.errorMessage],
                )
            }
    }
}
