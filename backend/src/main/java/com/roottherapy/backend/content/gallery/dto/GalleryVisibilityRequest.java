package com.roottherapy.backend.content.gallery.dto;

import jakarta.validation.constraints.NotNull;

public record GalleryVisibilityRequest(

        @NotNull(message = "Visible status is required")
        Boolean visible
) {
}