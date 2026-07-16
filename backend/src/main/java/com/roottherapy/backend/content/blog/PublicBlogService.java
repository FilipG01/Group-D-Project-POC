package com.roottherapy.backend.content.blog;

import com.roottherapy.backend.content.blog.dto.PublicBlogPostDetailResponse;
import com.roottherapy.backend.content.blog.dto.PublicBlogPostSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class PublicBlogService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;

    private final BlogPostRepository blogPostRepository;

    public PublicBlogService(
            BlogPostRepository blogPostRepository
    ) {
        this.blogPostRepository = blogPostRepository;
    }

    public Page<PublicBlogPostSummaryResponse>
    getPublishedPosts(
            int page,
            int size
    ) {
        validatePage(page);

        int safeSize = validateAndLimitSize(size);

        Pageable pageable = PageRequest.of(
                page,
                safeSize
        );

        return blogPostRepository
                .findByStatusOrderByFeaturedDescDisplayOrderAscPublishedAtDesc(
                        BlogPostStatus.PUBLISHED,
                        pageable
                )
                .map(PublicBlogPostSummaryResponse::fromEntity);
    }

    public PublicBlogPostDetailResponse
    getPublishedPostBySlug(
            String slug
    ) {
        String normalizedSlug = normalizeSlug(slug);

        BlogPost post = blogPostRepository
                .findBySlugAndStatus(
                        normalizedSlug,
                        BlogPostStatus.PUBLISHED
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Blog post not found"
                ));

        return PublicBlogPostDetailResponse.fromEntity(post);
    }

    private String normalizeSlug(String slug) {
        if (slug == null || slug.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Blog post not found"
            );
        }

        return slug
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private void validatePage(int page) {
        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page must be zero or greater"
            );
        }
    }

    private int validateAndLimitSize(int size) {
        if (size < 1) {
            return DEFAULT_PAGE_SIZE;
        }

        return Math.min(size, MAX_PAGE_SIZE);
    }
}