-- Migration 111: Syllabus Approval Workflow + NCERT Reference
-- Adds approval_status to curriculum_units (DRAFT → APPROVED | REJECTED)
-- Only APPROVED units are visible to parents.
-- Creates ncert_syllabus_reference table for auto-fill from NCERT curriculum.
-- All changes are ADDITIVE — no destructive ALTER or DROP.

-- ── ALTER TABLE: curriculum_units — add approval_status ────────────────
ALTER TABLE curriculum_units
    ADD COLUMN IF NOT EXISTS approval_status VARCHAR(12) NOT NULL DEFAULT 'APPROVED';

-- Backfill: existing units are already visible → mark APPROVED
UPDATE curriculum_units SET approval_status = 'APPROVED' WHERE approval_status IS NULL OR approval_status = '';

-- ── New Table: ncert_syllabus_reference ────────────────────────────────
-- Stores NCERT syllabus reference data for auto-fill.
-- Keyed by (class_level, subject_name) → returns chapters with topics.
-- populated by the server's NcertReferenceService on first access (lazy seed).
CREATE TABLE IF NOT EXISTS ncert_syllabus_reference (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    class_level     VARCHAR(8)  NOT NULL,   -- "Class 6", "Class 7", ... "Class 12"
    subject_name    VARCHAR(64) NOT NULL,   -- "Mathematics", "Science", "English" ...
    chapters_json   TEXT        NOT NULL DEFAULT '[]',  -- JSON: [{"title":"...","topics":[{"title":"..."}]}]
    source          VARCHAR(32) NOT NULL DEFAULT 'NCERT',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_ncert_ref_class_subject
    ON ncert_syllabus_reference(class_level, subject_name);

-- ── ALTER TABLE: daily_class_log — add source values support ───────────
-- The existing `source` column (VARCHAR(8)) supports TEACHER | AI.
-- No schema change needed — just documenting that AI-generated logs
-- will use source='AI' and is_ai_estimated=true.
