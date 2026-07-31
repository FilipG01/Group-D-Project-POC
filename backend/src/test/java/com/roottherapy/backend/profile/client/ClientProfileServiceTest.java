package com.roottherapy.backend.profile.client;

import com.roottherapy.backend.profile.client.dto.ClientProfileResponse;
import com.roottherapy.backend.profile.client.dto.UpdateClientProfileRequest;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientProfileServiceTest {

    @Mock
    private ClientProfileRepository clientProfileRepository;

    private ClientProfileService clientProfileService;

    @BeforeEach
    void setUp() {
        clientProfileService =
                new ClientProfileService(clientProfileRepository);
    }

    @Test
    @DisplayName(
            "JUNIT-CLIENT-001: Existing client profile returns response DTO"
    )
    void getCurrentProfile_existingProfile_returnsDto() {
        // Arrange
        User client = createUser(UserRole.CLIENT);

        ClientProfile profile = new ClientProfile(client);
        profile.setUserId(client.getId());
        profile.setDateOfBirth(LocalDate.of(1995, 4, 12));
        profile.setTherapyGoalsSummary(
                "Reduce anxiety and improve coping skills."
        );
        profile.setPreferredContactMethod(
                PreferredContactMethod.IN_APP
        );

        when(clientProfileRepository.findById(client.getId()))
                .thenReturn(Optional.of(profile));

        // Act
        ClientProfileResponse result =
                clientProfileService.getMyProfile(client);

        // Assert
        assertNotNull(result);
        assertEquals(client.getId(), result.userId());
        assertEquals(
                LocalDate.of(1995, 4, 12),
                result.dateOfBirth()
        );
        assertEquals(
                "Reduce anxiety and improve coping skills.",
                result.therapyGoalsSummary()
        );
        assertEquals(
                PreferredContactMethod.IN_APP,
                result.preferredContactMethod()
        );

        verify(clientProfileRepository).findById(client.getId());
        verify(clientProfileRepository, never())
                .save(any(ClientProfile.class));
        verifyNoMoreInteractions(clientProfileRepository);
    }

    @Test
    @DisplayName(
            "JUNIT-CLIENT-002: Missing client profile is created automatically"
    )
    void getCurrentProfile_missingProfile_createsProfileAndReturnsDto() {
        // Arrange
        User client = createUser(UserRole.CLIENT);

        when(clientProfileRepository.findById(client.getId()))
                .thenReturn(Optional.empty());

        when(clientProfileRepository.save(any(ClientProfile.class)))
                .thenAnswer(invocation -> {
                    ClientProfile profile = invocation.getArgument(0);
                    profile.setUserId(profile.getUser().getId());
                    return profile;
                });

        // Act
        ClientProfileResponse result =
                clientProfileService.getMyProfile(client);

        // Assert
        ArgumentCaptor<ClientProfile> profileCaptor =
                ArgumentCaptor.forClass(ClientProfile.class);

        verify(clientProfileRepository).save(profileCaptor.capture());

        ClientProfile savedProfile = profileCaptor.getValue();

        assertSame(client, savedProfile.getUser());
        assertEquals(client.getId(), result.userId());

        verify(clientProfileRepository).findById(client.getId());
        verifyNoMoreInteractions(clientProfileRepository);
    }

    @Test
    @DisplayName(
            "JUNIT-CLIENT-003: Valid update persists editable profile fields"
    )
    void updateProfile_validData_persistsChanges() {
        // Arrange
        User client = createUser(UserRole.CLIENT);

        ClientProfile profile = new ClientProfile(client);
        profile.setUserId(client.getId());
        profile.setDateOfBirth(LocalDate.of(1990, 1, 1));
        profile.setTherapyGoalsSummary("Old goals");
        profile.setPreferredContactMethod(PreferredContactMethod.EMAIL);

        UpdateClientProfileRequest request =
                new UpdateClientProfileRequest(
                        LocalDate.of(1992, 8, 20),
                        "I want to manage stress better.",
                        PreferredContactMethod.PHONE
                );

        when(clientProfileRepository.findById(client.getId()))
                .thenReturn(Optional.of(profile));

        when(clientProfileRepository.save(profile))
                .thenReturn(profile);

        // Act
        ClientProfileResponse result =
                clientProfileService.updateMyProfile(
                        client,
                        request
                );

        // Assert
        assertEquals(
                LocalDate.of(1992, 8, 20),
                profile.getDateOfBirth()
        );
        assertEquals(
                "I want to manage stress better.",
                profile.getTherapyGoalsSummary()
        );
        assertEquals(
                PreferredContactMethod.PHONE,
                profile.getPreferredContactMethod()
        );

        assertEquals(client.getId(), result.userId());
        assertEquals(
                LocalDate.of(1992, 8, 20),
                result.dateOfBirth()
        );
        assertEquals(
                "I want to manage stress better.",
                result.therapyGoalsSummary()
        );
        assertEquals(
                PreferredContactMethod.PHONE,
                result.preferredContactMethod()
        );

        verify(clientProfileRepository).findById(client.getId());
        verify(clientProfileRepository).save(profile);
        verifyNoMoreInteractions(clientProfileRepository);
    }

    @Test
    @DisplayName(
            "JUNIT-CLIENT-005: Non-client user cannot access client profile"
    )
    void updateProfile_nonClientUser_throwsForbiddenException() {
        // Arrange
        User therapist = createUser(UserRole.THERAPIST);

        UpdateClientProfileRequest request =
                new UpdateClientProfileRequest(
                        LocalDate.of(1992, 8, 20),
                        "Invalid role update attempt.",
                        PreferredContactMethod.EMAIL
                );

        // Act
        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> clientProfileService.updateMyProfile(
                        therapist,
                        request
                )
        );

        // Assert
        assertEquals(
                "Only clients have access the client profiles!",
                exception.getMessage()
        );

        verify(clientProfileRepository, never())
                .findById(any());
        verify(clientProfileRepository, never())
                .save(any(ClientProfile.class));
        verifyNoMoreInteractions(clientProfileRepository);
    }

    private User createUser(UserRole role) {
        User user = new User(
                UUID.randomUUID() + "@example.com",
                "encoded-password",
                "Test",
                "User",
                role
        );
        user.setId(UUID.randomUUID());
        return user;
    }
}
