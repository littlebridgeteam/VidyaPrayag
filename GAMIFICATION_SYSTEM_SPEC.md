# GAMIFICATION SYSTEM — FULL END-TO-END SPEC & IMPLEMENTATION PLAN

Read this ENTIRE document before writing a single line of code.

This is NOT an isolated feature bolted onto the side of the app.
This is a SYSTEM that weaves through every existing feature — attendance,
homework, assessments, library, events, health, scholarships, AI tutor,
messaging — and adds a motivational layer on top.

The system is designed around ONE question:
  "Will this make a student want to try harder tomorrow?"

If the answer is no, the feature doesn't ship.

---

## TABLE OF CONTENTS

  0.  THE PHILOSOPHY — MOTIVATION, NOT SPAM
  1.  THE KILL SWITCH — ONE CLICK, GONE, APP STILL WORKS
  2.  ANTI-SPAM RULES — WHAT THIS SYSTEM WILL NEVER DO
  3.  THE STUDENT — WHO IS THIS FOR?
  4.  SYSTEM ARCHITECTURE — HOW IT WEAVES INTO EXISTING FEATURES
  5.  XP ENGINE — SIX CATEGORIES, FULL LIFE COVERAGE
  6.  LEVEL SYSTEM — 10 LEVELS, TITLES, NEVER LOSE A LEVEL
  7.  BADGE SYSTEM — 6 CATEGORIES, 5 RARITY TIERS, CRITERIA ENGINE
  8.  QUEST SYSTEM — DAILY, WEEKLY, SEASONAL
  9.  HOUSE SYSTEM — GUILDS, COLLECTIVE COMPETITION
  10. REWARDS SHOP — SPEND XP ON REAL SCHOOL REWARDS
  11. COMBO SYSTEM — CONSECUTIVE ACTIVITY MULTIPLIERS
  12. XP BOOSTS — TIME-LIMITED MULTIPLIERS
  13. LEADERBOARDS — NON-TOXIC, MULTI-DIMENSIONAL
  14. MENTOR & STUDY BUDDY — PEER MOTIVATION
  15. SHOUT-OUTS — PEER ENCOURAGEMENT
  16. CLASS GOALS — COLLECTIVE REWARDS
  17. TITLES & FLAIR — EARNABLE DISPLAY NAMES
  18. PROGRESSION PATHS — SKILL TREES
  19. SEASONAL EVENTS — LIMITED-EDITION BADGES
  20. CATCH-UP MECHANICS — FOR LOW XP STUDENTS
  21. SMART NOTIFICATIONS — MOTIVATIONAL, NOT ANNOYING
  22. GROWTH MINDSET MESSAGING — LANGUAGE THROUGHOUT
  23. TEACHER TOOLS — MANUAL MOTIVATION FROM CLASSROOM
  24. ADMIN CONTROLS — CONFIGURE, CUSTOMIZE, KILL SWITCH
  25. PARENT VISIBILITY — SEE CHILD'S GROWTH
  26. DATABASE SCHEMA — ALL TABLES
  27. SERVER ARCHITECTURE — SERVICES, ROUTES, HOOKS
  28. SHARED MODULE — KOTLIN MULTIPLATFORM
  29. UI INTEGRATION — WHERE IT APPEARS (NOT SPAMMY)
  30. WEBSITE ADMIN PANEL — KILL SWITCH + CONFIG
  31. EXECUTION PLAN — 8 PHASES
  32. VERIFICATION CHECKLIST

---

## 0. THE PHILOSOPHY — MOTIVATION, NOT SPAM

This system exists for ONE reason: to make students want to engage more
with their school life — academics, co-curriculars, character, health.

THE THREE LAWS:

LAW 1: NEVER DEMORALIZE
  The system never shows a student they are "bad" or "behind."
  Low XP = more opportunities, not shame. Every screen frames
  low performance as "growth opportunity" with concrete next steps.

LAW 2: NEVER SPAM
  This is NOT a mobile game that bombards you with pop-ups, forced
  animations, and "CLAIM YOUR REWARD!" overlays. Gamification is
  woven into existing screens — a badge appears in a corner, an XP
  bar sits in a profile, a level title shows next to the name.
  Notifications are rare, personalized, and actionable.

LAW 3: NEVER BLOCK
  Gamification NEVER blocks a user from doing their actual task.
  A student marking attendance doesn't get a pop-up. A parent
  checking fees doesn't see a level-up animation blocking the screen.
  XP is awarded silently in the background. The student sees it
  when they CHOOSE to look at their profile.

THE GOLDEN RULE:
  If a student disables gamification (or the admin kills it), the app
  must feel EXACTLY the same as before gamification existed. No empty
  spaces, no broken layouts, no "feature unavailable" messages.
  Gamification is a layer, not a scaffold.

---

## 1. THE KILL SWITCH — ONE CLICK, GONE, APP STILL WORKS

The admin can disable the ENTIRE gamification system with one toggle.

WHEN DISABLED:
  - All gamification API endpoints return 200 with { enabled: false }
  - The mobile app hides ALL gamification UI (XP bars, badges, levels,
    quests, houses, leaderboards, rewards shop — everything)
  - The app's layout adjusts seamlessly — no empty gaps, no "coming soon"
    placeholders, no broken navigation
  - XP is still silently tracked in the database (so if the admin
    re-enables later, historical XP is preserved)
  - Existing features (attendance, homework, assessments, etc.) work
    EXACTLY as before — they just skip the awardXp() call
  - The website admin panel hides the gamification section
  - No notifications about gamification are sent

IMPLEMENTATION:

Server-side:
  - Stored in AppConfigTable under key "flags" as JSON:
      "is_gamification_enabled": true/false
  - GET /api/v1/config/app-status already returns flags to the app.
  - New admin endpoint to toggle:
      PUT /api/v1/school/gamification/toggle
      Body: { enabled: boolean }
      Requires: JWT + school_admin role
  - All gamification routes check the flag first.

Client-side (mobile):
  - GamificationFlagHolder reads the flag from app-status on boot
  - All gamification UI components check:
      if (!gamificationEnabled) return // render nothing, no gap
  - The gamification tab/section is conditionally composed
  - NO gamification API calls when disabled

Client-side (website):
  - Settings page shows a "Gamification" card with toggle switch
  - When disabled, gamification admin section disappears from sidebar
  - Toggle calls PUT /api/v1/school/gamification/toggle

GRANULAR CONTROLS (within the kill switch):
  - is_gamification_enabled           (master kill switch)
  - gamification_leaderboards_enabled  (hide leaderboards only)
  - gamification_rewards_shop_enabled  (hide rewards shop only)
  - gamification_houses_enabled        (hide house system only)
  - gamification_quests_enabled        (hide quests only)
  - gamification_mentor_enabled        (hide mentor system only)

  If master is OFF, all subsystems are OFF regardless.
  If master is ON, individual subsystems can be toggled independently.

---

## 2. ANTI-SPAM RULES — WHAT THIS SYSTEM WILL NEVER DO

This is NOT a freemium mobile game. This is a school management app.

THE SYSTEM WILL NEVER:

1. NEVER show a full-screen pop-up for XP/badge/level-up.
   XP is awarded silently. A small toast (auto-dismiss 3s) may appear
   at the bottom, but it NEVER blocks interaction.

2. NEVER force an animation before letting the user proceed.
   No "Level Up! [Watch this 5-second animation]" screens.

3. NEVER send more than 1 gamification notification per day.
   And only if the student/parent has notifications enabled.
   Sent at a time the parent chose, not random.

4. NEVER show "CLAIM YOUR REWARD" buttons.
   Badges and XP are auto-claimed. No red dot anxiety.

5. NEVER use dark patterns.
   No "Your streak is about to break! Act NOW!" urgency pop-ups.
   No "Don't lose your progress!" guilt-trip messages.
   No "Your friends are ahead of you!" comparison anxiety.

6. NEVER show ads or sponsored content.
   The rewards shop only contains school-defined rewards.

7. NEVER block core app functionality.
   A parent can check fees, message teachers, view attendance
   without ever interacting with gamification.

8. NEVER show gamification on every screen.
   Gamification UI appears in specific, intentional places:
     - Student/child profile (XP bar, level, badges)
     - A dedicated "Progress" section (not on every tab)
     - A small level badge next to the student's name (optional)
   It does NOT appear on: fee payment, messaging, attendance
   marking, homework submission, settings, login, onboarding.

9. NEVER use red/orange for low performance.
   Green = good, blue = neutral, grey = locked.
   NEVER red for "behind" or "failing."

10. NEVER compare a student to named peers negatively.
    "Arjun has 500 more XP than you" = BANNED.
    "You're climbing! +120 XP this week" = CORRECT.

11. NEVER show a "0" without context.
    0 XP -> "Your journey starts here! Complete your first activity"
    0 badges -> "40 badges waiting to be discovered!"
    0 streak -> "Today is Day 1. One activity starts your streak!"

12. NEVER make gamification the primary focus of the app.
    The app's primary purpose is school management. Gamification
    is a background motivator. The home screen leads with
    attendance, homework, fees, messages — NOT with XP and badges.

---

## 3. THE STUDENT — WHO IS THIS FOR?

Indian school students, ages 6-18 (Grade 1 to Grade 12).
The PARENT operates the app on behalf of younger students (6-12).
Older students (13+) may use the app themselves if the school allows.

The student cares about:
- "Am I doing well?" (self-worth tied to school performance)
- "Can I get better?" (growth mindset, if nurtured)
- "Do my teachers notice my effort?" (recognition)
- "Am I as good as my friends?" (social comparison — handle carefully)

The student does NOT care about:
- Complex game mechanics they have to learn
- Reading long descriptions of how XP works
- Being reminded constantly that they're being tracked

