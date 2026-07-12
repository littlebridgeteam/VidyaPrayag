-- migration_072_language_pref_default.sql
-- Multi-Language Support: Change app_users.languagePref default from 'hi' to 'en'
-- Spec ref: MULTI_LANGUAGE_SPEC.md §6.9
--
-- Safe: multi-language feature is not yet implemented. No user has explicitly
-- chosen a language — all 'hi' values are from the column default.

BEGIN;

-- 1. Change column default from 'hi' to 'en'
ALTER TABLE app_users
    ALTER COLUMN language_pref SET DEFAULT 'en';

-- 2. Update existing rows with default 'hi' to 'en'
UPDATE app_users
    SET language_pref = 'en'
    WHERE language_pref = 'hi';

-- 3. Record migration in language_pref_history (audit trail)
INSERT INTO language_pref_history (user_id, school_id, old_lang, new_lang, changed_at, source)
SELECT id, school_id, 'hi', 'en', now(), 'migration'
FROM app_users;

COMMIT;
