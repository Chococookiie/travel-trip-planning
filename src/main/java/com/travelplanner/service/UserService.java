package com.travelplanner.service;

import com.travelplanner.dto.UserRegisterRequest;
import com.travelplanner.exception.ResourceNotFoundException;
import com.travelplanner.model.User;
import com.travelplanner.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * User Service for managing user accounts and user-related operations.
 */
@Service
@Slf4j
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Register a new user with the provided registration request.
     *
     * @param request the user registration request
     * @return the created user
     */
    public User registerUser(UserRegisterRequest request) {
        log.info("Registering new user with username: {}", request.getUsername());
        
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username already exists: {}", request.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }
        
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .isActive(true)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());
        return savedUser;
    }
    
    /**
     * Authenticate user with username and password.
     *
     * @param username the username
     * @param password the password
     * @return true if credentials are valid, false otherwise
     */
    public boolean authenticate(String username, String password) {
        log.debug("Authenticating user: {}", username);
        
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
            
            if (passwordMatches && user.getIsActive()) {
                log.debug("User authenticated successfully: {}", username);
                return true;
            }
        }
        
        log.warn("Authentication failed for user: {}", username);
        return false;
    }
    
    /**
     * Get user by ID.
     *
     * @param userId the user ID
     * @return the user
     * @throws ResourceNotFoundException if user not found
     */
    public User getUserById(Long userId) {
        log.debug("Fetching user with id: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", userId);
                    return new ResourceNotFoundException("User not found with id: " + userId);
                });
    }
    
    /**
     * Update user profile information.
     *
     * @param userId the user ID
     * @param updatedUser the updated user information
     * @return the updated user
     */
    public User updateUserProfile(Long userId, User updatedUser) {
        log.info("Updating user profile for id: {}", userId);
        
        User user = getUserById(userId);
        
        if (updatedUser.getFirstName() != null) {
            user.setFirstName(updatedUser.getFirstName());
        }
        if (updatedUser.getLastName() != null) {
            user.setLastName(updatedUser.getLastName());
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                log.warn("Email already exists: {}", updatedUser.getEmail());
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(updatedUser.getEmail());
        }
        
        User savedUser = userRepository.save(user);
        log.info("User profile updated successfully for id: {}", userId);
        return savedUser;
    }
    
    /**
     * Delete user account.
     *
     * @param userId the user ID
     */
    public void deleteUser(Long userId) {
        log.info("Deleting user with id: {}", userId);
        
        User user = getUserById(userId);
        userRepository.delete(user);
        
        log.info("User deleted successfully with id: {}", userId);
    }
    
    /**
     * Check if email already exists.
     *
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
