-- ═══════════════════════════════════════════════════════════════════════════
-- MIGRATION 100: GAMIFICATION SYSTEM
-- Spec: GAMIFICATION_SYSTEM_SPEC.md §26
--
-- Creates 19 game_* tables for the full student motivation platform.
-- Run in Supabase SQL Editor before deploying gamification code.
-- ═══════════════════════════════════════════════════════════════════════════

-- ── Definition tables (no FKs to other game_ tables) ────────────────────

CREATE TABLE IF NOT EXISTS game_level_definitions (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id     UUID,
    level         INTEGER NOT NULL,
    xp_required   INTEGER NOT NULL,
    title         VARCHAR(64) NOT NULL,
    icon_name     VARCHAR(32) NOT NULL,
    is_active     BOOLEAN DEFAULT TRUE,
    created_at    TIMESTAMPTZ DEFAULT now(),
    UNIQUE(school_id, level)
);

CREATE TABLE IF NOT EXISTS game_badge_definitions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id       UUID,
    code            VARCHAR(64) UNIQUE NOT NULL,
    name            VARCHAR(128) NOT NULL,
    description     TEXT NOT NULL,
    icon_name       VARCHAR(32) NOT NULL,
    category        VARCHAR(16) NOT NULL,
    rarity          VARCHAR(16) NOT NULL,
    xp_requirement  INTEGER DEFAULT 0,
    criteria_json   TEXT DEFAULT '{}',
    is_active       BOOLEAN DEFAULT TRUE,
    is_seasonal     BOOLEAN DEFAULT FALSE,
    available_from  DATE,
    available_until DATE,
    created_at      TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_gbd_category ON game_badge_definitions(category);
CREATE INDEX IF NOT EXISTS idx_gbd_school ON game_badge_definitions(school_id);

CREATE TABLE IF NOT EXISTS game_houses (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id  UUID NOT NULL,
    name       VARCHAR(64) NOT NULL,
    icon_name  VARCHAR(32) NOT NULL,
    color      VARCHAR(16) NOT NULL,
    motto      TEXT,
    created_at TIMESTAMPTZ DEFAULT now(),
    UNIQUE(school_id, name)
);

