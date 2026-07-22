package com.roottherapy.backend.content.gallery.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ReorderGalleryImagesRequest(

        @NotEmpty(message = "Gallery images are required")
        List<@Valid GalleryImageOrderItem> images
) {
}