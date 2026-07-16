package com.littlebridge.enrollplus.feature.gamification.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class StudentStats(
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
data class LevelDefinition(
    val level: Int,
    val xpRequired: Int,
    val title: String,
    val iconName: String
)

@Serializable
data class GamificationFlags(
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
data class BadgeDefinition(
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
data class StudentBadge(
    val badgeId: String,
    val badgeCode: String,
    val badgeName: String,
    val badgeIcon: String,
    val badgeCategory: String,
    val badgeRarity: String,
    val earnedAt: String
)

@Serializable
data class QuestDefinition(
    val id: String,
    val code: String,
    val name: String,
    val description: String,
    val xpReward: Int,
    val questType: String,
    val isActive: Boolean
)

@Serializable
data class StudentQuest(
    val id: String,
    val questCode: String,
    val questName: String,
    val progress: Int,
    val target: Int,
    val completed: Boolean,
    val xpReward: Int,
    val completedAt: String?
)

@Serializable
data class House(
    val id: String,
    val name: String,
    val iconName: String,
    val color: String,
    val totalPoints: Int,
    val memberCount: Int
)

@Serializable
data class Reward(
    val id: String,
    val name: String,
    val description: String,
    val xpCost: Int,
    val fulfillmentRole: String,
    val stockRemaining: Int?,
    val isActive: Boolean
)

@Serializable
data class RewardRedemption(
    val id: String,
    val rewardId: String,
    val rewardName: String,
    val xpSpent: Int,
    val status: String,
    val createdAt: String
)

@Serializable
data class LeaderboardEntry(
    val rank: Int,
    val studentId: String,
    val studentName: String = "Unknown",
    val totalXp: Int,
    val currentLevel: Int,
    val levelTitle: String,
    val streakDays: Int
)

@Serializable
data class LeaderboardResponse(
    val leaderboard: List<LeaderboardEntry>,
    val myRank: Int
)

@Serializable
data class SeasonalEvent(
    val id: String,
    val code: String,
    val name: String,
    val startDate: String,
    val endDate: String,
    val isActive: Boolean
)

@Serializable
data class EncourageRequest(
    val studentId: String,
    val amount: Int = 10,
    val reason: String = "Keep up the great work!",
    val encouragementType: String = "ENCOURAGE"
)

@Serializable
data class XpHistoryEntry(
    val id: String,
    val amount: Int,
    val reason: String,
    val source: String,
    val category: String,
    val multiplier: Float,
    val createdAt: String
)

@Serializable
data class XpBoost(
    val id: String,
    val boostType: String,
    val multiplier: Float,
    val targetScope: String,
    val endsAt: String
)

@Serializable
data class ClassGoal(
    val id: String,
    val className: String,
    val section: String,
    val goalType: String,
    val target: Int,
    val currentProgress: Int,
    val reward: String,
    val deadline: String
)
