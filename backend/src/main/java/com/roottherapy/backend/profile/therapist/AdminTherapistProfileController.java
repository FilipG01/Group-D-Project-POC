package com.roottherapy.backend.profile.therapist;

import com.roottherapy.backend.profile.therapist.dto.AdminTherapistProfileResponse;
import com.roottherapy.backend.profile.therapist.dto.AdminUpdateTherapistProfileRequest;
import com.roottherapy.backend.profile.therapist.dto.ReorderTherapistsRequest;
import com.roottherapy.backend.profile.therapist.dto.CreateTherapistRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/therapists")
public class AdminTherapistProfileController {

    private final TherapistProfileManagementService managementService;

    public AdminTherapistProfileController(
            TherapistProfileManagementService managementService
    ) {
        this.managementService = managementService;
    }

    @GetMapping
    public List<AdminTherapistProfileResponse> getAllTherapists() {
        return managementService.getAllTherapistsForAdmin();
    }

    @GetMapping("/{userId}")
    public AdminTherapistProfileResponse getTherapist(
            @PathVariable UUID userId
    ) {
        return managementService.getTherapistForAdmin(userId);
    }

    @PutMapping("/{userId}")
    public AdminTherapistProfileResponse updateTherapist(
            @PathVariable UUID userId,
            @Valid @RequestBody AdminUpdateTherapistProfileRequest request
    ) {
        return managementService.updateTherapistAsAdmin(
                userId,
                request
        );
    }

    @PatchMapping("/reorder")
    public List<AdminTherapistProfileResponse> reorderTherapists(
            @Valid @RequestBody ReorderTherapistsRequest request
    ) {
        return managementService.reorderTherapists(request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminTherapistProfileResponse createTherapist(
            @Valid @RequestBody CreateTherapistRequest request
    ) {
        return managementService.createTherapist(request);
    }
}