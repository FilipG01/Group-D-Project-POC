-- V1__create_users_and_profiles.sql

-- created tables:
-- users
-- clients_profiles
-- therapist_profiles
-- admin_profiles

-- created triggers
-- updated_at trigger function
-- updated at triggers

-- enables get_random_uuid() for UUID primary keys
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(30),
    
    role VARCHAR(30) NOT NULL,
    account_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',

    last_login_at TIMESTAMPTZ,
    anonymised_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_users_role CHECK (role IN ('CLIENT', 'THERAPIST', 'ADMIN')),
    CONSTRAINT chk_users_account_status CHECK (account_status IN ('ACTIVE', 'SUSPENDED', 'DEACTIVATED', 'ANONYMISED'))
);

-- indexs
CREATE UNIQUE INDEX uq_users_email_lower ON users (LOWER(email));
CREATE INDEX idx_users_role ON users (role);
CREATE INDEX idx_users_account_status ON users (account_status);

-- client profiles table
CREATE TABLE client_profiles (
    user_id UUID PRIMARY KEY,
    date_of_birth DATE,
    therapy_goals_summary TEXT,
    preferred_contact_method VARCHAR(50),

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_client_profiles_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,

    CONSTRAINT chk_client_preferred_contact_method CHECK (
        preferred_contact_method IS NULL
        OR preferred_contact_method IN ('EMAIL','PHONE','IN_APP')
    )
);

-- therapist profiles table
CREATE TABLE therapist_profiles (
    user_id UUID PRIMARY KEY,
    qualifications TEXT NOT NULL,
    registration_number VARCHAR(100) NOT NULL,
    years_experience INTEGER NOT NULL DEFAULT 0,
    bio TEXT,
    is_accepting_clients BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_therapist_profiles_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_therapist_registration_number UNIQUE (registration_number),
    CONSTRAINT chk_therapist_years_experience CHECK (years_experience >= 0)
);

-- admin profiles table
CREATE TABLE admin_profiles (
    user_id UUID PRIMARY KEY,
    job_title VARCHAR(150),
    department VARCHAR(150),

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_admin_profiles_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

-- updated_at trigger function
CREATE OR REPLACE FUNCTION set_updated_at() RETURNS TRIGGER AS $$ 
    BEGIN
        NEW.updated_at = NOW();
        RETURN NEW;
    END;
    $$ LANGUAGE plpgsql;

-- update_at triggers
CREATE TRIGGER trg_users_updated_at
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_client_profiles_updated_at
BEFORE UPDATE ON client_profiles
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_therapist_profiles_updated_at
BEFORE UPDATE ON therapist_profiles
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_admin_profiles_updated_at
BEFORE UPDATE ON admin_profiles
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();