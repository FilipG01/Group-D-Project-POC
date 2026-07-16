CREATE TABLE blog_posts (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                            title VARCHAR(220) NOT NULL,
                            slug VARCHAR(220) NOT NULL,
                            summary TEXT NOT NULL DEFAULT '',
                            body TEXT NOT NULL DEFAULT '',

                            author_user_id UUID NOT NULL,
                            featured_image_url TEXT,

                            status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
                            featured BOOLEAN NOT NULL DEFAULT FALSE,
                            display_order INTEGER NOT NULL DEFAULT 0,

                            seo_title VARCHAR(255),
                            seo_description TEXT,
                            keywords JSONB NOT NULL DEFAULT '[]'::jsonb,

                            review_notes TEXT,
                            reviewed_by_user_id UUID,
                            reviewed_at TIMESTAMPTZ,
                            submitted_at TIMESTAMPTZ,
                            published_at TIMESTAMPTZ,
                            archived_at TIMESTAMPTZ,

                            created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                            updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                            version BIGINT NOT NULL DEFAULT 0,

                            CONSTRAINT uq_blog_posts_slug UNIQUE (slug),

                            CONSTRAINT fk_blog_posts_author
                                FOREIGN KEY (author_user_id)
                                    REFERENCES users(id)
                                    ON DELETE RESTRICT,

                            CONSTRAINT fk_blog_posts_reviewer
                                FOREIGN KEY (reviewed_by_user_id)
                                    REFERENCES users(id)
                                    ON DELETE SET NULL,

                            CONSTRAINT chk_blog_posts_status CHECK (
                                status IN (
                                           'DRAFT',
                                           'SUBMITTED',
                                           'CHANGES_REQUESTED',
                                           'REJECTED',
                                           'PUBLISHED',
                                           'ARCHIVED'
                                    )
                                ),

                            CONSTRAINT chk_blog_posts_display_order
                                CHECK (display_order >= 0),

                            CONSTRAINT chk_blog_posts_keywords_array
                                CHECK (jsonb_typeof(keywords) = 'array')
);

CREATE TABLE blog_post_status_history (
                                          id BIGSERIAL PRIMARY KEY,

                                          blog_post_id UUID NOT NULL,

                                          from_status VARCHAR(30),
                                          to_status VARCHAR(30) NOT NULL,

                                          changed_by_user_id UUID NOT NULL,
                                          note TEXT,

                                          created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                                          CONSTRAINT fk_blog_status_history_post
                                              FOREIGN KEY (blog_post_id)
                                                  REFERENCES blog_posts(id)
                                                  ON DELETE CASCADE,

                                          CONSTRAINT fk_blog_status_history_user
                                              FOREIGN KEY (changed_by_user_id)
                                                  REFERENCES users(id)
                                                  ON DELETE RESTRICT,

                                          CONSTRAINT chk_blog_status_history_from_status CHECK (
                                              from_status IS NULL OR from_status IN (
                                                                                     'DRAFT',
                                                                                     'SUBMITTED',
                                                                                     'CHANGES_REQUESTED',
                                                                                     'REJECTED',
                                                                                     'PUBLISHED',
                                                                                     'ARCHIVED'
                                                  )
                                              ),

                                          CONSTRAINT chk_blog_status_history_to_status CHECK (
                                              to_status IN (
                                                            'DRAFT',
                                                            'SUBMITTED',
                                                            'CHANGES_REQUESTED',
                                                            'REJECTED',
                                                            'PUBLISHED',
                                                            'ARCHIVED'
                                                  )
                                              )
);

CREATE INDEX idx_blog_posts_public_listing
    ON blog_posts (
                   featured DESC,
                   display_order ASC,
                   published_at DESC
        )
    WHERE status = 'PUBLISHED';

CREATE INDEX idx_blog_posts_author_status
    ON blog_posts (
                   author_user_id,
                   status,
                   updated_at DESC
        );

CREATE INDEX idx_blog_posts_admin_status
    ON blog_posts (
                   status,
                   submitted_at DESC
        );

CREATE INDEX idx_blog_post_status_history_post
    ON blog_post_status_history (
                                 blog_post_id,
                                 created_at DESC
        );

CREATE TRIGGER trg_blog_posts_updated_at
    BEFORE UPDATE ON blog_posts
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();