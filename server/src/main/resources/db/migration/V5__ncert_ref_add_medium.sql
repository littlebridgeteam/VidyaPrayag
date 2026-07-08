-- SCH-016: ncert_syllabus_reference unique index missing medium column.
-- The original unique index idx_ncert_ref_class_subject(class_level, subject_name)
-- causes collisions when Hindi-medium and English-medium NCERT syllabi
-- share the same class_level + subject_name.

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'ncert_syllabus_reference' AND column_name = 'medium'
    ) THEN
        ALTER TABLE ncert_syllabus_reference ADD COLUMN medium VARCHAR(16) DEFAULT 'English';
    END IF;
END $$;

DO $$ BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE indexname = 'idx_ncert_ref_class_subject'
    ) THEN
        DROP INDEX IF EXISTS idx_ncert_ref_class_subject;
    END IF;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS idx_ncert_ref_class_subject_medium
    ON ncert_syllabus_reference (class_level, subject_name, medium);
