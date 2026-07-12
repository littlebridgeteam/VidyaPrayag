/*
 * File: GamificationSeeder.kt
 * Module: feature/gamification
 *
 * Seeds default gamification data on first boot:
 *   - Level definitions (10 levels)
 *   - Badge definitions (40 badges)
 *   - Progression paths (5 paths)
 *   - Titles (10 titles)
 *   - Quest definitions (12 templates)
 *   - Motivation messages (22 messages)
 *   - Kill switch flag in app_config flags JSON
 *
 * Idempotent: every insert is guarded by a presence check.
 * Gate: APP_SEED_GAMIFICATION env var (default "true").
 */
package com.littlebridge.enrollplus.feature.gamification

import com.littlebridge.enrollplus.db.AppConfigTable
import com.littlebridge.enrollplus.db.GameBadgeDefinitionsTable
import com.littlebridge.enrollplus.db.GameLevelDefinitionsTable
import com.littlebridge.enrollplus.db.GameMotivationMessagesTable
import com.littlebridge.enrollplus.db.GameProgressionPathsTable
import com.littlebridge.enrollplus.db.GameQuestDefinitionsTable
import com.littlebridge.enrollplus.db.GameTitlesTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.slf4j.LoggerFactory
import java.time.Instant

object GamificationSeeder {

    private val logger = LoggerFactory.getLogger("GamificationSeeder")

    fun seed() {
        val enabled = System.getenv("APP_SEED_GAMIFICATION")?.lowercase() != "false"
        if (!enabled) {
            logger.info("GamificationSeeder: SKIPPED (APP_SEED_GAMIFICATION=false)")
            return
        }

        transaction {
            seedKillSwitchFlag()
            seedLevels()
            seedBadges()
            seedProgressionPaths()
            seedTitles()
            seedMotivationMessages()
            seedQuests()
        }
        logger.info("GamificationSeeder: seed complete")
    }

    // ── Kill switch flag in app_config ────────────────────────────────────
    private fun seedKillSwitchFlag() {
        val row = AppConfigTable.selectAll()
            .firstOrNull { it[AppConfigTable.key] == "flags" }

        if (row == null) {
            // No flags row at all — create one with gamification defaults
            val defaultFlags = """
                {"is_gamification_enabled":false,"gamification_leaderboards":true,"gamification_rewards":true,
                "gamification_houses":true,"gamification_quests":true,"gamification_mentor":true,
                "gamification_shoutouts":true,"gamification_events":true,"gamification_class_goals":true,
                "gamification_combos":true,"gamification_boosts":true}
            """.trimIndent()
            AppConfigTable.insert {
                it[AppConfigTable.key] = "flags"
                it[AppConfigTable.value] = defaultFlags
                it[AppConfigTable.updatedAt] = Instant.now()
            }
            logger.info("GamificationSeeder: created app_config flags with gamification defaults (disabled by default)")
            return
        }

        // Flags row exists — check if gamification keys are present
        val currentFlags = row[AppConfigTable.value]
        if (!currentFlags.contains("is_gamification_enabled")) {
            // Inject gamification flags into existing JSON
            val updatedFlags = currentFlags
                .removeSuffix("}")
                .trim()
                .let { if (it.endsWith(",")) it else "$it," } +
                """"is_gamification_enabled":false,"gamification_leaderboards":true,"gamification_rewards":true,"gamification_houses":true,"gamification_quests":true,"gamification_mentor":true,"gamification_shoutouts":true,"gamification_events":true,"gamification_class_goals":true,"gamification_combos":true,"gamification_boosts":true}"""
            // Update the row
            AppConfigTable.update({ AppConfigTable.key eq "flags" }) {
                it[AppConfigTable.value] = updatedFlags
                it[AppConfigTable.updatedAt] = Instant.now()
            }
            logger.info("GamificationSeeder: injected gamification flags into existing app_config (disabled by default)")
        }
    }

