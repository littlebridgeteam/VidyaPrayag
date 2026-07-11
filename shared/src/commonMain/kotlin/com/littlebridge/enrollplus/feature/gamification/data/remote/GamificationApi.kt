package com.littlebridge.enrollplus.feature.gamification.data.remote

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.network.safeApiCall
import com.littlebridge.enrollplus.feature.gamification.domain.model.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class GamificationApi(
    private val client: HttpClient,
    private val baseUrl: String,
) {
    private fun getUrl(path: String): String {
        val base = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        val cleanPath = if (path.startsWith("/")) path.substring(1) else path
        return "$base$cleanPath"
    }

    // ── Parent: Stats ─────────────────────────────────────────────────
    suspend fun getStats(token: String, childId: String): NetworkResult<ApiResponse<StudentStats>> = safeApiCall {
        client.get(getUrl("api/v1/parent/gamification/$childId/stats")) {
            bearerAuth(token)
        }
    }

    // ── Parent: Badges ────────────────────────────────────────────────
    suspend fun getBadges(token: String, childId: String): NetworkResult<ApiResponse<List<StudentBadge>>> = safeApiCall {
        client.get(getUrl("api/v1/parent/gamification/$childId/badges")) {
            bearerAuth(token)
        }
    }

    // ── Parent: Quests ────────────────────────────────────────────────
    suspend fun getQuests(token: String, childId: String): NetworkResult<ApiResponse<List<StudentQuest>>> = safeApiCall {
        client.get(getUrl("api/v1/parent/gamification/$childId/quests")) {
            bearerAuth(token)
        }
    }

    // ── Parent: House ─────────────────────────────────────────────────
    suspend fun getHouse(token: String, childId: String): NetworkResult<ApiResponse<House>> = safeApiCall {
        client.get(getUrl("api/v1/parent/gamification/$childId/house")) {
            bearerAuth(token)
        }
    }

    // ── Parent: Rewards ───────────────────────────────────────────────
    suspend fun getRewards(token: String, childId: String): NetworkResult<ApiResponse<List<Reward>>> = safeApiCall {
        client.get(getUrl("api/v1/parent/gamification/$childId/rewards")) {
            bearerAuth(token)
        }
    }

    suspend fun redeemReward(token: String, childId: String, rewardId: String): NetworkResult<ApiResponse<RewardRedemption>> = safeApiCall {
        client.post(getUrl("api/v1/parent/gamification/$childId/rewards/$rewardId/redeem")) {
            bearerAuth(token)
        }
    }

    suspend fun getRedemptions(token: String, childId: String): NetworkResult<ApiResponse<List<RewardRedemption>>> = safeApiCall {
        client.get(getUrl("api/v1/parent/gamification/$childId/redemptions")) {
            bearerAuth(token)
        }
    }

    // ── Parent: Leaderboard ───────────────────────────────────────────
    suspend fun getLeaderboard(token: String, childId: String): NetworkResult<ApiResponse<LeaderboardResponse>> = safeApiCall {
        client.get(getUrl("api/v1/parent/gamification/$childId/leaderboard")) {
            bearerAuth(token)
        }
    }

    // ── Parent: Events ────────────────────────────────────────────────
    suspend fun getActiveEvents(token: String): NetworkResult<ApiResponse<List<SeasonalEvent>>> = safeApiCall {
        client.get(getUrl("api/v1/parent/gamification/events")) {
            bearerAuth(token)
        }
    }

    // ── Parent: XP History ───────────────────────────────────────────
    suspend fun getXpHistory(token: String, childId: String): NetworkResult<ApiResponse<List<Map<String, *>>>> = safeApiCall {
        client.get(getUrl("api/v1/parent/gamification/$childId/xp-history")) {
            bearerAuth(token)
        }
    }

    // ── Parent: Active Boosts ────────────────────────────────────────
    suspend fun getActiveBoosts(token: String, childId: String): NetworkResult<ApiResponse<List<Map<String, *>>>> = safeApiCall {
        client.get(getUrl("api/v1/parent/gamification/$childId/boosts")) {
            bearerAuth(token)
        }
    }

    // ── Parent: Class Goals ──────────────────────────────────────────
    suspend fun getClassGoalsForChild(token: String, childId: String): NetworkResult<ApiResponse<List<Map<String, *>>>> = safeApiCall {
        client.get(getUrl("api/v1/parent/gamification/$childId/class-goals")) {
            bearerAuth(token)
        }
    }

    // ── Teacher: Encourage ────────────────────────────────────────────
    suspend fun encourageStudent(token: String, request: EncourageRequest): NetworkResult<ApiResponse<Map<String, *>>> = safeApiCall {
        client.post(getUrl("api/v1/teacher/gamification/encourage")) {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    // ── Teacher: Award Badge ──────────────────────────────────────────
    suspend fun awardBadge(token: String, studentId: String, badgeId: String): NetworkResult<ApiResponse<StudentBadge>> = safeApiCall {
        client.post(getUrl("api/v1/teacher/gamification/badge/award")) {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(mapOf("studentId" to studentId, "badgeId" to badgeId))
        }
    }

    // ── Teacher Tools: Class leaderboard ──────────────────────────────
    suspend fun getClassLeaderboard(token: String, limit: Int = 50): NetworkResult<ApiResponse<List<LeaderboardEntry>>> = safeApiCall {
        client.get(getUrl("api/v1/teacher/gamification/class/leaderboard")) {
            bearerAuth(token)
            parameter("limit", limit)
        }
    }

    // ── Teacher Tools: Student badges ─────────────────────────────────
    suspend fun getStudentBadges(token: String, studentId: String): NetworkResult<ApiResponse<List<StudentBadge>>> = safeApiCall {
        client.get(getUrl("api/v1/teacher/gamification/student/$studentId/badges")) {
            bearerAuth(token)
        }
    }

    // ── Teacher Tools: Send shoutout ──────────────────────────────────
    suspend fun sendShoutout(token: String, receiverId: String, message: String, templateId: Int = 0, isPublic: Boolean = true): NetworkResult<ApiResponse<Map<String, *>>> = safeApiCall {
        client.post(getUrl("api/v1/teacher/gamification/shoutout")) {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(mapOf("receiverId" to receiverId, "message" to message, "templateId" to templateId, "isPublic" to isPublic))
        }
    }

    // ── Teacher Tools: Class goals ────────────────────────────────────
    suspend fun getClassGoals(token: String): NetworkResult<ApiResponse<List<Map<String, *>>>> = safeApiCall {
        client.get(getUrl("api/v1/teacher/gamification/class-goals")) {
            bearerAuth(token)
        }
    }

    suspend fun createClassGoal(token: String, request: Map<String, *>): NetworkResult<ApiResponse<Map<String, *>>> = safeApiCall {
        client.post(getUrl("api/v1/teacher/gamification/class-goals")) {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    // ── Teacher Tools: Quest assignment ───────────────────────────────
    suspend fun assignQuest(token: String, studentId: String, questId: String): NetworkResult<ApiResponse<Map<String, *>>> = safeApiCall {
        client.post(getUrl("api/v1/teacher/gamification/quest/assign")) {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(mapOf("studentId" to studentId, "questId" to questId))
        }
    }

    suspend fun getTeacherQuests(token: String): NetworkResult<ApiResponse<List<QuestDefinition>>> = safeApiCall {
        client.get(getUrl("api/v1/teacher/gamification/quests")) {
            bearerAuth(token)
        }
    }

    // ── Teacher Tools: Spotlight ─────────────────────────────────────
    suspend fun spotlightStudent(token: String, studentId: String, reason: String = "Spotlight award for improvement"): NetworkResult<ApiResponse<Map<String, *>>> = safeApiCall {
        client.post(getUrl("api/v1/teacher/gamification/spotlight")) {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(mapOf("studentId" to studentId, "reason" to reason))
        }
    }

    // ── Teacher Tools: Pep Talk ──────────────────────────────────────
    suspend fun pepTalk(token: String, className: String, section: String? = null): NetworkResult<ApiResponse<Map<String, *>>> = safeApiCall {
        client.post(getUrl("api/v1/teacher/gamification/pep-talk")) {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(mapOf("className" to className, "section" to (section ?: "")))
        }
    }

    // ── Teacher Tools: Shoutout Moderation ───────────────────────────
    suspend fun getShoutouts(token: String): NetworkResult<ApiResponse<List<Map<String, *>>>> = safeApiCall {
        client.get(getUrl("api/v1/teacher/gamification/shoutouts")) {
            bearerAuth(token)
        }
    }

    suspend fun deleteShoutout(token: String, shoutoutId: String): NetworkResult<ApiResponse<Map<String, *>>> = safeApiCall {
        client.delete(getUrl("api/v1/teacher/gamification/shoutouts/$shoutoutId")) {
            bearerAuth(token)
        }
    }

    // ── Teacher Tools: Gamification Overview ─────────────────────────
    suspend fun getGamificationOverview(token: String): NetworkResult<ApiResponse<Map<String, *>>> = safeApiCall {
        client.get(getUrl("api/v1/teacher/gamification/overview")) {
            bearerAuth(token)
        }
    }

    // ── Teacher Tools: Update Class Goal Progress ────────────────────
    suspend fun updateClassGoalProgress(token: String, goalId: String, progress: Int): NetworkResult<ApiResponse<Map<String, *>>> = safeApiCall {
        client.put(getUrl("api/v1/teacher/gamification/class-goals/$goalId/progress?progress=$progress")) {
            bearerAuth(token)
        }
    }

    // ── Teacher Tools: Parent Alert ──────────────────────────────────
    suspend fun sendParentAlert(token: String, studentId: String, message: String): NetworkResult<ApiResponse<Map<String, *>>> = safeApiCall {
        client.post(getUrl("api/v1/teacher/gamification/parent-alert")) {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(mapOf("studentId" to studentId, "message" to message))
        }
    }

    // ── Teacher Tools: Mentor Assignment ─────────────────────────────
    suspend fun assignMentor(token: String, mentorId: String, menteeId: String): NetworkResult<ApiResponse<Map<String, *>>> = safeApiCall {
        client.post(getUrl("api/v1/teacher/gamification/mentor/assign")) {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(mapOf("mentorId" to mentorId, "menteeId" to menteeId))
        }
    }

    suspend fun unassignMentor(token: String, assignmentId: String): NetworkResult<ApiResponse<Map<String, *>>> = safeApiCall {
        client.delete(getUrl("api/v1/teacher/gamification/mentor/$assignmentId")) {
            bearerAuth(token)
        }
    }

    suspend fun getMentorAssignments(token: String): NetworkResult<ApiResponse<List<Map<String, *>>>> = safeApiCall {
        client.get(getUrl("api/v1/teacher/gamification/mentors")) {
            bearerAuth(token)
        }
    }

    // ── Teacher Tools: Study Buddy Assignment ────────────────────────
    suspend fun assignStudyBuddy(token: String, student1Id: String, student2Id: String, classId: String? = null): NetworkResult<ApiResponse<Map<String, *>>> = safeApiCall {
        client.post(getUrl("api/v1/teacher/gamification/study-buddy/assign")) {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(mapOf("student1Id" to student1Id, "student2Id" to student2Id, "classId" to (classId ?: "")))
        }
    }

    suspend fun unassignStudyBuddy(token: String, pairId: String): NetworkResult<ApiResponse<Map<String, *>>> = safeApiCall {
        client.delete(getUrl("api/v1/teacher/gamification/study-buddy/$pairId")) {
            bearerAuth(token)
        }
    }

    suspend fun getStudyBuddyPairs(token: String): NetworkResult<ApiResponse<List<Map<String, *>>>> = safeApiCall {
        client.get(getUrl("api/v1/teacher/gamification/study-buddies")) {
            bearerAuth(token)
        }
    }

    // ── Admin: Flags ──────────────────────────────────────────────────
    suspend fun getFlags(token: String): NetworkResult<ApiResponse<GamificationFlags>> = safeApiCall {
        client.get(getUrl("api/v1/admin/gamification/flags")) {
            bearerAuth(token)
        }
    }

    suspend fun setEnabled(token: String, enabled: Boolean): NetworkResult<ApiResponse<Map<String, *>>> = safeApiCall {
        client.put(getUrl("api/v1/admin/gamification/flags")) {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(mapOf("isGamificationEnabled" to enabled))
        }
    }

    // ── Admin: Badge Definitions ──────────────────────────────────────
    suspend fun getBadgeDefinitions(token: String): NetworkResult<ApiResponse<List<BadgeDefinition>>> = safeApiCall {
        client.get(getUrl("api/v1/admin/gamification/badges")) {
            bearerAuth(token)
        }
    }

    // ── Admin: Level Definitions ──────────────────────────────────────
    suspend fun getLevelDefinitions(token: String): NetworkResult<ApiResponse<List<LevelDefinition>>> = safeApiCall {
        client.get(getUrl("api/v1/admin/gamification/levels")) {
            bearerAuth(token)
        }
    }

    // ── Admin: Houses ─────────────────────────────────────────────────
    suspend fun getHouses(token: String): NetworkResult<ApiResponse<List<House>>> = safeApiCall {
        client.get(getUrl("api/v1/admin/gamification/houses")) {
            bearerAuth(token)
        }
    }

    // ── Admin: Rewards ────────────────────────────────────────────────
    suspend fun getAdminRewards(token: String): NetworkResult<ApiResponse<List<Reward>>> = safeApiCall {
        client.get(getUrl("api/v1/admin/gamification/rewards")) {
            bearerAuth(token)
        }
    }

    // ── Admin: Quests ─────────────────────────────────────────────────
    suspend fun getAdminQuests(token: String): NetworkResult<ApiResponse<List<QuestDefinition>>> = safeApiCall {
        client.get(getUrl("api/v1/admin/gamification/quests")) {
            bearerAuth(token)
        }
    }

    // ── Admin: Events ─────────────────────────────────────────────────
    suspend fun getAdminEvents(token: String): NetworkResult<ApiResponse<List<SeasonalEvent>>> = safeApiCall {
        client.get(getUrl("api/v1/admin/gamification/events")) {
            bearerAuth(token)
        }
    }

    // ── Admin: Leaderboard ────────────────────────────────────────────
    suspend fun getAdminLeaderboard(token: String): NetworkResult<ApiResponse<List<LeaderboardEntry>>> = safeApiCall {
        client.get(getUrl("api/v1/admin/gamification/leaderboard")) {
            bearerAuth(token)
        }
    }

    // ── Admin: Redemptions ───────────────────────────────────────────
    suspend fun getAdminRedemptions(token: String): NetworkResult<ApiResponse<List<Map<String, *>>>> = safeApiCall {
        client.get(getUrl("api/v1/admin/gamification/redemptions")) {
            bearerAuth(token)
        }
    }

    suspend fun updateRedemptionStatus(token: String, redemptionId: String, status: String): NetworkResult<ApiResponse<Map<String, *>>> = safeApiCall {
        client.put(getUrl("api/v1/admin/gamification/redemptions/status")) {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(mapOf("redemptionId" to redemptionId, "status" to status))
        }
    }

    // ── Admin: Boosts ────────────────────────────────────────────────
    suspend fun getAdminBoosts(token: String): NetworkResult<ApiResponse<List<Map<String, *>>>> = safeApiCall {
        client.get(getUrl("api/v1/admin/gamification/boosts")) {
            bearerAuth(token)
        }
    }

    suspend fun createBoost(token: String, boostType: String, multiplier: Float, targetScope: String = "ALL", targetId: String? = null, durationHours: Int = 24): NetworkResult<ApiResponse<Map<String, *>>> = safeApiCall {
        client.post(getUrl("api/v1/admin/gamification/boosts")) {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(mapOf("boostType" to boostType, "multiplier" to multiplier, "targetScope" to targetScope, "targetId" to targetId, "durationHours" to durationHours))
        }
    }

    // ── Admin: Analytics ─────────────────────────────────────────────
    suspend fun getAnalytics(token: String): NetworkResult<ApiResponse<Map<String, *>>> = safeApiCall {
        client.get(getUrl("api/v1/admin/gamification/analytics")) {
            bearerAuth(token)
        }
    }
}
