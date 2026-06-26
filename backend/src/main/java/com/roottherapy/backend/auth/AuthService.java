package com.roottherapy.backend.auth;


import com.roottherapy.backend.auth.dto.LoginRequest;
import com.roottherapy.backend.auth.dto.RegisterClientRequest;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRole;
import com.roottherapy.backend.users.UserService;
import com.roottherapy.backend.users.dto.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
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
    private final SecurityContextRepository securityContextRepo;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, SecurityContextRepository securityContextRepo){
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.securityContextRepo = securityContextRepo;
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

    public UserResponse login(LoginRequest request, HttpServletRequest httpReq, HttpServletResponse httpRes){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )

        );
            // stores the authenticated user in the http session for requests to use later, e.g., "/me"
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
                securityContextRepo.saveContext(context, httpReq, httpRes);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return UserResponse.from(userDetails.getUser());
    }
}
