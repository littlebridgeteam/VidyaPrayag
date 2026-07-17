/*
 * File: BadgeCriteriaEvaluator.kt
 * Module: feature/gamification
 *
 * Evaluates badge criteria against student activity and awards badges.
 * Called after every XP award to check if any new badges should be granted.
 *
 * Criteria types:
 *   - count: threshold-based (e.g. "complete 5 assessments")
 *   - level: level-based (e.g. "reach level 5")
 *   - manual: teacher/admin awarded
 *   - birthday: auto-awarded on student's birthday
 *   - anniversary: auto-awarded on platform anniversary
 *
 * Spec ref: GAMIFICATION_SYSTEM_SPEC.md §8, §27
 */
package com.littlebridge.enrollplus.feature.gamification

import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.GameBadgeDefinitionsTable
import com.littlebridge.enrollplus.db.GameStudentBadgesTable
import com.littlebridge.enrollplus.db.GameStudentStatsTable
import com.littlebridge.enrollplus.db.GameXpLedgerTable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.exposed.sql.*
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID

@Serializable
data class BadgeDefinitionDto(
    val id: String,
    val code: String,
    val name: String,
    val description: String,
    val iconName: String,
    val category: String,
    val rarity: String,
    val xpRequirement: Int,
    val isSeasonal: Boolean
)

@Serializable
data class StudentBadgeDto(
    val badgeId: String,
    val badgeCode: String,
    val badgeName: String,
    val badgeIcon: String,
    val badgeCategory: String,
    val badgeRarity: String,
    val earnedAt: String
)

object BadgeCriteriaEvaluator {

    private val logger = LoggerFactory.getLogger("BadgeCriteriaEvaluator")
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    /**
     * Evaluates all active badges for a student and awards any newly-earned ones.
     * Returns the list of newly awarded badges.
     */
    suspend fun evaluateBadges(studentId: UUID, schoolId: UUID): List<StudentBadgeDto> = dbQuery {
        val stats = GameStudentStatsTable.selectAll()
            .firstOrNull { it[GameStudentStatsTable.studentId] == studentId }
            ?: return@dbQuery emptyList()

        val totalXp = stats[GameStudentStatsTable.totalXp]
        val currentLevel = stats[GameStudentStatsTable.currentLevel]

        // Get all active badges
        val allBadges = GameBadgeDefinitionsTable.selectAll()
            .where { GameBadgeDefinitionsTable.isActive eq true }
            .toList()

        // Get already-earned badge IDs
        val earnedBadgeIds = GameStudentBadgesTable.selectAll()
            .where { GameStudentBadgesTable.studentId eq studentId }
            .map { it[GameStudentBadgesTable.badgeId] }
            .toSet()

        val newlyAwarded = mutableListOf<StudentBadgeDto>()

        for (badge in allBadges) {
            val badgeId = badge[GameBadgeDefinitionsTable.id].value
            if (badgeId in earnedBadgeIds) continue

            val criteriaJson = badge[GameBadgeDefinitionsTable.criteriaJson]
            val criteria = try {
                json.parseToJsonElement(criteriaJson) as JsonObject
            } catch (e: Exception) { continue }

            val type = criteria["type"]?.jsonPrimitive?.contentOrNull ?: continue
            val earned = when (type) {
                "level" -> {
                    val requiredLevel = criteria["level"]?.jsonPrimitive?.intOrNull ?: 0
                    currentLevel >= requiredLevel
                }
                "count" -> {
                    val source = criteria["source"]?.jsonPrimitive?.contentOrNull
                    val threshold = criteria["threshold"]?.jsonPrimitive?.intOrNull ?: 0
                    if (source != null && threshold > 0) {
                        countStudentEvents(studentId, source) >= threshold
                    } else false
                }
                "xp" -> {
                    val xpThreshold = criteria["xp"]?.jsonPrimitive?.intOrNull ?: 0
                    totalXp >= xpThreshold
                }
                "manual" -> false // Manual badges are awarded by teacher/admin only
                "birthday" -> false // Handled by scheduled job
                "anniversary" -> false // Handled by scheduled job
                else -> false
            }

            if (earned) {
                GameStudentBadgesTable.insert {
                    it[GameStudentBadgesTable.studentId] = studentId
                    it[GameStudentBadgesTable.badgeId] = badgeId
                    it[GameStudentBadgesTable.earnedAt] = Instant.now()
                    it[GameStudentBadgesTable.awardedBy] = null
                }
                newlyAwarded.add(
                    StudentBadgeDto(
                        badgeId = badgeId.toString(),
                        badgeCode = badge[GameBadgeDefinitionsTable.code],
                        badgeName = badge[GameBadgeDefinitionsTable.name],
                        badgeIcon = badge[GameBadgeDefinitionsTable.iconName],
                        badgeCategory = badge[GameBadgeDefinitionsTable.category],
                        badgeRarity = badge[GameBadgeDefinitionsTable.rarity],
                        earnedAt = Instant.now().toString()
                    )
                )
                logger.info("Badge awarded: ${badge[GameBadgeDefinitionsTable.code]} to student $studentId")
            }
        }

        newlyAwarded
    }

