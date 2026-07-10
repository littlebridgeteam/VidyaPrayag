package com.littlebridge.enrollplus.feature.gamification.data.repository

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.gamification.data.remote.GamificationApi
import com.littlebridge.enrollplus.feature.gamification.domain.model.*
import com.littlebridge.enrollplus.feature.gamification.domain.repository.GamificationRepository

class GamificationRepositoryImpl(
    private val api: GamificationApi,
) : GamificationRepository {

    override suspend fun getStats(token: String, childId: String) = api.getStats(token, childId)
    override suspend fun getBadges(token: String, childId: String) = api.getBadges(token, childId)
    override suspend fun getQuests(token: String, childId: String) = api.getQuests(token, childId)
    override suspend fun getHouse(token: String, childId: String) = api.getHouse(token, childId)
    override suspend fun getRewards(token: String, childId: String) = api.getRewards(token, childId)
    override suspend fun redeemReward(token: String, childId: String, rewardId: String) = api.redeemReward(token, childId, rewardId)
    override suspend fun getRedemptions(token: String, childId: String) = api.getRedemptions(token, childId)
    override suspend fun getLeaderboard(token: String, childId: String) = api.getLeaderboard(token, childId)
    override suspend fun getActiveEvents(token: String) = api.getActiveEvents(token)
    override suspend fun getXpHistory(token: String, childId: String) = api.getXpHistory(token, childId)
    override suspend fun getActiveBoosts(token: String, childId: String) = api.getActiveBoosts(token, childId)
    override suspend fun getClassGoalsForChild(token: String, childId: String) = api.getClassGoalsForChild(token, childId)

    override suspend fun encourageStudent(token: String, request: EncourageRequest) = api.encourageStudent(token, request)
    override suspend fun awardBadge(token: String, studentId: String, badgeId: String) = api.awardBadge(token, studentId, badgeId)
    override suspend fun getClassLeaderboard(token: String, limit: Int) = api.getClassLeaderboard(token, limit)
    override suspend fun getStudentBadges(token: String, studentId: String) = api.getStudentBadges(token, studentId)
    override suspend fun sendShoutout(token: String, receiverId: String, message: String, templateId: Int, isPublic: Boolean) = api.sendShoutout(token, receiverId, message, templateId, isPublic)
    override suspend fun getClassGoals(token: String) = api.getClassGoals(token)
    override suspend fun createClassGoal(token: String, request: Map<String, *>) = api.createClassGoal(token, request)
    override suspend fun assignQuest(token: String, studentId: String, questId: String) = api.assignQuest(token, studentId, questId)
    override suspend fun getTeacherQuests(token: String) = api.getTeacherQuests(token)
    override suspend fun spotlightStudent(token: String, studentId: String, reason: String) = api.spotlightStudent(token, studentId, reason)
    override suspend fun pepTalk(token: String, className: String, section: String?) = api.pepTalk(token, className, section)
    override suspend fun getShoutouts(token: String) = api.getShoutouts(token)
    override suspend fun deleteShoutout(token: String, shoutoutId: String) = api.deleteShoutout(token, shoutoutId)
    override suspend fun getGamificationOverview(token: String) = api.getGamificationOverview(token)
    override suspend fun updateClassGoalProgress(token: String, goalId: String, progress: Int) = api.updateClassGoalProgress(token, goalId, progress)

    override suspend fun getFlags(token: String) = api.getFlags(token)
    override suspend fun setEnabled(token: String, enabled: Boolean) = api.setEnabled(token, enabled)
    override suspend fun getBadgeDefinitions(token: String) = api.getBadgeDefinitions(token)
    override suspend fun getLevelDefinitions(token: String) = api.getLevelDefinitions(token)
    override suspend fun getHouses(token: String) = api.getHouses(token)
    override suspend fun getAdminRewards(token: String) = api.getAdminRewards(token)
    override suspend fun getAdminQuests(token: String) = api.getAdminQuests(token)
    override suspend fun getAdminEvents(token: String) = api.getAdminEvents(token)
    override suspend fun getAdminLeaderboard(token: String) = api.getAdminLeaderboard(token)
    override suspend fun getAdminRedemptions(token: String) = api.getAdminRedemptions(token)
    override suspend fun updateRedemptionStatus(token: String, redemptionId: String, status: String) = api.updateRedemptionStatus(token, redemptionId, status)
    override suspend fun getAdminBoosts(token: String) = api.getAdminBoosts(token)
    override suspend fun createBoost(token: String, boostType: String, multiplier: Float, targetScope: String, targetId: String?, durationHours: Int) = api.createBoost(token, boostType, multiplier, targetScope, targetId, durationHours)
    override suspend fun getAnalytics(token: String) = api.getAnalytics(token)
}
