package com.roottherapy.backend.messaging.dto;

import com.roottherapy.backend.messaging.Message;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        UUID conversationId,
        UUID senderUserId,
        String senderFirstName,
        String senderLastName,
        String ciphertext,
        String encryptionAlgorithm,
        String iv,
        String authTag,
        Instant readAt,
        Instant createdAt

) {
    public static MessageResponse from(Message message){
        return new MessageResponse(
                message.getId(),
                message.getConversation().getId(),
                message.getSender().getId(),
                message.getSender().getFirstName(),
                message.getSender().getLastName(),
                message.getCiphertext(),
                message.getEncryptionAlgorithm(),
                message.getIv(),
                message.getAuthTag(),
                message.getReadAt(),
                message.getCreatedAt()
        );
    }
}
