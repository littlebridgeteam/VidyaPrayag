-- =============================================================================
-- Migration: setup_gamification_schema
-- Subject:   Gamification system — 21 tables for XP, badges, quests, houses,
--            rewards, boosts, shoutouts, class goals, mentor/study buddy,
--            progression paths, titles, seasonal events, motivation messages,
--            teacher encouragements
--
-- WHY THIS EXISTS
--   The gamification system requires a complete database schema to store XP
--   transactions, student stats, badge definitions, quest definitions, house
--   assignments, reward catalog, reward redemptions, XP boosts, class goals,
--   shoutouts, mentor assignments, study buddy pairs, progression paths,
--   titles, seasonal events, motivation messages, and teacher encouragements.
--   This migration creates all 21 tables matching the Exposed ORM definitions
--   in server/src/main/kotlin/com/littlebridge/enrollplus/db/Tables.kt.
--
-- HOW TO RUN
--   Supabase -> SQL Editor -> paste this whole file -> Run.
--   100% SAFE TO RE-RUN: every statement uses CREATE TABLE IF NOT EXISTS,
--   CREATE INDEX IF NOT EXISTS, so running it against a database that already
--   has the tables / indexes / constraints is a harmless no-op.
--
-- COLUMN FIDELITY
--   The column names / types / nullability / defaults below match
--   server/src/main/kotlin/com.littlebridge.enrollplus/db/Tables.kt
--   (Game*Table objects) EXACTLY so the Exposed ORM mapping lines up with
--   the real Postgres schema.
-- =============================================================================

