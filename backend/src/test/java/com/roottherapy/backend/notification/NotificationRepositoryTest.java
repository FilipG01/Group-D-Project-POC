package com.roottherapy.backend.notification;

import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRepository;
import com.roottherapy.backend.users.UserRole;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Test
    void recipientQueries_returnNewestFirstAndCorrectUnreadCount() {
        // Arrange: unique users isolate this test from records in the shared database.
        String token = UUID.randomUUID().toString();
        User recipient = userRepository.saveAndFlush(new User(
                "recipient-" + token + "@example.com",
                "encoded-password",
                "Recipient",
                "User",
                UserRole.CLIENT
        ));
        User otherUser = userRepository.saveAndFlush(new User(
                "other-" + token + "@example.com",
                "encoded-password",
                "Other",
                "User",
                UserRole.CLIENT
        ));

        Notification oldestUnread = saveNotification(
                recipient,
                NotificationType.BLOG_APPROVED,
                "Oldest",
                false
        );
        Notification middleRead = saveNotification(
                recipient,
                NotificationType.PROFILE_APPROVED,
                "Middle",
                true
        );
        Notification newestUnread = saveNotification(
                recipient,
                NotificationType.BLOG_REJECTED,
                "Newest",
                false
        );
        saveNotification(
                otherUser,
                NotificationType.PROFILE_REJECTED,
                "Other user's notification",
                false
        );

        setCreatedAt(oldestUnread.getId(), Instant.parse("2026-07-28T10:00:00Z"));
        setCreatedAt(middleRead.getId(), Instant.parse("2026-07-29T10:00:00Z"));
        setCreatedAt(newestUnread.getId(), Instant.parse("2026-07-30T10:00:00Z"));

        entityManager.clear();

        // Act
        List<Notification> result =
                notificationRepository.findTop30ByRecipientIdOrderByCreatedAtDesc(recipient.getId());
        long unreadCount =
                notificationRepository.countByRecipientIdAndReadFalse(recipient.getId());

        // Assert
        assertEquals(3, result.size());
        assertEquals(newestUnread.getId(), result.get(0).getId());
        assertEquals(middleRead.getId(), result.get(1).getId());
        assertEquals(oldestUnread.getId(), result.get(2).getId());
        assertTrue(result.stream().allMatch(
                notification -> notification.getRecipient().getId().equals(recipient.getId())
        ));
        assertEquals(2L, unreadCount);
    }

    private Notification saveNotification(
            User recipient,
            NotificationType type,
            String title,
            boolean read
    ) {
        Notification notification = new Notification(
                recipient,
                type,
                title,
                title + " message",
                "/test-link",
                "TEST_ENTITY",
                UUID.randomUUID()
        );
        notification.setRead(read);
        notification.setReadAt(read ? Instant.now() : null);
        return notificationRepository.saveAndFlush(notification);
    }

    private void setCreatedAt(UUID notificationId, Instant createdAt) {
        jdbcTemplate.update(
                "UPDATE notifications SET created_at = ? WHERE id = ?",
                Timestamp.from(createdAt),
                notificationId
        );
    }
}
