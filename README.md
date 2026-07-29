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