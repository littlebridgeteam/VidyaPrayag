package com.littlebridge.enrollplus.feature.platform

import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.PlatformDiscoveredScreensTable
import com.littlebridge.enrollplus.db.PlatformScreensTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.io.File
import java.time.Instant
import java.util.UUID
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import kotlinx.serialization.Serializable

/**
 * Auto-discovery service for Compose Multiplatform screens and Next.js web pages.
 *
 * Scans:
 *   - composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/screens/  (Compose screens)
 *   - website/src/app/admin/  (Next.js admin pages)
 *
 * Extracts screen_id from file name, module from directory, and checks if
 * the screen is already mapped to a platform_screens row.
 */
object ScreenDiscoveryService {

    private const val COMPOSE_SCREENS_DIR = "composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/screens"
    private const val WEB_ADMIN_DIR = "website/src/app/admin"

    @Serializable
    data class ScanResult(
        val discovered: Int,
        val updated: Int,
        val stale: Int,
        val errors: List<String>,
    )

    suspend fun scan(): ScanResult {
        val errors = mutableListOf<String>()
        var discovered = 0
        var updated = 0
        var stale = 0

        // Scan Compose screens
        try {
            val composeDir = File(COMPOSE_SCREENS_DIR)
            if (composeDir.exists()) {
                val results = scanComposeScreens(composeDir)
                discovered += results.first
                updated += results.second
            }
        } catch (e: Exception) {
            errors.add("Compose scan: ${e.message}")
        }

        // Scan web admin pages
        try {
            val webDir = File(WEB_ADMIN_DIR)
            if (webDir.exists()) {
                val results = scanWebPages(webDir)
                discovered += results.first
                updated += results.second
            }
        } catch (e: Exception) {
            errors.add("Web scan: ${e.message}")
        }

        return ScanResult(discovered, updated, stale, errors)
    }

