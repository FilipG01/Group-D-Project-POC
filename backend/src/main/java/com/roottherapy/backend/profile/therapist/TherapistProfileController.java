package com.roottherapy.backend.profile.therapist;


import com.roottherapy.backend.profile.therapist.dto.TherapistProfileRequest;
import com.roottherapy.backend.profile.therapist.dto.TherapistProfileResponse;
import com.roottherapy.backend.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/therapist-profile")
public class TherapistProfileController {

    private final TherapistProfileService therapistProfileService;

    public TherapistProfileController(TherapistProfileService therapistProfileService) {
        this.therapistProfileService = therapistProfileService;
    }

    @GetMapping("/me")
    public TherapistProfileResponse getMyProfile(Authentication auth){
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return therapistProfileService.getMyProfile(userDetails.getUser());
    }

    @PutMapping("/me")
    public TherapistProfileResponse updateMyProfile(Authentication auth, @Valid @RequestBody TherapistProfileRequest req){
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return therapistProfileService.updateMyProfile(userDetails.getUser(), req);
    }
}

