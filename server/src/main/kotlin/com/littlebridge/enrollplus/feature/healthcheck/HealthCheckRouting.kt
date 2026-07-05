package com.littlebridge.enrollplus.feature.healthcheck

import com.littlebridge.enrollplus.db.DatabaseFactory
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

@Serializable
data class HealthResponse(
    val status: String,
    val timestamp: String,
    val uptimeSeconds: Long,
    val db: String,
    val seedWarning: String? = null,
)

private val startTime = System.currentTimeMillis()

fun Route.healthCheckRouting() {
    route("/api/v1/health") {
        get {
            val dbStatus = runCatching {
                transaction { exec("SELECT 1"); true } ?: false
            }.getOrDefault(false)

            call.respond(
                HealthResponse(
                    status = if (dbStatus) "ok" else "degraded",
                    timestamp = Instant.now().toString(),
                    uptimeSeconds = (System.currentTimeMillis() - startTime) / 1000,
                    db = if (dbStatus) "up" else "down",
                    seedWarning = DatabaseFactory.seedFailure,
                )
            )
        }
    }
}
