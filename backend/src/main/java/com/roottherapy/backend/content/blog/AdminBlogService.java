package com.roottherapy.backend.content.blog;

import com.roottherapy.backend.content.blog.dto.AdminBlogPostResponse;
import com.roottherapy.backend.content.blog.dto.AdminBlogReviewRequest;
import com.roottherapy.backend.content.blog.dto.AdminPublishBlogPostRequest;
import com.roottherapy.backend.content.blog.dto.AdminBlogLifecycleRequest;
import com.roottherapy.backend.content.blog.dto.UpdateAdminBlogPostRequest;
import com.roottherapy.backend.content.blog.dto.AdminBlogFeatureRequest;
import com.roottherapy.backend.content.blog.dto.AdminBlogReorderRequest;
import com.roottherapy.backend.content.blog.dto.AdminBlogReorderResponse;
import com.roottherapy.backend.content.blog.dto.CreateAdminBlogPostRequest;
import com.roottherapy.backend.users.AccountStatus;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRepository;
import com.roottherapy.backend.users.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

@Service
public class AdminBlogService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private static final Set<BlogPostStatus>
            PUBLISHABLE_STATUSES = EnumSet.of(
            BlogPostStatus.DRAFT,
            BlogPostStatus.SUBMITTED,
            BlogPostStatus.CHANGES_REQUESTED,
            BlogPostStatus.REJECTED
    );

    private static final Set<BlogPostStatus>
            REVIEWABLE_STATUSES = EnumSet.of(
            BlogPostStatus.SUBMITTED
    );

    private final BlogPostRepository blogPostRepository;



    private final UserRepository userRepository;

    private final BlogSlugService blogSlugService;

    public AdminBlogService(
            BlogPostRepository blogPostRepository,
            UserRepository userRepository,
            BlogSlugService blogSlugService
    ) {
        this.blogPostRepository =
                blogPostRepository;

        this.userRepository =
                userRepository;

        this.blogSlugService =
                blogSlugService;
    }

    @Transactional(readOnly = true)
    public Page<AdminBlogPostResponse> getPosts(
            UUID authenticatedUserId,
            BlogPostStatus status,
            int page,
            int size
    ) {
        getActiveAdmin(authenticatedUserId);

        Pageable pageable = PageRequest.of(
                validatePage(page),
                validateSize(size)
        );

        Page<BlogPost> posts;

        if (status == null) {
            posts = blogPostRepository
                    .findAllByOrderByUpdatedAtDesc(
                            pageable
                    );
        } else {
            posts = blogPostRepository
                    .findByStatusOrderBySubmittedAtAsc(
                            status,
                            pageable
                    );
        }

        return posts.map(
                AdminBlogPostResponse::fromEntity
        );
    }

    @Transactional(readOnly = true)
    public AdminBlogPostResponse getPost(
            UUID authenticatedUserId,
            UUID postId
    ) {
        getActiveAdmin(authenticatedUserId);

        BlogPost post = getPostOrThrow(postId);

        return AdminBlogPostResponse.fromEntity(post);
    }

    @Transactional
    public AdminBlogPostResponse publish(
            UUID authenticatedUserId,
            UUID postId,
            AdminPublishBlogPostRequest request
    ) {
        User admin = getActiveAdmin(
                authenticatedUserId
        );

        BlogPost post = getPostOrThrow(postId);

        validateVersion(post, request.version());

        if (!PUBLISHABLE_STATUSES.contains(
                post.getStatus()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This blog post cannot be published while its status is "
                            + post.getStatus()
            );
        }

        validateReadyForPublication(post);

        BlogPostStatus previousStatus =
                post.getStatus();

        Instant now = Instant.now();

        post.setStatus(BlogPostStatus.PUBLISHED);
        post.setPublishedAt(now);
        post.setReviewedBy(admin);
        post.setReviewedAt(now);
        post.setReviewNotes(null);
        post.setArchivedAt(null);

        BlogPost savedPost =
                blogPostRepository.saveAndFlush(post);

        saveHistory(
                savedPost,
                previousStatus,
                BlogPostStatus.PUBLISHED,
                admin,
                null
        );

        return AdminBlogPostResponse.fromEntity(
                savedPost
        );
    }

    @Transactional
    public AdminBlogPostResponse requestChanges(
            UUID authenticatedUserId,
            UUID postId,
            AdminBlogReviewRequest request
    ) {
        User admin = getActiveAdmin(
                authenticatedUserId
        );

        BlogPost post = getPostOrThrow(postId);

        validateVersion(post, request.version());
        validateReviewable(post);

        String note = request.note().trim();

        BlogPostStatus previousStatus =
                post.getStatus();

        Instant now = Instant.now();

        post.setStatus(
                BlogPostStatus.CHANGES_REQUESTED
        );

        post.setReviewNotes(note);
        post.setReviewedBy(admin);
        post.setReviewedAt(now);

        BlogPost savedPost =
                blogPostRepository.saveAndFlush(post);

        saveHistory(
                savedPost,
                previousStatus,
                BlogPostStatus.CHANGES_REQUESTED,
                admin,
                note
        );

        return AdminBlogPostResponse.fromEntity(
                savedPost
        );
    }

    @Transactional
    public AdminBlogPostResponse reject(
            UUID authenticatedUserId,
            UUID postId,
            AdminBlogReviewRequest request
    ) {
        User admin = getActiveAdmin(
                authenticatedUserId
        );

        BlogPost post = getPostOrThrow(postId);

        validateVersion(post, request.version());
        validateReviewable(post);

        String note = request.note().trim();

        BlogPostStatus previousStatus =
                post.getStatus();

        Instant now = Instant.now();

        post.setStatus(BlogPostStatus.REJECTED);
        post.setReviewNotes(note);
        post.setReviewedBy(admin);
        post.setReviewedAt(now);

        BlogPost savedPost =
                blogPostRepository.saveAndFlush(post);

        saveHistory(
                savedPost,
                previousStatus,
                BlogPostStatus.REJECTED,
                admin,
                note
        );

        return AdminBlogPostResponse.fromEntity(
                savedPost
        );
    }

    private void saveHistory(
            BlogPost post,
            BlogPostStatus fromStatus,
            BlogPostStatus toStatus,
            User changedBy,
            String note
    ) {
        BlogPostStatusHistory history =
                new BlogPostStatusHistory(
                        post,
                        fromStatus,
                        toStatus,
                        changedBy,
                        note
                );
    }

    @Transactional
    public AdminBlogPostResponse updatePost(
            UUID authenticatedUserId,
            UUID postId,
            UpdateAdminBlogPostRequest request
    ) {
        getActiveAdmin(authenticatedUserId);

        BlogPost post = getPostOrThrow(postId);

        validateVersion(post, request.version());

        String normalizedSlug =
                blogSlugService
                        .normalizeAndValidateUpdatedSlug(
                                request.slug(),
                                postId
                        );

        if (
                blogPostRepository.existsBySlugAndIdNot(
                        normalizedSlug,
                        postId
                )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A blog post with this slug already exists"
            );
        }

        post.setTitle(request.title().trim());
        post.setSlug(normalizedSlug);

        post.setSummary(
                cleanText(request.summary())
        );

        post.setBody(
                cleanText(request.body())
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
         * Editing does not automatically change:
         * - status
         * - author
         * - featured setting
         * - display order
         * - review data
         * - publication timestamps
         */
        BlogPost savedPost =
                blogPostRepository.saveAndFlush(post);

        return AdminBlogPostResponse.fromEntity(
                savedPost
        );
    }

    @Transactional
    public AdminBlogPostResponse unpublish(
            UUID authenticatedUserId,
            UUID postId,
            AdminBlogLifecycleRequest request
    ) {
        User admin = getActiveAdmin(
                authenticatedUserId
        );

        BlogPost post = getPostOrThrow(postId);

        validateVersion(post, request.version());

        if (post.getStatus() != BlogPostStatus.PUBLISHED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only a published post can be unpublished"
            );
        }

        BlogPostStatus previousStatus =
                post.getStatus();

        /*
         * Return the post to a private admin-editable draft.
         */
        post.setStatus(BlogPostStatus.DRAFT);
        post.setPublishedAt(null);
        post.setFeatured(false);

        BlogPost savedPost =
                blogPostRepository.saveAndFlush(post);

        saveHistory(
                savedPost,
                previousStatus,
                BlogPostStatus.DRAFT,
                admin,
                cleanNullableValue(request.note())
        );

        return AdminBlogPostResponse.fromEntity(
                savedPost
        );
    }

    @Transactional
    public AdminBlogPostResponse archive(
            UUID authenticatedUserId,
            UUID postId,
            AdminBlogLifecycleRequest request
    ) {
        User admin = getActiveAdmin(
                authenticatedUserId
        );

        BlogPost post = getPostOrThrow(postId);

        validateVersion(post, request.version());

        if (post.getStatus() == BlogPostStatus.ARCHIVED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This blog post is already archived"
            );
        }

        BlogPostStatus previousStatus =
                post.getStatus();

        post.setStatus(BlogPostStatus.ARCHIVED);
        post.setArchivedAt(Instant.now());
        post.setFeatured(false);

        /*
         * An archived post is no longer considered
         * currently published.
         */
        post.setPublishedAt(null);

        BlogPost savedPost =
                blogPostRepository.saveAndFlush(post);

        saveHistory(
                savedPost,
                previousStatus,
                BlogPostStatus.ARCHIVED,
                admin,
                cleanNullableValue(request.note())
        );

        return AdminBlogPostResponse.fromEntity(
                savedPost
        );
    }

    @Transactional
    public AdminBlogPostResponse restore(
            UUID authenticatedUserId,
            UUID postId,
            AdminBlogLifecycleRequest request
    ) {
        User admin = getActiveAdmin(
                authenticatedUserId
        );

        BlogPost post = getPostOrThrow(postId);

        validateVersion(post, request.version());

        if (post.getStatus() != BlogPostStatus.ARCHIVED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only an archived post can be restored"
            );
        }

        BlogPostStatus previousStatus =
                post.getStatus();

        /*
         * Restoration never publishes automatically.
         * The admin must review and publish it again.
         */
        post.setStatus(BlogPostStatus.DRAFT);
        post.setArchivedAt(null);
        post.setPublishedAt(null);
        post.setFeatured(false);

        BlogPost savedPost =
                blogPostRepository.saveAndFlush(post);

        saveHistory(
                savedPost,
                previousStatus,
                BlogPostStatus.DRAFT,
                admin,
                cleanNullableValue(request.note())
        );

        return AdminBlogPostResponse.fromEntity(
                savedPost
        );
    }

    @Transactional
    public AdminBlogPostResponse setFeatured(
            UUID authenticatedUserId,
            UUID postId,
            AdminBlogFeatureRequest request
    ) {
        getActiveAdmin(authenticatedUserId);

        BlogPost post = getPostOrThrow(postId);

        validateVersion(post, request.version());

        if (post.getStatus() != BlogPostStatus.PUBLISHED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only published posts can be featured"
            );
        }

        post.setFeatured(request.featured());

        BlogPost savedPost =
                blogPostRepository.saveAndFlush(post);

        return AdminBlogPostResponse.fromEntity(
                savedPost
        );
    }

    @Transactional
    public AdminBlogReorderResponse reorderPosts(
            UUID authenticatedUserId,
            AdminBlogReorderRequest request
    ) {
        getActiveAdmin(authenticatedUserId);

        validateReorderRequest(request);

        Set<UUID> requestedIds = new HashSet<>();

        for (
                AdminBlogReorderRequest.ReorderItem item
                : request.posts()
        ) {
            requestedIds.add(item.id());
        }

        List<BlogPost> posts =
                blogPostRepository.findAllByIdIn(
                        requestedIds
                );

        if (posts.size() != requestedIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "One or more blog posts were not found"
            );
        }

        Map<UUID, BlogPost> postsById =
                new HashMap<>();

        for (BlogPost post : posts) {
            postsById.put(post.getId(), post);
        }

        List<BlogPost> orderedPosts =
                new ArrayList<>();

        for (
                AdminBlogReorderRequest.ReorderItem item
                : request.posts()
        ) {
            BlogPost post = postsById.get(item.id());

            if (post.getStatus() != BlogPostStatus.PUBLISHED) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Only published posts can be reordered"
                );
            }

            validateVersion(
                    post,
                    item.version()
            );

            post.setDisplayOrder(
                    item.displayOrder()
            );

            orderedPosts.add(post);
        }

        List<BlogPost> savedPosts =
                blogPostRepository.saveAll(orderedPosts);

        blogPostRepository.flush();

        List<AdminBlogPostResponse> responsePosts =
                savedPosts.stream()
                        .sorted(
                                java.util.Comparator.comparing(
                                        BlogPost::getDisplayOrder
                                )
                        )
                        .map(
                                AdminBlogPostResponse::fromEntity
                        )
                        .toList();

        return new AdminBlogReorderResponse(
                responsePosts
        );
    }

    @Transactional
    public AdminBlogPostResponse createPost(
            UUID authenticatedUserId,
            CreateAdminBlogPostRequest request
    ) {
        User admin = getActiveAdmin(
                authenticatedUserId
        );

        String title =
                request.title().trim();

        String slug;

        if (
                request.slug() == null
                        || request.slug().isBlank()
        ) {
            slug = blogSlugService
                    .generateUniqueSlug(title);
        } else {
            slug = blogSlugService
                    .normalizeAndValidateNewSlug(
                            request.slug()
                    );
        }

        BlogPost post = new BlogPost(
                title,
                slug,
                admin
        );

        post.setSummary(
                cleanText(request.summary())
        );

        post.setBody(
                cleanText(request.body())
        );

        post.setFeaturedImageUrl(
                validateFeaturedImageUrl(
                        request.featuredImageUrl()
                )
        );

        post.setSeoTitle(
                cleanNullableValue(
                        request.seoTitle()
                )
        );

        post.setSeoDescription(
                cleanNullableValue(
                        request.seoDescription()
                )
        );

        post.setKeywords(
                normalizeKeywords(
                        request.keywords()
                )
        );

        /*
         * Admin-created posts start as private drafts.
         */
        post.setStatus(BlogPostStatus.DRAFT);
        post.setFeatured(false);
        post.setDisplayOrder(0);

        BlogPost savedPost =
                blogPostRepository.saveAndFlush(post);

        return AdminBlogPostResponse.fromEntity(
                savedPost
        );
    }

    private BlogPost getPostOrThrow(UUID postId) {
        return blogPostRepository.findById(postId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Blog post not found"
                        )
                );
    }

    private User getActiveAdmin(
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

        if (user.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Admin access is required"
            );
        }

        if (
                user.getAccountStatus()
                        != AccountStatus.ACTIVE
        ) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "The admin account is not active"
            );
        }

        return user;
    }

    private String validateFeaturedImageUrl(
            String featuredImageUrl
    ) {
        String value =
                cleanNullableValue(featuredImageUrl);

        if (value == null) {
            return null;
        }

        if (
                !value.startsWith("/uploads/blog/")
                        || value.contains("..")
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Featured image must be a valid blog upload"
            );
        }

        return value;
    }

    private void validateReorderRequest(
            AdminBlogReorderRequest request
    ) {
        Set<UUID> ids = new HashSet<>();
        Set<Integer> displayOrders = new HashSet<>();

        for (
                AdminBlogReorderRequest.ReorderItem item
                : request.posts()
        ) {
            if (!ids.add(item.id())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "The reorder request contains duplicate post IDs"
                );
            }

            if (!displayOrders.add(item.displayOrder())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "The reorder request contains duplicate display orders"
                );
            }
        }
    }

    private List<String> normalizeKeywords(
            List<String> keywords
    ) {
        if (keywords == null || keywords.isEmpty()) {
            return List.of();
        }

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

            uniqueKeywords.putIfAbsent(
                    cleaned.toLowerCase(Locale.ROOT),
                    cleaned
            );
        }

        return List.copyOf(
                uniqueKeywords.values()
        );
    }

    private String cleanText(String value) {
        return value == null
                ? ""
                : value.trim();
    }

    private String cleanNullableValue(
            String value
    ) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private void validateReviewable(
            BlogPost post
    ) {
        if (!REVIEWABLE_STATUSES.contains(
                post.getStatus()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This blog post cannot be reviewed while its status is "
                            + post.getStatus()
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

    private void validateReadyForPublication(
            BlogPost post
    ) {
        if (
                post.getTitle() == null
                        || post.getTitle().isBlank()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A title is required before publication"
            );
        }

        if (
                post.getSummary() == null
                        || post.getSummary().isBlank()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A summary is required before publication"
            );
        }

        if (
                post.getBody() == null
                        || post.getBody().isBlank()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Post body is required before publication"
            );
        }

        if (
                post.getSlug() == null
                        || post.getSlug().isBlank()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A slug is required before publication"
            );
        }

        String imageUrl =
                post.getFeaturedImageUrl();

        if (
                imageUrl != null
                        && (
                        !imageUrl.startsWith(
                                "/uploads/blog/"
                        )
                                || imageUrl.contains("..")
                )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The featured image path is invalid"
            );
        }
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