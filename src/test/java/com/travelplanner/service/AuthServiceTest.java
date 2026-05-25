package com.travelplanner.service;

import com.travelplanner.dto.AuthResponse;
import com.travelplanner.dto.LoginRequest;
import com.travelplanner.dto.UserRegisterRequest;
import com.travelplanner.model.User;
import com.travelplanner.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginRequest loginRequest;
    private UserRegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("hashed_password")
                .firstName("Test")
                .lastName("User")
                .build();

        loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();

        registerRequest = UserRegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .build();
    }

    @Test
    void testLogin_Success() {
        String token = "jwt_token_123";
        when(userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword()))
                .thenReturn(testUser);
        when(jwtTokenProvider.generateToken(testUser.getId())).thenReturn(token);

        AuthResponse result = authService.login(loginRequest);

        assertNotNull(result);
        assertEquals(token, result.getToken());
        assertNotNull(result.getUser());
        assertEquals(testUser.getId(), result.getUser().getId());
    }

    @Test
    void testRegister_Success() {
        String token = "jwt_token_123";
        when(userService.registerUser(registerRequest)).thenReturn(testUser);
        when(jwtTokenProvider.generateToken(testUser.getId())).thenReturn(token);

        AuthResponse result = authService.register(registerRequest);

        assertNotNull(result);
        assertEquals(token, result.getToken());
        assertNotNull(result.getUser());
        assertEquals(testUser.getId(), result.getUser().getId());
        verify(userService, times(1)).registerUser(registerRequest);
    }

    @Test
    void testRefreshToken_Success() {
        String oldToken = "old_jwt_token";
        String newToken = "new_jwt_token";
        
        when(jwtTokenProvider.validateToken(oldToken)).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken(oldToken)).thenReturn(testUser.getId());
        when(userService.getUserById(anyLong())).thenReturn(testUser);
        when(jwtTokenProvider.generateToken(testUser.getId())).thenReturn(newToken);

        AuthResponse result = authService.refreshToken(oldToken);

        assertNotNull(result);
        assertEquals(newToken, result.getToken());
        verify(jwtTokenProvider, times(1)).validateToken(oldToken);
        verify(jwtTokenProvider, times(1)).generateToken(testUser.getId());
    }

    @Test
    void testRefreshToken_InvalidToken() {
        String invalidToken = "invalid_token";
        
        when(jwtTokenProvider.validateToken(invalidToken)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.refreshToken(invalidToken));
    }
}
