package com.roottherapy.backend.notification;

import com.roottherapy.backend.notification.dto.NotificationResponse;
import com.roottherapy.backend.notification.dto.UnreadNotificationCountResponse;
import com.roottherapy.backend.users.AccountStatus;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(notificationRepository);
    }

    @Test
    void create_validData_savesUnreadNotification() {
        // Arrange
        User recipient = user(UUID.randomUUID(), "admin@example.com", UserRole.ADMIN);
        UUID relatedEntityId = UUID.randomUUID();

        // Act
        notificationService.create(
                recipient,
                NotificationType.BLOG_SUBMITTED,
                "Blog awaiting review",
                "A therapist submitted a blog post.",
                "/admin/blog/" + relatedEntityId,
                "BLOG_POST",
                relatedEntityId
        );

        // Assert
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());

        Notification saved = captor.getValue();
        assertSame(recipient, saved.getRecipient());
        assertEquals(NotificationType.BLOG_SUBMITTED, saved.getType());
        assertEquals("Blog awaiting review", saved.getTitle());
        assertEquals("A therapist submitted a blog post.", saved.getMessage());
        assertEquals("/admin/blog/" + relatedEntityId, saved.getLinkUrl());
        assertEquals("BLOG_POST", saved.getRelatedEntityType());
        assertEquals(relatedEntityId, saved.getRelatedEntityId());
        assertFalse(saved.isRead());
        assertNull(saved.getReadAt());
    }

    @Test
    void getRecent_existingNotifications_returnsMappedRecipientNotifications() {
        // Arrange
        UUID recipientId = UUID.randomUUID();
        User recipient = user(recipientId, "client@example.com", UserRole.CLIENT);

        Notification newest = notification(
                UUID.randomUUID(), recipient, NotificationType.PROFILE_APPROVED,
                "Profile approved", "Your profile was approved.", false,
                Instant.parse("2026-07-30T10:00:00Z")
        );
        Notification older = notification(
                UUID.randomUUID(), recipient, NotificationType.BLOG_REJECTED,
                "Blog rejected", "Your blog was rejected.", true,
                Instant.parse("2026-07-29T10:00:00Z")
        );

        when(notificationRepository.findTop30ByRecipientIdOrderByCreatedAtDesc(recipientId))
                .thenReturn(List.of(newest, older));

        // Act
        List<NotificationResponse> result = notificationService.getRecent(recipientId);

        // Assert
        assertEquals(2, result.size());
        assertEquals(newest.getId(), result.get(0).id());
        assertEquals("Profile approved", result.get(0).title());
        assertFalse(result.get(0).read());
        assertEquals(older.getId(), result.get(1).id());
        assertTrue(result.get(1).read());

        verify(notificationRepository)
                .findTop30ByRecipientIdOrderByCreatedAtDesc(recipientId);
    }

    @Test
    void getUnreadCount_mixedNotifications_returnsRepositoryCount() {
        // Arrange
        UUID recipientId = UUID.randomUUID();
        when(notificationRepository.countByRecipientIdAndReadFalse(recipientId))
                .thenReturn(4L);

        // Act
        UnreadNotificationCountResponse result =
                notificationService.getUnreadCount(recipientId);

        // Assert
        assertEquals(4L, result.unreadCount());
        verify(notificationRepository)
                .countByRecipientIdAndReadFalse(recipientId);
    }

    @Test
    void markRead_ownUnreadNotification_setsReadAndReadTimestamp() {
        // Arrange
        UUID recipientId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();
        User recipient = user(recipientId, "therapist@example.com", UserRole.THERAPIST);

        Notification notification = notification(
                notificationId, recipient, NotificationType.BLOG_APPROVED,
                "Blog approved", "Your blog was approved.", false,
                Instant.parse("2026-07-30T10:00:00Z")
        );

        when(notificationRepository.findById(notificationId))
                .thenReturn(Optional.of(notification));
        when(notificationRepository.saveAndFlush(notification))
                .thenReturn(notification);

        // Act
        NotificationResponse result =
                notificationService.markRead(recipientId, notificationId);

        // Assert
        assertTrue(notification.isRead());
        assertNotNull(notification.getReadAt());
        assertTrue(result.read());
        assertEquals(notification.getReadAt(), result.readAt());
        verify(notificationRepository).saveAndFlush(notification);
    }

    @Test
    void markRead_otherUsersNotification_throwsForbiddenException() {
        // Arrange
        UUID ownerId = UUID.randomUUID();
        UUID requestingUserId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();
        User owner = user(ownerId, "owner@example.com", UserRole.THERAPIST);

        Notification notification = notification(
                notificationId, owner, NotificationType.PROFILE_REJECTED,
                "Profile rejected", "Your profile was rejected.", false,
                Instant.now()
        );

        when(notificationRepository.findById(notificationId))
                .thenReturn(Optional.of(notification));

        // Act
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> notificationService.markRead(requestingUserId, notificationId)
        );

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("You cannot access this notification", exception.getReason());
        assertFalse(notification.isRead());
        verify(notificationRepository, never()).saveAndFlush(any());
    }

    @Test
    void markRead_invalidId_throwsNotFoundException() {
        // Arrange
        UUID recipientId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();

        when(notificationRepository.findById(notificationId))
                .thenReturn(Optional.empty());

        // Act
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> notificationService.markRead(recipientId, notificationId)
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Notification not found", exception.getReason());
        verify(notificationRepository, never()).saveAndFlush(any());
    }

    private User user(UUID id, String email, UserRole role) {
        User user = new User(email, "encoded-password", "Test", "User", role);
        user.setId(id);
        user.setAccountStatus(AccountStatus.ACTIVE);
        return user;
    }

    private Notification notification(
            UUID id,
            User recipient,
            NotificationType type,
            String title,
            String message,
            boolean read,
            Instant createdAt
    ) {
        Notification notification = new Notification(
                recipient,
                type,
                title,
                message,
                "/notifications/target",
                "TEST_ENTITY",
                UUID.randomUUID()
        );
        notification.setId(id);
        notification.setRead(read);
        notification.setReadAt(read ? createdAt.plusSeconds(60) : null);
        notification.setCreatedAt(createdAt);
        return notification;
    }
}
