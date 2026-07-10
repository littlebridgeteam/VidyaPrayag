/*
 * File: GamificationSubsystems.kt
 * Module: feature/gamification
 *
 * Subsystem services for the gamification platform:
 *   - QuestService: quest assignment, progress tracking, completion
 *   - HouseService: house assignment, house points, leaderboard
 *   - RewardService: reward catalog, redemption workflow
 *   - LeaderboardService: school/class/leaderboard rankings
 *   - SeasonalEventService: active events, event participation
 *
 * Spec ref: GAMIFICATION_SYSTEM_SPEC.md §9-18, §27
 */
package com.littlebridge.enrollplus.feature.gamification

import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.GameHousesTable
import com.littlebridge.enrollplus.db.GameQuestDefinitionsTable
import com.littlebridge.enrollplus.db.GameRewardCatalogTable
import com.littlebridge.enrollplus.db.GameRewardRedemptionsTable
import com.littlebridge.enrollplus.db.GameSeasonalEventsTable
import com.littlebridge.enrollplus.db.GameStudentHouseAssignmentsTable
import com.littlebridge.enrollplus.db.GameStudentQuestsTable
import com.littlebridge.enrollplus.db.GameStudentStatsTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

// ── Quest DTOs ────────────────────────────────────────────────────────

@Serializable
data class QuestDefinitionDto(
    val id: String,
    val code: String,
    val name: String,
    val description: String,
    val xpReward: Int,
    val questType: String,
    val isActive: Boolean
)

@Serializable
data class StudentQuestDto(
    val id: String,
    val questCode: String,
    val questName: String,
    val progress: Int,
    val target: Int,
    val completed: Boolean,
    val xpReward: Int,
    val completedAt: String?
)

// ── House DTOs ────────────────────────────────────────────────────────

@Serializable
data class HouseDto(
    val id: String,
    val name: String,
    val iconName: String,
    val color: String,
    val totalPoints: Int,
    val memberCount: Int
)

// ── Reward DTOs ───────────────────────────────────────────────────────

@Serializable
data class RewardDto(
    val id: String,
    val name: String,
    val description: String,
    val xpCost: Int,
    val fulfillmentRole: String,
    val stockRemaining: Int?,
    val isActive: Boolean
)

@Serializable
data class RewardRedemptionDto(
    val id: String,
    val rewardId: String,
    val rewardName: String,
    val xpSpent: Int,
    val status: String,
    val createdAt: String
)

// ── Leaderboard DTOs ──────────────────────────────────────────────────

@Serializable
data class LeaderboardEntryDto(
    val rank: Int,
    val studentId: String,
    val totalXp: Int,
    val currentLevel: Int,
    val levelTitle: String,
    val streakDays: Int
)

// ── Seasonal Event DTOs ───────────────────────────────────────────────

@Serializable
data class SeasonalEventDto(
    val id: String,
    val code: String,
    val name: String,
    val startDate: String,
    val endDate: String,
    val isActive: Boolean
)

// ══════════════════════════════════════════════════════════════════════
// QuestService
// ══════════════════════════════════════════════════════════════════════

object QuestService {

    suspend fun getActiveQuests(): List<QuestDefinitionDto> = dbQuery {
        GameQuestDefinitionsTable.selectAll()
            .where { GameQuestDefinitionsTable.isActive eq true }
            .map {
                QuestDefinitionDto(
                    id = it[GameQuestDefinitionsTable.id].value.toString(),
                    code = it[GameQuestDefinitionsTable.code],
                    name = it[GameQuestDefinitionsTable.name],
                    description = it[GameQuestDefinitionsTable.description],
                    xpReward = it[GameQuestDefinitionsTable.xpReward],
                    questType = it[GameQuestDefinitionsTable.questType],
                    isActive = it[GameQuestDefinitionsTable.isActive]
                )
            }
    }

    suspend fun getStudentQuests(studentId: UUID): List<StudentQuestDto> = dbQuery {
        GameStudentQuestsTable
            .join(GameQuestDefinitionsTable, JoinType.INNER,
                GameStudentQuestsTable.questId, GameQuestDefinitionsTable.id)
            .selectAll()
            .where { GameStudentQuestsTable.studentId eq studentId }
            .map {
                StudentQuestDto(
                    id = it[GameStudentQuestsTable.id].value.toString(),
                    questCode = it[GameQuestDefinitionsTable.code],
                    questName = it[GameQuestDefinitionsTable.name],
                    progress = it[GameStudentQuestsTable.progress],
                    target = it[GameStudentQuestsTable.target],
                    completed = it[GameStudentQuestsTable.completed],
                    xpReward = it[GameQuestDefinitionsTable.xpReward],
                    completedAt = it[GameStudentQuestsTable.completedAt]?.toString()
                )
            }
    }

