package ma.farmsense.service;

import ma.farmsense.dto.auth.AuthResponse;
import ma.farmsense.dto.auth.LoginRequest;
import ma.farmsense.dto.auth.RegisterRequest;
import ma.farmsense.entity.User;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.UserRepository;
import ma.farmsense.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_ShouldCreateUser_WhenEmailIsNew() {
        // Arrange
        RegisterRequest req = new RegisterRequest("test@example.com", "password123", "Test User", null, User.Language.EN);

        when(userRepository.existsByEmail(req.email())).thenReturn(false);
        when(passwordEncoder.encode(req.password())).thenReturn("hashed_password");
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("access_token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh_token");

        // Act
        AuthResponse res = authService.register(req);

        // Assert
        assertNotNull(res);
        assertEquals("test@example.com", res.email());
        assertEquals("access_token", res.accessToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        RegisterRequest req = new RegisterRequest("existing@example.com", null, null, null, null);

        when(userRepository.existsByEmail(req.email())).thenReturn(true);

        // Act & Assert
        assertThrows(AppException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldReturnResponse_WhenCredentialsAreValid() {
        // Arrange
        LoginRequest req = new LoginRequest("test@example.com", "password123");
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setName("Test User");

        when(userRepository.findByEmail(req.email())).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("access_token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh_token");

        // Act
        AuthResponse res = authService.login(req);

        // Assert
        assertNotNull(res);
        assertEquals("test@example.com", res.email());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void refresh_ShouldReturnNewTokens_WhenRefreshTokenIsValid() {
        // Arrange
        String refreshToken = "valid_refresh_token";
        User user = new User();
        user.setEmail("test@example.com");

        when(jwtService.isRefreshToken(refreshToken)).thenReturn(true);
        when(jwtService.extractUsername(refreshToken)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("new_access_token");
        when(jwtService.generateRefreshToken(user)).thenReturn("new_refresh_token");

        // Act
        AuthResponse res = authService.refresh(refreshToken);

        // Assert
        assertNotNull(res);
        assertEquals("new_access_token", res.accessToken());
    }
}
