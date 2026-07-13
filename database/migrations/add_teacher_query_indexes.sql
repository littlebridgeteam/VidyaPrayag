-- Migration: add_teacher_query_indexes.sql
-- Date: 2026-07-13
-- Purpose: Add missing indexes on hot teacher query paths to fix 15-38s response times.
--
-- The /teacher/classes endpoint runs ~7 queries per assignment (N+1 pattern).
-- Without these indexes, each query does a sequential scan on tables with
-- thousands of rows. With indexes, Postgres can use index-only scans.
--
-- Run in Supabase SQL Editor. All CREATE INDEX IF NOT EXISTS (idempotent).

-- 1. enrollments: roster lookup by (class_id, section, status)
CREATE INDEX IF NOT EXISTS idx_enrollments_class_section_status
    ON enrollments (class_id, section, status);

-- 2. attendance_records: per-assignment attendance queries filter by (school_id, assignment_id, date)
CREATE INDEX IF NOT EXISTS idx_att_records_assignment_date
    ON attendance_records (school_id, assignment_id, date);

-- 3. assessments: scopedAssessmentsInTxn loads all school active assessments
CREATE INDEX IF NOT EXISTS idx_assessments_school_active
    ON assessments (school_id, is_active);

-- 3b. assessments: lookup by assignment_id (typed scope binding)
CREATE INDEX IF NOT EXISTS idx_assessments_assignment
    ON assessments (assignment_id);

-- 4. assessment_marks: recentMarksInTxn filters by student_ref
CREATE INDEX IF NOT EXISTS idx_assessment_marks_student_ref
    ON assessment_marks (student_ref);

-- 5. teacher_periods: nextPeriodForInTxn + weeklyTimetableInTxn filter by (school_id, teacher_id, assignment_id, is_active)
CREATE INDEX IF NOT EXISTS idx_periods_teacher_assignment
    ON teacher_periods (school_id, teacher_id, assignment_id, is_active);

-- 6. teacher_subject_assignments: idx_tsa_school_active and idx_tsa_teacher
--    already defined in Tables.kt — verify they exist in DB, create if missing.
CREATE INDEX IF NOT EXISTS idx_tsa_school_active
    ON teacher_subject_assignments (school_id, is_active);

CREATE INDEX IF NOT EXISTS idx_tsa_teacher
    ON teacher_subject_assignments (teacher_id);