DESIGN CALIBRATION:
- Gamification UI is simple: a level, an XP bar, badges, a streak.
- No tutorials needed. The student sees their level and understands.
- Everything is visual — icons, colors, progress bars — not text walls.
- The parent sees the same data and can encourage the child at home.
- Growth-mindset language everywhere. See Section 22.

---

## 4. SYSTEM ARCHITECTURE — HOW IT WEAVES INTO EXISTING FEATURES

Gamification is NOT a separate screen. It's a layer that hooks into
existing feature flows. Here's every integration point:

| EXISTING FEATURE          | GAMIFICATION HOOK                     |
|---------------------------|---------------------------------------|
| Attendance marking        | +10 XP for PRESENT, streak update     |
| Homework submission       | +15 XP on-time, +5 XP late            |
| Assessment published      | +50 XP (90%+), +25 (75%+), +10 (50%+) |
| AI Tutor practice         | +5 XP per practice                    |
| AI Tutor doubt asked      | +3 XP (curiosity reward)              |
| Library book issued       | +10 XP                                |
| Library book returned     | +15 XP on-time                        |
| Event registration        | +20 XP standard, +40 XP inter-school  |
| Health checkup            | +15 XP                                |
| Scholarship applied       | +30 XP                                |
| Scholarship approved      | +100 XP                               |
| PTM attended              | +20 XP (parent participation)         |
| Parent app open (daily)   | +2 XP + streak update                 |
| Shout-out sent            | +5 XP sender, +10 XP receiver         |
| Teacher manual award      | Variable (teacher chooses)            |
| Quest completed           | +quest reward XP                      |

HOOK IMPLEMENTATION PATTERN:

Every hook follows the same pattern — a single function call at the
end of an existing route handler, wrapped in try-catch:

```kotlin
// ... existing endpoint logic completes successfully ...
try {
    if (isGamificationEnabled(schoolId)) {
        GamificationService.awardXp(
            studentId = resolvedStudentId,
            schoolId = schoolId,
            amount = 10,
            reason = "Present for the day",
            source = XpSource.ATTENDANCE
        )
    }
} catch (e: Exception) {
    logger.warn("XP award failed for $studentId: ${e.message}")
    // XP failure MUST NEVER affect the parent operation
}
```

The awardXp() call is fire-and-forget from the caller's perspective.
It runs asynchronously, never blocks the response, and never throws
to the caller.

---

## 5. XP ENGINE — SIX CATEGORIES, FULL LIFE COVERAGE

XP is the currency. Students earn it across six categories covering
their entire school life — not just academics.

| CATEGORY          | XP RANGE | EXAMPLES                           |
|-------------------|----------|------------------------------------|
| Academic          | 3-50     | Attendance, homework, tests, tutor |
| Co-Curricular     | 10-40    | Library, events, sports            |
| Character         | 20-200   | Streaks, teacher-awarded, leadership|
| Health            | 10-15    | Checkup, BMI, vaccination          |
| Digital Engagement| 2-100    | App open, streaks, scholarship, PTM|
| Special/Milestone | 5-50     | First XP, level up, birthday       |

FULL XP TABLE:

ACADEMIC:
  Present for the day                    +10
  Homework on-time                       +15
  Homework late                          +5
  Assessment score >= 90%                +50
  Assessment score >= 75%                +25
  Assessment score >= 50%                +10
  AI Tutor practice completed            +5
  AI Tutor doubt asked                   +3

CO-CURRICULAR:
  Library book issued                    +10
  Library book returned on-time          +15
  Event registration (standard)          +20
  Inter-school event participation       +40
  Sports day participation               +30
  Cultural event participation           +25

CHARACTER:
  Perfect attendance week (5 days)       +50
  Perfect attendance month (20 days)    +200
  No disciplinary issues (term)         +100
  Teacher-awarded (helpful behavior)    +20-50
  Leadership role (class monitor)        +30

HEALTH:
  Health checkup completed               +15
  BMI in healthy range                   +10
  Vaccination up to date                 +10

DIGITAL ENGAGEMENT:
  Daily app open                         +2
  7-day streak                           +25
  30-day streak                         +100
  Scholarship application submitted      +30
  Scholarship approved                  +100
  PTM attended (parent)                  +20

SPECIAL / MILESTONE:
  First XP ever                           +5
  Level up (per level)            +level x 10
  New academic year start                +50
  Birthday                                +10
  Teacher manual award               variable

