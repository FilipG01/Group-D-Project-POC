package com.roottherapy.backend.auth;

import com.roottherapy.backend.security.SecurityConfig;
import com.roottherapy.backend.users.AccountStatus;
import com.roottherapy.backend.users.UserRole;
import com.roottherapy.backend.users.dto.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName(
            "JUNIT-AUTH-011: Valid registration request returns created response"
    )
    void register_validRequest_returnsCreated() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();

        UserResponse response = new UserResponse(
                userId,
                "client@example.com",
                "Test",
                "Client",
                "0871234567",
                UserRole.CLIENT,
                AccountStatus.ACTIVE
        );

        when(authService.registerClient(
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(response);

        String requestJson = """
        {
          "email": "client@example.com",
          "password": "password123",
          "firstName": "Test",
          "lastName": "Client",
          "phoneNumber": "0871234567"
        }
        """;

        // Act and assert
        mockMvc.perform(
                        post("/api/auth/register/client")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("client@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("Client"))
                .andExpect(jsonPath("$.phoneNumber").value("0871234567"))
                .andExpect(jsonPath("$.role").value("CLIENT"))
                .andExpect(jsonPath("$.accountStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        verify(authService).registerClient(
                argThat(request ->
                        request != null
                                && "client@example.com"
                                .equals(request.email())
                                && "password123"
                                .equals(request.password())
                                && "Test"
                                .equals(request.firstName())
                                && "Client"
                                .equals(request.lastName())
                                && "0871234567"
                                .equals(request.phoneNumber())
                )
        );
    }

    @Test
    @DisplayName(
            "JUNIT-AUTH-012: Invalid registration request returns bad request"
    )
    void register_invalidRequest_returnsBadRequest() throws Exception {
        // Arrange
        String requestJson = """
        {
          "email": "not-an-email",
          "password": "short",
          "firstName": "",
          "lastName": "Client",
          "phoneNumber": "0871234567"
        }
        """;

        // Act and assert
        mockMvc.perform(
                        post("/api/auth/register/client")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName(
            "JUNIT-AUTH-013: Valid login request returns current user"
    )
    void login_validRequest_returnsUser() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();

        UserResponse response = new UserResponse(
                userId,
                "client@example.com",
                "Test",
                "Client",
                null,
                UserRole.CLIENT,
                AccountStatus.ACTIVE
        );

        when(authService.login(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(response);

        String requestJson = """
        {
          "email": "client@example.com",
          "password": "password123"
        }
        """;

        // Act and assert
        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("client@example.com"))
                .andExpect(jsonPath("$.role").value("CLIENT"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        verify(authService).login(
                argThat(request ->
                        request != null
                                && "client@example.com"
                                .equals(request.email())
                                && "password123"
                                .equals(request.password())
                ),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        );
    }
}
