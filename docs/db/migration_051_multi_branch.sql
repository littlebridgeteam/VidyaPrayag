-- migration_051_multi_branch.sql
-- Multi-Branch / School Chain Support (MULTI_BRANCH_SPEC.md)
--
-- Creates 2 new tables + adds nullable columns to 2 existing tables.
-- All changes are ADDITIVE — existing standalone schools remain unaffected.
--
-- Run order: after migration_050_health_records.sql (or any prior migration).
-- Rollback statements at the bottom.

-- ── New table: school_organizations ───────────────────────────────────
CREATE TABLE IF NOT EXISTS school_organizations (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            TEXT NOT NULL,
    description     TEXT,
    logo_url        TEXT,
    is_active       BOOLEAN NOT NULL DEFAULT true,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

-- ── New table: student_transfers ──────────────────────────────────────
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

-- ── Additive columns on schools ───────────────────────────────────────
ALTER TABLE schools ADD COLUMN IF NOT EXISTS organization_id UUID REFERENCES school_organizations(id);
ALTER TABLE schools ADD COLUMN IF NOT EXISTS branch_name TEXT;

CREATE INDEX IF NOT EXISTS idx_schools_organization ON schools(organization_id) WHERE organization_id IS NOT NULL;

-- ── Additive columns on app_users ─────────────────────────────────────
ALTER TABLE app_users ADD COLUMN IF NOT EXISTS organization_id UUID;
ALTER TABLE app_users ADD COLUMN IF NOT EXISTS org_admin_role VARCHAR(16);

CREATE INDEX IF NOT EXISTS idx_app_users_organization ON app_users(organization_id) WHERE organization_id IS NOT NULL;

-- ── ROLLBACK ──────────────────────────────────────────────────────────
-- DROP TABLE IF EXISTS student_transfers;
-- DROP TABLE IF EXISTS school_organizations;
-- ALTER TABLE schools DROP COLUMN IF EXISTS organization_id;
-- ALTER TABLE schools DROP COLUMN IF EXISTS branch_name;
-- ALTER TABLE app_users DROP COLUMN IF EXISTS organization_id;
-- ALTER TABLE app_users DROP COLUMN IF EXISTS org_admin_role;
