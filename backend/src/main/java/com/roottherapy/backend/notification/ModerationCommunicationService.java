package com.roottherapy.backend.notification;

import com.roottherapy.backend.users.AccountStatus;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRepository;
import com.roottherapy.backend.users.UserRole;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Coordinates moderation-related in-app notifications.
 *
 * Email delivery is intentionally not included in the current POC. A future
 * production version can add a separate email or messaging adapter without
 * changing the notification database model used here.
 */
@Service
public class ModerationCommunicationService {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public ModerationCommunicationService(
            NotificationService notificationService,
            UserRepository userRepository
    ) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    /** Creates one notification for every active administrator. */
    public void notifyAdminsOfBlogSubmission(User therapist, UUID postId, String title) {
        String link = "/admin/blog/" + postId;
        String therapistName = fullName(therapist);

        notifyAdmins(
                NotificationType.BLOG_SUBMITTED,
                "Blog draft awaiting review",
                therapistName + " submitted “" + title + "”.",
                link,
                "BLOG_POST",
                postId
        );
    }

    /** Creates one notification for every active administrator. */
    public void notifyAdminsOfProfileSubmission(User therapist, UUID submissionId) {
        String link = "/admin/therapist-submissions/" + submissionId;
        String therapistName = fullName(therapist);

        notifyAdmins(
                NotificationType.PROFILE_SUBMITTED,
                "Profile changes awaiting review",
                therapistName + " submitted profile changes.",
                link,
                "THERAPIST_PROFILE_SUBMISSION",
                submissionId
        );
    }

    /** Notifies the therapist after an administrator approves or rejects a blog. */
    public void notifyTherapistOfBlogDecision(
            User therapist,
            UUID postId,
            String title,
            String decision,
            String note
    ) {
        boolean approved = "APPROVED".equals(decision);
        NotificationType type = approved
                ? NotificationType.BLOG_APPROVED
                : NotificationType.BLOG_REJECTED;
        String link = "/therapist/blog/" + postId + "/edit";
        String message = buildDecisionMessage(
                "Your blog post “" + title + "” was " + decision.toLowerCase() + ".",
                note
        );

        notifyTherapist(
                therapist,
                type,
                approved ? "Blog approved" : "Blog rejected",
                message,
                link,
                "BLOG_POST",
                postId
        );
    }

    /** Notifies the therapist after an administrator approves or rejects profile changes. */
    public void notifyTherapistOfProfileDecision(
            User therapist,
            UUID submissionId,
            String decision,
            String note
    ) {
        boolean approved = "APPROVED".equals(decision);
        NotificationType type = approved
                ? NotificationType.PROFILE_APPROVED
                : NotificationType.PROFILE_REJECTED;
        String message = buildDecisionMessage(
                "Your therapist profile changes were " + decision.toLowerCase() + ".",
                note
        );

        notifyTherapist(
                therapist,
                type,
                approved ? "Profile changes approved" : "Profile changes rejected",
                message,
                "/therapist/profile",
                "THERAPIST_PROFILE_SUBMISSION",
                submissionId
        );
    }

    private void notifyAdmins(
            NotificationType type,
            String title,
            String message,
            String link,
            String entityType,
            UUID entityId
    ) {
        List<User> admins = userRepository.findAllByRoleAndAccountStatus(
                UserRole.ADMIN,
                AccountStatus.ACTIVE
        );

        for (User admin : admins) {
            notificationService.create(
                    admin,
                    type,
                    title,
                    message,
                    link,
                    entityType,
                    entityId
            );
        }
    }

    private void notifyTherapist(
            User therapist,
            NotificationType type,
            String title,
            String message,
            String link,
            String entityType,
            UUID entityId
    ) {
        notificationService.create(
                therapist,
                type,
                title,
                message,
                link,
                entityType,
                entityId
        );
    }

    /** Includes useful review feedback in the in-app notification when supplied. */
    private String buildDecisionMessage(String message, String note) {
        if (note == null || note.isBlank()) {
            return message;
        }

        return message + " Review note: " + note.trim();
    }

    private String fullName(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }
}
