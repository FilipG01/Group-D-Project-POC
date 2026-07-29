package com.roottherapy.backend.auth;
import com.roottherapy.backend.auth.dto.ChangePasswordRequest;
import com.roottherapy.backend.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.roottherapy.backend.security.CustomUserDetails;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;


@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthPasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;


//JUNIT-PASS-006: Valid password change request returns successful response
    @Test
void changePassword_validRequest_returnsNoContent() throws Exception {

    User user = new User(
            "test@test.com",
            "oldEncodedPassword",
            "Test",
            "User",
            UserRole.CLIENT
    );

    CustomUserDetails userDetails =
            new CustomUserDetails(user);

    UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

    SecurityContextHolder.getContext()
            .setAuthentication(authentication);


    String requestJson = """
        {
          "currentPassword": "oldPassword123",
          "newPassword": "newPassword123",
          "confirmNewPassword": "newPassword123"
        }
        """;


    mockMvc.perform(
            put("/api/auth/password")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
    )
    .andExpect(status().isNoContent());


    verify(authService)
            .changePassword(
                    user,
                    new ChangePasswordRequest(
                            "oldPassword123",
                            "newPassword123",
                            "newPassword123"
                    )
            );
}

//JUNIT-PASS-007: Unauthenticated password change request is rejected by security
@Test
void changePassword_unauthenticated_returnsForbidden() throws Exception {

    String requestJson = """
        {
          "currentPassword": "oldPassword123",
          "newPassword": "newPassword123",
          "confirmNewPassword": "newPassword123"
        }
        """;


    mockMvc.perform(
        put("/api/auth/password")
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
)
    .andExpect(status().isForbidden());


    verifyNoInteractions(authService);
}
}