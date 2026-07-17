-- Migration: create_game_student_combos.sql
-- GAM-017: Combo system — consecutive activity multipliers
-- Tracks per-student streaks for HOMEWORK, ATTENDANCE, STUDY, READING combo types

CREATE TABLE IF NOT EXISTS game_student_combos (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id    UUID NOT NULL,
    school_id     UUID NOT NULL,
    combo_type    VARCHAR(32) NOT NULL,
    streak_count  INTEGER NOT NULL DEFAULT 0,
    last_event_at TIMESTAMP NOT NULL DEFAULT now(),
    is_active     BOOLEAN NOT NULL DEFAULT true,
    created_at    TIMESTAMP NOT NULL DEFAULT now(),
    updated_at    TIMESTAMP NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_student_combo_type
    ON game_student_combos (student_id, combo_type);
