package com.roottherapy.backend.content.blog.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record AdminBlogFeatureRequest(

        @NotNull(message = "Featured value is required")
        Boolean featured,

        @NotNull(message = "Version is required")
        @PositiveOrZero(message = "Version cannot be negative")
        Long version
) {
}