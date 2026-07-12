-- =============================================================================
-- Migration: setup_exam_ecosystem_schema
-- Subject:   Exam Timetable + Syllabus Mapping ecosystem
--
-- TABLES CREATED
--   1. exam_timetables         — groups assessments into a named set (e.g. "Mid Term 2026")
--   2. exam_timetable_entries   — per-exam slots within a timetable (date, time, subject)
--   3. exam_syllabus_mapping    — many-to-many: assessment ↔ curriculum_units (what to study)
--   4. exam_reminder_log        — tracks which exams have had evening-before reminders sent
--
-- 100% SAFE TO RE-RUN: every CREATE TABLE uses IF NOT EXISTS.
-- =============================================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =============================================================================
-- 1. exam_timetables — a named collection of exams for a class+section
-- =============================================================================
CREATE TABLE IF NOT EXISTS exam_timetables (
    id              UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    school_id       UUID        NOT NULL,
    teacher_id      UUID        NOT NULL,
    class_name      TEXT        NOT NULL,
    section         VARCHAR(8)  NOT NULL DEFAULT 'A',
    academic_year_id UUID       NULL,
    name            TEXT        NOT NULL,
    term            VARCHAR(32) NULL,
    status          VARCHAR(16) NOT NULL DEFAULT 'draft',
    source_image_url TEXT       NULL,
    ai_used         BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS ix_exam_tt_school ON exam_timetables (school_id, class_name, section);
CREATE INDEX IF NOT EXISTS ix_exam_tt_status ON exam_timetables (school_id, status);

-- =============================================================================
-- 2. exam_timetable_entries — individual exam slots within a timetable
-- =============================================================================
CREATE TABLE IF NOT EXISTS exam_timetable_entries (
    id              UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    timetable_id    UUID        NOT NULL,
    assessment_id   UUID        NULL,
    calendar_event_id UUID      NULL,
    school_id       UUID        NOT NULL,
    exam_date       DATE        NOT NULL,
    start_time      TIME        NULL,
    end_time        TIME        NULL,
    subject         TEXT        NOT NULL,
    exam_name       TEXT        NOT NULL,
    max_marks       INTEGER     NOT NULL DEFAULT 100,
    room            VARCHAR(64) NULL,
    sort_order      INTEGER     NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS ix_exam_tte_tt ON exam_timetable_entries (timetable_id);
CREATE INDEX IF NOT EXISTS ix_exam_tte_date ON exam_timetable_entries (school_id, exam_date);
CREATE INDEX IF NOT EXISTS ix_exam_tte_assessment ON exam_timetable_entries (assessment_id);

-- =============================================================================
-- 3. exam_syllabus_mapping — which curriculum units to study for an exam
-- =============================================================================
CREATE TABLE IF NOT EXISTS exam_syllabus_mapping (
    id              UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    assessment_id   UUID        NOT NULL,
    curriculum_unit_id UUID     NOT NULL,
    school_id       UUID        NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE(assessment_id, curriculum_unit_id)
);
CREATE INDEX IF NOT EXISTS ix_esm_assessment ON exam_syllabus_mapping (assessment_id);
CREATE INDEX IF NOT EXISTS ix_esm_unit ON exam_syllabus_mapping (curriculum_unit_id);

-- =============================================================================
-- 4. exam_reminder_log — prevents duplicate evening-before reminders
-- =============================================================================
CREATE TABLE IF NOT EXISTS exam_reminder_log (
    id              UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    assessment_id   UUID        NOT NULL,
    school_id       UUID        NOT NULL,
    reminded_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE(assessment_id)
);
CREATE INDEX IF NOT EXISTS ix_erl_assessment ON exam_reminder_log (assessment_id);

-- =============================================================================
-- VERIFICATION QUERIES (safe to run, read-only)
-- =============================================================================
SELECT 'exam_timetables' AS table_name, COUNT(*) AS row_count FROM exam_timetables
UNION ALL
SELECT 'exam_timetable_entries', COUNT(*) FROM exam_timetable_entries
UNION ALL
SELECT 'exam_syllabus_mapping', COUNT(*) FROM exam_syllabus_mapping
UNION ALL
SELECT 'exam_reminder_log', COUNT(*) FROM exam_reminder_log;
