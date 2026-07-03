-- Migration 110: Agentic Syllabus Management
-- Creates 5 new tables for the AI-powered syllabus lifecycle:
--   syllabus_sources     — raw upload + AI-parsed JSON
--   daily_class_log      — structured "what was taught today" record
--   syllabus_pace_plan   — AI-estimated pace per assignment
--   syllabus_popup_prefs — teacher suppression prefs for daily check-in
--   syllabus_pace_alerts — pace deviation alerts with AI reconfirmation
-- Also adds additive columns to existing tables:
--   syllabus_progress.coverage_percent (partial coverage 0-100)
--   curriculum_units.depth (0=chapter, 1=topic, 2=subtopic)
-- All changes are ADDITIVE — no destructive ALTER or DROP.

-- ── New Table: syllabus_sources ──────────────────────────────────────
CREATE TABLE IF NOT EXISTS syllabus_sources (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id       UUID NOT NULL,
    assignment_id   UUID NOT NULL,
    source_type     VARCHAR(8) NOT NULL,
    source_url      TEXT,
    raw_text        TEXT,
    parsed_json     TEXT NOT NULL DEFAULT '{}',
    ai_provider     VARCHAR(32),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_ss_assignment ON syllabus_sources(assignment_id);
CREATE INDEX IF NOT EXISTS idx_ss_school ON syllabus_sources(school_id);

-- ── New Table: daily_class_log ───────────────────────────────────────
CREATE TABLE IF NOT EXISTS daily_class_log (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id       UUID NOT NULL,
    assignment_id   UUID NOT NULL,
    date            DATE NOT NULL,
    topic_ids       TEXT NOT NULL DEFAULT '[]',
    summary_text    TEXT DEFAULT '',
    coverage_pct    INTEGER NOT NULL DEFAULT 0,
    source          VARCHAR(8) NOT NULL,
    is_ai_estimated BOOLEAN NOT NULL DEFAULT false,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_dcl_assignment_date ON daily_class_log(assignment_id, date);
CREATE INDEX IF NOT EXISTS idx_dcl_school_date ON daily_class_log(school_id, date);

-- ── New Table: syllabus_pace_plan ────────────────────────────────────
CREATE TABLE IF NOT EXISTS syllabus_pace_plan (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id               UUID NOT NULL,
    assignment_id           UUID NOT NULL,
    academic_year_id        UUID,
    total_topics            INTEGER NOT NULL DEFAULT 0,
    total_classes_expected  INTEGER NOT NULL DEFAULT 0,
    classes_elapsed         INTEGER NOT NULL DEFAULT 0,
    expected_coverage_pct   INTEGER NOT NULL DEFAULT 0,
    actual_coverage_pct     INTEGER NOT NULL DEFAULT 0,
    ai_estimate_json        TEXT DEFAULT '{}',
    needs_recalc            BOOLEAN NOT NULL DEFAULT false,
    last_recalc_at          TIMESTAMPTZ,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_spp_assignment ON syllabus_pace_plan(assignment_id);
CREATE INDEX IF NOT EXISTS idx_spp_school ON syllabus_pace_plan(school_id);

-- ── New Table: syllabus_popup_prefs ──────────────────────────────────
CREATE TABLE IF NOT EXISTS syllabus_popup_prefs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    teacher_id      UUID NOT NULL,
    assignment_id   UUID,
    suppress_mode   VARCHAR(12) NOT NULL DEFAULT 'off',
    suppressed_until DATE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_spp_teacher_assignment ON syllabus_popup_prefs(teacher_id, assignment_id);

-- ── New Table: syllabus_pace_alerts ──────────────────────────────────
CREATE TABLE IF NOT EXISTS syllabus_pace_alerts (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id       UUID NOT NULL,
    assignment_id   UUID NOT NULL,
    alert_level     VARCHAR(12) NOT NULL,
    expected_pct    INTEGER NOT NULL,
    actual_pct      INTEGER NOT NULL,
    ai_confirmed    BOOLEAN NOT NULL DEFAULT false,
    ai_reconfirm_json TEXT DEFAULT '{}',
    notified_roles  TEXT NOT NULL DEFAULT '[]',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    resolved_at     TIMESTAMPTZ
);
CREATE INDEX IF NOT EXISTS idx_spa_school_active ON syllabus_pace_alerts(school_id) WHERE resolved_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_spa_assignment ON syllabus_pace_alerts(assignment_id);

-- ── ALTER TABLE: syllabus_progress — add coverage_percent ────────────
ALTER TABLE syllabus_progress
    ADD COLUMN IF NOT EXISTS coverage_percent INTEGER NOT NULL DEFAULT 0;

-- ── ALTER TABLE: curriculum_units — add depth ───────────────────────
ALTER TABLE curriculum_units
    ADD COLUMN IF NOT EXISTS depth INTEGER NOT NULL DEFAULT 0;

-- ── Backfill: curriculum_units.depth ─────────────────────────────────
-- Chapters (parentId = null) → depth = 0
UPDATE curriculum_units SET depth = 0 WHERE parent_id IS NULL;
-- Topics (parentId = a chapter, i.e. parent's parentId is null) → depth = 1
UPDATE curriculum_units cu
SET depth = 1
FROM curriculum_units parent
WHERE cu.parent_id = parent.id AND parent.parent_id IS NULL;
-- Subtopics (parentId = a topic, i.e. parent's parentId is non-null) → depth = 2
UPDATE curriculum_units cu
SET depth = 2
FROM curriculum_units parent
WHERE cu.parent_id = parent.id AND parent.parent_id IS NOT NULL;

-- ── Backfill: syllabus_progress.coverage_percent ─────────────────────
-- Where isCovered = true, set coverage_percent = 100
UPDATE syllabus_progress SET coverage_percent = 100 WHERE is_covered = true;
