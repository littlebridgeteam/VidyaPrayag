-- ============================================================================
-- COMBINED: Migration 110 + 111 + 111b — Run this in Supabase SQL Editor
-- Creates all 7 missing tables + additive columns
-- ============================================================================

-- ════════════════════════════════════════════════════════════════════════════
-- MIGRATION 110: Agentic Syllabus Management (5 new tables + 2 ALTER)
-- ════════════════════════════════════════════════════════════════════════════

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

ALTER TABLE syllabus_progress
    ADD COLUMN IF NOT EXISTS coverage_percent INTEGER NOT NULL DEFAULT 0;

ALTER TABLE curriculum_units
    ADD COLUMN IF NOT EXISTS depth INTEGER NOT NULL DEFAULT 0;

UPDATE curriculum_units SET depth = 0 WHERE parent_id IS NULL;
UPDATE curriculum_units cu
SET depth = 1
FROM curriculum_units parent
WHERE cu.parent_id = parent.id AND parent.parent_id IS NULL;
UPDATE curriculum_units cu
SET depth = 2
FROM curriculum_units parent
WHERE cu.parent_id = parent.id AND parent.parent_id IS NOT NULL;

UPDATE syllabus_progress SET coverage_percent = 100 WHERE is_covered = true;

-- ════════════════════════════════════════════════════════════════════════════
-- MIGRATION 111a: Quiz System (2 new tables + 2 ALTER)
-- ════════════════════════════════════════════════════════════════════════════

CREATE TABLE IF NOT EXISTS quiz_questions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    homework_id     UUID NOT NULL,
    question_type   VARCHAR(12) NOT NULL,
    question_text   TEXT NOT NULL,
    options_json    TEXT DEFAULT '[]',
    correct_answer  TEXT NOT NULL,
    explanation     TEXT DEFAULT '',
    difficulty_offset INTEGER NOT NULL DEFAULT 0,
    position        INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_qq_homework ON quiz_questions(homework_id, position);

CREATE TABLE IF NOT EXISTS quiz_answers (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    submission_id   UUID NOT NULL,
    question_id     UUID NOT NULL,
    answer_text     TEXT NOT NULL,
    is_correct      BOOLEAN NOT NULL DEFAULT false,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_qa_submission ON quiz_answers(submission_id);
CREATE INDEX IF NOT EXISTS idx_qa_question ON quiz_answers(question_id);

ALTER TABLE homework
    ADD COLUMN IF NOT EXISTS is_quiz BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN IF NOT EXISTS quiz_meta_json TEXT DEFAULT '{}';

ALTER TABLE homework_submissions
    ADD COLUMN IF NOT EXISTS score INTEGER,
    ADD COLUMN IF NOT EXISTS rank INTEGER;

-- ════════════════════════════════════════════════════════════════════════════
-- MIGRATION 111b: Syllabus Approval + NCERT Reference (1 new table + 1 ALTER)
-- ════════════════════════════════════════════════════════════════════════════

ALTER TABLE curriculum_units
    ADD COLUMN IF NOT EXISTS approval_status VARCHAR(12) NOT NULL DEFAULT 'APPROVED';

UPDATE curriculum_units SET approval_status = 'APPROVED' WHERE approval_status IS NULL OR approval_status = '';

CREATE TABLE IF NOT EXISTS ncert_syllabus_reference (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    class_level     VARCHAR(8)  NOT NULL,
    subject_name    VARCHAR(64) NOT NULL,
    chapters_json   TEXT        NOT NULL DEFAULT '[]',
    source          VARCHAR(32) NOT NULL DEFAULT 'NCERT',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_ncert_ref_class_subject
    ON ncert_syllabus_reference(class_level, subject_name);