    // ── Level definitions ────────────────────────────────────────────────
    private fun seedLevels() {
        val existing = GameLevelDefinitionsTable.selectAll().count()
        if (existing > 0) return

        val levels = listOf(
            Triple(1, 0, "Beginner"),
            Triple(2, 100, "Explorer"),
            Triple(3, 300, "Achiever"),
            Triple(4, 600, "Rising Star"),
            Triple(5, 1000, "Scholar"),
            Triple(6, 1500, "Expert"),
            Triple(7, 2200, "Master"),
            Triple(8, 3000, "Champion"),
            Triple(9, 4000, "Legend"),
            Triple(10, 5500, "Grandmaster")
        )
        levels.forEach { (level, xp, title) ->
            GameLevelDefinitionsTable.insert {
                it[GameLevelDefinitionsTable.schoolId] = null
                it[GameLevelDefinitionsTable.level] = level
                it[GameLevelDefinitionsTable.xpRequired] = xp
                it[GameLevelDefinitionsTable.title] = title
                it[GameLevelDefinitionsTable.iconName] = when (level) {
                    1 -> "sprout"
                    2 -> "explore"
                    in 3..4 -> "star"
                    5 -> "menu_book"
                    6 -> "school"
                    7 -> "military_tech"
                    8 -> "emoji_events"
                    9 -> "workspace_premium"
                    else -> "diamond"
                }
                it[GameLevelDefinitionsTable.isActive] = true
                it[GameLevelDefinitionsTable.createdAt] = Instant.now()
            }
        }
        logger.info("GamificationSeeder: seeded 10 level definitions")
    }

