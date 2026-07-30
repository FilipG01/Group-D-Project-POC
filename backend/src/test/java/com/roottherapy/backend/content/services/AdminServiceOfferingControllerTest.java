package com.roottherapy.backend.content.services;

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

@WebMvcTest(AdminServiceOfferingController.class)
@Import(SecurityConfig.class)
class AdminServiceOfferingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServiceOfferingManagementService service;

    // JUNIT-SVC-012
    @ParameterizedTest(name = "role {0} is forbidden")
    @ValueSource(strings = {"CLIENT", "THERAPIST"})
    void createService_nonAdmin_returnsForbidden(String role) throws Exception {
        mockMvc.perform(post("/api/admin/services")
                        .with(SecurityMockMvcRequestPostProcessors.user("user@example.com").roles(role))
                        .contentType("application/json")
                        .content("""
                                {
                                  "title": "Individual Therapy",
                                  "slug": "individual-therapy",
                                  "category": "Therapy",
                                  "shortDescription": "One-to-one therapeutic support",
                                  "fullDescription": ["Full description"],
                                  "points": ["Anxiety support"],
                                  "imageUrl": null,
                                  "displayOrder": 0,
                                  "published": false,
                                  "metaTitle": null,
                                  "metaDescription": null,
                                  "keywords": []
                                }
                                """))
                .andExpect(status().isForbidden());

        verifyNoInteractions(service);
    }
}
