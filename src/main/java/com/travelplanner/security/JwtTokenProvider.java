package com.travelplanner.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.travelplanner.model.User;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT Token Provider for generating and validating JWT tokens.
 * Handles token creation with user claims and token validation.
 */
@Component
@Slf4j
public class JwtTokenProvider {
    
    @Value("${spring.security.jwt.secret}")
    private String jwtSecret;
    
    @Value("${spring.security.jwt.expiration}")
    private long jwtExpirationMs;
    
    /**
     * Generate JWT token for the given user.
     *
     * @param user the user entity
     * @return JWT token string
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        String token = Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
        
        log.debug("Generated JWT token for user: {}", user.getUsername());
        return token;
    }
    
    /**
     * Validate JWT token.
     *
     * @param token the JWT token
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            log.debug("JWT token is valid");
            return true;
        } catch (io.jsonwebtoken.security.SecurityException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (io.jsonwebtoken.MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (io.jsonwebtoken.UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }
    
    /**
     * Get user ID from JWT token.
     *
     * @param token the JWT token
     * @return user ID as Long
     */
    public Long getUserIdFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return Long.parseLong(claims.getSubject());
        } catch (Exception ex) {
            log.error("Error extracting userId from token: {}", ex.getMessage());
            return null;
        }
    }
    
    /**
     * Get username from JWT token.
     *
     * @param token the JWT token
     * @return username string
     */
    public String getUsernameFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return (String) claims.get("username");
        } catch (Exception ex) {
            log.error("Error extracting username from token: {}", ex.getMessage());
            return null;
        }
    }
}
