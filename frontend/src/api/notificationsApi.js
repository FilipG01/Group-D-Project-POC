import { apiRequest } from "./apiClient.js";

export function getNotifications() {
    return apiRequest("/api/notifications");
}

export function getUnreadNotificationCount() {
    return apiRequest("/api/notifications/unread-count");
}

export function markNotificationRead(notificationId) {
    return apiRequest(`/api/notifications/${notificationId}/read`, {
        method: "PATCH",
    });
}
