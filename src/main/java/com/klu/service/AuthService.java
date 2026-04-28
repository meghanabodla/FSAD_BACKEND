package com.klu.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.klu.dto.auth.AuthResponse;
import com.klu.dto.auth.LoginRequest;
import com.klu.dto.auth.RegisterRequest;
import com.klu.dto.auth.VerifyOtpRequest;
import com.klu.model.Role;
import com.klu.model.User;
import com.klu.repository.UserRepository;
import com.klu.security.CustomUserDetailsService;
import com.klu.security.JwtService;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        EmailService emailService,
        AuthenticationManager authenticationManager,
        CustomUserDetailsService userDetailsService,
        JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    public String register(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());
        String normalizedPassword = normalizePassword(request.getPassword());
        Optional<User> existingUser = userRepository.findByEmailIgnoreCase(normalizedEmail);

        if (existingUser.isPresent() && existingUser.get().isVerified()) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = existingUser.orElseGet(User::new);
        user.setName(request.getName());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(normalizedPassword));
        user.setRole(request.getRole());
        user.setVerified(false);
        user.setMfaEnabled(request.getRole() == Role.TEACHER);
        user.setRegistrationOtp(generateOtp());
        user.setRegistrationOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        return sendRegistrationOtp(user);
    }

    public String verifyRegistrationOtp(VerifyOtpRequest request) {
        User user = getUserByEmail(normalizeEmail(request.getEmail()));
        validateOtp(user.getRegistrationOtp(), user.getRegistrationOtpExpiry(), request.getOtp());
        user.setVerified(true);
        user.setRegistrationOtp(null);
        user.setRegistrationOtpExpiry(null);
        userRepository.save(user);
        return "Account verified successfully.";
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());
        String normalizedPassword = normalizePassword(request.getPassword());
        User user = getUserByEmail(normalizedEmail);
        if (!user.isVerified()) {
            throw new IllegalStateException("Account is not verified");
        }

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getEmail(), normalizedPassword)
        );

        if (user.isMfaEnabled()) {
            user.setLoginOtp(generateOtp());
            user.setLoginOtpExpiry(LocalDateTime.now().plusMinutes(10));
            userRepository.save(user);
            String message = sendTeacherLoginOtp(user);
            return new AuthResponse(null, user.getId(), user.getName(), user.getEmail(), user.getRole(), true, true, message);
        }

        return buildAuthenticatedResponse(user, "Login successful.");
    }

    public AuthResponse verifyLoginOtp(VerifyOtpRequest request) {
        User user = getUserByEmail(normalizeEmail(request.getEmail()));
        validateOtp(user.getLoginOtp(), user.getLoginOtpExpiry(), request.getOtp());
        user.setLoginOtp(null);
        user.setLoginOtpExpiry(null);
        userRepository.save(user);
        return buildAuthenticatedResponse(user, "Teacher MFA login successful.");
    }

    public AuthResponse currentUser(String email) {
        User user = getUserByEmail(normalizeEmail(email));
        return new AuthResponse(null, user.getId(), user.getName(), user.getEmail(), user.getRole(), user.isVerified(),
            false, "Current user");
    }

    private AuthResponse buildAuthenticatedResponse(User user, String message) {
        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails, Map.of("role", user.getRole().name(), "name", user.getName()));
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole(), true, false, message);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private String sendRegistrationOtp(User user) {
        String otp = user.getRegistrationOtp();
        String body = "Your registration OTP is " + otp + ". It expires in 10 minutes.";
        emailService.sendEmail(user.getEmail(), "Assignment Portal Registration OTP", body);
        return "Registration successful. Please verify the OTP sent to email.";
    }

    private String sendTeacherLoginOtp(User user) {
        String otp = user.getLoginOtp();
        String body = "Your teacher MFA OTP is " + otp + ". It expires in 10 minutes.";
        emailService.sendEmail(user.getEmail(), "Assignment Portal Teacher Login OTP", body);
        return "Teacher MFA OTP sent to email.";
    }

    private void validateOtp(String savedOtp, LocalDateTime expiry, String receivedOtp) {
        if (savedOtp == null || expiry == null || LocalDateTime.now().isAfter(expiry)) {
            throw new IllegalStateException("OTP expired");
        }
        if (!savedOtp.equals(receivedOtp)) {
            throw new IllegalArgumentException("Invalid OTP");
        }
    }

    private String generateOtp() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private String normalizePassword(String password) {
        return password == null ? null : password.trim();
    }
}
