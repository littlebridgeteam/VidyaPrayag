-- =====================================================================
-- People Tab Enrichment Migration
-- Adds: staff_shifts, staff_check_ins, teacher_ratings tables
-- Adds: employee_id, shift_id columns to non_teaching_staff
-- Idempotent — safe to re-run.
-- =====================================================================

-- 1. New columns on non_teaching_staff
ALTER TABLE non_teaching_staff ADD COLUMN IF NOT EXISTS employee_id varchar;
ALTER TABLE non_teaching_staff ADD COLUMN IF NOT EXISTS shift_id uuid;

-- 2. staff_shifts — shift definitions per staff member
CREATE TABLE IF NOT EXISTS staff_shifts (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id   UUID NOT NULL,
    staff_id    UUID NOT NULL,
    shift_name  VARCHAR(64) NOT NULL,
    start_time  VARCHAR(8) NOT NULL,
    end_time    VARCHAR(8) NOT NULL,
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 3. staff_check_ins — daily check-in/check-out tracking
CREATE TABLE IF NOT EXISTS staff_check_ins (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id   UUID NOT NULL,
    staff_id    UUID NOT NULL,
    check_in_at TIMESTAMPTZ NOT NULL,
    check_out_at TIMESTAMPTZ,
    date        DATE NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 4. teacher_ratings — teacher rating for card display
CREATE TABLE IF NOT EXISTS teacher_ratings (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id   UUID NOT NULL,
    teacher_id  UUID NOT NULL,
    rating      INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    rated_by    UUID,
    feedback    TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes for query performance
CREATE INDEX IF NOT EXISTS idx_staff_shifts_school_staff ON staff_shifts(school_id, staff_id);
CREATE INDEX IF NOT EXISTS idx_staff_check_ins_school_staff_date ON staff_check_ins(school_id, staff_id, date);
CREATE INDEX IF NOT EXISTS idx_teacher_ratings_school_teacher ON teacher_ratings(school_id, teacher_id);
