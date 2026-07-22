package com.roottherapy.backend.content.gallery;

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

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "gallery_images")
public class GalleryImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "image_url",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String caption;

    @Column(
            name = "alt_text",
            nullable = false,
            length = 255
    )
    private String altText;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(nullable = false)
    private Boolean visible = false;

    @Column(nullable = false)
    private Boolean archived = false;

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

    public GalleryImage(
            String imageUrl,
            String caption,
            String altText
    ) {
        this.imageUrl = imageUrl;
        this.caption = caption;
        this.altText = altText;
        this.displayOrder = 0;
        this.visible = false;
        this.archived = false;
    }
}