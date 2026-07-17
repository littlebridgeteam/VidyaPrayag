/*
 * File: GamificationService.kt
 * Module: feature/gamification
 *
 * Core gamification engine — XP awarding, level calculation, stats management.
 * All XP flows through awardXp() which:
 *   1. Checks the kill switch (if off, XP is still tracked silently but no UI events fire)
 *   2. Applies active boosts (multiplier)
 *   3. Updates game_student_stats (totalXp, currentXp, currentLevel)
 *   4. Inserts into game_xp_ledger
 *   5. Checks for level-up
 *   6. Triggers badge criteria evaluation
 *
 * Spec ref: GAMIFICATION_SYSTEM_SPEC.md §5-7, §27
 */
package com.littlebridge.enrollplus.feature.gamification

import com.littlebridge.enrollplus.db.AppConfigTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.GameLevelDefinitionsTable
import com.littlebridge.enrollplus.db.GameStudentStatsTable
import com.littlebridge.enrollplus.db.GameXpBoostsTable
import com.littlebridge.enrollplus.db.GameXpLedgerTable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.exposed.sql.*
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Serializable
data class XpAwardResult(
    val xpAwarded: Int,
    val newTotalXp: Int,
    val newCurrentXp: Int,
    val newLevel: Int,
    val leveledUp: Boolean,
    val newTitle: String? = null,
    val multiplierApplied: Float = 1.0f,
    val gamificationEnabled: Boolean = true
)

@Serializable
data class StudentStatsDto(
    val studentId: String,
    val totalXp: Int,
    val currentXp: Int,
    val currentLevel: Int,
    val levelTitle: String,
    val streakDays: Int,
    val activeTitle: String? = null,
    val houseId: String? = null,
    val catchUpActive: Boolean = false
)

@Serializable
data class LevelDefinitionDto(
    val level: Int,
    val xpRequired: Int,
    val title: String,
    val iconName: String
)

@Serializable
data class GamificationFlagsDto(
    val isGamificationEnabled: Boolean = false,
    val gamificationLeaderboards: Boolean = true,
    val gamificationRewards: Boolean = true,
    val gamificationHouses: Boolean = true,
    val gamificationQuests: Boolean = true,
    val gamificationMentor: Boolean = true,
    val gamificationShoutouts: Boolean = true,
    val gamificationEvents: Boolean = true,
    val gamificationClassGoals: Boolean = true,
    val gamificationCombos: Boolean = true,
    val gamificationBoosts: Boolean = true
)

object GamificationService {

    private val logger = LoggerFactory.getLogger("GamificationService")
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    // ── Kill Switch ──────────────────────────────────────────────────────

