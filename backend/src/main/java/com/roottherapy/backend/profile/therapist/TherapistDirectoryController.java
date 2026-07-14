package com.roottherapy.backend.profile.therapist;

import com.roottherapy.backend.profile.therapist.dto.PublicTherapistProfileResponse;
import com.roottherapy.backend.profile.therapist.dto.TherapistDirectoryResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/therapists")
public class TherapistDirectoryController {

    private final TherapistDirectoryService directoryService;
    private final TherapistProfileManagementService managementService;

    public TherapistDirectoryController(
            TherapistDirectoryService directoryService,
            TherapistProfileManagementService managementService
    ) {
        this.directoryService = directoryService;
        this.managementService = managementService;
    }

    /*
     * Existing authenticated client directory.
     * Only therapists accepting clients are returned.
     */

    @GetMapping
    public List<TherapistDirectoryResponse> listAvailableTherapists() {
        return directoryService.listAvailableTherapists();
    }

    @GetMapping("/{userId}")
    public TherapistDirectoryResponse getAvailableTherapist(
            @PathVariable UUID userId
    ) {
        return directoryService.getAvailableTherapist(userId);
    }

    /*
     * Public About-page profiles.
     * Visibility is controlled independently of accepting-client status.
     */

    @GetMapping("/public")
    public List<PublicTherapistProfileResponse> listPublicTherapists() {
        return managementService.getPublicTherapists();
    }

    @GetMapping("/public/{userId}")
    public PublicTherapistProfileResponse getPublicTherapist(
            @PathVariable UUID userId
    ) {
        return managementService.getPublicTherapist(userId);
    }
}
