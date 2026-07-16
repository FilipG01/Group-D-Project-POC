import { apiRequest } from "./apiClient";

export function getOwnClientProfile() {
    return apiRequest("/api/client-profile/me");
}

export function updateOwnClientProfile(profileData) {
    return apiRequest("/api/client-profile/me", {
        method: "PUT",
        body: JSON.stringify(profileData),
    });
}
