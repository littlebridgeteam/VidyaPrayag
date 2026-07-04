/*
 * File: I18nRouting.kt
 * Module: feature.i18n
 *
 * Multi-language API endpoints:
 *
 *   User endpoints (JWT auth, any role):
 *     GET   /api/v1/user/language-pref       — get own language preference
 *     PATCH /api/v1/user/language-pref       — update own language preference
 *     GET   /api/v1/user/language-history    — own language change history
 *
 *   School Admin endpoints (JWT auth, school admin):
 *     GET   /api/v1/school/language-distribution  — language distribution for school
 *     GET   /api/v1/school/users-language-pref    — per-user language for school
 *
 *   Super Admin endpoints (JWT auth, platform admin):
 *     GET   /api/admin/language-adoption           — platform-wide language stats
 *     GET   /api/admin/users-by-language           — all users grouped by role
 *     GET   /api/admin/server-strings              — all ServerStrings keys × languages
 *     PATCH /api/admin/server-strings/{key}        — upsert DB override
 *     DELETE /api/admin/server-strings/{key}       — remove DB override
 *     PATCH /api/admin/server-strings/bulk         — bulk upsert overrides
 *     GET   /api/admin/server-strings/history      — audit log of all changes
 *
 * Spec ref: MULTI_LANGUAGE_SPEC.md §9
 */
package com.littlebridge.enrollplus.feature.i18n

import com.littlebridge.enrollplus.core.ApiResponse
import com.littlebridge.enrollplus.core.fail
import com.littlebridge.enrollplus.core.ok
import com.littlebridge.enrollplus.core.principalUserUuid
import com.littlebridge.enrollplus.core.requirePlatformAdmin
import com.littlebridge.enrollplus.core.requireSchoolAdmin
import com.littlebridge.enrollplus.db.AppUsersTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.SchoolsTable
import com.littlebridge.enrollplus.feature.i18n.LanguagePrefHistoryRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.util.UUID

// ── DTOs ──────────────────────────────────────────────────────────────

@Serializable
data class LanguagePrefRequest(
    val language: String,
)

@Serializable
data class LanguagePrefResponse(
    val language: String,
    @SerialName("updated_at") val updatedAt: String,
)

@Serializable
data class LanguageHistoryResponse(
    val history: List<LanguagePrefHistoryEntry>,
)

@Serializable
data class LanguageDistributionEntry(
    val language: String,
    val count: Int,
    val percentage: Double,
)

@Serializable
data class LanguageDistributionResponse(
    val distribution: List<LanguageDistributionEntry>,
    @SerialName("total_users") val totalUsers: Int,
    @SerialName("most_popular") val mostPopular: String,
    @SerialName("switch_rate_7d") val switchRate7d: Int,
)

@Serializable
data class UserLanguageEntry(
    @SerialName("user_id") val userId: String,
    val name: String,
    val language: String,
    @SerialName("last_changed") val lastChanged: String?,
)

@Serializable
data class SchoolUsersLanguageResponse(
    val users: List<UserLanguageEntry>,
    val total: Int,
)

@Serializable
data class PlatformLanguageEntry(
    val language: String,
    val count: Int,
    val percentage: Double,
)

@Serializable
data class SchoolLanguageEntry(
    @SerialName("school_id") val schoolId: String,
    @SerialName("school_name") val schoolName: String,
    @SerialName("most_popular") val mostPopular: String,
    @SerialName("total_users") val totalUsers: Int,
)

@Serializable
data class LanguageAdoptionResponse(
    @SerialName("platform_distribution") val platformDistribution: List<PlatformLanguageEntry>,
    @SerialName("total_users") val totalUsers: Int,
    @SerialName("switch_rate_7d") val switchRate7d: Int,
    @SerialName("by_school") val bySchool: List<SchoolLanguageEntry>,
    @SerialName("by_language") val byLanguage: List<PlatformLanguageEntry>,
    @SerialName("by_role") val byRole: Map<String, List<PlatformLanguageEntry>>,
)

@Serializable
data class UsersByLanguageGroupEntry(
    @SerialName("user_id") val userId: String,
    val name: String,
    val phone: String?,
    @SerialName("school_name") val schoolName: String?,
    val language: String,
    @SerialName("last_changed") val lastChanged: String?,
)

@Serializable
data class UsersByLanguageResponse(
    val groups: Map<String, List<UsersByLanguageGroupEntry>>,
    val counts: Map<String, Int>,
    val total: Int,
)

@Serializable
data class ServerStringTranslation(
    val value: String,
    @SerialName("is_override") val isOverride: Boolean,
    @SerialName("updated_by") val updatedBy: String?,
    @SerialName("updated_at") val updatedAt: String?,
)

