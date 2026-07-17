package com.roottherapy.backend.content.blog;

import com.roottherapy.backend.content.blog.dto.CreateTherapistBlogPostRequest;
import com.roottherapy.backend.content.blog.dto.TherapistBlogPostResponse;
import com.roottherapy.backend.content.blog.dto.UpdateTherapistBlogPostRequest;
import com.roottherapy.backend.content.blog.dto.SubmitTherapistBlogPostRequest;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRepository;
import com.roottherapy.backend.users.UserRole;
import com.roottherapy.backend.users.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

@Service
public class TherapistBlogService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;

    private static final Set<BlogPostStatus>
            THERAPIST_EDITABLE_STATUSES = EnumSet.of(
            BlogPostStatus.DRAFT,
            BlogPostStatus.CHANGES_REQUESTED,
            BlogPostStatus.REJECTED
    );

    private static final Set<BlogPostStatus>
            THERAPIST_SUBMITTABLE_STATUSES = EnumSet.of(
            BlogPostStatus.DRAFT,
            BlogPostStatus.CHANGES_REQUESTED,
            BlogPostStatus.REJECTED
    );

    private final BlogPostRepository blogPostRepository;
    private final UserRepository userRepository;
    private final BlogSlugService blogSlugService;

    public TherapistBlogService(
            BlogPostRepository blogPostRepository,
            UserRepository userRepository,
            BlogSlugService blogSlugService
    ) {
        this.blogPostRepository = blogPostRepository;
        this.userRepository = userRepository;
        this.blogSlugService = blogSlugService;
    }

    @Transactional
    public TherapistBlogPostResponse createDraft(
            UUID authenticatedUserId,
            CreateTherapistBlogPostRequest request
    ) {
        User therapist = getActiveTherapist(
                authenticatedUserId
        );

        String title = request.title().trim();

        BlogPost post = new BlogPost(
                title,
                blogSlugService.generateUniqueSlug(title),
                therapist
        );

        post.setSummary(cleanNullableText(request.summary()));
        post.setBody(cleanNullableText(request.body()));

        post.setFeaturedImageUrl(
                validateFeaturedImageUrl(
                        request.featuredImageUrl()
                )
        );

        post.setSeoTitle(
                cleanNullableValue(request.seoTitle())
        );

        post.setSeoDescription(
                cleanNullableValue(request.seoDescription())
        );

        post.setKeywords(
                normalizeKeywords(request.keywords())
        );

        /*
         * These values are controlled only by the backend.
         */
        post.setStatus(BlogPostStatus.DRAFT);
        post.setFeatured(false);
        post.setDisplayOrder(0);

        BlogPost savedPost =
                blogPostRepository.save(post);

        return TherapistBlogPostResponse.fromEntity(
                savedPost
        );
    }

    @Transactional
    public TherapistBlogPostResponse updateOwnPost(
            UUID authenticatedUserId,
            UUID postId,
            UpdateTherapistBlogPostRequest request
    ) {
        getActiveTherapist(authenticatedUserId);

        BlogPost post = blogPostRepository
                .findByIdAndAuthorId(
                        postId,
                        authenticatedUserId
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Blog post not found"
                        )
                );

        validateEditableStatus(post);

        validateVersion(
                post,
                request.version()
        );

        post.setTitle(request.title().trim());

        post.setSummary(
                cleanNullableText(request.summary())
        );

        post.setBody(
                cleanNullableText(request.body())
        );

        post.setFeaturedImageUrl(
                validateFeaturedImageUrl(
                        request.featuredImageUrl()
                )
        );

        post.setSeoTitle(
                cleanNullableValue(request.seoTitle())
        );

        post.setSeoDescription(
                cleanNullableValue(
                        request.seoDescription()
                )
        );

        post.setKeywords(
                normalizeKeywords(request.keywords())
        );

        /*
         * The slug is deliberately not changed here.
         * Changing a title does not silently change the URL.
         *
         * Therapists also cannot update:
         * - author
         * - status
         * - featured
         * - displayOrder
         * - review information
         * - publication timestamps
         */
        BlogPost savedPost =
                blogPostRepository.saveAndFlush(post);

        return TherapistBlogPostResponse.fromEntity(
                savedPost
        );
    }

    @Transactional
    public TherapistBlogPostResponse submitOwnPost(
            UUID authenticatedUserId,
            UUID postId,
            SubmitTherapistBlogPostRequest request
    ) {
        User therapist = getActiveTherapist(
                authenticatedUserId
        );

        BlogPost post = blogPostRepository
                .findByIdAndAuthorId(
                        postId,
                        authenticatedUserId
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Blog post not found"
                        )
                );

        validateSubmittableStatus(post);

        validateVersion(
                post,
                request.version()
        );

        validateReadyForSubmission(post);

        BlogPostStatus previousStatus =
                post.getStatus();

        post.setStatus(BlogPostStatus.SUBMITTED);
        post.setSubmittedAt(Instant.now());

        /*
         * Clear feedback from an earlier review cycle.
         * Previous feedback remains available in history.
         */
        post.setReviewNotes(null);
        post.setReviewedBy(null);
        post.setReviewedAt(null);

        BlogPost savedPost =
                blogPostRepository.saveAndFlush(post);

        BlogPostStatusHistory history =
                new BlogPostStatusHistory(
                        savedPost,
                        previousStatus,
                        BlogPostStatus.SUBMITTED,
                        therapist,
                        null
                );

        return TherapistBlogPostResponse.fromEntity(
                savedPost
        );
    }

    @Transactional(readOnly = true)
    public Page<TherapistBlogPostResponse> getOwnPosts(
            UUID authenticatedUserId,
            int page,
            int size
    ) {
        getActiveTherapist(authenticatedUserId);

        Pageable pageable = PageRequest.of(
                validatePage(page),
                validateSize(size)
        );

        return blogPostRepository
                .findByAuthorIdOrderByUpdatedAtDesc(
                        authenticatedUserId,
                        pageable
                )
                .map(TherapistBlogPostResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public TherapistBlogPostResponse getOwnPost(
            UUID authenticatedUserId,
            UUID postId
    ) {
        getActiveTherapist(authenticatedUserId);

        BlogPost post = blogPostRepository
                .findByIdAndAuthorId(
                        postId,
                        authenticatedUserId
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Blog post not found"
                        )
                );

        return TherapistBlogPostResponse.fromEntity(post);
    }

    private void validateEditableStatus(BlogPost post) {
        if (!THERAPIST_EDITABLE_STATUSES.contains(
                post.getStatus()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This blog post cannot be edited while its status is "
                            + post.getStatus()
            );
        }
    }

    private void validateSubmittableStatus(
            BlogPost post
    ) {
        if (!THERAPIST_SUBMITTABLE_STATUSES.contains(
                post.getStatus()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This blog post cannot be submitted while its status is "
                            + post.getStatus()
            );
        }
    }

    private void validateReadyForSubmission(
            BlogPost post
    ) {
        if (
                post.getTitle() == null
                        || post.getTitle().isBlank()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A title is required before submission"
            );
        }

        if (
                post.getSummary() == null
                        || post.getSummary().isBlank()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A summary is required before submission"
            );
        }

        if (
                post.getBody() == null
                        || post.getBody().isBlank()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Post body is required before submission"
            );
        }

        if (
                post.getSlug() == null
                        || post.getSlug().isBlank()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A slug is required before submission"
            );
        }

        if (post.getSummary().length() > 500) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Summary cannot exceed 500 characters"
            );
        }

        if (
                post.getSeoTitle() != null
                        && post.getSeoTitle().length() > 255
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "SEO title cannot exceed 255 characters"
            );
        }

        if (
                post.getSeoDescription() != null
                        && post.getSeoDescription().length() > 320
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "SEO description cannot exceed 320 characters"
            );
        }

        validateStoredFeaturedImage(post);
    }

    private void validateStoredFeaturedImage(
            BlogPost post
    ) {
        String imageUrl = post.getFeaturedImageUrl();

        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        if (
                !imageUrl.startsWith("/uploads/blog/")
                        || imageUrl.contains("..")
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The featured image path is invalid"
            );
        }
    }

    private void validateVersion(
            BlogPost post,
            Long requestVersion
    ) {
        if (!Objects.equals(
                post.getVersion(),
                requestVersion
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This blog post has been updated elsewhere. "
                            + "Refresh the post and try again."
            );
        }
    }

    private User getActiveTherapist(
            UUID authenticatedUserId
    ) {
        User user = userRepository
                .findById(authenticatedUserId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Authenticated user not found"
                        )
                );

        if (user.getRole() != UserRole.THERAPIST) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Therapist access is required"
            );
        }

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "The therapist account is not active"
            );
        }

        return user;
    }

    private String validateFeaturedImageUrl(
            String featuredImageUrl
    ) {
        String value = cleanNullableValue(
                featuredImageUrl
        );

        if (value == null) {
            return null;
        }

        if (!value.startsWith("/uploads/blog/")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Featured image must be a blog upload"
            );
        }

        if (value.contains("..")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid featured image path"
            );
        }

        return value;
    }

    private List<String> normalizeKeywords(
            List<String> keywords
    ) {
        if (keywords == null || keywords.isEmpty()) {
            return List.of();
        }

        /*
         * The map removes duplicates while preserving
         * the original insertion order.
         */
        Map<String, String> uniqueKeywords =
                new LinkedHashMap<>();

        for (String keyword : keywords) {
            if (keyword == null) {
                continue;
            }

            String cleaned = keyword.trim();

            if (cleaned.isBlank()) {
                continue;
            }

            String duplicateKey =
                    cleaned.toLowerCase(Locale.ROOT);

            uniqueKeywords.putIfAbsent(
                    duplicateKey,
                    cleaned
            );
        }

        return List.copyOf(uniqueKeywords.values());
    }

    private String cleanNullableText(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }

    private String cleanNullableValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private int validatePage(int page) {
        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page must be zero or greater"
            );
        }

        return page;
    }

    private int validateSize(int size) {
        if (size < 1) {
            return DEFAULT_PAGE_SIZE;
        }

        return Math.min(size, MAX_PAGE_SIZE);
    }
}