package com.roottherapy.backend.profile.therapist.submission;

import com.roottherapy.backend.profile.therapist.submission.dto.*;
import com.roottherapy.backend.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/therapist-profile/me/draft")
public class TherapistProfileSubmissionController {
    private final TherapistProfileSubmissionService service;

    public TherapistProfileSubmissionController(TherapistProfileSubmissionService service) {
        this.service = service;
    }

    @GetMapping
    public TherapistProfileSubmissionResponse getDraft(Authentication authentication) {
        return service.getOrCreateOwnDraft(userId(authentication));
    }

    @PutMapping
    public TherapistProfileSubmissionResponse updateDraft(
            Authentication authentication,
            @Valid @RequestBody UpdateTherapistProfileDraftRequest request
    ) {
        return service.updateOwnDraft(userId(authentication), request);
    }

    @PostMapping("/submit")
    public TherapistProfileSubmissionResponse submitDraft(
            Authentication authentication,
            @Valid @RequestBody SubmitTherapistProfileDraftRequest request
    ) {
        return service.submitOwnDraft(userId(authentication), request);
    }

    private UUID userId(Authentication authentication) {
        CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
        return details.getUser().getId();
    }
}
