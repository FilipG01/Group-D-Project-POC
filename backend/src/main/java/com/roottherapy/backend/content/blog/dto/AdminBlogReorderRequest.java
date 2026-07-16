package com.roottherapy.backend.content.blog.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;
import java.util.UUID;

public record AdminBlogReorderRequest(

        @NotEmpty(message = "At least one post is required")
        List<@Valid ReorderItem> posts
) {

    public record ReorderItem(

            @NotNull(message = "Post ID is required")
            UUID id,

            @NotNull(message = "Display order is required")
            @PositiveOrZero(
                    message = "Display order cannot be negative"
            )
            Integer displayOrder,

            @NotNull(message = "Version is required")
            @PositiveOrZero(message = "Version cannot be negative")
            Long version
    ) {
    }
}