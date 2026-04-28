package com.klu.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean verified;

    private String registrationOtp;
    private LocalDateTime registrationOtpExpiry;
    private String loginOtp;
    private LocalDateTime loginOtpExpiry;

    @Column(nullable = false)
    private boolean mfaEnabled;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Assignment> createdAssignments = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getRegistrationOtp() {
        return registrationOtp;
    }

    public void setRegistrationOtp(String registrationOtp) {
        this.registrationOtp = registrationOtp;
    }

    public LocalDateTime getRegistrationOtpExpiry() {
        return registrationOtpExpiry;
    }

    public void setRegistrationOtpExpiry(LocalDateTime registrationOtpExpiry) {
        this.registrationOtpExpiry = registrationOtpExpiry;
    }

    public String getLoginOtp() {
        return loginOtp;
    }

    public void setLoginOtp(String loginOtp) {
        this.loginOtp = loginOtp;
    }

    public LocalDateTime getLoginOtpExpiry() {
        return loginOtpExpiry;
    }

    public void setLoginOtpExpiry(LocalDateTime loginOtpExpiry) {
        this.loginOtpExpiry = loginOtpExpiry;
    }

    public boolean isMfaEnabled() {
        return mfaEnabled;
    }

    public void setMfaEnabled(boolean mfaEnabled) {
        this.mfaEnabled = mfaEnabled;
    }

    public List<Assignment> getCreatedAssignments() {
        return createdAssignments;
    }

    public void setCreatedAssignments(List<Assignment> createdAssignments) {
        this.createdAssignments = createdAssignments;
    }
}
