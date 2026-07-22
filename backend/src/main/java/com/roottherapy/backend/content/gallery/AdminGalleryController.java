package com.roottherapy.backend.content.gallery;

import com.roottherapy.backend.content.gallery.dto.CreateGalleryImageRequest;
import com.roottherapy.backend.content.gallery.dto.GalleryArchiveRequest;
import com.roottherapy.backend.content.gallery.dto.GalleryImageResponse;
import com.roottherapy.backend.content.gallery.dto.GalleryVisibilityRequest;
import com.roottherapy.backend.content.gallery.dto.ReorderGalleryImagesRequest;
import com.roottherapy.backend.content.gallery.dto.UpdateGalleryImageRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/gallery")
public class AdminGalleryController {

    private final GalleryManagementService galleryService;

    public AdminGalleryController(
            GalleryManagementService galleryService
    ) {
        this.galleryService = galleryService;
    }

    @GetMapping
    public List<GalleryImageResponse> getAllGalleryImages() {
        return galleryService.getAllGalleryImagesForAdmin();
    }

    @GetMapping("/{id}")
    public GalleryImageResponse getGalleryImageById(
            @PathVariable Long id
    ) {
        return galleryService
                .getGalleryImageByIdForAdmin(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GalleryImageResponse createGalleryImage(
            @Valid
            @RequestBody
            CreateGalleryImageRequest request
    ) {
        return galleryService.createGalleryImage(request);
    }

    @PutMapping("/{id}")
    public GalleryImageResponse updateGalleryImage(
            @PathVariable Long id,
            @Valid
            @RequestBody
            UpdateGalleryImageRequest request
    ) {
        return galleryService.updateGalleryImage(
                id,
                request
        );
    }

    @PatchMapping("/{id}/visibility")
    public GalleryImageResponse setVisible(
            @PathVariable Long id,
            @Valid
            @RequestBody
            GalleryVisibilityRequest request
    ) {
        return galleryService.setVisible(
                id,
                request.visible()
        );
    }

    @PatchMapping("/{id}/archive")
    public GalleryImageResponse setArchived(
            @PathVariable Long id,
            @Valid
            @RequestBody
            GalleryArchiveRequest request
    ) {
        return galleryService.setArchived(
                id,
                request.archived()
        );
    }

    @PatchMapping("/reorder")
    public List<GalleryImageResponse> reorderGalleryImages(
            @Valid
            @RequestBody
            ReorderGalleryImagesRequest request
    ) {
        return galleryService.reorderGalleryImages(request);
    }
}