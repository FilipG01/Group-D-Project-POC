package com.roottherapy.backend.auth;

import com.roottherapy.backend.auth.dto.LoginRequest;
import com.roottherapy.backend.auth.dto.RegisterClientRequest;
import com.roottherapy.backend.security.CustomUserDetails;
import com.roottherapy.backend.users.AccountStatus;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRole;
import com.roottherapy.backend.users.UserService;
import com.roottherapy.backend.users.dto.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private SecurityContextRepository securityContextRepository;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private Authentication authentication;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        authService = new AuthService(
                userService,
                passwordEncoder,
                authenticationManager,
                securityContextRepository
        );
    }

    @Test
    @DisplayName(
            "JUNIT-AUTH-001: Valid client registration creates a client user"
    )
    void register_validClient_createsClientUser() {
        // Arrange
        RegisterClientRequest request = new RegisterClientRequest(
                "client@example.com",
                "password123",
                "Test",
                "Client",
                "0871234567"
        );

        UUID userId = UUID.randomUUID();
        String encodedPassword = "$2a$10$encodedPassword";

        when(userService.emailExists(request.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.password()))
                .thenReturn(encodedPassword);

        when(userService.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(userId);
                    return user;
                });

        // Act
        UserResponse result = authService.registerClient(request);

        // Assert
        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userService).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals("client@example.com", savedUser.getEmail());
        assertEquals("Test", savedUser.getFirstName());
        assertEquals("Client", savedUser.getLastName());
        assertEquals("0871234567", savedUser.getPhoneNumber());
        assertEquals(UserRole.CLIENT, savedUser.getRole());
        assertEquals(AccountStatus.ACTIVE, savedUser.getAccountStatus());
        assertEquals(encodedPassword, savedUser.getPasswordHash());
        assertNotEquals(request.password(), savedUser.getPasswordHash());

        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("client@example.com", result.email());
        assertEquals("Test", result.firstName());
        assertEquals("Client", result.lastName());
        assertEquals("0871234567", result.phoneNumber());
        assertEquals(UserRole.CLIENT, result.role());
        assertEquals(AccountStatus.ACTIVE, result.accountStatus());

        verify(userService).emailExists(request.email());
        verify(passwordEncoder).encode(request.password());

        verifyNoMoreInteractions(
                userService,
                passwordEncoder,
                authenticationManager,
                securityContextRepository
        );
    }

    @Test
    @DisplayName(
            "JUNIT-AUTH-002: Duplicate client email rejects registration"
    )
    void registerClient_existingEmail_throwsIllegalArgumentException() {
        // Arrange
        RegisterClientRequest request = new RegisterClientRequest(
                "client@example.com",
                "password123",
                "Test",
                "Client",
                "0871234567"
        );

        when(userService.emailExists(request.email()))
                .thenReturn(true);

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.registerClient(request)
        );

        // Assert
        assertEquals(
                "This Email already exists!",
                exception.getMessage()
        );

        verify(userService).emailExists(request.email());
        verify(passwordEncoder, never()).encode(any());
        verify(userService, never()).save(any(User.class));

        verifyNoMoreInteractions(
                userService,
                passwordEncoder,
                authenticationManager,
                securityContextRepository
        );
    }

    @Test
    @DisplayName(
            "JUNIT-AUTH-005: Valid registration encodes password before saving"
    )
    void register_validPassword_encodesBeforeSaving() {
        // Arrange
        RegisterClientRequest request = new RegisterClientRequest(
                "client@example.com",
                "password123",
                "Test",
                "Client",
                null
        );

        when(userService.emailExists(request.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.password()))
                .thenReturn("encoded-password");

        when(userService.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(UUID.randomUUID());
                    return user;
                });

        // Act
        authService.registerClient(request);

        // Assert
        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(passwordEncoder).encode("password123");
        verify(userService).save(userCaptor.capture());

        assertEquals(
                "encoded-password",
                userCaptor.getValue().getPasswordHash()
        );

        assertFalse(
                "password123".equals(userCaptor.getValue().getPasswordHash())
        );
    }

    @Test
    @DisplayName(
            "JUNIT-AUTH-006: Valid registration assigns CLIENT role"
    )
    void register_validClient_assignsClientRole() {
        // Arrange
        RegisterClientRequest request = new RegisterClientRequest(
                "client@example.com",
                "password123",
                "Test",
                "Client",
                null
        );

        when(userService.emailExists(request.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.password()))
                .thenReturn("encoded-password");

        when(userService.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(UUID.randomUUID());
                    return user;
                });

        // Act
        authService.registerClient(request);

        // Assert
        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userService).save(userCaptor.capture());

        assertEquals(
                UserRole.CLIENT,
                userCaptor.getValue().getRole()
        );
    }

    @Test
    @DisplayName(
            "JUNIT-AUTH-007: Valid login stores security context and returns user"
    )
    void login_validCredentials_storesSecurityContextAndReturnsUser() {
        // Arrange
        LoginRequest request = new LoginRequest(
                "client@example.com",
                "password123"
        );

        UUID userId = UUID.randomUUID();
        User user = new User(
                "client@example.com",
                "encoded-password",
                "Test",
                "Client",
                UserRole.CLIENT
        );
        user.setId(userId);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        when(authenticationManager.authenticate(any(
                UsernamePasswordAuthenticationToken.class
        ))).thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        // Act
        UserResponse result = authService.login(
                request,
                httpServletRequest,
                httpServletResponse
        );

        // Assert
        ArgumentCaptor<UsernamePasswordAuthenticationToken>
                authenticationTokenCaptor =
                ArgumentCaptor.forClass(
                        UsernamePasswordAuthenticationToken.class
                );

        verify(authenticationManager)
                .authenticate(authenticationTokenCaptor.capture());

        UsernamePasswordAuthenticationToken token =
                authenticationTokenCaptor.getValue();

        assertEquals("client@example.com", token.getPrincipal());
        assertEquals("password123", token.getCredentials());

        verify(securityContextRepository).saveContext(
                any(),
                same(httpServletRequest),
                same(httpServletResponse)
        );

        assertSame(
                authentication,
                SecurityContextHolder.getContext().getAuthentication()
        );

        assertEquals(userId, result.id());
        assertEquals("client@example.com", result.email());
        assertEquals(UserRole.CLIENT, result.role());
    }
}
