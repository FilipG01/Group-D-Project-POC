package com.roottherapy.backend.content.blog;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

@Service
public class BlogSlugService {

    private static final int MAX_SLUG_LENGTH = 220;

    private final BlogPostRepository blogPostRepository;

    public BlogSlugService(
            BlogPostRepository blogPostRepository
    ) {
        this.blogPostRepository = blogPostRepository;
    }

    public String generateUniqueSlug(String title) {
        String baseSlug = normalizeSlug(title);

        if (!blogPostRepository.existsBySlug(baseSlug)) {
            return baseSlug;
        }

        int suffix = 2;

        while (true) {
            String suffixText = "-" + suffix;

            int allowedBaseLength =
                    MAX_SLUG_LENGTH - suffixText.length();

            String shortenedBase =
                    baseSlug.length() > allowedBaseLength
                            ? baseSlug.substring(
                            0,
                            allowedBaseLength
                    )
                            : baseSlug;

            shortenedBase =
                    shortenedBase.replaceAll("-+$", "");

            String candidate =
                    shortenedBase + suffixText;

            if (!blogPostRepository.existsBySlug(candidate)) {
                return candidate;
            }

            suffix++;
        }
    }

    public String normalizeAndValidateNewSlug(
            String requestedSlug
    ) {
        String normalized =
                normalizeSlug(requestedSlug);

        if (blogPostRepository.existsBySlug(normalized)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A blog post with this slug already exists"
            );
        }

        return normalized;
    }

    public String normalizeAndValidateUpdatedSlug(
            String requestedSlug,
            UUID postId
    ) {
        String normalized =
                normalizeSlug(requestedSlug);

        if (
                blogPostRepository.existsBySlugAndIdNot(
                        normalized,
                        postId
                )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A blog post with this slug already exists"
            );
        }

        return normalized;
    }

    public String normalizeSlug(String value) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Slug value is required"
            );
        }

        String withoutAccents = Normalizer
                .normalize(
                        value,
                        Normalizer.Form.NFD
                )
                .replaceAll("\\p{M}", "");

        String normalized = withoutAccents
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");

        if (normalized.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Slug must contain letters or numbers"
            );
        }

        if (normalized.length() > MAX_SLUG_LENGTH) {
            normalized = normalized
                    .substring(0, MAX_SLUG_LENGTH)
                    .replaceAll("-+$", "");
        }

        return normalized;
    }
}