-- Migration: add merged registration + onboarding fields to schools and app_users
-- Used by: new unified 4-step registration + onboarding flow (Figma "New School Onboarding Flow")
-- These columns support the simplified onboarding that replaces the old 4-step backend wizard.

-- schools: new fields collected in Step 3 (School Identity) and Step 4 (Academic Year)
ALTER TABLE schools ADD COLUMN IF NOT EXISTS short_name TEXT;
ALTER TABLE schools ADD COLUMN IF NOT EXISTS academic_year_label TEXT;
ALTER TABLE schools ADD COLUMN IF NOT EXISTS academic_year_start_date TEXT;
ALTER TABLE schools ADD COLUMN IF NOT EXISTS academic_year_end_date TEXT;
ALTER TABLE schools ADD COLUMN IF NOT EXISTS working_days TEXT;
ALTER TABLE schools ADD COLUMN IF NOT EXISTS school_start_time TEXT;
ALTER TABLE schools ADD COLUMN IF NOT EXISTS school_end_time TEXT;
ALTER TABLE schools ADD COLUMN IF NOT EXISTS periods_per_day INTEGER;

-- app_users: admin role label collected in Step 1 (Basic Details)
ALTER TABLE app_users ADD COLUMN IF NOT EXISTS admin_role TEXT;