    suspend fun assignQuest(studentId: UUID, questId: UUID, schoolId: UUID): Boolean = dbQuery {
        val alreadyAssigned = GameStudentQuestsTable.selectAll()
            .where { (GameStudentQuestsTable.studentId eq studentId) and (GameStudentQuestsTable.questId eq questId) }
            .count() > 0
        if (alreadyAssigned) return@dbQuery false

        GameStudentQuestsTable.insert {
            it[GameStudentQuestsTable.studentId] = studentId
            it[GameStudentQuestsTable.questId] = questId
            it[GameStudentQuestsTable.schoolId] = schoolId
            it[GameStudentQuestsTable.progress] = 0
            it[GameStudentQuestsTable.target] = 1
            it[GameStudentQuestsTable.completed] = false
            it[GameStudentQuestsTable.expiresAt] = Instant.now().plusSeconds(86400)
            it[GameStudentQuestsTable.createdAt] = Instant.now()
        }
        true
    }

    suspend fun updateQuestProgress(studentId: UUID, questId: UUID, progressDelta: Int): Boolean = dbQuery {
        val quest = GameStudentQuestsTable.selectAll()
            .where { (GameStudentQuestsTable.studentId eq studentId) and (GameStudentQuestsTable.questId eq questId) }
            .firstOrNull() ?: return@dbQuery false

        if (quest[GameStudentQuestsTable.completed]) return@dbQuery false

        val newProgress = quest[GameStudentQuestsTable.progress] + progressDelta
        val target = quest[GameStudentQuestsTable.target]

        val completed = newProgress >= target
        GameStudentQuestsTable.update({
            (GameStudentQuestsTable.studentId eq studentId) and (GameStudentQuestsTable.questId eq questId)
        }) {
            it[GameStudentQuestsTable.progress] = newProgress
            if (completed) {
                it[GameStudentQuestsTable.completed] = true
                it[GameStudentQuestsTable.completedAt] = Instant.now()
            }
        }
        completed
    }
}

// ══════════════════════════════════════════════════════════════════════
// HouseService
// ══════════════════════════════════════════════════════════════════════

object HouseService {

    suspend fun getHouses(schoolId: UUID): List<HouseDto> = dbQuery {
        GameHousesTable.selectAll()
            .where { GameHousesTable.schoolId eq schoolId }
            .map { row ->
                val houseId = row[GameHousesTable.id].value
                val memberCount = GameStudentHouseAssignmentsTable.selectAll()
                    .where { GameStudentHouseAssignmentsTable.houseId eq houseId }
                    .count().toInt()
                val totalPoints = GameStudentStatsTable.selectAll()
                    .where { GameStudentStatsTable.houseId eq houseId }
                    .sumOf { it[GameStudentStatsTable.totalXp] }
                HouseDto(
                    id = houseId.toString(),
                    name = row[GameHousesTable.name],
                    iconName = row[GameHousesTable.iconName],
                    color = row[GameHousesTable.color],
                    totalPoints = totalPoints,
                    memberCount = memberCount
                )
            }
            .sortedByDescending { it.totalPoints }
    }

    suspend fun assignStudentToHouse(studentId: UUID, houseId: UUID, schoolId: UUID): Boolean = dbQuery {
        val existing = GameStudentHouseAssignmentsTable.selectAll()
            .where { GameStudentHouseAssignmentsTable.studentId eq studentId }
            .firstOrNull()

        if (existing != null) {
            GameStudentHouseAssignmentsTable.update({
                GameStudentHouseAssignmentsTable.studentId eq studentId
            }) {
                it[GameStudentHouseAssignmentsTable.houseId] = houseId
            }
        } else {
            GameStudentHouseAssignmentsTable.insert {
                it[GameStudentHouseAssignmentsTable.studentId] = studentId
                it[GameStudentHouseAssignmentsTable.houseId] = houseId
                it[GameStudentHouseAssignmentsTable.schoolId] = schoolId
                it[GameStudentHouseAssignmentsTable.assignedAt] = Instant.now()
            }
        }

        GameStudentStatsTable.update({ GameStudentStatsTable.studentId eq studentId }) {
            it[GameStudentStatsTable.houseId] = houseId
        }
        true
    }

