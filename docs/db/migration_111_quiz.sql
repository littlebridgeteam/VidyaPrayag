-- Migration 111: Agentic Quiz System
-- Creates 2 new tables for AI-generated quizzes:
--   quiz_questions  — structured question bank linked to homework
--   quiz_answers    — per-question student answers linked to submissions
-- Also adds additive columns to existing tables:
--   homework.is_quiz + homework.quiz_meta_json
--   homework_submissions.score + homework_submissions.rank
-- All changes are ADDITIVE — no destructive ALTER or DROP.

-- ── New Table: quiz_questions ────────────────────────────────────────
CREATE TABLE IF NOT EXISTS quiz_questions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    homework_id     UUID NOT NULL,
    question_type   VARCHAR(12) NOT NULL,
    question_text   TEXT NOT NULL,
    options_json    TEXT DEFAULT '[]',
    correct_answer  TEXT NOT NULL,
    explanation     TEXT DEFAULT '',
    difficulty_offset INTEGER NOT NULL DEFAULT 0,
    position        INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_qq_homework ON quiz_questions(homework_id, position);

-- ── New Table: quiz_answers ──────────────────────────────────────────
CREATE TABLE IF NOT EXISTS quiz_answers (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    submission_id   UUID NOT NULL,
    question_id     UUID NOT NULL,
    answer_text     TEXT NOT NULL,
    is_correct      BOOLEAN NOT NULL DEFAULT false,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_qa_submission ON quiz_answers(submission_id);
CREATE INDEX IF NOT EXISTS idx_qa_question ON quiz_answers(question_id);

-- ── ALTER TABLE: homework — add is_quiz + quiz_meta_json ─────────────
ALTER TABLE homework
    ADD COLUMN IF NOT EXISTS is_quiz BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN IF NOT EXISTS quiz_meta_json TEXT DEFAULT '{}';

-- ── ALTER TABLE: homework_submissions — add score + rank ─────────────
ALTER TABLE homework_submissions
    ADD COLUMN IF NOT EXISTS score INTEGER,
    ADD COLUMN IF NOT EXISTS rank INTEGER;
