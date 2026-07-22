package com.roottherapy.backend.content.gallery.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateGalleryImageRequest(

        @NotBlank(message = "Image URL is required")
        String imageUrl,

        String caption,

        @NotBlank(message = "Alt text is required")
        @Size(
                max = 255,
                message = "Alt text must not exceed 255 characters"
        )
        String altText,

        @NotNull(message = "Display order is required")
        @Min(
                value = 0,
                message = "Display order cannot be negative"
        )
        Integer displayOrder,

        Boolean visible
) {
}