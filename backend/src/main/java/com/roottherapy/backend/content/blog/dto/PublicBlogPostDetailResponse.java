package com.roottherapy.backend.content.blog.dto;

import com.roottherapy.backend.content.blog.BlogPost;
import com.roottherapy.backend.users.User;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PublicBlogPostDetailResponse(
        UUID id,
        String title,
        String slug,
        String summary,
        String body,
        String featuredImageUrl,
        PublicBlogAuthorResponse author,
        String seoTitle,
        String seoDescription,
        List<String> keywords,
        Instant publishedAt,
        Instant updatedAt
) {

    public static PublicBlogPostDetailResponse fromEntity(
            BlogPost post
    ) {
        return new PublicBlogPostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getSlug(),
                post.getSummary(),
                post.getBody(),
                post.getFeaturedImageUrl(),
                PublicBlogAuthorResponse.fromEntity(
                        post.getAuthor()
                ),
                post.getSeoTitle(),
                post.getSeoDescription(),
                post.getKeywords() == null
                        ? List.of()
                        : List.copyOf(post.getKeywords()),
                post.getPublishedAt(),
                post.getUpdatedAt()
        );
    }

    public record PublicBlogAuthorResponse(
            UUID id,
            String name
    ) {

        public static PublicBlogAuthorResponse fromEntity(
                User author
        ) {
            String name = (
                    author.getFirstName()
                            + " "
                            + author.getLastName()
            ).trim();

            return new PublicBlogAuthorResponse(
                    author.getId(),
                    name
            );
        }
    }
}