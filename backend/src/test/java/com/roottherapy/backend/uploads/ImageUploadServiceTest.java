package com.roottherapy.backend.uploads;

import com.roottherapy.backend.uploads.dto.ImageUploadResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ImageUploadServiceTest {

    @TempDir
    Path tempDirectory;

    @ParameterizedTest
    @MethodSource("supportedImages")
    void uploadImage_supportedImage_storesFileInCategoryAndReturnsPublicPath(
            String filename,
            String contentType
    ) throws IOException {
        // Arrange
        ImageUploadService service = new ImageUploadService(
                tempDirectory.toString()
        );

        byte[] imageBytes = "valid-image-content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                filename,
                contentType,
                imageBytes
        );

        // Act
        ImageUploadResponse response = service.uploadImage(
                file,
                "gallery"
        );

        // Assert
        assertNotNull(response);
        assertTrue(response.url().startsWith("/uploads/gallery/"));
        assertEquals(
                "/uploads/gallery/" + response.filename(),
                response.url()
        );

        Path storedFile = tempDirectory
                .resolve("gallery")
                .resolve(response.filename());

        assertTrue(Files.exists(storedFile));
        assertArrayEquals(imageBytes, Files.readAllBytes(storedFile));
    }

    private static Stream<Arguments> supportedImages() {
        return Stream.of(
                Arguments.of("photo.jpg", "image/jpeg"),
                Arguments.of("photo.jpeg", "image/jpeg"),
                Arguments.of("photo.png", "image/png"),
                Arguments.of("photo.webp", "image/webp")
        );
    }

    @Test
    void uploadImage_emptyFile_throwsBadRequest() {
        // Arrange
        ImageUploadService service = new ImageUploadService(
                tempDirectory.toString()
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.png",
                "image/png",
                new byte[0]
        );

        // Act
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.uploadImage(file, "gallery")
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("An image file is required", exception.getReason());
    }

    @ParameterizedTest
    @MethodSource("unsupportedFiles")
    void uploadImage_unsupportedTypeOrExtension_throwsBadRequest(
            String filename,
            String contentType
    ) {
        // Arrange
        ImageUploadService service = new ImageUploadService(
                tempDirectory.toString()
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                filename,
                contentType,
                "unsupported-content".getBytes()
        );

        // Act
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.uploadImage(file, "gallery")
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertFalse(Files.exists(tempDirectory.resolve("gallery")));
    }

    private static Stream<Arguments> unsupportedFiles() {
        return Stream.of(
                Arguments.of("document.pdf", "application/pdf"),
                Arguments.of("script.js", "text/javascript"),
                Arguments.of("archive.zip", "application/zip"),
                Arguments.of("fake.jpg", "application/pdf"),
                Arguments.of("fake.exe", "image/jpeg"),
                Arguments.of("no-extension", "image/png")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "../profile.png",
            "../../outside.png",
            "folder/profile.png",
            "folder\\profile.png",
            "profile.backup.final.png"
    })
    void uploadImage_dangerousRepeatedFilename_generatesSafeUniqueNames(
            String originalFilename
    ) {
        // Arrange
        ImageUploadService service = new ImageUploadService(
                tempDirectory.toString()
        );

        MockMultipartFile firstFile = new MockMultipartFile(
                "file",
                originalFilename,
                "image/png",
                "first".getBytes()
        );

        MockMultipartFile secondFile = new MockMultipartFile(
                "file",
                originalFilename,
                "image/png",
                "second".getBytes()
        );

        // Act
        ImageUploadResponse firstResponse =
                service.uploadImage(firstFile, "gallery");
        ImageUploadResponse secondResponse =
                service.uploadImage(secondFile, "gallery");

        // Assert
        assertNotEquals(
                firstResponse.filename(),
                secondResponse.filename()
        );

        Set<String> unsafeSequences = Set.of(
                "..",
                "/",
                "\\"
        );

        for (String unsafeSequence : unsafeSequences) {
            assertFalse(firstResponse.filename().contains(unsafeSequence));
            assertFalse(secondResponse.filename().contains(unsafeSequence));
        }

        Path categoryDirectory = tempDirectory.resolve("gallery");
        Path firstStoredFile = categoryDirectory
                .resolve(firstResponse.filename())
                .normalize();
        Path secondStoredFile = categoryDirectory
                .resolve(secondResponse.filename())
                .normalize();

        assertTrue(firstStoredFile.startsWith(categoryDirectory));
        assertTrue(secondStoredFile.startsWith(categoryDirectory));
        assertTrue(Files.exists(firstStoredFile));
        assertTrue(Files.exists(secondStoredFile));
    }

    @Test
    void uploadImage_fileSystemFailure_throwsInternalServerError() throws IOException {
        // Arrange
        Path regularFile = tempDirectory.resolve("upload-root");
        Files.writeString(regularFile, "This path is a file, not a directory.");

        ImageUploadService service = new ImageUploadService(
                regularFile.toString()
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.png",
                "image/png",
                "image-content".getBytes()
        );

        // Act
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.uploadImage(file, "gallery")
        );

        // Assert
        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getStatusCode()
        );
        assertEquals(
                "The image could not be saved",
                exception.getReason()
        );
        assertNotNull(exception.getCause());
    }
}
