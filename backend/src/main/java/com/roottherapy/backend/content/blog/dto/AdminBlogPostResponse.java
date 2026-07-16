package com.roottherapy.backend.content.blog.dto;

import com.roottherapy.backend.content.blog.BlogPost;
import com.roottherapy.backend.content.blog.BlogPostStatus;
import com.roottherapy.backend.users.User;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AdminBlogPostResponse(
        UUID id,
        String title,
        String slug,
        String summary,
        String body,
        AuthorResponse author,
        String featuredImageUrl,
        BlogPostStatus status,
        Boolean featured,
        Integer displayOrder,
        String seoTitle,
        String seoDescription,
        List<String> keywords,
        String reviewNotes,
        ReviewerResponse reviewedBy,
        Instant reviewedAt,
        Instant submittedAt,
        Instant publishedAt,
        Instant archivedAt,
        Instant createdAt,
        Instant updatedAt,
        Long version
) {

    public static AdminBlogPostResponse fromEntity(
            BlogPost post
    ) {
        return new AdminBlogPostResponse(
                post.getId(),
                post.getTitle(),
                post.getSlug(),
                post.getSummary(),
                post.getBody(),
                AuthorResponse.fromEntity(post.getAuthor()),
                post.getFeaturedImageUrl(),
                post.getStatus(),
                post.getFeatured(),
                post.getDisplayOrder(),
                post.getSeoTitle(),
                post.getSeoDescription(),
                post.getKeywords() == null
                        ? List.of()
                        : List.copyOf(post.getKeywords()),
                post.getReviewNotes(),
                post.getReviewedBy() == null
                        ? null
                        : ReviewerResponse.fromEntity(
                        post.getReviewedBy()
                ),
                post.getReviewedAt(),
                post.getSubmittedAt(),
                post.getPublishedAt(),
                post.getArchivedAt(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getVersion()
        );
    }

    public record AuthorResponse(
            UUID id,
            String name,
            String role
    ) {

        public static AuthorResponse fromEntity(User user) {
            return new AuthorResponse(
                    user.getId(),
                    buildName(user),
                    user.getRole().name()
            );
        }
    }

    public record ReviewerResponse(
            UUID id,
            String name
    ) {

        public static ReviewerResponse fromEntity(User user) {
            return new ReviewerResponse(
                    user.getId(),
                    buildName(user)
            );
        }
    }

    private static String buildName(User user) {
        return (
                user.getFirstName()
                        + " "
                        + user.getLastName()
        ).trim();
    }
}