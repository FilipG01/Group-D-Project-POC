package com.roottherapy.backend.integration;

import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRepository;
import com.roottherapy.backend.users.UserRole;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuditingIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private UUID createdUserId;

    @AfterEach
    void cleanUp() {
        if (createdUserId != null) {
            userRepository.deleteById(createdUserId);
        }
    }

    @Test
    void createAndUpdate_setsExpectedDatabaseTimestamps()
            throws InterruptedException {
        User savedUser = userRepository.saveAndFlush(new User(
                "timestamp-test@example.com",
                "encoded-password",
                "Timestamp",
                "Original",
                UserRole.CLIENT
        ));
        createdUserId = savedUser.getId();

        User createdState = userRepository.findById(
                createdUserId
        ).orElseThrow();

        Instant createdAt = createdState.getCreatedAt();
        Instant firstUpdatedAt = createdState.getUpdatedAt();

        assertNotNull(createdAt);
        assertNotNull(firstUpdatedAt);

        // PostgreSQL NOW() is transaction-scoped. The test is deliberately
        // not @Transactional so the update occurs in a later transaction.
        Thread.sleep(30);

        createdState.setLastName("Updated");
        userRepository.saveAndFlush(createdState);

        User updatedState = userRepository.findById(
                createdUserId
        ).orElseThrow();

        assertEquals(createdAt, updatedState.getCreatedAt());
        assertTrue(
                updatedState.getUpdatedAt().isAfter(firstUpdatedAt),
                "updated_at should advance after the entity is modified"
        );

        assertTrue(
                Duration.between(
                        updatedState.getCreatedAt(),
                        updatedState.getUpdatedAt()
                ).toMillis() >= 0
        );
    }
}