    // ── Badge definitions ────────────────────────────────────────────────
    private fun seedBadges() {
        if (GameBadgeDefinitionsTable.selectAll().count() > 0) return

        data class Badge(
            val code: String, val name: String, val desc: String, val icon: String,
            val category: String, val rarity: String, val xp: Int, val criteria: String
        )

        val badges = listOf(
            // Academic (8)
            Badge("first_steps", "First Steps", "Complete your first assessment", "flag", "ACADEMIC", "COMMON", 10, """{"type":"count","source":"assessment","threshold":1}"""),
            Badge("top_scorer", "Top Scorer", "Score 90%+ on any assessment", "emoji_events", "ACADEMIC", "RARE", 20, """{"type":"count","source":"assessment","min_score":90,"threshold":1}"""),
            Badge("subject_master", "Subject Master", "Complete 5 assessments in one subject", "school", "ACADEMIC", "EPIC", 30, """{"type":"count","source":"assessment","threshold":5}"""),
            Badge("homework_hero", "Homework Hero", "10 on-time homework submissions", "task_alt", "ACADEMIC", "RARE", 20, """{"type":"count","source":"homework","on_time":true,"threshold":10}"""),
            Badge("early_bird", "Early Bird", "Submit before due date 5 times", "schedule", "ACADEMIC", "RARE", 20, """{"type":"count","source":"homework","early":true,"threshold":5}"""),
            Badge("quiz_champion", "Quiz Champion", "Complete 20 AI Tutor practices", "psychology", "ACADEMIC", "EPIC", 30, """{"type":"count","source":"tutor","threshold":20}"""),
            Badge("consistent_performer", "Consistent Performer", "Score 75%+ on 10 assessments", "verified", "ACADEMIC", "EPIC", 30, """{"type":"count","source":"assessment","min_score":75,"threshold":10}"""),
            Badge("perfect_score", "Perfect Score", "Score 100% on any assessment", "stars", "ACADEMIC", "LEGENDARY", 50, """{"type":"count","source":"assessment","min_score":100,"threshold":1}"""),
            // Attendance (4)
            Badge("perfect_week", "Perfect Week", "5 consecutive present days", "calendar_today", "ATTENDANCE", "RARE", 20, """{"type":"count","source":"attendance","status":"PRESENT","consecutive":true,"threshold":5}"""),
            Badge("iron_streak", "Iron Streak", "30 consecutive present days", "whatshot", "ATTENDANCE", "EPIC", 30, """{"type":"count","source":"attendance","status":"PRESENT","consecutive":true,"threshold":30}"""),
            Badge("semester_champion", "Semester Champion", "90+ present days in a semester", "military_tech", "ATTENDANCE", "LEGENDARY", 50, """{"type":"count","source":"attendance","status":"PRESENT","threshold":90}"""),
            Badge("unbreakable", "Unbreakable", "Full year, no absences", "shield", "ATTENDANCE", "MYTHIC", 100, """{"type":"count","source":"attendance","status":"PRESENT","consecutive":true,"threshold":200}"""),
            // Co-Curricular (5)
            Badge("book_worm", "Book Worm", "Issue 5 library books", "menu_book", "CO_CURRICULAR", "RARE", 20, """{"type":"count","source":"library","threshold":5}"""),
            Badge("event_enthusiast", "Event Enthusiast", "Register for 3 events", "event", "CO_CURRICULAR", "RARE", 20, """{"type":"count","source":"event","threshold":3}"""),
            Badge("sports_star", "Sports Star", "Participate in sports day", "sports", "CO_CURRICULAR", "RARE", 20, """{"type":"count","source":"event","category":"sports","threshold":1}"""),
            Badge("stage_performer", "Stage Performer", "Participate in cultural event", "theater_comedy", "CO_CURRICULAR", "EPIC", 30, """{"type":"count","source":"event","category":"cultural","threshold":1}"""),
            Badge("competitor", "Competitor", "Represent school in inter-school event", "sports_score", "CO_CURRICULAR", "EPIC", 30, """{"type":"count","source":"event","category":"inter_school","threshold":1}"""),
            // Character (4)
            Badge("good_samaritan", "Good Samaritan", "Teacher-awarded for helping behavior", "volunteer_activism", "CHARACTER", "RARE", 20, """{"type":"manual","awarded_by":"teacher"}"""),
            Badge("class_leader", "Class Leader", "Selected as class monitor/leader", "groups", "CHARACTER", "EPIC", 30, """{"type":"manual","awarded_by":"teacher"}"""),
            Badge("model_student", "Model Student", "Full term with zero disciplinary issues", "verified_user", "CHARACTER", "EPIC", 30, """{"type":"manual","awarded_by":"admin"}"""),
            Badge("team_player", "Team Player", "Participate in group project/activity", "handshake", "CHARACTER", "RARE", 20, """{"type":"manual","awarded_by":"teacher"}"""),
            // Health (3)
            Badge("health_conscious", "Health Conscious", "Complete annual health checkup", "health_and_safety", "HEALTH", "COMMON", 10, """{"type":"count","source":"health","threshold":1}"""),
            Badge("fit_kid", "Fit Kid", "BMI in healthy range for the year", "fitness_center", "HEALTH", "RARE", 20, """{"type":"manual","awarded_by":"admin"}"""),
            Badge("protected", "Protected", "All vaccinations up to date", "vaccines", "HEALTH", "COMMON", 10, """{"type":"manual","awarded_by":"admin"}"""),
            // Milestone (6)
            Badge("rising_star", "Rising Star", "Reach Level 5", "star", "MILESTONE", "RARE", 20, """{"type":"level","level":5}"""),
            Badge("scholar_badge", "Scholar", "Reach Level 10", "school", "MILESTONE", "EPIC", 30, """{"type":"level","level":10}"""),
            Badge("legend_badge", "Legend", "Reach Level 25", "workspace_premium", "MILESTONE", "LEGENDARY", 50, """{"type":"level","level":25}"""),
            Badge("grandmaster_badge", "Grandmaster", "Reach Level 50", "diamond", "MILESTONE", "MYTHIC", 100, """{"type":"level","level":50}"""),
            Badge("anniversary", "Anniversary", "1 year on the platform", "cake", "MILESTONE", "EPIC", 30, """{"type":"anniversary","years":1}"""),
            Badge("birthday_star", "Birthday Star", "Birthday badge (annual)", "cake", "MILESTONE", "COMMON", 10, """{"type":"birthday"}""")
        )

        badges.forEach { b ->
            GameBadgeDefinitionsTable.insert {
                it[GameBadgeDefinitionsTable.schoolId] = null
                it[GameBadgeDefinitionsTable.code] = b.code
                it[GameBadgeDefinitionsTable.name] = b.name
                it[GameBadgeDefinitionsTable.description] = b.desc
                it[GameBadgeDefinitionsTable.iconName] = b.icon
                it[GameBadgeDefinitionsTable.category] = b.category
                it[GameBadgeDefinitionsTable.rarity] = b.rarity
                it[GameBadgeDefinitionsTable.xpRequirement] = b.xp
                it[GameBadgeDefinitionsTable.criteriaJson] = b.criteria
                it[GameBadgeDefinitionsTable.isActive] = true
                it[GameBadgeDefinitionsTable.isSeasonal] = false
                it[GameBadgeDefinitionsTable.createdAt] = Instant.now()
            }
        }
        logger.info("GamificationSeeder: seeded ${badges.size} badge definitions")
    }

