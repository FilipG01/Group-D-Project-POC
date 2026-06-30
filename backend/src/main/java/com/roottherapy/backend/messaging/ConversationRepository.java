package com.roottherapy.backend.messaging;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    Optional<Conversation> findByClientIdAndTherapistId(UUID clientId, UUID therapistId);
    List<Conversation> findByClientIdOrTherapistId(UUID clientId, UUID therapistId);
}
