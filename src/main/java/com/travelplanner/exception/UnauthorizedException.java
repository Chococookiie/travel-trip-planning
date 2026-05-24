package com.travelplanner.exception;

public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String message) {
        super(message);
    }
    
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static UnauthorizedException invalidCredentials() {
        return new UnauthorizedException("Invalid username or password");
    }
    
    public static UnauthorizedException invalidToken() {
        return new UnauthorizedException("Invalid or expired token");
    }
    
    public static UnauthorizedException userAlreadyExists(String username) {
        return new UnauthorizedException("User already exists: " + username);
    }
}
