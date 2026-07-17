-- V14: Add fee & salary management tables (Phase 1 — ledger-based)
--   database/migrations/setup_fee_salary_management.sql was never converted
--   to a Flyway migration. The fee_late_fee_tiers table was added to the
--   Kotlin Exposed schema but never provisioned on Supabase, causing boot
--   failure: "Schema validation FOUND 1 MISSING table(s): [fee_late_fee_tiers]"
--   All statements use IF NOT EXISTS — safe on DBs where already applied manually.

-- 1. Fee Structures — recurring monthly fee templates per school/class
CREATE TABLE IF NOT EXISTS fee_structures (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id   UUID NOT NULL,
    class_id    UUID,
    title       TEXT NOT NULL,
    description TEXT,
    amount      DOUBLE PRECISION NOT NULL DEFAULT 0,
    currency    VARCHAR(8) NOT NULL DEFAULT 'INR',
    frequency   VARCHAR(16) NOT NULL DEFAULT 'MONTHLY',
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX IF NOT EXISTS ux_fee_structures_school_class_title
    ON fee_structures (school_id, class_id, title);
CREATE INDEX IF NOT EXISTS idx_fee_structures_school
    ON fee_structures (school_id);

-- 2. Fee Additional Charges — one-off charges per student/month
CREATE TABLE IF NOT EXISTS fee_additional_charges (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id   UUID NOT NULL,
    child_id    UUID NOT NULL,
    class_id    UUID,
    month       VARCHAR(7) NOT NULL,
    title       TEXT NOT NULL,
    description TEXT,
    amount      DOUBLE PRECISION NOT NULL DEFAULT 0,
    currency    VARCHAR(8) NOT NULL DEFAULT 'INR',
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_fee_charges_school_child
    ON fee_additional_charges (school_id, child_id);
CREATE INDEX IF NOT EXISTS idx_fee_charges_month
    ON fee_additional_charges (month);

-- 3. Fee Reminder Config — per-school reminder day
CREATE TABLE IF NOT EXISTS fee_reminder_config (
    school_id    UUID PRIMARY KEY,
    reminder_day INTEGER NOT NULL DEFAULT 5,
    is_active    BOOLEAN NOT NULL DEFAULT TRUE,
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_reminder_day CHECK (reminder_day >= 1 AND reminder_day <= 28)
);

-- 4. Salary Records — teacher/staff salary per month
CREATE TABLE IF NOT EXISTS salary_records (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id   UUID NOT NULL,
    teacher_id  UUID NOT NULL,
    month       VARCHAR(7) NOT NULL,
    base_salary DOUBLE PRECISION NOT NULL DEFAULT 0,
    allowances  DOUBLE PRECISION NOT NULL DEFAULT 0,
    deductions  DOUBLE PRECISION NOT NULL DEFAULT 0,
    net_amount  DOUBLE PRECISION NOT NULL DEFAULT 0,
    currency    VARCHAR(8) NOT NULL DEFAULT 'INR',
    status      VARCHAR(16) NOT NULL DEFAULT 'UNPAID',
    paid_at     TIMESTAMPTZ,
    notes       TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX IF NOT EXISTS ux_salary_school_teacher_month
    ON salary_records (school_id, teacher_id, month);
CREATE INDEX IF NOT EXISTS idx_salary_school_month
    ON salary_records (school_id, month);

-- 5. Fee Late Fee Tiers — tiered late fee configuration per school
CREATE TABLE IF NOT EXISTS fee_late_fee_tiers (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id       UUID NOT NULL,
    days_after_due  INTEGER NOT NULL,
    amount          DOUBLE PRECISION NOT NULL DEFAULT 0,
    currency        VARCHAR(8) NOT NULL DEFAULT 'INR',
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX IF NOT EXISTS ux_fee_late_tiers_school_days
    ON fee_late_fee_tiers (school_id, days_after_due);
