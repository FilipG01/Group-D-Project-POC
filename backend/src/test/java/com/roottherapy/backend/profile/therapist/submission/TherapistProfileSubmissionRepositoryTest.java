package com.roottherapy.backend.profile.therapist.submission;

import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRepository;
import com.roottherapy.backend.users.UserRole;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TherapistProfileSubmissionRepositoryTest {

    @Autowired
    private TherapistProfileSubmissionRepository submissionRepository;

    @Autowired
    private UserRepository userRepository;

    // JUNIT-PROFILE-011
    @Test
    void findByStatusOrderBySubmittedAtAsc_returnsMatchingSubmissions() {
        User firstTherapist = saveTherapist("profile-repo-first-" + UUID.randomUUID() + "@example.com");
        User secondTherapist = saveTherapist("profile-repo-second-" + UUID.randomUUID() + "@example.com");
        User otherTherapist = saveTherapist("profile-repo-other-" + UUID.randomUUID() + "@example.com");

        TherapistProfileSubmission laterSubmitted = saveSubmission(
                firstTherapist,
                TherapistProfileSubmissionStatus.SUBMITTED,
                Instant.parse("2026-07-20T12:00:00Z"),
                "REG-" + UUID.randomUUID()
        );
        TherapistProfileSubmission earlierSubmitted = saveSubmission(
                secondTherapist,
                TherapistProfileSubmissionStatus.SUBMITTED,
                Instant.parse("2026-07-20T10:00:00Z"),
                "REG-" + UUID.randomUUID()
        );
        TherapistProfileSubmission approved = saveSubmission(
                otherTherapist,
                TherapistProfileSubmissionStatus.APPROVED,
                Instant.parse("2026-07-20T09:00:00Z"),
                "REG-" + UUID.randomUUID()
        );

        List<TherapistProfileSubmission> results = submissionRepository
                .findByStatusOrderBySubmittedAtAsc(TherapistProfileSubmissionStatus.SUBMITTED);

        Set<UUID> testIds = Set.of(
                laterSubmitted.getId(),
                earlierSubmitted.getId(),
                approved.getId()
        );
        List<TherapistProfileSubmission> testResults = results.stream()
                .filter(submission -> testIds.contains(submission.getId()))
                .toList();

        assertEquals(2, testResults.size());
        assertEquals(earlierSubmitted.getId(), testResults.get(0).getId());
        assertEquals(laterSubmitted.getId(), testResults.get(1).getId());
        assertEquals(
                List.of(
                        TherapistProfileSubmissionStatus.SUBMITTED,
                        TherapistProfileSubmissionStatus.SUBMITTED
                ),
                testResults.stream().map(TherapistProfileSubmission::getStatus).toList()
        );
    }

    private User saveTherapist(String email) {
        return userRepository.saveAndFlush(new User(
                email,
                "encoded-password",
                "Test",
                "Therapist",
                UserRole.THERAPIST
        ));
    }

    private TherapistProfileSubmission saveSubmission(
            User therapist,
            TherapistProfileSubmissionStatus status,
            Instant submittedAt,
            String registrationNumber
    ) {
        TherapistProfileSubmission submission = new TherapistProfileSubmission(therapist);
        submission.setQualifications("Test qualification");
        submission.setRegistrationNumber(registrationNumber);
        submission.setYearsExperience(3);
        submission.setBio("Test biography");
        submission.setAcceptingClients(true);
        submission.setProfileImageUrl(null);
        submission.setPublicBio(List.of("Test public biography"));
        submission.setLanguages(List.of("English"));
        submission.setSpecialisms(List.of("Anxiety"));
        submission.setStatus(status);
        submission.setSubmittedAt(submittedAt);
        return submissionRepository.saveAndFlush(submission);
    }
}
