-- Bug 19: Add admission_date column to students table
-- Stops using created_at as admission date; stores explicit admission date instead.
-- Falls back to created_at for existing rows via UPDATE.

ALTER TABLE students ADD COLUMN IF NOT EXISTS admission_date DATE;

-- Backfill: set admission_date to the date portion of created_at for existing rows
UPDATE students
SET admission_date = DATE(created_at)
WHERE admission_date IS NULL;
