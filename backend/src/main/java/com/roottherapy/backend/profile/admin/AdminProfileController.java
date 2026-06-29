package com.roottherapy.backend.profile.admin;

import com.roottherapy.backend.profile.admin.dto.AdminProfileRequest;
import com.roottherapy.backend.profile.admin.dto.AdminProfileResponse;
import com.roottherapy.backend.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin-profile")
public class AdminProfileController {
    private final AdminProfileService adminProfileService;

    public AdminProfileController(AdminProfileService adminProfileService){
        this.adminProfileService = adminProfileService;
    }

    @GetMapping("/me")
    public AdminProfileResponse getMyProfile(Authentication auth){
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return adminProfileService.getMyProfile(userDetails.getUser());

    }
    @PutMapping("/me")
    public AdminProfileResponse updateProfile(Authentication auth, @Valid @RequestBody AdminProfileRequest req){
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return adminProfileService.updateMyProfile(userDetails.getUser(), req);
    }
}