    suspend fun isGamificationEnabled(): Boolean = dbQuery {
        val flagsRow = AppConfigTable.selectAll()
            .firstOrNull { it[AppConfigTable.key] == "flags" }
            ?: return@dbQuery false
        val flagsJson = flagsRow[AppConfigTable.value]
        try {
            val parsed = json.parseToJsonElement(flagsJson) as JsonObject
            parsed["is_gamification_enabled"]?.jsonPrimitive?.content?.toBoolean() ?: false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getGamificationFlags(): GamificationFlagsDto = dbQuery {
        val flagsRow = AppConfigTable.selectAll()
            .firstOrNull { it[AppConfigTable.key] == "flags" }
            ?: return@dbQuery GamificationFlagsDto()
        val flagsJson = flagsRow[AppConfigTable.value]
        try {
            val parsed = json.parseToJsonElement(flagsJson) as JsonObject
            GamificationFlagsDto(
                isGamificationEnabled = parsed["is_gamification_enabled"]?.jsonPrimitive?.content?.toBoolean() ?: false,
                gamificationLeaderboards = parsed["gamification_leaderboards"]?.jsonPrimitive?.content?.toBoolean() ?: true,
                gamificationRewards = parsed["gamification_rewards"]?.jsonPrimitive?.content?.toBoolean() ?: true,
                gamificationHouses = parsed["gamification_houses"]?.jsonPrimitive?.content?.toBoolean() ?: true,
                gamificationQuests = parsed["gamification_quests"]?.jsonPrimitive?.content?.toBoolean() ?: true,
                gamificationMentor = parsed["gamification_mentor"]?.jsonPrimitive?.content?.toBoolean() ?: true,
                gamificationShoutouts = parsed["gamification_shoutouts"]?.jsonPrimitive?.content?.toBoolean() ?: true,
                gamificationEvents = parsed["gamification_events"]?.jsonPrimitive?.content?.toBoolean() ?: true,
                gamificationClassGoals = parsed["gamification_class_goals"]?.jsonPrimitive?.content?.toBoolean() ?: true,
                gamificationCombos = parsed["gamification_combos"]?.jsonPrimitive?.content?.toBoolean() ?: true,
                gamificationBoosts = parsed["gamification_boosts"]?.jsonPrimitive?.content?.toBoolean() ?: true
            )
        } catch (e: Exception) {
            GamificationFlagsDto()
        }
    }

    private val granularFlagKeys = setOf(
        "gamification_leaderboards", "gamification_rewards", "gamification_houses",
        "gamification_quests", "gamification_mentor", "gamification_shoutouts",
        "gamification_events", "gamification_class_goals", "gamification_combos",
        "gamification_boosts"
    )

    suspend fun setGamificationEnabled(enabled: Boolean): Boolean = dbQuery {
        val flagsRow = AppConfigTable.selectAll()
            .firstOrNull { it[AppConfigTable.key] == "flags" }
            ?: return@dbQuery false
        val flagsJson = flagsRow[AppConfigTable.value]
        try {
            val parsed = json.parseToJsonElement(flagsJson) as JsonObject
            val updated = JsonObject(parsed.toMutableMap().apply {
                this["is_gamification_enabled"] = kotlinx.serialization.json.JsonPrimitive(enabled)
            })
            AppConfigTable.update({ AppConfigTable.key eq "flags" }) {
                it[AppConfigTable.value] = json.encodeToString(JsonObject.serializer(), updated)
                it[AppConfigTable.updatedAt] = Instant.now()
            }
            logger.info("Gamification kill switch set to: $enabled")
            true
        } catch (e: Exception) {
            logger.error("Failed to set gamification flag: ${e.message}")
            false
        }
    }

    suspend fun setGranularFlag(flagKey: String, enabled: Boolean): Boolean = dbQuery {
        val dbKey = "gamification_" + flagKey.replace("gamification_", "")
        if (dbKey !in granularFlagKeys) return@dbQuery false
        val flagsRow = AppConfigTable.selectAll()
            .firstOrNull { it[AppConfigTable.key] == "flags" }
            ?: return@dbQuery false
        val flagsJson = flagsRow[AppConfigTable.value]
        try {
            val parsed = json.parseToJsonElement(flagsJson) as JsonObject
            val updated = JsonObject(parsed.toMutableMap().apply {
                this[dbKey] = kotlinx.serialization.json.JsonPrimitive(enabled)
            })
            AppConfigTable.update({ AppConfigTable.key eq "flags" }) {
                it[AppConfigTable.value] = json.encodeToString(JsonObject.serializer(), updated)
                it[AppConfigTable.updatedAt] = Instant.now()
            }
            logger.info("Granular flag $dbKey set to: $enabled")
            true
        } catch (e: Exception) {
            logger.error("Failed to set granular flag $dbKey: ${e.message}")
            false
        }
    }

    // ── XP Awarding ──────────────────────────────────────────────────────

    suspend fun awardXp(
        studentId: UUID,
        schoolId: UUID,
        amount: Int,
        reason: String,
        source: String,
        category: String
    ): XpAwardResult = dbQuery {
        val flags = getGamificationFlagsRaw()
        val enabled = flags["is_gamification_enabled"]?.toBoolean() ?: false
        val combosEnabled = flags["gamification_combos"]?.toBoolean() ?: true

        // Get active boosts
        val boostMultiplier = getActiveBoostMultiplier(schoolId, studentId)

        // Get combo multiplier if combos are enabled and source matches a combo type
        val comboType = sourceToComboType(source)
        val comboMultiplier = if (combosEnabled && comboType != null) {
            ComboService.getComboMultiplier(studentId, comboType)
        } else {
            1.0f
        }

        // Combined multiplier (capped at x5 per spec)
        val totalMultiplier = minOf(boostMultiplier * comboMultiplier, 5.0f)
        val finalAmount = (amount * totalMultiplier).toInt()

        // Ensure student stats row exists
        ensureStatsRow(studentId, schoolId)

        // Get current stats
        val currentStats = GameStudentStatsTable.selectAll()
            .firstOrNull { it[GameStudentStatsTable.studentId] == studentId }
            ?: return@dbQuery XpAwardResult(0, 0, 0, 1, false, gamificationEnabled = enabled)

        val oldTotalXp = currentStats[GameStudentStatsTable.totalXp]
        val oldCurrentXp = currentStats[GameStudentStatsTable.currentXp]
        val oldLevel = currentStats[GameStudentStatsTable.currentLevel]

        val newTotalXp = oldTotalXp + finalAmount
        val newCurrentXp = oldCurrentXp + finalAmount

        // Calculate new level
        val levelDefs = GameLevelDefinitionsTable.selectAll()
            .where { GameLevelDefinitionsTable.schoolId.isNull() }
            .orderBy(GameLevelDefinitionsTable.level)
            .toList()

        var newLevel = oldLevel
        var newTitle: String? = null
        for (def in levelDefs) {
            if (newTotalXp >= def[GameLevelDefinitionsTable.xpRequired]) {
                newLevel = def[GameLevelDefinitionsTable.level]
                newTitle = def[GameLevelDefinitionsTable.title]
            } else {
                break
            }
        }

        val leveledUp = newLevel > oldLevel

        // Update stats
        GameStudentStatsTable.update({ GameStudentStatsTable.studentId eq studentId }) {
            it[GameStudentStatsTable.totalXp] = newTotalXp
            it[GameStudentStatsTable.currentXp] = newCurrentXp
            it[GameStudentStatsTable.currentLevel] = newLevel
            it[GameStudentStatsTable.lastActiveDate] = LocalDate.now()
            it[GameStudentStatsTable.updatedAt] = Instant.now()
        }

        // Insert ledger entry
        GameXpLedgerTable.insert {
            it[GameXpLedgerTable.studentId] = studentId
            it[GameXpLedgerTable.schoolId] = schoolId
            it[GameXpLedgerTable.amount] = finalAmount
            it[GameXpLedgerTable.reason] = reason
            it[GameXpLedgerTable.xpSource] = source
            it[GameXpLedgerTable.category] = category
            it[GameXpLedgerTable.multiplier] = totalMultiplier
            it[GameXpLedgerTable.createdAt] = Instant.now()
        }

        // Update streak
        updateStreak(studentId, currentStats)

        XpAwardResult(
            xpAwarded = finalAmount,
            newTotalXp = newTotalXp,
            newCurrentXp = newCurrentXp,
            newLevel = newLevel,
            leveledUp = leveledUp,
            newTitle = if (leveledUp) newTitle else null,
            multiplierApplied = totalMultiplier,
            gamificationEnabled = enabled
        )
    }

    // ── Stats Retrieval ──────────────────────────────────────────────────

    suspend fun getStudentStats(studentId: UUID): StudentStatsDto? = dbQuery {
        val stats = GameStudentStatsTable.selectAll()
            .firstOrNull { it[GameStudentStatsTable.studentId] == studentId }
            ?: return@dbQuery null

        val levelTitle = GameLevelDefinitionsTable.selectAll()
            .where {
                (GameLevelDefinitionsTable.schoolId.isNull()) and
                (GameLevelDefinitionsTable.level eq stats[GameStudentStatsTable.currentLevel]) and
                (GameLevelDefinitionsTable.isActive eq true)
            }
            .firstOrNull()?.get(GameLevelDefinitionsTable.title) ?: "Beginner"

        StudentStatsDto(
            studentId = studentId.toString(),
            totalXp = stats[GameStudentStatsTable.totalXp],
            currentXp = stats[GameStudentStatsTable.currentXp],
            currentLevel = stats[GameStudentStatsTable.currentLevel],
            levelTitle = levelTitle,
            streakDays = stats[GameStudentStatsTable.streakDays],
            activeTitle = stats[GameStudentStatsTable.activeTitle],
            houseId = stats[GameStudentStatsTable.houseId]?.toString(),
            catchUpActive = stats[GameStudentStatsTable.catchUpActive]
        )
    }

    suspend fun getLevelDefinitions(): List<LevelDefinitionDto> = dbQuery {
        GameLevelDefinitionsTable.selectAll()
            .where { GameLevelDefinitionsTable.schoolId.isNull() and (GameLevelDefinitionsTable.isActive eq true) }
            .orderBy(GameLevelDefinitionsTable.level)
            .map {
                LevelDefinitionDto(
                    level = it[GameLevelDefinitionsTable.level],
                    xpRequired = it[GameLevelDefinitionsTable.xpRequired],
                    title = it[GameLevelDefinitionsTable.title],
                    iconName = it[GameLevelDefinitionsTable.iconName]
                )
            }
    }

    suspend fun createLevel(schoolId: UUID?, req: CreateLevelRequest): LevelDefinitionDto? = dbQuery {
        val existing = GameLevelDefinitionsTable.selectAll()
            .where { (GameLevelDefinitionsTable.schoolId eq schoolId) and (GameLevelDefinitionsTable.level eq req.level) }
            .firstOrNull()
        if (existing != null) return@dbQuery null

        GameLevelDefinitionsTable.insert {
            it[GameLevelDefinitionsTable.schoolId] = schoolId
            it[GameLevelDefinitionsTable.level] = req.level
            it[GameLevelDefinitionsTable.xpRequired] = req.xpRequired
            it[GameLevelDefinitionsTable.title] = req.title
            it[GameLevelDefinitionsTable.iconName] = req.iconName
            it[GameLevelDefinitionsTable.isActive] = true
            it[GameLevelDefinitionsTable.createdAt] = Instant.now()
        }
        LevelDefinitionDto(
            level = req.level, xpRequired = req.xpRequired,
            title = req.title, iconName = req.iconName
        )
    }

    suspend fun toggleLevelActive(level: Int, isActive: Boolean): Boolean = dbQuery {
        GameLevelDefinitionsTable.update({
            (GameLevelDefinitionsTable.schoolId.isNull()) and (GameLevelDefinitionsTable.level eq level)
        }) {
            it[GameLevelDefinitionsTable.isActive] = isActive
        } > 0
    }

    // ── Internal helpers ─────────────────────────────────────────────────

    private fun sourceToComboType(source: String): String? {
        return when (source.uppercase()) {
            "HOMEWORK_REVIEWED", "HOMEWORK" -> "HOMEWORK"
            "ATTENDANCE_PRESENT", "ATTENDANCE" -> "ATTENDANCE"
            "QUIZ_COMPLETED", "SYLLABUS_TOPIC_COVERED", "AI_TUTOR", "STUDY" -> "STUDY"
            "LIBRARY_BOOK_RETURNED", "READING" -> "READING"
            else -> null
        }
    }

    private fun getGamificationFlagsRaw(): Map<String, String> {
        val flagsRow = AppConfigTable.selectAll()
            .firstOrNull { it[AppConfigTable.key] == "flags" }
            ?: return emptyMap()
        val flagsJson = flagsRow[AppConfigTable.value]
        return try {
            val parsed = json.parseToJsonElement(flagsJson) as JsonObject
            parsed.mapValues { it.value.jsonPrimitive.content }
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private fun getActiveBoostMultiplier(schoolId: UUID, studentId: UUID): Float {
        val now = Instant.now()
        val boosts = GameXpBoostsTable.selectAll()
            .where {
                (GameXpBoostsTable.schoolId eq schoolId) and
                (GameXpBoostsTable.isActive eq true) and
                (GameXpBoostsTable.startsAt lessEq now) and
                (GameXpBoostsTable.endsAt greater now)
            }
            .toList()

        var multiplier = 1.0f
        for (boost in boosts) {
            val scope = boost[GameXpBoostsTable.targetScope]
            val targetId = boost[GameXpBoostsTable.targetId]
            val applies = when (scope) {
                "ALL" -> true
                "STUDENT" -> targetId == studentId
                "SCHOOL" -> true // Already filtered by schoolId
                else -> false
            }
            if (applies) {
                multiplier *= boost[GameXpBoostsTable.multiplier]
            }
        }
        return multiplier
    }

    private fun ensureStatsRow(studentId: UUID, schoolId: UUID) {
        val existing = GameStudentStatsTable.selectAll()
            .firstOrNull { it[GameStudentStatsTable.studentId] == studentId }
        if (existing == null) {
            GameStudentStatsTable.insert {
                it[GameStudentStatsTable.studentId] = studentId
                it[GameStudentStatsTable.schoolId] = schoolId
                it[GameStudentStatsTable.totalXp] = 0
                it[GameStudentStatsTable.currentXp] = 0
                it[GameStudentStatsTable.currentLevel] = 1
                it[GameStudentStatsTable.streakDays] = 0
                it[GameStudentStatsTable.updatedAt] = Instant.now()
            }
        }
    }

    private fun updateStreak(studentId: UUID, stats: org.jetbrains.exposed.sql.ResultRow) {
        val today = LocalDate.now()
        val lastActive = stats[GameStudentStatsTable.lastActiveDate]
        val currentStreak = stats[GameStudentStatsTable.streakDays]

        val newStreak = when {
            lastActive == null -> 1
            lastActive == today -> currentStreak // Already counted today
            lastActive.plusDays(1) == today -> currentStreak + 1
            else -> 1 // Streak broken
        }

        if (newStreak != currentStreak) {
            GameStudentStatsTable.update({ GameStudentStatsTable.studentId eq studentId }) {
                it[GameStudentStatsTable.streakDays] = newStreak
            }
        }
    }
}