@Serializable
data class ServerStringEntry(
    val key: String,
    val translations: Map<String, ServerStringTranslation>,
)

@Serializable
data class ServerStringsResponse(
    val strings: List<ServerStringEntry>,
    @SerialName("total_keys") val totalKeys: Int,
    val languages: List<String>,
)

@Serializable
data class ServerStringOverrideRequest(
    val lang: String,
    val value: String,
)

@Serializable
data class ServerStringOverrideResponse(
    val key: String,
    val lang: String,
    val value: String,
    @SerialName("updated_at") val updatedAt: String,
)

@Serializable
data class ServerStringDeleteResponse(
    val deleted: Boolean,
    val key: String,
    val lang: String,
)

@Serializable
data class BulkUpsertItem(
    val key: String,
    val lang: String,
    val value: String,
)

@Serializable
data class BulkUpsertRequest(
    val items: List<BulkUpsertItem>,
)

@Serializable
data class BulkUpsertErrorEntry(
    val key: String,
    val lang: String,
    val error: String,
)

@Serializable
data class BulkUpsertResponse(
    val updated: Int,
    val errors: List<BulkUpsertErrorEntry>,
)

@Serializable
data class StringOverrideHistoryDto(
    val id: String,
    @SerialName("string_key") val stringKey: String,
    val lang: String,
    @SerialName("old_value") val oldValue: String?,
    @SerialName("new_value") val newValue: String,
    val action: String,
    @SerialName("changed_by") val changedBy: String?,
    @SerialName("changed_by_name") val changedByName: String?,
    @SerialName("changed_at") val changedAt: String,
)

@Serializable
data class StringOverrideHistoryResponseDto(
    val history: List<StringOverrideHistoryDto>,
    val total: Int,
)

// ── Routing ───────────────────────────────────────────────────────────

private val SUPPORTED_LANGS = setOf("en", "hi", "bn", "ta", "te", "mr", "gu", "kn", "ml", "pa")

