package com.roottherapy.backend.content.gallery;

import com.roottherapy.backend.content.gallery.dto.CreateGalleryImageRequest;
import com.roottherapy.backend.content.gallery.dto.GalleryImageResponse;
import com.roottherapy.backend.content.gallery.dto.ReorderGalleryImagesRequest;
import com.roottherapy.backend.content.gallery.dto.UpdateGalleryImageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class GalleryManagementService {

    private final GalleryImageRepository repository;

    public GalleryManagementService(
            GalleryImageRepository repository
    ) {
        this.repository = repository;
    }

    /*
     * Public methods
     */

    @Transactional(readOnly = true)
    public List<GalleryImageResponse> getVisibleGalleryImages() {
        return repository
                .findByVisibleTrueAndArchivedFalseOrderByDisplayOrderAsc()
                .stream()
                .map(GalleryImageResponse::fromEntity)
                .toList();
    }

    /*
     * Admin methods
     */

    @Transactional(readOnly = true)
    public List<GalleryImageResponse> getAllGalleryImagesForAdmin() {
        return repository
                .findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(GalleryImageResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public GalleryImageResponse getGalleryImageByIdForAdmin(
            Long id
    ) {
        return GalleryImageResponse.fromEntity(
                findById(id)
        );
    }

    public GalleryImageResponse createGalleryImage(
            CreateGalleryImageRequest request
    ) {
        GalleryImage image = new GalleryImage(
                cleanRequiredText(request.imageUrl()),
                cleanOptionalText(request.caption()),
                cleanRequiredText(request.altText())
        );

        image.setDisplayOrder(request.displayOrder());
        image.setVisible(Boolean.TRUE.equals(request.visible()));
        image.setArchived(false);

        GalleryImage savedImage = repository.save(image);

        return GalleryImageResponse.fromEntity(savedImage);
    }

    public GalleryImageResponse updateGalleryImage(
            Long id,
            UpdateGalleryImageRequest request
    ) {
        GalleryImage image = findById(id);

        image.setImageUrl(
                cleanRequiredText(request.imageUrl())
        );

        image.setCaption(
                cleanOptionalText(request.caption())
        );

        image.setAltText(
                cleanRequiredText(request.altText())
        );

        image.setDisplayOrder(request.displayOrder());

        /*
         * Archived images cannot be made publicly visible.
         */
        if (
                Boolean.TRUE.equals(image.getArchived()) &&
                        Boolean.TRUE.equals(request.visible())
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "An archived gallery image cannot be made visible"
            );
        }

        image.setVisible(
                Boolean.TRUE.equals(request.visible())
        );

        GalleryImage savedImage = repository.save(image);

        return GalleryImageResponse.fromEntity(savedImage);
    }

    public GalleryImageResponse setVisible(
            Long id,
            Boolean visible
    ) {
        GalleryImage image = findById(id);

        if (
                Boolean.TRUE.equals(image.getArchived()) &&
                        Boolean.TRUE.equals(visible)
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "An archived gallery image cannot be made visible"
            );
        }

        image.setVisible(Boolean.TRUE.equals(visible));

        GalleryImage savedImage = repository.save(image);

        return GalleryImageResponse.fromEntity(savedImage);
    }

    public GalleryImageResponse setArchived(
            Long id,
            Boolean archived
    ) {
        GalleryImage image = findById(id);

        boolean shouldArchive =
                Boolean.TRUE.equals(archived);

        image.setArchived(shouldArchive);

        /*
         * An archived image must not remain visible publicly.
         */
        if (shouldArchive) {
            image.setVisible(false);
        }

        GalleryImage savedImage = repository.save(image);

        return GalleryImageResponse.fromEntity(savedImage);
    }

    public List<GalleryImageResponse> reorderGalleryImages(
            ReorderGalleryImagesRequest request
    ) {
        for (var orderItem : request.images()) {
            GalleryImage image = findById(
                    orderItem.id()
            );

            image.setDisplayOrder(
                    orderItem.displayOrder()
            );

            repository.save(image);
        }

        return repository
                .findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(GalleryImageResponse::fromEntity)
                .toList();
    }

    /*
     * Internal helpers
     */

    private GalleryImage findById(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Gallery image not found"
                        )
                );
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

        return cleaned.isEmpty()
                ? null
                : cleaned;
    }
}