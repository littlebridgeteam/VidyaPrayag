-- Announcement delivery log table
-- Tracks every per-recipient delivery attempt for school announcements across
-- WhatsApp, push, SMS, and email channels.
CREATE TABLE IF NOT EXISTS announcement_delivery_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id UUID NOT NULL,
    announcement_id TEXT NOT NULL,
    channel VARCHAR(16) NOT NULL CHECK (channel IN ('whatsapp', 'push', 'sms', 'email')),
    recipient_id UUID NULL,
    recipient_identifier TEXT NOT NULL,
    status VARCHAR(16) NOT NULL CHECK (status IN ('queued', 'sent', 'delivered', 'read', 'failed', 'skipped')),
    provider_message_id TEXT NULL,
    error_message TEXT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_adl_school_announcement
    ON announcement_delivery_logs (school_id, announcement_id);
CREATE INDEX IF NOT EXISTS idx_adl_school_created
    ON announcement_delivery_logs (school_id, created_at DESC);

-- Backfill WhatsApp delivery history from existing whatsapp_logs table.
-- Only run once; subsequent deployments should skip rows that already exist.
INSERT INTO announcement_delivery_logs (
    id,
    school_id,
    announcement_id,
    channel,
    recipient_id,
    recipient_identifier,
    status,
    provider_message_id,
    error_message,
    created_at,
    updated_at
)
SELECT
    gen_random_uuid(),
    wl.school_id,
    wl.announcement_id,
    'whatsapp',
    NULL,
    wl.phone,
    CASE
        WHEN wl.status = 'QUEUED' THEN 'queued'
        WHEN wl.status = 'SENT' THEN 'sent'
        WHEN wl.status = 'DELIVERED' THEN 'delivered'
        WHEN wl.status = 'READ' THEN 'read'
        WHEN wl.status = 'FAILED' THEN 'failed'
        WHEN wl.status = 'SKIPPED' THEN 'skipped'
        ELSE 'queued'
    END,
    wl.provider_message_id,
    wl.error_message,
    wl.created_at,
    wl.created_at
FROM whatsapp_logs wl
LEFT JOIN announcement_delivery_logs adl
    ON adl.announcement_id = wl.announcement_id
    AND adl.channel = 'whatsapp'
    AND adl.recipient_identifier = wl.phone
WHERE adl.id IS NULL;
