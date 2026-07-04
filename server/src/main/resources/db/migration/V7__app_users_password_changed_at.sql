-- SEC-022: Password change session invalidation.
-- Add password_changed_at column to app_users so JWT tokens issued before
-- a password change can be rejected at validation time.

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'app_users' AND column_name = 'password_changed_at'
    ) THEN
        ALTER TABLE app_users ADD COLUMN password_changed_at TIMESTAMP WITHOUT TIME ZONE;
    END IF;
END $$;
