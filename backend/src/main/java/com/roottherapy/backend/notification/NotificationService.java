package com.roottherapy.backend.notification;

import com.roottherapy.backend.notification.dto.NotificationResponse;
import com.roottherapy.backend.notification.dto.UnreadNotificationCountResponse;
import com.roottherapy.backend.users.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void create(
            User recipient,
            NotificationType type,
            String title,
            String message,
            String linkUrl,
            String relatedEntityType,
            UUID relatedEntityId
    ) {
        Notification notification = new Notification(
                recipient,
                type,
                title,
                message,
                linkUrl,
                relatedEntityType,
                relatedEntityId
        );

        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getRecent(UUID recipientUserId) {
        return notificationRepository
                .findTop30ByRecipientIdOrderByCreatedAtDesc(recipientUserId)
                .stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public UnreadNotificationCountResponse getUnreadCount(UUID recipientUserId) {
        return new UnreadNotificationCountResponse(
                notificationRepository.countByRecipientIdAndReadFalse(recipientUserId)
        );
    }

    @Transactional
    public NotificationResponse markRead(UUID recipientUserId, UUID notificationId) {
        Notification notification = getOwnedNotification(recipientUserId, notificationId);

        if (!notification.isRead()) {
            notification.setRead(true);
            notification.setReadAt(Instant.now());
        }

        return NotificationResponse.fromEntity(
                notificationRepository.saveAndFlush(notification)
        );
    }

    private Notification getOwnedNotification(UUID recipientUserId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Notification not found"
                ));

        // Prevent a signed-in user from reading another user's notification.
        if (!notification.getRecipient().getId().equals(recipientUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot access this notification");
        }

        return notification;
    }
}
