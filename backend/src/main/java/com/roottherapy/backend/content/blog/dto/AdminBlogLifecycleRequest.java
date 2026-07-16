package com.roottherapy.backend.content.blog.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record AdminBlogLifecycleRequest(

        @NotNull(message = "Version is required")
        @PositiveOrZero(message = "Version cannot be negative")
        Long version,

        @Size(
                max = 2000,
                message = "Note cannot exceed 2000 characters"
        )
        String note
) {
}