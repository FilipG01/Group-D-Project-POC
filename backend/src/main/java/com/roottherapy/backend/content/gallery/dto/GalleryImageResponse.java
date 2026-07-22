package com.roottherapy.backend.content.gallery.dto;

import com.roottherapy.backend.content.gallery.GalleryImage;

import java.time.Instant;

public record GalleryImageResponse(
        Long id,
        String imageUrl,
        String caption,
        String altText,
        Integer displayOrder,
        Boolean visible,
        Boolean archived,
        Instant createdAt,
        Instant updatedAt
) {

    public static GalleryImageResponse fromEntity(
            GalleryImage image
    ) {
        return new GalleryImageResponse(
                image.getId(),
                image.getImageUrl(),
                image.getCaption(),
                image.getAltText(),
                image.getDisplayOrder(),
                image.getVisible(),
                image.getArchived(),
                image.getCreatedAt(),
                image.getUpdatedAt()
        );
    }
}