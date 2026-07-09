import { apiRequest } from "./apiClient";

export function startConversation(therapistUserId){
    return apiRequest("/api/message/conversations", {
        method: "POST",
        body: JSON.stringify({ therapistUserId})
    });
}