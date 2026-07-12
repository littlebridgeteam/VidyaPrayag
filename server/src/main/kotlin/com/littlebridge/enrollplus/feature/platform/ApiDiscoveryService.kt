package com.littlebridge.enrollplus.feature.platform

import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.PlatformDiscoveredApisTable
import com.littlebridge.enrollplus.db.PlatformFeatureApisTable
import com.littlebridge.enrollplus.db.PlatformApiHealthChecksTable
import org.jetbrains.exposed.sql.*
import java.io.File
import java.time.Instant
import java.util.UUID
import kotlinx.serialization.Serializable

/**
 * Auto-discovery service for Ktor API routes.
 *
 * Scans server/src/main/kotlin/com/littlebridge/enrollplus/feature/**/*Routing.kt
 * Extracts HTTP method + path from route() declarations.
 */
object ApiDiscoveryService {

    private const val ROUTING_ROOT = "server/src/main/kotlin/com/littlebridge/enrollplus/feature"

    @Serializable
    data class ScanResult(
        val discovered: Int,
        val updated: Int,
        val stale: Int,
        val errors: List<String>,
    )

    private data class RouteEntry(
        val method: String,
        val path: String,
        val filePath: String,
        val packageName: String,
    )

    suspend fun scan(): ScanResult {
        val errors = mutableListOf<String>()
        var discovered = 0
        var updated = 0

        try {
            val rootDir = File(ROUTING_ROOT)
            if (!rootDir.exists()) {
                return ScanResult(0, 0, 0, listOf("Routing root not found: $ROUTING_ROOT"))
            }

            val routingFiles = rootDir.walkTopDown()
                .filter { it.isFile && it.name.endsWith("Routing.kt") }
                .toList()

            val allRoutes = mutableListOf<RouteEntry>()

            for (file in routingFiles) {
                try {
                    val relativePath = file.relativeTo(File(".")).path
                    val pkg = file.parentFile?.relativeTo(rootDir)?.path?.replace("/", ".") ?: ""
                    val routes = parseRoutes(file.readText(), relativePath, pkg)
                    allRoutes.addAll(routes)
                } catch (e: Exception) {
                    errors.add("Parse ${file.name}: ${e.message}")
                }
            }

            for (route in allRoutes) {
                val result = upsertDiscoveredApi(route)
                if (result == UpsertResult.INSERTED) discovered++ else if (result == UpsertResult.UPDATED) updated++
            }
        } catch (e: Exception) {
            errors.add("Scan error: ${e.message}")
        }

        return ScanResult(discovered, updated, 0, errors)
    }

    private fun parseRoutes(content: String, filePath: String, pkg: String): List<RouteEntry> {
        val routes = mutableListOf<RouteEntry>()

        // Match route("path") { get { ... } post { ... } }
        // Also match standalone get("path"), post("path"), etc.
        val routeBlockRegex = Regex("""route\(\s*"([^"]+)"\s*\)\s*\{""")
        val methodRegex = Regex("""\b(get|post|put|patch|delete|head|options)\s*(?:\(\s*"([^"]*)"\s*\))?\s*\{""")

        // Find route blocks and their methods
        val routeBlocks = routeBlockRegex.findAll(content).toList()
        for (routeBlock in routeBlocks) {
            val basePath = routeBlock.groupValues[1]
            // Find methods within this block (search forward from the route block start)
            val startIndex = routeBlock.range.last
            val endIndex = findMatchingBrace(content, startIndex)
            val blockContent = content.substring(startIndex, endIndex.coerceAtMost(content.length))

            for (methodMatch in methodRegex.findAll(blockContent)) {
                val method = methodMatch.groupValues[1].uppercase()
                val subPath = methodMatch.groupValues.getOrNull(2) ?: ""
                val fullPath = normalizePath(basePath, subPath)
                routes.add(RouteEntry(method, fullPath, filePath, pkg))
            }
        }

        // Also find standalone method calls outside route blocks
        val standaloneRegex = Regex("""\b(get|post|put|patch|delete)\s*\(\s*"([^"]+)"\s*\)\s*\{""")
        for (match in standaloneRegex.findAll(content)) {
            val method = match.groupValues[1].uppercase()
            val path = match.groupValues[2]
            // Skip if already captured in a route block
            if (routes.none { it.method == method && it.path == path }) {
                routes.add(RouteEntry(method, path, filePath, pkg))
            }
        }

        return routes
    }

    private fun findMatchingBrace(content: String, startIndex: Int): Int {
        var depth = 0
        for (i in startIndex until content.length) {
            when (content[i]) {
                '{' -> depth++
                '}' -> { depth--; if (depth == 0) return i }
            }
        }
        return content.length
    }

    private fun normalizePath(base: String, sub: String): String {
        val b = base.trimEnd('/')
        val s = sub.trimStart('/')
        return if (s.isBlank()) b else "$b/$s"
    }

    private enum class UpsertResult { INSERTED, UPDATED, NO_CHANGE }

