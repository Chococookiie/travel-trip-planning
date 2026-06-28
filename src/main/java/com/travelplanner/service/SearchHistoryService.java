package com.travelplanner.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelplanner.exception.ResourceNotFoundException;
import com.travelplanner.model.SearchHistory;
import com.travelplanner.model.User;
import com.travelplanner.repository.SearchHistoryRepository;
import com.travelplanner.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Search History Service for managing user search history.
 */
@Service
@Slf4j
@Transactional
public class SearchHistoryService {
    
    @Autowired
    private SearchHistoryRepository searchHistoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Save search to history.
     *
     * @param userId the user ID
     * @param searchType the search type (flight or train)
     * @param fromLocation the departure location
     * @param toLocation the arrival location
     * @param departureDate the departure date
     * @param returnDate the return date (optional)
     * @param passengerCount the number of passengers
     * @param searchResults the search results JSON
     * @return the saved search history
     */
    public SearchHistory saveSearch(Long userId, String searchType, String fromLocation, 
                                    String toLocation, LocalDate departureDate, LocalDate returnDate,
                                    Integer passengerCount, Object searchResults) {
        log.info("Saving search history for user id: {}, search type: {}", userId, searchType);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        String resultsJson = serializeResults(searchResults);
        
        SearchHistory history = SearchHistory.builder()
                .user(user)
                .searchType(searchType)
                .fromLocation(fromLocation)
                .toLocation(toLocation)
                .departureDate(departureDate)
                .returnDate(returnDate)
                .passengerCount(passengerCount)
                .searchResults(resultsJson)
                .build();
        
        SearchHistory saved = searchHistoryRepository.save(history);
        log.info("Search history saved with id: {}", saved.getId());
        return saved;
    }
    
    /**
     * Get user's search history.
     *
     * @param userId the user ID
     * @param page the page number (0-indexed)
     * @param size the page size
     * @return paginated search history
     */
    public Page<SearchHistory> getUserSearchHistory(Long userId, int page, int size) {
        log.debug("Fetching search history for user id: {}, page: {}, size: {}", userId, page, size);
        
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        
        Pageable pageable = PageRequest.of(page, size);
        return searchHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
    
    /**
     * Get search by ID.
     *
     * @param searchId the search ID
     * @return the search history
     * @throws ResourceNotFoundException if search not found
     */
    public SearchHistory getSearchById(Long searchId) {
        log.debug("Fetching search with id: {}", searchId);
        
        return searchHistoryRepository.findById(searchId)
                .orElseThrow(() -> {
                    log.warn("Search not found with id: {}", searchId);
                    return new ResourceNotFoundException("Search not found");
                });
    }
    
    public SearchHistory getSearchByIdForUser(Long userId, Long searchId) {
        SearchHistory search = getSearchById(searchId);
        if (!search.getUser().getId().equals(userId)) {
            log.warn("User {} attempted to access search {} owned by {}", userId, searchId, search.getUser().getId());
            throw new ResourceNotFoundException("Search not found");
        }
        return search;
    }
    
    public List<SearchHistory> getSearchHistoryForUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        return searchHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, Integer.MAX_VALUE)).getContent();
    }
    
    /**
     * Delete search from history.
     *
     * @param searchId the search ID
     */
    public void deleteSearch(Long searchId) {
        log.info("Deleting search with id: {}", searchId);
        
        SearchHistory search = getSearchById(searchId);
        searchHistoryRepository.delete(search);
        
        log.info("Search deleted successfully with id: {}", searchId);
    }
    
    /**
     * Serialize search results to JSON string.
     *
     * @param results the search results object
     * @return JSON string representation
     */
    private String serializeResults(Object results) {
        try {
            return objectMapper.writeValueAsString(results);
        } catch (Exception ex) {
            log.error("Error serializing search results: {}", ex.getMessage());
            return "{}";
        }
    }
}
