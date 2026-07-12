package com.littlebridge.enrollplus.feature.platform

import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.PlatformFeaturesTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.insert
import kotlinx.serialization.Serializable
import java.util.UUID
import java.io.File
import java.time.Instant

/**
 * CSV import service for seeding platform_features from feature_audit.csv.
 * Parses the 163-row audit CSV and upserts each row as a feature with
 * legacy_imported = true.
 *
 * CSV columns (0-indexed):
 *   0: Module, 1: Feature Category, 2: Feature Name, 3: Sub Feature,
 *   4: Description, 5: Primary Role, 6: Secondary Roles, 7: Navigation Path,
 *   8: Current Status, 9: Completion Percentage, 10: UI Available, 11: Backend Available,
 *   12: API Connected, 13: Database Ready, 14: Offline Support, 15: Push Notifications,
 *   16: Stub Present, 17: Stub Details, 18: TODO Present, 19: Uses Dummy Data,
 *   20: Uses Mock API, 21: Hidden Feature, 22: Unused Code, 23: Platform,
 *   24: Dependencies, 25: Priority, 26: Business Criticality, 27: Recommended Action,
 *   28: Missing Capability, 29: Suggested Enhancement, 30: Industry Standard,
 *   31: Competitor Reference, 32: Estimated Development Effort, 33: Notes
 */
object CsvImportService {

    private val STATUS_MAP = mapOf(
        "✅ complete" to "complete",
        "complete" to "complete",
        "🟡 partially implemented" to "in_progress",
        "partially implemented" to "in_progress",
        "🔴 todo" to "planned",
        "todo" to "planned",
        "not started" to "planned",
    )

    private val PRIORITY_MAP = mapOf(
        "high" to "high",
        "medium" to "medium",
        "low" to "low",
        "critical" to "critical",
    )

    @Serializable
    data class ImportResult(
        val imported: Int,
        val skipped: Int,
        val errors: List<String>,
    )

