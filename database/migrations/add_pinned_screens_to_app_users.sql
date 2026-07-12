-- Migration: add home-screen pinned shortcuts column to app_users
-- Used by: SchoolHomeScreenV2 pinned shortcuts feature
ALTER TABLE app_users ADD COLUMN IF NOT EXISTS pinned_screens TEXT;
