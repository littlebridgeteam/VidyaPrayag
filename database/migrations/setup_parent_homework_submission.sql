-- Migration: Parent homework submission (text + photo attachments)
-- Enables parents to submit homework on behalf of their child and teachers
-- to review those submissions with attachments.

-- 1. Text/note submitted by the parent alongside any photo attachments.
ALTER TABLE homework_submissions
    ADD COLUMN IF NOT EXISTS submission_text TEXT DEFAULT '';

-- 2. Attachments belonging to a student submission (photos of written work etc.)
CREATE TABLE IF NOT EXISTS homework_submission_attachments (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    submission_id UUID NOT NULL REFERENCES homework_submissions(id) ON DELETE CASCADE,
    url          TEXT NOT NULL,
    filename     TEXT DEFAULT '',
    mime         TEXT DEFAULT '',
    size_bytes   BIGINT DEFAULT 0,
    uploaded_by  UUID REFERENCES app_users(id) ON DELETE SET NULL,
    created_at   TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS ix_hsa_submission ON homework_submission_attachments(submission_id);
