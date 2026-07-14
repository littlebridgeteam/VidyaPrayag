-- V12: Apply all missing manual SQL patches as Flyway migration
--   These columns/tables were added via docs/db/migration_015, 017, 051, 052,
--   060, 064, 105 and database/migrations/*.sql but never as Flyway migrations.
--   All statements use IF NOT EXISTS — safe on databases where already applied.

-- ── migration_015_assessments.sql ────────────────────────────────────────────
ALTER TABLE assessments ADD COLUMN IF NOT EXISTS academic_year_id  uuid;
ALTER TABLE assessments ADD COLUMN IF NOT EXISTS assignment_id     uuid;
ALTER TABLE assessments ADD COLUMN IF NOT EXISTS class_id          uuid;
ALTER TABLE assessments ADD COLUMN IF NOT EXISTS subject_id        uuid;
ALTER TABLE assessments ADD COLUMN IF NOT EXISTS type              text   NOT NULL DEFAULT 'scheduled';
ALTER TABLE assessments ADD COLUMN IF NOT EXISTS pass_marks        integer;
ALTER TABLE assessments ADD COLUMN IF NOT EXISTS calendar_event_id uuid;
ALTER TABLE assessments ADD COLUMN IF NOT EXISTS status            text   NOT NULL DEFAULT 'draft';
ALTER TABLE assessments ADD COLUMN IF NOT EXISTS created_by        uuid;

ALTER TABLE assessment_marks ADD COLUMN IF NOT EXISTS student_ref uuid;
ALTER TABLE assessment_marks ADD COLUMN IF NOT EXISTS is_absent   boolean NOT NULL DEFAULT false;
ALTER TABLE assessment_marks ADD COLUMN IF NOT EXISTS remark      text;
ALTER TABLE assessment_marks ADD COLUMN IF NOT EXISTS entered_at  timestamptz;

-- backfill status
UPDATE assessments SET status = 'published'
 WHERE is_published = true AND status = 'draft';

-- backfill student_ref from legacy student_code
UPDATE assessment_marks am
   SET student_ref = s.id
  FROM assessments a, students s
 WHERE am.assessment_id = a.id
   AND s.school_id = a.school_id
   AND s.student_code = am.student_id
   AND am.student_ref IS NULL;

-- backfill entered_at
UPDATE assessment_marks SET entered_at = updated_at WHERE entered_at IS NULL;

-- ── migration_017_homework.sql ───────────────────────────────────────────────
ALTER TABLE homework ADD COLUMN IF NOT EXISTS assignment_id uuid    NULL;
ALTER TABLE homework ADD COLUMN IF NOT EXISTS class_id      uuid    NULL;
ALTER TABLE homework ADD COLUMN IF NOT EXISTS subject_id    uuid    NULL;
ALTER TABLE homework ADD COLUMN IF NOT EXISTS due_time      time    NULL;
ALTER TABLE homework ADD COLUMN IF NOT EXISTS allow_late    boolean NOT NULL DEFAULT false;

CREATE TABLE IF NOT EXISTS homework_attachments (
    id           uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    homework_id  uuid        NOT NULL,
    url          text        NOT NULL,
    filename     text        NOT NULL DEFAULT '',
    mime         text        NOT NULL DEFAULT '',
    size_bytes   bigint      NOT NULL DEFAULT 0,
    uploaded_by  uuid        NULL,
    created_at   timestamptz NOT NULL DEFAULT now()
);

ALTER TABLE homework_submissions ADD COLUMN IF NOT EXISTS student_uuid uuid        NULL;
ALTER TABLE homework_submissions ADD COLUMN IF NOT EXISTS grade        text        NULL;
ALTER TABLE homework_submissions ADD COLUMN IF NOT EXISTS reviewed_by  uuid        NULL;
ALTER TABLE homework_submissions ADD COLUMN IF NOT EXISTS reviewed_at  timestamptz NULL;

-- ── migration_051_multi_branch.sql ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS school_organizations (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            TEXT NOT NULL,
    description     TEXT,
    logo_url        TEXT,
    is_active       BOOLEAN NOT NULL DEFAULT true,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS student_transfers (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id      UUID NOT NULL,
    from_school_id  UUID NOT NULL,
    to_school_id    UUID NOT NULL,
    transfer_date   DATE NOT NULL,
    reason          TEXT,
    status          VARCHAR(16) NOT NULL DEFAULT 'pending',
    approved_by     UUID,
    created_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_student_transfers_student ON student_transfers(student_id);
CREATE INDEX IF NOT EXISTS idx_student_transfers_status ON student_transfers(status, transfer_date DESC);

ALTER TABLE app_users ADD COLUMN IF NOT EXISTS organization_id UUID;
ALTER TABLE app_users ADD COLUMN IF NOT EXISTS org_admin_role VARCHAR(16);

CREATE INDEX IF NOT EXISTS idx_app_users_organization ON app_users(organization_id) WHERE organization_id IS NOT NULL;

-- ── migration_052_alumni_management.sql ──────────────────────────────────────
-- (school_code, pan_number, g80_* already in V11 for schools table)

-- ── migration_060_scholarship_workflow.sql ───────────────────────────────────
ALTER TABLE scholarships ADD COLUMN IF NOT EXISTS school_id UUID;
ALTER TABLE scholarships ADD COLUMN IF NOT EXISTS scholarship_type VARCHAR(16) NOT NULL DEFAULT 'fixed';
ALTER TABLE scholarships ADD COLUMN IF NOT EXISTS waiver_percentage REAL;
ALTER TABLE scholarships ADD COLUMN IF NOT EXISTS numeric_amount DOUBLE PRECISION;
ALTER TABLE scholarships ADD COLUMN IF NOT EXISTS eligibility_criteria TEXT NOT NULL DEFAULT '';
ALTER TABLE scholarships ADD COLUMN IF NOT EXISTS start_date VARCHAR(12);
ALTER TABLE scholarships ADD COLUMN IF NOT EXISTS end_date VARCHAR(12);
ALTER TABLE scholarships ADD COLUMN IF NOT EXISTS is_renewable BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE scholarships ADD COLUMN IF NOT EXISTS renewal_period_months INTEGER;

CREATE INDEX IF NOT EXISTS idx_scholarships_school ON scholarships(school_id, is_active);

ALTER TABLE scholarship_applications ADD COLUMN IF NOT EXISTS scholarship_id UUID;
ALTER TABLE scholarship_applications ADD COLUMN IF NOT EXISTS student_id UUID;
ALTER TABLE scholarship_applications ADD COLUMN IF NOT EXISTS academic_year_id UUID;
ALTER TABLE scholarship_applications ADD COLUMN IF NOT EXISTS document_urls TEXT;
ALTER TABLE scholarship_applications ADD COLUMN IF NOT EXISTS parent_application_text TEXT;
ALTER TABLE scholarship_applications ADD COLUMN IF NOT EXISTS reviewed_at TIMESTAMP;
ALTER TABLE scholarship_applications ADD COLUMN IF NOT EXISTS reviewed_by UUID;
ALTER TABLE scholarship_applications ADD COLUMN IF NOT EXISTS remarks TEXT;
ALTER TABLE scholarship_applications ADD COLUMN IF NOT EXISTS disbursement_amount DOUBLE PRECISION;
ALTER TABLE scholarship_applications ADD COLUMN IF NOT EXISTS disbursement_date TIMESTAMP;
ALTER TABLE scholarship_applications ADD COLUMN IF NOT EXISTS disbursement_reference TEXT;

CREATE INDEX IF NOT EXISTS idx_scholarship_apps_status ON scholarship_applications(status, academic_year_id);
CREATE INDEX IF NOT EXISTS idx_scholarship_apps_parent ON scholarship_applications(parent_id);
CREATE INDEX IF NOT EXISTS idx_scholarship_apps_scholarship ON scholarship_applications(scholarship_id);

CREATE TABLE IF NOT EXISTS scholarship_renewals (
    id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    original_application_id  UUID NOT NULL,
    student_id               UUID NOT NULL,
    scholarship_id           UUID NOT NULL,
    school_id                UUID NOT NULL,
    academic_year_id         UUID NOT NULL,
    status                   VARCHAR(16) NOT NULL DEFAULT 'pending',
    document_urls            TEXT,
    applied_at               TIMESTAMP NOT NULL DEFAULT now(),
    reviewed_at              TIMESTAMP,
    reviewed_by              UUID,
    remarks                  TEXT
);

CREATE INDEX IF NOT EXISTS idx_scholarship_renewals_original ON scholarship_renewals(original_application_id);
CREATE INDEX IF NOT EXISTS idx_scholarship_renewals_student ON scholarship_renewals(student_id, academic_year_id);
CREATE INDEX IF NOT EXISTS idx_scholarship_renewals_school ON scholarship_renewals(school_id, status);

ALTER TABLE fee_records ADD COLUMN IF NOT EXISTS scholarship_id UUID;
ALTER TABLE fee_records ADD COLUMN IF NOT EXISTS scholarship_type VARCHAR(16);
ALTER TABLE fee_records ADD COLUMN IF NOT EXISTS scholarship_amount DOUBLE PRECISION;
ALTER TABLE fee_records ADD COLUMN IF NOT EXISTS original_amount DOUBLE PRECISION;

-- ── migration_064_tutor_2.sql ────────────────────────────────────────────────
ALTER TABLE assessments ADD COLUMN IF NOT EXISTS topic_id UUID;

CREATE TABLE IF NOT EXISTS tutor_sessions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id       UUID NOT NULL REFERENCES schools(id),
    child_id        UUID NOT NULL REFERENCES children(id),
    subject_id      UUID NOT NULL REFERENCES school_subjects(id),
    academic_year_id UUID REFERENCES academic_years(id),
    mode            VARCHAR(16) NOT NULL DEFAULT 'DOUBT',
    intent_class    VARCHAR(64),
    turns           JSONB NOT NULL DEFAULT '[]'::jsonb,
    grounded_refs   JSONB NOT NULL DEFAULT '[]'::jsonb,
    provider_used   VARCHAR(64),
    tokens_used     INTEGER NOT NULL DEFAULT 0,
    cache_hit       BOOLEAN NOT NULL DEFAULT false,
    safety_flag     VARCHAR(32),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_tutor_sessions_child_subject ON tutor_sessions(child_id, subject_id);
CREATE INDEX IF NOT EXISTS idx_tutor_sessions_school ON tutor_sessions(school_id);

-- ── migration_105_announcement_calendar_only.sql ─────────────────────────────
ALTER TABLE announcements ADD COLUMN IF NOT EXISTS is_calendar_only BOOLEAN DEFAULT FALSE;

-- ── database/migrations/add_pinned_screens_to_app_users.sql ──────────────────
ALTER TABLE app_users ADD COLUMN IF NOT EXISTS pinned_screens TEXT;
