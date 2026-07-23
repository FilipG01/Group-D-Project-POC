CREATE TABLE therapist_profile_submissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    therapist_user_id UUID NOT NULL,

    qualifications TEXT NOT NULL,
    registration_number VARCHAR(100) NOT NULL,
    years_experience INTEGER NOT NULL DEFAULT 0,
    bio TEXT,
    is_accepting_clients BOOLEAN NOT NULL DEFAULT TRUE,
    profile_image_url TEXT,
    public_bio JSONB NOT NULL DEFAULT '[]'::jsonb,
    languages JSONB NOT NULL DEFAULT '[]'::jsonb,
    specialisms JSONB NOT NULL DEFAULT '[]'::jsonb,

    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    review_notes TEXT,
    reviewed_by_user_id UUID,
    submitted_at TIMESTAMPTZ,
    reviewed_at TIMESTAMPTZ,
    approved_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_profile_submission_therapist
        FOREIGN KEY (therapist_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_profile_submission_reviewer
        FOREIGN KEY (reviewed_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT chk_profile_submission_years CHECK (years_experience >= 0),
    CONSTRAINT chk_profile_submission_status CHECK (
        status IN ('DRAFT', 'SUBMITTED', 'CHANGES_REQUESTED', 'REJECTED', 'APPROVED')
    ),
    CONSTRAINT chk_profile_submission_public_bio CHECK (jsonb_typeof(public_bio) = 'array'),
    CONSTRAINT chk_profile_submission_languages CHECK (jsonb_typeof(languages) = 'array'),
    CONSTRAINT chk_profile_submission_specialisms CHECK (jsonb_typeof(specialisms) = 'array')
);

CREATE UNIQUE INDEX uq_active_profile_submission_per_therapist
    ON therapist_profile_submissions (therapist_user_id)
    WHERE status IN ('DRAFT', 'SUBMITTED', 'CHANGES_REQUESTED', 'REJECTED');

CREATE INDEX idx_profile_submissions_status_submitted
    ON therapist_profile_submissions (status, submitted_at);

CREATE TRIGGER trg_therapist_profile_submissions_updated_at
BEFORE UPDATE ON therapist_profile_submissions
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();
