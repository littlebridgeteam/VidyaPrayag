-- migration_071_language_pref_history.sql
-- Multi-Language Support: Language preference change history table
-- Spec ref: MULTI_LANGUAGE_SPEC.md §6.2

BEGIN;

CREATE TABLE IF NOT EXISTS language_pref_history (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL,
    school_id   UUID,
    old_lang    VARCHAR(8),
    new_lang    VARCHAR(8) NOT NULL,
    changed_at  TIMESTAMP NOT NULL DEFAULT now(),
    source      VARCHAR(16) NOT NULL DEFAULT 'app'
);

CREATE INDEX IF NOT EXISTS idx_language_pref_history_user
    ON language_pref_history(user_id, changed_at DESC);

CREATE INDEX IF NOT EXISTS idx_language_pref_history_school
    ON language_pref_history(school_id, changed_at DESC);

COMMIT;
