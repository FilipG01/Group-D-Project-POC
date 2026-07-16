package com.roottherapy.backend.content.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record AdminBlogReviewRequest(

        @NotBlank(message = "Review notes are required")
        @Size(
                max = 2000,
                message = "Review notes cannot exceed 2000 characters"
        )
        String note,

        @NotNull(message = "Version is required")
        @PositiveOrZero(message = "Version cannot be negative")
        Long version
) {
}