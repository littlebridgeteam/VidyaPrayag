-- Migration: add campus cover photo column to schools table
-- Used by: SchoolBrandingScreenV2 cover photo upload
ALTER TABLE schools ADD COLUMN IF NOT EXISTS cover_image_url TEXT;
