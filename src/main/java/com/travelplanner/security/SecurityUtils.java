package com.travelplanner.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

/**
 * Utility methods for extracting security information from authenticated requests.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static String getUsername(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }

        if (principal instanceof String username && StringUtils.hasText(username)) {
            return username;
        }

        return null;
    }
}