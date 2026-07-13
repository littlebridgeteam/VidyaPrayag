-- V11: Add missing columns to schools table
--   The schools table was provisioned with base columns only.
--   These columns were added via manual SQL patches (schema-patch-school-onboarding.sql,
--   migration_051_multi_branch.sql, migration_052_alumni_management.sql, add_school_cover_image.sql)
--   but never as Flyway migrations, so production is missing them.

-- cover_image_url (from database/migrations/add_school_cover_image.sql)
ALTER TABLE schools ADD COLUMN IF NOT EXISTS cover_image_url TEXT;

-- onboarding wizard columns (from docs/db/schema-patch-school-onboarding.sql)
ALTER TABLE schools ADD COLUMN IF NOT EXISTS onboarding_status VARCHAR(16) NOT NULL DEFAULT 'pending';
ALTER TABLE schools ADD COLUMN IF NOT EXISTS school_type TEXT;
ALTER TABLE schools ADD COLUMN IF NOT EXISTS affiliation_number TEXT;
ALTER TABLE schools ADD COLUMN IF NOT EXISTS year_established INTEGER;
ALTER TABLE schools ADD COLUMN IF NOT EXISTS website TEXT;
ALTER TABLE schools ADD COLUMN IF NOT EXISTS total_students INTEGER;
ALTER TABLE schools ADD COLUMN IF NOT EXISTS total_classes INTEGER;
ALTER TABLE schools ADD COLUMN IF NOT EXISTS academic_year_start_month TEXT;
ALTER TABLE schools ADD COLUMN IF NOT EXISTS grading_system TEXT;
ALTER TABLE schools ADD COLUMN IF NOT EXISTS onboarding_steps_done TEXT;

-- multi-branch columns (from docs/db/migration_051_multi_branch.sql)
ALTER TABLE schools ADD COLUMN IF NOT EXISTS organization_id UUID;
ALTER TABLE schools ADD COLUMN IF NOT EXISTS branch_name TEXT;

-- alumni / 80G columns (from docs/db/migration_052_alumni_management.sql)
ALTER TABLE schools ADD COLUMN IF NOT EXISTS school_code VARCHAR(20);
ALTER TABLE schools ADD COLUMN IF NOT EXISTS pan_number VARCHAR(20);
ALTER TABLE schools ADD COLUMN IF NOT EXISTS g80_registration_number VARCHAR(50);
ALTER TABLE schools ADD COLUMN IF NOT EXISTS g80_validity_date DATE;
ALTER TABLE schools ADD COLUMN IF NOT EXISTS g80_certificate_url TEXT;

-- backfill onboarding_status for already-onboarded schools
UPDATE schools SET onboarding_status = 'active'
    WHERE onboarded_at IS NOT NULL AND onboarding_status <> 'active';

-- indexes
CREATE INDEX IF NOT EXISTS ix_schools_onboarding_status
    ON schools (onboarding_status);
CREATE INDEX IF NOT EXISTS ix_schools_organization
    ON schools (organization_id) WHERE organization_id IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS idx_schools_code
    ON schools (school_code) WHERE school_code IS NOT NULL;