    /**
     * Parse a CSV line that may contain quoted fields with embedded commas.
     */
    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            when {
                c == '"' && !inQuotes -> inQuotes = true
                c == '"' && inQuotes -> {
                    if (i + 1 < line.length && line[i + 1] == '"') {
                        current.append('"')
                        i++
                    } else {
                        inQuotes = false
                    }
                }
                c == ',' && !inQuotes -> {
                    result.add(current.toString().trim())
                    current = StringBuilder()
                }
                else -> current.append(c)
            }
            i++
        }
        result.add(current.toString().trim())
        return result
    }

    private fun mapStatus(raw: String): String {
        val lower = raw.lowercase().trim()
        return STATUS_MAP.entries.find { lower.contains(it.key) }?.value ?: "planned"
    }

    private fun mapPriority(raw: String): String {
        val lower = raw.lowercase().trim()
        return PRIORITY_MAP[lower] ?: "medium"
    }

    private fun genFeatureId(module: String, category: String, name: String, idx: Int): String {
        val mod = module.uppercase().replace(" ", "-").take(8)
        val cat = category.uppercase().replace(" ", "-").take(12)
        val nameSlug = name.uppercase().replace(Regex("[^A-Z0-9]"), "-").replace(Regex("-+"), "-").trim('-').take(20)
        return "$mod-$cat-$nameSlug-${"%03d".format(idx)}"
    }

    private fun buildMetadata(
        uiAvailable: String, backendAvailable: String, apiConnected: String,
        dbReady: String, offlineSupport: String, pushNotifs: String,
        stubPresent: String, stubDetails: String, todoPresent: String,
        usesDummyData: String, usesMockApi: String, hiddenFeature: String,
        unusedCode: String, platform: String, dependencies: String,
        businessCriticality: String, recommendedAction: String,
        missingCapability: String, suggestedEnhancement: String,
        industryStandard: String, competitorReference: String, notes: String,
    ): String {
        val map = linkedMapOf<String, String>()
        if (uiAvailable.isNotBlank()) map["ui_available"] = uiAvailable
        if (backendAvailable.isNotBlank()) map["backend_available"] = backendAvailable
        if (apiConnected.isNotBlank()) map["api_connected"] = apiConnected
        if (dbReady.isNotBlank()) map["db_ready"] = dbReady
        if (offlineSupport.isNotBlank()) map["offline_support"] = offlineSupport
        if (pushNotifs.isNotBlank()) map["push_notifications"] = pushNotifs
        if (stubPresent.isNotBlank() && stubPresent != "No") map["stub_present"] = stubPresent
        if (stubDetails.isNotBlank() && stubDetails != "-") map["stub_details"] = stubDetails
        if (todoPresent.isNotBlank() && todoPresent != "No") map["todo_present"] = todoPresent
        if (usesDummyData.isNotBlank() && usesDummyData != "No") map["uses_dummy_data"] = usesDummyData
        if (usesMockApi.isNotBlank() && usesMockApi != "No") map["uses_mock_api"] = usesMockApi
        if (hiddenFeature.isNotBlank() && hiddenFeature != "No") map["hidden_feature"] = hiddenFeature
        if (unusedCode.isNotBlank() && unusedCode != "No") map["unused_code"] = unusedCode
        if (platform.isNotBlank()) map["platform"] = platform
        if (dependencies.isNotBlank() && dependencies != "-") map["dependencies"] = dependencies
        if (businessCriticality.isNotBlank()) map["business_criticality"] = businessCriticality
        if (recommendedAction.isNotBlank() && recommendedAction != "-") map["recommended_action"] = recommendedAction
        if (missingCapability.isNotBlank() && missingCapability != "-") map["missing_capability"] = missingCapability
        if (suggestedEnhancement.isNotBlank() && suggestedEnhancement != "-") map["suggested_enhancement"] = suggestedEnhancement
        if (industryStandard.isNotBlank() && industryStandard != "-") map["industry_standard"] = industryStandard
        if (competitorReference.isNotBlank() && competitorReference != "-") map["competitor_reference"] = competitorReference
        if (notes.isNotBlank() && notes != "-") map["notes"] = notes
        return buildJsonString(map)
    }

    private fun buildJsonString(map: Map<String, String>): String {
        val entries = map.entries.joinToString(",") { (k, v) ->
            "\"${k.replace("\"", "\\\"")}\":\"${v.replace("\"", "\\\"")}\""
        }
        return "{$entries}"
    }

    suspend fun importFromCsv(csvPath: String, userId: UUID): ImportResult {
        val file = File(csvPath)
        if (!file.exists()) {
            return ImportResult(0, 0, listOf("CSV file not found: $csvPath"))
        }

        val lines = file.readLines()
        if (lines.size < 2) {
            return ImportResult(0, 0, listOf("CSV file is empty or has no data rows"))
        }

        // Skip header row
        val dataLines = lines.drop(1)
        val errors = mutableListOf<String>()
        var imported = 0
        var skipped = 0

        // Collect existing feature_ids to skip duplicates
        val existingIds = dbQuery {
            PlatformFeaturesTable.selectAll().map { it[PlatformFeaturesTable.featureId] }.toSet()
        }

        dataLines.forEachIndexed { idx, line ->
            if (line.isBlank()) { skipped++; return@forEachIndexed }
            try {
                val cols = parseCsvLine(line)
                if (cols.size < 10) { errors.add("Row ${idx + 2}: insufficient columns (${cols.size})"); skipped++; return@forEachIndexed }

                val module = cols.getOrNull(0)?.trim() ?: ""
                val category = cols.getOrNull(1)?.trim() ?: ""
                val featureName = cols.getOrNull(2)?.trim() ?: ""
                val subFeature = cols.getOrNull(3)?.trim() ?: ""
                val description = cols.getOrNull(4)?.trim() ?: ""
                val completionPct = cols.getOrNull(9)?.trim()?.replace("%", "")?.toIntOrNull() ?: 0
                val priority = mapPriority(cols.getOrNull(25)?.trim() ?: "medium")
                val status = mapStatus(cols.getOrNull(8)?.trim() ?: "")
                val effort = cols.getOrNull(32)?.trim()?.takeIf { it.isNotBlank() && it != "-" }?.take(4)
                val businessCriticality = cols.getOrNull(26)?.trim()?.takeIf { it.isNotBlank() && it != "-" }

                val featureId = genFeatureId(module, category, featureName, idx + 1)
                if (featureId in existingIds) { skipped++; return@forEachIndexed }

                val name = if (subFeature.isNotBlank() && subFeature != featureName) {
                    "$featureName — $subFeature"
                } else {
                    featureName
                }

                val metadata = buildMetadata(
                    cols.getOrNull(10)?.trim() ?: "",
                    cols.getOrNull(11)?.trim() ?: "",
                    cols.getOrNull(12)?.trim() ?: "",
                    cols.getOrNull(13)?.trim() ?: "",
                    cols.getOrNull(14)?.trim() ?: "",
                    cols.getOrNull(15)?.trim() ?: "",
                    cols.getOrNull(16)?.trim() ?: "",
                    cols.getOrNull(17)?.trim() ?: "",
                    cols.getOrNull(18)?.trim() ?: "",
                    cols.getOrNull(19)?.trim() ?: "",
                    cols.getOrNull(20)?.trim() ?: "",
                    cols.getOrNull(21)?.trim() ?: "",
                    cols.getOrNull(22)?.trim() ?: "",
                    cols.getOrNull(23)?.trim() ?: "",
                    cols.getOrNull(24)?.trim() ?: "",
                    businessCriticality ?: "",
                    cols.getOrNull(27)?.trim() ?: "",
                    cols.getOrNull(28)?.trim() ?: "",
                    cols.getOrNull(29)?.trim() ?: "",
                    cols.getOrNull(30)?.trim() ?: "",
                    cols.getOrNull(31)?.trim() ?: "",
                    cols.getOrNull(33)?.trim() ?: "",
                )

                val businessImpact = when (businessCriticality?.lowercase()?.trim()) {
                    "critical" -> "critical"
                    "high" -> "high"
                    "medium" -> "medium"
                    "low" -> "low"
                    else -> null
                }

                dbQuery {
                    PlatformFeaturesTable.insert {
                        it[PlatformFeaturesTable.featureId] = featureId
                        it[PlatformFeaturesTable.name] = name
                        it[PlatformFeaturesTable.description] = description.ifBlank { null }
                        it[PlatformFeaturesTable.productArea] = module.ifBlank { null }
                        it[PlatformFeaturesTable.category] = category.ifBlank { null }
                        it[PlatformFeaturesTable.module] = cols.getOrNull(23)?.trim()?.takeIf { c -> c.isNotBlank() && c != "-" }
                        it[PlatformFeaturesTable.status] = status
                        it[PlatformFeaturesTable.completionPct] = completionPct
                        it[PlatformFeaturesTable.priority] = priority
                        it[PlatformFeaturesTable.businessImpact] = businessImpact
                        it[PlatformFeaturesTable.estimatedEffort] = effort
                        it[PlatformFeaturesTable.tags] = "[]"
                        it[PlatformFeaturesTable.metadata] = metadata
                        it[PlatformFeaturesTable.legacyImported] = true
                        it[PlatformFeaturesTable.isArchived] = false
                        it[PlatformFeaturesTable.createdAt] = Instant.now()
                        it[PlatformFeaturesTable.updatedAt] = Instant.now()
                        it[PlatformFeaturesTable.createdBy] = userId
                        it[PlatformFeaturesTable.updatedBy] = userId
                    }
                }
                imported++
            } catch (e: Exception) {
                errors.add("Row ${idx + 2}: ${e.message}")
                skipped++
            }
        }

        return ImportResult(imported, skipped, errors)
    }
}
