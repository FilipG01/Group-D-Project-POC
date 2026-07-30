package com.roottherapy.backend.integration;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationState;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FlywayStartupIntegrationTest {

    @Autowired
    private Flyway flyway;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void configuredDatabase_hasAllMigrationsAndExpectedSchemaObjects() {
        MigrationInfo[] pendingMigrations = flyway.info().pending();
        MigrationInfo[] appliedMigrations = flyway.info().applied();

        assertEquals(
                0,
                pendingMigrations.length,
                "No Flyway migration should remain pending after startup"
        );

        assertTrue(
                appliedMigrations.length >= 11,
                "The current project contains migrations V1 through V11"
        );

        assertTrue(
                List.of(appliedMigrations).stream()
                        .noneMatch(
                                migration ->
                                        migration.getState()
                                                == MigrationState.FAILED
                        ),
                "No migration should be in a failed state"
        );

        List<String> requiredTables = List.of(
                "users",
                "therapist_profiles",
                "conversations",
                "messages",
                "services",
                "blog_posts",
                "gallery_images",
                "therapist_profile_submissions",
                "notifications",
                "appointments"
        );

        for (String tableName : requiredTables) {
            Integer tableCount = jdbcTemplate.queryForObject(
                    """
                    SELECT COUNT(*)
                    FROM information_schema.tables
                    WHERE table_schema = current_schema()
                      AND table_name = ?
                    """,
                    Integer.class,
                    tableName
            );

            assertEquals(
                    1,
                    tableCount,
                    "Expected table was not found: " + tableName
            );
        }
    }
}
