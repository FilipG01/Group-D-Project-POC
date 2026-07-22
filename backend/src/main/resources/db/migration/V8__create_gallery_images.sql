-- Stores the gallery images displayed on the Home and About pages.

CREATE TABLE gallery_images (
                                id BIGSERIAL PRIMARY KEY,

    -- Public URL returned by the existing image upload system.
                                image_url TEXT NOT NULL,

    -- Optional text shown alongside or beneath the image.
                                caption TEXT,

    -- Required accessibility description for the image.
                                alt_text VARCHAR(255) NOT NULL,

    -- Controls the order in which images appear publicly.
                                display_order INTEGER NOT NULL DEFAULT 0,

    -- Hidden images remain available to admins but do not appear publicly.
                                visible BOOLEAN NOT NULL DEFAULT FALSE,

    -- Archived images are removed from normal use without deleting the record.
                                archived BOOLEAN NOT NULL DEFAULT FALSE,

                                created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                                CONSTRAINT chk_gallery_image_url_not_blank
                                    CHECK (LENGTH(TRIM(image_url)) > 0),

                                CONSTRAINT chk_gallery_alt_text_not_blank
                                    CHECK (LENGTH(TRIM(alt_text)) > 0),

                                CONSTRAINT chk_gallery_display_order
                                    CHECK (display_order >= 0)
);

CREATE INDEX idx_gallery_images_visible
    ON gallery_images (visible);

CREATE INDEX idx_gallery_images_archived
    ON gallery_images (archived);

CREATE INDEX idx_gallery_images_display_order
    ON gallery_images (display_order);

CREATE INDEX idx_gallery_images_public_listing
    ON gallery_images (visible, archived, display_order);

-- Reuse of updated_at trigger function created by the earlier migrations.
CREATE TRIGGER trg_gallery_images_updated_at
    BEFORE UPDATE ON gallery_images
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();