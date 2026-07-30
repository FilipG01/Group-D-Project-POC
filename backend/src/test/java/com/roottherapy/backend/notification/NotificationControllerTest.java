package com.roottherapy.backend.notification;

import com.roottherapy.backend.notification.dto.NotificationResponse;
import com.roottherapy.backend.security.CustomUserDetails;
import com.roottherapy.backend.security.SecurityConfig;
import com.roottherapy.backend.users.AccountStatus;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRole;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@Import(SecurityConfig.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @Test
    void markRead_authenticatedOwner_returnsOk() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();
        Instant readAt = Instant.parse("2026-07-30T12:00:00Z");

        User user = new User(
                "client@example.com",
                "encoded-password",
                "Client",
                "User",
                UserRole.CLIENT
        );
        user.setId(userId);
        user.setAccountStatus(AccountStatus.ACTIVE);

        CustomUserDetails principal = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                );

        NotificationResponse response = new NotificationResponse(
                notificationId,
                NotificationType.BLOG_APPROVED,
                "Blog approved",
                "Your blog was approved.",
                "/therapist/blog/" + UUID.randomUUID() + "/edit",
                true,
                readAt,
                Instant.parse("2026-07-30T10:00:00Z")
        );

        when(notificationService.markRead(userId, notificationId))
                .thenReturn(response);

        // Act and assert
        mockMvc.perform(
                        patch("/api/notifications/{notificationId}/read", notificationId)
                                .with(authentication(authentication))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(notificationId.toString()))
                .andExpect(jsonPath("$.type").value("BLOG_APPROVED"))
                .andExpect(jsonPath("$.title").value("Blog approved"))
                .andExpect(jsonPath("$.read").value(true))
                .andExpect(jsonPath("$.readAt").value(readAt.toString()));

        verify(notificationService).markRead(userId, notificationId);
    }
}
