package com.littlebridge.enrollplus.feature.gamification.domain.repository

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.gamification.domain.model.*

interface GamificationRepository {

    // Parent
    suspend fun getStats(token: String, childId: String): NetworkResult<ApiResponse<StudentStats>>
    suspend fun getBadges(token: String, childId: String): NetworkResult<ApiResponse<List<StudentBadge>>>
    suspend fun getQuests(token: String, childId: String): NetworkResult<ApiResponse<List<StudentQuest>>>
    suspend fun getHouse(token: String, childId: String): NetworkResult<ApiResponse<House>>
    suspend fun getRewards(token: String, childId: String): NetworkResult<ApiResponse<List<Reward>>>
    suspend fun redeemReward(token: String, childId: String, rewardId: String): NetworkResult<ApiResponse<RewardRedemption>>
    suspend fun getRedemptions(token: String, childId: String): NetworkResult<ApiResponse<List<RewardRedemption>>>
    suspend fun getLeaderboard(token: String, childId: String): NetworkResult<ApiResponse<LeaderboardResponse>>
    suspend fun getActiveEvents(token: String): NetworkResult<ApiResponse<List<SeasonalEvent>>>
    suspend fun getXpHistory(token: String, childId: String): NetworkResult<ApiResponse<List<XpHistoryEntry>>>
    suspend fun getActiveBoosts(token: String, childId: String): NetworkResult<ApiResponse<List<XpBoost>>>
    suspend fun getClassGoalsForChild(token: String, childId: String): NetworkResult<ApiResponse<List<ClassGoal>>>

    // Teacher
    suspend fun encourageStudent(token: String, request: EncourageRequest): NetworkResult<ApiResponse<Map<String, *>>>
    suspend fun awardBadge(token: String, studentId: String, badgeId: String): NetworkResult<ApiResponse<StudentBadge>>
    suspend fun getClassLeaderboard(token: String, limit: Int = 50, className: String? = null): NetworkResult<ApiResponse<List<LeaderboardEntry>>>
    suspend fun getStudentBadges(token: String, studentId: String): NetworkResult<ApiResponse<List<StudentBadge>>>
    suspend fun sendShoutout(token: String, receiverId: String, message: String, templateId: Int = 0, isPublic: Boolean = true): NetworkResult<ApiResponse<Map<String, *>>>
    suspend fun getClassGoals(token: String): NetworkResult<ApiResponse<List<Map<String, *>>>>
    suspend fun createClassGoal(token: String, request: Map<String, *>): NetworkResult<ApiResponse<Map<String, *>>>
    suspend fun assignQuest(token: String, studentId: String, questId: String): NetworkResult<ApiResponse<Map<String, *>>>
    suspend fun getTeacherQuests(token: String): NetworkResult<ApiResponse<List<QuestDefinition>>>
    suspend fun spotlightStudent(token: String, studentId: String, reason: String = "Spotlight award for improvement"): NetworkResult<ApiResponse<Map<String, *>>>
    suspend fun pepTalk(token: String, className: String, section: String? = null): NetworkResult<ApiResponse<Map<String, *>>>
    suspend fun getShoutouts(token: String): NetworkResult<ApiResponse<List<Map<String, *>>>>
    suspend fun deleteShoutout(token: String, shoutoutId: String): NetworkResult<ApiResponse<Map<String, *>>>
    suspend fun getGamificationOverview(token: String): NetworkResult<ApiResponse<Map<String, *>>>
    suspend fun updateClassGoalProgress(token: String, goalId: String, progress: Int): NetworkResult<ApiResponse<Map<String, *>>>

    // Teacher: Parent Alert
    suspend fun sendParentAlert(token: String, studentId: String, message: String): NetworkResult<ApiResponse<Map<String, *>>>

    // Teacher: Mentor Assignment
    suspend fun assignMentor(token: String, mentorId: String, menteeId: String): NetworkResult<ApiResponse<Map<String, *>>>
    suspend fun unassignMentor(token: String, assignmentId: String): NetworkResult<ApiResponse<Map<String, *>>>
    suspend fun getMentorAssignments(token: String): NetworkResult<ApiResponse<List<Map<String, *>>>>

    // Teacher: Study Buddy Assignment
    suspend fun assignStudyBuddy(token: String, student1Id: String, student2Id: String, classId: String? = null): NetworkResult<ApiResponse<Map<String, *>>>
    suspend fun unassignStudyBuddy(token: String, pairId: String): NetworkResult<ApiResponse<Map<String, *>>>
    suspend fun getStudyBuddyPairs(token: String): NetworkResult<ApiResponse<List<Map<String, *>>>>

    // Admin
    suspend fun getFlags(token: String): NetworkResult<ApiResponse<GamificationFlags>>
    suspend fun setEnabled(token: String, enabled: Boolean): NetworkResult<ApiResponse<Map<String, *>>>
    suspend fun setGranularFlag(token: String, flagKey: String, enabled: Boolean): NetworkResult<ApiResponse<Map<String, *>>>
    suspend fun getBadgeDefinitions(token: String): NetworkResult<ApiResponse<List<BadgeDefinition>>>
    suspend fun getLevelDefinitions(token: String): NetworkResult<ApiResponse<List<LevelDefinition>>>
    suspend fun getHouses(token: String): NetworkResult<ApiResponse<List<House>>>
    suspend fun getAdminRewards(token: String): NetworkResult<ApiResponse<List<Reward>>>
    suspend fun getAdminQuests(token: String): NetworkResult<ApiResponse<List<QuestDefinition>>>
    suspend fun getAdminEvents(token: String): NetworkResult<ApiResponse<List<SeasonalEvent>>>
    suspend fun getAdminLeaderboard(token: String): NetworkResult<ApiResponse<List<LeaderboardEntry>>>
    suspend fun getAdminRedemptions(token: String): NetworkResult<ApiResponse<List<Map<String, *>>>>
    suspend fun updateRedemptionStatus(token: String, redemptionId: String, status: String): NetworkResult<ApiResponse<Map<String, *>>>
    suspend fun getAdminBoosts(token: String): NetworkResult<ApiResponse<List<Map<String, *>>>>
    suspend fun createBoost(token: String, boostType: String, multiplier: Float, targetScope: String = "ALL", targetId: String? = null, durationHours: Int = 24): NetworkResult<ApiResponse<Map<String, *>>>
    suspend fun getAnalytics(token: String): NetworkResult<ApiResponse<Map<String, *>>>
}
