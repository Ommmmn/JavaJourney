package com.petymate.service;

import com.petymate.dto.AuthDto;
import com.petymate.entity.User;
import com.petymate.exception.UnauthorizedException;
import com.petymate.repository.UserRepository;
import com.petymate.security.JwtUtil;
import com.petymate.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @InjectMocks private AuthServiceImpl authService;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(authService, "uploadDir", "./uploads");
        ReflectionTestUtils.setField(authService, "baseUrl", "http://localhost:8080");
    }

    @Test @DisplayName("Register: success")
    void register_Success() {
        AuthDto.RegisterRequest req = new AuthDto.RegisterRequest("John", "john@test.com", "password123", "9876543210", "Mumbai", "MH", "400001");
        when(userRepository.existsByEmail("john@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(jwtUtil.generateAccessToken(anyString(), anyString())).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(anyString())).thenReturn("refresh-token");
        when(userRepository.save(any(User.class))).thenAnswer(i -> { User u = i.getArgument(0); u.setId(1L); return u; });

        AuthDto.AuthResponse resp = authService.register(req);

        assertNotNull(resp);
        assertEquals("access-token", resp.getAccessToken());
        assertEquals("refresh-token", resp.getRefreshToken());
        assertEquals("John", resp.getUser().getName());
        verify(userRepository).save(any(User.class));
    }

    @Test @DisplayName("Register: duplicate email → exception")
    void register_DuplicateEmail() {
        AuthDto.RegisterRequest req = new AuthDto.RegisterRequest("John", "john@test.com", "pass", null, null, null, null);
        when(userRepository.existsByEmail("john@test.com")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> authService.register(req));
    }

    @Test @DisplayName("Login: success")
    void login_Success() {
        AuthDto.LoginRequest req = new AuthDto.LoginRequest("john@test.com", "password123");
        User user = User.builder().id(1L).email("john@test.com").name("John").passwordHash("hashed")
                .role(User.Role.USER).subscriptionTier(User.SubscriptionTier.FREE).isBanned(false).build();
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);
        when(jwtUtil.generateAccessToken(anyString(), anyString())).thenReturn("access");
        when(jwtUtil.generateRefreshToken(anyString())).thenReturn("refresh");
        when(userRepository.save(any(User.class))).thenReturn(user);

        AuthDto.AuthResponse resp = authService.login(req);

        assertNotNull(resp);
        assertEquals("John", resp.getUser().getName());
    }

    @Test @DisplayName("Login: wrong password → exception")
    void login_WrongPassword() {
        AuthDto.LoginRequest req = new AuthDto.LoginRequest("john@test.com", "wrong");
        User user = User.builder().email("john@test.com").passwordHash("hashed").isBanned(false).build();
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);
        assertThrows(UnauthorizedException.class, () -> authService.login(req));
    }

    @Test @DisplayName("Login: banned user → exception")
    void login_BannedUser() {
        AuthDto.LoginRequest req = new AuthDto.LoginRequest("john@test.com", "pass");
        User user = User.builder().email("john@test.com").isBanned(true).build();
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));
        assertThrows(UnauthorizedException.class, () -> authService.login(req));
    }

    @Test @DisplayName("Refresh token: success")
    void refreshToken_Success() {
        AuthDto.RefreshRequest req = new AuthDto.RefreshRequest("valid-refresh-token");
        User user = User.builder().email("john@test.com").refreshToken("valid-refresh-token")
                .role(User.Role.USER).build();
        when(jwtUtil.validateToken("valid-refresh-token")).thenReturn(true);
        when(jwtUtil.extractEmail("valid-refresh-token")).thenReturn("john@test.com");
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken("john@test.com", "USER")).thenReturn("new-access");

        AuthDto.TokenResponse resp = authService.refreshToken(req);
        assertEquals("new-access", resp.getAccessToken());
    }

    @Test @DisplayName("Update profile: success")
    void updateProfile_Success() {
        User user = User.builder().id(1L).email("john@test.com").name("John").role(User.Role.USER)
                .subscriptionTier(User.SubscriptionTier.FREE).build();
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        AuthDto.UpdateProfileRequest req = new AuthDto.UpdateProfileRequest("John Updated", "9876543210", "Delhi", "DL", "110001");
        AuthDto.UserDto dto = authService.updateProfile("john@test.com", req);

        assertEquals("John Updated", dto.getName());
    }
}
