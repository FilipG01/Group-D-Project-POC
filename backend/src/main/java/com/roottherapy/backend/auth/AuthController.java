package com.roottherapy.backend.auth;


import com.roottherapy.backend.auth.dto.LoginRequest;
import com.roottherapy.backend.auth.dto.RegisterClientRequest;
import com.roottherapy.backend.security.CustomUserDetails;
import com.roottherapy.backend.users.dto.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register/client")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse registerClient(@Valid @RequestBody RegisterClientRequest request){
        return authService.registerClient(request);
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpReq, HttpServletResponse httpRes){
        return authService.login(request, httpReq, httpRes);
    }

    @GetMapping("/me")
    public UserResponse me(Authentication auth){
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return UserResponse.from(userDetails.getUser());
    }
}
