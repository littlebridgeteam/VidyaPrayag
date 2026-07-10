/*
 * File: GamificationRouting.kt
 * Module: feature/gamification
 *
 * All gamification API endpoints. Routes are JWT-authenticated and scoped
 * by role:
 *
 *   Parent:
 *     GET  /api/v1/parent/gamification/{childId}/stats          — child's stats
 *     GET  /api/v1/parent/gamification/{childId}/badges         — child's badges
 *     GET  /api/v1/parent/gamification/{childId}/levels         — level definitions
 *
 *   Teacher:
 *     POST /api/v1/teacher/gamification/encourage               — award XP to student
 *     POST /api/v1/teacher/gamification/badge/award             — manually award badge
 *     GET  /api/v1/teacher/gamification/badges                  — all badge definitions
 *     GET  /api/v1/teacher/gamification/student/{id}/stats      — student stats
 *
 *   Admin:
 *     GET  /api/v1/admin/gamification/flags                     — get kill switch state
 *     PUT  /api/v1/admin/gamification/flags                     — set kill switch state
 *     GET  /api/v1/admin/gamification/badges                    — all badge definitions
 *     GET  /api/v1/admin/gamification/levels                    — level definitions
 *
 * Spec ref: GAMIFICATION_SYSTEM_SPEC.md §27
 */
package com.littlebridge.enrollplus.feature.gamification

import com.littlebridge.enrollplus.core.fail
import com.littlebridge.enrollplus.core.ok
import com.littlebridge.enrollplus.core.okMessage
import com.littlebridge.enrollplus.core.principalUserUuid
import com.littlebridge.enrollplus.core.requireSchoolAdmin
import com.littlebridge.enrollplus.core.requireSchoolContext
import com.littlebridge.enrollplus.core.requireSchoolOrTeacherContext
import com.littlebridge.enrollplus.db.ChildrenTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.GameClassGoalsTable
import com.littlebridge.enrollplus.db.GameShoutoutsTable
import com.littlebridge.enrollplus.db.GameStudentStatsTable
import org.jetbrains.exposed.sql.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class EncourageRequest(
    val studentId: String,
    val amount: Int = 10,
    val reason: String = "Keep up the great work!",
    val encouragementType: String = "ENCOURAGE" // ENCOURAGE | SPOTLIGHT | PEP_TALK
)

@Serializable
data class AwardBadgeRequest(
    val studentId: String,
    val badgeId: String
)

@Serializable
data class SetGamificationFlagRequest(
    val isGamificationEnabled: Boolean
)

@Serializable
data class ShoutoutRequest(
    val receiverId: String,
    val message: String,
    val templateId: Int = 0,
    val isPublic: Boolean = true
)

@Serializable
data class ClassGoalRequest(
    val className: String,
    val section: String? = null,
    val goalType: String,
    val target: Int,
    val reward: String,
    val deadline: String? = null
)

@Serializable
data class AssignQuestRequest(
    val studentId: String,
    val questId: String
)

