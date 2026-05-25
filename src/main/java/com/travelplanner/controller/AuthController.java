package com.travelplanner.controller;

import com.travelplanner.dto.LoginRequest;
import com.travelplanner.dto.UserRegisterRequest;
import com.travelplanner.dto.AuthResponse;
import com.travelplanner.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Authentication Controller for user login and registration.
 */
@RestController
@RequestMapping("/api/auth")
@Validated
@Slf4j
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * Register a new user.
     *
     * @param request the registration request
     * @return authentication response with JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        log.info("Registration request for user: {}", request.getUsername());
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Login user.
     *
     * @param request the login request
     * @return authentication response with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for user: {}", request.getUsername());
        AuthResponse response = authService.login(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /**
     * Refresh JWT token.
     *
     * @param request the HTTP request containing Authorization header
     * @return authentication response with new JWT token
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest request) {
        log.debug("Token refresh request");
        String token = extractTokenFromRequest(request);
        AuthResponse response = authService.refreshToken(token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /**
     * Logout user (mainly client-side, this is optional endpoint).
     *
     * @return success response
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        log.debug("Logout request");
        String token = extractTokenFromRequest(request);
        authService.logout(token);
        return ResponseEntity.ok().body("Logged out successfully");
    }
    
    /**
     * Extract JWT token from Authorization header.
     *
     * @param request the HTTP request
     * @return JWT token or null
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
