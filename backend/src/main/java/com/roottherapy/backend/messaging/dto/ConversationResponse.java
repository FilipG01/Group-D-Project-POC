package com.roottherapy.backend.messaging.dto;

import java.time.Instant;
import java.util.UUID;
import com.roottherapy.backend.messaging.Conversation;
import com.roottherapy.backend.messaging.ConversationStatus;

public record ConversationResponse(
        UUID id,
        UUID clientUserId,
        String clientFirstName,
        String clientLastName,
        UUID therapistUserId,
        String therapistFirstName,
        String therapistLastName,
        ConversationStatus status,
        Instant createdAt,
        Instant updatedAt
) {

    public static ConversationResponse from(Conversation conversation) {
        return new ConversationResponse(
                conversation.getId(),
                conversation.getClient().getId(),
                conversation.getClient().getFirstName(),
                conversation.getClient().getLastName(),
                conversation.getTherapist().getId(),
                conversation.getTherapist().getFirstName(),
                conversation.getTherapist().getLastName(),
                conversation.getStatus(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt()
        );
    }
}
