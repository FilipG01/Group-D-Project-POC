package com.roottherapy.backend.notification;

import com.roottherapy.backend.notification.dto.NotificationResponse;
import com.roottherapy.backend.notification.dto.UnreadNotificationCountResponse;
import com.roottherapy.backend.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationResponse> getNotifications(Authentication authentication) {
        return notificationService.getRecent(userId(authentication));
    }

    @GetMapping("/unread-count")
    public UnreadNotificationCountResponse getUnreadCount(Authentication authentication) {
        return notificationService.getUnreadCount(userId(authentication));
    }

    @PatchMapping("/{notificationId}/read")
    public NotificationResponse markRead(
            Authentication authentication,
            @PathVariable UUID notificationId
    ) {
        return notificationService.markRead(userId(authentication), notificationId);
    }

    private UUID userId(Authentication authentication) {
        CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
        return details.getUser().getId();
    }
}