    // ── Progression paths ────────────────────────────────────────────────
    private fun seedProgressionPaths() {
        if (GameProgressionPathsTable.selectAll().count() > 0) return

        data class Path(
            val code: String, val name: String,
            val s1n: String, val s1x: Int, val s2n: String, val s2x: Int,
            val s3n: String, val s3x: Int, val s4n: String, val s4x: Int
        )

        val paths = listOf(
            Path("ACADEMIC", "Academic Path", "Beginner", 0, "Scholar", 500, "Subject Expert", 1500, "Academic Champion", 3000),
            Path("ATTENDANCE", "Attendance Path", "Present", 0, "Consistent", 200, "Iron Will", 600, "Unbreakable", 1500),
            Path("CO_CURRICULAR", "Co-Curricular Path", "Participant", 0, "Enthusiast", 150, "All-Rounder", 500, "Versatile Star", 1200),
            Path("CHARACTER", "Character Path", "Good Citizen", 0, "Role Model", 200, "Leader", 600, "Mentor", 1500),
            Path("DIGITAL", "Digital Engagement Path", "Newcomer", 0, "Active", 100, "Engaged", 400, "Power User", 1000)
        )

        paths.forEach { p ->
            GameProgressionPathsTable.insert {
                it[GameProgressionPathsTable.code] = p.code
                it[GameProgressionPathsTable.name] = p.name
                it[GameProgressionPathsTable.stage1Name] = p.s1n
                it[GameProgressionPathsTable.stage1Xp] = p.s1x
                it[GameProgressionPathsTable.stage2Name] = p.s2n
                it[GameProgressionPathsTable.stage2Xp] = p.s2x
                it[GameProgressionPathsTable.stage3Name] = p.s3n
                it[GameProgressionPathsTable.stage3Xp] = p.s3x
                it[GameProgressionPathsTable.stage4Name] = p.s4n
                it[GameProgressionPathsTable.stage4Xp] = p.s4x
                it[GameProgressionPathsTable.badgeId] = null
                it[GameProgressionPathsTable.createdAt] = Instant.now()
            }
        }
        logger.info("GamificationSeeder: seeded ${paths.size} progression paths")
    }