-- Enable UUID extension if not already enabled
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =============================================================================
-- 1. game_xp_ledger
-- =============================================================================
CREATE TABLE IF NOT EXISTS game_xp_ledger (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id  UUID        NOT NULL,
    school_id   UUID        NOT NULL,
    amount      INTEGER     NOT NULL,
    reason      TEXT        NOT NULL,
    source      VARCHAR(32) NOT NULL,
    category    VARCHAR(16) NOT NULL,
    multiplier  REAL        NOT NULL DEFAULT 1.0,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_gxl_student ON game_xp_ledger (student_id);
CREATE INDEX IF NOT EXISTS idx_gxl_school  ON game_xp_ledger (school_id);
CREATE INDEX IF NOT EXISTS idx_gxl_created ON game_xp_ledger (created_at);

-- =============================================================================
-- 2. game_student_stats
-- =============================================================================
CREATE TABLE IF NOT EXISTS game_student_stats (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id       UUID        NOT NULL UNIQUE,
    school_id        UUID        NOT NULL,
    total_xp         INTEGER     NOT NULL DEFAULT 0,
    current_xp       INTEGER     NOT NULL DEFAULT 0,
    current_level    INTEGER     NOT NULL DEFAULT 1,
    streak_days      INTEGER     NOT NULL DEFAULT 0,
    last_active_date DATE,
    active_title     VARCHAR(64),
    house_id         UUID,
    catch_up_active  BOOLEAN     NOT NULL DEFAULT FALSE,
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_gss_school ON game_student_stats (school_id);
CREATE INDEX IF NOT EXISTS idx_gss_level  ON game_student_stats (current_level);

-- =============================================================================
-- 3. game_level_definitions
-- =============================================================================
CREATE TABLE IF NOT EXISTS game_level_definitions (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    school_id    UUID,                     -- NULL = global default
    level        INTEGER     NOT NULL,
    xp_required  INTEGER     NOT NULL,
    title        VARCHAR(64) NOT NULL,
    icon_name    VARCHAR(32) NOT NULL,
    is_active    BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (school_id, level)
);

-- =============================================================================
-- 4. game_badge_definitions
-- =============================================================================
CREATE TABLE IF NOT EXISTS game_badge_definitions (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    school_id        UUID,                     -- NULL = global
    code             VARCHAR(64) NOT NULL UNIQUE,
    name             VARCHAR(128) NOT NULL,
    description      TEXT NOT NULL,
    icon_name        VARCHAR(32) NOT NULL,
    category         VARCHAR(16) NOT NULL,     -- ACADEMIC/ATTENDANCE/CO_CURRICULAR/CHARACTER/HEALTH/MILESTONE/SEASONAL
    rarity           VARCHAR(16) NOT NULL,     -- COMMON/RARE/EPIC/LEGENDARY/MYTHIC
    xp_requirement   INTEGER     NOT NULL DEFAULT 0,
    criteria_json    TEXT        NOT NULL DEFAULT '{}',
    is_active        BOOLEAN     NOT NULL DEFAULT TRUE,
    is_seasonal      BOOLEAN     NOT NULL DEFAULT FALSE,
    available_from   DATE,
    available_until  DATE,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_gbd_category ON game_badge_definitions (category);
CREATE INDEX IF NOT EXISTS idx_gbd_school   ON game_badge_definitions (school_id);

-- =============================================================================
-- 5. game_student_badges
-- =============================================================================
CREATE TABLE IF NOT EXISTS game_student_badges (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id  UUID        NOT NULL,
    badge_id    UUID        NOT NULL,
    earned_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    awarded_by  UUID,
    UNIQUE (student_id, badge_id)
);

-- =============================================================================
-- 6. game_houses
-- =============================================================================
CREATE TABLE IF NOT EXISTS game_houses (
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    school_id  UUID        NOT NULL,
    name       VARCHAR(64) NOT NULL,
    icon_name  VARCHAR(32) NOT NULL,
    color      VARCHAR(16) NOT NULL,
    motto      TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (school_id, name)
);

-- =============================================================================
-- 7. game_student_house_assignments
-- =============================================================================
CREATE TABLE IF NOT EXISTS game_student_house_assignments (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id  UUID        NOT NULL,
    house_id    UUID        NOT NULL,
    school_id   UUID        NOT NULL,
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (student_id, school_id)
);

-- =============================================================================
-- 8. game_quest_definitions
-- =============================================================================
CREATE TABLE IF NOT EXISTS game_quest_definitions (
    id             UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    school_id      UUID,                     -- NULL = global
    code           VARCHAR(64) NOT NULL UNIQUE,
    name           VARCHAR(128) NOT NULL,
    description    TEXT NOT NULL,
    quest_type     VARCHAR(16) NOT NULL,     -- DAILY/WEEKLY/MILESTONE/CUSTOM
    category       VARCHAR(16) NOT NULL,     -- ACADEMIC/ATTENDANCE/CO_CURRICULAR/CHARACTER/HEALTH
    xp_reward      INTEGER     NOT NULL,
    criteria_json  TEXT        NOT NULL DEFAULT '{}',
    target_scope   VARCHAR(16) NOT NULL,     -- INDIVIDUAL/CLASS/SCHOOL
    duration_hours INTEGER     NOT NULL,
    is_active      BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =============================================================================
-- 9. game_student_quests
-- =============================================================================
CREATE TABLE IF NOT EXISTS game_student_quests (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id   UUID        NOT NULL,
    quest_id     UUID        NOT NULL,
    school_id    UUID        NOT NULL,
    progress     INTEGER     NOT NULL DEFAULT 0,
    target       INTEGER     NOT NULL,
    completed    BOOLEAN     NOT NULL DEFAULT FALSE,
    completed_at TIMESTAMPTZ,
    expires_at   TIMESTAMPTZ NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (student_id, quest_id, expires_at)
);

-- =============================================================================
-- 10. game_xp_boosts
-- =============================================================================
CREATE TABLE IF NOT EXISTS game_xp_boosts (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    school_id    UUID        NOT NULL,
    boost_type   VARCHAR(32) NOT NULL,       -- PEP_TALK/DOUBLE_XP/CATCH_UP/CUSTOM
    multiplier   REAL        NOT NULL,
    target_scope VARCHAR(16) NOT NULL,       -- SCHOOL/CLASS/INDIVIDUAL/ALL
    target_id    UUID,
    starts_at    TIMESTAMPTZ NOT NULL,
    ends_at      TIMESTAMPTZ NOT NULL,
    is_active    BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_gxb_school_active ON game_xp_boosts (school_id, is_active);

-- =============================================================================
-- 11. game_reward_catalog
-- =============================================================================
CREATE TABLE IF NOT EXISTS game_reward_catalog (
    id                UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    school_id         UUID        NOT NULL,
    name              VARCHAR(128) NOT NULL,
    description       TEXT        NOT NULL,
    icon_name         VARCHAR(32) NOT NULL,
    xp_cost           INTEGER     NOT NULL,
    stock_limit       INTEGER,
    stock_remaining   INTEGER,
    fulfillment_role  VARCHAR(16) NOT NULL,   -- TEACHER/ADMIN/PARENT
    is_active         BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =============================================================================
-- 12. game_reward_redemptions
-- =============================================================================
CREATE TABLE IF NOT EXISTS game_reward_redemptions (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id   UUID        NOT NULL,
    reward_id    UUID        NOT NULL,
    school_id    UUID        NOT NULL,
    xp_spent     INTEGER     NOT NULL,
    status       VARCHAR(16) NOT NULL DEFAULT 'PENDING',  -- PENDING/APPROVED/REJECTED/FULFILLED
    qr_code      TEXT,
    approved_by  UUID,
    approved_at  TIMESTAMPTZ,
    fulfilled_at TIMESTAMPTZ,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_grr_status ON game_reward_redemptions (status);
CREATE INDEX IF NOT EXISTS idx_grr_school ON game_reward_redemptions (school_id);

-- =============================================================================
-- 13. game_class_goals
-- =============================================================================
CREATE TABLE IF NOT EXISTS game_class_goals (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    school_id        UUID        NOT NULL,
    class_id         UUID,
    class_name       VARCHAR(32),
    section          VARCHAR(8),
    goal_type        VARCHAR(16) NOT NULL,    -- ATTENDANCE/XP/QUIZ/BEHAVIOUR/CUSTOM
    target           INTEGER     NOT NULL,
    current_progress INTEGER     NOT NULL DEFAULT 0,
    reward           TEXT        NOT NULL,
    completed        BOOLEAN     NOT NULL DEFAULT FALSE,
    completed_at     TIMESTAMPTZ,
    deadline         DATE,
    created_by       UUID        NOT NULL,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =============================================================================
-- 14. game_shoutouts
-- =============================================================================
CREATE TABLE IF NOT EXISTS game_shoutouts (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    sender_id   UUID        NOT NULL,
    receiver_id UUID        NOT NULL,
    school_id   UUID        NOT NULL,
    template_id INTEGER     NOT NULL DEFAULT 0,
    message     TEXT        NOT NULL,
    is_public   BOOLEAN     NOT NULL DEFAULT TRUE,
    is_deleted  BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_gsh_receiver ON game_shoutouts (receiver_id);
CREATE INDEX IF NOT EXISTS idx_gsh_school   ON game_shoutouts (school_id);

-- =============================================================================
-- 15. game_mentor_assignments
-- =============================================================================
CREATE TABLE IF NOT EXISTS game_mentor_assignments (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    mentor_id   UUID        NOT NULL,
    mentee_id   UUID        NOT NULL,
    school_id   UUID        NOT NULL,
    assigned_by UUID        NOT NULL,
    is_active   BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (mentor_id, mentee_id, school_id)
);

-- =============================================================================
-- 16. game_study_buddy_pairs
-- =============================================================================
CREATE TABLE IF NOT EXISTS game_study_buddy_pairs (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    student1_id UUID        NOT NULL,
    student2_id UUID        NOT NULL,
    school_id   UUID        NOT NULL,
    class_id    UUID,
    assigned_by UUID        NOT NULL,
    is_active   BOOLEAN     NOT NULL DEFAULT TRUE,
    expires_at  TIMESTAMPTZ NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =============================================================================
-- 17. game_progression_paths
-- =============================================================================
CREATE TABLE IF NOT EXISTS game_progression_paths (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code         VARCHAR(32) NOT NULL UNIQUE,
    name         VARCHAR(64) NOT NULL,
    stage1_name  VARCHAR(64) NOT NULL,
    stage1_xp    INTEGER     NOT NULL,
    stage2_name  VARCHAR(64) NOT NULL,
    stage2_xp    INTEGER     NOT NULL,
    stage3_name  VARCHAR(64) NOT NULL,
    stage3_xp    INTEGER     NOT NULL,
    stage4_name  VARCHAR(64) NOT NULL,
    stage4_xp    INTEGER     NOT NULL,
    badge_id     UUID,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =============================================================================
-- 18. game_student_path_progress
-- =============================================================================
CREATE TABLE IF NOT EXISTS game_student_path_progress (
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id    UUID        NOT NULL,
    path_code     VARCHAR(32) NOT NULL,
    current_xp    INTEGER     NOT NULL DEFAULT 0,
    current_stage INTEGER     NOT NULL DEFAULT 1,
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (student_id, path_code)
);

-- =============================================================================
-- 19. game_titles
-- =============================================================================
CREATE TABLE IF NOT EXISTS game_titles (
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code          VARCHAR(64) NOT NULL UNIQUE,
    name          VARCHAR(128) NOT NULL,
    criteria_json TEXT        NOT NULL DEFAULT '{}',
    icon_name     VARCHAR(32) NOT NULL,
    is_active     BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =============================================================================
-- 20. game_seasonal_events
-- =============================================================================
CREATE TABLE IF NOT EXISTS game_seasonal_events (
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    school_id  UUID,                     -- NULL = global
    code       VARCHAR(64) NOT NULL UNIQUE,
    name       VARCHAR(128) NOT NULL,
    badge_id   UUID        NOT NULL,
    quest_id   UUID        NOT NULL,
    start_date DATE        NOT NULL,
    end_date   DATE        NOT NULL,
    is_active  BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =============================================================================
-- 21. game_motivation_messages
-- =============================================================================
CREATE TABLE IF NOT EXISTS game_motivation_messages (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    message_key  VARCHAR(64) NOT NULL UNIQUE,
    message_text TEXT        NOT NULL,
    language     VARCHAR(8)  NOT NULL DEFAULT 'en',
    is_active    BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =============================================================================
-- 22. game_teacher_encouragements
-- =============================================================================
CREATE TABLE IF NOT EXISTS game_teacher_encouragements (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    teacher_id          UUID        NOT NULL,
    student_id          UUID        NOT NULL,
    school_id           UUID        NOT NULL,
    amount              INTEGER     NOT NULL,
    reason              TEXT        NOT NULL,
    encouragement_type  VARCHAR(16) NOT NULL,   -- PRAPE/NUDGE/SPOTLIGHT/PEP_TALK
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_gte_teacher_student ON game_teacher_encouragements (teacher_id, student_id);

-- =============================================================================
-- SEED DATA: Default level definitions (global, school_id = NULL)
-- =============================================================================
INSERT INTO game_level_definitions (school_id, level, xp_required, title, icon_name)
SELECT NULL, lvl.xp_req, lvl.xp_req, lvl.title, lvl.icon
FROM (VALUES
    (1,     0, 'Novice',           'star_outline'),
    (2,   100, 'Apprentice',       'star_half'),
    (3,   300, 'Scholar',          'star'),
    (4,   600, 'Adept',            'auto_awesome'),
    (5,  1000, 'Expert',           'workspace_premium'),
    (6,  1500, 'Master',           'emoji_events'),
    (7,  2200, 'Grandmaster',      'diamond'),
    (8,  3000, 'Legend',           'bolt'),
    (9,  4000, 'Mythic',           'auto_awesome_motion'),
    (10, 5500, 'Ascended',         'rocket_launch')
) AS lvl(level, xp_req, title, icon)
WHERE NOT EXISTS (
    SELECT 1 FROM game_level_definitions WHERE school_id IS NULL AND level = lvl.level
);

-- =============================================================================
-- SEED DATA: Default badge definitions (global, school_id = NULL)
-- =============================================================================
INSERT INTO game_badge_definitions (school_id, code, name, description, icon_name, category, rarity, xp_requirement, criteria_json)
SELECT NULL, b.code, b.name, b.desc, b.icon, b.cat, b.rarity, b.xp_req, b.criteria
FROM (VALUES
    ('FIRST_ATTENDANCE',   'First Attendance',      'Marked present for the first time',              'check_circle',     'ATTENDANCE',   'COMMON',    10, '{"type":"count","source":"attendance","threshold":1}'),
    ('PERFECT_WEEK',       'Perfect Week',          'Attended every day for a full week',             'verified',         'ATTENDANCE',   'RARE',      50, '{"type":"count","source":"attendance","threshold":5}'),
    ('QUIZ_MASTER',        'Quiz Master',           'Completed 10 quizzes',                           'quiz',             'ACADEMIC',     'RARE',      50, '{"type":"count","source":"quiz","threshold":10}'),
    ('ASSESSMENT_ACE',     'Assessment Ace',        'Scored 80%+ on 5 assessments',                   'school',           'ACADEMIC',     'EPIC',     100, '{"type":"count","source":"assessment","threshold":5}'),
    ('HOMEWORK_HERO',      'Homework Hero',         'Submitted 20 homework assignments',              'assignment',       'ACADEMIC',     'RARE',      50, '{"type":"count","source":"homework","threshold":20}'),
    ('LEVEL_5',            'Reached Level 5',       'Achieved Level 5',                               'workspace_premium','MILESTONE',    'EPIC',     100, '{"type":"level","threshold":5}'),
    ('LEVEL_10',           'Reached Level 10',      'Achieved Level 10',                              'rocket_launch',    'MILESTONE',    'LEGENDARY',250, '{"type":"level","threshold":10}'),
    ('STREAK_7',           '7-Day Streak',          'Maintained a 7-day login streak',                'local_fire_department','CHARACTER','RARE',      30, '{"type":"xp","threshold":0}'),
    ('STREAK_30',          '30-Day Streak',         'Maintained a 30-day login streak',               'whatshot',         'CHARACTER',    'EPIC',     100, '{"type":"xp","threshold":0}'),
    ('GOOD_SAMARITAN',     'Good Samaritan',        'Received a shoutout from a teacher',             'volunteer_activism','CHARACTER',   'COMMON',    20, '{"type":"manual"}'),
    ('SPOTLIGHT',          'In the Spotlight',      'Received a spotlight award',                     'flare',            'CHARACTER',    'RARE',      50, '{"type":"manual"}'),
    ('1000_XP',            'XP Milestone: 1000',    'Earned 1000 total XP',                           'diamond',          'MILESTONE',    'EPIC',     100, '{"type":"xp","threshold":1000}'),
    ('5000_XP',            'XP Milestone: 5000',    'Earned 5000 total XP',                           'auto_awesome',     'MILESTONE',    'LEGENDARY',250, '{"type":"xp","threshold":5000}')
) AS b(code, name, desc, icon, cat, rarity, xp_req, criteria)
WHERE NOT EXISTS (
    SELECT 1 FROM game_badge_definitions WHERE code = b.code
);

-- =============================================================================
-- SEED DATA: Default motivation messages (English)
-- =============================================================================
INSERT INTO game_motivation_messages (message_key, message_text, language)
SELECT m.key, m.text, 'en'
FROM (VALUES
    ('level_up',           'Amazing! You just reached Level {level}! Keep going!'),
    ('badge_earned',       'Congratulations! You earned the "{badge_name}" badge!'),
    ('streak_milestone',   'You are on a {days}-day streak! Don''t break the chain!'),
    ('quest_completed',    'Quest complete! +{xp} XP earned. You''re unstoppable!'),
    ('house_points',       'Your house "{house_name}" gained {points} points!'),
    ('reward_redeemed',    'Your reward "{reward_name}" redemption is pending approval!'),
    ('reward_approved',    'Your reward "{reward_name}" has been approved! Collect it soon.'),
    ('catch_up_activated', 'Catch-up boost activated! You''re earning 1.5x XP for the next 24 hours!'),
    ('pep_talk',           'Your teacher just gave the class a pep talk! 1.5x XP is active!'),
    ('spotlight',          'You''re in the spotlight! +50 XP for your amazing improvement!')
) AS m(key, text)
WHERE NOT EXISTS (
    SELECT 1 FROM game_motivation_messages WHERE message_key = m.key
);

-- =============================================================================
-- SEED DATA: Gamification flags in app_config (if not exists)
-- =============================================================================
INSERT INTO app_config (key, value, updated_at)
SELECT 'gamification_flags',
       '{"isGamificationEnabled":true,"gamificationLeaderboards":true,"gamificationRewards":true,"gamificationHouses":true,"gamificationQuests":true,"gamificationMentor":true,"gamificationShoutouts":true,"gamificationEvents":true,"gamificationClassGoals":true,"gamificationCombos":true,"gamificationBoosts":true}',
       now()
WHERE NOT EXISTS (
    SELECT 1 FROM app_config WHERE key = 'gamification_flags'
);

-- =============================================================================
-- DONE
--   21 tables created.
--   Default level definitions (10 levels) seeded.
--   Default badge definitions (13 badges) seeded.
--   Default motivation messages (10 messages) seeded.
--   Gamification flags seeded in app_config.
-- =============================================================================
