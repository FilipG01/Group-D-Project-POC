package com.roottherapy.backend.content.services.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateServiceOfferingRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must not exceed 200 characters")
        String title,

        @NotBlank(message = "Slug is required")
        @Size(max = 200, message = "Slug must not exceed 200 characters")
        String slug,

        @Size(max = 150, message = "Category must not exceed 150 characters")
        String category,

        @NotBlank(message = "Short description is required")
        String shortDescription,

        @NotNull(message = "Full description is required")
        List<String> fullDescription,

        @NotNull(message = "Service points are required")
        List<String> points,

        String imageUrl,

        @NotNull(message = "Display order is required")
        @Min(value = 0, message = "Display order cannot be negative")
        Integer displayOrder,

        Boolean published,

        @Size(max = 255, message = "Meta title must not exceed 255 characters")
        String metaTitle,

        String metaDescription,

        List<String> keywords
) {
}