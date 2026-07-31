package com.roottherapy.backend.security;

import com.roottherapy.backend.content.services.AdminServiceOfferingController;
import com.roottherapy.backend.content.services.ServiceOfferingManagementService;
import com.roottherapy.backend.profile.therapist.TherapistProfileController;
import com.roottherapy.backend.profile.therapist.TherapistProfileManagementService;
import com.roottherapy.backend.profile.therapist.dto.AdminTherapistProfileResponse;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        AdminServiceOfferingController.class,
        TherapistProfileController.class
})
@Import({
        SecurityConfig.class,
        RolePermissionControllerTest.AccessDeniedTestController.class
})
class RolePermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServiceOfferingManagementService serviceOfferingService;

    @MockitoBean
    private TherapistProfileManagementService therapistProfileManagementService;

    @Test
    @DisplayName("JUNIT-SEC-001: Admin user can access admin service endpoint")
    void adminEndpoint_adminUser_isAllowed() throws Exception {
        User admin = createUser(UserRole.ADMIN);

        when(serviceOfferingService.getAllServicesForAdmin())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/admin/services")
                        .with(authentication(authFor(admin))))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(serviceOfferingService).getAllServicesForAdmin();
    }

    @ParameterizedTest
    @EnumSource(
            value = UserRole.class,
            names = {"CLIENT", "THERAPIST"}
    )
    @DisplayName("JUNIT-SEC-002: Non-admin users cannot access admin service endpoint")
    void adminEndpoint_clientOrTherapist_isForbidden(UserRole role)
            throws Exception {
        User user = createUser(role);

        mockMvc.perform(get("/api/admin/services")
                        .with(authentication(authFor(user))))
                .andExpect(status().isForbidden());

        verifyNoInteractions(serviceOfferingService);
    }

    @Test
    @DisplayName("JUNIT-SEC-003: Unauthenticated user cannot access admin service endpoint")
    void adminEndpoint_unauthenticated_isBlocked() throws Exception {
        mockMvc.perform(get("/api/admin/services"))
                .andExpect(status().is4xxClientError());

        verifyNoInteractions(serviceOfferingService);
    }

    @Test
    @DisplayName("JUNIT-SEC-004: Therapist user can access therapist profile endpoint")
    void therapistEndpoint_therapistUser_isAllowed() throws Exception {
        User therapist = createUser(UserRole.THERAPIST);

        AdminTherapistProfileResponse response =
                new AdminTherapistProfileResponse(
                        therapist.getId(),
                        therapist.getFirstName(),
                        therapist.getLastName(),
                        therapist.getEmail(),
                        "MSc Counselling",
                        "REG-123",
                        5,
                        "Therapist bio",
                        true,
                        null,
                        List.of("I support clients with anxiety."),
                        List.of("English"),
                        List.of("Anxiety"),
                        0,
                        true,
                        null,
                        null
                );

        when(therapistProfileManagementService.getOwnProfile(
                therapist.getId()
        )).thenReturn(response);

        mockMvc.perform(get("/api/therapist-profile/me")
                        .with(authentication(authFor(therapist))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(
                        therapist.getId().toString()
                ));

        verify(therapistProfileManagementService).getOwnProfile(
                therapist.getId()
        );
    }

    @Test
    @DisplayName("JUNIT-SEC-005: Client user cannot access therapist profile endpoint")
    void therapistEndpoint_clientUser_isForbidden() throws Exception {
        User client = createUser(UserRole.CLIENT);

        mockMvc.perform(get("/api/therapist-profile/me")
                        .with(authentication(authFor(client))))
                .andExpect(status().isForbidden());

        verifyNoInteractions(therapistProfileManagementService);
    }

    @Test
    @DisplayName("JUNIT-SEC-010: AccessDeniedException returns forbidden response")
    void accessDeniedException_returnsForbiddenResponse() throws Exception {
        User client = createUser(UserRole.CLIENT);

        mockMvc.perform(get("/security-test/access-denied")
                        .with(authentication(authFor(client))))
                .andExpect(status().isForbidden());
    }

    private User createUser(UserRole role) {
        User user = new User(
                role.name().toLowerCase() + "-" + UUID.randomUUID()
                        + "@example.com",
                "encoded-password",
                "Test",
                role.name(),
                role
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

    @RestController
    @RequestMapping("/security-test")
    public static class AccessDeniedTestController {

        @GetMapping("/access-denied")
        void accessDenied() {
            throw new AccessDeniedException("Forbidden");
        }
    }
}
