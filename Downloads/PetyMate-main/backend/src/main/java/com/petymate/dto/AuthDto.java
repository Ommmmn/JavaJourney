package com.petymate.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class AuthDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank(message = "Name is required")
        @Size(max = 100)
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 150)
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String password;

        @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
        private String phone;

        private String city;
        private String state;
        private String pincode;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "Email is required")
        @Email
        private String email;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class RefreshRequest {
        @NotBlank(message = "Refresh token is required")
        private String refreshToken;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AuthResponse {
        private String accessToken;
        private String refreshToken;
        private UserDto user;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TokenResponse {
        private String accessToken;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UserDto {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String city;
        private String state;
        private String pincode;
        private String profilePhotoUrl;
        private String role;
        private String subscriptionTier;
        private LocalDateTime subscriptionExpiry;
        private Boolean isVerified;
        private LocalDateTime createdAt;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class UpdateProfileRequest {
        @Size(max = 100)
        private String name;
        @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
        private String phone;
        private String city;
        private String state;
        private String pincode;
    }
}
