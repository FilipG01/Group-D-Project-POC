package com.roottherapy.backend.profile.therapist;

import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRepository;
import com.roottherapy.backend.users.UserRole;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class TherapistProfileRepositoryTest {

    @Autowired
    private TherapistProfileRepository therapistProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName(
            "JUNIT-THER-007: Public therapists are filtered and correctly ordered"
    )
    void findPubliclyVisibleTherapists_returnsOnlyVisibleProfilesInDisplayOrder() {
        // Arrange
        User murphyUser = createTherapistUser(
                "jane.murphy@example.com",
                "Jane",
                "Murphy"
        );

        User kellyUser = createTherapistUser(
                "zoe.kelly@example.com",
                "Zoe",
                "Kelly"
        );

        User byrneUser = createTherapistUser(
                "amy.byrne@example.com",
                "Amy",
                "Byrne"
        );

        User hiddenUser = createTherapistUser(
                "hidden.therapist@example.com",
                "Hidden",
                "Therapist"
        );

        /*
         * Byrne has the lowest display order and should be first.
         */
        TherapistProfile byrneProfile = createProfile(
                byrneUser,
                "REG-001",
                1,
                true
        );

        /*
         * Kelly and Murphy have the same display order.
         * Kelly should appear first because Kelly comes before
         * Murphy alphabetically.
         */
        TherapistProfile kellyProfile = createProfile(
                kellyUser,
                "REG-002",
                2,
                true
        );

        TherapistProfile murphyProfile = createProfile(
                murphyUser,
                "REG-003",
                2,
                true
        );

        /*
         * This therapist has the lowest order overall but is hidden,
         * so the repository query must exclude the profile.
         */
        TherapistProfile hiddenProfile = createProfile(
                hiddenUser,
                "REG-004",
                0,
                false
        );

        therapistProfileRepository.saveAllAndFlush(
                List.of(
                        murphyProfile,
                        hiddenProfile,
                        kellyProfile,
                        byrneProfile
                )
        );

        // Act
        List<TherapistProfile> result =
                therapistProfileRepository
                        .findByPubliclyVisibleTrueOrderByDisplayOrderAscUserLastNameAsc();

        // Assert

        /*
         * The test uses the development PostgreSQL database, which may already
         * contain other visible therapists. Keep only the profiles created by
         * this test before checking the result.
         */
        List<TherapistProfile> testProfiles = result.stream()
                .filter(profile ->
                        profile.getUserId().equals(byrneUser.getId())
                                || profile.getUserId().equals(kellyUser.getId())
                                || profile.getUserId().equals(murphyUser.getId())
                                || profile.getUserId().equals(hiddenUser.getId())
                )
                .toList();

        /*
         * The hidden therapist must not be included, leaving the three
         * publicly visible profiles created by this test.
         */
        assertEquals(3, testProfiles.size());

        assertEquals(
                byrneUser.getId(),
                testProfiles.get(0).getUserId()
        );

        assertEquals(
                kellyUser.getId(),
                testProfiles.get(1).getUserId()
        );

        assertEquals(
                murphyUser.getId(),
                testProfiles.get(2).getUserId()
        );

        assertTrue(
                testProfiles.stream()
                        .allMatch(TherapistProfile::getPubliclyVisible)
        );

        assertTrue(
                testProfiles.stream()
                        .noneMatch(profile ->
                                profile.getUserId().equals(hiddenUser.getId())
                        )
        );
    }

    private User createTherapistUser(
            String email,
            String firstName,
            String lastName
    ) {
        User user = new User(
                email,
                "encodedPassword",
                firstName,
                lastName,
                UserRole.THERAPIST
        );

        return userRepository.saveAndFlush(user);
    }

    private TherapistProfile createProfile(
            User user,
            String registrationNumber,
            int displayOrder,
            boolean publiclyVisible
    ) {
        TherapistProfile profile = new TherapistProfile(
                user,
                "MSc Counselling Psychology",
                registrationNumber
        );

        profile.setDisplayOrder(displayOrder);
        profile.setPubliclyVisible(publiclyVisible);
        profile.setAcceptingClients(true);

        return profile;
    }
}