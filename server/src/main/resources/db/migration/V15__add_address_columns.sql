-- V15: Add address column to students and non_teaching_staff tables.
-- Powers the "Address" row in the Student / Staff profile Contact sections.
-- Nullable so existing rows remain valid; the UI only renders the row when set.

ALTER TABLE students ADD COLUMN IF NOT EXISTS address TEXT;
ALTER TABLE non_teaching_staff ADD COLUMN IF NOT EXISTS address TEXT;
