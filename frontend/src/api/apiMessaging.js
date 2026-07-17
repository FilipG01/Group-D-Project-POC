import { apiRequest } from "./apiClient";

export function startConversation(therapistUserId){
    return apiRequest("/api/message/conversations", {
        method: "POST",
        body: JSON.stringify({ therapistUserId})
    });
}

export function listConversations(){
    return apiRequest("/api/message/conversations");
}

export function listMessages(conversationId){
    return apiRequest(`/api/message/conversations/${conversationId}/messages`);
}

export function sendMessage(conversationId, text){
    return apiRequest(`/api/message/conversations/${conversationId}/messages`, {
        method: "POST",
        body: JSON.stringify({
            ciphertext: text,
            encryptionAlgorithm: "PLAINTEXT_DEV",
            iv: "dev-placeholder",
            authTag: null,
        }),
    });
}