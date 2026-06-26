package com.roottherapy.backend.auth;


import com.roottherapy.backend.auth.dto.LoginRequest;
import com.roottherapy.backend.auth.dto.RegisterClientRequest;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRole;
import com.roottherapy.backend.users.UserService;
import com.roottherapy.backend.users.dto.UserResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.roottherapy.backend.security.CustomUserDetails;


// service class logic for authenticating a new client
@Service
@Transactional
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager){
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public UserResponse registerClient(RegisterClientRequest request){
        if(userService.emailExists(request.email())){
            throw new IllegalArgumentException("This Email already exists!");
        }

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User(
                request.email(),
                passwordHash,
                request.firstName(),
                request.lastName(),
                UserRole.CLIENT
        );

        user.setPhoneNumber(request.phoneNumber());
        User savedUser = userService.save(user);
        return UserResponse.from(savedUser);
    }

    public UserResponse login(LoginRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return UserResponse.from(userDetails.getUser());
    }
}
