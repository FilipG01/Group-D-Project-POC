-- V11__create_appointments.sql

-- created table
-- appointments

CREATE TABLE appointments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    client_user_id UUID NOT NULL,
    therapist_user_id UUID NOT NULL,
    created_by_user_id UUID NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'REQUESTED',
    location_type VARCHAR(30) NOT NULL DEFAULT 'ONLINE',

    scheduled_start TIMESTAMPTZ NOT NULL,
    scheduled_end TIMESTAMPTZ NOT NULL,

    client_notes TEXT,
    meeting_link VARCHAR(500),
    cancellation_reason TEXT,
    cancelled_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_appointments_client_user
    FOREIGN KEY (client_user_id) REFERENCES users(id) ON DELETE CASCADE,

    CONSTRAINT fk_appointments_therapist_user
    FOREIGN KEY (therapist_user_id) REFERENCES users(id) ON DELETE CASCADE,

    CONSTRAINT fk_appointments_created_by_user
    FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE CASCADE,

    CONSTRAINT chk_appointments_status CHECK (
        status IN ('REQUESTED', 'CONFIRMED', 'COMPLETED', 'CANCELLED', 'NO_SHOW')
        ),

    CONSTRAINT chk_appointments_location_type CHECK (
        location_type IN ('ONLINE', 'IN_PERSON', 'PHONE')
        ),

    CONSTRAINT chk_appointments_time_order CHECK (
        scheduled_end > scheduled_start
        )
    );

CREATE INDEX idx_appointments_client_user_id
    ON appointments(client_user_id);

CREATE INDEX idx_appointments_therapist_user_id
    ON appointments(therapist_user_id);

CREATE INDEX idx_appointments_scheduled_start
    ON appointments(scheduled_start);

CREATE TRIGGER trg_appointments_updated_at
    BEFORE UPDATE ON appointments
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();