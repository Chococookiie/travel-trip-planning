package com.travelplanner.service;

import com.travelplanner.exception.ResourceNotFoundException;
import com.travelplanner.model.PriceAlert;
import com.travelplanner.model.SearchHistory;
import com.travelplanner.model.User;
import com.travelplanner.repository.PriceAlertRepository;
import com.travelplanner.repository.SearchHistoryRepository;
import com.travelplanner.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Price Alert Service for managing price alerts and checking prices.
 */
@Service
@Slf4j
@Transactional
public class PriceAlertService {
    
    @Autowired
    private PriceAlertRepository priceAlertRepository;
    
    @Autowired
    private SearchHistoryRepository searchHistoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Create a new price alert.
     *
     * @param userId the user ID
     * @param searchId the search history ID
     * @param searchType the search type (flight or train)
     * @param targetPrice the target price threshold
     * @return the created price alert
     */
    public PriceAlert createAlert(Long userId, Long searchId, String searchType, BigDecimal targetPrice) {
        log.info("Creating price alert for user id: {}, search id: {}, target price: {}", 
                userId, searchId, targetPrice);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        SearchHistory search = searchHistoryRepository.findById(searchId)
                .orElseThrow(() -> new ResourceNotFoundException("Search not found"));
        
        PriceAlert alert = PriceAlert.builder()
                .user(user)
                .search(search)
                .searchType(searchType)
                .targetPrice(targetPrice)
                .isActive(true)
                .alertTriggered(false)
                .build();
        
        PriceAlert saved = priceAlertRepository.save(alert);
        log.info("Price alert created with id: {}", saved.getId());
        return saved;
    }
    
    /**
     * Update price alert threshold.
     *
     * @param alertId the alert ID
     * @param newTargetPrice the new target price
     * @return the updated price alert
     */
    public PriceAlert updateAlert(Long userId, Long alertId, BigDecimal newTargetPrice) {
        log.info("Updating price alert id: {}, new target price: {}", alertId, newTargetPrice);
        
        PriceAlert alert = getAlertByIdForUser(userId, alertId);
        alert.setTargetPrice(newTargetPrice);
        PriceAlert updated = priceAlertRepository.save(alert);
        
        log.info("Price alert updated successfully");
        return updated;
    }
    
    /**
     * Delete price alert.
     *
     * @param userId the user ID
     * @param alertId the alert ID
     */
    public void deleteAlert(Long userId, Long alertId) {
        log.info("Deleting price alert with id: {}", alertId);
        
        PriceAlert alert = getAlertByIdForUser(userId, alertId);
        priceAlertRepository.delete(alert);
        log.info("Price alert deleted successfully");
    }

    public PriceAlert getAlertByIdForUser(Long userId, Long alertId) {
        PriceAlert alert = priceAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found"));

        if (!alert.getUser().getId().equals(userId)) {
            log.warn("User {} attempted to access alert {} owned by {}", userId, alertId, alert.getUser().getId());
            throw new ResourceNotFoundException("Alert not found");
        }
        return alert;
    }
    
    /**
     * Get user's price alerts.
     *
     * @param userId the user ID
     * @param activeOnly if true, return only active alerts
     * @return list of price alerts
     */
    public List<PriceAlert> getUserAlerts(Long userId, Boolean activeOnly) {
        log.debug("Fetching alerts for user id: {}, active only: {}", userId, activeOnly);
        
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        
        List<PriceAlert> alerts;
        if (activeOnly != null && activeOnly) {
            alerts = priceAlertRepository.findByUserIdAndIsActiveTrue(userId);
        } else {
            alerts = priceAlertRepository.findByUserId(userId);
        }
        
        return alerts;
    }
    
    /**
     * Scheduled task to check prices and trigger alerts.
     * Runs every hour.
     */
    @Scheduled(fixedDelay = 3600000, initialDelay = 60000) // 1 hour
    public void checkAlerts() {
        log.debug("Running price check for alerts");
        
        List<PriceAlert> activeAlerts = priceAlertRepository.findByIsActiveTrue();
        
        for (PriceAlert alert : activeAlerts) {
            checkAndTriggerAlert(alert);
        }
        
        log.debug("Price check completed");
    }
    
    /**
     * Check individual alert and trigger if condition met.
     *
     * @param alert the price alert to check
     */
    private void checkAndTriggerAlert(PriceAlert alert) {
        log.debug("Checking alert id: {}", alert.getId());
        
        // TODO: Implement actual price checking logic
        // This would involve:
        // 1. Get original search parameters from SearchHistory
        // 2. Call appropriate API (Flight or Train) to get current prices
        // 3. Compare with target price
        // 4. Trigger notification if price is lower
        
        // For now, just logging
        log.debug("Alert check completed for alert id: {}", alert.getId());
    }
}
