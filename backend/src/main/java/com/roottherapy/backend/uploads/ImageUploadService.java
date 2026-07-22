package com.roottherapy.backend.uploads;

import com.roottherapy.backend.uploads.dto.ImageUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageUploadService {

    private static final Set<String> ALLOWED_CATEGORIES = Set.of(
            "services",
            "therapists",
            "blog",
            "gallery"
    );

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg",
            "jpeg",
            "png",
            "webp"
    );

    private final Path rootUploadDirectory;

    public ImageUploadService(
            @Value("${app.uploads.directory:uploads}")
            String uploadDirectory
    ) {
        this.rootUploadDirectory = Paths
                .get(uploadDirectory)
                .toAbsolutePath()
                .normalize();
    }

    public ImageUploadResponse uploadImage(
            MultipartFile file,
            String category
    ) {
        validateCategory(category);
        validateFile(file);

        String extension = getExtension(file.getOriginalFilename());
        String generatedFilename =
                UUID.randomUUID() + "." + extension;

        Path categoryDirectory = rootUploadDirectory
                .resolve(category)
                .normalize();

        Path destination = categoryDirectory
                .resolve(generatedFilename)
                .normalize();

        /*
         * Prevent attempts to escape the intended upload directory.
         */
        if (!destination.startsWith(categoryDirectory)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid upload path"
            );
        }

        try {
            Files.createDirectories(categoryDirectory);

            Files.copy(
                    file.getInputStream(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "The image could not be saved",
                    exception
            );
        }

        String publicUrl =
                "/uploads/" + category + "/" + generatedFilename;

        return new ImageUploadResponse(
                publicUrl,
                generatedFilename
        );
    }

    private void validateCategory(String category) {
        if (category == null ||
                !ALLOWED_CATEGORIES.contains(category)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid upload category"
            );
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "An image file is required"
            );
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only JPG, PNG and WebP images are allowed"
            );
        }

        String extension = getExtension(file.getOriginalFilename());

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unsupported image extension"
            );
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The image must have a file extension"
            );
        }

        return filename
                .substring(filename.lastIndexOf('.') + 1)
                .toLowerCase(Locale.ROOT);
    }
}