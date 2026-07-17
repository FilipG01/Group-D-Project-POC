package com.roottherapy.backend.content.blog;

import com.roottherapy.backend.users.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "blog_posts")
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 220)
    private String title;

    @Column(nullable = false, unique = true, length = 220)
    private String slug;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary = "";

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body = "";

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_user_id", nullable = false)
    private User author;

    @Column(name = "featured_image_url", columnDefinition = "TEXT")
    private String featuredImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BlogPostStatus status = BlogPostStatus.DRAFT;

    @Column(nullable = false)
    private Boolean featured = false;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "seo_title", length = 255)
    private String seoTitle;

    @Column(name = "seo_description", columnDefinition = "TEXT")
    private String seoDescription;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private List<String> keywords = new ArrayList<>();

    @Column(name = "review_notes", columnDefinition = "TEXT")
    private String reviewNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_user_id")
    private User reviewedBy;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "archived_at")
    private Instant archivedAt;

    @Column(
            name = "created_at",
            insertable = false,
            updatable = false
    )
    private Instant createdAt;

    @Column(
            name = "updated_at",
            insertable = false,
            updatable = false
    )
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private Long version;

    public BlogPost(
            String title,
            String slug,
            User author
    ) {
        this.title = title;
        this.slug = slug;
        this.author = author;

        this.summary = "";
        this.body = "";
        this.status = BlogPostStatus.DRAFT;
        this.featured = false;
        this.displayOrder = 0;
        this.keywords = new ArrayList<>();
    }
}