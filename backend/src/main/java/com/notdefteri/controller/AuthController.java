package com.notdefteri.controller;

import com.notdefteri.dto.AuthResponse;
import com.notdefteri.dto.LoginRequest;
import com.notdefteri.dto.RegisterRequest;
import com.notdefteri.exception.NotFoundException;
import com.notdefteri.repository.UserRepository;
import com.notdefteri.security.CurrentUser;
import com.notdefteri.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;

    public AuthController(AuthService authService, UserRepository userRepository, CurrentUser currentUser) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.currentUser = currentUser;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public AuthResponse me() {
        var user = userRepository.findById(currentUser.id())
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı"));
        return new AuthResponse(null, user.getId(), user.getEmail(), user.getDisplayName());
    }
}
