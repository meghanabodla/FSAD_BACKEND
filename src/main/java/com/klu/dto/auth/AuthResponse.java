package com.klu.dto.auth;

import com.klu.model.Role;

public class AuthResponse {
    private String token;
    private Long userId;
    private String name;
    private String email;
    private Role role;
    private boolean verified;
    private boolean mfaRequired;
    private String message;

    public AuthResponse(String token, Long userId, String name, String email, Role role, boolean verified,
            boolean mfaRequired, String message) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.verified = verified;
        this.mfaRequired = mfaRequired;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public boolean isVerified() {
        return verified;
    }

    public boolean isMfaRequired() {
        return mfaRequired;
    }

    public String getMessage() {
        return message;
    }
}
