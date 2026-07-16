package com.roottherapy.backend.content.blog;

import com.roottherapy.backend.content.blog.dto.CreateTherapistBlogPostRequest;
import com.roottherapy.backend.content.blog.dto.TherapistBlogPostResponse;
import com.roottherapy.backend.content.blog.dto.UpdateTherapistBlogPostRequest;
import com.roottherapy.backend.content.blog.dto.SubmitTherapistBlogPostRequest;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/therapist/blog")
public class TherapistBlogController {

    private final TherapistBlogService therapistBlogService;

    public TherapistBlogController(
            TherapistBlogService therapistBlogService
    ) {
        this.therapistBlogService =
                therapistBlogService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TherapistBlogPostResponse createDraft(
            Authentication authentication,
            @Valid @RequestBody
            CreateTherapistBlogPostRequest request
    ) {
        return therapistBlogService.createDraft(
                getAuthenticatedUserId(authentication),
                request
        );
    }

    @GetMapping
    public Page<TherapistBlogPostResponse> getOwnPosts(
            Authentication authentication,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {
        return therapistBlogService.getOwnPosts(
                getAuthenticatedUserId(authentication),
                page,
                size
        );
    }

    @GetMapping("/{postId}")
    public TherapistBlogPostResponse getOwnPost(
            Authentication authentication,
            @PathVariable UUID postId
    ) {
        return therapistBlogService.getOwnPost(
                getAuthenticatedUserId(authentication),
                postId
        );
    }

    @PostMapping("/{postId}/submit")
    public TherapistBlogPostResponse submitOwnPost(
            Authentication authentication,
            @PathVariable UUID postId,
            @Valid @RequestBody
            SubmitTherapistBlogPostRequest request
    ) {
        return therapistBlogService.submitOwnPost(
                getAuthenticatedUserId(authentication),
                postId,
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

    @PutMapping("/{postId}")
    public TherapistBlogPostResponse updateOwnPost(
            Authentication authentication,
            @PathVariable UUID postId,
            @Valid @RequestBody
            UpdateTherapistBlogPostRequest request
    ) {
        return therapistBlogService.updateOwnPost(
                getAuthenticatedUserId(authentication),
                postId,
                request
        );
    }
}