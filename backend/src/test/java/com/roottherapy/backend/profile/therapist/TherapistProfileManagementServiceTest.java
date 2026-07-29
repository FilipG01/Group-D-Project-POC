package com.roottherapy.backend.profile.therapist;

import com.roottherapy.backend.profile.therapist.dto.AdminTherapistProfileResponse;
import com.roottherapy.backend.profile.therapist.dto.CreateTherapistRequest;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRepository;
import com.roottherapy.backend.users.UserRole;
import com.roottherapy.backend.profile.therapist.dto.AdminUpdateTherapistProfileRequest;
import com.roottherapy.backend.profile.therapist.dto.ReorderTherapistsRequest;
import com.roottherapy.backend.profile.therapist.dto.TherapistOrderItem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TherapistProfileManagementServiceTest {

    @Mock
    private TherapistProfileRepository therapistProfileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private TherapistProfileManagementService service;

    @BeforeEach
    void setUp() {
        service = new TherapistProfileManagementService(
                therapistProfileRepository,
                userRepository,
                passwordEncoder
        );
    }

    @Test
    @DisplayName(
            "JUNIT-THER-001: Valid therapist data creates a user and profile"
    )
    void createTherapist_validData_createsUserAndProfile() {
        // Arrange
        CreateTherapistRequest request = new CreateTherapistRequest(
                " Jane ",
                " Murphy ",
                " JANE.MURPHY@EXAMPLE.COM ",
                "TemporaryPassword123",
                " 0851234567 ",
                " MSc Counselling Psychology ",
                " REG-12345 ",
                7
        );

        UUID therapistId = UUID.randomUUID();
        String encodedPassword = "$2a$10$encodedPassword";

        when(userRepository.existsByEmailIgnoreCase(
                "jane.murphy@example.com"
        )).thenReturn(false);

        when(therapistProfileRepository.existsByRegistrationNumber(
                "REG-12345"
        )).thenReturn(false);

        when(passwordEncoder.encode(request.temporaryPassword()))
                .thenReturn(encodedPassword);

        /*
         * Simulate the database assigning an ID to the new user.
         */
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(therapistId);
                    return user;
                });

        when(therapistProfileRepository.save(any(TherapistProfile.class)))
                .thenAnswer(invocation -> {
                    TherapistProfile profile = invocation.getArgument(0);

                    /*
                     * Simulate JPA's @MapsId behavior by copying the linked
                     * user's ID into the therapist profile primary key.
                     */
                    profile.setUserId(profile.getUser().getId());

                    return profile;
                });

        // Act
        AdminTherapistProfileResponse result =
                service.createTherapist(request);

        // Capture saved entities
        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        ArgumentCaptor<TherapistProfile> profileCaptor =
                ArgumentCaptor.forClass(TherapistProfile.class);

        verify(userRepository).save(userCaptor.capture());
        verify(therapistProfileRepository).save(profileCaptor.capture());

        User savedUser = userCaptor.getValue();
        TherapistProfile savedProfile = profileCaptor.getValue();

        // Assert created user
        assertEquals(
                "jane.murphy@example.com",
                savedUser.getEmail()
        );
        assertEquals("Jane", savedUser.getFirstName());
        assertEquals("Murphy", savedUser.getLastName());
        assertEquals("0851234567", savedUser.getPhoneNumber());
        assertEquals(UserRole.THERAPIST, savedUser.getRole());
        assertEquals(encodedPassword, savedUser.getPasswordHash());
        assertNotEquals(
                request.temporaryPassword(),
                savedUser.getPasswordHash()
        );

        // Assert created therapist profile
        assertSame(savedUser, savedProfile.getUser());
        assertEquals(
                "MSc Counselling Psychology",
                savedProfile.getQualifications()
        );
        assertEquals(
                "REG-12345",
                savedProfile.getRegistrationNumber()
        );
        assertEquals(7, savedProfile.getYearsExperience());
        assertFalse(savedProfile.getAcceptingClients());
        assertFalse(savedProfile.getPubliclyVisible());
        assertEquals(0, savedProfile.getDisplayOrder());

        // Assert returned response
        assertNotNull(result);
        assertEquals(therapistId, result.userId());
        assertEquals("Jane", result.firstName());
        assertEquals("Murphy", result.lastName());
        assertEquals(
                "jane.murphy@example.com",
                result.email()
        );
        assertEquals(
                "MSc Counselling Psychology",
                result.qualifications()
        );
        assertEquals("REG-12345", result.registrationNumber());
        assertEquals(7, result.yearsExperience());
        assertFalse(result.acceptingClients());
        assertFalse(result.publiclyVisible());
        assertEquals(0, result.displayOrder());

        // Verify required service interactions
        verify(userRepository)
                .existsByEmailIgnoreCase("jane.murphy@example.com");

        verify(therapistProfileRepository)
                .existsByRegistrationNumber("REG-12345");

        verify(passwordEncoder)
                .encode("TemporaryPassword123");

        verifyNoMoreInteractions(
                userRepository,
                therapistProfileRepository,
                passwordEncoder
        );
    }

    @Test
    @DisplayName(
            "JUNIT-THER-002: Duplicate therapist email rejects creation"
    )
    void createTherapist_duplicateEmail_throwsConflictException() {
        // Arrange
        CreateTherapistRequest request = new CreateTherapistRequest(
                "Jane",
                "Murphy",
                " JANE.MURPHY@EXAMPLE.COM ",
                "TemporaryPassword123",
                "0851234567",
                "MSc Counselling Psychology",
                "REG-12345",
                7
        );

        /*
         * The service trims the email and converts it to lowercase
         * before checking whether it already exists.
         */
        when(userRepository.existsByEmailIgnoreCase(
                "jane.murphy@example.com"
        )).thenReturn(true);

        // Act
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.createTherapist(request)
        );

        // Assert
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals(
                "An account with this email already exists",
                exception.getReason()
        );

        verify(userRepository).existsByEmailIgnoreCase(
                "jane.murphy@example.com"
        );

        /*
         * Creation must stop immediately after detecting the
         * duplicate email.
         */
        verifyNoInteractions(
                therapistProfileRepository,
                passwordEncoder
        );

        verify(userRepository, never()).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName(
            "JUNIT-THER-003: Admin can update an existing therapist profile"
    )
    void updateTherapistAsAdmin_validRequest_updatesProfile() {
        // Arrange
        UUID therapistId = UUID.randomUUID();

        User therapistUser = new User(
                "jane.murphy@example.com",
                "encodedPassword",
                "Jane",
                "Murphy",
                UserRole.THERAPIST
        );
        therapistUser.setId(therapistId);

        TherapistProfile existingProfile = new TherapistProfile(
                therapistUser,
                "Old qualification",
                "OLD-REG-001"
        );
        existingProfile.setUserId(therapistId);
        existingProfile.setYearsExperience(2);
        existingProfile.setBio("Old biography");
        existingProfile.setAcceptingClients(false);
        existingProfile.setProfileImageUrl("/uploads/old-image.jpg");
        existingProfile.setPublicBio(List.of("Old public biography"));
        existingProfile.setLanguages(List.of("English"));
        existingProfile.setSpecialisms(List.of("Anxiety"));
        existingProfile.setDisplayOrder(5);
        existingProfile.setPubliclyVisible(false);

        AdminUpdateTherapistProfileRequest request =
                new AdminUpdateTherapistProfileRequest(
                        " MSc Counselling Psychology ",
                        " REG-NEW-123 ",
                        8,
                        " Updated internal biography ",
                        true,
                        " /uploads/therapists/jane.jpg ",
                        List.of(
                                " Updated public biography ",
                                " ",
                                "Works with adults"
                        ),
                        Arrays.asList(
                                " English ",
                                null,
                                " Irish "
                        ),
                        List.of(
                                " Anxiety ",
                                "",
                                " Trauma "
                        ),
                        2,
                        true
                );

        when(userRepository.findById(therapistId))
                .thenReturn(Optional.of(therapistUser));

        when(therapistProfileRepository.findById(therapistId))
                .thenReturn(Optional.of(existingProfile));

        when(
                therapistProfileRepository
                        .existsByRegistrationNumberAndUserIdNot(
                                "REG-NEW-123",
                                therapistId
                        )
        ).thenReturn(false);

        when(therapistProfileRepository.save(existingProfile))
                .thenReturn(existingProfile);

        // Act
        AdminTherapistProfileResponse result =
                service.updateTherapistAsAdmin(
                        therapistId,
                        request
                );

        // Assert updated entity values
        assertEquals(
                "MSc Counselling Psychology",
                existingProfile.getQualifications()
        );
        assertEquals(
                "REG-NEW-123",
                existingProfile.getRegistrationNumber()
        );
        assertEquals(8, existingProfile.getYearsExperience());
        assertEquals(
                "Updated internal biography",
                existingProfile.getBio()
        );
        assertTrue(existingProfile.getAcceptingClients());
        assertEquals(
                "/uploads/therapists/jane.jpg",
                existingProfile.getProfileImageUrl()
        );

        /*
         * Blank and null list entries should be removed,
         * while valid entries should be trimmed.
         */
        assertEquals(
                List.of(
                        "Updated public biography",
                        "Works with adults"
                ),
                existingProfile.getPublicBio()
        );

        assertEquals(
                List.of("English", "Irish"),
                existingProfile.getLanguages()
        );

        assertEquals(
                List.of("Anxiety", "Trauma"),
                existingProfile.getSpecialisms()
        );

        assertEquals(2, existingProfile.getDisplayOrder());
        assertTrue(existingProfile.getPubliclyVisible());

        // Assert returned response
        assertNotNull(result);
        assertEquals(therapistId, result.userId());
        assertEquals("Jane", result.firstName());
        assertEquals("Murphy", result.lastName());
        assertEquals(
                "jane.murphy@example.com",
                result.email()
        );
        assertEquals(
                "MSc Counselling Psychology",
                result.qualifications()
        );
        assertEquals(
                "REG-NEW-123",
                result.registrationNumber()
        );
        assertEquals(8, result.yearsExperience());
        assertEquals(
                "Updated internal biography",
                result.bio()
        );
        assertTrue(result.acceptingClients());
        assertEquals(2, result.displayOrder());
        assertTrue(result.publiclyVisible());

        // Verify repository interactions
        verify(userRepository).findById(therapistId);
        verify(therapistProfileRepository)
                .findById(therapistId);

        verify(therapistProfileRepository)
                .existsByRegistrationNumberAndUserIdNot(
                        "REG-NEW-123",
                        therapistId
                );

        verify(therapistProfileRepository)
                .save(existingProfile);

        verifyNoInteractions(passwordEncoder);

        verifyNoMoreInteractions(
                userRepository,
                therapistProfileRepository
        );
    }

    @Test
    @DisplayName(
            "JUNIT-THER-004: Admin can make a therapist publicly visible"
    )
    void updateTherapistAsAdmin_publiclyVisibleTrue_updatesVisibility() {
        // Arrange
        UUID therapistId = UUID.randomUUID();

        User therapistUser = new User(
                "jane.murphy@example.com",
                "encodedPassword",
                "Jane",
                "Murphy",
                UserRole.THERAPIST
        );
        therapistUser.setId(therapistId);

        TherapistProfile existingProfile = new TherapistProfile(
                therapistUser,
                "MSc Counselling Psychology",
                "REG-12345"
        );

        existingProfile.setUserId(therapistId);
        existingProfile.setYearsExperience(6);
        existingProfile.setBio("Therapist biography");
        existingProfile.setAcceptingClients(true);
        existingProfile.setProfileImageUrl(
                "/uploads/therapists/jane.jpg"
        );
        existingProfile.setPublicBio(
                List.of("Public therapist biography")
        );
        existingProfile.setLanguages(
                List.of("English")
        );
        existingProfile.setSpecialisms(
                List.of("Anxiety")
        );
        existingProfile.setDisplayOrder(3);

        // Therapist is initially hidden from the public website.
        existingProfile.setPubliclyVisible(false);

        AdminUpdateTherapistProfileRequest request =
                new AdminUpdateTherapistProfileRequest(
                        "MSc Counselling Psychology",
                        "REG-12345",
                        6,
                        "Therapist biography",
                        true,
                        "/uploads/therapists/jane.jpg",
                        List.of("Public therapist biography"),
                        List.of("English"),
                        List.of("Anxiety"),
                        3,
                        true
                );

        when(userRepository.findById(therapistId))
                .thenReturn(Optional.of(therapistUser));

        when(therapistProfileRepository.findById(therapistId))
                .thenReturn(Optional.of(existingProfile));

        when(
                therapistProfileRepository
                        .existsByRegistrationNumberAndUserIdNot(
                                "REG-12345",
                                therapistId
                        )
        ).thenReturn(false);

        when(therapistProfileRepository.save(existingProfile))
                .thenReturn(existingProfile);

        // Act
        AdminTherapistProfileResponse result =
                service.updateTherapistAsAdmin(
                        therapistId,
                        request
                );

        // Assert
        assertTrue(existingProfile.getPubliclyVisible());
        assertTrue(result.publiclyVisible());

        verify(userRepository).findById(therapistId);

        verify(therapistProfileRepository)
                .findById(therapistId);

        verify(therapistProfileRepository)
                .existsByRegistrationNumberAndUserIdNot(
                        "REG-12345",
                        therapistId
                );

        verify(therapistProfileRepository)
                .save(existingProfile);

        verifyNoInteractions(passwordEncoder);

        verifyNoMoreInteractions(
                userRepository,
                therapistProfileRepository
        );
    }

    @Test
    @DisplayName(
            "JUNIT-THER-005: Admin can reorder therapist profiles"
    )
    void reorderTherapists_validRequest_updatesDisplayOrders() {
        // Arrange
        UUID firstTherapistId = UUID.randomUUID();
        UUID secondTherapistId = UUID.randomUUID();

        User firstUser = new User(
                "amy.byrne@example.com",
                "encodedPassword",
                "Amy",
                "Byrne",
                UserRole.THERAPIST
        );
        firstUser.setId(firstTherapistId);

        User secondUser = new User(
                "zoe.kelly@example.com",
                "encodedPassword",
                "Zoe",
                "Kelly",
                UserRole.THERAPIST
        );
        secondUser.setId(secondTherapistId);

        TherapistProfile firstProfile = new TherapistProfile(
                firstUser,
                "MSc Counselling",
                "REG-001"
        );
        firstProfile.setUserId(firstTherapistId);
        firstProfile.setDisplayOrder(0);
        firstProfile.setPubliclyVisible(true);

        TherapistProfile secondProfile = new TherapistProfile(
                secondUser,
                "MSc Psychotherapy",
                "REG-002"
        );
        secondProfile.setUserId(secondTherapistId);
        secondProfile.setDisplayOrder(1);
        secondProfile.setPubliclyVisible(true);

        ReorderTherapistsRequest request =
                new ReorderTherapistsRequest(
                        List.of(
                                new TherapistOrderItem(
                                        firstTherapistId,
                                        2
                                ),
                                new TherapistOrderItem(
                                        secondTherapistId,
                                        1
                                )
                        )
                );

        when(userRepository.findById(firstTherapistId))
                .thenReturn(Optional.of(firstUser));

        when(userRepository.findById(secondTherapistId))
                .thenReturn(Optional.of(secondUser));

        when(therapistProfileRepository.findById(firstTherapistId))
                .thenReturn(Optional.of(firstProfile));

        when(therapistProfileRepository.findById(secondTherapistId))
                .thenReturn(Optional.of(secondProfile));

        /*
         * The repository returns the profiles in their new display order
         * after the service updates the entity fields.
         */
        when(
                therapistProfileRepository
                        .findAllByOrderByDisplayOrderAscUserLastNameAsc()
        ).thenReturn(
                List.of(secondProfile, firstProfile)
        );

        // Act
        List<AdminTherapistProfileResponse> result =
                service.reorderTherapists(request);

        // Assert entity updates
        assertEquals(2, firstProfile.getDisplayOrder());
        assertEquals(1, secondProfile.getDisplayOrder());

        // Assert returned ordering
        assertEquals(2, result.size());

        assertEquals(
                secondTherapistId,
                result.get(0).userId()
        );
        assertEquals(
                1,
                result.get(0).displayOrder()
        );

        assertEquals(
                firstTherapistId,
                result.get(1).userId()
        );
        assertEquals(
                2,
                result.get(1).displayOrder()
        );

        // Verify repository interactions
        verify(userRepository).findById(firstTherapistId);
        verify(userRepository).findById(secondTherapistId);

        verify(therapistProfileRepository)
                .findById(firstTherapistId);

        verify(therapistProfileRepository)
                .findById(secondTherapistId);

        verify(therapistProfileRepository)
                .findAllByOrderByDisplayOrderAscUserLastNameAsc();

        /*
         * reorderTherapists() relies on the transaction and Hibernate
         * dirty checking, so it does not call save() directly.
         */
        verify(
                therapistProfileRepository,
                never()
        ).save(any(TherapistProfile.class));

        verifyNoInteractions(passwordEncoder);

        verifyNoMoreInteractions(
                userRepository,
                therapistProfileRepository
        );
    }

    @Test
    @DisplayName(
            "JUNIT-THER-006: Admin can retrieve a therapist profile"
    )
    void getTherapistForAdmin_existingTherapist_returnsProfile() {
        // Arrange
        UUID therapistId = UUID.randomUUID();

        User therapistUser = new User(
                "jane.murphy@example.com",
                "encodedPassword",
                "Jane",
                "Murphy",
                UserRole.THERAPIST
        );
        therapistUser.setId(therapistId);

        TherapistProfile profile = new TherapistProfile(
                therapistUser,
                "MSc Counselling Psychology",
                "REG-12345"
        );

        profile.setUserId(therapistId);
        profile.setYearsExperience(7);
        profile.setBio("Therapist biography");
        profile.setAcceptingClients(true);
        profile.setProfileImageUrl("/uploads/therapists/jane.jpg");
        profile.setPublicBio(
                List.of("Public therapist biography")
        );
        profile.setLanguages(
                List.of("English", "Irish")
        );
        profile.setSpecialisms(
                List.of("Anxiety", "Trauma")
        );
        profile.setDisplayOrder(4);
        profile.setPubliclyVisible(true);

        when(userRepository.findById(therapistId))
                .thenReturn(Optional.of(therapistUser));

        when(therapistProfileRepository.findById(therapistId))
                .thenReturn(Optional.of(profile));

        // Act
        AdminTherapistProfileResponse result =
                service.getTherapistForAdmin(therapistId);

        // Assert
        assertNotNull(result);

        assertEquals(therapistId, result.userId());
        assertEquals("Jane", result.firstName());
        assertEquals("Murphy", result.lastName());
        assertEquals(
                "jane.murphy@example.com",
                result.email()
        );

        assertEquals(
                "MSc Counselling Psychology",
                result.qualifications()
        );

        assertEquals(
                "REG-12345",
                result.registrationNumber()
        );

        assertEquals(
                7,
                result.yearsExperience()
        );

        assertEquals(
                "Therapist biography",
                result.bio()
        );

        assertTrue(result.acceptingClients());

        assertEquals(
                "/uploads/therapists/jane.jpg",
                result.profileImageUrl()
        );

        assertEquals(
                List.of("Public therapist biography"),
                result.publicBio()
        );

        assertEquals(
                List.of("English", "Irish"),
                result.languages()
        );

        assertEquals(
                List.of("Anxiety", "Trauma"),
                result.specialisms()
        );

        assertEquals(4, result.displayOrder());

        assertTrue(result.publiclyVisible());

        verify(userRepository).findById(therapistId);

        verify(therapistProfileRepository)
                .findById(therapistId);

        verifyNoInteractions(passwordEncoder);

        verifyNoMoreInteractions(
                userRepository,
                therapistProfileRepository
        );
    }
}