package com.travelplanner.controller;

import com.travelplanner.model.PriceAlert;
import com.travelplanner.service.PriceAlertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
        
        log.info("Creating price alert for search id: {}, target price: {}", searchId, targetPrice);
        
        Long userId = extractUserIdFromAuth(authentication);
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
        
        log.debug("Fetching alerts for user, active={}", active);
        
        Long userId = extractUserIdFromAuth(authentication);
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
            @RequestParam @NotNull @Positive BigDecimal newTargetPrice) {
        
        log.info("Updating price alert id: {}, new target price: {}", id, newTargetPrice);
        
        PriceAlert updated = priceAlertService.updateAlert(id, newTargetPrice);
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
    public ResponseEntity<?> deleteAlert(@PathVariable @NotNull Long id) {
        log.info("Deleting price alert with id: {}", id);
        priceAlertService.deleteAlert(id);
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
        // TODO: Extract user ID from JWT claims or principal
        return 1L; // Placeholder
    }
}
