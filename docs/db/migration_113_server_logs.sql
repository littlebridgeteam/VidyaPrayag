-- migration_113_server_logs.sql
-- Notification Deep-Linking & Backend Log Viewer Plan §3.1
-- Structured server-side log table for the super-admin Log Viewer.

CREATE TABLE IF NOT EXISTS server_logs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id       UUID,                        -- nullable: system-wide logs have no school
    timestamp       TIMESTAMP NOT NULL,
    level           VARCHAR(8) NOT NULL,         -- TRACE | DEBUG | INFO | WARN | ERROR
    category        VARCHAR(32) NOT NULL,        -- http | ai | job | auth | notification | pews | sync | general
    message         TEXT NOT NULL,
    actor_id        UUID,                        -- who triggered it (nullable = system)
    endpoint        TEXT,                        -- e.g. "POST /api/v1/school/attendance"
    status_code     INTEGER,                     -- HTTP status if applicable
    duration_ms     BIGINT,                      -- request duration if applicable
    details_json    TEXT DEFAULT '{}',           -- structured context (request body, error stack, AI tokens, etc.)
    created_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_sl_timestamp ON server_logs(timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_sl_level ON server_logs(level);
CREATE INDEX IF NOT EXISTS idx_sl_category ON server_logs(category);
CREATE INDEX IF NOT EXISTS idx_sl_school ON server_logs(school_id);