    // ── Titles ───────────────────────────────────────────────────────────
    private fun seedTitles() {
        if (GameTitlesTable.selectAll().count() > 0) return

        data class Title(val code: String, val name: String, val criteria: String, val icon: String)

        val titles = listOf(
            Title("the_bookworm", "The Bookworm", """{"type":"count","source":"library","threshold":10}""", "menu_book"),
            Title("math_wizard", "Math Wizard", """{"type":"count","source":"assessment","subject":"math","min_score":90,"threshold":5}""", "calculate"),
            Title("iron_will", "Iron Will", """{"type":"count","source":"attendance","status":"PRESENT","consecutive":true,"threshold":30}""", "whatshot"),
            Title("helping_hand", "Helping Hand", """{"type":"count","source":"badge","category":"CHARACTER","threshold":3}""", "volunteer_activism"),
            Title("quiz_master", "Quiz Master", """{"type":"count","source":"tutor","threshold":50}""", "psychology"),
            Title("rising_star_title", "Rising Star", """{"type":"level","level":5}""", "star"),
            Title("legend_title", "Legend", """{"type":"level","level":25}""", "workspace_premium"),
            Title("house_captain", "House Captain", """{"type":"house_captain"}""", "military_tech"),
            Title("mentor_title", "Mentor", """{"type":"mentor","active_mentees":3}""", "school"),
            Title("perfect_attendee", "Perfect Attendee", """{"type":"count","source":"attendance","status":"PRESENT","threshold":120}""", "verified")
        )

        titles.forEach { t ->
            GameTitlesTable.insert {
                it[GameTitlesTable.code] = t.code
                it[GameTitlesTable.name] = t.name
                it[GameTitlesTable.criteriaJson] = t.criteria
                it[GameTitlesTable.iconName] = t.icon
                it[GameTitlesTable.isActive] = true
                it[GameTitlesTable.createdAt] = Instant.now()
            }
        }
        logger.info("GamificationSeeder: seeded ${titles.size} titles")
    }

    // ── Motivation messages ──────────────────────────────────────────────
    private fun seedMotivationMessages() {
        if (GameMotivationMessagesTable.selectAll().count() > 0) return

        val messages = listOf(
            "low_xp_3_days" to "A quick 10-minute practice can earn you 15 XP today. Want to try?",
            "catch_up_active" to "You've got a Boost Active — all your XP is worth 1.5x right now!",
            "streak_breaking" to "Your 5-day streak needs you! One activity today keeps it alive.",
            "level_close" to "Just 30 XP to Level 4! One homework submission does it.",
            "badge_available" to "You're close to earning a badge — one more activity!",
            "after_low_assessment" to "Every expert was once a beginner. Try the AI Tutor practice!",
            "level_up" to "LEVEL UP! You're now Level {level} — {title}! New badges unlocked!",
            "badge_earned" to "Badge earned: {badge_name}!",
            "class_goal_contribution" to "You contributed {percent}% of your class's XP this week!",
            "streak_milestone" to "{days}-day streak! Comeback quest unlocked — double XP tomorrow!",
            "mentor_nudge" to "Your mentee {name} hasn't earned XP in 3 days. Send a shout-out!",
            "buddy_nudge" to "Your study buddy {name} is {xp} XP away from Level {level}. Help them!",
            "zero_xp" to "Your journey starts here! Complete your first activity to earn XP.",
            "zero_badges" to "40 badges waiting to be discovered! Start with First Steps.",
            "zero_streak" to "Today is Day 1. One activity starts your streak!",
            "welcome_back" to "Welcome back! Double XP is active for your first activity.",
            "max_level" to "You've reached the top! Help others rise as a Mentor!",
            "parent_level_up" to "{name} reached Level {level} — {title}!",
            "parent_badge_earned" to "{name} earned the {badge} badge!",
            "parent_streak" to "{name} is on a {days}-day streak! Keep encouraging them!",
            "parent_low_activity" to "{name} hasn't earned XP in 3 days. A little encouragement can help!",
            "parent_teacher_alert" to "{name}'s teacher says he's close to Level {level} — encourage him at home!"
        )

        messages.forEach { (key, text) ->
            GameMotivationMessagesTable.insert {
                it[GameMotivationMessagesTable.messageKey] = key
                it[GameMotivationMessagesTable.messageText] = text
                it[GameMotivationMessagesTable.language] = "en"
                it[GameMotivationMessagesTable.isActive] = true
                it[GameMotivationMessagesTable.createdAt] = Instant.now()
            }
        }
        logger.info("GamificationSeeder: seeded ${messages.size} motivation messages")
    }

