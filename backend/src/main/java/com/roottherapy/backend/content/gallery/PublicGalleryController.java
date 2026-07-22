package com.roottherapy.backend.content.gallery;

import com.roottherapy.backend.content.gallery.dto.GalleryImageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/gallery")
public class PublicGalleryController {

    private final GalleryManagementService galleryService;

    public PublicGalleryController(
            GalleryManagementService galleryService
    ) {
        this.galleryService = galleryService;
    }

    @GetMapping
    public List<GalleryImageResponse> getVisibleGalleryImages() {
        return galleryService.getVisibleGalleryImages();
    }
}