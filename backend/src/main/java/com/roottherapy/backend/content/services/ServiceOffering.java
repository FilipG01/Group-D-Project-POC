package com.roottherapy.backend.content.services;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "services")
public class ServiceOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, unique = true, length = 200)
    private String slug;

    @Column(length = 150)
    private String category;

    @Column(name = "short_description", nullable = false, columnDefinition = "TEXT")
    private String shortDescription;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "full_description", nullable = false, columnDefinition = "jsonb")
    private List<String> fullDescription = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private List<String> points = new ArrayList<>();

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(nullable = false)
    private Boolean published = false;

    @Column(nullable = false)
    private Boolean archived = false;

    @Column(name = "meta_title", length = 255)
    private String metaTitle;

    @Column(name = "meta_description", columnDefinition = "TEXT")
    private String metaDescription;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private List<String> keywords = new ArrayList<>();

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;

    public ServiceOffering(
            String title,
            String slug,
            String category,
            String shortDescription
    ) {
        this.title = title;
        this.slug = slug;
        this.category = category;
        this.shortDescription = shortDescription;
        this.fullDescription = new ArrayList<>();
        this.points = new ArrayList<>();
        this.keywords = new ArrayList<>();
        this.displayOrder = 0;
        this.published = false;
        this.archived = false;
    }
}
