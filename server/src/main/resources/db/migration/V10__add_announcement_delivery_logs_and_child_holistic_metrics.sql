-- V10: Create announcement_delivery_logs and child_holistic_metrics
--   Both tables are defined in Tables.kt but were never provisioned in production.

-- ── announcement_delivery_logs ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS announcement_delivery_logs (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id             UUID NOT NULL,
    announcement_id       TEXT NOT NULL,
    channel               VARCHAR(16) NOT NULL,
    recipient_id          UUID,
    recipient_identifier  TEXT NOT NULL,
    status                VARCHAR(16) NOT NULL,
    provider_message_id   TEXT,
    error_message         TEXT,
    created_at            TIMESTAMP NOT NULL DEFAULT now(),
    updated_at            TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS ix_adl_school_announcement
    ON announcement_delivery_logs (school_id, announcement_id);
CREATE INDEX IF NOT EXISTS ix_adl_school_created
    ON announcement_delivery_logs (school_id, created_at);

-- ── child_holistic_metrics ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS child_holistic_metrics (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    child_id        UUID NOT NULL UNIQUE REFERENCES children(id),
    literacy        REAL NOT NULL DEFAULT 0,
    numeracy        REAL NOT NULL DEFAULT 0,
    creativity      REAL NOT NULL DEFAULT 0,
    empathy         REAL NOT NULL DEFAULT 0,
    resilience      REAL NOT NULL DEFAULT 0,
    social          REAL NOT NULL DEFAULT 0,
    confidence      REAL NOT NULL DEFAULT 0,
    last_attempt_id UUID,
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS ix_chm_child
    ON child_holistic_metrics (child_id);
CREATE INDEX IF NOT EXISTS ix_chm_updated
    ON child_holistic_metrics (updated_at);
