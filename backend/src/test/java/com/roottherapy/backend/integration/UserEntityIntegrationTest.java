package com.roottherapy.backend.integration;

import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRepository;
import com.roottherapy.backend.users.UserRole;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class UserEntityIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void duplicateEmail_violatesCaseInsensitiveUniqueConstraint() {
        // The database migration creates a unique index on LOWER(email),
        // so differently-cased versions of the same address must conflict.
        User firstUser = new User(
                "duplicate@example.com",
                "encoded-password",
                "First",
                "User",
                UserRole.CLIENT
        );

        User secondUser = new User(
                "DUPLICATE@example.com",
                "encoded-password",
                "Second",
                "User",
                UserRole.CLIENT
        );

        userRepository.saveAndFlush(firstUser);

        assertThrows(
                DataIntegrityViolationException.class,
                () -> userRepository.saveAndFlush(secondUser)
        );
    }
}
