-- ============================================================================
-- V2__add_fk_constraints.sql — SCH-013: Add foreign key constraints
-- ============================================================================
-- Adds FK constraints to existing Postgres tables for data integrity.
-- Uses DO blocks to check if the constraint already exists before adding,
-- so this is idempotent and safe to re-run.
-- ============================================================================

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_user_sessions_user_id') THEN
        ALTER TABLE user_sessions
            ADD CONSTRAINT fk_user_sessions_user_id
            FOREIGN KEY (user_id) REFERENCES app_users(id) ON DELETE CASCADE;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_messages_thread_id') THEN
        ALTER TABLE messages
            ADD CONSTRAINT fk_messages_thread_id
            FOREIGN KEY (thread_id) REFERENCES message_threads(id) ON DELETE CASCADE;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_messages_reply_to') THEN
        ALTER TABLE messages
            ADD CONSTRAINT fk_messages_reply_to
            FOREIGN KEY (reply_to_id) REFERENCES messages(id) ON DELETE SET NULL;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_message_status_message_id') THEN
        ALTER TABLE message_status
            ADD CONSTRAINT fk_message_status_message_id
            FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_message_attachments_message_id') THEN
        ALTER TABLE message_attachments
            ADD CONSTRAINT fk_message_attachments_message_id
            FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_syllabus_progress_unit_id') THEN
        ALTER TABLE syllabus_progress
            ADD CONSTRAINT fk_syllabus_progress_unit_id
            FOREIGN KEY (unit_id) REFERENCES curriculum_units(id) ON DELETE CASCADE;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_syllabus_progress_assignment_id') THEN
        ALTER TABLE syllabus_progress
            ADD CONSTRAINT fk_syllabus_progress_assignment_id
            FOREIGN KEY (assignment_id) REFERENCES teacher_subject_assignments(id) ON DELETE RESTRICT;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_transport_assignments_route_id') THEN
        ALTER TABLE transport_assignments
            ADD CONSTRAINT fk_transport_assignments_route_id
            FOREIGN KEY (route_id) REFERENCES transport_routes(id) ON DELETE RESTRICT;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_transport_assignments_stop_id') THEN
        ALTER TABLE transport_assignments
            ADD CONSTRAINT fk_transport_assignments_stop_id
            FOREIGN KEY (stop_id) REFERENCES transport_stops(id) ON DELETE RESTRICT;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_transport_assignments_vehicle_id') THEN
        ALTER TABLE transport_assignments
            ADD CONSTRAINT fk_transport_assignments_vehicle_id
            FOREIGN KEY (vehicle_id) REFERENCES transport_vehicles(id) ON DELETE RESTRICT;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_transport_tracking_vehicle_id') THEN
        ALTER TABLE transport_tracking
            ADD CONSTRAINT fk_transport_tracking_vehicle_id
            FOREIGN KEY (vehicle_id) REFERENCES transport_vehicles(id) ON DELETE CASCADE;
    END IF;
END $$;
