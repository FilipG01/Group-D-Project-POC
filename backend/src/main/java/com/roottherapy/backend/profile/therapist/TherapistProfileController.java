package com.roottherapy.backend.profile.therapist;

import com.roottherapy.backend.profile.therapist.dto.AdminTherapistProfileResponse;
import com.roottherapy.backend.profile.therapist.dto.UpdateOwnTherapistProfileRequest;
import com.roottherapy.backend.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/therapist-profile")
public class TherapistProfileController {

    private final TherapistProfileManagementService managementService;

    public TherapistProfileController(
            TherapistProfileManagementService managementService
    ) {
        this.managementService = managementService;
    }

    @GetMapping("/me")
    public AdminTherapistProfileResponse getMyProfile(
            Authentication authentication
    ) {
        return managementService.getOwnProfile(
                getAuthenticatedUserId(authentication)
        );
    }

    @PutMapping("/me")
    public AdminTherapistProfileResponse updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateOwnTherapistProfileRequest request
    ) {
        return managementService.updateOwnProfile(
                getAuthenticatedUserId(authentication),
                request
        );
    }

    private UUID getAuthenticatedUserId(
            Authentication authentication
    ) {
        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        return userDetails.getUser().getId();
    }
}