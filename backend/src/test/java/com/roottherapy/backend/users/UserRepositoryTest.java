package com.roottherapy.backend.users;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName(
            "JUNIT-AUTH-009: Existing email lookup returns matching user"
    )
    void findByEmail_existingEmail_returnsUser() {
        // Arrange
        String email = uniqueEmail();

        User user = new User(
                email,
                "encoded-password",
                "Test",
                "Client",
                UserRole.CLIENT
        );

        User savedUser = userRepository.saveAndFlush(user);

        // Act
        Optional<User> result =
                userRepository.findByEmailIgnoreCase(
                        email.toUpperCase()
                );

        // Assert
        assertTrue(result.isPresent());
        assertEquals(savedUser.getId(), result.get().getId());
        assertEquals(email, result.get().getEmail());
        assertEquals(UserRole.CLIENT, result.get().getRole());
    }

    @Test
    @DisplayName(
            "JUNIT-AUTH-010: Existing email check returns true only for duplicates"
    )
    void existsByEmail_duplicateEmail_returnsTrue() {
        // Arrange
        String email = uniqueEmail();

        User user = new User(
                email,
                "encoded-password",
                "Test",
                "Client",
                UserRole.CLIENT
        );

        userRepository.saveAndFlush(user);

        // Act and assert
        assertTrue(
                userRepository.existsByEmailIgnoreCase(
                        email.toUpperCase()
                )
        );

        assertFalse(
                userRepository.existsByEmailIgnoreCase(
                        "missing-" + email
                )
        );
    }

    private String uniqueEmail() {
        return "repo-" + UUID.randomUUID() + "@example.com";
    }
}
