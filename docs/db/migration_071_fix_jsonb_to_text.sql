-- =====================================================================
-- migration_071_fix_jsonb_to_text.sql
-- Fix: migration_071 created JSONB columns but Exposed uses text() for all
-- JSONB-as-text columns. Postgres refuses implicit varchar→jsonb cast,
-- causing INSERT failures. This alters all JSONB columns to TEXT.
--
-- Run in Supabase SQL Editor.
-- =====================================================================

-- platform_features
ALTER TABLE platform_features ALTER COLUMN dependencies TYPE TEXT USING dependencies::text;
ALTER TABLE platform_features ALTER COLUMN tags TYPE TEXT USING tags::text;
ALTER TABLE platform_features ALTER COLUMN metadata TYPE TEXT USING metadata::text;

-- platform_feature_flows
ALTER TABLE platform_feature_flows ALTER COLUMN flow_steps TYPE TEXT USING flow_steps::text;
ALTER TABLE platform_feature_flows ALTER COLUMN entry_points TYPE TEXT USING entry_points::text;
ALTER TABLE platform_feature_flows ALTER COLUMN exit_points TYPE TEXT USING exit_points::text;
ALTER TABLE platform_feature_flows ALTER COLUMN deep_links TYPE TEXT USING deep_links::text;
ALTER TABLE platform_feature_flows ALTER COLUMN edge_cases TYPE TEXT USING edge_cases::text;

-- platform_screens
ALTER TABLE platform_screens ALTER COLUMN permissions TYPE TEXT USING permissions::text;
ALTER TABLE platform_screens ALTER COLUMN user_actions TYPE TEXT USING user_actions::text;
ALTER TABLE platform_screens ALTER COLUMN connected_screens TYPE TEXT USING connected_screens::text;
ALTER TABLE platform_screens ALTER COLUMN metadata TYPE TEXT USING metadata::text;

-- platform_feature_apis
ALTER TABLE platform_feature_apis ALTER COLUMN db_entities TYPE TEXT USING db_entities::text;
ALTER TABLE platform_feature_apis ALTER COLUMN analytics_events TYPE TEXT USING analytics_events::text;
ALTER TABLE platform_feature_apis ALTER COLUMN notifications TYPE TEXT USING notifications::text;

-- platform_test_cases
ALTER TABLE platform_test_cases ALTER COLUMN test_steps TYPE TEXT USING test_steps::text;
ALTER TABLE platform_test_cases ALTER COLUMN devices TYPE TEXT USING devices::text;
ALTER TABLE platform_test_cases ALTER COLUMN os_versions TYPE TEXT USING os_versions::text;
ALTER TABLE platform_test_cases ALTER COLUMN metadata TYPE TEXT USING metadata::text;

-- platform_bugs
ALTER TABLE platform_bugs ALTER COLUMN steps_to_reproduce TYPE TEXT USING steps_to_reproduce::text;
ALTER TABLE platform_bugs ALTER COLUMN tags TYPE TEXT USING tags::text;
ALTER TABLE platform_bugs ALTER COLUMN metadata TYPE TEXT USING metadata::text;

-- platform_bug_comments
ALTER TABLE platform_bug_comments ALTER COLUMN mentions TYPE TEXT USING mentions::text;

-- platform_audit_log
ALTER TABLE platform_audit_log ALTER COLUMN old_snapshot TYPE TEXT USING old_snapshot::text;
ALTER TABLE platform_audit_log ALTER COLUMN new_snapshot TYPE TEXT USING new_snapshot::text;