    private suspend fun scanComposeScreens(rootDir: File): Pair<Int, Int> {
        var discovered = 0
        var updated = 0
        val ktFiles = rootDir.walkTopDown().filter { it.isFile && it.extension == "kt" }

        for (file in ktFiles) {
            val relativePath = file.relativeTo(File(".")).path
            val screenId = file.nameWithoutExtension
            val module = file.parentFile?.relativeTo(rootDir)?.path?.replace("/", ".") ?: "root"
            val fileModified = getFileModifiedTime(file)
            val content = file.readText()

            // Extract screen name from @Composable fun name
            val nameMatch = Regex("""@Composable\s+fun\s+(\w+)""").find(content)
            val screenName = nameMatch?.groupValues?.getOrNull(1) ?: screenId

            // Extract overlay enum if present
            val overlayMatch = Regex("""overlay\s*[:=]\s*(\w+)""", RegexOption.IGNORE_CASE).find(content)
            val overlayEnum = overlayMatch?.groupValues?.getOrNull(1)

            // Extract deep link if present
            val deepLinkMatch = Regex("""deepLink\s*[:=]\s*"([^"]+)"""", RegexOption.IGNORE_CASE).find(content)
            val deepLink = deepLinkMatch?.groupValues?.getOrNull(1)

            val result = upsertDiscoveredScreen(
                screenId = screenId,
                name = screenName,
                module = "composeApp.$module",
                filePath = relativePath,
                portal = null,
                overlayEnum = overlayEnum,
                deepLinkPath = deepLink,
                fileModified = fileModified,
            )
            if (result == UpsertResult.INSERTED) discovered++ else if (result == UpsertResult.UPDATED) updated++
        }
        return discovered to updated
    }

    private suspend fun scanWebPages(rootDir: File): Pair<Int, Int> {
        var discovered = 0
        var updated = 0
        val pageFiles = rootDir.walkTopDown().filter {
            it.isFile && (it.name == "page.tsx" || it.name == "page.ts" || it.name == "layout.tsx")
        }

        for (file in pageFiles) {
            val relativePath = file.relativeTo(File(".")).path
            val parentName = file.parentFile?.name ?: "unknown"
            val screenId = "web_${parentName.replace(Regex("[^a-zA-Z0-9]"), "_")}"
            val module = file.parentFile?.relativeTo(rootDir)?.path?.replace("/", ".") ?: "root"
            val fileModified = getFileModifiedTime(file)
            val content = file.readText()

            // Extract page title or heading
            val titleMatch = Regex("""(?:title|heading|<h[12][^>]*>)([^<]+)""", RegexOption.IGNORE_CASE).find(content)
            val pageName = titleMatch?.groupValues?.getOrNull(1)?.trim()?.takeIf { it.isNotBlank() } ?: parentName

            val result = upsertDiscoveredScreen(
                screenId = screenId,
                name = pageName,
                module = "website.$module",
                filePath = relativePath,
                portal = "web",
                overlayEnum = null,
                deepLinkPath = "/${file.parentFile?.relativeTo(rootDir)?.path ?: ""}",
                fileModified = fileModified,
            )
            if (result == UpsertResult.INSERTED) discovered++ else if (result == UpsertResult.UPDATED) updated++
        }
        return discovered to updated
    }

    private enum class UpsertResult { INSERTED, UPDATED, NO_CHANGE }

    private suspend fun upsertDiscoveredScreen(
        screenId: String,
        name: String,
        module: String,
        filePath: String,
        portal: String?,
        overlayEnum: String?,
        deepLinkPath: String?,
        fileModified: Instant?,
    ): UpsertResult = dbQuery {
        val existing = PlatformDiscoveredScreensTable.selectAll()
            .where { PlatformDiscoveredScreensTable.screenId eq screenId }
            .singleOrNull()

        // Check if mapped to a platform screen
        val mappedScreen = PlatformScreensTable.selectAll()
            .where { PlatformScreensTable.screenId eq screenId }
            .singleOrNull()
        val isMapped = mappedScreen != null
        val mappedScreenId = mappedScreen?.get(PlatformScreensTable.id)?.value

        if (existing == null) {
            PlatformDiscoveredScreensTable.insert {
                it[PlatformDiscoveredScreensTable.screenId] = screenId
                it[PlatformDiscoveredScreensTable.name] = name
                it[PlatformDiscoveredScreensTable.module] = module
                it[PlatformDiscoveredScreensTable.filePath] = filePath
                it[PlatformDiscoveredScreensTable.portal] = portal
                it[PlatformDiscoveredScreensTable.overlayEnum] = overlayEnum
                it[PlatformDiscoveredScreensTable.deepLinkPath] = deepLinkPath
                it[PlatformDiscoveredScreensTable.isMapped] = isMapped
                it[PlatformDiscoveredScreensTable.mappedScreenId] = mappedScreenId
                it[PlatformDiscoveredScreensTable.discoveredAt] = Instant.now()
                it[PlatformDiscoveredScreensTable.lastSeenAt] = Instant.now()
                it[PlatformDiscoveredScreensTable.fileModifiedAt] = fileModified
            }
            UpsertResult.INSERTED
        } else {
            val changed = existing[PlatformDiscoveredScreensTable.name] != name ||
                existing[PlatformDiscoveredScreensTable.filePath] != filePath ||
                existing[PlatformDiscoveredScreensTable.isMapped] != isMapped
            PlatformDiscoveredScreensTable.update(
                where = { PlatformDiscoveredScreensTable.id eq existing[PlatformDiscoveredScreensTable.id] }
            ) {
                it[PlatformDiscoveredScreensTable.name] = name
                it[PlatformDiscoveredScreensTable.module] = module
                it[PlatformDiscoveredScreensTable.filePath] = filePath
                it[PlatformDiscoveredScreensTable.portal] = portal
                it[PlatformDiscoveredScreensTable.overlayEnum] = overlayEnum
                it[PlatformDiscoveredScreensTable.deepLinkPath] = deepLinkPath
                it[PlatformDiscoveredScreensTable.isMapped] = isMapped
                it[PlatformDiscoveredScreensTable.mappedScreenId] = mappedScreenId
                it[PlatformDiscoveredScreensTable.lastSeenAt] = Instant.now()
                it[PlatformDiscoveredScreensTable.fileModifiedAt] = fileModified
            }
            if (changed) UpsertResult.UPDATED else UpsertResult.NO_CHANGE
        }
    }

    suspend fun listDiscovered(
        page: Int = 1,
        pageSize: Int = 50,
        module: String? = null,
        isMapped: Boolean? = null,
    ): Pair<List<DiscoveredScreenDto>, Long> = dbQuery {
        val conditions = Op.build {
            (if (module != null) PlatformDiscoveredScreensTable.module eq module else Op.TRUE) and
            (if (isMapped != null) PlatformDiscoveredScreensTable.isMapped eq isMapped else Op.TRUE)
        }
        val total = PlatformDiscoveredScreensTable.selectAll().where { conditions }.count()
        val items = PlatformDiscoveredScreensTable.selectAll().where { conditions }
            .orderBy(PlatformDiscoveredScreensTable.discoveredAt, SortOrder.DESC)
            .limit(pageSize, ((page - 1) * pageSize).toLong())
            .map { row ->
                DiscoveredScreenDto(
                    id = row[PlatformDiscoveredScreensTable.id].value.toString(),
                    screen_id = row[PlatformDiscoveredScreensTable.screenId],
                    name = row[PlatformDiscoveredScreensTable.name],
                    module = row[PlatformDiscoveredScreensTable.module],
                    file_path = row[PlatformDiscoveredScreensTable.filePath],
                    portal = row[PlatformDiscoveredScreensTable.portal],
                    overlay_enum = row[PlatformDiscoveredScreensTable.overlayEnum],
                    deep_link_path = row[PlatformDiscoveredScreensTable.deepLinkPath],
                    is_mapped = row[PlatformDiscoveredScreensTable.isMapped],
                    mapped_screen_id = row[PlatformDiscoveredScreensTable.mappedScreenId]?.toString(),
                    discovered_at = row[PlatformDiscoveredScreensTable.discoveredAt].toString(),
                    last_seen_at = row[PlatformDiscoveredScreensTable.lastSeenAt].toString(),
                    file_modified_at = row[PlatformDiscoveredScreensTable.fileModifiedAt]?.toString(),
                )
            }
        items to total
    }

    suspend fun linkToFeature(
        discoveredScreenId: UUID,
        featureId: UUID,
        screenName: String? = null,
    ): Boolean = dbQuery {
        val discovered = PlatformDiscoveredScreensTable.selectAll()
            .where { PlatformDiscoveredScreensTable.id eq discoveredScreenId }
            .singleOrNull() ?: return@dbQuery false

        // Create a platform_screens row from the discovered screen
        val screenId = PlatformScreensTable.insert {
            it[PlatformScreensTable.screenId] = discovered[PlatformDiscoveredScreensTable.screenId]
            it[PlatformScreensTable.name] = screenName ?: discovered[PlatformDiscoveredScreensTable.name]
            it[PlatformScreensTable.route] = discovered[PlatformDiscoveredScreensTable.deepLinkPath]
            it[PlatformScreensTable.module] = discovered[PlatformDiscoveredScreensTable.module]
            it[PlatformScreensTable.featureId] = featureId
            it[PlatformScreensTable.createdAt] = Instant.now()
            it[PlatformScreensTable.updatedAt] = Instant.now()
        }[PlatformScreensTable.id].value

        // Mark discovered screen as mapped
        PlatformDiscoveredScreensTable.update(
            where = { PlatformDiscoveredScreensTable.id eq discoveredScreenId }
        ) {
            it[PlatformDiscoveredScreensTable.isMapped] = true
            it[PlatformDiscoveredScreensTable.mappedScreenId] = screenId
            it[PlatformDiscoveredScreensTable.lastSeenAt] = Instant.now()
        }
        true
    }

    private fun getFileModifiedTime(file: File): Instant? {
        return try {
            val attrs = Files.readAttributes(file.toPath(), BasicFileAttributes::class.java)
            attrs.lastModifiedTime().toInstant()
        } catch (e: Exception) { null }
    }
}
