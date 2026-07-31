# Root Therapy POC

Root Therapy is a proof-of-concept therapy website with a React frontend and a Spring Boot backend.
The project includes user authentication, role-based dashboards, client/therapist profiles,
messaging, appointment requests, blog/content management, gallery management, notifications, and
basic performance testing.

## Project Structure

```text
Group-D-Project-POC/
  backend/      Spring Boot API, PostgreSQL, Flyway migrations
  frontend/     React + Vite website
```
## Prerequisites

Install the following before running the project:

- Java 25
- Node.js and npm
- PostgreSQL 18.4
- Git
- k6, optional, for performance testing

Check installed versions:

java --version
node --version
npm --version
psql --version
k6 version

## Database Setup

Create a PostgreSQL database for the project.

Example:

CREATE DATABASE root_therapy;

The backend uses Flyway, so database tables are created automatically from the migration files when
the backend starts.

### Database User Permissions

• Use this in pgAdmin Query Tool or psql as the postgres user or another database owner/superuser. A limited app user usually cannot grant
itself these permissions.

Replace:
- root_therapy_db with your database name
- root_therapy_user with the username from your .env

-- Root Therapy local development database permissions
-- Run this as postgres / database owner, not as the limited app user.

-- 1. Replace these names before running.
-- Database: root_therapy_db
-- User:     root_therapy_user

-- 2. Allow the app user to connect to the database.
```sql
GRANT CONNECT, TEMPORARY
ON DATABASE root_therapy_db
TO root_therapy_user;

ALTER DATABASE root_therapy_db
OWNER TO root_therapy_user;
OWNER TO root_therapy_user;

-- 6. Required by the migrations for gen_random_uuid().
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 7. Grant permissions on existing tables.
-- This includes any already-created Flyway, Spring Session, or app tables.
GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE, REFERENCES, TRIGGER
ON ALL TABLES IN SCHEMA public
TO root_therapy_user;

-- 8. Grant permissions on existing sequences.
GRANT USAGE, SELECT, UPDATE
ON ALL SEQUENCES IN SCHEMA public
TO root_therapy_user;

-- 9. Grant permissions on existing functions.
-- This helps with functions such as set_updated_at().
GRANT EXECUTE
ON ALL FUNCTIONS IN SCHEMA public
TO root_therapy_user;

-- 10. Grant permissions on future tables created in public.
ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE, REFERENCES, TRIGGER
ON TABLES
TO root_therapy_user;

-- 11. Grant permissions on future sequences created in public.
ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT USAGE, SELECT, UPDATE
ON SEQUENCES
TO root_therapy_user;

-- 12. Grant permissions on future functions created in public.
ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT EXECUTE
ON FUNCTIONS
TO root_therapy_user;
```

This script is intended for local development. It gives the application database user enough permissions for Flyway to create
extensions, tables, indexes, triggers, functions, Spring Session tables and future migrations. In production, permissions should be more
restrictive and managed by a database administrator.

### Optional: Create Local Admin And Therapist Accounts

Registration through the website currently creates client accounts only. For local development, you can create an admin and therapist
directly with SQL after the backend has run once and Flyway has created the tables.

Run this in pgAdmin Query Tool or `psql` against your project database.

Default login details created by this script:

- Admin: `admin@roottherapy.local` / `AdminPass123!`
- Therapist: `therapist@roottherapy.local` / `TherapistPass123!`

