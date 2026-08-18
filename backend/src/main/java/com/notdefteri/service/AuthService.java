package com.notdefteri.service;

import com.notdefteri.domain.User;
import com.notdefteri.dto.AuthResponse;
import com.notdefteri.dto.LoginRequest;
import com.notdefteri.dto.RegisterRequest;
import com.notdefteri.exception.EmailAlreadyRegisteredException;
import com.notdefteri.repository.UserRepository;
import com.notdefteri.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyRegisteredException(email);
        }
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setDisplayName(request.displayName());
        user = userRepository.saveAndFlush(user);
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new BadCredentialsException("E-posta veya şifre hatalı"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("E-posta veya şifre hatalı");
        }
        return toResponse(user);
    }

    private AuthResponse toResponse(User user) {
        return new AuthResponse(jwtService.generateToken(user), user.getId(), user.getEmail(), user.getDisplayName());
    }
}
