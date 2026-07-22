package com.roottherapy.backend.content.gallery.dto;

import jakarta.validation.constraints.NotNull;

public record GalleryArchiveRequest(

        @NotNull(message = "Archived status is required")
        Boolean archived
) {
}