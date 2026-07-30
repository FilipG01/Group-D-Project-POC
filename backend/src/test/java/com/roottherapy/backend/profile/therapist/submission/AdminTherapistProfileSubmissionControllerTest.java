package com.roottherapy.backend.profile.therapist.submission;

import com.roottherapy.backend.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminTherapistProfileSubmissionController.class)
@Import(SecurityConfig.class)
class AdminTherapistProfileSubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TherapistProfileSubmissionService service;

    // JUNIT-PROFILE-012
    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void getAdminSubmissions_admin_returnsOk() throws Exception {
        when(service.getAdminSubmissions(null)).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/therapist-profile-submissions"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // JUNIT-PROFILE-013
    @Test
    @WithMockUser(username = "therapist@example.com", roles = "THERAPIST")
    void approveSubmission_nonAdmin_returnsForbidden() throws Exception {
        UUID submissionId = UUID.randomUUID();

        mockMvc.perform(post(
                        "/api/admin/therapist-profile-submissions/{submissionId}/approve",
                        submissionId
                )
                        .contentType("application/json")
                        .content("""
                                {
                                  "version": 1,
                                  "note": "Approved"
                                }
                                """))
                .andExpect(status().isForbidden());

        verifyNoInteractions(service);
    }
}
