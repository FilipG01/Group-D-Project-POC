package com.roottherapy.backend.auth;


import com.roottherapy.backend.auth.dto.RegisterClientRequest;
import com.roottherapy.backend.users.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
}
