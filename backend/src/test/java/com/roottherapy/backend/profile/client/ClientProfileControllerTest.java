package com.roottherapy.backend.profile.client;

import com.roottherapy.backend.profile.client.dto.ClientProfileResponse;
import com.roottherapy.backend.security.CustomUserDetails;
import com.roottherapy.backend.security.SecurityConfig;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientProfileController.class)
@Import(SecurityConfig.class)
class ClientProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientProfileService clientProfileService;

    @Test
    @DisplayName(
            "JUNIT-CLIENT-006: Authenticated client can retrieve profile"
    )
    void getProfile_authenticatedClient_returnsOk() throws Exception {
        // Arrange
        User client = createClientUser();

        ClientProfileResponse response =
                new ClientProfileResponse(
                        client.getId(),
                        LocalDate.of(1995, 4, 12),
                        "Reduce anxiety.",
                        PreferredContactMethod.IN_APP,
                        null,
                        null
                );

        when(clientProfileService.getMyProfile(
                org.mockito.ArgumentMatchers.any(User.class)
        )).thenReturn(response);

        // Act and assert
        mockMvc.perform(
                        get("/api/client-profile/me")
                                .with(authentication(authFor(client)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId")
                        .value(client.getId().toString()))
                .andExpect(jsonPath("$.dateOfBirth")
                        .value("1995-04-12"))
                .andExpect(jsonPath("$.therapyGoalsSummary")
                        .value("Reduce anxiety."))
                .andExpect(jsonPath("$.preferredContactMethod")
                        .value("IN_APP"));

        verify(clientProfileService).getMyProfile(
                argThat(user ->
                        user != null
                                && client.getId().equals(user.getId())
                )
        );
    }

    @Test
    @DisplayName(
            "JUNIT-CLIENT-007: Valid profile update returns updated response"
    )
    void updateProfile_validRequest_returnsOk() throws Exception {
        // Arrange
        User client = createClientUser();

        ClientProfileResponse response =
                new ClientProfileResponse(
                        client.getId(),
                        LocalDate.of(1990, 1, 15),
                        "I want to manage stress better.",
                        PreferredContactMethod.EMAIL,
                        null,
                        null
                );

        when(clientProfileService.updateMyProfile(
                org.mockito.ArgumentMatchers.any(User.class),
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(response);

        String requestJson = """
        {
          "dateOfBirth": "1990-01-15",
          "therapyGoalsSummary": "I want to manage stress better.",
          "preferredContactMethod": "EMAIL"
        }
        """;

        // Act and assert
        mockMvc.perform(
                        put("/api/client-profile/me")
                                .with(authentication(authFor(client)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId")
                        .value(client.getId().toString()))
                .andExpect(jsonPath("$.dateOfBirth")
                        .value("1990-01-15"))
                .andExpect(jsonPath("$.therapyGoalsSummary")
                        .value("I want to manage stress better."))
                .andExpect(jsonPath("$.preferredContactMethod")
                        .value("EMAIL"));

        verify(clientProfileService).updateMyProfile(
                argThat(user ->
                        user != null
                                && client.getId().equals(user.getId())
                ),
                argThat(request ->
                        request != null
                                && LocalDate.of(1990, 1, 15)
                                .equals(request.dateOfBirth())
                                && "I want to manage stress better."
                                .equals(request.therapyGoalsSummary())
                                && PreferredContactMethod.EMAIL
                                .equals(request.preferredContactMethod())
                )
        );
    }

    @Test
    @DisplayName(
            "JUNIT-CLIENT-008: Invalid profile update returns bad request"
    )
    void updateProfile_invalidRequest_returnsBadRequest() throws Exception {
        // Arrange
        User client = createClientUser();

        String requestJson = """
        {
          "dateOfBirth": "2999-01-15",
          "therapyGoalsSummary": "%s",
          "preferredContactMethod": "EMAIL"
        }
        """.formatted("x".repeat(2001));

        // Act and assert
        mockMvc.perform(
                        put("/api/client-profile/me")
                                .with(authentication(authFor(client)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(clientProfileService);
    }

    private User createClientUser() {
        User user = new User(
                "client@example.com",
                "encoded-password",
                "Test",
                "Client",
                UserRole.CLIENT
        );
        user.setId(UUID.randomUUID());
        return user;
    }

    private Authentication authFor(User user) {
        CustomUserDetails userDetails = new CustomUserDetails(user);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}
