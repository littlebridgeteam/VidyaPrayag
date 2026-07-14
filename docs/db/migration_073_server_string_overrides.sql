-- migration_073_server_string_overrides.sql
-- Multi-Language Support: Server string overrides table (FR-018)
-- Spec ref: MULTI_LANGUAGE_SPEC.md §6.2
--
-- Allows Super Admin to update server-level notification template translations
-- from the website without a server redeploy. Compiled Kotlin ServerStrings
-- serve as defaults; DB overrides take priority at runtime.

BEGIN;

CREATE TABLE IF NOT EXISTS server_string_overrides (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    string_key   VARCHAR(128) NOT NULL,
    lang         VARCHAR(8) NOT NULL,
    value        TEXT NOT NULL,
    updated_by   UUID,
    updated_at   TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE(string_key, lang)
);

COMMIT;
