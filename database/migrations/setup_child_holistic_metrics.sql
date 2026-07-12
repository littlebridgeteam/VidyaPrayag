-- Migration: Per-child holistic metrics derived from the Skill Test
-- These competencies and emotional-intelligence metrics are updated every time
-- a child completes a skill test attempt. TrackProgress reads them and falls
-- back to the CMS app_config templates if no test has been taken yet.

CREATE TABLE IF NOT EXISTS child_holistic_metrics (
    child_id            UUID PRIMARY KEY REFERENCES children(id) ON DELETE CASCADE,
    literacy            FLOAT DEFAULT 0.0,    -- English score %
    numeracy            FLOAT DEFAULT 0.0,    -- Mathematics score %
    creativity          FLOAT DEFAULT 0.0,    -- Science + GK / Environmental Awareness avg %
    empathy             FLOAT DEFAULT 0.0,
    resilience          FLOAT DEFAULT 0.0,    -- grows with repeated attempts
    social              FLOAT DEFAULT 0.0,
    confidence          FLOAT DEFAULT 0.0,    -- overall test score %
    last_attempt_id     UUID REFERENCES skill_test_attempts(id) ON DELETE SET NULL,
    updated_at          TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS ix_child_holistic_metrics_child ON child_holistic_metrics(child_id);
CREATE INDEX IF NOT EXISTS ix_child_holistic_metrics_updated ON child_holistic_metrics(updated_at);
