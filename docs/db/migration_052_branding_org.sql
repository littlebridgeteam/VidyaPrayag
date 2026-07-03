-- migration_052_branding_org.sql
-- School Branding Kit: Multi-campus branding support + asset upload infrastructure
--
-- Adds organization_id to school_branding so branches can inherit org-level
-- branding unless they have a branch-level override (is_customized=true).
-- Also adds BRANDING to the valid media kinds for Supabase Storage path isolation.
--
-- Depends on: migration_051_multi_branch.sql (school_organizations table)

-- ── Alter school_branding ──────────────────────────────────────────────
ALTER TABLE school_branding
    ADD COLUMN IF NOT EXISTS organization_id UUID REFERENCES school_organizations(id) ON DELETE SET NULL;

-- Index for org-level branding lookups
CREATE INDEX IF NOT EXISTS idx_school_branding_org ON school_branding(organization_id);

-- ── Backfill: link existing branding rows to their school's organization ─
UPDATE school_branding sb
SET organization_id = s.organization_id
FROM schools s
WHERE sb.school_id = s.id
  AND s.organization_id IS NOT NULL
  AND sb.organization_id IS NULL;
