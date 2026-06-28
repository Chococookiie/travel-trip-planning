package com.travelplanner.controller;

import com.travelplanner.model.SearchHistory;
import com.travelplanner.security.UserPrincipal;
import com.travelplanner.service.FlightService;
import com.travelplanner.service.SearchHistoryService;
import com.travelplanner.service.TrainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Search Controller for managing search history.
 */
@RestController
@RequestMapping("/api/searches")
@Validated
@Slf4j
public class SearchController {
    
    @Autowired
    private SearchHistoryService searchHistoryService;

    @Autowired
    private FlightService flightService;

    @Autowired
    private TrainService trainService;
    
    /**
     * Get user's search history.
     *
     * @param page the page number (0-indexed)
     * @param size the page size
     * @param authentication the current authentication
     * @return paginated search history
     */
    @GetMapping("/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<SearchHistory>> getSearchHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        log.debug("Fetching search history for page={}, size={}", page, size);
        
        Long userId = extractUserIdFromAuth(authentication);
        Page<SearchHistory> history = searchHistoryService.getUserSearchHistory(userId, page, size);
        
        return ResponseEntity.ok(history);
    }
    
    /**
     * Get specific search with results.
     *
     * @param id the search ID
     * @return search history with results
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SearchHistory> getSearch(@PathVariable Long id,
                                                   Authentication authentication) {
        log.debug("Fetching search with id: {}", id);
        Long userId = requireUserId(authentication);
        SearchHistory search = searchHistoryService.getSearchByIdForUser(userId, id);
        return ResponseEntity.ok(search);
    }
    
    /**
     * Delete search from history.
     *
     * @param id the search ID
     * @return success response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteSearch(@PathVariable Long id,
                                          Authentication authentication) {
        log.info("Deleting search with id: {}", id);
        Long userId = requireUserId(authentication);
        SearchHistory search = searchHistoryService.getSearchByIdForUser(userId, id);
        searchHistoryService.deleteSearch(search.getId());
        return ResponseEntity.ok().body("Search deleted successfully");
    }
    
    /**
     * Re-execute saved search.
     *
     * @param id the search ID
     * @return search results
     */
    @PostMapping("/{id}/resync")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> resyncSearch(@PathVariable Long id,
                                          Authentication authentication) {
        log.info("Re-syncing search with id: {}", id);
        Long userId = requireUserId(authentication);
        SearchHistory search = searchHistoryService.getSearchByIdForUser(userId, id);

        if ("flight" .equalsIgnoreCase(search.getSearchType())) {
            var request = com.travelplanner.dto.FlightSearchRequest.builder()
                    .fromLocation(search.getFromLocation())
                    .toLocation(search.getToLocation())
                    .departureDate(search.getDepartureDate())
                    .returnDate(search.getReturnDate())
                    .passengerCount(search.getPassengerCount())
                    .build();
            var results = flightService.searchFlights(request);
            searchHistoryService.saveSearch(userId,
                    search.getSearchType(),
                    search.getFromLocation(),
                    search.getToLocation(),
                    search.getDepartureDate(),
                    search.getReturnDate(),
                    search.getPassengerCount(),
                    results);
            return ResponseEntity.ok(results);
        }

        if ("train" .equalsIgnoreCase(search.getSearchType())) {
            var results = trainService.searchTrains(
                    search.getFromLocation(),
                    search.getToLocation(),
                    search.getDepartureDate());
            searchHistoryService.saveSearch(userId,
                    search.getSearchType(),
                    search.getFromLocation(),
                    search.getToLocation(),
                    search.getDepartureDate(),
                    search.getReturnDate(),
                    search.getPassengerCount(),
                    results);
            return ResponseEntity.ok(results);
        }

        return ResponseEntity.badRequest().body("Unsupported search type: " + search.getSearchType());
    }
    
    /**
     * Extract user ID from authentication.
     *
     * @param authentication the current authentication
     * @return user ID or null
     */
    private Long requireUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new com.travelplanner.exception.UnauthorizedException("Invalid authentication credentials");
        }
        return principal.getId();
    }
}
