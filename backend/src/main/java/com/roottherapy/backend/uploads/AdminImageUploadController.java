package com.roottherapy.backend.uploads;

import com.roottherapy.backend.uploads.dto.ImageUploadResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/uploads")
public class AdminImageUploadController {

    private final ImageUploadService imageUploadService;

    public AdminImageUploadController(
            ImageUploadService imageUploadService
    ) {
        this.imageUploadService = imageUploadService;
    }

    @PostMapping("/{category}")
    @ResponseStatus(HttpStatus.CREATED)
    public ImageUploadResponse uploadImage(
            @PathVariable String category,
            @RequestParam("file") MultipartFile file
    ) {
        return imageUploadService.uploadImage(
                file,
                category
        );
    }
}