package com.petymate.controller;

import com.petymate.dto.ApiResponse;
import com.petymate.dto.AuthDto;
import com.petymate.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthDto.AuthResponse>> register(
            @Valid @RequestBody AuthDto.RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Registration successful", authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDto.AuthResponse>> login(
            @Valid @RequestBody AuthDto.LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", authService.login(request)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthDto.TokenResponse>> refresh(
            @Valid @RequestBody AuthDto.RefreshRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", authService.refreshToken(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(Authentication auth) {
        authService.logout(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthDto.UserDto>> me(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(authService.getCurrentUser(auth.getName())));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<AuthDto.UserDto>> updateProfile(
            Authentication auth, @Valid @RequestBody AuthDto.UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated", authService.updateProfile(auth.getName(), request)));
    }

    @PostMapping("/me/photo")
    public ResponseEntity<ApiResponse<AuthDto.UserDto>> uploadPhoto(
            Authentication auth, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success("Photo uploaded", authService.uploadPhoto(auth.getName(), file)));
    }
}
