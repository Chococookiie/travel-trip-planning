package com.travelplanner.controller;

import com.travelplanner.service.SearchHistoryService;
import com.travelplanner.service.TrainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Train Controller for train search operations.
 */
@RestController
@RequestMapping("/api/trains")
@Validated
@Slf4j
public class TrainController {
    
    @Autowired
    private TrainService trainService;
    
    @Autowired
    private SearchHistoryService searchHistoryService;
    
    /**
     * Search for trains.
     *
     * @param fromLocation the departure location
     * @param toLocation the arrival location
     * @param departureDate the departure date
     * @param returnDate the return date (optional)
     * @param passengerCount the number of passengers
     * @param authentication the current authentication
     * @return list of train search results
     */
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Map<String, Object>>> searchTrains(
            @RequestParam String fromLocation,
            @RequestParam String toLocation,
            @RequestParam LocalDate departureDate,
            @RequestParam(required = false) LocalDate returnDate,
            @RequestParam Integer passengerCount,
            Authentication authentication) {
        
        log.info("Train search request: from={}, to={}, date={}", fromLocation, toLocation, departureDate);
        
        List<Map<String, Object>> results = trainService.searchTrains(fromLocation, toLocation, departureDate);
        
        // Save to search history
        Long userId = extractUserIdFromAuth(authentication);
        searchHistoryService.saveSearch(userId, "train", fromLocation, toLocation, 
                departureDate, returnDate, passengerCount, results);
        
        return ResponseEntity.ok(results);
    }
    
    /**
     * Get train details.
     *
     * @param id the train ID
     * @return train details
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getTrainDetails(@PathVariable String id) {
        log.debug("Fetching train details for id: {}", id);
        Map<String, Object> train = trainService.getTrainDetails(id);
        return ResponseEntity.ok(train);
    }
    
    /**
     * Filter trains by price, duration, and stops.
     *
     * @param trains the list of trains to filter
     * @param maxPrice the maximum price filter
     * @param maxStops the maximum stops filter
     * @return filtered list of trains
     */
    @PostMapping("/filter")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Map<String, Object>>> filterTrains(
            @RequestBody List<Map<String, Object>> trains,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer maxStops) {
        
        log.debug("Filtering trains with maxPrice={}, maxStops={}", maxPrice, maxStops);
        
        List<Map<String, Object>> filtered = trains.stream()
                .filter(train -> maxPrice == null || 
                        ((Number) train.getOrDefault("price", Double.MAX_VALUE)).doubleValue() <= maxPrice)
                .filter(train -> maxStops == null || 
                        ((Number) train.getOrDefault("stops", Integer.MAX_VALUE)).intValue() <= maxStops)
                .toList();
        
        return ResponseEntity.ok(filtered);
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
