package com.petymate.service;

import com.petymate.dto.AuthDto;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {
    AuthDto.AuthResponse register(AuthDto.RegisterRequest request);
    AuthDto.AuthResponse login(AuthDto.LoginRequest request);
    AuthDto.TokenResponse refreshToken(AuthDto.RefreshRequest request);
    void logout(String email);
    AuthDto.UserDto getCurrentUser(String email);
    AuthDto.UserDto updateProfile(String email, AuthDto.UpdateProfileRequest request);
    AuthDto.UserDto uploadPhoto(String email, MultipartFile file);
}
