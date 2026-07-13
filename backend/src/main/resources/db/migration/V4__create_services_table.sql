-- V4__create_services_table.sql

CREATE TABLE services (
                          id BIGSERIAL PRIMARY KEY,

                          title VARCHAR(200) NOT NULL,
                          slug VARCHAR(200) NOT NULL,
                          category VARCHAR(150),

                          short_description TEXT NOT NULL,

    -- Stored as JSON arrays:
    -- ["Paragraph one", "Paragraph two"]
                          full_description JSONB NOT NULL DEFAULT '[]'::jsonb,

    -- Stored as a JSON array:
    -- ["Managing anxiety", "Reducing overwhelm"]
                          points JSONB NOT NULL DEFAULT '[]'::jsonb,

                          image_url TEXT,

                          display_order INTEGER NOT NULL DEFAULT 0,

                          published BOOLEAN NOT NULL DEFAULT FALSE,
                          archived BOOLEAN NOT NULL DEFAULT FALSE,

                          meta_title VARCHAR(255),
                          meta_description TEXT,

    -- Stored as a JSON array:
    -- ["therapy Dublin", "anxiety counselling"]
                          keywords JSONB NOT NULL DEFAULT '[]'::jsonb,

                          created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                          updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                          CONSTRAINT uq_services_slug UNIQUE (slug),

                          CONSTRAINT chk_services_title_not_blank
                              CHECK (LENGTH(TRIM(title)) > 0),

                          CONSTRAINT chk_services_slug_not_blank
                              CHECK (LENGTH(TRIM(slug)) > 0),

                          CONSTRAINT chk_services_short_description_not_blank
                              CHECK (LENGTH(TRIM(short_description)) > 0),

                          CONSTRAINT chk_services_display_order
                              CHECK (display_order >= 0),

                          CONSTRAINT chk_services_full_description_array
                              CHECK (jsonb_typeof(full_description) = 'array'),

                          CONSTRAINT chk_services_points_array
                              CHECK (jsonb_typeof(points) = 'array'),

                          CONSTRAINT chk_services_keywords_array
                              CHECK (jsonb_typeof(keywords) = 'array')
);

CREATE INDEX idx_services_published
    ON services (published);

CREATE INDEX idx_services_archived
    ON services (archived);

CREATE INDEX idx_services_display_order
    ON services (display_order);

CREATE INDEX idx_services_public_listing
    ON services (published, archived, display_order);

CREATE TRIGGER trg_services_updated_at
    BEFORE UPDATE ON services
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();