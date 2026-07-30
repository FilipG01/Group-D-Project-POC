package com.roottherapy.backend.uploads;

import com.roottherapy.backend.security.SecurityConfig;
import com.roottherapy.backend.uploads.dto.ImageUploadResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminImageUploadController.class)
@Import(SecurityConfig.class)
class AdminImageUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageUploadService imageUploadService;

    @Test
    void uploadImage_validAdminImage_returnsCreatedAndPath() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "gallery.png",
                "image/png",
                "image-content".getBytes()
        );

        ImageUploadResponse response = new ImageUploadResponse(
                "/uploads/gallery/generated-file.png",
                "generated-file.png"
        );

        when(imageUploadService.uploadImage(any(), eq("gallery")))
                .thenReturn(response);

        // Act and assert
        mockMvc.perform(
                        multipart("/api/admin/uploads/{category}", "gallery")
                                .file(file)
                                .with(user("admin@example.com").roles("ADMIN"))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.url")
                        .value("/uploads/gallery/generated-file.png"))
                .andExpect(jsonPath("$.filename")
                        .value("generated-file.png"));

        verify(imageUploadService).uploadImage(any(), eq("gallery"));
    }

    @Test
    void uploadImage_invalidCategory_returnsBadRequest() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.png",
                "image/png",
                "image-content".getBytes()
        );

        when(imageUploadService.uploadImage(any(), eq("documents")))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid upload category"
                ));

        // Act and assert
        mockMvc.perform(
                        multipart("/api/admin/uploads/{category}", "documents")
                                .file(file)
                                .with(user("admin@example.com").roles("ADMIN"))
                )
                .andExpect(status().isBadRequest());

        verify(imageUploadService).uploadImage(any(), eq("documents"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"CLIENT", "THERAPIST"})
    void uploadImage_nonAdminRole_returnsForbidden(String role)
            throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.png",
                "image/png",
                "image-content".getBytes()
        );

        SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor user =
                user(role.toLowerCase() + "@example.com").roles(role);

        // Act and assert
        mockMvc.perform(
                        multipart("/api/admin/uploads/{category}", "gallery")
                                .file(file)
                                .with(user)
                )
                .andExpect(status().isForbidden());

        verifyNoInteractions(imageUploadService);
    }
}
