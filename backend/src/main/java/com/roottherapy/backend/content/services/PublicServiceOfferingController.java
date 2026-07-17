package com.roottherapy.backend.content.services;

import com.roottherapy.backend.content.services.dto.ServiceOfferingResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class PublicServiceOfferingController {

    private final ServiceOfferingManagementService serviceOfferingService;

    public PublicServiceOfferingController(
            ServiceOfferingManagementService serviceOfferingService
    ) {
        this.serviceOfferingService = serviceOfferingService;
    }

    @GetMapping
    public List<ServiceOfferingResponse> getPublishedServices() {
        return serviceOfferingService.getPublishedServices();
    }

    @GetMapping("/{slug}")
    public ServiceOfferingResponse getPublishedServiceBySlug(
            @PathVariable String slug
    ) {
        return serviceOfferingService.getPublishedServiceBySlug(slug);
    }
}