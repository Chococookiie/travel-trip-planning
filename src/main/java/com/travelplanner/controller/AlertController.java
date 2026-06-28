package com.travelplanner.controller;

import com.travelplanner.model.PriceAlert;
import com.travelplanner.service.PriceAlertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import com.travelplanner.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

/**
 * Alert Controller for managing price alerts.
 */
@RestController
@RequestMapping("/api/alerts")
@Validated
@Slf4j
public class AlertController {
    
    @Autowired
    private PriceAlertService priceAlertService;
    
    /**
     * Create a new price alert.
     *
     * @param searchId the search history ID
     * @param searchType the search type (flight or train)
     * @param targetPrice the target price threshold
     * @param authentication the current authentication
     * @return created price alert
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PriceAlert> createAlert(
            @RequestParam @NotNull Long searchId,
            @RequestParam @NotNull String searchType,
            @RequestParam @NotNull @Positive BigDecimal targetPrice,
            Authentication authentication) {
        
        Long userId = extractUserIdFromAuth(authentication);
        log.info("Creating price alert for user id={}, search id={}, target price={}", userId, searchId, targetPrice);
        
        PriceAlert alert = priceAlertService.createAlert(userId, searchId, searchType, targetPrice);
        return new ResponseEntity<>(alert, HttpStatus.CREATED);
    }
    
    /**
     * Get user's price alerts.
     *
     * @param active filter by active status (optional)
     * @param authentication the current authentication
     * @return list of price alerts
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PriceAlert>> getAlerts(
            @RequestParam(required = false) Boolean active,
            Authentication authentication) {
        
        Long userId = extractUserIdFromAuth(authentication);
        log.debug("Fetching alerts for user id={}, active={}", userId, active);
        
        List<PriceAlert> alerts = priceAlertService.getUserAlerts(userId, active);
        return ResponseEntity.ok(alerts);
    }
    
    /**
     * Update price alert threshold.
     *
     * @param id the alert ID
     * @param newTargetPrice the new target price
     * @return updated price alert
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PriceAlert> updateAlert(
            @PathVariable @NotNull Long id,
            @RequestParam @NotNull @Positive BigDecimal newTargetPrice,
            Authentication authentication) {
        
        Long userId = extractUserIdFromAuth(authentication);
        log.info("Updating price alert id: {}, new target price: {}", id, newTargetPrice);
        
        PriceAlert updated = priceAlertService.updateAlert(userId, id, newTargetPrice);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * Delete price alert.
     *
     * @param id the alert ID
     * @return success response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteAlert(@PathVariable @NotNull Long id,
                                         Authentication authentication) {
        Long userId = extractUserIdFromAuth(authentication);
        log.info("Deleting price alert with id: {}", id);
        priceAlertService.deleteAlert(userId, id);
        return ResponseEntity.ok().body("Alert deleted successfully");
    }
    
    /**
     * Get alert history.
     *
     * @param id the alert ID
     * @return alert details
     */
    @GetMapping("/{id}/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAlertHistory(@PathVariable @NotNull Long id) {
        log.debug("Fetching alert history for alert id: {}", id);
        
        // TODO: Implement logic to fetch alert trigger history
        // This would require a separate AlertHistory table to track price changes
        
        return ResponseEntity.ok().body("Alert history retrieved successfully");
    }
    
    /**
     * Extract user ID from authentication.
     *
     * @param authentication the current authentication
     * @return user ID or null
     */
    private Long extractUserIdFromAuth(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return null;
        }
        return principal.getId();
    }
}
