package com.roottherapy.backend.messaging;

import com.roottherapy.backend.messaging.dto.ConversationResponse;
import com.roottherapy.backend.messaging.dto.MessageResponse;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessagingController.class)
@Import(SecurityConfig.class)
class MessagingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessagingService messagingService;

    @Test
    @DisplayName(
            "JUNIT-MSG-011: Valid start conversation request returns success"
    )
    void startConversation_validRequest_returnsOk() throws Exception {
        // Arrange
        User client = createUser(UserRole.CLIENT, "Test", "Client");
        User therapist = createUser(UserRole.THERAPIST, "Test", "Therapist");
        UUID conversationId = UUID.randomUUID();

        ConversationResponse response = new ConversationResponse(
                conversationId,
                client.getId(),
                client.getFirstName(),
                client.getLastName(),
                therapist.getId(),
                therapist.getFirstName(),
                therapist.getLastName(),
                ConversationStatus.ACTIVE,
                null,
                null
        );

        when(messagingService.startConversation(
                org.mockito.ArgumentMatchers.any(User.class),
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(response);

        String requestJson = """
        {
          "therapistUserId": "%s"
        }
        """.formatted(therapist.getId());

        // Act and assert
        mockMvc.perform(
                        post("/api/message/conversations")
                                .with(authentication(authFor(client)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(conversationId.toString()))
                .andExpect(jsonPath("$.clientUserId")
                        .value(client.getId().toString()))
                .andExpect(jsonPath("$.therapistUserId")
                        .value(therapist.getId().toString()))
                .andExpect(jsonPath("$.status")
                        .value("ACTIVE"));

        verify(messagingService).startConversation(
                argThat(user ->
                        user != null
                                && client.getId().equals(user.getId())
                ),
                argThat(request ->
                        request != null
                                && therapist.getId()
                                .equals(request.therapistUserId())
                )
        );
    }

    @Test
    @DisplayName(
            "JUNIT-MSG-003/JUNIT-MSG-004: Authenticated user can list own conversations"
    )
    void listConversations_authenticatedUser_returnsOk() throws Exception {
        // Arrange
        User client = createUser(UserRole.CLIENT, "Test", "Client");
        User therapist = createUser(UserRole.THERAPIST, "Test", "Therapist");
        UUID conversationId = UUID.randomUUID();

        ConversationResponse response = new ConversationResponse(
                conversationId,
                client.getId(),
                client.getFirstName(),
                client.getLastName(),
                therapist.getId(),
                therapist.getFirstName(),
                therapist.getLastName(),
                ConversationStatus.ACTIVE,
                null,
                null
        );

        when(messagingService.listMyConversations(
                org.mockito.ArgumentMatchers.any(User.class)
        )).thenReturn(List.of(response));

        // Act and assert
        mockMvc.perform(
                        get("/api/message/conversations")
                                .with(authentication(authFor(client)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id")
                        .value(conversationId.toString()))
                .andExpect(jsonPath("$[0].clientUserId")
                        .value(client.getId().toString()))
                .andExpect(jsonPath("$[0].therapistUserId")
                        .value(therapist.getId().toString()));

        verify(messagingService).listMyConversations(
                argThat(user ->
                        user != null
                                && client.getId().equals(user.getId())
                )
        );
    }

    @Test
    @DisplayName(
            "Authenticated user can list messages for an owned conversation"
    )
    void listMessages_authenticatedUser_returnsOk() throws Exception {
        // Arrange
        User client = createUser(UserRole.CLIENT, "Test", "Client");
        UUID conversationId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();

        MessageResponse response = new MessageResponse(
                messageId,
                conversationId,
                client.getId(),
                client.getFirstName(),
                client.getLastName(),
                "Hello for now",
                "PLAINTEXT_DEV",
                "dev-placeholder",
                null,
                null,
                null
        );

        when(messagingService.listMessages(
                org.mockito.ArgumentMatchers.any(User.class),
                eq(conversationId)
        )).thenReturn(List.of(response));

        // Act and assert
        mockMvc.perform(
                        get("/api/message/conversations/{conversationId}/messages",
                                conversationId)
                                .with(authentication(authFor(client)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id")
                        .value(messageId.toString()))
                .andExpect(jsonPath("$[0].conversationId")
                        .value(conversationId.toString()))
                .andExpect(jsonPath("$[0].ciphertext")
                        .value("Hello for now"))
                .andExpect(jsonPath("$[0].encryptionAlgorithm")
                        .value("PLAINTEXT_DEV"))
                .andExpect(jsonPath("$[0].iv")
                        .value("dev-placeholder"));

        verify(messagingService).listMessages(
                argThat(user ->
                        user != null
                                && client.getId().equals(user.getId())
                ),
                eq(conversationId)
        );
    }

    @Test
    @DisplayName(
            "JUNIT-MSG-011: Valid send message request returns success"
    )
    void sendMessage_validRequest_returnsOk() throws Exception {
        // Arrange
        User client = createUser(UserRole.CLIENT, "Test", "Client");
        UUID conversationId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();

        MessageResponse response = new MessageResponse(
                messageId,
                conversationId,
                client.getId(),
                client.getFirstName(),
                client.getLastName(),
                "Hello for now",
                "PLAINTEXT_DEV",
                "dev-placeholder",
                null,
                null,
                null
        );

        when(messagingService.sendMessage(
                org.mockito.ArgumentMatchers.any(User.class),
                eq(conversationId),
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(response);

        String requestJson = """
        {
          "ciphertext": "Hello for now",
          "encryptionAlgorithm": "PLAINTEXT_DEV",
          "iv": "dev-placeholder",
          "authTag": null
        }
        """;

        // Act and assert
        mockMvc.perform(
                        post("/api/message/conversations/{conversationId}/messages",
                                conversationId)
                                .with(authentication(authFor(client)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(messageId.toString()))
                .andExpect(jsonPath("$.conversationId")
                        .value(conversationId.toString()))
                .andExpect(jsonPath("$.senderUserId")
                        .value(client.getId().toString()))
                .andExpect(jsonPath("$.ciphertext")
                        .value("Hello for now"))
                .andExpect(jsonPath("$.encryptionAlgorithm")
                        .value("PLAINTEXT_DEV"))
                .andExpect(jsonPath("$.iv")
                        .value("dev-placeholder"));

        verify(messagingService).sendMessage(
                argThat(user ->
                        user != null
                                && client.getId().equals(user.getId())
                ),
                eq(conversationId),
                argThat(request ->
                        request != null
                                && "Hello for now"
                                .equals(request.ciphertext())
                                && "PLAINTEXT_DEV"
                                .equals(request.encryptionAlgorithm())
                                && "dev-placeholder".equals(request.iv())
                                && request.authTag() == null
                )
        );
    }

    @Test
    @DisplayName(
            "JUNIT-MSG-006: Blank message payload returns bad request"
    )
    void sendMessage_blankContent_returnsBadRequest() throws Exception {
        // Arrange
        User client = createUser(UserRole.CLIENT, "Test", "Client");
        UUID conversationId = UUID.randomUUID();

        String requestJson = """
        {
          "ciphertext": "   ",
          "encryptionAlgorithm": "",
          "iv": "",
          "authTag": null
        }
        """;

        // Act and assert
        mockMvc.perform(
                        post("/api/message/conversations/{conversationId}/messages",
                                conversationId)
                                .with(authentication(authFor(client)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(messagingService);
    }

    @Test
    @DisplayName(
            "JUNIT-MSG-012: Non-participant conversation access returns forbidden"
    )
    void getConversation_nonParticipant_returnsForbidden() throws Exception {
        // Arrange
        User client = createUser(UserRole.CLIENT, "Test", "Client");
        UUID conversationId = UUID.randomUUID();

        when(messagingService.listMessages(
                org.mockito.ArgumentMatchers.any(User.class),
                eq(conversationId)
        )).thenThrow(
                new AccessDeniedException(
                        "You don't have access to this conversation"
                )
        );

        // Act and assert
        mockMvc.perform(
                        get("/api/message/conversations/{conversationId}/messages",
                                conversationId)
                                .with(authentication(authFor(client)))
                )
                .andExpect(status().isForbidden());
    }

    private User createUser(
            UserRole role,
            String firstName,
            String lastName
    ) {
        User user = new User(
                UUID.randomUUID() + "@example.com",
                "encoded-password",
                firstName,
                lastName,
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
}
