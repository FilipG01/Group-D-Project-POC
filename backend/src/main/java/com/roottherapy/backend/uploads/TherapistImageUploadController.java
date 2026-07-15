package com.roottherapy.backend.uploads;

import com.roottherapy.backend.uploads.dto.ImageUploadResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/therapist/uploads")
public class TherapistImageUploadController {

    private final ImageUploadService imageUploadService;

    public TherapistImageUploadController(
            ImageUploadService imageUploadService
    ) {
        this.imageUploadService = imageUploadService;
    }

    @PostMapping("/profile-image")
    @ResponseStatus(HttpStatus.CREATED)
    public ImageUploadResponse uploadProfileImage(
            @RequestParam("file") MultipartFile file
    ) {
        return imageUploadService.uploadImage(
                file,
                "therapists"
        );
    }
}