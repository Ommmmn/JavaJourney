package com.petymate.service.impl;

import com.petymate.dto.AuthDto;
import com.petymate.entity.User;
import com.petymate.exception.ResourceNotFoundException;
import com.petymate.exception.UnauthorizedException;
import com.petymate.repository.UserRepository;
import com.petymate.security.JwtUtil;
import com.petymate.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${petymate.upload.dir}")
    private String uploadDir;

    @Value("${petymate.base-url}")
    private String baseUrl;

    @Override
    @Transactional
    public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .role(User.Role.USER)
                .subscriptionTier(User.SubscriptionTier.FREE)
                .isVerified(false)
                .isBanned(false)
                .build();

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        user.setRefreshToken(refreshToken);

        userRepository.save(user);
        log.info("New user registered: {}", user.getEmail());

        return AuthDto.AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(mapToUserDto(user))
                .build();
    }

    @Override
    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (user.getIsBanned()) {
            throw new UnauthorizedException("Account is banned. Please contact support.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        log.info("User logged in: {}", user.getEmail());

        return AuthDto.AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(mapToUserDto(user))
                .build();
    }

    @Override
    public AuthDto.TokenResponse refreshToken(AuthDto.RefreshRequest request) {
        if (!jwtUtil.validateToken(request.getRefreshToken())) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        String email = jwtUtil.extractEmail(request.getRefreshToken());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (!request.getRefreshToken().equals(user.getRefreshToken())) {
            throw new UnauthorizedException("Refresh token mismatch");
        }

        String newAccessToken = jwtUtil.generateAccessToken(email, user.getRole().name());
        return AuthDto.TokenResponse.builder().accessToken(newAccessToken).build();
    }

    @Override
    @Transactional
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setRefreshToken(null);
        userRepository.save(user);
        log.info("User logged out: {}", email);
    }

    @Override
    public AuthDto.UserDto getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToUserDto(user);
    }

    @Override
    @Transactional
    public AuthDto.UserDto updateProfile(String email, AuthDto.UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getState() != null) user.setState(request.getState());
        if (request.getPincode() != null) user.setPincode(request.getPincode());

        userRepository.save(user);
        log.info("Profile updated: {}", email);
        return mapToUserDto(user);
    }

    @Override
    @Transactional
    public AuthDto.UserDto uploadPhoto(String email, MultipartFile file) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        try {
            String filename = "user_" + user.getId() + "_" + System.currentTimeMillis() +
                    getFileExtension(file.getOriginalFilename());
            Path uploadPath = Paths.get(uploadDir, "profiles");
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(filename);
            Files.write(filePath, file.getBytes());

            String photoUrl = baseUrl + "/files/profiles/" + filename;
            user.setProfilePhotoUrl(photoUrl);
            userRepository.save(user);

            log.info("Profile photo uploaded for user: {}", email);
            return mapToUserDto(user);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload photo", e);
        }
    }

    private AuthDto.UserDto mapToUserDto(User user) {
        return AuthDto.UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .city(user.getCity())
                .state(user.getState())
                .pincode(user.getPincode())
                .profilePhotoUrl(user.getProfilePhotoUrl())
                .role(user.getRole().name())
                .subscriptionTier(user.getSubscriptionTier().name())
                .subscriptionExpiry(user.getSubscriptionExpiry())
                .isVerified(user.getIsVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return ".jpg";
    }
}
