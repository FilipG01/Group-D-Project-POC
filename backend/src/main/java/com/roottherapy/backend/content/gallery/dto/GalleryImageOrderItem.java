package com.roottherapy.backend.content.gallery.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GalleryImageOrderItem(

        @NotNull(message = "Gallery image ID is required")
        Long id,

        @NotNull(message = "Display order is required")
        @Min(
                value = 0,
                message = "Display order cannot be negative"
        )
        Integer displayOrder
) {
}