CREATE TABLE IF NOT EXISTS game_quest_definitions (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id      UUID,
    code           VARCHAR(64) UNIQUE NOT NULL,
    name           VARCHAR(128) NOT NULL,
    description    TEXT NOT NULL,
    quest_type     VARCHAR(16) NOT NULL,
    category       VARCHAR(16) NOT NULL,
    xp_reward      INTEGER NOT NULL,
    criteria_json  TEXT DEFAULT '{}',
    target_scope   VARCHAR(16) NOT NULL,
    duration_hours INTEGER NOT NULL,
    is_active      BOOLEAN DEFAULT TRUE,
    created_at     TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS game_progression_paths (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code         VARCHAR(32) UNIQUE NOT NULL,
    name         VARCHAR(64) NOT NULL,
    stage1_name  VARCHAR(64) NOT NULL,
    stage1_xp    INTEGER NOT NULL,
    stage2_name  VARCHAR(64) NOT NULL,
    stage2_xp    INTEGER NOT NULL,
    stage3_name  VARCHAR(64) NOT NULL,
    stage3_xp    INTEGER NOT NULL,
    stage4_name  VARCHAR(64) NOT NULL,
    stage4_xp    INTEGER NOT NULL,
    badge_id     UUID,
    created_at   TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS game_titles (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code          VARCHAR(64) UNIQUE NOT NULL,
    name          VARCHAR(128) NOT NULL,
    criteria_json TEXT DEFAULT '{}',
    icon_name     VARCHAR(32) NOT NULL,
    is_active     BOOLEAN DEFAULT TRUE,
    created_at    TIMESTAMPTZ DEFAULT now()
);

-- ── Student-scoped tables ────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS game_xp_ledger (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id  UUID NOT NULL,
    school_id   UUID NOT NULL,
    amount      INTEGER NOT NULL,
    reason      TEXT NOT NULL,
    source      VARCHAR(32) NOT NULL,
    category    VARCHAR(16) NOT NULL,
    multiplier  REAL DEFAULT 1.0,
    created_at  TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_gxl_student ON game_xp_ledger(student_id);
CREATE INDEX IF NOT EXISTS idx_gxl_school ON game_xp_ledger(school_id);
CREATE INDEX IF NOT EXISTS idx_gxl_created ON game_xp_ledger(created_at);

CREATE TABLE IF NOT EXISTS game_student_stats (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id       UUID UNIQUE NOT NULL,
    school_id        UUID NOT NULL,
    total_xp         INTEGER DEFAULT 0,
    current_xp       INTEGER DEFAULT 0,
    current_level    INTEGER DEFAULT 1,
    streak_days      INTEGER DEFAULT 0,
    last_active_date DATE,
    active_title     VARCHAR(64),
    house_id         UUID,
    catch_up_active  BOOLEAN DEFAULT FALSE,
    updated_at       TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_gss_school ON game_student_stats(school_id);
CREATE INDEX IF NOT EXISTS idx_gss_level ON game_student_stats(current_level);

CREATE TABLE IF NOT EXISTS game_student_badges (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id  UUID NOT NULL,
    badge_id    UUID NOT NULL,
    earned_at   TIMESTAMPTZ DEFAULT now(),
    awarded_by  UUID,
    UNIQUE(student_id, badge_id)
);

CREATE TABLE IF NOT EXISTS game_student_house_assignments (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id  UUID NOT NULL,
    house_id    UUID NOT NULL,
    school_id   UUID NOT NULL,
    assigned_at TIMESTAMPTZ DEFAULT now(),
    UNIQUE(student_id, school_id)
);

CREATE TABLE IF NOT EXISTS game_student_quests (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id   UUID NOT NULL,
    quest_id     UUID NOT NULL,
    school_id    UUID NOT NULL,
    progress     INTEGER DEFAULT 0,
    target       INTEGER NOT NULL,
    completed    BOOLEAN DEFAULT FALSE,
    completed_at TIMESTAMPTZ,
    expires_at   TIMESTAMPTZ NOT NULL,
    created_at   TIMESTAMPTZ DEFAULT now(),
    UNIQUE(student_id, quest_id, expires_at)
);

CREATE TABLE IF NOT EXISTS game_xp_boosts (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id    UUID NOT NULL,
    boost_type   VARCHAR(32) NOT NULL,
    multiplier   REAL NOT NULL,
    target_scope VARCHAR(16) NOT NULL,
    target_id    UUID,
    starts_at    TIMESTAMPTZ NOT NULL,
    ends_at      TIMESTAMPTZ NOT NULL,
    is_active    BOOLEAN DEFAULT TRUE,
    created_at   TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_gxb_school_active ON game_xp_boosts(school_id, is_active);

-- ── Reward system ────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS game_reward_catalog (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id         UUID NOT NULL,
    name              VARCHAR(128) NOT NULL,
    description       TEXT NOT NULL,
    icon_name         VARCHAR(32) NOT NULL,
    xp_cost           INTEGER NOT NULL,
    stock_limit       INTEGER,
    stock_remaining   INTEGER,
    fulfillment_role  VARCHAR(16) NOT NULL,
    is_active         BOOLEAN DEFAULT TRUE,
    created_at        TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS game_reward_redemptions (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id   UUID NOT NULL,
    reward_id    UUID NOT NULL,
    school_id    UUID NOT NULL,
    xp_spent     INTEGER NOT NULL,
    status       VARCHAR(16) DEFAULT 'PENDING',
    qr_code      TEXT,
    approved_by  UUID,
    approved_at  TIMESTAMPTZ,
    fulfilled_at TIMESTAMPTZ,
    created_at   TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_grr_status ON game_reward_redemptions(status);
CREATE INDEX IF NOT EXISTS idx_grr_school ON game_reward_redemptions(school_id);

-- ── Class goals ──────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS game_class_goals (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id        UUID NOT NULL,
    class_id         UUID,
    class_name       VARCHAR(32),
    section          VARCHAR(8),
    goal_type        VARCHAR(16) NOT NULL,
    target           INTEGER NOT NULL,
    current_progress INTEGER DEFAULT 0,
    reward           TEXT NOT NULL,
    completed        BOOLEAN DEFAULT FALSE,
    completed_at     TIMESTAMPTZ,
    deadline         DATE,
    created_by       UUID NOT NULL,
    created_at       TIMESTAMPTZ DEFAULT now()
);

-- ── Social / peer features ───────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS game_shoutouts (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sender_id   UUID NOT NULL,
    receiver_id UUID NOT NULL,
    school_id   UUID NOT NULL,
    template_id INTEGER NOT NULL,
    message     TEXT NOT NULL,
    is_public   BOOLEAN DEFAULT TRUE,
    is_deleted  BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_gsh_receiver ON game_shoutouts(receiver_id);
CREATE INDEX IF NOT EXISTS idx_gsh_school ON game_shoutouts(school_id);

CREATE TABLE IF NOT EXISTS game_mentor_assignments (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    mentor_id   UUID NOT NULL,
    mentee_id   UUID NOT NULL,
    school_id   UUID NOT NULL,
    assigned_by UUID NOT NULL,
    is_active   BOOLEAN DEFAULT TRUE,
    created_at  TIMESTAMPTZ DEFAULT now(),
    UNIQUE(mentor_id, mentee_id, school_id)
);

CREATE TABLE IF NOT EXISTS game_study_buddy_pairs (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student1_id UUID NOT NULL,
    student2_id UUID NOT NULL,
    school_id   UUID NOT NULL,
    class_id    UUID,
    assigned_by UUID NOT NULL,
    is_active   BOOLEAN DEFAULT TRUE,
    expires_at  TIMESTAMPTZ NOT NULL,
    created_at  TIMESTAMPTZ DEFAULT now()
);

-- ── Progression path progress ────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS game_student_path_progress (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id    UUID NOT NULL,
    path_code     VARCHAR(32) NOT NULL,
    current_xp    INTEGER DEFAULT 0,
    current_stage INTEGER DEFAULT 1,
    updated_at    TIMESTAMPTZ DEFAULT now(),
    UNIQUE(student_id, path_code)
);

-- ── Seasonal events ──────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS game_seasonal_events (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id  UUID,
    code       VARCHAR(64) UNIQUE NOT NULL,
    name       VARCHAR(128) NOT NULL,
    badge_id   UUID NOT NULL,
    quest_id   UUID NOT NULL,
    start_date DATE NOT NULL,
    end_date   DATE NOT NULL,
    is_active  BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- ── Motivation messages ──────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS game_motivation_messages (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    message_key  VARCHAR(64) UNIQUE NOT NULL,
    message_text TEXT NOT NULL,
    language     VARCHAR(8) DEFAULT 'en',
    is_active    BOOLEAN DEFAULT TRUE,
    created_at   TIMESTAMPTZ DEFAULT now()
);

-- ── Teacher encouragements ───────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS game_teacher_encouragements (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    teacher_id         UUID NOT NULL,
    student_id         UUID NOT NULL,
    school_id          UUID NOT NULL,
    amount             INTEGER NOT NULL,
    reason             TEXT NOT NULL,
    encouragement_type VARCHAR(16) NOT NULL,
    created_at         TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_gte_teacher_student ON game_teacher_encouragements(teacher_id, student_id);

-- ═══════════════════════════════════════════════════════════════════════════
-- SEED DATA: Default levels, badges, quests, houses, paths, titles, messages
-- ═══════════════════════════════════════════════════════════════════════════

-- Default levels (school_id = NULL = global default)
INSERT INTO game_level_definitions (school_id, level, xp_required, title, icon_name) VALUES
    (NULL, 1, 0, 'Beginner', 'sprout'),
    (NULL, 2, 100, 'Explorer', 'explore'),
    (NULL, 3, 300, 'Achiever', 'star'),
    (NULL, 4, 600, 'Rising Star', 'star'),
    (NULL, 5, 1000, 'Scholar', 'menu_book'),
    (NULL, 6, 1500, 'Expert', 'school'),
    (NULL, 7, 2200, 'Master', 'military_tech'),
    (NULL, 8, 3000, 'Champion', 'emoji_events'),
    (NULL, 9, 4000, 'Legend', 'workspace_premium'),
    (NULL, 10, 5500, 'Grandmaster', 'diamond')
ON CONFLICT DO NOTHING;

-- Default badges (40 total)
INSERT INTO game_badge_definitions (school_id, code, name, description, icon_name, category, rarity, xp_requirement, criteria_json) VALUES
-- Academic (8)
(NULL, 'first_steps', 'First Steps', 'Complete your first assessment', 'flag', 'ACADEMIC', 'COMMON', 10, '{"type":"count","source":"assessment","threshold":1}'),
(NULL, 'top_scorer', 'Top Scorer', 'Score 90%+ on any assessment', 'emoji_events', 'ACADEMIC', 'RARE', 20, '{"type":"count","source":"assessment","min_score":90,"threshold":1}'),
(NULL, 'subject_master', 'Subject Master', 'Complete 5 assessments in one subject', 'school', 'ACADEMIC', 'EPIC', 30, '{"type":"count","source":"assessment","threshold":5}'),
(NULL, 'homework_hero', 'Homework Hero', '10 on-time homework submissions', 'task_alt', 'ACADEMIC', 'RARE', 20, '{"type":"count","source":"homework","on_time":true,"threshold":10}'),
(NULL, 'early_bird', 'Early Bird', 'Submit before due date 5 times', 'schedule', 'ACADEMIC', 'RARE', 20, '{"type":"count","source":"homework","early":true,"threshold":5}'),
(NULL, 'quiz_champion', 'Quiz Champion', 'Complete 20 AI Tutor practices', 'psychology', 'ACADEMIC', 'EPIC', 30, '{"type":"count","source":"tutor","threshold":20}'),
(NULL, 'consistent_performer', 'Consistent Performer', 'Score 75%+ on 10 assessments', 'verified', 'ACADEMIC', 'EPIC', 30, '{"type":"count","source":"assessment","min_score":75,"threshold":10}'),
(NULL, 'perfect_score', 'Perfect Score', 'Score 100% on any assessment', 'stars', 'ACADEMIC', 'LEGENDARY', 50, '{"type":"count","source":"assessment","min_score":100,"threshold":1}'),
-- Attendance (4)
(NULL, 'perfect_week', 'Perfect Week', '5 consecutive present days', 'calendar_today', 'ATTENDANCE', 'RARE', 20, '{"type":"count","source":"attendance","status":"PRESENT","consecutive":true,"threshold":5}'),
(NULL, 'iron_streak', 'Iron Streak', '30 consecutive present days', 'whatshot', 'ATTENDANCE', 'EPIC', 30, '{"type":"count","source":"attendance","status":"PRESENT","consecutive":true,"threshold":30}'),
(NULL, 'semester_champion', 'Semester Champion', '90+ present days in a semester', 'military_tech', 'ATTENDANCE', 'LEGENDARY', 50, '{"type":"count","source":"attendance","status":"PRESENT","threshold":90}'),
(NULL, 'unbreakable', 'Unbreakable', 'Full year, no absences', 'shield', 'ATTENDANCE', 'MYTHIC', 100, '{"type":"count","source":"attendance","status":"PRESENT","consecutive":true,"threshold":200}'),
-- Co-Curricular (5)
(NULL, 'book_worm', 'Book Worm', 'Issue 5 library books', 'menu_book', 'CO_CURRICULAR', 'RARE', 20, '{"type":"count","source":"library","threshold":5}'),
(NULL, 'event_enthusiast', 'Event Enthusiast', 'Register for 3 events', 'event', 'CO_CURRICULAR', 'RARE', 20, '{"type":"count","source":"event","threshold":3}'),
(NULL, 'sports_star', 'Sports Star', 'Participate in sports day', 'sports', 'CO_CURRICULAR', 'RARE', 20, '{"type":"count","source":"event","category":"sports","threshold":1}'),
(NULL, 'stage_performer', 'Stage Performer', 'Participate in cultural event', 'theater_comedy', 'CO_CURRICULAR', 'EPIC', 30, '{"type":"count","source":"event","category":"cultural","threshold":1}'),
(NULL, 'competitor', 'Competitor', 'Represent school in inter-school event', 'sports_score', 'CO_CURRICULAR', 'EPIC', 30, '{"type":"count","source":"event","category":"inter_school","threshold":1}'),
-- Character (4)
(NULL, 'good_samaritan', 'Good Samaritan', 'Teacher-awarded for helping behavior', 'volunteer_activism', 'CHARACTER', 'RARE', 20, '{"type":"manual","awarded_by":"teacher"}'),
(NULL, 'class_leader', 'Class Leader', 'Selected as class monitor/leader', 'groups', 'CHARACTER', 'EPIC', 30, '{"type":"manual","awarded_by":"teacher"}'),
(NULL, 'model_student', 'Model Student', 'Full term with zero disciplinary issues', 'verified_user', 'CHARACTER', 'EPIC', 30, '{"type":"manual","awarded_by":"admin"}'),
(NULL, 'team_player', 'Team Player', 'Participate in group project/activity', 'handshake', 'CHARACTER', 'RARE', 20, '{"type":"manual","awarded_by":"teacher"}'),
-- Health (3)
(NULL, 'health_conscious', 'Health Conscious', 'Complete annual health checkup', 'health_and_safety', 'HEALTH', 'COMMON', 10, '{"type":"count","source":"health","threshold":1}'),
(NULL, 'fit_kid', 'Fit Kid', 'BMI in healthy range for the year', 'fitness_center', 'HEALTH', 'RARE', 20, '{"type":"manual","awarded_by":"admin"}'),
(NULL, 'protected', 'Protected', 'All vaccinations up to date', 'vaccines', 'HEALTH', 'COMMON', 10, '{"type":"manual","awarded_by":"admin"}'),
-- Milestone (6)
(NULL, 'rising_star', 'Rising Star', 'Reach Level 5', 'star', 'MILESTONE', 'RARE', 20, '{"type":"level","level":5}'),
(NULL, 'scholar_badge', 'Scholar', 'Reach Level 10', 'school', 'MILESTONE', 'EPIC', 30, '{"type":"level","level":10}'),
(NULL, 'legend_badge', 'Legend', 'Reach Level 25', 'workspace_premium', 'MILESTONE', 'LEGENDARY', 50, '{"type":"level","level":25}'),
(NULL, 'grandmaster_badge', 'Grandmaster', 'Reach Level 50', 'diamond', 'MILESTONE', 'MYTHIC', 100, '{"type":"level","level":50}'),
(NULL, 'anniversary', 'Anniversary', '1 year on the platform', 'cake', 'MILESTONE', 'EPIC', 30, '{"type":"anniversary","years":1}'),
(NULL, 'birthday_star', 'Birthday Star', 'Birthday badge (annual)', 'cake', 'MILESTONE', 'COMMON', 10, '{"type":"birthday"}')
ON CONFLICT DO NOTHING;

-- Default progression paths
INSERT INTO game_progression_paths (code, name, stage1_name, stage1_xp, stage2_name, stage2_xp, stage3_name, stage3_xp, stage4_name, stage4_xp) VALUES
    ('ACADEMIC', 'Academic Path', 'Beginner', 0, 'Scholar', 500, 'Subject Expert', 1500, 'Academic Champion', 3000),
    ('ATTENDANCE', 'Attendance Path', 'Present', 0, 'Consistent', 200, 'Iron Will', 600, 'Unbreakable', 1500),
    ('CO_CURRICULAR', 'Co-Curricular Path', 'Participant', 0, 'Enthusiast', 150, 'All-Rounder', 500, 'Versatile Star', 1200),
    ('CHARACTER', 'Character Path', 'Good Citizen', 0, 'Role Model', 200, 'Leader', 600, 'Mentor', 1500),
    ('DIGITAL', 'Digital Engagement Path', 'Newcomer', 0, 'Active', 100, 'Engaged', 400, 'Power User', 1000)
ON CONFLICT DO NOTHING;

-- Default titles
INSERT INTO game_titles (code, name, criteria_json, icon_name) VALUES
    ('the_bookworm', 'The Bookworm', '{"type":"count","source":"library","threshold":10}', 'menu_book'),
    ('math_wizard', 'Math Wizard', '{"type":"count","source":"assessment","subject":"math","min_score":90,"threshold":5}', 'calculate'),
    ('iron_will', 'Iron Will', '{"type":"count","source":"attendance","status":"PRESENT","consecutive":true,"threshold":30}', 'whatshot'),
    ('helping_hand', 'Helping Hand', '{"type":"count","source":"badge","category":"CHARACTER","threshold":3}', 'volunteer_activism'),
    ('quiz_master', 'Quiz Master', '{"type":"count","source":"tutor","threshold":50}', 'psychology'),
    ('rising_star_title', 'Rising Star', '{"type":"level","level":5}', 'star'),
    ('legend_title', 'Legend', '{"type":"level","level":25}', 'workspace_premium'),
    ('house_captain', 'House Captain', '{"type":"house_captain"}', 'military_tech'),
    ('mentor_title', 'Mentor', '{"type":"mentor","active_mentees":3}', 'school'),
    ('perfect_attendee', 'Perfect Attendee', '{"type":"count","source":"attendance","status":"PRESENT","threshold":120}', 'verified')
ON CONFLICT DO NOTHING;

-- Default motivation messages (growth-mindset language)
INSERT INTO game_motivation_messages (message_key, message_text, language) VALUES
    ('low_xp_3_days', 'A quick 10-minute practice can earn you 15 XP today. Want to try?', 'en'),
    ('catch_up_active', 'You''ve got a Boost Active — all your XP is worth 1.5x right now!', 'en'),
    ('streak_breaking', 'Your 5-day streak needs you! One activity today keeps it alive.', 'en'),
    ('level_close', 'Just 30 XP to Level 4! One homework submission does it.', 'en'),
    ('badge_available', 'You''re close to earning a badge — one more activity!', 'en'),
    ('after_low_assessment', 'Every expert was once a beginner. Try the AI Tutor practice!', 'en'),
    ('level_up', 'LEVEL UP! You''re now Level {level} — {title}! New badges unlocked!', 'en'),
    ('badge_earned', 'Badge earned: {badge_name}!', 'en'),
    ('class_goal_contribution', 'You contributed {percent}% of your class''s XP this week!', 'en'),
    ('streak_milestone', '{days}-day streak! Comeback quest unlocked — double XP tomorrow!', 'en'),
    ('mentor_nudge', 'Your mentee {name} hasn''t earned XP in 3 days. Send a shout-out!', 'en'),
    ('buddy_nudge', 'Your study buddy {name} is {xp} XP away from Level {level}. Help them!', 'en'),
    ('zero_xp', 'Your journey starts here! Complete your first activity to earn XP.', 'en'),
    ('zero_badges', '40 badges waiting to be discovered! Start with First Steps.', 'en'),
    ('zero_streak', 'Today is Day 1. One activity starts your streak!', 'en'),
    ('welcome_back', 'Welcome back! Double XP is active for your first activity.', 'en'),
    ('max_level', 'You''ve reached the top! Help others rise as a Mentor!', 'en'),
    ('parent_level_up', '{name} reached Level {level} — {title}!', 'en'),
    ('parent_badge_earned', '{name} earned the {badge} badge!', 'en'),
    ('parent_streak', '{name} is on a {days}-day streak! Keep encouraging them!', 'en'),
    ('parent_low_activity', '{name} hasn''t earned XP in 3 days. A little encouragement can help!', 'en'),
    ('parent_teacher_alert', '{name}''s teacher says he''s close to Level {level} — encourage him at home!', 'en')
ON CONFLICT DO NOTHING;

-- Default quest templates
INSERT INTO game_quest_definitions (school_id, code, name, description, quest_type, category, xp_reward, criteria_json, target_scope, duration_hours) VALUES
-- Daily quests
(NULL, 'daily_attend_all', 'Perfect Attendance Today', 'Attend all classes today', 'DAILY', 'ACADEMIC', 15, '{"type":"attendance_all_day"}', 'ALL', 24),
(NULL, 'daily_submit_homework', 'Submit Homework', 'Submit any pending homework today', 'DAILY', 'ACADEMIC', 20, '{"type":"count","source":"homework","threshold":1,"timeframe":"today"}', 'ALL', 24),
(NULL, 'daily_tutor_practice', 'AI Tutor Practice', 'Complete 1 AI Tutor practice', 'DAILY', 'ACADEMIC', 10, '{"type":"count","source":"tutor","threshold":1,"timeframe":"today"}', 'ALL', 24),
(NULL, 'daily_read_20', 'Read for 20 Minutes', 'Read a library book for 20 minutes', 'DAILY', 'CO_CURRICULAR', 10, '{"type":"count","source":"library","threshold":1,"timeframe":"today"}', 'ALL', 24),
(NULL, 'daily_help_friend', 'Help a Friend', 'Send a shout-out to a classmate', 'DAILY', 'CHARACTER', 10, '{"type":"count","source":"shoutout","threshold":1,"timeframe":"today"}', 'ALL', 24),
-- Weekly quests
(NULL, 'weekly_perfect_attendance', 'Perfect Attendance Week', 'Attend all classes this week', 'WEEKLY', 'CHARACTER', 75, '{"type":"attendance_all_week"}', 'ALL', 168),
(NULL, 'weekly_all_homework', 'Homework Champion', 'Submit all homework on time this week', 'WEEKLY', 'ACADEMIC', 50, '{"type":"count","source":"homework","on_time":true,"threshold":5,"timeframe":"week"}', 'ALL', 168),
(NULL, 'weekly_5_practices', 'Practice Makes Perfect', 'Complete 5 AI Tutor practices this week', 'WEEKLY', 'ACADEMIC', 40, '{"type":"count","source":"tutor","threshold":5,"timeframe":"week"}', 'ALL', 168),
(NULL, 'weekly_participate_event', 'Get Involved', 'Participate in any event this week', 'WEEKLY', 'CO_CURRICULAR', 30, '{"type":"count","source":"event","threshold":1,"timeframe":"week"}', 'ALL', 168),
-- Catch-up quests (bottom 25% only)
(NULL, 'catchup_3_homework', 'Comeback: Homework', 'Complete 3 homework assignments this week', 'CATCH_UP', 'ACADEMIC', 100, '{"type":"count","source":"homework","threshold":3,"timeframe":"week"}', 'BOTTOM_25', 168),
(NULL, 'catchup_2_practices', 'Comeback: Practice', 'Complete 2 AI Tutor practices', 'CATCH_UP', 'ACADEMIC', 80, '{"type":"count","source":"tutor","threshold":2,"timeframe":"week"}', 'BOTTOM_25', 168),
(NULL, 'catchup_3_attendance', 'Comeback: Attendance', 'Attend 3 days this week', 'CATCH_UP', 'CHARACTER', 90, '{"type":"count","source":"attendance","status":"PRESENT","threshold":3,"timeframe":"week"}', 'BOTTOM_25', 168)
ON CONFLICT DO NOTHING;

-- ═══════════════════════════════════════════════════════════════════════════
-- KILL SWITCH: Add gamification flag to app_config
-- ═══════════════════════════════════════════════════════════════════════════
-- The flags JSON in app_config already exists. We need to add the gamification
-- flags to it. This is done programmatically by GamificationSeeder on boot
-- (it reads the existing flags JSON, adds the gamification keys if missing,
-- and writes it back). No SQL needed here — the seeder handles it.
-- ═══════════════════════════════════════════════════════════════════════════
