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
 *     GET  /api/v1/parent/gamification/{childId}/xp-history     — last 10 XP transactions
 *     GET  /api/v1/parent/gamification/{childId}/boosts         — active XP boosts
 *     GET  /api/v1/parent/gamification/{childId}/class-goals    — active class goals
 *     GET  /api/v1/parent/gamification/{childId}/quests         — child's quests
 *     GET  /api/v1/parent/gamification/{childId}/house          — child's house
 *     GET  /api/v1/parent/gamification/{childId}/rewards        — reward catalog
 *     POST /api/v1/parent/gamification/{childId}/rewards/{rid}/redeem — redeem reward
 *     GET  /api/v1/parent/gamification/{childId}/redemptions    — redemption history
 *     GET  /api/v1/parent/gamification/{childId}/leaderboard    — leaderboard
 *     GET  /api/v1/parent/gamification/events                   — active seasonal events
 *
 *   Teacher:
 *     POST /api/v1/teacher/gamification/encourage               — award XP to student
 *     POST /api/v1/teacher/gamification/badge/award             — manually award badge
 *     GET  /api/v1/teacher/gamification/badges                  — all badge definitions
 *     GET  /api/v1/teacher/gamification/student/{id}/stats      — student stats
 *     GET  /api/v1/teacher/gamification/student/{id}/badges     — student's earned badges
 *     GET  /api/v1/teacher/gamification/class/leaderboard       — class leaderboard
 *     POST /api/v1/teacher/gamification/shoutout                — send shoutout
 *     GET  /api/v1/teacher/gamification/shoutouts               — list shoutouts (moderation)
 *     DELETE /api/v1/teacher/gamification/shoutouts/{id}        — delete shoutout (moderation)
 *     GET  /api/v1/teacher/gamification/class-goals             — list class goals
 *     POST /api/v1/teacher/gamification/class-goals             — create class goal
 *     PUT  /api/v1/teacher/gamification/class-goals/{id}/progress — update goal progress
 *     POST /api/v1/teacher/gamification/quest/assign            — assign quest to student
 *     GET  /api/v1/teacher/gamification/quests                  — active quest definitions
 *     POST /api/v1/teacher/gamification/spotlight               — spotlight award (+50 XP)
 *     POST /api/v1/teacher/gamification/pep-talk                — class pep talk (1.5x XP boost)
 *     GET  /api/v1/teacher/gamification/overview                — gamification overview
 *     POST /api/v1/teacher/gamification/parent-alert             — send positive nudge to parent
 *     POST /api/v1/teacher/gamification/mentor/assign            — assign mentor to student
 *     DELETE /api/v1/teacher/gamification/mentor/{id}            — unassign mentor
 *     GET  /api/v1/teacher/gamification/mentors                  — list mentor assignments
 *     POST /api/v1/teacher/gamification/study-buddy/assign       — pair two students as study buddies
 *     DELETE /api/v1/teacher/gamification/study-buddy/{id}       — unpair study buddies
 *     GET  /api/v1/teacher/gamification/study-buddies            — list study buddy pairs
 *
 *   Admin:
 *     GET  /api/v1/admin/gamification/flags                     — get kill switch state
 *     PUT  /api/v1/admin/gamification/flags                     — set kill switch state
 *     GET  /api/v1/admin/gamification/badges                    — all badge definitions
 *     GET  /api/v1/admin/gamification/levels                    — level definitions
 *     GET  /api/v1/admin/gamification/houses                    — all houses
 *     GET  /api/v1/admin/gamification/rewards                   — reward catalog
 *     GET  /api/v1/admin/gamification/quests                    — active quests
 *     GET  /api/v1/admin/gamification/events                    — seasonal events
 *     GET  /api/v1/admin/gamification/leaderboard               — school leaderboard
 *     GET  /api/v1/admin/gamification/redemptions               — all redemptions
 *     PUT  /api/v1/admin/gamification/redemptions/status        — approve/reject/fulfill
 *     GET  /api/v1/admin/gamification/boosts                    — all XP boosts
 *     POST /api/v1/admin/gamification/boosts                    — create XP boost
 *     GET  /api/v1/admin/gamification/analytics                 — analytics dashboard
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
import com.littlebridge.enrollplus.db.GameRewardRedemptionsTable
import com.littlebridge.enrollplus.db.GameShoutoutsTable
import com.littlebridge.enrollplus.db.GameStudentStatsTable
import com.littlebridge.enrollplus.db.GameXpBoostsTable
import com.littlebridge.enrollplus.db.GameXpLedgerTable
import com.littlebridge.enrollplus.db.GameMentorAssignmentsTable
import com.littlebridge.enrollplus.db.GameStudyBuddyPairsTable
import com.littlebridge.enrollplus.db.StudentsTable
import com.littlebridge.enrollplus.feature.notifications.Notify
import org.jetbrains.exposed.sql.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import kotlinx.serialization.Serializable
import java.time.Instant
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
data class SetGranularFlagRequest(
    val flagKey: String,
    val enabled: Boolean
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

@Serializable
data class SpotlightRequest(
    val studentId: String,
    val reason: String = "Spotlight award for improvement"
)

@Serializable
data class PepTalkRequest(
    val className: String,
    val section: String? = null
)

@Serializable
data class CreateBoostRequest(
    val boostType: String,
    val multiplier: Float,
    val targetScope: String = "ALL",
    val targetId: String? = null,
    val durationHours: Int = 24
)

@Serializable
data class ParentAlertRequest(
    val studentId: String,
    val message: String = "Your child is making great progress! Keep encouraging them at home."
)

@Serializable
data class MentorAssignRequest(
    val mentorId: String,
    val menteeId: String
)

@Serializable
data class StudyBuddyAssignRequest(
    val student1Id: String,
    val student2Id: String,
    val classId: String? = null
)

@Serializable
data class UpdateRedemptionRequest(
    val redemptionId: String,
    val status: String // APPROVED | REJECTED | FULFILLED
)

@Serializable
data class AdminRedemptionDto(
    val id: String,
    val studentId: String,
    val rewardId: String,
    val xpSpent: Int,
    val status: String,
    val createdAt: String
)

@Serializable
data class AdminBoostDto(
    val id: String,
    val boostType: String,
    val multiplier: Float,
    val targetScope: String,
    val isActive: Boolean,
    val startsAt: String,
    val endsAt: String
)

@Serializable
data class GamificationAnalyticsDto(
    val totalStudents: Int,
    val totalXp: Int,
    val averageXp: Int,
    val levelDistribution: Map<String, Int>,
    val categoryXp: Map<String, Int>,
    val pendingRedemptions: Int
)

@Serializable
data class GamificationOverviewDto(
    val totalStudents: Int,
    val totalXp: Int,
    val levelDistribution: Map<String, Int>,
    val bottom25Ids: List<String>,
    val averageXp: Int
)

@Serializable
data class SpotlightResultDto(
    val xpResult: XpAwardResult,
    val newBadges: List<StudentBadgeDto>
)

@Serializable
data class ShoutoutDto(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val message: String,
    val templateId: Int,
    val isPublic: Boolean,
    val createdAt: String
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

            // ── Parent: XP history (last 10 transactions) ─────────────────
            get("/{childId}/xp-history") {
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
                if (owns == null) { call.fail("Child not found", HttpStatusCode.NotFound, "CHILD_NOT_FOUND"); return@get }

                val history = dbQuery {
                    GameXpLedgerTable.selectAll()
                        .where { GameXpLedgerTable.studentId eq childId }
                        .orderBy(GameXpLedgerTable.createdAt, SortOrder.DESC)
                        .limit(10)
                        .map {
                            XpHistoryEntryDto(
                                id = it[GameXpLedgerTable.id].value.toString(),
                                amount = it[GameXpLedgerTable.amount],
                                reason = it[GameXpLedgerTable.reason],
                                source = it[GameXpLedgerTable.xpSource],
                                category = it[GameXpLedgerTable.category],
                                multiplier = it[GameXpLedgerTable.multiplier],
                                createdAt = it[GameXpLedgerTable.createdAt].toString()
                            )
                        }
                }
                call.ok(history, "XP history (${history.size})")
            }

            // ── Parent: Active boosts for child ────────────────────────────
            get("/{childId}/boosts") {
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
                if (owns == null) { call.fail("Child not found", HttpStatusCode.NotFound, "CHILD_NOT_FOUND"); return@get }

                val sid = owns[ChildrenTable.schoolId]
                if (sid == null) { call.ok(emptyList<XpBoostDto>(), "Active boosts (0 — no school)"); return@get }

                val now = Instant.now()
                val boosts = dbQuery {
                    GameXpBoostsTable.selectAll()
                        .where {
                            (GameXpBoostsTable.schoolId eq sid) and
                            (GameXpBoostsTable.isActive eq true) and
                            (GameXpBoostsTable.startsAt lessEq now) and
                            (GameXpBoostsTable.endsAt greater now)
                        }
                        .map {
                            XpBoostDto(
                                id = it[GameXpBoostsTable.id].value.toString(),
                                boostType = it[GameXpBoostsTable.boostType],
                                multiplier = it[GameXpBoostsTable.multiplier],
                                targetScope = it[GameXpBoostsTable.targetScope],
                                endsAt = it[GameXpBoostsTable.endsAt].toString()
                            )
                        }
                }
                call.ok(boosts, "Active boosts (${boosts.size})")
            }

            // ── Parent: Class goals for child's school ─────────────────────
            get("/{childId}/class-goals") {
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
                if (owns == null) { call.fail("Child not found", HttpStatusCode.NotFound, "CHILD_NOT_FOUND"); return@get }
                val sid = owns[ChildrenTable.schoolId]
                if (sid == null) { call.ok(emptyList<ClassGoalDto>(), "Class goals (0 — no school)"); return@get }

                val goals = dbQuery {
                    GameClassGoalsTable.selectAll()
                        .where {
                            (GameClassGoalsTable.schoolId eq sid) and
                            (GameClassGoalsTable.completed eq false)
                        }
                        .orderBy(GameClassGoalsTable.createdAt, SortOrder.DESC)
                        .map {
                            ClassGoalDto(
                                id = it[GameClassGoalsTable.id].value.toString(),
                                className = it[GameClassGoalsTable.className] ?: "",
                                section = it[GameClassGoalsTable.section] ?: "",
                                goalType = it[GameClassGoalsTable.goalType],
                                target = it[GameClassGoalsTable.target],
                                currentProgress = it[GameClassGoalsTable.currentProgress],
                                reward = it[GameClassGoalsTable.reward],
                                deadline = it[GameClassGoalsTable.deadline]?.toString() ?: ""
                            )
                        }
                }
                call.ok(goals, "Class goals (${goals.size})")
            }

            // ── Parent: Quests ─────────────────────────────────────────────
            get("/{childId}/quests") {
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
                if (owns == null) { call.fail("Child not found", HttpStatusCode.NotFound, "CHILD_NOT_FOUND"); return@get }

                val quests = QuestService.getStudentQuests(childId)
                call.ok(quests, "Student quests (${quests.size})")
            }

            // ── Parent: House ──────────────────────────────────────────────
            get("/{childId}/house") {
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
                if (owns == null) { call.fail("Child not found", HttpStatusCode.NotFound, "CHILD_NOT_FOUND"); return@get }

                val house = HouseService.getStudentHouse(childId)
                if (house != null) call.ok(house, "Student house")
                else call.okMessage("No house assigned")
            }

            // ── Parent: Rewards ────────────────────────────────────────────
            get("/{childId}/rewards") {
                val uid = call.principalUserUuid() ?: run {
                    call.fail("Invalid token", HttpStatusCode.Unauthorized, "UNAUTHORIZED"); return@get
                }
                val childId = call.parameters["childId"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("Invalid child id"); return@get }

                val childRow = dbQuery {
                    ChildrenTable.selectAll()
                        .where { (ChildrenTable.id eq childId) and (ChildrenTable.parentId eq uid) }
                        .firstOrNull()
                }
                if (childRow == null) { call.fail("Child not found", HttpStatusCode.NotFound, "CHILD_NOT_FOUND"); return@get }
                val sid = childRow[ChildrenTable.schoolId]
                if (sid == null) { call.ok(emptyList<RewardDto>(), "Reward catalog (0 — no school)"); return@get }
                val rewards = RewardService.getRewardCatalog(sid)
                call.ok(rewards, "Reward catalog (${rewards.size})")
            }

            // ── Parent: Redeem reward ──────────────────────────────────────
            post("/{childId}/rewards/{rewardId}/redeem") {
                val uid = call.principalUserUuid() ?: run {
                    call.fail("Invalid token", HttpStatusCode.Unauthorized, "UNAUTHORIZED"); return@post
                }
                val childId = call.parameters["childId"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("Invalid child id"); return@post }
                val rewardId = call.parameters["rewardId"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("Invalid reward id"); return@post }

                val childRow = dbQuery {
                    ChildrenTable.selectAll()
                        .where { (ChildrenTable.id eq childId) and (ChildrenTable.parentId eq uid) }
                        .firstOrNull()
                }
                if (childRow == null) { call.fail("Child not found", HttpStatusCode.NotFound, "CHILD_NOT_FOUND"); return@post }
                val sid = childRow[ChildrenTable.schoolId]
                if (sid == null) {
                    call.fail("Child school not found", HttpStatusCode.NotFound, "SCHOOL_NOT_FOUND"); return@post
                }

                val redemption = RewardService.redeemReward(childId, rewardId, sid)
                if (redemption != null) call.ok(redemption, "Reward redeemed")
                else call.fail("Insufficient XP or reward unavailable", HttpStatusCode.BadRequest, "REDEMPTION_FAILED")
            }

            // ── Parent: Redemption history ─────────────────────────────────
            get("/{childId}/redemptions") {
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
                if (owns == null) { call.fail("Child not found", HttpStatusCode.NotFound, "CHILD_NOT_FOUND"); return@get }

                val redemptions = RewardService.getStudentRedemptions(childId)
                call.ok(redemptions, "Redemptions (${redemptions.size})")
            }

            // ── Parent: Leaderboard ────────────────────────────────────────
            get("/{childId}/leaderboard") {
                val uid = call.principalUserUuid() ?: run {
                    call.fail("Invalid token", HttpStatusCode.Unauthorized, "UNAUTHORIZED"); return@get
                }
                val childId = call.parameters["childId"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("Invalid child id"); return@get }

                val childRow = dbQuery {
                    ChildrenTable.selectAll()
                        .where { (ChildrenTable.id eq childId) and (ChildrenTable.parentId eq uid) }
                        .firstOrNull()
                }
                if (childRow == null) { call.fail("Child not found", HttpStatusCode.NotFound, "CHILD_NOT_FOUND"); return@get }
                val sid = childRow[ChildrenTable.schoolId]
                if (sid == null) {
                    call.ok(LeaderboardResponseDto(emptyList(), 0), "Leaderboard (no school)")
                    return@get
                }

                val leaderboard = LeaderboardService.getSchoolLeaderboard(sid)
                val rank = LeaderboardService.getStudentRank(sid, childId)
                call.ok(LeaderboardResponseDto(leaderboard, rank), "Leaderboard")
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

            // ── Teacher Tools: Spotlight award (+50 XP) ───────────────────
            post("/spotlight") {
                val ctx = call.requireSchoolOrTeacherContext() ?: return@post
                val req = runCatching { call.receive<SpotlightRequest>() }.getOrNull()
                    ?: run { call.fail("Invalid request body"); return@post }

                val studentId = runCatching { UUID.fromString(req.studentId) }.getOrNull()
                    ?: run { call.fail("Invalid student id"); return@post }

                val result = GamificationService.awardXp(
                    studentId = studentId,
                    schoolId = ctx.schoolId,
                    amount = 50,
                    reason = "Spotlight: ${req.reason}",
                    source = "teacher_spotlight",
                    category = "CHARACTER"
                )
                val newBadges = BadgeCriteriaEvaluator.evaluateBadges(studentId, ctx.schoolId)
                call.ok(SpotlightResultDto(xpResult = result, newBadges = newBadges), "Spotlight awarded: +50 XP")
            }

            // ── Teacher Tools: Class Pep Talk (x1.5 XP boost for 24h) ─────
            post("/pep-talk") {
                val ctx = call.requireSchoolOrTeacherContext() ?: return@post
                val req = runCatching { call.receive<PepTalkRequest>() }.getOrNull()
                    ?: run { call.fail("Invalid request body"); return@post }

                val now = Instant.now()
                dbQuery {
                    GameXpBoostsTable.insert {
                        it[GameXpBoostsTable.schoolId] = ctx.schoolId
                        it[GameXpBoostsTable.boostType] = "PEP_TALK"
                        it[GameXpBoostsTable.multiplier] = 1.5f
                        it[GameXpBoostsTable.targetScope] = "SCHOOL"
                        it[GameXpBoostsTable.targetId] = null
                        it[GameXpBoostsTable.startsAt] = now
                        it[GameXpBoostsTable.endsAt] = now.plusSeconds(24 * 3600)
                        it[GameXpBoostsTable.isActive] = true
                        it[GameXpBoostsTable.createdAt] = now
                    }
                }
                call.okMessage("Class pep talk activated! 1.5x XP for 24 hours")
            }

            // ── Teacher Tools: Shoutout moderation (list) ─────────────────
            get("/shoutouts") {
                val ctx = call.requireSchoolOrTeacherContext() ?: return@get
                val shoutouts = dbQuery {
                    GameShoutoutsTable.selectAll()
                        .where {
                            (GameShoutoutsTable.schoolId eq ctx.schoolId) and
                            (GameShoutoutsTable.isDeleted eq false)
                        }
                        .orderBy(GameShoutoutsTable.createdAt, SortOrder.DESC)
                        .map {
                            ShoutoutDto(
                                id = it[GameShoutoutsTable.id].value.toString(),
                                senderId = it[GameShoutoutsTable.senderId].toString(),
                                receiverId = it[GameShoutoutsTable.receiverId].toString(),
                                message = it[GameShoutoutsTable.message],
                                templateId = it[GameShoutoutsTable.templateId],
                                isPublic = it[GameShoutoutsTable.isPublic],
                                createdAt = it[GameShoutoutsTable.createdAt].toString()
                            )
                        }
                }
                call.ok(shoutouts, "Shoutouts (${shoutouts.size})")
            }

            // ── Teacher Tools: Shoutout moderation (delete) ───────────────
            delete("/shoutouts/{id}") {
                val ctx = call.requireSchoolOrTeacherContext() ?: return@delete
                val shoutoutId = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("Invalid shoutout id"); return@delete }

                val updated = dbQuery {
                    GameShoutoutsTable.update({
                        (GameShoutoutsTable.id eq shoutoutId) and (GameShoutoutsTable.schoolId eq ctx.schoolId)
                    }) {
                        it[GameShoutoutsTable.isDeleted] = true
                    }
                }
                if (updated > 0) call.okMessage("Shoutout removed")
                else call.fail("Shoutout not found", HttpStatusCode.NotFound, "SHOUTOUT_NOT_FOUND")
            }

            // ── Teacher Tools: Gamification overview ──────────────────────
            get("/overview") {
                val ctx = call.requireSchoolOrTeacherContext() ?: return@get

                val allStats = dbQuery {
                    GameStudentStatsTable.selectAll()
                        .where { GameStudentStatsTable.schoolId eq ctx.schoolId }
                        .orderBy(GameStudentStatsTable.totalXp, SortOrder.DESC)
                        .toList()
                }

                val totalStudents = allStats.size
                val bottom25Threshold = if (totalStudents > 0) {
                    allStats.sortedByDescending { it[GameStudentStatsTable.totalXp] }
                        .drop((totalStudents * 0.75).toInt())
                        .map { it[GameStudentStatsTable.studentId].toString() }
                } else emptyList()

                val levelDistribution = allStats.groupingBy { it[GameStudentStatsTable.currentLevel] }.eachCount()

                val totalXp = allStats.sumOf { it[GameStudentStatsTable.totalXp] }

                call.ok(GamificationOverviewDto(
                    totalStudents = totalStudents,
                    totalXp = totalXp,
                    levelDistribution = levelDistribution.mapKeys { it.key.toString() },
                    bottom25Ids = bottom25Threshold,
                    averageXp = if (totalStudents > 0) totalXp / totalStudents else 0
                ), "Gamification overview")
            }

            // ── Teacher Tools: Update class goal progress ─────────────────
            put("/class-goals/{id}/progress") {
                val ctx = call.requireSchoolOrTeacherContext() ?: return@put
                val goalId = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("Invalid goal id"); return@put }
                val progress = call.request.queryParameters["progress"]?.toIntOrNull()
                    ?: run { call.fail("Invalid progress"); return@put }

                val updated = dbQuery {
                    val goal = GameClassGoalsTable.selectAll()
                        .where { (GameClassGoalsTable.id eq goalId) and (GameClassGoalsTable.schoolId eq ctx.schoolId) }
                        .firstOrNull() ?: return@dbQuery 0

                    val target = goal[GameClassGoalsTable.target]
                    val completed = progress >= target
                    GameClassGoalsTable.update({
                        (GameClassGoalsTable.id eq goalId) and (GameClassGoalsTable.schoolId eq ctx.schoolId)
                    }) {
                        it[GameClassGoalsTable.currentProgress] = progress
                        if (completed) {
                            it[GameClassGoalsTable.completed] = true
                            it[GameClassGoalsTable.completedAt] = Instant.now()
                        }
                    }
                    if (completed) 1 else 1
                }
                if (updated > 0) call.okMessage("Class goal progress updated")
                else call.fail("Goal not found", HttpStatusCode.NotFound, "GOAL_NOT_FOUND")
            }

            // ── Teacher Tools: Parent Alert ───────────────────────────────
            post("/parent-alert") {
                val ctx = call.requireSchoolOrTeacherContext() ?: return@post
                val req = runCatching { call.receive<ParentAlertRequest>() }.getOrNull()
                    ?: run { call.fail("Invalid request body"); return@post }

                val studentId = runCatching { UUID.fromString(req.studentId) }.getOrNull()
                    ?: run { call.fail("Invalid student id"); return@post }

                // Find the student's parent via StudentsTable → studentCode → ChildrenTable.parentId
                val parentUserIds = dbQuery {
                    val student = StudentsTable.selectAll()
                        .where { StudentsTable.id eq studentId }
                        .firstOrNull()
                    if (student == null) return@dbQuery emptyList()

                    val code = student[StudentsTable.studentCode]
                    ChildrenTable.selectAll()
                        .where { ChildrenTable.studentCode eq code }
                        .map { it[ChildrenTable.parentId] }
                }

                if (parentUserIds.isNotEmpty()) {
                    Notify.toUsers(
                        userIds = parentUserIds,
                        category = "GAMIFICATION",
                        title = "Teacher Update",
                        body = req.message,
                        schoolId = ctx.schoolId,
                        actorId = ctx.userId,
                        deepLink = "/parent/academics",
                        refType = "gamification_update",
                        refId = studentId?.toString(),
                    )
                    call.okMessage("Parent alert sent to ${parentUserIds.size} parent(s)")
                } else {
                    call.fail("No parent linked to this student", HttpStatusCode.NotFound, "NO_PARENT_LINKED")
                }
            }

            // ── Teacher Tools: Mentor Assignment ──────────────────────────
            post("/mentor/assign") {
                val ctx = call.requireSchoolOrTeacherContext() ?: return@post
                val req = runCatching { call.receive<MentorAssignRequest>() }.getOrNull()
                    ?: run { call.fail("Invalid request body"); return@post }

                val mentorId = runCatching { UUID.fromString(req.mentorId) }.getOrNull()
                    ?: run { call.fail("Invalid mentor id"); return@post }
                val menteeId = runCatching { UUID.fromString(req.menteeId) }.getOrNull()
                    ?: run { call.fail("Invalid mentee id"); return@post }

                if (mentorId == menteeId) {
                    call.fail("Cannot assign student as their own mentor", HttpStatusCode.BadRequest, "INVALID_MENTOR")
                    return@post
                }

                val inserted = dbQuery {
                    val existing = GameMentorAssignmentsTable.selectAll()
                        .where {
                            (GameMentorAssignmentsTable.mentorId eq mentorId) and
                            (GameMentorAssignmentsTable.menteeId eq menteeId) and
                            (GameMentorAssignmentsTable.schoolId eq ctx.schoolId) and
                            (GameMentorAssignmentsTable.isActive eq true)
                        }
                        .count() > 0
                    if (existing) return@dbQuery false

                    GameMentorAssignmentsTable.insert {
                        it[GameMentorAssignmentsTable.mentorId] = mentorId
                        it[GameMentorAssignmentsTable.menteeId] = menteeId
                        it[GameMentorAssignmentsTable.schoolId] = ctx.schoolId
                        it[GameMentorAssignmentsTable.assignedBy] = ctx.userId
                        it[GameMentorAssignmentsTable.isActive] = true
                        it[GameMentorAssignmentsTable.createdAt] = Instant.now()
                    }
                    true
                }
                if (inserted) call.okMessage("Mentor assigned")
                else call.fail("Mentor already assigned", HttpStatusCode.BadRequest, "MENTOR_ALREADY_ASSIGNED")
            }

            delete("/mentor/{id}") {
                val ctx = call.requireSchoolOrTeacherContext() ?: return@delete
                val assignmentId = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("Invalid assignment id"); return@delete }

                val updated = dbQuery {
                    GameMentorAssignmentsTable.update({
                        (GameMentorAssignmentsTable.id eq assignmentId) and
                        (GameMentorAssignmentsTable.schoolId eq ctx.schoolId)
                    }) {
                        it[GameMentorAssignmentsTable.isActive] = false
                    }
                }
                if (updated > 0) call.okMessage("Mentor unassigned")
                else call.fail("Assignment not found", HttpStatusCode.NotFound, "MENTOR_NOT_FOUND")
            }

            get("/mentors") {
                val ctx = call.requireSchoolOrTeacherContext() ?: return@get
                val assignments = dbQuery {
                    GameMentorAssignmentsTable.selectAll()
                        .where {
                            (GameMentorAssignmentsTable.schoolId eq ctx.schoolId) and
                            (GameMentorAssignmentsTable.isActive eq true)
                        }
                        .orderBy(GameMentorAssignmentsTable.createdAt, SortOrder.DESC)
                        .map {
                            mapOf(
                                "id" to it[GameMentorAssignmentsTable.id].value.toString(),
                                "mentorId" to it[GameMentorAssignmentsTable.mentorId].toString(),
                                "menteeId" to it[GameMentorAssignmentsTable.menteeId].toString(),
                                "assignedBy" to it[GameMentorAssignmentsTable.assignedBy].toString(),
                                "createdAt" to it[GameMentorAssignmentsTable.createdAt].toString()
                            )
                        }
                }
                call.ok(assignments, "Mentor assignments (${assignments.size})")
            }

            // ── Teacher Tools: Study Buddy Assignment ─────────────────────
            post("/study-buddy/assign") {
                val ctx = call.requireSchoolOrTeacherContext() ?: return@post
                val req = runCatching { call.receive<StudyBuddyAssignRequest>() }.getOrNull()
                    ?: run { call.fail("Invalid request body"); return@post }

                val student1Id = runCatching { UUID.fromString(req.student1Id) }.getOrNull()
                    ?: run { call.fail("Invalid student1 id"); return@post }
                val student2Id = runCatching { UUID.fromString(req.student2Id) }.getOrNull()
                    ?: run { call.fail("Invalid student2 id"); return@post }
                val classId = req.classId?.let { runCatching { UUID.fromString(it) }.getOrNull() }

                if (student1Id == student2Id) {
                    call.fail("Cannot pair a student with themselves", HttpStatusCode.BadRequest, "INVALID_BUDDY")
                    return@post
                }

                val inserted = dbQuery {
                    val existing = GameStudyBuddyPairsTable.selectAll()
                        .where {
                            (GameStudyBuddyPairsTable.schoolId eq ctx.schoolId) and
                            (GameStudyBuddyPairsTable.isActive eq true) and
                            (
                                ((GameStudyBuddyPairsTable.student1Id eq student1Id) and (GameStudyBuddyPairsTable.student2Id eq student2Id)) or
                                ((GameStudyBuddyPairsTable.student1Id eq student2Id) and (GameStudyBuddyPairsTable.student2Id eq student1Id))
                            )
                        }
                        .count() > 0
                    if (existing) return@dbQuery false

                    GameStudyBuddyPairsTable.insert {
                        it[GameStudyBuddyPairsTable.student1Id] = student1Id
                        it[GameStudyBuddyPairsTable.student2Id] = student2Id
                        it[GameStudyBuddyPairsTable.schoolId] = ctx.schoolId
                        it[GameStudyBuddyPairsTable.classId] = classId
                        it[GameStudyBuddyPairsTable.assignedBy] = ctx.userId
                        it[GameStudyBuddyPairsTable.isActive] = true
                        it[GameStudyBuddyPairsTable.expiresAt] = Instant.now().plusSeconds(7 * 24 * 3600)
                        it[GameStudyBuddyPairsTable.createdAt] = Instant.now()
                    }
                    true
                }
                if (inserted) call.okMessage("Study buddy pair created")
                else call.fail("Pair already exists", HttpStatusCode.BadRequest, "BUDDY_ALREADY_PAIRED")
            }

            delete("/study-buddy/{id}") {
                val ctx = call.requireSchoolOrTeacherContext() ?: return@delete
                val pairId = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("Invalid pair id"); return@delete }

                val updated = dbQuery {
                    GameStudyBuddyPairsTable.update({
                        (GameStudyBuddyPairsTable.id eq pairId) and
                        (GameStudyBuddyPairsTable.schoolId eq ctx.schoolId)
                    }) {
                        it[GameStudyBuddyPairsTable.isActive] = false
                    }
                }
                if (updated > 0) call.okMessage("Study buddy pair removed")
                else call.fail("Pair not found", HttpStatusCode.NotFound, "BUDDY_NOT_FOUND")
            }

            get("/study-buddies") {
                val ctx = call.requireSchoolOrTeacherContext() ?: return@get
                val pairs = dbQuery {
                    GameStudyBuddyPairsTable.selectAll()
                        .where {
                            (GameStudyBuddyPairsTable.schoolId eq ctx.schoolId) and
                            (GameStudyBuddyPairsTable.isActive eq true)
                        }
                        .orderBy(GameStudyBuddyPairsTable.createdAt, SortOrder.DESC)
                        .map {
                            mapOf(
                                "id" to it[GameStudyBuddyPairsTable.id].value.toString(),
                                "student1Id" to it[GameStudyBuddyPairsTable.student1Id].toString(),
                                "student2Id" to it[GameStudyBuddyPairsTable.student2Id].toString(),
                                "assignedBy" to it[GameStudyBuddyPairsTable.assignedBy].toString(),
                                "expiresAt" to it[GameStudyBuddyPairsTable.expiresAt].toString(),
                                "createdAt" to it[GameStudyBuddyPairsTable.createdAt].toString()
                            )
                        }
                }
                call.ok(pairs, "Study buddy pairs (${pairs.size})")
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

            put("/flags/granular") {
                val ctx = call.requireSchoolAdmin() ?: return@put
                val req = runCatching { call.receive<SetGranularFlagRequest>() }.getOrNull()
                    ?: run { call.fail("Invalid request body"); return@put }

                val updated = GamificationService.setGranularFlag(req.flagKey, req.enabled)
                if (updated) call.okMessage("Flag ${req.flagKey} ${if (req.enabled) "enabled" else "disabled"}")
                else call.fail("Failed to update flag", HttpStatusCode.InternalServerError, "UPDATE_FAILED")
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

        // ── Parent: Active seasonal events (not child-specific) ───────────
        get("/api/v1/parent/gamification/events") {
            val uid = call.principalUserUuid() ?: run {
                call.fail("Invalid token", HttpStatusCode.Unauthorized, "UNAUTHORIZED"); return@get
            }
            val events = SeasonalEventService.getActiveEvents()
            call.ok(events, "Active events (${events.size})")
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

            // ── Admin: Pending redemptions ────────────────────────────────
            get("/redemptions") {
                val ctx = call.requireSchoolAdmin() ?: return@get
                val redemptions = dbQuery {
                    GameRewardRedemptionsTable.selectAll()
                        .where { GameRewardRedemptionsTable.schoolId eq ctx.schoolId }
                        .orderBy(GameRewardRedemptionsTable.createdAt, SortOrder.DESC)
                        .map {
                            AdminRedemptionDto(
                                id = it[GameRewardRedemptionsTable.id].value.toString(),
                                studentId = it[GameRewardRedemptionsTable.studentId].toString(),
                                rewardId = it[GameRewardRedemptionsTable.rewardId].toString(),
                                xpSpent = it[GameRewardRedemptionsTable.xpSpent],
                                status = it[GameRewardRedemptionsTable.status],
                                createdAt = it[GameRewardRedemptionsTable.createdAt].toString()
                            )
                        }
                }
                call.ok(redemptions, "Redemptions (${redemptions.size})")
            }

            // ── Admin: Approve/reject redemption ──────────────────────────
            put("/redemptions/status") {
                val ctx = call.requireSchoolAdmin() ?: return@put
                val req = runCatching { call.receive<UpdateRedemptionRequest>() }.getOrNull()
                    ?: run { call.fail("Invalid request body"); return@put }

                val redemptionId = runCatching { UUID.fromString(req.redemptionId) }.getOrNull()
                    ?: run { call.fail("Invalid redemption id"); return@put }

                val validStatus = req.status in setOf("APPROVED", "REJECTED", "FULFILLED")
                if (!validStatus) { call.fail("Invalid status"); return@put }

                val updated = dbQuery {
                    GameRewardRedemptionsTable.update({
                        (GameRewardRedemptionsTable.id eq redemptionId) and
                        (GameRewardRedemptionsTable.schoolId eq ctx.schoolId)
                    }) {
                        it[GameRewardRedemptionsTable.status] = req.status
                        if (req.status == "APPROVED") {
                            it[GameRewardRedemptionsTable.approvedBy] = ctx.userId
                            it[GameRewardRedemptionsTable.approvedAt] = Instant.now()
                        }
                        if (req.status == "FULFILLED") {
                            it[GameRewardRedemptionsTable.fulfilledAt] = Instant.now()
                        }
                    }
                }
                if (updated > 0) call.okMessage("Redemption $${req.status}")
                else call.fail("Redemption not found", HttpStatusCode.NotFound, "REDEMPTION_NOT_FOUND")
            }

            // ── Admin: List active boosts ─────────────────────────────────
            get("/boosts") {
                val ctx = call.requireSchoolAdmin() ?: return@get
                val boosts = dbQuery {
                    GameXpBoostsTable.selectAll()
                        .where { GameXpBoostsTable.schoolId eq ctx.schoolId }
                        .orderBy(GameXpBoostsTable.createdAt, SortOrder.DESC)
                        .map {
                            AdminBoostDto(
                                id = it[GameXpBoostsTable.id].value.toString(),
                                boostType = it[GameXpBoostsTable.boostType],
                                multiplier = it[GameXpBoostsTable.multiplier],
                                targetScope = it[GameXpBoostsTable.targetScope],
                                isActive = it[GameXpBoostsTable.isActive],
                                startsAt = it[GameXpBoostsTable.startsAt].toString(),
                                endsAt = it[GameXpBoostsTable.endsAt].toString()
                            )
                        }
                }
                call.ok(boosts, "Boosts (${boosts.size})")
            }

            // ── Admin: Create boost ───────────────────────────────────────
            post("/boosts") {
                val ctx = call.requireSchoolAdmin() ?: return@post
                val req = runCatching { call.receive<CreateBoostRequest>() }.getOrNull()
                    ?: run { call.fail("Invalid request body"); return@post }

                val now = Instant.now()
                dbQuery {
                    GameXpBoostsTable.insert {
                        it[GameXpBoostsTable.schoolId] = ctx.schoolId
                        it[GameXpBoostsTable.boostType] = req.boostType
                        it[GameXpBoostsTable.multiplier] = req.multiplier
                        it[GameXpBoostsTable.targetScope] = req.targetScope
                        it[GameXpBoostsTable.targetId] = req.targetId?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                        it[GameXpBoostsTable.startsAt] = now
                        it[GameXpBoostsTable.endsAt] = now.plusSeconds(req.durationHours.toLong() * 3600)
                        it[GameXpBoostsTable.isActive] = true
                        it[GameXpBoostsTable.createdAt] = now
                    }
                }
                call.okMessage("Boost created: ${req.boostType} x${req.multiplier}")
            }

            // ── Admin: Analytics dashboard ────────────────────────────────
            get("/analytics") {
                val ctx = call.requireSchoolAdmin() ?: return@get

                val allStats = dbQuery {
                    GameStudentStatsTable.selectAll()
                        .where { GameStudentStatsTable.schoolId eq ctx.schoolId }
                        .toList()
                }

                val totalStudents = allStats.size
                val totalXp = allStats.sumOf { it[GameStudentStatsTable.totalXp] }
                val levelDistribution = allStats.groupingBy { it[GameStudentStatsTable.currentLevel] }.eachCount()

                val categoryXp = dbQuery {
                    GameXpLedgerTable.selectAll()
                        .where { GameXpLedgerTable.schoolId eq ctx.schoolId }
                        .map { it[GameXpLedgerTable.category] to it[GameXpLedgerTable.amount] }
                        .groupBy({ it.first }, { it.second })
                        .mapValues { (_, amounts) -> amounts.sum() }
                }

                val pendingRedemptions = dbQuery {
                    GameRewardRedemptionsTable.selectAll()
                        .where {
                            (GameRewardRedemptionsTable.schoolId eq ctx.schoolId) and
                            (GameRewardRedemptionsTable.status eq "PENDING")
                        }
                        .count()
                }

                call.ok(GamificationAnalyticsDto(
                    totalStudents = totalStudents,
                    totalXp = totalXp,
                    averageXp = if (totalStudents > 0) totalXp / totalStudents else 0,
                    levelDistribution = levelDistribution.mapKeys { it.key.toString() },
                    categoryXp = categoryXp,
                    pendingRedemptions = pendingRedemptions
                ), "Gamification analytics")
            }
        }
    }
}
