-- migration_074_server_string_override_history.sql
-- Translation Management Dashboard: audit log for server string overrides
--
-- Tracks every create/update/delete on server_string_overrides so the
-- admin dashboard can show version history and who changed what.

BEGIN;

CREATE TABLE IF NOT EXISTS server_string_override_history (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    string_key   VARCHAR(128) NOT NULL,
    lang         VARCHAR(8) NOT NULL,
    old_value    TEXT,
    new_value    TEXT NOT NULL,
    action       VARCHAR(16) NOT NULL,
    changed_by   UUID,
    changed_at   TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_override_history_key
    ON server_string_override_history(string_key);

CREATE INDEX IF NOT EXISTS idx_override_history_lang
    ON server_string_override_history(lang);

CREATE INDEX IF NOT EXISTS idx_override_history_changed_at
    ON server_string_override_history(changed_at DESC);

COMMIT;
