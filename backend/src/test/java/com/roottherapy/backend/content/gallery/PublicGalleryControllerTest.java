package com.roottherapy.backend.content.gallery;

import com.roottherapy.backend.content.gallery.dto.GalleryImageResponse;

import com.roottherapy.backend.security.SecurityConfig;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicGalleryController.class)
@Import(SecurityConfig.class)
class PublicGalleryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GalleryManagementService galleryService;

    // JUNIT-GAL-011
    @Test
    void getGallery_returnsVisibleOrderedImages() throws Exception {
        GalleryImageResponse first = new GalleryImageResponse(
                2L,
                "/uploads/gallery/first.jpg",
                "First image",
                "First gallery image",
                1,
                true,
                false,
                null,
                null
        );

        GalleryImageResponse second = new GalleryImageResponse(
                1L,
                "/uploads/gallery/second.jpg",
                "Second image",
                "Second gallery image",
                2,
                true,
                false,
                null,
                null
        );

        when(galleryService.getVisibleGalleryImages())
                .thenReturn(List.of(first, second));

        mockMvc.perform(get("/api/gallery"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].displayOrder").value(1))
                .andExpect(jsonPath("$[0].visible").value(true))
                .andExpect(jsonPath("$[0].archived").value(false))
                .andExpect(jsonPath("$[1].id").value(1))
                .andExpect(jsonPath("$[1].displayOrder").value(2));

        verify(galleryService).getVisibleGalleryImages();
    }
}
