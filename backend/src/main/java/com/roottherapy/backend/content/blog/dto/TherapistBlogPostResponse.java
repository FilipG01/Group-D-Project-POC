package com.roottherapy.backend.content.blog.dto;

import com.roottherapy.backend.content.blog.BlogPost;
import com.roottherapy.backend.content.blog.BlogPostStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TherapistBlogPostResponse(
        UUID id,
        String title,
        String slug,
        String summary,
        String body,
        String featuredImageUrl,
        BlogPostStatus status,
        String seoTitle,
        String seoDescription,
        List<String> keywords,
        String reviewNotes,
        Instant submittedAt,
        Instant publishedAt,
        Instant createdAt,
        Instant updatedAt,
        Long version
) {

    public static TherapistBlogPostResponse fromEntity(
            BlogPost post
    ) {
        return new TherapistBlogPostResponse(
                post.getId(),
                post.getTitle(),
                post.getSlug(),
                post.getSummary(),
                post.getBody(),
                post.getFeaturedImageUrl(),
                post.getStatus(),
                post.getSeoTitle(),
                post.getSeoDescription(),
                post.getKeywords() == null
                        ? List.of()
                        : List.copyOf(post.getKeywords()),
                post.getReviewNotes(),
                post.getSubmittedAt(),
                post.getPublishedAt(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getVersion()
        );
    }
}