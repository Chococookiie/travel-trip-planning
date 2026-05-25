package com.travelplanner.controller;

import com.travelplanner.dto.FlightSearchRequest;
import com.travelplanner.dto.FlightSearchResponse;
import com.travelplanner.service.FlightService;
import com.travelplanner.service.SearchHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * Flight Controller for flight search operations.
 */
@RestController
@RequestMapping("/api/flights")
@Validated
@Slf4j
public class FlightController {
    
    @Autowired
    private FlightService flightService;
    
    @Autowired
    private SearchHistoryService searchHistoryService;
    
    /**
     * Search for flights.
     *
     * @param fromLocation the departure location
     * @param toLocation the arrival location
     * @param departureDate the departure date
     * @param returnDate the return date (optional)
     * @param passengerCount the number of passengers
     * @param cabin the cabin class (optional)
     * @param authentication the current authentication
     * @return list of flight search responses
     */
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FlightSearchResponse>> searchFlights(
            @RequestParam String fromLocation,
            @RequestParam String toLocation,
            @RequestParam LocalDate departureDate,
            @RequestParam(required = false) LocalDate returnDate,
            @RequestParam Integer passengerCount,
            @RequestParam(required = false) String cabin,
            Authentication authentication) {
        
        log.info("Flight search request: from={}, to={}, date={}", fromLocation, toLocation, departureDate);
        
        FlightSearchRequest request = FlightSearchRequest.builder()
                .fromLocation(fromLocation)
                .toLocation(toLocation)
                .departureDate(departureDate)
                .returnDate(returnDate)
                .passengerCount(passengerCount)
                .cabin(cabin)
                .build();
        
        List<FlightSearchResponse> results = flightService.searchFlights(request);
        
        // Save to search history
        Long userId = extractUserIdFromAuth(authentication);
        searchHistoryService.saveSearch(userId, "flight", fromLocation, toLocation, 
                departureDate, returnDate, passengerCount, results);
        
        return ResponseEntity.ok(results);
    }
    
    /**
     * Get flight details.
     *
     * @param id the flight ID
     * @return flight search response
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FlightSearchResponse> getFlightDetails(@PathVariable String id) {
        log.debug("Fetching flight details for id: {}", id);
        FlightSearchResponse flight = flightService.getFlightDetails(id);
        return ResponseEntity.ok(flight);
    }
    
    /**
     * Filter flights by price, duration, and stops.
     *
     * @param flights the list of flights to filter
     * @param maxPrice the maximum price filter
     * @param maxDuration the maximum duration filter
     * @param maxStops the maximum stops filter
     * @return filtered list of flights
     */
    @PostMapping("/filter")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FlightSearchResponse>> filterFlights(
            @RequestBody List<FlightSearchResponse> flights,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(required = false) String maxDuration,
            @RequestParam(required = false) Integer maxStops) {
        
        log.debug("Filtering flights with maxPrice={}, maxDuration={}, maxStops={}", 
                maxPrice, maxDuration, maxStops);
        
        List<FlightSearchResponse> filtered = flights.stream()
                .filter(flight -> maxPrice == null || flight.getPrice().compareTo(maxPrice) <= 0)
                .filter(flight -> maxStops == null || flight.getStops() <= maxStops)
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