fun Route.gamificationRouting() {
    authenticate("jwt") {

        // ── Parent: View child gamification ──────────────────────────────
        route("/api/v1/parent/gamification") {

            get("/{childId}/stats") {
                val uid = call.principalUserUuid() ?: run {
                    call.fail("Invalid token", HttpStatusCode.Unauthorized, "UNAUTHORIZED"); return@get
                }
                val childId = call.parameters["childId"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("Invalid child id"); return@get }

                // Verify child belongs to parent
                val owns = dbQuery {
                    ChildrenTable.selectAll()
                        .where { (ChildrenTable.id eq childId) and (ChildrenTable.parentId eq uid) }
                        .firstOrNull()
                }
                if (owns == null) {
                    call.fail("Child not found", HttpStatusCode.NotFound, "CHILD_NOT_FOUND"); return@get
                }

                val stats = GamificationService.getStudentStats(childId)
                if (stats != null) call.ok(stats, "Student gamification stats")
                else call.ok(StudentStatsDto(childId.toString(), 0, 0, 1, "Beginner", 0), "Student gamification stats (new)")
            }

            get("/{childId}/badges") {
                val uid = call.principalUserUuid() ?: run {
                    call.fail("Invalid token", HttpStatusCode.Unauthorized, "UNAUTHORIZED"); return@get
                }
                val childId = call.parameters["childId"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("Invalid child id"); return@get }

                val owns = dbQuery {
                    ChildrenTable.selectAll()
                        .where { (ChildrenTable.id eq childId) and (ChildrenTable.parentId eq uid) }
                        .firstOrNull()
                }
                if (owns == null) {
                    call.fail("Child not found", HttpStatusCode.NotFound, "CHILD_NOT_FOUND"); return@get
                }

                val badges = BadgeCriteriaEvaluator.getStudentBadges(childId)
                call.ok(badges, "Student badges (${badges.size})")
            }

            get("/{childId}/levels") {
                val levels = GamificationService.getLevelDefinitions()
                call.ok(levels, "Level definitions (${levels.size})")
            }
        }

        // ── Teacher: Encourage + Award badges ────────────────────────────
        route("/api/v1/teacher/gamification") {

            post("/encourage") {
                val ctx = call.requireSchoolOrTeacherContext() ?: return@post
                val req = runCatching { call.receive<EncourageRequest>() }.getOrNull()
                    ?: run { call.fail("Invalid request body"); return@post }

                val studentId = runCatching { UUID.fromString(req.studentId) }.getOrNull()
                    ?: run { call.fail("Invalid student id"); return@post }

                val result = GamificationService.awardXp(
                    studentId = studentId,
                    schoolId = ctx.schoolId,
                    amount = req.amount,
                    reason = req.reason,
                    source = "teacher_encourage",
                    category = "CHARACTER"
                )

                // Also evaluate badges after XP award
                val newBadges = BadgeCriteriaEvaluator.evaluateBadges(studentId, ctx.schoolId)

                call.ok(
                    mapOf("xpResult" to result, "newBadges" to newBadges),
                    "Encouragement sent: +${result.xpAwarded} XP"
                )
            }

            post("/badge/award") {
                val ctx = call.requireSchoolOrTeacherContext() ?: return@post
                val req = runCatching { call.receive<AwardBadgeRequest>() }.getOrNull()
                    ?: run { call.fail("Invalid request body"); return@post }

                val studentId = runCatching { UUID.fromString(req.studentId) }.getOrNull()
                    ?: run { call.fail("Invalid student id"); return@post }
                val badgeId = runCatching { UUID.fromString(req.badgeId) }.getOrNull()
                    ?: run { call.fail("Invalid badge id"); return@post }

                val awarded = BadgeCriteriaEvaluator.manuallyAwardBadge(studentId, badgeId, ctx.userId)
                if (awarded) call.okMessage("Badge awarded")
                else call.fail("Badge already earned or invalid", HttpStatusCode.BadRequest, "BADGE_ALREADY_EARNED")
            }

            get("/badges") {
                val badges = BadgeCriteriaEvaluator.getAllBadgeDefinitions()
                call.ok(badges, "Badge definitions (${badges.size})")
            }

            get("/student/{id}/stats") {
                val ctx = call.requireSchoolOrTeacherContext() ?: return@get
                val studentId = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("Invalid student id"); return@get }

                val stats = GamificationService.getStudentStats(studentId)
                if (stats != null) call.ok(stats, "Student stats")
                else call.ok(StudentStatsDto(studentId.toString(), 0, 0, 1, "Beginner", 0), "Student stats (new)")
            }

            // ── Teacher Tools: Class leaderboard ──────────────────────────
            get("/class/leaderboard") {
                val ctx = call.requireSchoolOrTeacherContext() ?: return@get
                val limit = call.request.queryParameters["limit"]?.toIntOrNull()?.coerceIn(1, 100) ?: 50
                val leaderboard = LeaderboardService.getSchoolLeaderboard(ctx.schoolId, limit)
                call.ok(leaderboard, "Class leaderboard (${leaderboard.size})")
            }

            // ── Teacher Tools: Student badges ─────────────────────────────
            get("/student/{id}/badges") {
                val ctx = call.requireSchoolOrTeacherContext() ?: return@get
                val studentId = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("Invalid student id"); return@get }

                val badges = BadgeCriteriaEvaluator.getStudentBadges(studentId)
                call.ok(badges, "Student badges (${badges.size})")
            }

            // ── Teacher Tools: Send shoutout ──────────────────────────────
            post("/shoutout") {
                val ctx = call.requireSchoolOrTeacherContext() ?: return@post
                val req = runCatching { call.receive<ShoutoutRequest>() }.getOrNull()
                    ?: run { call.fail("Invalid request body"); return@post }

                val receiverId = runCatching { UUID.fromString(req.receiverId) }.getOrNull()
                    ?: run { call.fail("Invalid receiver id"); return@post }

                dbQuery {
                    GameShoutoutsTable.insert {
                        it[GameShoutoutsTable.senderId] = ctx.userId
                        it[GameShoutoutsTable.receiverId] = receiverId
                        it[GameShoutoutsTable.schoolId] = ctx.schoolId
                        it[GameShoutoutsTable.templateId] = req.templateId
                        it[GameShoutoutsTable.message] = req.message
                        it[GameShoutoutsTable.isPublic] = req.isPublic
                        it[GameShoutoutsTable.createdAt] = java.time.Instant.now()
                    }
                }
                call.okMessage("Shoutout sent!")
            }

            // ── Teacher Tools: Class goals ────────────────────────────────
            get("/class-goals") {
                val ctx = call.requireSchoolOrTeacherContext() ?: return@get
                val goals = dbQuery {
                    GameClassGoalsTable.selectAll()
                        .where { GameClassGoalsTable.schoolId eq ctx.schoolId }
                        .orderBy(GameClassGoalsTable.createdAt, SortOrder.DESC)
                        .map {
                            mapOf(
                                "id" to it[GameClassGoalsTable.id].value.toString(),
                                "className" to (it[GameClassGoalsTable.className] ?: ""),
                                "section" to (it[GameClassGoalsTable.section] ?: ""),
                                "goalType" to it[GameClassGoalsTable.goalType],
                                "target" to it[GameClassGoalsTable.target],
                                "currentProgress" to it[GameClassGoalsTable.currentProgress],
                                "reward" to it[GameClassGoalsTable.reward],
                                "completed" to it[GameClassGoalsTable.completed],
                                "deadline" to (it[GameClassGoalsTable.deadline]?.toString() ?: "")
                            )
                        }
                }
                call.ok(goals, "Class goals (${goals.size})")
            }

            post("/class-goals") {
                val ctx = call.requireSchoolOrTeacherContext() ?: return@post
                val req = runCatching { call.receive<ClassGoalRequest>() }.getOrNull()
                    ?: run { call.fail("Invalid request body"); return@post }

                val deadline = req.deadline?.let { runCatching { java.time.LocalDate.parse(it) }.getOrNull() }
                dbQuery {
                    GameClassGoalsTable.insert {
                        it[GameClassGoalsTable.schoolId] = ctx.schoolId
                        it[GameClassGoalsTable.className] = req.className
                        it[GameClassGoalsTable.section] = req.section
                        it[GameClassGoalsTable.goalType] = req.goalType
                        it[GameClassGoalsTable.target] = req.target
                        it[GameClassGoalsTable.currentProgress] = 0
                        it[GameClassGoalsTable.reward] = req.reward
                        it[GameClassGoalsTable.completed] = false
                        it[GameClassGoalsTable.deadline] = deadline
                        it[GameClassGoalsTable.createdBy] = ctx.userId
                        it[GameClassGoalsTable.createdAt] = java.time.Instant.now()
                    }
                }
                call.okMessage("Class goal created")
            }

            // ── Teacher Tools: Assign quest to student ────────────────────
            post("/quest/assign") {
                val ctx = call.requireSchoolOrTeacherContext() ?: return@post
                val req = runCatching { call.receive<AssignQuestRequest>() }.getOrNull()
                    ?: run { call.fail("Invalid request body"); return@post }

                val studentId = runCatching { UUID.fromString(req.studentId) }.getOrNull()
                    ?: run { call.fail("Invalid student id"); return@post }
                val questId = runCatching { UUID.fromString(req.questId) }.getOrNull()
                    ?: run { call.fail("Invalid quest id"); return@post }

                val assigned = QuestService.assignQuest(studentId, questId, ctx.schoolId)
                if (assigned) call.okMessage("Quest assigned to student")
                else call.fail("Quest already assigned or invalid", HttpStatusCode.BadRequest, "QUEST_ALREADY_ASSIGNED")
            }

            // ── Teacher Tools: Active quests list ─────────────────────────
            get("/quests") {
                val quests = QuestService.getActiveQuests()
                call.ok(quests, "Active quests (${quests.size})")
            }
        }

        // ── Admin: Kill switch + config ──────────────────────────────────
        route("/api/v1/admin/gamification") {

            get("/flags") {
                val ctx = call.requireSchoolAdmin() ?: return@get
                val flags = GamificationService.getGamificationFlags()
                call.ok(flags, "Gamification flags")
            }

            put("/flags") {
                val ctx = call.requireSchoolAdmin() ?: return@put
                val req = runCatching { call.receive<SetGamificationFlagRequest>() }.getOrNull()
                    ?: run { call.fail("Invalid request body"); return@put }

                val updated = GamificationService.setGamificationEnabled(req.isGamificationEnabled)
                if (updated) call.okMessage("Gamification ${if (req.isGamificationEnabled) "enabled" else "disabled"}")
                else call.fail("Failed to update gamification flag", HttpStatusCode.InternalServerError, "UPDATE_FAILED")
            }

            get("/badges") {
                val ctx = call.requireSchoolAdmin() ?: return@get
                val badges = BadgeCriteriaEvaluator.getAllBadgeDefinitions()
                call.ok(badges, "Badge definitions (${badges.size})")
            }

            get("/levels") {
                val ctx = call.requireSchoolAdmin() ?: return@get
                val levels = GamificationService.getLevelDefinitions()
                call.ok(levels, "Level definitions (${levels.size})")
            }
        }

        // ── Parent: Quests, Houses, Rewards, Leaderboard, Events ────────
        route("/api/v1/parent/gamification/{childId}") {

            get("/quests") {
                val uid = call.principalUserUuid() ?: run {
                    call.fail("Invalid token", HttpStatusCode.Unauthorized, "UNAUTHORIZED"); return@get
                }
                val childId = call.parameters["childId"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("Invalid child id"); return@get }

                val quests = QuestService.getStudentQuests(childId)
                call.ok(quests, "Student quests (${quests.size})")
            }

            get("/house") {
                val uid = call.principalUserUuid() ?: run {
                    call.fail("Invalid token", HttpStatusCode.Unauthorized, "UNAUTHORIZED"); return@get
                }
                val childId = call.parameters["childId"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("Invalid child id"); return@get }

                val house = HouseService.getStudentHouse(childId)
                if (house != null) call.ok(house, "Student house")
                else call.okMessage("No house assigned")
            }

            get("/rewards") {
                val uid = call.principalUserUuid() ?: run {
                    call.fail("Invalid token", HttpStatusCode.Unauthorized, "UNAUTHORIZED"); return@get
                }
                val childId = call.parameters["childId"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("Invalid child id"); return@get }

                val stats = GamificationService.getStudentStats(childId)
                val schoolId = stats?.let { UUID.fromString(it.studentId) } // not ideal but works for now
                // Get child's schoolId from ChildrenTable
                val childRow = dbQuery {
                    ChildrenTable.selectAll()
                        .where { ChildrenTable.id eq childId }
                        .firstOrNull()
                }
                val sid = childRow?.get(ChildrenTable.schoolId)
                if (sid == null) {
                    call.fail("Child school not found", HttpStatusCode.NotFound, "SCHOOL_NOT_FOUND"); return@get
                }
                val rewards = RewardService.getRewardCatalog(sid)
                call.ok(rewards, "Reward catalog (${rewards.size})")
            }

            post("/rewards/{rewardId}/redeem") {
                val uid = call.principalUserUuid() ?: run {
                    call.fail("Invalid token", HttpStatusCode.Unauthorized, "UNAUTHORIZED"); return@post
                }
                val childId = call.parameters["childId"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("Invalid child id"); return@post }
                val rewardId = call.parameters["rewardId"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("Invalid reward id"); return@post }

                val childRow = dbQuery {
                    ChildrenTable.selectAll()
                        .where { ChildrenTable.id eq childId }
                        .firstOrNull()
                }
                val sid = childRow?.get(ChildrenTable.schoolId)
                if (sid == null) {
                    call.fail("Child school not found", HttpStatusCode.NotFound, "SCHOOL_NOT_FOUND"); return@post
                }

                val redemption = RewardService.redeemReward(childId, rewardId, sid)
                if (redemption != null) call.ok(redemption, "Reward redeemed")
                else call.fail("Insufficient XP or reward unavailable", HttpStatusCode.BadRequest, "REDEMPTION_FAILED")
            }

            get("/redemptions") {
                val uid = call.principalUserUuid() ?: run {
                    call.fail("Invalid token", HttpStatusCode.Unauthorized, "UNAUTHORIZED"); return@get
                }
                val childId = call.parameters["childId"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("Invalid child id"); return@get }

                val redemptions = RewardService.getStudentRedemptions(childId)
                call.ok(redemptions, "Redemptions (${redemptions.size})")
            }

            get("/leaderboard") {
                val uid = call.principalUserUuid() ?: run {
                    call.fail("Invalid token", HttpStatusCode.Unauthorized, "UNAUTHORIZED"); return@get
                }
                val childId = call.parameters["childId"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("Invalid child id"); return@get }

                val childRow = dbQuery {
                    ChildrenTable.selectAll()
                        .where { ChildrenTable.id eq childId }
                        .firstOrNull()
                }
                val sid = childRow?.get(ChildrenTable.schoolId)
                if (sid == null) {
                    call.fail("Child school not found", HttpStatusCode.NotFound, "SCHOOL_NOT_FOUND"); return@get
                }

                val leaderboard = LeaderboardService.getSchoolLeaderboard(sid)
                val rank = LeaderboardService.getStudentRank(sid, childId)
                call.ok(mapOf("leaderboard" to leaderboard, "myRank" to rank), "Leaderboard")
            }

            get("/events") {
                val events = SeasonalEventService.getActiveEvents()
                call.ok(events, "Active events (${events.size})")
            }
        }

        // ── Admin: Houses, Rewards, Quests, Events management ───────────
        route("/api/v1/admin/gamification") {

            get("/houses") {
                val ctx = call.requireSchoolAdmin() ?: return@get
                val houses = HouseService.getHouses(ctx.schoolId)
                call.ok(houses, "Houses (${houses.size})")
            }

            get("/rewards") {
                val ctx = call.requireSchoolAdmin() ?: return@get
                val rewards = RewardService.getRewardCatalog(ctx.schoolId)
                call.ok(rewards, "Reward catalog (${rewards.size})")
            }

            get("/quests") {
                val quests = QuestService.getActiveQuests()
                call.ok(quests, "Active quests (${quests.size})")
            }

            get("/events") {
                val events = SeasonalEventService.getAllEvents()
                call.ok(events, "Seasonal events (${events.size})")
            }

            get("/leaderboard") {
                val ctx = call.requireSchoolAdmin() ?: return@get
                val leaderboard = LeaderboardService.getSchoolLeaderboard(ctx.schoolId)
                call.ok(leaderboard, "School leaderboard (${leaderboard.size})")
            }
        }
    }
}
