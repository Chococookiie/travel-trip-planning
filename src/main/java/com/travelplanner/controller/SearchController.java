package com.travelplanner.controller;

import com.travelplanner.model.SearchHistory;
import com.travelplanner.service.SearchHistoryService;
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
    public ResponseEntity<SearchHistory> getSearch(@PathVariable Long id) {
        log.debug("Fetching search with id: {}", id);
        SearchHistory search = searchHistoryService.getSearchById(id);
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
    public ResponseEntity<?> deleteSearch(@PathVariable Long id) {
        log.info("Deleting search with id: {}", id);
        searchHistoryService.deleteSearch(id);
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
    public ResponseEntity<?> resyncSearch(@PathVariable Long id) {
        log.info("Re-syncing search with id: {}", id);
        
        SearchHistory search = searchHistoryService.getSearchById(id);
        
        // TODO: Implement logic to re-execute the search
        // This would involve parsing the original search parameters and calling
        // the appropriate service (FlightService or TrainService)
        
        return ResponseEntity.ok().body("Search re-synced successfully");
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
