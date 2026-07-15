-- V6__extend_therapist_profiles_for_public_display.sql

ALTER TABLE therapist_profiles
    ADD COLUMN profile_image_url TEXT,
ADD COLUMN public_bio JSONB NOT NULL DEFAULT '[]'::jsonb,
ADD COLUMN languages JSONB NOT NULL DEFAULT '[]'::jsonb,
ADD COLUMN specialisms JSONB NOT NULL DEFAULT '[]'::jsonb,
ADD COLUMN display_order INTEGER NOT NULL DEFAULT 0,
ADD COLUMN is_publicly_visible BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE therapist_profiles
    ADD CONSTRAINT chk_therapist_public_bio_array
        CHECK (jsonb_typeof(public_bio) = 'array');

ALTER TABLE therapist_profiles
    ADD CONSTRAINT chk_therapist_languages_array
        CHECK (jsonb_typeof(languages) = 'array');

ALTER TABLE therapist_profiles
    ADD CONSTRAINT chk_therapist_specialisms_array
        CHECK (jsonb_typeof(specialisms) = 'array');

ALTER TABLE therapist_profiles
    ADD CONSTRAINT chk_therapist_display_order
        CHECK (display_order >= 0);

CREATE INDEX idx_therapist_profiles_public_display
    ON therapist_profiles (
                           is_publicly_visible,
                           display_order
        );