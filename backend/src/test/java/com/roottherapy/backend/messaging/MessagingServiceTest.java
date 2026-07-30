package com.roottherapy.backend.messaging;

import com.roottherapy.backend.messaging.dto.ConversationResponse;
import com.roottherapy.backend.messaging.dto.MessageResponse;
import com.roottherapy.backend.messaging.dto.SendMessageRequest;
import com.roottherapy.backend.messaging.dto.StartConversationRequest;
import com.roottherapy.backend.profile.therapist.TherapistProfile;
import com.roottherapy.backend.profile.therapist.TherapistProfileRepository;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRepository;
import com.roottherapy.backend.users.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessagingServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TherapistProfileRepository therapistProfileRepository;

    private MessagingService messagingService;

    @BeforeEach
    void setUp() {
        messagingService = new MessagingService(
                conversationRepository,
                messageRepository,
                userRepository,
                therapistProfileRepository
        );
    }

    @Test
    @DisplayName(
            "JUNIT-MSG-001: Valid client and therapist creates conversation"
    )
    void startConversation_validClientAndTherapist_createsConversation() {
        // Arrange
        User client = createUser(UserRole.CLIENT, "Test", "Client");
        User therapist = createUser(UserRole.THERAPIST, "Test", "Therapist");
        StartConversationRequest request =
                new StartConversationRequest(therapist.getId());

        UUID conversationId = UUID.randomUUID();

        when(userRepository.findById(therapist.getId()))
                .thenReturn(Optional.of(therapist));

        when(therapistProfileRepository
                .findByUserIdAndAcceptingClientsTrue(therapist.getId()))
                .thenReturn(Optional.of(createTherapistProfile(therapist)));

        when(conversationRepository.findFirstByClientIdAndStatus(
                client.getId(),
                ConversationStatus.ACTIVE
        )).thenReturn(Optional.empty());

        when(conversationRepository.save(any(Conversation.class)))
                .thenAnswer(invocation -> {
                    Conversation conversation = invocation.getArgument(0);
                    conversation.setId(conversationId);
                    return conversation;
                });

        // Act
        ConversationResponse result =
                messagingService.startConversation(client, request);

        // Assert
        ArgumentCaptor<Conversation> conversationCaptor =
                ArgumentCaptor.forClass(Conversation.class);

        verify(conversationRepository).save(conversationCaptor.capture());

        Conversation savedConversation = conversationCaptor.getValue();

        assertEquals(client.getId(), savedConversation.getClient().getId());
        assertEquals(
                therapist.getId(),
                savedConversation.getTherapist().getId()
        );
        assertEquals(ConversationStatus.ACTIVE, savedConversation.getStatus());

        assertEquals(conversationId, result.id());
        assertEquals(client.getId(), result.clientUserId());
        assertEquals(therapist.getId(), result.therapistUserId());
        assertEquals(ConversationStatus.ACTIVE, result.status());
    }

    @Test
    @DisplayName(
            "JUNIT-MSG-002: Existing conversation with same therapist is returned"
    )
    void startConversation_existingConversationWithSameTherapist_returnsExistingConversation() {
        // Arrange
        User client = createUser(UserRole.CLIENT, "Test", "Client");
        User therapist = createUser(UserRole.THERAPIST, "Test", "Therapist");
        StartConversationRequest request =
                new StartConversationRequest(therapist.getId());

        Conversation existingConversation =
                createConversation(client, therapist, ConversationStatus.ACTIVE);

        when(userRepository.findById(therapist.getId()))
                .thenReturn(Optional.of(therapist));

        when(therapistProfileRepository
                .findByUserIdAndAcceptingClientsTrue(therapist.getId()))
                .thenReturn(Optional.of(createTherapistProfile(therapist)));

        when(conversationRepository.findFirstByClientIdAndStatus(
                client.getId(),
                ConversationStatus.ACTIVE
        )).thenReturn(Optional.of(existingConversation));

        // Act
        ConversationResponse result =
                messagingService.startConversation(client, request);

        // Assert
        assertEquals(existingConversation.getId(), result.id());
        assertEquals(client.getId(), result.clientUserId());
        assertEquals(therapist.getId(), result.therapistUserId());

        verify(conversationRepository, never())
                .save(any(Conversation.class));
    }

    @Test
    @DisplayName(
            "JUNIT-MSG-002: Existing conversation with different therapist is rejected"
    )
    void startConversation_existingConversationWithDifferentTherapist_throwsIllegalArgumentException() {
        // Arrange
        User client = createUser(UserRole.CLIENT, "Test", "Client");
        User existingTherapist =
                createUser(UserRole.THERAPIST, "Existing", "Therapist");
        User requestedTherapist =
                createUser(UserRole.THERAPIST, "Requested", "Therapist");

        StartConversationRequest request =
                new StartConversationRequest(requestedTherapist.getId());

        Conversation existingConversation =
                createConversation(
                        client,
                        existingTherapist,
                        ConversationStatus.ACTIVE
                );

        when(userRepository.findById(requestedTherapist.getId()))
                .thenReturn(Optional.of(requestedTherapist));

        when(therapistProfileRepository
                .findByUserIdAndAcceptingClientsTrue(
                        requestedTherapist.getId()
                ))
                .thenReturn(Optional.of(
                        createTherapistProfile(requestedTherapist)
                ));

        when(conversationRepository.findFirstByClientIdAndStatus(
                client.getId(),
                ConversationStatus.ACTIVE
        )).thenReturn(Optional.of(existingConversation));

        // Act and assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> messagingService.startConversation(client, request)
        );

        assertEquals(
                "you already have an active conversation with a therapist",
                exception.getMessage()
        );

        verify(conversationRepository, never())
                .save(any(Conversation.class));
    }

    @Test
    @DisplayName(
            "JUNIT-MSG-003: Client conversation list returns current user's conversations"
    )
    void getClientConversations_returnsOnlyClientConversations() {
        // Arrange
        User client = createUser(UserRole.CLIENT, "Test", "Client");
        User therapistOne =
                createUser(UserRole.THERAPIST, "First", "Therapist");
        User therapistTwo =
                createUser(UserRole.THERAPIST, "Second", "Therapist");

        List<Conversation> conversations = List.of(
                createConversation(client, therapistOne, ConversationStatus.ACTIVE),
                createConversation(client, therapistTwo, ConversationStatus.ACTIVE)
        );

        when(conversationRepository.findByClientIdOrTherapistId(
                client.getId(),
                client.getId()
        )).thenReturn(conversations);

        // Act
        List<ConversationResponse> result =
                messagingService.listMyConversations(client);

        // Assert
        assertEquals(2, result.size());
        assertEquals(client.getId(), result.getFirst().clientUserId());

        verify(conversationRepository).findByClientIdOrTherapistId(
                client.getId(),
                client.getId()
        );
    }

    @Test
    @DisplayName(
            "JUNIT-MSG-004: Therapist conversation list returns current user's conversations"
    )
    void getTherapistConversations_returnsOnlyTherapistConversations() {
        // Arrange
        User therapist = createUser(UserRole.THERAPIST, "Test", "Therapist");
        User clientOne = createUser(UserRole.CLIENT, "First", "Client");
        User clientTwo = createUser(UserRole.CLIENT, "Second", "Client");

        List<Conversation> conversations = List.of(
                createConversation(clientOne, therapist, ConversationStatus.ACTIVE),
                createConversation(clientTwo, therapist, ConversationStatus.ACTIVE)
        );

        when(conversationRepository.findByClientIdOrTherapistId(
                therapist.getId(),
                therapist.getId()
        )).thenReturn(conversations);

        // Act
        List<ConversationResponse> result =
                messagingService.listMyConversations(therapist);

        // Assert
        assertEquals(2, result.size());
        assertEquals(therapist.getId(), result.getFirst().therapistUserId());

        verify(conversationRepository).findByClientIdOrTherapistId(
                therapist.getId(),
                therapist.getId()
        );
    }

    @Test
    @DisplayName(
            "JUNIT-MSG-005: Valid participant message is saved"
    )
    void sendMessage_validParticipant_savesMessage() {
        // Arrange
        User client = createUser(UserRole.CLIENT, "Test", "Client");
        User therapist = createUser(UserRole.THERAPIST, "Test", "Therapist");
        Conversation conversation =
                createConversation(client, therapist, ConversationStatus.ACTIVE);

        SendMessageRequest request = new SendMessageRequest(
                "Hello for now",
                "PLAINTEXT_DEV",
                "dev-placeholder",
                null
        );

        UUID messageId = UUID.randomUUID();

        when(conversationRepository.findById(conversation.getId()))
                .thenReturn(Optional.of(conversation));

        when(messageRepository.save(any(Message.class)))
                .thenAnswer(invocation -> {
                    Message message = invocation.getArgument(0);
                    message.setId(messageId);
                    return message;
                });

        // Act
        MessageResponse result =
                messagingService.sendMessage(
                        client,
                        conversation.getId(),
                        request
                );

        // Assert
        ArgumentCaptor<Message> messageCaptor =
                ArgumentCaptor.forClass(Message.class);

        verify(messageRepository).save(messageCaptor.capture());

        Message savedMessage = messageCaptor.getValue();

        assertEquals(conversation.getId(), savedMessage.getConversation().getId());
        assertEquals(client.getId(), savedMessage.getSender().getId());
        assertEquals("Hello for now", savedMessage.getCiphertext());
        assertEquals("PLAINTEXT_DEV", savedMessage.getEncryptionAlgorithm());
        assertEquals("dev-placeholder", savedMessage.getIv());
        assertEquals(messageId, result.id());
        assertEquals("Hello for now", result.ciphertext());
    }

    @Test
    @DisplayName(
            "JUNIT-MSG-007: Non-participant cannot send message"
    )
    void sendMessage_nonParticipant_throwsForbiddenException() {
        // Arrange
        User client = createUser(UserRole.CLIENT, "Test", "Client");
        User therapist = createUser(UserRole.THERAPIST, "Test", "Therapist");
        User otherUser = createUser(UserRole.CLIENT, "Other", "Client");

        Conversation conversation =
                createConversation(client, therapist, ConversationStatus.ACTIVE);

        SendMessageRequest request = new SendMessageRequest(
                "Hello",
                "PLAINTEXT_DEV",
                "dev-placeholder",
                null
        );

        when(conversationRepository.findById(conversation.getId()))
                .thenReturn(Optional.of(conversation));

        // Act and assert
        assertThrows(
                AccessDeniedException.class,
                () -> messagingService.sendMessage(
                        otherUser,
                        conversation.getId(),
                        request
                )
        );

        verifyNoInteractions(messageRepository);
    }

    @Test
    @DisplayName(
            "JUNIT-MSG-008: Unknown conversation throws exception"
    )
    void sendMessage_unknownConversation_throwsIllegalArgumentException() {
        // Arrange
        User client = createUser(UserRole.CLIENT, "Test", "Client");
        UUID conversationId = UUID.randomUUID();

        SendMessageRequest request = new SendMessageRequest(
                "Hello",
                "PLAINTEXT_DEV",
                "dev-placeholder",
                null
        );

        when(conversationRepository.findById(conversationId))
                .thenReturn(Optional.empty());

        // Act and assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> messagingService.sendMessage(
                        client,
                        conversationId,
                        request
                )
        );

        assertEquals(
                "conversation could not be found!",
                exception.getMessage()
        );

        verifyNoInteractions(messageRepository);
    }

    @Test
    @DisplayName(
            "Closed conversation rejects new message"
    )
    void sendMessage_closedConversation_throwsIllegalArgumentException() {
        // Arrange
        User client = createUser(UserRole.CLIENT, "Test", "Client");
        User therapist = createUser(UserRole.THERAPIST, "Test", "Therapist");

        Conversation conversation =
                createConversation(client, therapist, ConversationStatus.CLOSED);

        SendMessageRequest request = new SendMessageRequest(
                "Hello",
                "PLAINTEXT_DEV",
                "dev-placeholder",
                null
        );

        when(conversationRepository.findById(conversation.getId()))
                .thenReturn(Optional.of(conversation));

        // Act and assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> messagingService.sendMessage(
                        client,
                        conversation.getId(),
                        request
                )
        );

        assertEquals("this conversation is closed", exception.getMessage());

        verifyNoInteractions(messageRepository);
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

    private Conversation createConversation(
            User client,
            User therapist,
            ConversationStatus status
    ) {
        Conversation conversation = new Conversation(client, therapist);
        conversation.setId(UUID.randomUUID());
        conversation.setStatus(status);
        return conversation;
    }

    private TherapistProfile createTherapistProfile(User therapist) {
        TherapistProfile therapistProfile = new TherapistProfile(
                therapist,
                "MSc Counselling",
                "REG-123"
        );
        therapistProfile.setUserId(therapist.getId());
        therapistProfile.setAcceptingClients(true);
        return therapistProfile;
    }
}
