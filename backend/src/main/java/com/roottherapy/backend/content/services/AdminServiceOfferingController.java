package com.roottherapy.backend.content.services;

import com.roottherapy.backend.content.services.dto.CreateServiceOfferingRequest;
import com.roottherapy.backend.content.services.dto.ServiceArchiveRequest;
import com.roottherapy.backend.content.services.dto.ServiceOfferingResponse;
import com.roottherapy.backend.content.services.dto.ServicePublicationRequest;
import com.roottherapy.backend.content.services.dto.UpdateServiceOfferingRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.roottherapy.backend.content.services.dto.ReorderServicesRequest;

import java.util.List;

@RestController
@RequestMapping("/api/admin/services")
public class AdminServiceOfferingController {

    private final ServiceOfferingManagementService serviceOfferingService;

    public AdminServiceOfferingController(
            ServiceOfferingManagementService serviceOfferingService
    ) {
        this.serviceOfferingService = serviceOfferingService;
    }

    @GetMapping
    public List<ServiceOfferingResponse> getAllServices() {
        return serviceOfferingService.getAllServicesForAdmin();
    }

    @GetMapping("/{id}")
    public ServiceOfferingResponse getServiceById(
            @PathVariable Long id
    ) {
        return serviceOfferingService.getServiceByIdForAdmin(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceOfferingResponse createService(
            @Valid @RequestBody CreateServiceOfferingRequest request
    ) {
        return serviceOfferingService.createService(request);
    }

    @PutMapping("/{id}")
    public ServiceOfferingResponse updateService(
            @PathVariable Long id,
            @Valid @RequestBody UpdateServiceOfferingRequest request
    ) {
        return serviceOfferingService.updateService(id, request);
    }

    @PatchMapping("/{id}/publication")
    public ServiceOfferingResponse setPublished(
            @PathVariable Long id,
            @Valid @RequestBody ServicePublicationRequest request
    ) {
        return serviceOfferingService.setPublished(
                id,
                request.published()
        );
    }

    @PatchMapping("/{id}/archive")
    public ServiceOfferingResponse setArchived(
            @PathVariable Long id,
            @Valid @RequestBody ServiceArchiveRequest request
    ) {
        return serviceOfferingService.setArchived(
                id,
                request.archived()
        );
    }

    @PatchMapping("/reorder")
    public List<ServiceOfferingResponse> reorderServices(
            @Valid @RequestBody ReorderServicesRequest request
    ) {
        return serviceOfferingService.reorderServices(request);
    }
}