```sql
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Create or update local admin account.
WITH inserted_admin AS (
    INSERT INTO users (
        email,
        password_hash,
        first_name,
        last_name,
        phone_number,
        role,
        account_status
    )
    SELECT
        'admin@roottherapy.local',
        crypt('AdminPass123!', gen_salt('bf', 10)),
        'Local',
        'Admin',
        NULL,
        'ADMIN',
        'ACTIVE'
    WHERE NOT EXISTS (
        SELECT 1
        FROM users
        WHERE LOWER(email) = LOWER('admin@roottherapy.local')
    )
    RETURNING id
),
selected_admin AS (
    SELECT id FROM inserted_admin
    UNION
    SELECT id
    FROM users
    WHERE LOWER(email) = LOWER('admin@roottherapy.local')
),
updated_admin AS (
    UPDATE users
    SET
        role = 'ADMIN',
        account_status = 'ACTIVE',
        password_hash = crypt('AdminPass123!', gen_salt('bf', 10))
    WHERE id IN (SELECT id FROM selected_admin)
    RETURNING id
)
INSERT INTO admin_profiles (
    user_id,
    job_title,
    department
)
SELECT
    id,
    'System Administrator',
    'Operations'
FROM updated_admin
ON CONFLICT (user_id) DO UPDATE
SET
    job_title = EXCLUDED.job_title,
    department = EXCLUDED.department;

-- Create or update local therapist account.
WITH inserted_therapist AS (
    INSERT INTO users (
        email,
        password_hash,
        first_name,
        last_name,
        phone_number,
        role,
        account_status
    )
    SELECT
        'therapist@roottherapy.local',
        crypt('TherapistPass123!', gen_salt('bf', 10)),
        'Local',
        'Therapist',
        NULL,
        'THERAPIST',
        'ACTIVE'
    WHERE NOT EXISTS (
        SELECT 1
        FROM users
        WHERE LOWER(email) = LOWER('therapist@roottherapy.local')
    )
    RETURNING id
),
selected_therapist AS (
    SELECT id FROM inserted_therapist
    UNION
    SELECT id
    FROM users
    WHERE LOWER(email) = LOWER('therapist@roottherapy.local')
),
updated_therapist AS (
    UPDATE users
    SET
        role = 'THERAPIST',
        account_status = 'ACTIVE',
        password_hash = crypt('TherapistPass123!', gen_salt('bf', 10))
    WHERE id IN (SELECT id FROM selected_therapist)
    RETURNING id
)
INSERT INTO therapist_profiles (
    user_id,
    qualifications,
    registration_number,
    years_experience,
    bio,
    is_accepting_clients,
    public_bio,
    languages,
    specialisms,
    display_order,
    is_publicly_visible
)
SELECT
    id,
    'MSc Counselling and Psychotherapy',
    'LOCAL-THERAPIST-001',
    5,
    'Local therapist account for development and testing.',
    TRUE,
    '["Local development therapist profile."]'::jsonb,
    '["English"]'::jsonb,
    '["Anxiety", "Stress", "General wellbeing"]'::jsonb,
    0,
    TRUE
FROM updated_therapist
ON CONFLICT (user_id) DO UPDATE
SET
    qualifications = EXCLUDED.qualifications,
    registration_number = EXCLUDED.registration_number,
    years_experience = EXCLUDED.years_experience,
    bio = EXCLUDED.bio,
    is_accepting_clients = EXCLUDED.is_accepting_clients,
    public_bio = EXCLUDED.public_bio,
    languages = EXCLUDED.languages,
    specialisms = EXCLUDED.specialisms,
    display_order = EXCLUDED.display_order,
    is_publicly_visible = EXCLUDED.is_publicly_visible;
```

This script is intended for local development only. It uses PostgreSQL `pgcrypto` to create BCrypt-compatible password hashes instead of
storing plaintext passwords.

Migration files are stored in:

backend/src/main/resources/db/migration

Do not edit old migrations after they have already been applied to a shared database. Create a new
numbered migration instead.

## Backend Environment Variables

Create a .env file inside the backend folder:

backend/.env

Example contents:

DB_URL=jdbc:postgresql://localhost:5432/root_therapy
DB_USERNAME=your_postgres_username
DB_PASSWORD=your_postgres_password
UPLOAD_DIRECTORY=backend/uploads

Replace the username, password, and database name with your local PostgreSQL details.

## Running The Backend

From the project root:

cd backend
./mvnw.cmd spring-boot:run

The backend should start on:

http://localhost:8080

Flyway migrations and Spring Session JDBC tables are created automatically on startup.

## Running The Frontend

Open a second terminal.

From the project root:

cd frontend
npm install
npm run dev

The frontend should start on:

http://localhost:5173

The frontend API client expects the backend to be running on:

http://localhost:8080

## Building The Project

Backend compile:

cd backend
./mvnw.cmd -DskipTests compile

Backend tests:

cd backend
./mvnw.cmd test

Frontend build:

cd frontend
npm run build

Frontend preview after build:

cd frontend
npm run preview