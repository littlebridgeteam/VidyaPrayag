package com.littlebridge.enrollplus.core

import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.FeatureFlagsTable
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

@Serializable
data class FeatureFlagResponse(
    val scope: String,
    val key: String,
    val isEnabled: Boolean,
    val value: String? = null,
    val description: String? = null,
)

@Serializable
data class FeatureFlagUpdateRequest(
    val isEnabled: Boolean? = null,
    val value: String? = null,
)

/**
 * Admin route for reading and toggling feature flags.
 *
 *   GET  /api/v1/admin/flags              — list all flags
 *   GET  /api/v1/admin/flags/{scope}      — list flags for a scope
 *   POST /api/v1/admin/flags/{scope}/{key} — update a flag
 */
fun Route.featureFlagRouting() {
    authenticate("jwt") {
        route("/api/v1/admin/flags") {
            get {
                if (call.requirePlatformAdmin() == null) return@get
                val flags = dbQuery {
                    FeatureFlagsTable.selectAll()
                        .orderBy(FeatureFlagsTable.scope to SortOrder.ASC)
                        .orderBy(FeatureFlagsTable.key to SortOrder.ASC)
                        .map { row ->
                            FeatureFlagResponse(
                                scope = row[FeatureFlagsTable.scope],
                                key = row[FeatureFlagsTable.key],
                                isEnabled = row[FeatureFlagsTable.isEnabled],
                                value = row[FeatureFlagsTable.value],
                                description = row[FeatureFlagsTable.description],
                            )
                        }
                }
                call.ok(flags)
            }

            get("/{scope}") {
                if (call.requirePlatformAdmin() == null) return@get
                val scope = call.parameters["scope"] ?: return@get call.fail("Missing scope")
                val flags = dbQuery {
                    FeatureFlagsTable.selectAll()
                        .where { FeatureFlagsTable.scope eq scope }
                        .orderBy(FeatureFlagsTable.key to SortOrder.ASC)
                        .map { row ->
                            FeatureFlagResponse(
                                scope = row[FeatureFlagsTable.scope],
                                key = row[FeatureFlagsTable.key],
                                isEnabled = row[FeatureFlagsTable.isEnabled],
                                value = row[FeatureFlagsTable.value],
                                description = row[FeatureFlagsTable.description],
                            )
                        }
                }
                call.ok(flags)
            }

            post("/{scope}/{key}") {
                if (call.requirePlatformAdmin() == null) return@post
                val scope = call.parameters["scope"] ?: return@post call.fail("Missing scope")
                val key = call.parameters["key"] ?: return@post call.fail("Missing key")
                val req = runCatching { call.receive<FeatureFlagUpdateRequest>() }.getOrNull()
                    ?: return@post call.fail("Invalid request body")

                val rowsUpdated = dbQuery {
                    FeatureFlagsTable.update(
                        { (FeatureFlagsTable.scope eq scope) and (FeatureFlagsTable.key eq key) }
                    ) {
                        if (req.isEnabled != null) it[FeatureFlagsTable.isEnabled] = req.isEnabled
                        if (req.value != null) it[FeatureFlagsTable.value] = req.value
                        it[FeatureFlagsTable.updatedAt] = java.time.Instant.now()
                    }
                }
                if (rowsUpdated == 0) {
                    return@post call.fail("Feature flag not found: $scope/$key", io.ktor.http.HttpStatusCode.NotFound)
                }
                FeatureFlagService.reload()

                call.ok(mapOf("ok" to true, "scope" to scope, "key" to key))
            }
        }
    }
}
