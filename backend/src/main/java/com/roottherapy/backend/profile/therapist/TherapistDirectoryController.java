package com.roottherapy.backend.profile.therapist;


import com.roottherapy.backend.profile.therapist.dto.TherapistDirectoryResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/therapists")
public class TherapistDirectoryController {
    private final TherapistDirectoryService therapistDirectoryService;

    public TherapistDirectoryController(TherapistDirectoryService therapistDirectoryService){
        this.therapistDirectoryService = therapistDirectoryService;
    }

    @GetMapping
    public List<TherapistDirectoryResponse> listAvailableTherapists(){
        return therapistDirectoryService.listAvailableTherapists();
    }

    @GetMapping("/{userId}")
    public TherapistDirectoryResponse getAvailableTherapist(@PathVariable UUID userId){
        return therapistDirectoryService.getAvailableTherapist(userId);
    }
}
