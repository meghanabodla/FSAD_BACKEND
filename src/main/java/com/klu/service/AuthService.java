package com.klu.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.klu.dto.auth.AuthResponse;
import com.klu.dto.auth.LoginRequest;
import com.klu.dto.auth.RegisterRequest;
import com.klu.model.User;
import com.klu.repository.UserRepository;
import com.klu.security.CustomUserDetailsService;
import com.klu.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    public String register(RegisterRequest request) {

        String email = normalizeEmail(request.getEmail());
        String password = normalizePassword(request.getPassword());

        Optional<User> existingUser = userRepository.findByEmailIgnoreCase(email);

        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(request.getRole());
        user.setVerified(true);
        user.setMfaEnabled(false);

        userRepository.save(user);

        return "Registration successful. Please login.";
    }

    public AuthResponse login(LoginRequest request) {

        String email = normalizeEmail(request.getEmail());
        String password = normalizePassword(request.getPassword());

        User user = getUserByEmail(email);
        ensureVerified(user);

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return buildAuthenticatedResponse(user, "Login successful.");
    }

    public AuthResponse currentUser(String email) {

        User user = getUserByEmail(normalizeEmail(email));
        ensureVerified(user);

        return new AuthResponse(
                null,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                true,
                "Current user"
        );
    }

    private AuthResponse buildAuthenticatedResponse(User user, String message) {

        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        String token = jwtService.generateToken(
                userDetails,
                Map.of(
                        "role", user.getRole().name(),
                        "name", user.getName()
                )
        );

        return new AuthResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                true,
                message
        );
    }

    private void ensureVerified(User user) {
        if (!user.isVerified()) {
            user.setVerified(true);
            userRepository.save(user);
        }
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private String normalizePassword(String password) {
        return password == null ? null : password.trim();
    }
}
