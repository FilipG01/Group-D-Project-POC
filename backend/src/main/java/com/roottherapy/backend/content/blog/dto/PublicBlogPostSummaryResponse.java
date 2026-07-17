package com.roottherapy.backend.content.blog.dto;

import com.roottherapy.backend.content.blog.BlogPost;
import com.roottherapy.backend.users.User;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PublicBlogPostSummaryResponse(
        UUID id,
        String title,
        String slug,
        String summary,
        String featuredImageUrl,
        String authorName,
        Boolean featured,
        List<String> keywords,
        Instant publishedAt
) {

    public static PublicBlogPostSummaryResponse fromEntity(
            BlogPost post
    ) {
        return new PublicBlogPostSummaryResponse(
                post.getId(),
                post.getTitle(),
                post.getSlug(),
                post.getSummary(),
                post.getFeaturedImageUrl(),
                buildAuthorName(post.getAuthor()),
                post.getFeatured(),
                post.getKeywords() == null
                        ? List.of()
                        : List.copyOf(post.getKeywords()),
                post.getPublishedAt()
        );
    }

    private static String buildAuthorName(User author) {
        return (
                author.getFirstName() + " " + author.getLastName()
        ).trim();
    }
}