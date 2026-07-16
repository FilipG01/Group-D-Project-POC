package com.roottherapy.backend.content.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateAdminBlogPostRequest(

        @NotBlank(message = "Title is required")
        @Size(
                max = 220,
                message = "Title cannot exceed 220 characters"
        )
        String title,

        @NotBlank(message = "Slug is required")
        @Size(
                max = 220,
                message = "Slug cannot exceed 220 characters"
        )
        String slug,

        @Size(
                max = 500,
                message = "Summary cannot exceed 500 characters"
        )
        String summary,

        String body,

        String featuredImageUrl,

        @Size(
                max = 255,
                message = "SEO title cannot exceed 255 characters"
        )
        String seoTitle,

        @Size(
                max = 320,
                message = "SEO description cannot exceed 320 characters"
        )
        String seoDescription,

        List<
                @NotBlank(message = "Keywords cannot be blank")
                @Size(
                        max = 80,
                        message = "A keyword cannot exceed 80 characters"
                )
                        String
                > keywords,

        @NotNull(message = "Version is required")
        @PositiveOrZero(message = "Version cannot be negative")
        Long version
) {
}