package com.roottherapy.backend.content.services;

import com.roottherapy.backend.content.services.dto.CreateServiceOfferingRequest;
import com.roottherapy.backend.content.services.dto.ServiceOfferingResponse;
import com.roottherapy.backend.content.services.dto.UpdateServiceOfferingRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.roottherapy.backend.content.services.dto.ReorderServicesRequest;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class ServiceOfferingManagementService {

    private final ServiceOfferingRepository repository;

    public ServiceOfferingManagementService(
            ServiceOfferingRepository repository
    ) {
        this.repository = repository;
    }

    /*
     * Public methods
     */

    @Transactional(readOnly = true)
    public List<ServiceOfferingResponse> getPublishedServices() {
        return repository
                .findByPublishedTrueAndArchivedFalseOrderByDisplayOrderAsc()
                .stream()
                .map(ServiceOfferingResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServiceOfferingResponse getPublishedServiceBySlug(String slug) {
        String normalisedSlug = normaliseSlug(slug);

        ServiceOffering service = repository
                .findBySlugAndPublishedTrueAndArchivedFalse(normalisedSlug)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Service not found"
                ));

        return ServiceOfferingResponse.fromEntity(service);
    }

    /*
     * Admin methods
     */

    @Transactional(readOnly = true)
    public List<ServiceOfferingResponse> getAllServicesForAdmin() {
        return repository
                .findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(ServiceOfferingResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServiceOfferingResponse getServiceByIdForAdmin(Long id) {
        ServiceOffering service = findById(id);

        return ServiceOfferingResponse.fromEntity(service);
    }

    public ServiceOfferingResponse createService(
            CreateServiceOfferingRequest request
    ) {
        String slug = normaliseSlug(request.slug());

        if (repository.existsBySlug(slug)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A service with this slug already exists"
            );
        }

        ServiceOffering service = new ServiceOffering(
                cleanRequiredText(request.title()),
                slug,
                cleanOptionalText(request.category()),
                cleanRequiredText(request.shortDescription())
        );

        service.setFullDescription(
                cleanList(request.fullDescription())
        );

        service.setPoints(
                cleanList(request.points())
        );

        service.setImageUrl(
                cleanOptionalText(request.imageUrl())
        );

        service.setDisplayOrder(request.displayOrder());

        service.setPublished(
                Boolean.TRUE.equals(request.published())
        );

        service.setArchived(false);

        service.setMetaTitle(
                cleanOptionalText(request.metaTitle())
        );

        service.setMetaDescription(
                cleanOptionalText(request.metaDescription())
        );

        service.setKeywords(
                cleanList(request.keywords())
        );

        ServiceOffering savedService = repository.save(service);

        return ServiceOfferingResponse.fromEntity(savedService);
    }

    public ServiceOfferingResponse updateService(
            Long id,
            UpdateServiceOfferingRequest request
    ) {
        ServiceOffering service = findById(id);
        String slug = normaliseSlug(request.slug());

        if (repository.existsBySlugAndIdNot(slug, id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Another service is already using this slug"
            );
        }

        service.setTitle(
                cleanRequiredText(request.title())
        );

        service.setSlug(slug);

        service.setCategory(
                cleanOptionalText(request.category())
        );

        service.setShortDescription(
                cleanRequiredText(request.shortDescription())
        );

        service.setFullDescription(
                cleanList(request.fullDescription())
        );

        service.setPoints(
                cleanList(request.points())
        );

        service.setImageUrl(
                cleanOptionalText(request.imageUrl())
        );

        service.setDisplayOrder(request.displayOrder());

        service.setPublished(
                Boolean.TRUE.equals(request.published())
        );

        service.setMetaTitle(
                cleanOptionalText(request.metaTitle())
        );

        service.setMetaDescription(
                cleanOptionalText(request.metaDescription())
        );

        service.setKeywords(
                cleanList(request.keywords())
        );

        ServiceOffering savedService = repository.save(service);

        return ServiceOfferingResponse.fromEntity(savedService);
    }

    public ServiceOfferingResponse setPublished(
            Long id,
            Boolean published
    ) {
        ServiceOffering service = findById(id);

        if (service.getArchived() && Boolean.TRUE.equals(published)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "An archived service cannot be published"
            );
        }

        service.setPublished(Boolean.TRUE.equals(published));

        ServiceOffering savedService = repository.save(service);

        return ServiceOfferingResponse.fromEntity(savedService);
    }

    public ServiceOfferingResponse setArchived(
            Long id,
            Boolean archived
    ) {
        ServiceOffering service = findById(id);
        boolean shouldArchive = Boolean.TRUE.equals(archived);

        service.setArchived(shouldArchive);

        /*
         * Archived services should not remain visible publicly.
         */
        if (shouldArchive) {
            service.setPublished(false);
        }

        ServiceOffering savedService = repository.save(service);

        return ServiceOfferingResponse.fromEntity(savedService);
    }

    public List<ServiceOfferingResponse> reorderServices(
            ReorderServicesRequest request
    ) {
        for (var orderItem : request.services()) {
            ServiceOffering service = findById(orderItem.id());
            service.setDisplayOrder(orderItem.displayOrder());
            repository.save(service);
        }

        return repository
                .findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(ServiceOfferingResponse::fromEntity)
                .toList();
    }

    /*
     * Internal helper methods
     */

    private ServiceOffering findById(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Service not found"
                ));
    }

    private String normaliseSlug(String value) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Slug is required"
            );
        }

        String slug = value
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");

        if (slug.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Slug must contain letters or numbers"
            );
        }

        return slug;
    }

    private String cleanRequiredText(String value) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Required text cannot be blank"
            );
        }

        return value.trim();
    }

    private String cleanOptionalText(String value) {
        if (value == null) {
            return null;
        }

        String cleaned = value.trim();

        return cleaned.isEmpty() ? null : cleaned;
    }

    private List<String> cleanList(List<String> values) {
        if (values == null) {
            return List.of();
        }

        return values.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .toList();
    }
}