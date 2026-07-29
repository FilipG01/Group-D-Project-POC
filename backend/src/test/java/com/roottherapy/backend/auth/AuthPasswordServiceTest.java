package com.roottherapy.backend.auth;
import com.roottherapy.backend.auth.dto.ChangePasswordRequest;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRole;
import com.roottherapy.backend.users.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthPasswordServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;


//JUNIT-PASS-001: Correct current password updates encoded password
@Test
void changePassword_correctCurrentPassword_updatesEncodedPassword() {

    User user = new User(
            "test@test.com",
            "oldEncodedPassword",
            "Test",
            "User",
            UserRole.CLIENT
    );

    ChangePasswordRequest request = new ChangePasswordRequest(
            "oldPassword123",
            "newPassword123",
            "newPassword123"
    );

    when(passwordEncoder.matches(
            "oldPassword123",
            "oldEncodedPassword"
    )).thenReturn(true);

    when(passwordEncoder.matches(
            "newPassword123",
            "oldEncodedPassword"
    )).thenReturn(false);

    when(passwordEncoder.encode("newPassword123"))
            .thenReturn("newEncodedPassword");


    authService.changePassword(user, request);


    assertEquals(
            "newEncodedPassword",
            user.getPasswordHash()
    );

    verify(passwordEncoder)
            .encode("newPassword123");

    verify(userService)
            .save(user);
        }


// JUNIT-PASS-002: Incorrect current password rejects password change
@Test
void changePassword_incorrectCurrentPassword_throwsException() {

    User user = new User(
            "test@test.com",
            "oldEncodedPassword",
            "Test",
            "User",
            UserRole.CLIENT
    );

    ChangePasswordRequest request = new ChangePasswordRequest(
            "wrongPassword123",
            "newPassword123",
            "newPassword123"
    );

    //simulating user running wrong pass
    when(passwordEncoder.matches(
            "wrongPassword123",
            "oldEncodedPassword"
    )).thenReturn(false);

    //checks rejection
    assertThrows(
            IllegalArgumentException.class,
            () -> authService.changePassword(user, request)
    );


    verify(passwordEncoder, never())
            .encode(anyString());
    //check no updates made to db
    verify(userService, never())
            .save(any(User.class));
}

//JUNIT-PASS-003: Reject reuse of the current password
@Test
void changePassword_sameAsCurrentPassword_rejectsReuse() {

    User user = new User(
            "test@test.com",
            "oldEncodedPassword",
            "Test",
            "User",
            UserRole.CLIENT
    );

    ChangePasswordRequest request = new ChangePasswordRequest(
            "oldPassword123",
            "oldPassword123",
            "oldPassword123"
    );

    //current pw is correct
    when(passwordEncoder.matches(
            "oldPassword123",
            "oldEncodedPassword"
    )).thenReturn(true);

    //new pw is the same as the existing pw
    when(passwordEncoder.matches(
            "oldPassword123",
            "oldEncodedPassword"
    )).thenReturn(true);

    assertThrows(
            IllegalArgumentException.class,
            () -> authService.changePassword(user, request)
    );

    verify(passwordEncoder, never())
            .encode(anyString());

    verify(userService, never())
            .save(any(User.class));
}

//JUNIT-PASS-005: Successful password change replaces the old password
@Test
void changePassword_success_oldPasswordNoLongerMatches() {

    User user = new User(
            "test@test.com",
            "oldEncodedPassword",
            "Test",
            "User",
            UserRole.CLIENT
    );

    ChangePasswordRequest request = new ChangePasswordRequest(
            "oldPassword123",
            "newPassword123",
            "newPassword123"
    );

    when(passwordEncoder.matches(
            "oldPassword123",
            "oldEncodedPassword"
    )).thenReturn(true);

    when(passwordEncoder.matches(
            "newPassword123",
            "oldEncodedPassword"
    )).thenReturn(false);

    when(passwordEncoder.encode(
            "newPassword123"
    )).thenReturn("newEncodedPassword");

    authService.changePassword(user, request);

    assertNotEquals(
            "oldEncodedPassword",
            user.getPasswordHash()
    );

    assertEquals(
            "newEncodedPassword",
            user.getPasswordHash()
    );

    verify(userService).save(user);
}
}