    suspend fun getStudentHouse(studentId: UUID): HouseDto? = dbQuery {
        val assignment = GameStudentHouseAssignmentsTable.selectAll()
            .where { GameStudentHouseAssignmentsTable.studentId eq studentId }
            .firstOrNull() ?: return@dbQuery null

        val houseId = assignment[GameStudentHouseAssignmentsTable.houseId]
        val house = GameHousesTable.selectAll()
            .where { GameHousesTable.id eq houseId }
            .firstOrNull() ?: return@dbQuery null

        HouseDto(
            id = house[GameHousesTable.id].value.toString(),
            name = house[GameHousesTable.name],
            iconName = house[GameHousesTable.iconName],
            color = house[GameHousesTable.color],
            totalPoints = 0,
            memberCount = 0
        )
    }
}

// ══════════════════════════════════════════════════════════════════════
// RewardService
// ══════════════════════════════════════════════════════════════════════

object RewardService {

    suspend fun getRewardCatalog(schoolId: UUID): List<RewardDto> = dbQuery {
        GameRewardCatalogTable.selectAll()
            .where { (GameRewardCatalogTable.schoolId eq schoolId) and (GameRewardCatalogTable.isActive eq true) }
            .map {
                RewardDto(
                    id = it[GameRewardCatalogTable.id].value.toString(),
                    name = it[GameRewardCatalogTable.name],
                    description = it[GameRewardCatalogTable.description],
                    xpCost = it[GameRewardCatalogTable.xpCost],
                    fulfillmentRole = it[GameRewardCatalogTable.fulfillmentRole],
                    stockRemaining = it[GameRewardCatalogTable.stockRemaining],
                    isActive = it[GameRewardCatalogTable.isActive]
                )
            }
    }

    suspend fun redeemReward(studentId: UUID, rewardId: UUID, schoolId: UUID): RewardRedemptionDto? = dbQuery {
        val reward = GameRewardCatalogTable.selectAll()
            .where { (GameRewardCatalogTable.id eq rewardId) and (GameRewardCatalogTable.isActive eq true) }
            .firstOrNull() ?: return@dbQuery null

        val xpCost = reward[GameRewardCatalogTable.xpCost]
        val stock = reward[GameRewardCatalogTable.stockRemaining]
        if (stock != null && stock <= 0) return@dbQuery null

        val stats = GameStudentStatsTable.selectAll()
            .where { GameStudentStatsTable.studentId eq studentId }
            .firstOrNull() ?: return@dbQuery null

        if (stats[GameStudentStatsTable.currentXp] < xpCost) return@dbQuery null

        // Deduct XP
        GameStudentStatsTable.update({ GameStudentStatsTable.studentId eq studentId }) {
            it[GameStudentStatsTable.currentXp] = stats[GameStudentStatsTable.currentXp] - xpCost
            it[GameStudentStatsTable.updatedAt] = Instant.now()
        }

        // Decrement stock if tracked
        if (stock != null) {
            GameRewardCatalogTable.update({ GameRewardCatalogTable.id eq rewardId }) {
                it[GameRewardCatalogTable.stockRemaining] = stock - 1
            }
        }

        val redemptionId = GameRewardRedemptionsTable.insert {
            it[GameRewardRedemptionsTable.studentId] = studentId
            it[GameRewardRedemptionsTable.rewardId] = rewardId
            it[GameRewardRedemptionsTable.schoolId] = schoolId
            it[GameRewardRedemptionsTable.xpSpent] = xpCost
            it[GameRewardRedemptionsTable.status] = "PENDING"
            it[GameRewardRedemptionsTable.createdAt] = Instant.now()
        }[GameRewardRedemptionsTable.id].value

        RewardRedemptionDto(
            id = redemptionId.toString(),
            rewardId = rewardId.toString(),
            rewardName = reward[GameRewardCatalogTable.name],
            xpSpent = xpCost,
            status = "PENDING",
            createdAt = Instant.now().toString()
        )
    }

    suspend fun getStudentRedemptions(studentId: UUID): List<RewardRedemptionDto> = dbQuery {
        GameRewardRedemptionsTable
            .join(GameRewardCatalogTable, JoinType.INNER,
                GameRewardRedemptionsTable.rewardId, GameRewardCatalogTable.id)
            .selectAll()
            .where { GameRewardRedemptionsTable.studentId eq studentId }
            .orderBy(GameRewardRedemptionsTable.createdAt, SortOrder.DESC)
            .map {
                RewardRedemptionDto(
                    id = it[GameRewardRedemptionsTable.id].value.toString(),
                    rewardId = it[GameRewardCatalogTable.id].value.toString(),
                    rewardName = it[GameRewardCatalogTable.name],
                    xpSpent = it[GameRewardRedemptionsTable.xpSpent],
                    status = it[GameRewardRedemptionsTable.status],
                    createdAt = it[GameRewardRedemptionsTable.createdAt].toString()
                )
            }
    }
}

