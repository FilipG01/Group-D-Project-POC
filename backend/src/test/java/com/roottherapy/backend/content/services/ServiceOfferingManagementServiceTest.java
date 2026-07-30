package com.roottherapy.backend.content.services;

import com.roottherapy.backend.content.services.dto.CreateServiceOfferingRequest;
import com.roottherapy.backend.content.services.dto.ReorderServicesRequest;
import com.roottherapy.backend.content.services.dto.ServiceOfferingResponse;
import com.roottherapy.backend.content.services.dto.ServiceOrderItem;
import com.roottherapy.backend.content.services.dto.UpdateServiceOfferingRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceOfferingManagementServiceTest {

    @Mock
    private ServiceOfferingRepository repository;

    private ServiceOfferingManagementService service;

    @BeforeEach
    void setUp() {
        service = new ServiceOfferingManagementService(repository);
    }

    // JUNIT-SVC-001
    @Test
    void createService_validData_savesService() {
        CreateServiceOfferingRequest request = validCreateRequest();

        when(repository.existsBySlug("individual-therapy")).thenReturn(false);
        when(repository.save(any(ServiceOffering.class))).thenAnswer(invocation -> {
            ServiceOffering saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        ServiceOfferingResponse result = service.createService(request);

        assertEquals(1L, result.id());
        assertEquals("Individual Therapy", result.title());
        assertEquals("individual-therapy", result.slug());
        assertEquals("Therapy", result.category());
        assertEquals("One-to-one therapeutic support", result.shortDescription());
        assertEquals(List.of("First paragraph", "Second paragraph"), result.fullDescription());
        assertEquals(List.of("Anxiety support", "Stress management"), result.points());
        assertEquals("/uploads/services/individual.jpg", result.imageUrl());
        assertEquals(2, result.displayOrder());
        assertFalse(result.published());
        assertFalse(result.archived());
        assertEquals("Individual Therapy Dublin", result.metaTitle());
        assertEquals("Private individual therapy sessions", result.metaDescription());
        assertEquals(List.of("therapy", "counselling"), result.keywords());

        verify(repository).existsBySlug("individual-therapy");
        verify(repository).save(any(ServiceOffering.class));
    }

    // JUNIT-SVC-002
    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidRequiredFieldRequests")
    void createService_missingRequiredFields_throwsValidationException(
            String description,
            CreateServiceOfferingRequest request
    ) {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.createService(request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(repository, never()).save(any(ServiceOffering.class));
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> invalidRequiredFieldRequests() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(
                        "blank title",
                        createRequest("   ", "valid-slug", "Valid description")
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        "blank slug",
                        createRequest("Valid title", "   ", "Valid description")
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        "blank short description",
                        createRequest("Valid title", "valid-slug", "   ")
                )
        );
    }

    // JUNIT-SVC-003
    @Test
    void createService_duplicateSlug_throwsConflictException() {
        CreateServiceOfferingRequest request = validCreateRequest();
        when(repository.existsBySlug("individual-therapy")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.createService(request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("A service with this slug already exists", exception.getReason());
        verify(repository, never()).save(any(ServiceOffering.class));
    }

    // JUNIT-SVC-004
    @Test
    void updateService_validData_persistsFields() {
        ServiceOffering existing = serviceEntity(1L, "Old Service", "old-service", 0, false, false);

        UpdateServiceOfferingRequest request = new UpdateServiceOfferingRequest(
                "  Updated Service  ",
                "  Updated Service!!!  ",
                "  Wellbeing  ",
                "  Updated short description  ",
                Arrays.asList("  Paragraph one  ", "", null, "Paragraph two"),
                Arrays.asList("  Point one  ", null, "Point two"),
                "  /uploads/services/updated.jpg  ",
                4,
                true,
                "  Updated meta title  ",
                "  Updated meta description  ",
                Arrays.asList("  updated  ", "", null, "service")
        );

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsBySlugAndIdNot("updated-service", 1L)).thenReturn(false);
        when(repository.save(existing)).thenReturn(existing);

        ServiceOfferingResponse result = service.updateService(1L, request);

        assertEquals("Updated Service", existing.getTitle());
        assertEquals("updated-service", existing.getSlug());
        assertEquals("Wellbeing", existing.getCategory());
        assertEquals("Updated short description", existing.getShortDescription());
        assertEquals(List.of("Paragraph one", "Paragraph two"), existing.getFullDescription());
        assertEquals(List.of("Point one", "Point two"), existing.getPoints());
        assertEquals("/uploads/services/updated.jpg", existing.getImageUrl());
        assertEquals(4, existing.getDisplayOrder());
        assertTrue(existing.getPublished());
        assertEquals("Updated meta title", existing.getMetaTitle());
        assertEquals("Updated meta description", existing.getMetaDescription());
        assertEquals(List.of("updated", "service"), existing.getKeywords());

        assertEquals(existing.getId(), result.id());
        assertEquals(existing.getSlug(), result.slug());
        assertEquals(existing.getFullDescription(), result.fullDescription());
        verify(repository).save(existing);
    }

    // JUNIT-SVC-005
    @Test
    void publishService_existingDraft_setsPublished() {
        ServiceOffering draft = serviceEntity(2L, "Draft Service", "draft-service", 1, false, false);
        when(repository.findById(2L)).thenReturn(Optional.of(draft));
        when(repository.save(draft)).thenReturn(draft);

        ServiceOfferingResponse result = service.setPublished(2L, true);

        assertTrue(draft.getPublished());
        assertTrue(result.published());
        assertFalse(result.archived());
        verify(repository).save(draft);
    }

    // JUNIT-SVC-006
    @Test
    void unpublishService_publishedService_hidesPublicly() {
        ServiceOffering published = serviceEntity(3L, "Published Service", "published-service", 1, true, false);
        when(repository.findById(3L)).thenReturn(Optional.of(published));
        when(repository.save(published)).thenReturn(published);

        ServiceOfferingResponse result = service.setPublished(3L, false);

        assertFalse(published.getPublished());
        assertFalse(result.published());
        verify(repository).save(published);
    }

    // JUNIT-SVC-007
    @Test
    void archiveService_existingService_setsArchivedAndUnpublishes() {
        ServiceOffering published = serviceEntity(4L, "Archive Me", "archive-me", 1, true, false);
        when(repository.findById(4L)).thenReturn(Optional.of(published));
        when(repository.save(published)).thenReturn(published);

        ServiceOfferingResponse result = service.setArchived(4L, true);

        assertTrue(published.getArchived());
        assertFalse(published.getPublished());
        assertTrue(result.archived());
        assertFalse(result.published());
        verify(repository).save(published);
    }

    // JUNIT-SVC-008
    @Test
    void reorderServices_validOrder_updatesDisplayOrder() {
        ServiceOffering first = serviceEntity(10L, "First", "first", 0, true, false);
        ServiceOffering second = serviceEntity(20L, "Second", "second", 1, true, false);

        ReorderServicesRequest request = new ReorderServicesRequest(List.of(
                new ServiceOrderItem(10L, 2),
                new ServiceOrderItem(20L, 0)
        ));

        when(repository.findById(10L)).thenReturn(Optional.of(first));
        when(repository.findById(20L)).thenReturn(Optional.of(second));
        when(repository.save(first)).thenReturn(first);
        when(repository.save(second)).thenReturn(second);
        when(repository.findAllByOrderByDisplayOrderAsc()).thenReturn(List.of(second, first));

        List<ServiceOfferingResponse> result = service.reorderServices(request);

        assertEquals(2, first.getDisplayOrder());
        assertEquals(0, second.getDisplayOrder());
        assertEquals(List.of(20L, 10L), result.stream().map(ServiceOfferingResponse::id).toList());
        assertEquals(List.of(0, 2), result.stream().map(ServiceOfferingResponse::displayOrder).toList());
        verify(repository).save(first);
        verify(repository).save(second);
    }

    // JUNIT-SVC-009
    @Test
    void getService_invalidId_throwsNotFoundException() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.getServiceByIdForAdmin(999L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Service not found", exception.getReason());
        verify(repository).findById(999L);
    }

    private CreateServiceOfferingRequest validCreateRequest() {
        return new CreateServiceOfferingRequest(
                "  Individual Therapy  ",
                "  Individual Therapy!!!  ",
                "  Therapy  ",
                "  One-to-one therapeutic support  ",
                Arrays.asList("  First paragraph  ", "", null, "Second paragraph"),
                Arrays.asList("  Anxiety support  ", null, "Stress management"),
                "  /uploads/services/individual.jpg  ",
                2,
                false,
                "  Individual Therapy Dublin  ",
                "  Private individual therapy sessions  ",
                Arrays.asList("  therapy  ", "", null, "counselling")
        );
    }

    private static CreateServiceOfferingRequest createRequest(
            String title,
            String slug,
            String shortDescription
    ) {
        return new CreateServiceOfferingRequest(
                title,
                slug,
                "Category",
                shortDescription,
                List.of("Full description"),
                List.of("Point"),
                null,
                0,
                false,
                null,
                null,
                List.of()
        );
    }

    private ServiceOffering serviceEntity(
            Long id,
            String title,
            String slug,
            int displayOrder,
            boolean published,
            boolean archived
    ) {
        ServiceOffering offering = new ServiceOffering(
                title,
                slug,
                "Category",
                "Short description"
        );
        offering.setId(id);
        offering.setFullDescription(List.of("Full description"));
        offering.setPoints(List.of("Point"));
        offering.setKeywords(List.of("keyword"));
        offering.setDisplayOrder(displayOrder);
        offering.setPublished(published);
        offering.setArchived(archived);
        assertNotNull(offering.getFullDescription());
        return offering;
    }
}