    // ── Quest definitions ────────────────────────────────────────────────
    private fun seedQuests() {
        if (GameQuestDefinitionsTable.selectAll().count() > 0) return

        data class Quest(
            val code: String, val name: String, val desc: String,
            val type: String, val category: String, val xp: Int,
            val criteria: String, val scope: String, val duration: Int
        )

        val quests = listOf(
            // Daily
            Quest("daily_attend_all", "Perfect Attendance Today", "Attend all classes today", "DAILY", "ACADEMIC", 15, """{"type":"attendance_all_day"}""", "ALL", 24),
            Quest("daily_submit_homework", "Submit Homework", "Submit any pending homework today", "DAILY", "ACADEMIC", 20, """{"type":"count","source":"homework","threshold":1,"timeframe":"today"}""", "ALL", 24),
            Quest("daily_tutor_practice", "AI Tutor Practice", "Complete 1 AI Tutor practice", "DAILY", "ACADEMIC", 10, """{"type":"count","source":"tutor","threshold":1,"timeframe":"today"}""", "ALL", 24),
            Quest("daily_read_20", "Read for 20 Minutes", "Read a library book for 20 minutes", "DAILY", "CO_CURRICULAR", 10, """{"type":"count","source":"library","threshold":1,"timeframe":"today"}""", "ALL", 24),
            Quest("daily_help_friend", "Help a Friend", "Send a shout-out to a classmate", "DAILY", "CHARACTER", 10, """{"type":"count","source":"shoutout","threshold":1,"timeframe":"today"}""", "ALL", 24),
            // Weekly
            Quest("weekly_perfect_attendance", "Perfect Attendance Week", "Attend all classes this week", "WEEKLY", "CHARACTER", 75, """{"type":"attendance_all_week"}""", "ALL", 168),
            Quest("weekly_all_homework", "Homework Champion", "Submit all homework on time this week", "WEEKLY", "ACADEMIC", 50, """{"type":"count","source":"homework","on_time":true,"threshold":5,"timeframe":"week"}""", "ALL", 168),
            Quest("weekly_5_practices", "Practice Makes Perfect", "Complete 5 AI Tutor practices this week", "WEEKLY", "ACADEMIC", 40, """{"type":"count","source":"tutor","threshold":5,"timeframe":"week"}""", "ALL", 168),
            Quest("weekly_participate_event", "Get Involved", "Participate in any event this week", "WEEKLY", "CO_CURRICULAR", 30, """{"type":"count","source":"event","threshold":1,"timeframe":"week"}""", "ALL", 168),
            // Catch-up
            Quest("catchup_3_homework", "Comeback: Homework", "Complete 3 homework assignments this week", "CATCH_UP", "ACADEMIC", 100, """{"type":"count","source":"homework","threshold":3,"timeframe":"week"}""", "BOTTOM_25", 168),
            Quest("catchup_2_practices", "Comeback: Practice", "Complete 2 AI Tutor practices", "CATCH_UP", "ACADEMIC", 80, """{"type":"count","source":"tutor","threshold":2,"timeframe":"week"}""", "BOTTOM_25", 168),
            Quest("catchup_3_attendance", "Comeback: Attendance", "Attend 3 days this week", "CATCH_UP", "CHARACTER", 90, """{"type":"count","source":"attendance","status":"PRESENT","threshold":3,"timeframe":"week"}""", "BOTTOM_25", 168)
        )

        quests.forEach { q ->
            GameQuestDefinitionsTable.insert {
                it[GameQuestDefinitionsTable.schoolId] = null
                it[GameQuestDefinitionsTable.code] = q.code
                it[GameQuestDefinitionsTable.name] = q.name
                it[GameQuestDefinitionsTable.description] = q.desc
                it[GameQuestDefinitionsTable.questType] = q.type
                it[GameQuestDefinitionsTable.category] = q.category
                it[GameQuestDefinitionsTable.xpReward] = q.xp
                it[GameQuestDefinitionsTable.criteriaJson] = q.criteria
                it[GameQuestDefinitionsTable.targetScope] = q.scope
                it[GameQuestDefinitionsTable.durationHours] = q.duration
                it[GameQuestDefinitionsTable.isActive] = true
                it[GameQuestDefinitionsTable.createdAt] = Instant.now()
            }
        }
        logger.info("GamificationSeeder: seeded ${quests.size} quest definitions")
    }
}