fun Route.i18nRouting() {

    authenticate("jwt") {

        // ── User language preference ──────────────────────────────────

        get("/api/v1/user/language-pref") {
            val uid = call.principalUserUuid() ?: run {
                call.fail("Invalid token", HttpStatusCode.Unauthorized, "UNAUTHORIZED")
                return@get
            }
            val lang = dbQuery {
                AppUsersTable.selectAll()
                    .where { AppUsersTable.id eq uid }
                    .singleOrNull()
                    ?.get(AppUsersTable.languagePref)
            } ?: "en"

            call.ok(
                LanguagePrefResponse(
                    language = lang ?: "en",
                    updatedAt = Instant.now().toString(),
                )
            )
        }

        patch("/api/v1/user/language-pref") {
            val uid = call.principalUserUuid() ?: run {
                call.fail("Invalid token", HttpStatusCode.Unauthorized, "UNAUTHORIZED")
                return@patch
            }
            val req = runCatching { call.receive<LanguagePrefRequest>() }.getOrNull()
                ?: run {
                    call.fail("Invalid body: expected { \"language\": \"en\" }")
                    return@patch
                }

            if (req.language !in SUPPORTED_LANGS) {
                call.fail("Invalid language code: ${req.language}. Supported: $SUPPORTED_LANGS")
                return@patch
            }

            val oldLang = dbQuery {
                AppUsersTable.selectAll()
                    .where { AppUsersTable.id eq uid }
                    .singleOrNull()
                    ?.get(AppUsersTable.languagePref)
            }

            val schoolId = dbQuery {
                AppUsersTable.selectAll()
                    .where { AppUsersTable.id eq uid }
                    .singleOrNull()
                    ?.get(AppUsersTable.schoolId)
            }

            dbQuery {
                AppUsersTable.update({ AppUsersTable.id eq uid }) {
                    it[AppUsersTable.languagePref] = req.language
                }
            }

            // Record history (non-blocking — audit trail)
            runCatching {
                LanguagePrefHistoryRepository.record(
                    userId = uid,
                    schoolId = schoolId,
                    oldLang = oldLang,
                    newLang = req.language,
                    source = "app",
                )
            }

            // Evict cache so next notification uses the new language
            UserLanguageResolver.evict(uid)

            call.ok(
                LanguagePrefResponse(
                    language = req.language,
                    updatedAt = Instant.now().toString(),
                )
            )
        }

        // ── User language history ─────────────────────────────────────

        get("/api/v1/user/language-history") {
            val uid = call.principalUserUuid() ?: run {
                call.fail("Invalid token", HttpStatusCode.Unauthorized, "UNAUTHORIZED")
                return@get
            }
            val history = LanguagePrefHistoryRepository.getUserHistory(uid)
            call.ok(LanguageHistoryResponse(history = history))
        }

        // ── School Admin: language distribution ───────────────────────

        get("/api/v1/school/language-distribution") {
            val ctx = call.requireSchoolAdmin() ?: return@get
            val rows = dbQuery {
                AppUsersTable.selectAll()
                    .where { AppUsersTable.schoolId eq ctx.schoolId }
                    .toList()
            }

            val total = rows.size
            val byLang = rows.groupingBy { it[AppUsersTable.languagePref] ?: "en" }.eachCount()
            val distribution = byLang.entries
                .map { (lang, count) ->
                    LanguageDistributionEntry(
                        language = lang,
                        count = count,
                        percentage = if (total > 0) count.toDouble() / total else 0.0,
                    )
                }
                .sortedByDescending { it.count }

            val mostPopular = distribution.firstOrNull()?.language ?: "en"
            val switchRate = LanguagePrefHistoryRepository.getSchoolSwitchCount(ctx.schoolId, 7)

            call.ok(
                LanguageDistributionResponse(
                    distribution = distribution,
                    totalUsers = total,
                    mostPopular = mostPopular,
                    switchRate7d = switchRate,
                )
            )
        }

        // ── School Admin: per-user language preferences ───────────────

        get("/api/v1/school/users-language-pref") {
            val ctx = call.requireSchoolAdmin() ?: return@get
            val rows = dbQuery {
                AppUsersTable.selectAll()
                    .where { AppUsersTable.schoolId eq ctx.schoolId }
                    .orderBy(AppUsersTable.fullName, SortOrder.ASC)
                    .toList()
            }

            // Fetch last_changed per user from history (most recent)
            val lastChangedMap = mutableMapOf<UUID, String>()
            rows.forEach { row ->
                val userId = row[AppUsersTable.id].value
                val history = LanguagePrefHistoryRepository.getUserHistory(userId, 1)
                if (history.isNotEmpty()) {
                    lastChangedMap[userId] = history[0].changedAt
                }
            }

            val users = rows.map { row ->
                val userId = row[AppUsersTable.id].value
                UserLanguageEntry(
                    userId = userId.toString(),
                    name = row[AppUsersTable.fullName],
                    language = row[AppUsersTable.languagePref] ?: "en",
                    lastChanged = lastChangedMap[userId],
                )
            }

            call.ok(SchoolUsersLanguageResponse(users = users, total = users.size))
        }

        // ── Super Admin: platform language adoption ───────────────────

        get("/api/admin/language-adoption") {
            val adminUid = call.requirePlatformAdmin() ?: return@get

            val allUsers = dbQuery {
                AppUsersTable.selectAll().toList()
            }

            val total = allUsers.size
            val byLang = allUsers.groupingBy { it[AppUsersTable.languagePref] ?: "en" }.eachCount()
            val platformDist = byLang.entries
                .map { (lang, count) ->
                    PlatformLanguageEntry(
                        language = lang,
                        count = count,
                        percentage = if (total > 0) count.toDouble() / total else 0.0,
                    )
                }
                .sortedByDescending { it.count }

            // By role
            val byRole: Map<String, List<PlatformLanguageEntry>> = allUsers
                .groupBy { it[AppUsersTable.role] }
                .mapValues { (role, rows) ->
                    val roleTotal = rows.size
                    rows.groupingBy { it[AppUsersTable.languagePref] ?: "en" }.eachCount()
                        .entries
                        .map { (lang, count) ->
                            PlatformLanguageEntry(
                                language = lang,
                                count = count,
                                percentage = if (roleTotal > 0) count.toDouble() / roleTotal else 0.0,
                            )
                        }
                        .sortedByDescending { it.count }
                }

            // By school
            val bySchoolRows = dbQuery {
                AppUsersTable
                    .join(SchoolsTable, JoinType.INNER, AppUsersTable.schoolId, SchoolsTable.id)
                    .select(AppUsersTable.schoolId, SchoolsTable.name, AppUsersTable.languagePref)
                    .where { AppUsersTable.schoolId.isNotNull() }
                    .toList()
            }

            val schoolGroups = bySchoolRows.groupBy { it[AppUsersTable.schoolId]!! }
            val bySchool = schoolGroups.entries.map { (schoolId, schoolRows) ->
                val schoolName = schoolRows.first()[SchoolsTable.name]
                val schoolTotal = schoolRows.size
                val schoolByLang = schoolRows.groupingBy { it[AppUsersTable.languagePref] ?: "en" }.eachCount()
                val schoolPopular = schoolByLang.entries.maxByOrNull { it.value }?.key ?: "en"
                SchoolLanguageEntry(
                    schoolId = schoolId.toString(),
                    schoolName = schoolName,
                    mostPopular = schoolPopular,
                    totalUsers = schoolTotal,
                )
            }.sortedByDescending { it.totalUsers }

            // Platform-wide switch rate (all schools)
            var platformSwitchRate = 0
            for (schoolId in schoolGroups.keys) {
                platformSwitchRate += LanguagePrefHistoryRepository.getSchoolSwitchCount(schoolId, 7)
            }

            call.ok(
                LanguageAdoptionResponse(
                    platformDistribution = platformDist,
                    totalUsers = total,
                    switchRate7d = platformSwitchRate,
                    bySchool = bySchool,
                    byLanguage = platformDist,
                    byRole = byRole,
                )
            )
        }

        // ── Super Admin: users by language (grouped by role) ──────────

        get("/api/admin/users-by-language") {
            val adminUid = call.requirePlatformAdmin() ?: return@get
            val roleFilter = call.request.queryParameters["role"]

            val allUsers = dbQuery {
                AppUsersTable.selectAll().toList()
            }

            val schoolNames = dbQuery {
                SchoolsTable.selectAll().associate { it[SchoolsTable.id].value to it[SchoolsTable.name] }
            }

            val filtered = if (roleFilter != null) allUsers.filter { it[AppUsersTable.role] == roleFilter } else allUsers

            // Fetch last_changed for each user
            val lastChangedMap = mutableMapOf<UUID, String>()
            filtered.forEach { row ->
                val userId = row[AppUsersTable.id].value
                val history = LanguagePrefHistoryRepository.getUserHistory(userId, 1)
                if (history.isNotEmpty()) {
                    lastChangedMap[userId] = history[0].changedAt
                }
            }

            val groups = filtered.groupBy { it[AppUsersTable.role] }
                .mapValues { (_, rows) ->
                    rows.map { row ->
                        val userId = row[AppUsersTable.id].value
                        UsersByLanguageGroupEntry(
                            userId = userId.toString(),
                            name = row[AppUsersTable.fullName],
                            phone = row[AppUsersTable.phone],
                            schoolName = row[AppUsersTable.schoolId]?.let { schoolNames[it] },
                            language = row[AppUsersTable.languagePref] ?: "en",
                            lastChanged = lastChangedMap[userId],
                        )
                    }
                }

            val counts = groups.mapValues { it.value.size }
            val totalCount = filtered.size

            call.ok(
                UsersByLanguageResponse(
                    groups = groups,
                    counts = counts,
                    total = totalCount,
                )
            )
        }

        // ── Super Admin: server strings management ────────────────────

        get("/api/admin/server-strings") {
            val adminUid = call.requirePlatformAdmin() ?: return@get

            val overrides = ServerStringOverrideRepository.getAll()
            val overrideMap = overrides.associateBy { "${it.stringKey}:${it.lang}" }

            val allKeys = ServerStrings.allKeys()
            val languages = ServerStrings.supportedLanguages

            val strings = allKeys.map { key ->
                val translations = languages.associateWith { lang ->
                    val override = overrideMap["$key:$lang"]
                    ServerStringTranslation(
                        value = override?.value ?: ServerStrings.compiledDefault(key, lang) ?: key,
                        isOverride = override != null,
                        updatedBy = override?.updatedBy,
                        updatedAt = override?.updatedAt,
                    )
                }
                ServerStringEntry(key = key, translations = translations)
            }.sortedBy { it.key }

            call.ok(
                ServerStringsResponse(
                    strings = strings,
                    totalKeys = allKeys.size,
                    languages = languages,
                )
            )
        }

        patch("/api/admin/server-strings/{key}") {
            val adminUid = call.requirePlatformAdmin() ?: return@patch
            val key = call.parameters["key"] ?: run {
                call.fail("Missing key parameter")
                return@patch
            }
            val req = runCatching { call.receive<ServerStringOverrideRequest>() }.getOrNull()
                ?: run {
                    call.fail("Invalid body: expected { \"lang\": \"hi\", \"value\": \"...\" }")
                    return@patch
                }

            if (req.lang !in SUPPORTED_LANGS) {
                call.fail("Invalid language code: ${req.lang}")
                return@patch
            }
            if (req.value.isBlank()) {
                call.fail("Value cannot be empty")
                return@patch
            }
            if (req.value.length > 2000) {
                call.fail("Value exceeds maximum length of 2000 characters")
                return@patch
            }
            if (key !in ServerStrings.allKeys()) {
                call.fail("Invalid string key: $key. Key must exist in compiled ServerStrings templates.")
                return@patch
            }

            ServerStringOverrideRepository.upsert(key, req.lang, req.value, adminUid)

            call.ok(
                ServerStringOverrideResponse(
                    key = key,
                    lang = req.lang,
                    value = req.value,
                    updatedAt = Instant.now().toString(),
                )
            )
        }

        delete("/api/admin/server-strings/{key}") {
            val adminUid = call.requirePlatformAdmin() ?: return@delete
            val key = call.parameters["key"] ?: run {
                call.fail("Missing key parameter")
                return@delete
            }
            val lang = call.request.queryParameters["lang"] ?: run {
                call.fail("Missing query parameter: lang")
                return@delete
            }

            if (lang !in SUPPORTED_LANGS) {
                call.fail("Invalid language code: $lang")
                return@delete
            }

            val deleted = ServerStringOverrideRepository.delete(key, lang, adminUid)
            if (!deleted) {
                call.fail("Override not found for key '$key' and language '$lang'", HttpStatusCode.NotFound, "NOT_FOUND")
                return@delete
            }

            call.ok(ServerStringDeleteResponse(deleted = true, key = key, lang = lang))
        }

        // ── Super Admin: bulk upsert server string overrides ──────────

        patch("/api/admin/server-strings/bulk") {
            val adminUid = call.requirePlatformAdmin() ?: return@patch
            val req = runCatching { call.receive<BulkUpsertRequest>() }.getOrNull()
                ?: run {
                    call.fail("Invalid body: expected { \"items\": [{ \"key\": \"...\", \"lang\": \"...\", \"value\": \"...\" }] }")
                    return@patch
                }

            if (req.items.isEmpty()) {
                call.fail("Items list cannot be empty")
                return@patch
            }
            if (req.items.size > 500) {
                call.fail("Cannot process more than 500 items at once")
                return@patch
            }

            val allKeys = ServerStrings.allKeys()
            var updated = 0
            val errors = mutableListOf<BulkUpsertErrorEntry>()

            for (item in req.items) {
                if (item.lang !in SUPPORTED_LANGS) {
                    errors.add(BulkUpsertErrorEntry(item.key, item.lang, "Invalid language code"))
                    continue
                }
                if (item.value.isBlank()) {
                    errors.add(BulkUpsertErrorEntry(item.key, item.lang, "Value cannot be empty"))
                    continue
                }
                if (item.value.length > 2000) {
                    errors.add(BulkUpsertErrorEntry(item.key, item.lang, "Value exceeds 2000 characters"))
                    continue
                }
                if (item.key !in allKeys) {
                    errors.add(BulkUpsertErrorEntry(item.key, item.lang, "Invalid string key"))
                    continue
                }
                try {
                    ServerStringOverrideRepository.upsert(item.key, item.lang, item.value, adminUid)
                    updated++
                } catch (e: Exception) {
                    errors.add(BulkUpsertErrorEntry(item.key, item.lang, e.message ?: "Unknown error"))
                }
            }

            call.ok(BulkUpsertResponse(updated = updated, errors = errors))
        }

        // ── Super Admin: string override history (audit log) ──────────

        get("/api/admin/server-strings/history") {
            val adminUid = call.requirePlatformAdmin() ?: return@get
            val keyFilter = call.request.queryParameters["key"]
            val langFilter = call.request.queryParameters["lang"]
            val limit = call.request.queryParameters["limit"]?.toIntOrNull()?.coerceIn(1, 500) ?: 100

            val history = ServerStringOverrideRepository.getHistory(keyFilter, langFilter, limit)

            // Resolve admin names for changed_by
            val adminNameCache = mutableMapOf<String, String?>()
            val dtos = history.map { entry ->
                val adminName = entry.changedBy?.let { uid ->
                    adminNameCache.getOrPut(uid) { ServerStringOverrideRepository.resolveAdminName(uid) }
                }
                StringOverrideHistoryDto(
                    id = entry.id,
                    stringKey = entry.stringKey,
                    lang = entry.lang,
                    oldValue = entry.oldValue,
                    newValue = entry.newValue,
                    action = entry.action,
                    changedBy = entry.changedBy,
                    changedByName = adminName,
                    changedAt = entry.changedAt,
                )
            }

            call.ok(StringOverrideHistoryResponseDto(history = dtos, total = dtos.size))
        }
    }
}