    /**
     * Manually awards a badge to a student (teacher/admin action).
     */
    suspend fun manuallyAwardBadge(studentId: UUID, badgeId: UUID, awardedBy: UUID): Boolean = dbQuery {
        val alreadyEarned = GameStudentBadgesTable.selectAll()
            .where { (GameStudentBadgesTable.studentId eq studentId) and (GameStudentBadgesTable.badgeId eq badgeId) }
            .count() > 0

        if (alreadyEarned) return@dbQuery false

        GameStudentBadgesTable.insert {
            it[GameStudentBadgesTable.studentId] = studentId
            it[GameStudentBadgesTable.badgeId] = badgeId
            it[GameStudentBadgesTable.earnedAt] = Instant.now()
            it[GameStudentBadgesTable.awardedBy] = awardedBy
        }
        true
    }

    /**
     * Gets all badges earned by a student.
     */
    suspend fun getStudentBadges(studentId: UUID): List<StudentBadgeDto> = dbQuery {
        GameStudentBadgesTable
            .join(GameBadgeDefinitionsTable, JoinType.INNER,
                GameStudentBadgesTable.badgeId, GameBadgeDefinitionsTable.id)
            .selectAll()
            .where { GameStudentBadgesTable.studentId eq studentId }
            .map { row ->
                StudentBadgeDto(
                    badgeId = row[GameBadgeDefinitionsTable.id].value.toString(),
                    badgeCode = row[GameBadgeDefinitionsTable.code],
                    badgeName = row[GameBadgeDefinitionsTable.name],
                    badgeIcon = row[GameBadgeDefinitionsTable.iconName],
                    badgeCategory = row[GameBadgeDefinitionsTable.category],
                    badgeRarity = row[GameBadgeDefinitionsTable.rarity],
                    earnedAt = row[GameStudentBadgesTable.earnedAt].toString()
                )
            }
    }

    /**
     * Gets all available badge definitions.
     */
    suspend fun getAllBadgeDefinitions(): List<BadgeDefinitionDto> = dbQuery {
        GameBadgeDefinitionsTable.selectAll()
            .where { GameBadgeDefinitionsTable.isActive eq true }
            .map {
                BadgeDefinitionDto(
                    id = it[GameBadgeDefinitionsTable.id].value.toString(),
                    code = it[GameBadgeDefinitionsTable.code],
                    name = it[GameBadgeDefinitionsTable.name],
                    description = it[GameBadgeDefinitionsTable.description],
                    iconName = it[GameBadgeDefinitionsTable.iconName],
                    category = it[GameBadgeDefinitionsTable.category],
                    rarity = it[GameBadgeDefinitionsTable.rarity],
                    xpRequirement = it[GameBadgeDefinitionsTable.xpRequirement],
                    isSeasonal = it[GameBadgeDefinitionsTable.isSeasonal]
                )
            }
    }

    suspend fun createBadge(schoolId: UUID?, req: CreateBadgeRequest): BadgeDefinitionDto? = dbQuery {
        val existing = GameBadgeDefinitionsTable.selectAll()
            .where { GameBadgeDefinitionsTable.code eq req.code }
            .firstOrNull()
        if (existing != null) return@dbQuery null

        val id = GameBadgeDefinitionsTable.insert {
            it[GameBadgeDefinitionsTable.schoolId] = schoolId
            it[GameBadgeDefinitionsTable.code] = req.code
            it[GameBadgeDefinitionsTable.name] = req.name
            it[GameBadgeDefinitionsTable.description] = req.description
            it[GameBadgeDefinitionsTable.iconName] = req.iconName
            it[GameBadgeDefinitionsTable.category] = req.category
            it[GameBadgeDefinitionsTable.rarity] = req.rarity
            it[GameBadgeDefinitionsTable.xpRequirement] = req.xpRequirement
            it[GameBadgeDefinitionsTable.criteriaJson] = req.criteriaJson
            it[GameBadgeDefinitionsTable.isActive] = true
            it[GameBadgeDefinitionsTable.isSeasonal] = req.isSeasonal
            it[GameBadgeDefinitionsTable.createdAt] = Instant.now()
        }[GameBadgeDefinitionsTable.id].value

        BadgeDefinitionDto(
            id = id.toString(), code = req.code, name = req.name,
            description = req.description, iconName = req.iconName,
            category = req.category, rarity = req.rarity,
            xpRequirement = req.xpRequirement, isSeasonal = req.isSeasonal
        )
    }

    suspend fun toggleBadgeActive(badgeId: UUID, isActive: Boolean): Boolean = dbQuery {
        GameBadgeDefinitionsTable.update({ GameBadgeDefinitionsTable.id eq badgeId }) {
            it[GameBadgeDefinitionsTable.isActive] = isActive
        } > 0
    }

    // Count events from the XP ledger by source
    private fun countStudentEvents(studentId: UUID, source: String): Int {
        return GameXpLedgerTable.selectAll()
            .where { (GameXpLedgerTable.studentId eq studentId) and (GameXpLedgerTable.xpSource eq source) }
            .count()
            .toInt()
    }
}
