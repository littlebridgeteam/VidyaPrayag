-- SCH-015: exam_results unique index missing section column.
-- The original unique index ux_exam_results_unique(school_id, test, class_name, subject, student_id)
-- causes collisions when two students in different sections of the same class
-- have the same student_id prefix and take the same test.

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'exam_results' AND column_name = 'section'
    ) THEN
        ALTER TABLE exam_results ADD COLUMN section VARCHAR(8) DEFAULT 'A';
    END IF;
END $$;

DO $$ BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE indexname = 'ux_exam_results_unique'
    ) THEN
        ALTER TABLE exam_results DROP CONSTRAINT IF EXISTS ux_exam_results_unique;
    END IF;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS ux_exam_results_unique
    ON exam_results (school_id, test, class_name, section, subject, student_id);
