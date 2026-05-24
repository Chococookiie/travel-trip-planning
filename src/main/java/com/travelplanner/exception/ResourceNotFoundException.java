package com.travelplanner.exception;

public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static ResourceNotFoundException userNotFound(String username) {
        return new ResourceNotFoundException("User not found: " + username);
    }
    
    public static ResourceNotFoundException userNotFound(Long id) {
        return new ResourceNotFoundException("User not found with id: " + id);
    }
    
    public static ResourceNotFoundException searchNotFound(Long id) {
        return new ResourceNotFoundException("Search not found with id: " + id);
    }
    
    public static ResourceNotFoundException alertNotFound(Long id) {
        return new ResourceNotFoundException("Alert not found with id: " + id);
    }
}