// ══════════════════════════════════════════════════════════════════════
// LeaderboardService
// ══════════════════════════════════════════════════════════════════════

object LeaderboardService {

    suspend fun getSchoolLeaderboard(schoolId: UUID, limit: Int = 50): List<LeaderboardEntryDto> = dbQuery {
        GameStudentStatsTable.selectAll()
            .where { GameStudentStatsTable.schoolId eq schoolId }
            .orderBy(GameStudentStatsTable.totalXp, SortOrder.DESC)
            .limit(limit)
            .mapIndexed { index, row ->
                val levelTitle = GameLevelDefinitionsTableFromStats(row[GameStudentStatsTable.currentLevel])
                LeaderboardEntryDto(
                    rank = index + 1,
                    studentId = row[GameStudentStatsTable.studentId].toString(),
                    totalXp = row[GameStudentStatsTable.totalXp],
                    currentLevel = row[GameStudentStatsTable.currentLevel],
                    levelTitle = levelTitle,
                    streakDays = row[GameStudentStatsTable.streakDays]
                )
            }
    }

    suspend fun getClassLeaderboard(schoolId: UUID, className: String, limit: Int = 50): List<LeaderboardEntryDto> = dbQuery {
        // Class leaderboard would need a join with students table for class info
        // For now, return school-level leaderboard filtered by class if available
        GameStudentStatsTable.selectAll()
            .where { GameStudentStatsTable.schoolId eq schoolId }
            .orderBy(GameStudentStatsTable.totalXp, SortOrder.DESC)
            .limit(limit)
            .mapIndexed { index, row ->
                LeaderboardEntryDto(
                    rank = index + 1,
                    studentId = row[GameStudentStatsTable.studentId].toString(),
                    totalXp = row[GameStudentStatsTable.totalXp],
                    currentLevel = row[GameStudentStatsTable.currentLevel],
                    levelTitle = GameLevelDefinitionsTableFromStats(row[GameStudentStatsTable.currentLevel]),
                    streakDays = row[GameStudentStatsTable.streakDays]
                )
            }
    }

    suspend fun getStudentRank(schoolId: UUID, studentId: UUID): Int = dbQuery {
        val studentXp = GameStudentStatsTable.selectAll()
            .where { GameStudentStatsTable.studentId eq studentId }
            .firstOrNull()?.get(GameStudentStatsTable.totalXp) ?: 0

        GameStudentStatsTable.selectAll()
            .where {
                (GameStudentStatsTable.schoolId eq schoolId) and
                (GameStudentStatsTable.totalXp greater studentXp)
            }
            .count().toInt() + 1
    }

    private fun GameLevelDefinitionsTableFromStats(level: Int): String {
        return when (level) {
            1 -> "Beginner"
            2 -> "Learner"
            3 -> "Achiever"
            4 -> "Scholar"
            5 -> "Expert"
            6 -> "Master"
            7 -> "Champion"
            8 -> "Legend"
            else -> "Legend"
        }
    }
}

// ══════════════════════════════════════════════════════════════════════
// SeasonalEventService
// ══════════════════════════════════════════════════════════════════════

object SeasonalEventService {

    private val logger = LoggerFactory.getLogger("SeasonalEventService")

    suspend fun getActiveEvents(): List<SeasonalEventDto> = dbQuery {
        val today = LocalDate.now()
        GameSeasonalEventsTable.selectAll()
            .where { GameSeasonalEventsTable.isActive eq true }
            .map {
                SeasonalEventDto(
                    id = it[GameSeasonalEventsTable.id].value.toString(),
                    code = it[GameSeasonalEventsTable.code],
                    name = it[GameSeasonalEventsTable.name],
                    startDate = it[GameSeasonalEventsTable.startDate].toString(),
                    endDate = it[GameSeasonalEventsTable.endDate].toString(),
                    isActive = it[GameSeasonalEventsTable.isActive]
                )
            }
            .filter {
                val start = LocalDate.parse(it.startDate)
                val end = LocalDate.parse(it.endDate)
                !today.isBefore(start) && !today.isAfter(end)
            }
    }

    suspend fun getAllEvents(): List<SeasonalEventDto> = dbQuery {
        GameSeasonalEventsTable.selectAll()
            .map {
                SeasonalEventDto(
                    id = it[GameSeasonalEventsTable.id].value.toString(),
                    code = it[GameSeasonalEventsTable.code],
                    name = it[GameSeasonalEventsTable.name],
                    startDate = it[GameSeasonalEventsTable.startDate].toString(),
                    endDate = it[GameSeasonalEventsTable.endDate].toString(),
                    isActive = it[GameSeasonalEventsTable.isActive]
                )
            }
    }
}
