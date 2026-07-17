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
import com.roottherapy.backend.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/blog")
public class AdminBlogController {

    private final AdminBlogService adminBlogService;

    public AdminBlogController(
            AdminBlogService adminBlogService
    ) {
        this.adminBlogService = adminBlogService;
    }

    @GetMapping
    public Page<AdminBlogPostResponse> getPosts(
            Authentication authentication,

            @RequestParam(required = false)
            BlogPostStatus status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {
        return adminBlogService.getPosts(
                getAuthenticatedUserId(authentication),
                status,
                page,
                size
        );
    }

    @GetMapping("/{postId}")
    public AdminBlogPostResponse getPost(
            Authentication authentication,
            @PathVariable UUID postId
    ) {
        return adminBlogService.getPost(
                getAuthenticatedUserId(authentication),
                postId
        );
    }

    @PostMapping("/{postId}/publish")
    public AdminBlogPostResponse publish(
            Authentication authentication,
            @PathVariable UUID postId,
            @Valid @RequestBody
            AdminPublishBlogPostRequest request
    ) {
        return adminBlogService.publish(
                getAuthenticatedUserId(authentication),
                postId,
                request
        );
    }

    @PostMapping("/{postId}/request-changes")
    public AdminBlogPostResponse requestChanges(
            Authentication authentication,
            @PathVariable UUID postId,
            @Valid @RequestBody
            AdminBlogReviewRequest request
    ) {
        return adminBlogService.requestChanges(
                getAuthenticatedUserId(authentication),
                postId,
                request
        );
    }

    @PostMapping("/{postId}/reject")
    public AdminBlogPostResponse reject(
            Authentication authentication,
            @PathVariable UUID postId,
            @Valid @RequestBody
            AdminBlogReviewRequest request
    ) {
        return adminBlogService.reject(
                getAuthenticatedUserId(authentication),
                postId,
                request
        );
    }

    @PutMapping("/{postId}")
    public AdminBlogPostResponse updatePost(
            Authentication authentication,
            @PathVariable UUID postId,
            @Valid @RequestBody
            UpdateAdminBlogPostRequest request
    ) {
        return adminBlogService.updatePost(
                getAuthenticatedUserId(authentication),
                postId,
                request
        );
    }

    @PostMapping("/{postId}/unpublish")
    public AdminBlogPostResponse unpublish(
            Authentication authentication,
            @PathVariable UUID postId,
            @Valid @RequestBody
            AdminBlogLifecycleRequest request
    ) {
        return adminBlogService.unpublish(
                getAuthenticatedUserId(authentication),
                postId,
                request
        );
    }

    @PostMapping("/{postId}/archive")
    public AdminBlogPostResponse archive(
            Authentication authentication,
            @PathVariable UUID postId,
            @Valid @RequestBody
            AdminBlogLifecycleRequest request
    ) {
        return adminBlogService.archive(
                getAuthenticatedUserId(authentication),
                postId,
                request
        );
    }

    @PostMapping("/{postId}/restore")
    public AdminBlogPostResponse restore(
            Authentication authentication,
            @PathVariable UUID postId,
            @Valid @RequestBody
            AdminBlogLifecycleRequest request
    ) {
        return adminBlogService.restore(
                getAuthenticatedUserId(authentication),
                postId,
                request
        );
    }

    @PatchMapping("/{postId}/feature")
    public AdminBlogPostResponse setFeatured(
            Authentication authentication,
            @PathVariable UUID postId,
            @Valid @RequestBody
            AdminBlogFeatureRequest request
    ) {
        return adminBlogService.setFeatured(
                getAuthenticatedUserId(authentication),
                postId,
                request
        );
    }

    @PatchMapping("/reorder")
    public AdminBlogReorderResponse reorderPosts(
            Authentication authentication,
            @Valid @RequestBody
            AdminBlogReorderRequest request
    ) {
        return adminBlogService.reorderPosts(
                getAuthenticatedUserId(authentication),
                request
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminBlogPostResponse createPost(
            Authentication authentication,
            @Valid @RequestBody
            CreateAdminBlogPostRequest request
    ) {
        return adminBlogService.createPost(
                getAuthenticatedUserId(authentication),
                request
        );
    }

    private UUID getAuthenticatedUserId(
            Authentication authentication
    ) {
        if (
                authentication == null
                        || !(authentication.getPrincipal()
                        instanceof CustomUserDetails userDetails)
        ) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication is required"
            );
        }

        return userDetails.getUser().getId();
    }
}