XP MULTIPLIERS (applied before awarding, stack multiplicatively, cap x5):
  Active boost event                     x boost.multiplier
  7+ day streak active                   +10% bonus
  Birthday (student's birthday)          x2 for that day
  Catch-up boost (bottom 25%)            x1.5 until reaches average
  House of the Month member              x1.25 for next week
  Combo active (4+ consecutive)          x1.5 for that activity

---

## 6. LEVEL SYSTEM — 10 LEVELS, TITLES, NEVER LOSE A LEVEL

Levels are the primary progression marker. XP is the input, levels
are the output. Students see their level prominently; XP is secondary.

THE LEVEL FLOOR RULE:
  You can NEVER lose a level. XP only goes up. Even if a student
  stops using the app for months, they return at the same level.
  There is no "XP decay" or "level reset." This is fundamental.

LEVEL CURVE:

  Level | XP Required | Title        | Icon
  ------|-------------|--------------|------
    1   |      0      | Beginner     | seedling
    2   |    100      | Explorer     | compass
    3   |    300      | Achiever     | star
    4   |    600      | Rising Star  | star
    5   |   1,000     | Scholar      | book
    6   |   1,500     | Expert       | graduation-cap
    7   |   2,200     | Master       | medal
    8   |   3,000     | Champion     | trophy
    9   |   4,000     | Legend       | crown
   10   |   5,500     | Grandmaster  | diamond

XP REQUIRED = cumulative total XP, not XP-since-last-level.
Level 10 is the current cap. When a student hits Level 10:
  - They get the "Grandmaster" title
  - They're offered the Mentor role
  - System shows: "You've reached the top! Help others rise."
  - Future levels (11+) can be added by admin without code changes

LEVEL-UP EXPERIENCE:
  - NO full-screen pop-up. NO forced animation.
  - A small toast at the bottom: "Level 5 — Scholar!" (auto-dismiss 3s)
  - The student's profile updates their level badge
  - A notification is queued (sent at parent's preferred time)
  - Level-up bonus XP (level x 10) is awarded silently
  - Any badges triggered by the level-up are checked

ADMIN CUSTOMIZATION:
  - Admin can change XP thresholds via LevelDefinitionsTable
  - Admin can rename titles (e.g. in Hindi)
  - Admin can add levels beyond 10
  - Default levels are seeded on first boot

---

## 7. BADGE SYSTEM — 6 CATEGORIES, 5 RARITY TIERS, CRITERIA ENGINE

Badges are collectible achievements. They're the "trophy case."

BADGE CATEGORIES (6):
  Academic, Attendance, Co-Curricular, Character, Health, Milestone

RARITY TIERS (5):

  Rarity     | Visual Treatment            | % of Students
  -----------|-----------------------------|--------------
  Common     | Grey border                 | ~80%+
  Rare       | Blue border + shimmer       | ~30%
  Epic       | Purple border + glow        | ~10%
  Legendary  | Gold border + animated      | ~3%
  Mythic     | Rainbow animated + exclusive| <1%

Locked badges show as silhouettes with rarity color hint.

DEFAULT BADGE CATALOG (40 badges):

ACADEMIC (8):
  First Steps          Common    Complete first assessment
  Top Scorer           Rare      Score 90%+ on any assessment
  Subject Master       Epic      5 assessments in one subject
  Homework Hero        Rare      10 on-time homework submissions
  Early Bird           Rare      Submit before due date 5 times
  Quiz Champion        Epic      Complete 20 AI Tutor practices
  Consistent Performer Epic      Score 75%+ on 10 assessments
  Perfect Score        Legendary Score 100% on any assessment

ATTENDANCE (4):
  Perfect Week         Rare      5 consecutive present days
  Iron Streak          Epic      30 consecutive present days
  Semester Champion    Legendary 90+ present days in a semester
  Unbreakable          Mythic    Full year, no absences

CO-CURRICULAR (5):
  Book Worm            Rare      Issue 5 library books
  Event Enthusiast     Rare      Register for 3 events
  Sports Star          Rare      Participate in sports day
  Stage Performer      Epic      Participate in cultural event
  Competitor           Epic      Represent school in inter-school event

CHARACTER (4):
  Good Samaritan       Rare      Teacher-awarded for helping behavior
  Class Leader         Epic      Selected as class monitor/leader
  Model Student        Epic      Full term with zero disciplinary issues
  Team Player          Rare      Participate in group project/activity

HEALTH (3):
  Health Conscious     Common    Complete annual health checkup
  Fit Kid              Rare      BMI in healthy range for the year
  Protected            Common    All vaccinations up to date

MILESTONE (6):
  Rising Star          Rare      Reach Level 5
  Scholar              Epic      Reach Level 10
  Legend               Legendary Reach Level 25
  Grandmaster          Mythic    Reach Level 50
  Anniversary          Epic      1 year on the platform
  Birthday Star        Common    Birthday badge (annual)

SEASONAL (10 — limited edition, see Section 19):
  Freedom Fighter, Grateful Student, Festival of Light,
  Constitution Keeper, Performer, Athlete, Exam Warrior,
  Summer Reader, New Year Star, Earth Guardian

CRITERIA ENGINE:
  Each badge has a criteria_json field defining the auto-award rule:

  { "type": "count", "source": "attendance", "status": "PRESENT",
    "consecutive": true, "threshold": 30 }

  { "type": "count", "source": "assessment", "min_score": 90,
    "threshold": 1 }

  { "type": "level", "level": 5 }

  { "type": "manual", "awarded_by": "teacher" }

  The BadgeCriteriaEvaluator checks all unearned badges after every
  XP award. If criteria is met, the badge is auto-awarded (silently).

ADMIN CUSTOMIZATION:
  - Admin can create custom badges with custom criteria
  - Admin can edit badge names, icons, descriptions
  - Admin can deactivate badges
  - Teacher can create class-specific badges (scoped to their class)

---

## 8. QUEST SYSTEM — DAILY, WEEKLY, SEASONAL

Quests are time-bound objectives with bonus XP. They give students
something specific to aim for.

DAILY QUESTS (reset every day at midnight):
  - System picks 3 from a pool, personalized per student
  - Pool: attend all classes, submit homework, complete 1 AI tutor
    practice, read for 20 min, help a friend
  - XP reward: 10-20 per quest
  - If not completed, they expire silently (no penalty)
  - New day = new quests

WEEKLY QUESTS (reset every Monday):
  - System picks 2 from a pool
  - Pool: perfect attendance all week, submit all homework on time,
    complete 5 AI tutor practices, participate in any event
  - XP reward: 30-75 per quest
  - If not completed, they expire silently

SEASONAL/SPECIAL QUESTS (time-limited, themed):
  - Tied to events (Independence Day, Diwali, Sports Week, Exam Prep)
  - Available for a defined window (e.g. Aug 1-15)
  - XP reward: 50-150 per quest
  - Completion may award a limited-edition badge
  - Missed = gone forever (collectible value)

QUEST ASSIGNMENT:
  - Daily quests assigned on first app open of the day
  - Weekly quests assigned on Monday's first app open
  - Seasonal quests assigned when event is active
  - Personalized: a student who never does AI tutor won't get
    "complete 5 AI tutor practices" — they'll get "attend all classes"
  - Bottom 25% students get CATCH-UP QUESTS (higher XP, easier tasks)

QUEST PROGRESS:
  - Tracked automatically from existing XP events
  - No manual "mark quest complete" — it's automatic
  - Student sees progress bar: "2/3 classes attended today"

QUEST UI:
  - Shown in the Progress section (NOT on home screen)
  - 3 cards: today's quests with progress bars
  - Completed quests show a checkmark + XP earned
  - Expired quests just disappear — no "you missed it" message

---

## 9. HOUSE SYSTEM — GUILDS, COLLECTIVE COMPETITION

Students are sorted into houses. Houses compete collectively.
This shifts competition from individual (stressful) to team (motivating).

DEFAULT HOUSES (admin can rename):
  Agni (Fire)     — Courage, passion
  Vayu (Air)      — Speed, freedom
  Prithvi (Earth) — Stability, strength
  Jal (Water)     — Adaptability, flow

ASSIGNMENT:
  - Auto-assigned on first enrollment, balanced by count
  - Admin can reassign students between houses
  - Admin can create custom houses (up to 6)
  - Admin can disable the house system (kill switch flag)

HOUSE POINTS:
  - Sum of all members' XP earned this academic year
  - Updated in real-time (cached, refreshed every 5 minutes)
  - Individual XP is separate from house points

HOUSE LEADERBOARD:
  - Shows house rankings (1st, 2nd, 3rd, 4th)
  - Shows total points + member count
  - Shown in the Progress section, NOT on home screen
  - No individual contributions shown (prevents "dragging us down")

HOUSE OF THE MONTH:
  - Winning house at end of each month
  - All members get x1.25 XP multiplier for next week
  - House gets a "House of the Month" banner
  - Individual members get a badge (monthly, collectible)

HOUSE CAPTAIN:
  - Highest XP student in each house
  - Gets "House Captain" title + special badge
  - If overtaken, title passes to new leader (no drama)

INTER-HOUSE COMPETITIONS:
  - Admin/teacher can trigger "House Battle" events
  - During battle week, all XP counts double for house points
  - Winning house gets bonus badges

---

## 10. REWARDS SHOP — SPEND XP ON REAL SCHOOL REWARDS

Students accumulate XP and spend it on real school rewards.
Spending XP does NOT reduce your level. Level is based on TOTAL XP
earned (lifetime). Current XP = total earned - total spent.

DEFAULT REWARD CATALOG (admin can customize):

  Reward                          | XP Cost | Fulfillment
  --------------------------------|---------|------------------
  Homework Pass (1 assignment)    |   300   | Teacher validates
  Lunch Queue Skip (1 day)        |   150   | School admin
  Choose Your Seat (1 week)       |   500   | Class teacher
  Extra Library Book (1 issue)    |   200   | Library auto
  School Merch (badge/sticker)    |   800   | Admin fulfills
  Free Dress Day (1 day)          |   600   | Admin approves
  Class Party Fund (whole class)  |  5,000  | Admin (collective)
  1-on-1 with favorite teacher    |  1,000  | Schedule via app
  Feature on Wall of Fame         |  1,500  | Digital + physical
  Name in school newsletter       |  1,200  | Admin publishes

REDEMPTION FLOW:
  1. Student/parent taps "Redeem" on a reward
  2. XP is deducted from current balance (not from level)
  3. Redemption request goes to admin/teacher for approval
  4. Status: PENDING -> APPROVED -> FULFILLED (or REJECTED)
  5. Student sees: "Request sent! Your teacher will approve it."
  6. On approval: "Approved! Show this to your teacher." (with QR code)
  7. On rejection: "Not available right now. Try another reward!"
     (NEVER "you don't deserve this" — always "not available")

ADMIN CONTROLS:
  - Add/remove/edit rewards
  - Set stock limits (e.g. only 5 homework passes per month)
  - Approve/reject redemptions from web or app
  - Disable the rewards shop (kill switch flag)

ANTI-SPAM:
  - The shop is in the Progress section, NOT on home screen
  - No "You have enough XP for a reward!" notifications
  - No red dots on the shop icon

---

## 11. COMBO SYSTEM — CONSECUTIVE ACTIVITY MULTIPLIERS

Combos reward consistency, not intensity.

COMBO TYPES:
  Homework Combo:  3 on-time submissions in a row -> 4th = x1.5 XP
  Attendance Combo: 5 days = x1.1, 10 days = x1.25, 20 days = x1.5
  Study Combo:     3 days of AI tutor practice in a row -> 4th = x1.5
  Reading Combo:   3 library books returned on time in a row -> 4th = x1.5

COMBO RULES:
  - Combo resets on break (missed day, late submission, absent)
  - Combo is per-type (homework combo doesn't affect attendance XP)
  - Combo multiplier applied AFTER all other multipliers
  - Shown as a small "x3" indicator in the Progress section
  - NO pop-up when combo increases. NO notification.
  - Combo breaking is SILENT — no "you broke your combo!" message

---

## 12. XP BOOSTS — TIME-LIMITED MULTIPLIERS

Boosts are time-limited XP multipliers, activated by admins or auto.

BOOST TYPES:
  Double XP Weekend       Admin activates   All XP x2 (Sat-Sun)
  Exam Prep Booster       Auto (2 weeks      Assessment XP x1.5
                          before exams)
  House Winner Boost      Auto (monthly)     Members x1.25 for 1 week
  Streak Multiplier       Auto (7-day        +10% on all XP
                          streak active)
  Birthday Boost          Auto (birthday)    All XP x2 for that day
  Welcome Back            Auto (inactive     x1.5 for first day back
                          7+ days, returns)
  Catch-Up Boost          Auto (bottom 25%)  x1.5 until reaches average

BOOST VISIBILITY:
  - Active boosts shown as a small banner in the Progress section
  - "Boost Active: 1.5x XP until you reach class average!"
  - NO push notification when a boost activates (anti-spam)
  - NO pop-up. Just visible when student checks their profile.

BOOST STACKING:
  - Multipliers stack multiplicatively, capped at x5 total
  - Example: birthday (x2) + catch-up (x1.5) + combo (x1.5) = x4.5

---

## 13. LEADERBOARDS — NON-TOXIC, MULTI-DIMENSIONAL

Leaderboards are the most dangerous part of gamification. Done wrong,
they demoralize the bottom 80%. Done right, they motivate without shame.

LEADERBOARD TYPES:

  Personal Progress     "You this week vs you last week"
                        Always positive. Always visible. Default view.

  Class Leaderboard     Only TOP 5 shown publicly.
                        Everyone else: "Your position: climbing!"
                        No exact rank below 5th.

  House Leaderboard     Team-based. Shows house points, not individual.
                        Reduces shame — it's a team effort.

  Weekly Hot Shot       "Most XP earned THIS WEEK" (not total)
                        Resets every Monday. Everyone starts equal.
                        A low-total student CAN win this.

  Rising Star           "Biggest improvement this month"
                        Specifically rewards growth, not absolute XP.

  Most Helpful          "Most shout-outs sent"
                        Rewards kindness, not academics.

  Badge Collector       "Most diverse badges earned"
                        Rewards variety, not grind.

  Friends Circle        Parent-selected peer group (opt-in)
                        Parent chooses 3-5 classmates to compare.
                        Only visible to that parent.

WHAT WE NEVER SHOW:
  - Full ranking from #1 to #50
  - "Below average" labels
  - Red/orange for low XP
  - "Needs improvement" tags
  - Comparing to named peers negatively
  - Last place highlighted

PRIVACY:
  - Parent/student can opt out of ALL public leaderboards
  - Opt-out is a one-way toggle (can always rejoin)
  - Even opted-out students see their OWN progress
  - Opted-out students don't appear in anyone's leaderboard

ADMIN CONTROLS:
  - Admin can disable leaderboards entirely (kill switch flag)
  - Admin can choose which leaderboard types are visible
  - Admin can set leaderboard scope (class/grade/school)

---

## 14. MENTOR & STUDY BUDDY — PEER MOTIVATION

This is the "ask them to help their colleagues" mechanism.
High-XP students help low-XP students. Both benefit.

MENTOR SYSTEM:
  - Level 5+ students can become mentors for Level 1-2 students
  - System auto-suggests mentor pairings (admin/teacher approves)
  - Mentor earns +25 XP per mentee milestone:
      Mentee reaches Level 2 -> +25 XP
      Mentee completes first quest -> +25 XP
      Mentee earns first badge -> +25 XP
      Mentee reaches Level 3 -> +25 XP
  - Mentor sees: "Your mentee Arjun just earned his first badge! +25 XP"
  - Mentee sees: "Your mentor Priya completed 50 quests — follow her lead!"
  - A mentor can have max 3 mentees
  - Teacher can assign/unassign mentors

STUDY BUDDY:
  - Students pair up for homework/assignments
  - Teacher can assign pairs OR students choose (teacher approves)
  - Both submit on time -> both get +10 XP bonus
  - If one submits and other doesn't:
      Submitter gets XP normally
      Non-submitter gets gentle nudge: "Your study buddy Rohan submitted!
      You can too — +15 XP waiting"
  - Study buddy pairs reset monthly (new pairs = new friendships)

ANTI-SPAM:
  - Mentor/mentee notifications sent to MENTORS ONLY
  - Never sent to the whole class
  - Never says "Arjun is failing" — always "Arjun could use encouragement"
  - Max 1 mentor notification per week

---

## 15. SHOUT-OUTS — PEER ENCOURAGEMENT

Students send positive messages to classmates. Template-based,
teacher-moderated, no negative messages possible.

SHOUT-OUT TEMPLATES (student picks one, can't free-type):
  "Great job on the quiz!"
  "You're improving so much!"
  "Thanks for helping me in class!"
  "Your presentation was awesome!"
  "You're a great study buddy!"
  "Keep it up, you've got this!"

XP:
  Sending a shout-out: +5 XP for sender (max 3 per day)
  Receiving a shout-out: +10 XP for receiver (max 5 per day)

MODERATION:
  - Teacher sees all shout-outs in their class
  - Teacher can delete inappropriate ones (template-based, so unlikely)
  - Teacher can disable shout-outs for their class
  - Admin can disable shout-outs school-wide (kill switch flag)

VISIBILITY:
  - Shout-outs appear on a "Class Wall" (opt-in per student)
  - Student can hide their shout-outs from the public wall
  - Shout-outs always visible to the student who received them
  - NO notification for shout-outs (anti-spam)

---

## 16. CLASS GOALS — COLLECTIVE REWARDS

The whole class works together toward a shared reward.
This makes high performers want to help low performers.

GOAL TYPES:
  Class XP Goal:      "Class 7B reaches 10,000 XP" -> Movie day
  Attendance Goal:    "100% attendance for a week" -> No homework Friday
  Participation Goal: "Every student in Sports Day" -> Class badge + XP
  Homework Goal:      "100% homework submission this week" -> Class party

HOW IT MOTIVATES LOW PERFORMERS:
  - "Your class needs YOU — just 200 XP from you to help reach the goal!"
  - High performers: "Come on Arjun, let's get you to 200 XP!"
  - When goal is reached: EVERYONE gets the reward, even low XP students
  - This creates peer support, not peer pressure

TEACHER ROLE:
  - Teacher sets class goals (from templates or custom)
  - Teacher defines the reward
  - Teacher tracks progress
  - Teacher marks goal as "Achieved!" and triggers reward

UI:
  - Class goal shown as a progress bar in the Progress section
  - "Class 7B: 8,200 / 10,000 XP toward Movie Day!"
  - Individual contribution: "You've contributed 450 XP (5.5%)"
  - NO "you contributed the least" messaging

---

## 17. TITLES & FLAIR — EARNABLE DISPLAY NAMES

Titles are displayed next to the student's name. They're status symbols
that students earn through specific achievements.

DEFAULT TITLES:
  "The Bookworm"       — Read 10 library books
  "Math Wizard"        — Score 90%+ in 5 math assessments
  "Iron Will"          — 30-day attendance streak
  "Helping Hand"       — 3 teacher-awarded character badges
  "Quiz Master"        — Complete 50 AI Tutor practices
  "Rising Star"        — Reach Level 5
  "Legend"             — Reach Level 25
  "House Captain"      — Top XP in house
  "Mentor"             — Active mentor with 3 mentees
  "Perfect Attendee"   — Full semester, no absences

USAGE:
  - Student picks ONE active title to display
  - Stored on StudentStatsTable.active_title
  - Shown next to name in: profile, class wall, leaderboards (if visible)
  - Parent sees the child's title in the parent portal
  - Teacher sees titles in class roster
  - Titles can be changed anytime (no cooldown)

---

## 18. PROGRESSION PATHS — SKILL TREES

Multiple tracks a student progresses through simultaneously.
Each path has its own XP sub-counter and milestones.

PATHS:
  Academic Path:     Beginner -> Scholar -> Subject Expert -> Academic Champion
  Attendance Path:   Present -> Consistent -> Iron Will -> Unbreakable
  Co-Curricular:     Participant -> Enthusiast -> All-Rounder -> Versatile Star
  Character Path:    Good Citizen -> Role Model -> Leader -> Mentor
  Digital Path:      Newcomer -> Active -> Engaged -> Power User

Each path has 4 stages. Each stage requires a path-specific XP threshold.
Path XP is a subset of total XP (tagged by category).

Path completion = special badge + title unlock.
Students see all paths progressing in parallel — a "skill tree" view
in the Progress section.

This shows students they're growing in MULTIPLE dimensions, not just
one number. A student weak in academics but strong in co-curriculars
sees their Co-Curricular path advancing — motivation through strength.

---

## 19. SEASONAL EVENTS — LIMITED-EDITION BADGES

Tied to Indian academic calendar and cultural events.
Limited-edition badges are only earnable during the event window.
Missed = gone forever (collectible value).

EVENT CALENDAR:

  Event              | Period          | Badge               | Quest
  -------------------|-----------------|---------------------|--------------------
  Independence Day   | Aug 1-15        | Freedom Fighter     | Patriotic quiz
  Teacher's Day      | Sep 1-5         | Grateful Student    | Thank-you message
  Diwali             | Festival week   | Festival of Light   | Digital card
  Republic Day       | Jan 1-26        | Constitution Keeper | Civics quiz
  Annual Day         | School-specific | Performer           | Participate
  Sports Day         | School-specific | Athlete             | 2+ events
  Exam Season        | Pre-exam 2 wk   | Exam Warrior        | 10 practice tests
  Summer Break       | Vacation        | Summer Reader       | Read 5 books
  New Academic Year  | April           | New Year Star       | First week activities
  Environment Day    | June 5          | Earth Guardian       | Eco activity

ADMIN CONTROLS:
  - Admin can activate/deactivate events
  - Admin can create custom events
  - Admin can set event duration
  - System auto-activates events based on calendar (if not overridden)

---

## 20. CATCH-UP MECHANICS — FOR LOW XP STUDENTS

When a student falls behind, the system gives them MORE ways to earn,
not fewer. This is the core of the motivation philosophy.

MECHANISMS:

  XP Acceleration      Students below class average get x1.5 XP on all
                       activities until they reach the average.
                       Shown as "Boost Active!" not "You're behind"

  Comeback Quests      Special quests only visible to bottom 25% —
                       higher XP rewards, easier tasks
                       "Complete 3 homework assignments -> +100 XP"

  Second Wind          Inactive 7+ days -> "Welcome Back" quest with
                       double XP for first activity

  Streak Grace         Missing a day doesn't reset streak to 0 —
                       it pauses. "2 days missed — your streak is
                       waiting! Come back today to keep it alive"

  Catch-Up Sunday      Every Sunday, bottom 25% students get
                       x2 XP on any activity

  Level Floor          You can NEVER lose a level. XP only goes up.

VISIBILITY:
  - Catch-up boosts shown as "Boost Active!" — never "you're behind"
  - Comeback quests appear alongside normal quests (no "special needs" label)
  - The student doesn't know they're in the "bottom 25%" — they just see
    a boost and easier quests

---

## 21. SMART NOTIFICATIONS — MOTIVATIONAL, NOT ANNOYING

Notifications are RARE, PERSONALIZED, and ACTIONABLE.
Max 1 gamification notification per day. Only if notifications enabled.
Sent at the parent's preferred time (from notification preferences).

WHEN XP IS LOW:
  No XP in 3 days -> "A quick 10-minute practice can earn you 15 XP today."
  Bottom 25% -> "You've got a Boost Active — all your XP is worth 1.5x right now!"
  Streak about to break -> "Your 5-day streak needs you! One activity today keeps it alive."
  Level close to next -> "Just 30 XP to Level 4! One homework submission does it."
  Badges available -> "You're close to earning 'Book Worm' — 1 more library book!"
  After low assessment -> "Every expert was once a beginner. Try the AI Tutor practice!"

WHEN XP IS HIGH:
  Level up -> "LEVEL UP! You're now Level 5 — Scholar! New badges unlocked!"
  New badge -> "Badge earned: Iron Streak! 30 days of perfect attendance."
  Class goal contribution -> "You contributed 15% of your class's XP this week!"
  Streak milestone -> "7-day streak! Comeback quest unlocked — double XP tomorrow!"

WHEN A CLASSMATE IS LOW (peer nudge — mentors only):
  "Your mentee Arjun hasn't earned XP in 3 days. Send him a shout-out!"
  "Your study buddy Rohan is 50 XP away from Level 3. Help him get there!"
  NEVER: "Arjun is failing" or "Arjun is last in class"
  ALWAYS: "Arjun could use your encouragement"

NOTIFICATION RULES:
  - Max 1 per day
  - Sent at parent's preferred notification time
  - Can be disabled entirely in notification preferences
  - Never sent during school hours (8am-3pm) — respect class time
  - Never sent after 9pm — respect family time
  - Quiet on weekends unless parent opted in

---

## 22. GROWTH MINDSET MESSAGING — LANGUAGE THROUGHOUT

Every piece of system-generated text uses growth-mindset language.
This is not optional — it's hardcoded into the message templates.

  Fixed Mindset               | Growth Mindset
  ----------------------------|--------------------------------------
  "You failed"                | "You're learning — try again!"
  "You're behind"             | "You're on your way — keep going!"
  "You're last"               | "You're climbing the ranks!"
  "Not good enough"           | "You're improving every day!"
  "You need to catch up"      | "Bonus XP is waiting for you!"
  "Weak performance"          | "Growth opportunity detected!"
  "Below average"             | "Your breakthrough is coming!"
  "You missed your streak"    | "Your streak is waiting — pick it up today!"
  "You don't have enough XP"  | "Keep earning — you're getting closer!"
  "You haven't earned badges" | "40 badges waiting to be discovered!"

Messages stored in MotivationMessagesTable so admin can customize
(e.g. translate to Hindi, Marathi, etc.).

---

## 23. TEACHER TOOLS — MANUAL MOTIVATION FROM CLASSROOM

Teachers can manually motivate students. These tools appear in the
teacher portal, integrated into existing class/student views.

TOOLS:

  Encourage Button
    - On any student in their class, teacher taps "Encourage"
    - Student gets +20 XP + notification: "Your teacher believes in you!"
    - Max 3 per student per week (prevents overuse)
    - Shown as a heart icon next to student name in class roster

  Spotlight Award
    - Teacher highlights a student's improvement in class
    - +50 XP + "Spotlight" badge (rare)
    - Max 1 per class per week
    - Student sees: "You're in the Spotlight! Your teacher noticed your improvement!"

  Custom Badge Creator
    - Teacher creates class-specific badges
    - Examples: "Most Curious", "Best Team Player", "Most Improved"
    - Teacher manually awards to students
    - Scoped to that teacher's class only

  Class Pep Talk
    - Teacher triggers a class-wide XP boost for the day
    - All students in class get x1.5 XP for 24 hours
    - Max 1 per week per class
    - Students see: "Your teacher activated a Class Boost! 1.5x XP today!"

  Individual Quest Assignment
    - Teacher assigns a custom quest to a struggling student
    - "Complete 2 practice tests this week" -> +50 XP
    - Only visible to that student (not shown to class)

  Parent Alert
    - Teacher flags a student for parent nudge
    - "Aarav is close to Level 3 — encourage him at home!"
    - Sent as a regular notification to the parent
    - Always positive framing

  Mentor Assignment
    - Teacher assigns/unassigns mentors and study buddies
    - System suggests pairings, teacher confirms

  Study Buddy Assignment
    - Teacher pairs students for homework
    - System suggests based on complementary strengths

  Shout-Out Moderation
    - Teacher sees all shout-outs in their class
    - Can delete inappropriate ones
    - Can disable shout-outs for their class

  Class Goal Management
    - Teacher sets class goals from templates or custom
    - Defines the reward, marks goal as achieved

  Gamification Overview (Teacher)
    - Class leaderboard (top 5)
    - Students who need encouragement (bottom 25% — PRIVATE
      to teacher only, NEVER to students)
    - Recent badge earners in class
    - Active quests progress

TEACHER PORTAL INTEGRATION:
  - These tools appear in EXISTING teacher screens, not a separate tab
  - "Encourage" button is on the student detail screen
  - "Spotlight" is on the class roster
  - "Class Pep Talk" is on the class home screen
  - "Mentor Assignment" is on the class roster
  - "Gamification Overview" is a small card on the class home screen
  - NO separate gamification tab — it's woven into existing views

---

## 24. ADMIN CONTROLS — CONFIGURE, CUSTOMIZE, KILL SWITCH

Admin has full control. Available in BOTH mobile admin app AND website.

MASTER KILL SWITCH:
  - One toggle: "Enable Gamification" ON/OFF
  - When OFF: entire system disappears from all apps
  - XP is still tracked silently (preserved for re-enable)
  - All gamification API endpoints return { enabled: false }

GRANULAR TOGGLES (only visible when master is ON):
  - Leaderboards ON/OFF
  - Rewards Shop ON/OFF
  - House System ON/OFF
  - Quests ON/OFF
  - Mentor System ON/OFF
  - Shout-Outs ON/OFF
  - Seasonal Events ON/OFF
  - Class Goals ON/OFF
  - Combos ON/OFF
  - XP Boosts ON/OFF

CONFIGURATION:
  - Level thresholds (XP required per level)
  - Level titles (rename in local language)
  - Badge catalog (add, edit, deactivate custom badges)
  - XP amounts per action (e.g. attendance = 10 XP, change to 15)
  - Reward catalog (add, edit, remove, set stock limits)
  - House names and count
  - Quest pools (add custom quest templates)
  - Seasonal events (activate, deactivate, create custom)
  - Notification message templates (customize, translate)
  - Leaderboard types (enable/disable individual types)
  - Catch-up boost threshold (default 25%, change to 30%)

ANALYTICS (admin dashboard):
  - Total XP awarded (school-wide, per category)
  - Badge distribution (which badges are most/least earned)
  - Level distribution (how many students at each level)
  - Engagement metrics (daily active, quest completion rate)
  - Reward redemption stats
  - House points standings
  - Top performers (for recognition, not shaming others)
  - Students who need attention (bottom 25% — PRIVATE to admin)

---

## 25. PARENT VISIBILITY — SEE CHILD'S GROWTH

Parents see their child's gamification profile in the parent portal.

WHAT PARENTS SEE:
  - Child's current level + title
  - XP bar (progress to next level)
  - Total XP earned
  - Streak count
  - Badges earned (with rarity)
  - Locked badges (silhouettes — "discover what's possible")
  - Active quests (today's + this week's)
  - Active boosts ("Your child has 1.5x XP active!")
  - Recent XP history (last 10 transactions)
  - House assignment + house ranking
  - Class goal progress
  - Reward shop (parent can redeem on child's behalf)
  - Progression paths (all 5 paths with current stage)

WHAT PARENTS DON'T SEE:
  - Other students' XP (unless in friends circle, opt-in)
  - Class leaderboard (unless admin enables and parent opts in)
  - Other students' badges
  - Mentor/mentee details (privacy)
  - Shout-outs from other students (only their child's received ones)

PARENT NOTIFICATIONS:
  - Level up: "Aarav reached Level 5 — Scholar!"
  - New badge: "Aarav earned the 'Perfect Week' badge!"
  - Streak milestone: "Aarav is on a 7-day streak! Keep encouraging him!"
  - Low activity: "Aarav hasn't earned XP in 3 days. A little encouragement can help!"
  - Teacher alert: "Aarav's teacher says he's close to Level 3 — encourage him at home!"

---

## 26. DATABASE SCHEMA — ALL TABLES

All tables prefixed with "game_" to separate from existing schema.
Registered in DatabaseFactory.allTables.
Migration file: docs/db/migration_100_gamification.sql

### game_xp_ledger
| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| student_id | UUID FK | students.id |
| school_id | UUID FK | schools.id |
| amount | INTEGER | XP amount (positive for earn, negative for spend) |
| reason | TEXT | Human-readable: "Present for the day" |
| source | VARCHAR(32) | ATTENDANCE/HOMEWORK/ASSESSMENT/TUTOR/LIBRARY/EVENT/HEALTH/SCHOLARSHIP/PTM/APP_OPEN/SHOUTOUT/TEACHER_AWARD/QUEST/LEVEL_BONUS/BIRTHDAY/REWARD_SPEND/CATCH_UP/CUSTOM |
| category | VARCHAR(16) | ACADEMIC/CO_CURRICULAR/CHARACTER/HEALTH/DIGITAL/MILESTONE |
| multiplier | REAL DEFAULT 1.0 | Multiplier applied (for audit) |
| created_at | TIMESTAMP | |

### game_student_stats
| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| student_id | UUID UNIQUE FK | students.id |
| school_id | UUID FK | schools.id |
| total_xp | INTEGER DEFAULT 0 | Lifetime XP earned |
| current_xp | INTEGER DEFAULT 0 | Spendable balance |
| current_level | INTEGER DEFAULT 1 | |
| streak_days | INTEGER DEFAULT 0 | |
| last_active_date | DATE NULLABLE | |
| active_title | VARCHAR(64) NULLABLE | |
| house_id | UUID NULLABLE FK | game_houses.id |
| catch_up_active | BOOLEAN DEFAULT FALSE | |
| updated_at | TIMESTAMP | |

### game_level_definitions
| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| school_id | UUID NULLABLE | NULL = global default |
| level | INTEGER | |
| xp_required | INTEGER | Cumulative |
| title | VARCHAR(64) | |
| icon_name | VARCHAR(32) | |
| is_active | BOOLEAN DEFAULT TRUE | |
| created_at | TIMESTAMP | |
| | | UNIQUE(school_id, level) |

### game_badge_definitions
| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| school_id | UUID NULLABLE | NULL = global |
| code | VARCHAR(64) UNIQUE | |
| name | VARCHAR(128) | |
| description | TEXT | |
| icon_name | VARCHAR(32) | Material symbol |
| category | VARCHAR(16) | ACADEMIC/ATTENDANCE/CO_CURRICULAR/CHARACTER/HEALTH/MILESTONE/SEASONAL |
| rarity | VARCHAR(16) | COMMON/RARE/EPIC/LEGENDARY/MYTHIC |
| xp_requirement | INTEGER DEFAULT 0 | XP bonus on earning |
| criteria_json | TEXT | JSON: auto-award rules |
| is_active | BOOLEAN DEFAULT TRUE | |
| is_seasonal | BOOLEAN DEFAULT FALSE | |
| available_from | DATE NULLABLE | For seasonal |
| available_until | DATE NULLABLE | For seasonal |
| created_at | TIMESTAMP | |

### game_student_badges
| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| student_id | UUID FK | students.id |
| badge_id | UUID FK | game_badge_definitions.id |
| earned_at | TIMESTAMP | |
| awarded_by | UUID NULLABLE FK | app_users.id (manual) |
| | | UNIQUE(student_id, badge_id) |

### game_houses
| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| school_id | UUID FK | schools.id |
| name | VARCHAR(64) | |
| icon_name | VARCHAR(32) | |
| color | VARCHAR(16) | Hex for UI |
| motto | TEXT NULLABLE | |
| created_at | TIMESTAMP | |
| | | UNIQUE(school_id, name) |

### game_student_house_assignments
| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| student_id | UUID FK | students.id |
| house_id | UUID FK | game_houses.id |
| school_id | UUID FK | schools.id |
| assigned_at | TIMESTAMP | |
| | | UNIQUE(student_id, school_id) |

### game_quest_definitions
| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| school_id | UUID NULLABLE | NULL = global template |
| code | VARCHAR(64) UNIQUE | |
| name | VARCHAR(128) | |
| description | TEXT | |
| quest_type | VARCHAR(16) | DAILY/WEEKLY/SEASONAL/CATCH_UP/CUSTOM |
| category | VARCHAR(16) | Which XP category |
| xp_reward | INTEGER | |
| criteria_json | TEXT | Completion rules |
| target_scope | VARCHAR(16) | ALL/BOTTOM_25/SPECIFIC_STUDENT/CLASS |
| duration_hours | INTEGER | Active duration |
| is_active | BOOLEAN DEFAULT TRUE | |
| created_at | TIMESTAMP | |

### game_student_quests
| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| student_id | UUID FK | students.id |
| quest_id | UUID FK | game_quest_definitions.id |
| school_id | UUID FK | schools.id |
| progress | INTEGER DEFAULT 0 | |
| target | INTEGER | From criteria_json |
| completed | BOOLEAN DEFAULT FALSE | |
| completed_at | TIMESTAMP NULLABLE | |
| expires_at | TIMESTAMP | |
| created_at | TIMESTAMP | |
| | | UNIQUE(student_id, quest_id, expires_at) |

### game_xp_boosts
| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| school_id | UUID FK | schools.id |
| boost_type | VARCHAR(32) | DOUBLE_XP_WEEKEND/EXAM_PREP/HOUSE_WINNER/STREAK/BIRTHDAY/WELCOME_BACK/CATCH_UP/CLASS_PEP_TALK/CUSTOM |
| multiplier | REAL | |
| target_scope | VARCHAR(16) | ALL/HOUSE/CLASS/STUDENT |
| target_id | UUID NULLABLE | Depends on scope |
| starts_at | TIMESTAMP | |
| ends_at | TIMESTAMP | |
| is_active | BOOLEAN DEFAULT TRUE | |
| created_at | TIMESTAMP | |

### game_reward_catalog
| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| school_id | UUID FK | schools.id |
| name | VARCHAR(128) | |
| description | TEXT | |
| icon_name | VARCHAR(32) | |
| xp_cost | INTEGER | |
| stock_limit | INTEGER NULLABLE | NULL = unlimited |
| stock_remaining | INTEGER NULLABLE | |
| fulfillment_role | VARCHAR(16) | TEACHER/ADMIN/LIBRARIAN |
| is_active | BOOLEAN DEFAULT TRUE | |
| created_at | TIMESTAMP | |

### game_reward_redemptions
| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| student_id | UUID FK | students.id |
| reward_id | UUID FK | game_reward_catalog.id |
| school_id | UUID FK | schools.id |
| xp_spent | INTEGER | |
| status | VARCHAR(16) | PENDING/APPROVED/REJECTED/FULFILLED |
| qr_code | TEXT NULLABLE | Generated on approval |
| approved_by | UUID NULLABLE FK | app_users.id |
| approved_at | TIMESTAMP NULLABLE | |
| fulfilled_at | TIMESTAMP NULLABLE | |
| created_at | TIMESTAMP | |

### game_class_goals
| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| school_id | UUID FK | schools.id |
| class_id | UUID NULLABLE FK | school_classes.id |
| class_name | VARCHAR(32) NULLABLE | For classes without class_id |
| section | VARCHAR(8) NULLABLE | |
| goal_type | VARCHAR(16) | XP_TOTAL/ATTENDANCE_PERFECT/PARTICIPATION_ALL/HOMEWORK_100 |
| target | INTEGER | |
| current_progress | INTEGER DEFAULT 0 | |
| reward | TEXT | Description |
| completed | BOOLEAN DEFAULT FALSE | |
| completed_at | TIMESTAMP NULLABLE | |
| deadline | DATE NULLABLE | |
| created_by | UUID FK | app_users.id (teacher) |
| created_at | TIMESTAMP | |

### game_shoutouts
| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| sender_id | UUID FK | students.id |
| receiver_id | UUID FK | students.id |
| school_id | UUID FK | schools.id |
| template_id | INTEGER | Which template |
| message | TEXT | Template text |
| is_public | BOOLEAN DEFAULT TRUE | Visible on class wall |
| is_deleted | BOOLEAN DEFAULT FALSE | Teacher moderation |
| created_at | TIMESTAMP | |

### game_mentor_assignments
| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| mentor_id | UUID FK | students.id |
| mentee_id | UUID FK | students.id |
| school_id | UUID FK | schools.id |
| assigned_by | UUID FK | app_users.id (teacher) |
| is_active | BOOLEAN DEFAULT TRUE | |
| created_at | TIMESTAMP | |
| | | UNIQUE(mentor_id, mentee_id, school_id) |

### game_study_buddy_pairs
| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| student1_id | UUID FK | students.id |
| student2_id | UUID FK | students.id |
| school_id | UUID FK | schools.id |
| class_id | UUID NULLABLE | |
| assigned_by | UUID FK | app_users.id (teacher) |
| is_active | BOOLEAN DEFAULT TRUE | |
| expires_at | TIMESTAMP | Monthly reset |
| created_at | TIMESTAMP | |

### game_progression_paths
| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| code | VARCHAR(32) UNIQUE | ACADEMIC/ATTENDANCE/CO_CURRICULAR/CHARACTER/DIGITAL |
| name | VARCHAR(64) | |
| stage1_name | VARCHAR(64) | |
| stage1_xp | INTEGER | |
| stage2_name | VARCHAR(64) | |
| stage2_xp | INTEGER | |
| stage3_name | VARCHAR(64) | |
| stage3_xp | INTEGER | |
| stage4_name | VARCHAR(64) | |
| stage4_xp | INTEGER | |
| badge_id | UUID NULLABLE FK | Badge on completion |
| created_at | TIMESTAMP | |

### game_student_path_progress
| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| student_id | UUID FK | students.id |
| path_code | VARCHAR(32) | |
| current_xp | INTEGER DEFAULT 0 | Path-specific XP |
| current_stage | INTEGER DEFAULT 1 | 1-4 |
| updated_at | TIMESTAMP | |
| | | UNIQUE(student_id, path_code) |

### game_titles
| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| code | VARCHAR(64) UNIQUE | |
| name | VARCHAR(128) | Display text |
| criteria_json | TEXT | Unlock rules |
| icon_name | VARCHAR(32) | |
| is_active | BOOLEAN DEFAULT TRUE | |
| created_at | TIMESTAMP | |

### game_seasonal_events
| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| school_id | UUID NULLABLE | NULL = global |
| code | VARCHAR(64) UNIQUE | |
| name | VARCHAR(128) | |
| badge_id | UUID FK | game_badge_definitions.id |
| quest_id | UUID FK | game_quest_definitions.id |
| start_date | DATE | |
| end_date | DATE | |
| is_active | BOOLEAN DEFAULT TRUE | |
| created_at | TIMESTAMP | |

### game_motivation_messages
| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| message_key | VARCHAR(64) UNIQUE | e.g. "low_xp_3_days" |
| message_text | TEXT | Growth-mindset text |
| language | VARCHAR(8) DEFAULT "en" | |
| is_active | BOOLEAN DEFAULT TRUE | |
| created_at | TIMESTAMP | |

### game_teacher_encouragements
| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| teacher_id | UUID FK | app_users.id |
| student_id | UUID FK | students.id |
| school_id | UUID FK | schools.id |
| amount | INTEGER | XP awarded |
| reason | TEXT | |
| encouragement_type | VARCHAR(16) | ENCOURAGE/SPOTLIGHT/CUSTOM_BADGE/PEP_TALK |
| created_at | TIMESTAMP | |

---

## 27. SERVER ARCHITECTURE — SERVICES, ROUTES, HOOKS

### File Structure

```
server/src/main/kotlin/com/littlebridge/enrollplus/feature/gamification/
  GamificationRouting.kt          — All API endpoints
  GamificationService.kt          — Core XP engine, level calc, badge check
  QuestService.kt                 — Quest generation, progress, expiry
  HouseService.kt                 — House points, house leaderboard
  RewardService.kt                — Reward catalog, redemption, fulfillment
  LeaderboardService.kt           — Multi-dimensional leaderboards
  SeasonalEventService.kt         — Event activation, limited badges
  ComboService.kt                 — Combo chain tracking + multipliers
  BoostService.kt                 — Active boost evaluation
  CatchUpService.kt               — Catch-up mechanic evaluation
  BadgeCriteriaEvaluator.kt       — Evaluate criteria_json rules
  GamificationSeeder.kt           — Seed defaults on first boot
```

### API Endpoints

PARENT (JWT):
  GET  /api/v1/parent/gamification/{childId}           — Full profile
  GET  /api/v1/parent/gamification/{childId}/badges     — Badge catalog + earned
  GET  /api/v1/parent/gamification/{childId}/history    — XP history (paginated)
  GET  /api/v1/parent/gamification/{childId}/quests     — Active quests
  GET  /api/v1/parent/gamification/{childId}/paths      — Progression paths
  GET  /api/v1/parent/gamification/{childId}/rewards    — Reward catalog
  POST /api/v1/parent/gamification/{childId}/redeem     — Redeem reward
  GET  /api/v1/parent/gamification/{childId}/leaderboard — Class leaderboard
  POST /api/v1/parent/gamification/{childId}/shoutout   — Send shout-out
  PUT  /api/v1/parent/gamification/{childId}/title      — Set active title

TEACHER (JWT + requireTeacherContext):
  GET  /api/v1/teacher/gamification/class/overview      — Class gamification overview
  GET  /api/v1/teacher/gamification/class/leaderboard   — Class top 5
  GET  /api/v1/teacher/gamification/students/needs-encouragement — Bottom 25% (private)
  POST /api/v1/teacher/gamification/students/{id}/encourage — Encourage button
  POST /api/v1/teacher/gamification/students/{id}/spotlight  — Spotlight award
  POST /api/v1/teacher/gamification/class/pep-talk      — Class boost
  POST /api/v1/teacher/gamification/quests/assign       — Assign custom quest
  POST /api/v1/teacher/gamification/badges/create       — Create class badge
  POST /api/v1/teacher/gamification/badges/{id}/award   — Award badge to student
  POST /api/v1/teacher/gamification/mentors/assign      — Assign mentor
  POST /api/v1/teacher/gamification/buddies/assign      — Assign study buddy
  POST /api/v1/teacher/gamification/class-goal          — Create class goal
  PUT  /api/v1/teacher/gamification/class-goal/{id}/complete — Mark goal achieved
  GET  /api/v1/teacher/gamification/shoutouts           — Moderate shout-outs
  DELETE /api/v1/teacher/gamification/shoutouts/{id}    — Delete shout-out
  POST /api/v1/teacher/gamification/parent-alert        — Send parent alert

ADMIN (JWT + requireSchoolContext):
  PUT  /api/v1/school/gamification/toggle               — Master kill switch
  PUT  /api/v1/school/gamification/flags                — Granular toggles
  GET  /api/v1/school/gamification/config               — Get all config
  PUT  /api/v1/school/gamification/config               — Update config
  GET  /api/v1/school/gamification/analytics             — Analytics dashboard
  GET  /api/v1/school/gamification/leaderboard           — School leaderboard
  GET  /api/v1/school/gamification/badges                — Manage badge catalog
  POST /api/v1/school/gamification/badges                — Create custom badge
  PUT  /api/v1/school/gamification/badges/{id}           — Edit badge
  GET  /api/v1/school/gamification/rewards               — Manage reward catalog
  POST /api/v1/school/gamification/rewards               — Create reward
  PUT  /api/v1/school/gamification/rewards/{id}          — Edit reward
  GET  /api/v1/school/gamification/redemptions           — Pending redemptions
  PUT  /api/v1/school/gamification/redemptions/{id}      — Approve/reject
  GET  /api/v1/school/gamification/houses                — Manage houses
  POST /api/v1/school/gamification/houses                — Create house
  PUT  /api/v1/school/gamification/houses/{id}           — Edit house
  GET  /api/v1/school/gamification/levels                — Manage level thresholds
  PUT  /api/v1/school/gamification/levels                — Update level config
  GET  /api/v1/school/gamification/events                — Manage seasonal events
  POST /api/v1/school/gamification/events                — Create custom event
  PUT  /api/v1/school/gamification/events/{id}           — Edit event

### XP Awarding Hooks (integrated into existing routes)

| Existing Route File | Hook Location | XP Event |
|---|---|---|
| AttendanceRouting | After marking PRESENT | +10 XP ATTENDANCE |
| HomeworkRouting | After submission | +15/+5 XP HOMEWORK |
| AssessmentRouting | After publishing marks | +50/+25/+10 XP ASSESSMENT |
| TutorActRouting | After practice grade | +5 XP TUTOR |
| TutorAgentRouting | After doubt response | +3 XP TUTOR |
| LibraryRouting | After book issue | +10 XP LIBRARY |
| LibraryRouting | After book return | +15 XP LIBRARY |
| EventRegistrationRouting | After registration | +20/+40 XP EVENT |
| HealthRouting | After checkup | +15 XP HEALTH |
| ScholarshipRouting | After application | +30 XP SCHOLARSHIP |
| ScholarshipRouting | After approval | +100 XP SCHOLARSHIP |
| PtmRouting | After PTM marked | +20 XP PTM |
| ParentDashboardRouting | On dashboard GET (daily) | +2 XP APP_OPEN |

### Scheduled Jobs

| Job | Schedule | Purpose |
|---|---|---|
| DailyQuestAssignment | Midnight daily | Assign new daily quests |
| WeeklyQuestAssignment | Monday midnight | Assign new weekly quests |
| StreakCheck | Midnight daily | Update streaks, check grace |
| PerfectWeekBonus | Friday midnight | Award perfect week XP |
| PerfectMonthBonus | Last day of month | Award perfect month XP |
| BirthdayCheck | Midnight daily | Award birthday XP + badge |
| CatchUpEvaluation | Sunday midnight | Evaluate bottom 25%, activate boosts |
| HouseOfTheMonth | Last day of month | Calculate winner, apply boost |
| SeasonalEventActivation | Midnight daily | Activate/deactivate events |
| QuestExpiry | Midnight daily | Expire incomplete quests silently |
| BadgeCriteriaCheck | After every XP award | Check all unearned badges |

---

## 28. SHARED MODULE — KOTLIN MULTIPLATFORM

```
shared/src/commonMain/kotlin/com/littlebridge/enrollplus/feature/gamification/
  domain/
    model/
      GamificationProfile.kt       — totalXp, currentXp, currentLevel, levelTitle,
                                     nextLevelXp, progressToNext, streakDays,
                                     activeTitle, houseName, catchUpActive
      Badge.kt                     — id, code, name, description, icon, category,
                                     rarity, isEarned, earnedAt?
      XpTransaction.kt             — id, amount, reason, source, category, createdAt
      Quest.kt                     — id, name, description, xpReward, progress,
                                     target, completed, expiresAt, questType
      LeaderboardEntry.kt          — studentName, totalXp, level, rank, title
      Reward.kt                    — id, name, description, icon, xpCost,
                                     stockRemaining, canAfford
      RewardRedemption.kt         — id, rewardName, status, xpSpent, createdAt
      House.kt                     — id, name, iconName, color, motto, points, rank
      ProgressionPath.kt          — code, name, currentStage, currentXp,
                                     stages (list of name + xpRequired)
      Title.kt                     — code, name, isUnlocked, isActive
      GamificationConfig.kt       — enabled flags, level definitions, xp amounts
      ShoutoutTemplate.kt         — id, message
    repository/
      GamificationRepository.kt   — interface
    usecase/
      GetGamificationProfileUseCase.kt
      GetBadgeCatalogUseCase.kt
      GetXpHistoryUseCase.kt
      GetActiveQuestsUseCase.kt
      GetProgressionPathsUseCase.kt
      GetRewardCatalogUseCase.kt
      RedeemRewardUseCase.kt
      GetLeaderboardUseCase.kt
      SendShoutoutUseCase.kt
      SetActiveTitleUseCase.kt
  data/
    remote/
      KtorGamificationApi.kt      — HTTP calls to all gamification endpoints
    repository/
      GamificationRepositoryImpl.kt
```

Register in Koin.kt:
  single { KtorGamificationApi(get(), AppConfig.schoolBaseUrl) }
  single<GamificationRepository> { GamificationRepositoryImpl(get()) }
  factory { GetGamificationProfileUseCase(get()) }
  factory { GetBadgeCatalogUseCase(get()) }
  // ... etc for all use cases

---

## 29. UI INTEGRATION — WHERE IT APPEARS (NOT SPAMMY)

Gamification UI appears in exactly these places. NOWHERE else.

### Parent Portal
  - **Child Profile Screen**: Level badge + XP bar + streak counter
    (small, at the top of the profile, NOT a full-screen takeover)
  - **Progress Tab** (one of the 5 bottom dock tabs):
    - Hero card: Level, XP bar, title, streak
    - Active quests (3 cards with progress bars)
    - Badges section (grid of earned + locked silhouettes)
    - Progression paths (5 horizontal bars)
    - House card (house name, rank, points)
    - Class goal card (progress bar)
    - Rewards shop (catalog grid, redeem button)
    - Active boosts banner (if any)
    - XP history (collapsible list)
  - **Child name in header**: Small level badge next to name (optional)

### Teacher Portal
  - **Class Roster**: Small level badge + title next to each student name
  - **Student Detail**: Gamification summary card (level, XP, badges, streak)
    + "Encourage" button + "Spotlight" button
  - **Class Home**: Small "Gamification Overview" card (top 5, recent badges)
    + "Class Pep Talk" button
  - **Class Roster actions**: "Assign Mentor" / "Assign Study Buddy" buttons
  - NO separate gamification tab — all woven into existing screens

### Admin Portal (Mobile App)
  - **Settings**: Gamification card with master toggle + granular toggles
  - **Dashboard**: Gamification analytics card (if enabled)
  - **Gamification Management** (in settings/admin section):
    - Badge catalog management
    - Reward catalog management
    - Level configuration
    - House management
    - Seasonal events management
    - Redemption approvals

### Admin Portal (Website)
  - **Settings page**: Gamification card with master toggle
  - **Sidebar**: "Gamification" section (only when enabled)
    - Dashboard (analytics)
    - Badges (catalog management)
    - Rewards (catalog + redemptions)
    - Levels (threshold config)
    - Houses (management)
    - Events (seasonal)
    - Configuration (XP amounts, toggles)

### What NEVER Shows Gamification
  - Login / Signup screens
  - Onboarding flow
  - Fee payment screen
  - Messaging / conversations
  - Attendance marking screen
  - Homework submission screen
  - Settings (except the gamification toggle itself)
  - Notifications list (gamification notifications appear here but
    don't dominate — max 1 per day)

---

## 30. WEBSITE ADMIN PANEL — KILL SWITCH + CONFIG

### Settings Page Addition

Add a "Gamification" card to the existing settings page at
`website/src/app/admin/settings/page.tsx`, below the existing
"Configuration" card.

The card contains:
  - Master toggle: "Enable Gamification" (switch)
  - When ON, shows granular toggles:
    - Leaderboards, Rewards Shop, Houses, Quests, Mentor,
      Shout-Outs, Seasonal Events, Class Goals, Combos, Boosts
  - Save button calls PUT /api/v1/school/gamification/flags

### Sidebar Addition

When gamification is enabled, add a "Gamification" section to the
sidebar at `website/src/components/admin/Sidebar.tsx`:

  Gamification/
    Dashboard     — /admin/gamification
    Badges        — /admin/gamification/badges
    Rewards       — /admin/gamification/rewards
    Levels        — /admin/gamification/levels
    Houses        — /admin/gamification/houses
    Events        — /admin/gamification/events
    Settings      — /admin/gamification/settings

When gamification is disabled, this section is hidden entirely.

### New Pages

```
website/src/app/admin/gamification/
  page.tsx              — Dashboard (analytics overview)
  badges/page.tsx       — Badge catalog management
  rewards/page.tsx      — Reward catalog + pending redemptions
  levels/page.tsx       — Level threshold configuration
  houses/page.tsx       — House management
  events/page.tsx       — Seasonal events management
  settings/page.tsx     — XP amounts, toggles, message templates
```

### API Client

Add to `website/src/lib/admin/client.ts`:
  - getGamificationConfig()
  - updateGamificationFlags(flags)
  - getGamificationAnalytics()
  - getBadges() / createBadge() / updateBadge()
  - getRewards() / createReward() / updateReward()
  - getRedemptions() / approveRedemption() / rejectRedemption()
  - getHouses() / createHouse() / updateHouse()
  - getLevels() / updateLevels()
  - getEvents() / createEvent() / updateEvent()

---

## 31. EXECUTION PLAN — 8 PHASES

### Phase 1: Database Schema + Migration
  - Add all game_* tables to Tables.kt
  - Create migration_100_gamification.sql
  - Register in DatabaseFactory.allTables
  - Create GamificationSeeder (default levels, badges, quests, houses, paths, titles, messages)
  - Verify: server boots, tables exist, seed data present

### Phase 2: Core Server Services
  - GamificationService (awardXp, recomputeLevel, getProfile)
  - BadgeCriteriaEvaluator (evaluate criteria_json)
  - BoostService (calculate active multipliers)
  - ComboService (track combos)
  - CatchUpService (evaluate bottom 25%)
  - GamificationRouting (all endpoints)
  - Kill switch flag check in all routes
  - Verify: API endpoints respond, XP awards work, level calc correct

### Phase 3: XP Awarding Hooks
  - Add awardXp() calls to all 13 existing route handlers
  - Wrap in try-catch (never break parent operation)
  - Check isGamificationEnabled before awarding
  - Verify: marking attendance awards XP, homework submission awards XP, etc.

### Phase 4: Subsystem Services
  - QuestService (generation, progress, expiry)
  - HouseService (points, leaderboard, house of month)
  - RewardService (catalog, redemption, fulfillment)
  - LeaderboardService (all 8 types, privacy checks)
  - SeasonalEventService (activation, limited badges)
  - Scheduled jobs (all 11 jobs)
  - Verify: quests assign/expire, house points update, rewards redeem

### Phase 5: Shared Module (KMP)
  - All domain models
  - KtorGamificationApi
  - GamificationRepository + Impl
  - All use cases
  - Register in Koin.kt
  - Verify: shared JVM compiles, API calls work from client

### Phase 6: Teacher Tools
  - All teacher endpoints (encourage, spotlight, pep talk, mentor assign, etc.)
  - Verify: teacher can award XP, assign mentors, create class goals

### Phase 7: Website Admin Panel
  - Settings page gamification card + kill switch
  - Sidebar gamification section (conditional)
  - All 7 admin pages
  - API client functions
  - Verify: admin can toggle, manage badges/rewards/houses/levels/events

### Phase 8: Mobile UI (Deferred to Portal Rebuilds)
  - Parent Portal: Progress tab, profile gamification card
  - Teacher Portal: Class roster badges, encourage button, overview card
  - Admin Portal: Settings toggle, analytics card
  - All UI checks gamificationEnabled flag first
  - Verify: kill switch hides all UI seamlessly

---

## 32. VERIFICATION CHECKLIST

### Kill Switch
  [ ] Admin toggles OFF -> all gamification API returns { enabled: false }
  [ ] Admin toggles OFF -> app hides all gamification UI, no empty gaps
  [ ] Admin toggles OFF -> existing features work exactly as before
  [ ] Admin toggles ON -> all gamification UI appears seamlessly
  [ ] Admin toggles OFF then ON -> historical XP preserved
  [ ] Granular toggles work independently when master is ON
  [ ] All granular toggles OFF when master is OFF

### Anti-Spam
  [ ] No full-screen pop-ups for XP/badge/level-up
  [ ] No forced animations blocking interaction
  [ ] Max 1 gamification notification per day
  [ ] No "CLAIM YOUR REWARD" buttons (auto-claimed)
  [ ] No dark patterns (urgency, guilt-trip, comparison anxiety)
  [ ] No gamification UI on: login, fees, messaging, attendance, homework, settings
  [ ] No red/orange for low performance
  [ ] No comparison to named peers negatively
  [ ] No "0" shown without growth-mindset context

### XP Engine
  [ ] All 13 hooks award correct XP amounts
  [ ] XP failures never break parent operations
  [ ] XP only goes up (never negative except reward spend)
  [ ] Multipliers stack correctly, capped at x5
  [ ] Level never decreases
  [ ] Level-up awards bonus XP silently
  [ ] Level-up shows toast (3s auto-dismiss), not pop-up

### Badges
  [ ] All 40 default badges seeded on first boot
  [ ] Criteria engine evaluates after every XP award
  [ ] Badges auto-awarded silently (no pop-up)
  [ ] Locked badges show as silhouettes with rarity hint
  [ ] Admin can create/edit/deactivate custom badges
  [ ] Teacher can create class-scoped badges
  [ ] Seasonal badges only earnable during event window

### Quests
  [ ] Daily quests assigned on first app open
  [ ] Weekly quests assigned on Monday
  [ ] Seasonal quests assigned when event active
  [ ] Quests personalized per student
  [ ] Bottom 25% get catch-up quests (higher XP, easier)
  [ ] Expired quests disappear silently (no "you missed it")
  [ ] Quest progress tracked automatically (no manual complete)

### Motivation
  [ ] Catch-up boost activates for bottom 25%
  [ ] Catch-up shown as "Boost Active!" not "you're behind"
  [ ] Streak grace: missing day pauses, doesn't reset to 0
  [ ] Welcome back quest for inactive 7+ days
  [ ] All system text uses growth-mindset language
  [ ] Leaderboards never show full ranking
  [ ] Leaderboards never show "below average" or red/orange
  [ ] Mentor notifications sent to mentors only, never to class
  [ ] Mentor notifications always positive framing
  [ ] Notifications not sent during school hours or after 9pm

### Teacher Tools
  [ ] Encourage button on student detail (max 3/week)
  [ ] Spotlight on class roster (max 1/week)
  [ ] Class Pep Talk on class home (max 1/week)
  [ ] Mentor/study buddy assignment on class roster
  [ ] Custom quest assignment to individual student
  [ ] Parent alert always positive framing
  [ ] Gamification overview card on class home (private to teacher)
  [ ] Bottom 25% list private to teacher/admin only

### Admin Controls
  [ ] Master kill switch in app settings
  [ ] Master kill switch in website settings
  [ ] Granular toggles work (10 subsystems)
  [ ] Badge catalog management (CRUD)
  [ ] Reward catalog management (CRUD + stock)
  [ ] Level threshold configuration
  [ ] House management (CRUD + assignment)
  [ ] Seasonal event management
  [ ] XP amount configuration per action
  [ ] Message template customization
  [ ] Analytics dashboard with all metrics
  [ ] Redemption approval workflow

### Data Integrity
  [ ] XP ledger records every transaction with multiplier
  [ ] Student stats cache matches ledger sum
  [ ] Level matches XP threshold
  [ ] Badge awards unique (no duplicates)
  [ ] Quest assignments unique per student per expiry
  [ ] House assignments unique per student per school
