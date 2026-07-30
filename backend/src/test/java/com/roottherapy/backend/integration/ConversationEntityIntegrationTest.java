package com.roottherapy.backend.integration;

import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRepository;
import com.roottherapy.backend.users.UserRole;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class ConversationEntityIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    void messageRequiresValidConversation_foreignKeyIsEnforced() {
        User sender = userRepository.saveAndFlush(new User(
                "message-sender@example.com",
                "encoded-password",
                "Message",
                "Sender",
                UserRole.CLIENT
        ));

        UUID nonexistentConversationId = UUID.randomUUID();

        assertThrows(
                DataIntegrityViolationException.class,
                () -> jdbcTemplate.update(
                        """
                        INSERT INTO messages (
                            conversation_id,
                            sender_user_id,
                            ciphertext,
                            encryption_algorithm,
                            iv,
                            auth_tag
                        )
                        VALUES (?, ?, ?, ?, ?, ?)
                        """,
                        nonexistentConversationId,
                        sender.getId(),
                        "encrypted-message",
                        "AES/GCM/NoPadding",
                        "test-initialisation-vector",
                        "test-authentication-tag"
                )
        );
    }
}
