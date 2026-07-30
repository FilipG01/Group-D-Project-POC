package com.roottherapy.backend.content.gallery;

import com.roottherapy.backend.content.gallery.dto.CreateGalleryImageRequest;
import com.roottherapy.backend.content.gallery.dto.GalleryImageOrderItem;
import com.roottherapy.backend.content.gallery.dto.GalleryImageResponse;
import com.roottherapy.backend.content.gallery.dto.ReorderGalleryImagesRequest;
import com.roottherapy.backend.content.gallery.dto.UpdateGalleryImageRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GalleryManagementServiceTest {

    @Mock
    private GalleryImageRepository repository;

    private GalleryManagementService service;

    @BeforeEach
    void setUp() {
        service = new GalleryManagementService(repository);
    }

    // JUNIT-GAL-001
    @Test
    void createGalleryImage_validRequest_sanitisesAndSavesRecord() {
        CreateGalleryImageRequest request = new CreateGalleryImageRequest(
                "  /uploads/gallery/therapy-room.jpg  ",
                "  A calm therapy room  ",
                "  A comfortable therapy room with two chairs  ",
                3,
                true
        );

        when(repository.save(any(GalleryImage.class))).thenAnswer(invocation -> {
            GalleryImage saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        GalleryImageResponse result = service.createGalleryImage(request);

        ArgumentCaptor<GalleryImage> captor =
                ArgumentCaptor.forClass(GalleryImage.class);

        verify(repository).save(captor.capture());
        GalleryImage saved = captor.getValue();

        assertEquals("/uploads/gallery/therapy-room.jpg", saved.getImageUrl());
        assertEquals("A calm therapy room", saved.getCaption());
        assertEquals(
                "A comfortable therapy room with two chairs",
                saved.getAltText()
        );
        assertEquals(3, saved.getDisplayOrder());
        assertTrue(saved.getVisible());
        assertFalse(saved.getArchived());

        assertEquals(1L, result.id());
        assertEquals(saved.getImageUrl(), result.imageUrl());
        assertEquals(saved.getCaption(), result.caption());
        assertEquals(saved.getAltText(), result.altText());
        assertEquals(saved.getDisplayOrder(), result.displayOrder());
        assertTrue(result.visible());
        assertFalse(result.archived());
    }

    // JUNIT-GAL-002
    @ParameterizedTest(name = "imageUrl={0}, altText={1}")
    @MethodSource("invalidRequiredTextRequests")
    void createGalleryImage_blankRequiredText_throwsBadRequest(
            String imageUrl,
            String altText
    ) {
        CreateGalleryImageRequest request = new CreateGalleryImageRequest(
                imageUrl,
                "Optional caption",
                altText,
                0,
                false
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.createGalleryImage(request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Required text cannot be blank", exception.getReason());
        verify(repository, never()).save(any(GalleryImage.class));
    }

    static Stream<Arguments> invalidRequiredTextRequests() {
        return Stream.of(
                Arguments.of(null, "Valid alt text"),
                Arguments.of("   ", "Valid alt text"),
                Arguments.of("/uploads/gallery/image.jpg", null),
                Arguments.of("/uploads/gallery/image.jpg", "   ")
        );
    }

    // JUNIT-GAL-003
    @Test
    void updateGalleryImage_validRequest_updatesImageReferenceAndMetadata() {
        GalleryImage image = galleryImage(2L, false, false, 0);
        image.setCaption("Old caption");
        image.setAltText("Old alt text");
        String oldImageUrl = image.getImageUrl();

        UpdateGalleryImageRequest request = new UpdateGalleryImageRequest(
                "  /uploads/gallery/replacement.jpg  ",
                "  Updated room caption  ",
                "  Updated accessible description  ",
                5,
                true
        );

        when(repository.findById(2L)).thenReturn(Optional.of(image));
        when(repository.save(image)).thenReturn(image);

        GalleryImageResponse result = service.updateGalleryImage(2L, request);

        assertEquals("/uploads/gallery/replacement.jpg", image.getImageUrl());
        assertFalse(oldImageUrl.equals(image.getImageUrl()));
        assertEquals("Updated room caption", image.getCaption());
        assertEquals("Updated accessible description", image.getAltText());
        assertEquals(5, image.getDisplayOrder());
        assertTrue(image.getVisible());
        assertFalse(image.getArchived());

        assertEquals(image.getId(), result.id());
        assertEquals(image.getImageUrl(), result.imageUrl());
        assertEquals(image.getCaption(), result.caption());
        assertEquals(image.getAltText(), result.altText());
        assertEquals(5, result.displayOrder());
        assertTrue(result.visible());
        verify(repository).save(image);
    }

    // JUNIT-GAL-004
    @ParameterizedTest(name = "visible={0}")
    @MethodSource("visibilityValues")
    void setVisible_existingActiveImage_updatesVisibility(boolean visible) {
        GalleryImage image = galleryImage(3L, !visible, false, 1);

        when(repository.findById(3L)).thenReturn(Optional.of(image));
        when(repository.save(image)).thenReturn(image);

        GalleryImageResponse result = service.setVisible(3L, visible);

        assertEquals(visible, image.getVisible());
        assertEquals(visible, result.visible());
        assertFalse(result.archived());
        verify(repository).save(image);
    }

    static Stream<Boolean> visibilityValues() {
        return Stream.of(true, false);
    }

    // JUNIT-GAL-005
    @Test
    void setVisible_archivedImageToTrue_throwsBadRequest() {
        GalleryImage image = galleryImage(4L, false, true, 2);
        when(repository.findById(4L)).thenReturn(Optional.of(image));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.setVisible(4L, true)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(
                "An archived gallery image cannot be made visible",
                exception.getReason()
        );
        assertFalse(image.getVisible());
        verify(repository, never()).save(any(GalleryImage.class));
    }

    // JUNIT-GAL-006
    @Test
    void setArchived_visibleImage_archivesAndHidesImage() {
        GalleryImage image = galleryImage(5L, true, false, 2);
        when(repository.findById(5L)).thenReturn(Optional.of(image));
        when(repository.save(image)).thenReturn(image);

        GalleryImageResponse result = service.setArchived(5L, true);

        assertTrue(image.getArchived());
        assertFalse(image.getVisible());
        assertTrue(result.archived());
        assertFalse(result.visible());
        verify(repository).save(image);
    }

    // JUNIT-GAL-007
    @Test
    void reorderGalleryImages_validRequest_updatesOrdersAndReturnsSortedList() {
        GalleryImage first = galleryImage(10L, true, false, 0);
        GalleryImage second = galleryImage(20L, true, false, 1);

        ReorderGalleryImagesRequest request =
                new ReorderGalleryImagesRequest(List.of(
                        new GalleryImageOrderItem(10L, 4),
                        new GalleryImageOrderItem(20L, 1)
                ));

        when(repository.findById(10L)).thenReturn(Optional.of(first));
        when(repository.findById(20L)).thenReturn(Optional.of(second));
        when(repository.save(first)).thenReturn(first);
        when(repository.save(second)).thenReturn(second);
        when(repository.findAllByOrderByDisplayOrderAsc())
                .thenReturn(List.of(second, first));

        List<GalleryImageResponse> result =
                service.reorderGalleryImages(request);

        assertEquals(4, first.getDisplayOrder());
        assertEquals(1, second.getDisplayOrder());
        assertEquals(
                List.of(20L, 10L),
                result.stream().map(GalleryImageResponse::id).toList()
        );
        assertEquals(
                List.of(1, 4),
                result.stream().map(GalleryImageResponse::displayOrder).toList()
        );
        verify(repository).save(first);
        verify(repository).save(second);
    }

    // JUNIT-GAL-008
    @Test
    void getGalleryImageByIdForAdmin_invalidId_throwsNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.getGalleryImageByIdForAdmin(999L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Gallery image not found", exception.getReason());
        verify(repository).findById(999L);
    }

    private GalleryImage galleryImage(
            Long id,
            boolean visible,
            boolean archived,
            int displayOrder
    ) {
        GalleryImage image = new GalleryImage(
                "/uploads/gallery/image-" + id + ".jpg",
                "Caption " + id,
                "Alt text " + id
        );
        image.setId(id);
        image.setVisible(visible);
        image.setArchived(archived);
        image.setDisplayOrder(displayOrder);
        return image;
    }
}
