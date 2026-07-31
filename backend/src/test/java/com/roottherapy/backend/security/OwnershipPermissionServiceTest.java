package com.roottherapy.backend.security;

import com.roottherapy.backend.content.blog.BlogPost;
import com.roottherapy.backend.content.blog.BlogPostRepository;
import com.roottherapy.backend.content.blog.BlogSlugService;
import com.roottherapy.backend.content.blog.TherapistBlogService;
import com.roottherapy.backend.content.blog.dto.UpdateTherapistBlogPostRequest;
import com.roottherapy.backend.messaging.Conversation;
import com.roottherapy.backend.messaging.ConversationRepository;
import com.roottherapy.backend.messaging.ConversationStatus;
import com.roottherapy.backend.messaging.MessageRepository;
import com.roottherapy.backend.messaging.MessagingService;
import com.roottherapy.backend.notification.ModerationCommunicationService;
import com.roottherapy.backend.notification.Notification;
import com.roottherapy.backend.notification.NotificationRepository;
import com.roottherapy.backend.notification.NotificationService;
import com.roottherapy.backend.notification.NotificationType;
import com.roottherapy.backend.profile.therapist.TherapistProfileRepository;
import com.roottherapy.backend.profile.therapist.submission.TherapistProfileSubmission;
import com.roottherapy.backend.profile.therapist.submission.TherapistProfileSubmissionRepository;
import com.roottherapy.backend.profile.therapist.submission.TherapistProfileSubmissionService;
import com.roottherapy.backend.profile.therapist.submission.dto.UpdateTherapistProfileDraftRequest;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRepository;
import com.roottherapy.backend.users.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OwnershipPermissionServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TherapistProfileRepository therapistProfileRepository;

    @Mock
    private BlogPostRepository blogPostRepository;

    @Mock
    private BlogSlugService blogSlugService;

    @Mock
    private ModerationCommunicationService communicationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private TherapistProfileSubmissionRepository submissionRepository;

    @Test
    @DisplayName("JUNIT-SEC-006: Non-participant cannot read conversation messages")
    void getConversation_userNotParticipant_throwsForbiddenException() {
        User client = createUser(UserRole.CLIENT);
        User therapist = createUser(UserRole.THERAPIST);
        User otherUser = createUser(UserRole.CLIENT);
        Conversation conversation = createConversation(client, therapist);

        MessagingService messagingService = new MessagingService(
                conversationRepository,
                messageRepository,
                userRepository,
                therapistProfileRepository
        );

        when(conversationRepository.findById(conversation.getId()))
                .thenReturn(Optional.of(conversation));

        assertThrows(
                AccessDeniedException.class,
                () -> messagingService.listMessages(
                        otherUser,
                        conversation.getId()
                )
        );

        verifyNoInteractions(messageRepository);
    }

    @Test
    @DisplayName("JUNIT-SEC-007: Wrong therapist cannot update another therapist post")
    void updatePost_wrongTherapist_throwsNotFoundAndDoesNotSave() {
        User therapist = createUser(UserRole.THERAPIST);
        UUID postId = UUID.randomUUID();

        TherapistBlogService blogService = new TherapistBlogService(
                blogPostRepository,
                userRepository,
                blogSlugService,
                communicationService
        );

        when(userRepository.findById(therapist.getId()))
                .thenReturn(Optional.of(therapist));

        when(blogPostRepository.findByIdAndAuthorId(
                postId,
                therapist.getId()
        )).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> blogService.updateOwnPost(
                        therapist.getId(),
                        postId,
                        validBlogUpdateRequest()
                )
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(blogPostRepository, never()).saveAndFlush(any(BlogPost.class));
    }

    @Test
    @DisplayName("JUNIT-SEC-008: User cannot mark another user's notification as read")
    void markRead_wrongRecipient_throwsForbiddenException() {
        User recipient = createUser(UserRole.CLIENT);
        User otherUser = createUser(UserRole.THERAPIST);
        UUID notificationId = UUID.randomUUID();

        Notification notification = new Notification(
                recipient,
                NotificationType.BLOG_SUBMITTED,
                "Blog submitted",
                "A blog post has been submitted.",
                "/dashboard",
                "BLOG_POST",
                UUID.randomUUID()
        );
        notification.setId(notificationId);

        NotificationService notificationService =
                new NotificationService(notificationRepository);

        when(notificationRepository.findById(notificationId))
                .thenReturn(Optional.of(notification));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> notificationService.markRead(
                        otherUser.getId(),
                        notificationId
                )
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());

        verify(notificationRepository, never())
                .saveAndFlush(any(Notification.class));
    }

    @Test
    @DisplayName("JUNIT-SEC-009: Non-therapist cannot update therapist draft")
    void updateDraft_wrongRole_throwsForbiddenException() {
        User client = createUser(UserRole.CLIENT);

        TherapistProfileSubmissionService submissionService =
                new TherapistProfileSubmissionService(
                        submissionRepository,
                        therapistProfileRepository,
                        userRepository,
                        communicationService
                );

        when(userRepository.findById(client.getId()))
                .thenReturn(Optional.of(client));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> submissionService.updateOwnDraft(
                        client.getId(),
                        validDraftRequest()
                )
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());

        verifyNoInteractions(
                submissionRepository,
                therapistProfileRepository,
                communicationService
        );
    }

    @Test
    @DisplayName("JUNIT-SEC-009: Therapist cannot update a draft they do not own")
    void updateDraft_noOwnedDraft_throwsNotFoundAndDoesNotSave() {
        User therapist = createUser(UserRole.THERAPIST);

        TherapistProfileSubmissionService submissionService =
                new TherapistProfileSubmissionService(
                        submissionRepository,
                        therapistProfileRepository,
                        userRepository,
                        communicationService
                );

        when(userRepository.findById(therapist.getId()))
                .thenReturn(Optional.of(therapist));

        when(submissionRepository
                .findFirstByTherapistIdAndStatusInOrderByUpdatedAtDesc(
                        eq(therapist.getId()),
                        anySet()
                )).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> submissionService.updateOwnDraft(
                        therapist.getId(),
                        validDraftRequest()
                )
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(submissionRepository, never())
                .saveAndFlush(any(TherapistProfileSubmission.class));
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

    private Conversation createConversation(User client, User therapist) {
        Conversation conversation = new Conversation(client, therapist);
        conversation.setId(UUID.randomUUID());
        conversation.setStatus(ConversationStatus.ACTIVE);
        return conversation;
    }

    private UpdateTherapistBlogPostRequest validBlogUpdateRequest() {
        return new UpdateTherapistBlogPostRequest(
                "Updated title",
                "Updated summary",
                "Updated body",
                null,
                null,
                null,
                List.of("Wellbeing"),
                0L
        );
    }

    private UpdateTherapistProfileDraftRequest validDraftRequest() {
        return new UpdateTherapistProfileDraftRequest(
                "MSc Counselling",
                "REG-123",
                5,
                "Therapist bio",
                true,
                null,
                List.of("I support clients with anxiety."),
                List.of("English"),
                List.of("Anxiety"),
                0L
        );
    }
}
