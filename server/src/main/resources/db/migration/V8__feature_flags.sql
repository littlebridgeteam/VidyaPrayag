-- V8__feature_flags.sql
-- GAP-019: General-purpose feature flag system.
-- Replaces the PEWS-only kill switches with a unified flag table that any
-- feature module can read. Flags are keyed by (scope, key) so the same
-- key name (e.g. "enabled") can exist under different scopes.
--
-- Columns:
--   id          UUID PK
--   scope       varchar(64)  — feature area ("pews", "tutor", "messaging", "global", …)
--   key         varchar(64)  — flag name ("enabled", "max_retries", …)
--   value       text         — string value (caller parses to bool/int/etc)
--   is_enabled  boolean      — convenience boolean for on/off flags
--   description text         — human-readable purpose
--   updated_at  timestamp    — last modification
--   created_at  timestamp    — creation
--
-- Unique constraint on (scope, key) so lookups are O(1).

CREATE TABLE IF NOT EXISTS feature_flags (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    scope       VARCHAR(64)  NOT NULL,
    key         VARCHAR(64)  NOT NULL,
    value       TEXT,
    is_enabled  BOOLEAN      NOT NULL DEFAULT false,
    description TEXT,
    updated_at  TIMESTAMP    NOT NULL DEFAULT now(),
    created_at  TIMESTAMP    NOT NULL DEFAULT now(),
    UNIQUE (scope, key)
);

-- Seed: migrate existing PEWS kill switches into the unified table.
INSERT INTO feature_flags (scope, key, is_enabled, value, description)
VALUES
    ('global', 'enabled', true, 'true', 'Master kill switch — disables all optional features when false'),
    ('pews',   'enabled', true, 'true', 'PEWS module master switch'),
    ('pews',   'sense',   true, 'true', 'PEWS Sense module (risk scoring)'),
    ('pews',   'triage',  true, 'true', 'PEWS Triage module (cohort routing)'),
    ('pews',   'caseworker', true, 'true', 'PEWS Caseworker module (interventions)'),
    ('pews',   'act',     true, 'true', 'PEWS Act module (nudge dispatch)'),
    ('pews',   'learn',   true, 'true', 'PEWS Learn module (effectiveness)'),
    ('tutor',  'enabled', true, 'true', 'AI Tutor master switch'),
    ('messaging', 'enabled', true, 'true', 'Messaging system master switch'),
    ('scheduled_messages', 'enabled', true, 'true', 'Scheduled message dispatch')
ON CONFLICT (scope, key) DO NOTHING;

-- Index for fast scope-level lookups.
CREATE INDEX IF NOT EXISTS idx_feature_flags_scope ON feature_flags(scope);
