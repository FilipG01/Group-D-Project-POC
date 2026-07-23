package com.roottherapy.backend.profile.therapist.submission;

import com.roottherapy.backend.profile.therapist.submission.dto.*;
import com.roottherapy.backend.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/therapist-profile-submissions")
public class AdminTherapistProfileSubmissionController {
    private final TherapistProfileSubmissionService service;

    public AdminTherapistProfileSubmissionController(TherapistProfileSubmissionService service) {
        this.service = service;
    }

    @GetMapping
    public List<AdminTherapistProfileSubmissionResponse> getSubmissions(
            @RequestParam(required = false) TherapistProfileSubmissionStatus status
    ) {
        return service.getAdminSubmissions(status);
    }

    @GetMapping("/{submissionId}")
    public AdminTherapistProfileSubmissionResponse getSubmission(@PathVariable UUID submissionId) {
        return service.getAdminSubmission(submissionId);
    }

    @PostMapping("/{submissionId}/approve")
    public AdminTherapistProfileSubmissionResponse approve(
            Authentication authentication,
            @PathVariable UUID submissionId,
            @Valid @RequestBody AdminTherapistProfileApproveRequest request
    ) {
        return service.approve(userId(authentication), submissionId, request);
    }

    @PostMapping("/{submissionId}/request-changes")
    public AdminTherapistProfileSubmissionResponse requestChanges(
            Authentication authentication,
            @PathVariable UUID submissionId,
            @Valid @RequestBody AdminTherapistProfileReviewRequest request
    ) {
        return service.requestChanges(userId(authentication), submissionId, request);
    }

    @PostMapping("/{submissionId}/reject")
    public AdminTherapistProfileSubmissionResponse reject(
            Authentication authentication,
            @PathVariable UUID submissionId,
            @Valid @RequestBody AdminTherapistProfileReviewRequest request
    ) {
        return service.reject(userId(authentication), submissionId, request);
    }

    private UUID userId(Authentication authentication) {
        CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
        return details.getUser().getId();
    }
}