    private suspend fun upsertDiscoveredApi(route: RouteEntry): UpsertResult = dbQuery {
        val existing = PlatformDiscoveredApisTable.selectAll()
            .where {
                (PlatformDiscoveredApisTable.method eq route.method) and
                (PlatformDiscoveredApisTable.path eq route.path)
            }
            .singleOrNull()

        // Check if mapped to a platform feature API
        val mappedApi = PlatformFeatureApisTable.selectAll()
            .where {
                (PlatformFeatureApisTable.endpoint eq route.path) and
                (PlatformFeatureApisTable.method eq route.method)
            }
            .singleOrNull()
        val isMapped = mappedApi != null
        val mappedApiId = mappedApi?.get(PlatformFeatureApisTable.id)?.value

        if (existing == null) {
            PlatformDiscoveredApisTable.insert {
                it[PlatformDiscoveredApisTable.method] = route.method
                it[PlatformDiscoveredApisTable.path] = route.path
                it[PlatformDiscoveredApisTable.filePath] = route.filePath
                it[PlatformDiscoveredApisTable.featurePackage] = route.packageName
                it[PlatformDiscoveredApisTable.isMapped] = isMapped
                it[PlatformDiscoveredApisTable.mappedApiId] = mappedApiId
                it[PlatformDiscoveredApisTable.discoveredAt] = Instant.now()
                it[PlatformDiscoveredApisTable.lastSeenAt] = Instant.now()
            }
            UpsertResult.INSERTED
        } else {
            val changed = existing[PlatformDiscoveredApisTable.filePath] != route.filePath ||
                existing[PlatformDiscoveredApisTable.isMapped] != isMapped
            PlatformDiscoveredApisTable.update(
                where = { PlatformDiscoveredApisTable.id eq existing[PlatformDiscoveredApisTable.id] }
            ) {
                it[PlatformDiscoveredApisTable.filePath] = route.filePath
                it[PlatformDiscoveredApisTable.featurePackage] = route.packageName
                it[PlatformDiscoveredApisTable.isMapped] = isMapped
                it[PlatformDiscoveredApisTable.mappedApiId] = mappedApiId
                it[PlatformDiscoveredApisTable.lastSeenAt] = Instant.now()
            }
            if (changed) UpsertResult.UPDATED else UpsertResult.NO_CHANGE
        }
    }

    suspend fun listDiscovered(
        page: Int = 1,
        pageSize: Int = 50,
        isMapped: Boolean? = null,
        method: String? = null,
    ): Pair<List<DiscoveredApiDto>, Long> = dbQuery {
        val total = PlatformDiscoveredApisTable.selectAll().where {
            (if (isMapped != null) PlatformDiscoveredApisTable.isMapped eq isMapped else Op.TRUE) and
            (if (method != null) PlatformDiscoveredApisTable.method eq method else Op.TRUE)
        }.count()
        val items = PlatformDiscoveredApisTable.selectAll().where {
            (if (isMapped != null) PlatformDiscoveredApisTable.isMapped eq isMapped else Op.TRUE) and
            (if (method != null) PlatformDiscoveredApisTable.method eq method else Op.TRUE)
        }
            .orderBy(PlatformDiscoveredApisTable.discoveredAt, SortOrder.DESC)
            .limit(pageSize, ((page - 1) * pageSize).toLong())
            .map { row ->
                DiscoveredApiDto(
                    id = row[PlatformDiscoveredApisTable.id].value.toString(),
                    method = row[PlatformDiscoveredApisTable.method],
                    path = row[PlatformDiscoveredApisTable.path],
                    file_path = row[PlatformDiscoveredApisTable.filePath],
                    feature_package = row[PlatformDiscoveredApisTable.featurePackage],
                    description = row[PlatformDiscoveredApisTable.description],
                    is_mapped = row[PlatformDiscoveredApisTable.isMapped],
                    mapped_api_id = row[PlatformDiscoveredApisTable.mappedApiId]?.toString(),
                    is_alive = row[PlatformDiscoveredApisTable.isAlive],
                    last_checked_at = row[PlatformDiscoveredApisTable.lastCheckedAt]?.toString(),
                    response_ms = row[PlatformDiscoveredApisTable.responseMs],
                    status_code = row[PlatformDiscoveredApisTable.statusCode],
                    discovered_at = row[PlatformDiscoveredApisTable.discoveredAt].toString(),
                    last_seen_at = row[PlatformDiscoveredApisTable.lastSeenAt].toString(),
                )
            }
        items to total
    }

    suspend fun linkToFeature(
        discoveredApiId: UUID,
        featureId: UUID,
        description: String? = null,
    ): Boolean = dbQuery {
        val discovered = PlatformDiscoveredApisTable.selectAll()
            .where { PlatformDiscoveredApisTable.id eq discoveredApiId }
            .singleOrNull() ?: return@dbQuery false

        val apiId = PlatformFeatureApisTable.insert {
            it[PlatformFeatureApisTable.featureId] = featureId
            it[PlatformFeatureApisTable.endpoint] = discovered[PlatformDiscoveredApisTable.path]
            it[PlatformFeatureApisTable.method] = discovered[PlatformDiscoveredApisTable.method]
            it[PlatformFeatureApisTable.description] = description
            it[PlatformFeatureApisTable.createdAt] = Instant.now()
            it[PlatformFeatureApisTable.updatedAt] = Instant.now()
        }[PlatformFeatureApisTable.id].value

        PlatformDiscoveredApisTable.update(
            where = { PlatformDiscoveredApisTable.id eq discoveredApiId }
        ) {
            it[PlatformDiscoveredApisTable.isMapped] = true
            it[PlatformDiscoveredApisTable.mappedApiId] = apiId
            it[PlatformDiscoveredApisTable.lastSeenAt] = Instant.now()
        }
        true
    }
}
