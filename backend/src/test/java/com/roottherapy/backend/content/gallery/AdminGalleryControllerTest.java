package com.roottherapy.backend.content.gallery;

import com.roottherapy.backend.security.SecurityConfig;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminGalleryController.class)
@Import(SecurityConfig.class)
class AdminGalleryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GalleryManagementService galleryService;

    // JUNIT-GAL-010
    @ParameterizedTest(name = "role {0} is forbidden")
    @ValueSource(strings = {"CLIENT", "THERAPIST"})
    void createGalleryImage_nonAdmin_returnsForbidden(String role)
            throws Exception {

        mockMvc.perform(post("/api/admin/gallery")
                        .with(SecurityMockMvcRequestPostProcessors
                                .user("user@example.com").roles(role))
                        .contentType("application/json")
                        .content("""
                                {
                                  "imageUrl": "/uploads/gallery/therapy-room.jpg",
                                  "caption": "A calm therapy room",
                                  "altText": "A comfortable therapy room with two chairs",
                                  "displayOrder": 0,
                                  "visible": true
                                }
                                """))
                .andExpect(status().isForbidden());

        verifyNoInteractions(galleryService);
    }
}
