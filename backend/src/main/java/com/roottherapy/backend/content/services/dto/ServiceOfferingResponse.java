package com.roottherapy.backend.content.services.dto;

import com.roottherapy.backend.content.services.ServiceOffering;

import java.time.Instant;
import java.util.List;

public record ServiceOfferingResponse(
        Long id,
        String title,
        String slug,
        String category,
        String shortDescription,
        List<String> fullDescription,
        List<String> points,
        String imageUrl,
        Integer displayOrder,
        Boolean published,
        Boolean archived,
        String metaTitle,
        String metaDescription,
        List<String> keywords,
        Instant createdAt,
        Instant updatedAt
) {
    public static ServiceOfferingResponse fromEntity(
            ServiceOffering service
    ) {
        return new ServiceOfferingResponse(
                service.getId(),
                service.getTitle(),
                service.getSlug(),
                service.getCategory(),
                service.getShortDescription(),
                service.getFullDescription(),
                service.getPoints(),
                service.getImageUrl(),
                service.getDisplayOrder(),
                service.getPublished(),
                service.getArchived(),
                service.getMetaTitle(),
                service.getMetaDescription(),
                service.getKeywords(),
                service.getCreatedAt(),
                service.getUpdatedAt()
        );
    }
}