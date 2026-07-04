-- SCH-020: attendance_records.status has no CHECK constraint.
-- The status column accepts arbitrary strings. Enforce the valid set.
-- SCH-021: exam_results.status has no CHECK constraint.
-- The status column accepts arbitrary strings. Enforce the valid set.

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'ck_attendance_records_status'
          AND table_name = 'attendance_records'
    ) THEN
        ALTER TABLE attendance_records
        ADD CONSTRAINT ck_attendance_records_status
        CHECK (LOWER(status) IN ('present', 'absent', 'late', 'leave'));
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'ck_exam_results_status'
          AND table_name = 'exam_results'
    ) THEN
        ALTER TABLE exam_results
        ADD CONSTRAINT ck_exam_results_status
        CHECK (status IN ('Exceeding', 'Meeting', 'Below', 'Pending'));
    END IF;
END $$;
