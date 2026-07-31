package com.roottherapy.backend.profile.therapist;

import com.roottherapy.backend.profile.therapist.dto.AdminTherapistProfileResponse;
import com.roottherapy.backend.profile.therapist.dto.CreateTherapistRequest;
import com.roottherapy.backend.security.SecurityConfig;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminTherapistProfileController.class)
@Import(SecurityConfig.class)
class AdminTherapistProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /*
     * Replaces the real management service with a Mockito mock.
     * This keeps the test focused on the controller layer.
     */
    @MockitoBean
    private TherapistProfileManagementService managementService;

    @Test
    @WithMockUser(
            username = "admin@roottherapy.ie",
            roles = "ADMIN"
    )
    void createTherapist_validAdminRequest_returnsCreated()
            throws Exception {

        /*
         * The controller only needs a response from the mocked
         * service. The service behavior itself was tested earlier.
         */
        AdminTherapistProfileResponse response =
                mock(AdminTherapistProfileResponse.class);

        when(managementService.createTherapist(
                org.mockito.ArgumentMatchers.any(
                        CreateTherapistRequest.class
                )
        )).thenReturn(response);

        /*
         * Send a valid therapist-creation request as an
         * authenticated administrator.
         */
        String requestJson = """
        {
          "firstName": "Anna",
          "lastName": "Murphy",
          "email": "anna.murphy@example.com",
          "temporaryPassword": "SecurePass123!",
          "phoneNumber": "0871234567",
          "qualifications": "MSc Counselling and Psychotherapy",
          "registrationNumber": "IACP-9001",
          "yearsExperience": 8
        }
        """;

        mockMvc.perform(
                        post("/api/admin/therapists")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isCreated());

        /*
         * Verify that the JSON body was converted into the correct
         * request DTO and passed to the management service.
         */
        verify(managementService).createTherapist(
                argThat(request ->
                        request != null
                                && "Anna".equals(request.firstName())
                                && "Murphy".equals(request.lastName())
                                && "anna.murphy@example.com"
                                .equals(request.email())
                                && "SecurePass123!"
                                .equals(request.temporaryPassword())
                                && "0871234567"
                                .equals(request.phoneNumber())
                                && "MSc Counselling and Psychotherapy"
                                .equals(request.qualifications())
                                && "IACP-9001"
                                .equals(request.registrationNumber())
                                && Integer.valueOf(8)
                                .equals(request.yearsExperience())
                )
        );
    }

    @Test
    @WithMockUser(
            username = "therapist@roottherapy.ie",
            roles = "THERAPIST"
    )
    void createTherapist_nonAdmin_returnsForbidden() throws Exception {

        String requestJson = """
            {
              "firstName": "Anna",
              "lastName": "Murphy",
              "email": "anna.murphy@example.com",
              "temporaryPassword": "SecurePass123!",
              "phoneNumber": "0871234567",
              "qualifications": "MSc Counselling and Psychotherapy",
              "registrationNumber": "IACP-9001",
              "yearsExperience": 8
            }
            """;

        mockMvc.perform(
                        post("/api/admin/therapists")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isForbidden());

        verifyNoInteractions(managementService);
    }
}