package com.travelplanner.service;

import com.travelplanner.dto.LoginRequest;
import com.travelplanner.dto.UserRegisterRequest;
import com.travelplanner.dto.AuthResponse;
import com.travelplanner.exception.UnauthorizedException;
import com.travelplanner.model.User;
import com.travelplanner.repository.UserRepository;
import com.travelplanner.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication Service for handling user login and registration.
 */
@Service
@Slf4j
@Transactional
public class AuthService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Login user with username and password.
     *
     * @param request the login request
     * @return authentication response with JWT token
     * @throws UnauthorizedException if credentials are invalid
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());
        
        if (!userService.authenticate(request.getUsername(), request.getPassword())) {
            log.warn("Login failed for user: {}", request.getUsername());
            throw new UnauthorizedException("Invalid username or password");
        }
        
        User authenticatedUser = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        String token = jwtTokenProvider.generateToken(authenticatedUser);
        
        log.info("User logged in successfully: {}", request.getUsername());
        return AuthResponse.fromUser(authenticatedUser, token);
    }
    
    /**
     * Register a new user.
     *
     * @param request the registration request
     * @return authentication response with JWT token
     */
    public AuthResponse register(UserRegisterRequest request) {
        log.info("Registration attempt for user: {}", request.getUsername());
        
        User newUser = userService.registerUser(request);
        String token = jwtTokenProvider.generateToken(newUser);
        
        log.info("User registered successfully: {}", request.getUsername());
        return AuthResponse.fromUser(newUser, token);
    }
    
    /**
     * Refresh JWT token.
     *
     * @param token the current JWT token
     * @return authentication response with new JWT token
     * @throws UnauthorizedException if token is invalid
     */
    public AuthResponse refreshToken(String token) {
        log.debug("Refreshing token");
        
        if (!jwtTokenProvider.validateToken(token)) {
            log.warn("Invalid token provided for refresh");
            throw new UnauthorizedException("Invalid token");
        }
        
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        if (userId == null) {
            log.warn("Could not extract userId from token");
            throw new UnauthorizedException("Invalid token");
        }
        
        User user = userService.getUserById(userId);
        String newToken = jwtTokenProvider.generateToken(user);
        
        log.info("Token refreshed for user id: {}", userId);
        return AuthResponse.fromUser(user, newToken);
    }
    
    /**
     * Logout user (token invalidation would be handled by client-side deletion).
     * This is mainly a placeholder for future token blacklist implementation.
     *
     * @param token the JWT token to invalidate
     */
    public void logout(String token) {
        log.debug("Logout request received");
        // TODO: Implement token blacklist for server-side logout
    }
    
}
