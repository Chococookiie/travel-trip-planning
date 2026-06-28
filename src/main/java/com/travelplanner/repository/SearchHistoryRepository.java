package com.travelplanner.repository;

import com.travelplanner.model.SearchHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    
    List<SearchHistory> findByUserId(Long userId);
    
    Page<SearchHistory> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    List<SearchHistory> findByFromLocationAndToLocationAndDepartureDate(
        String fromLocation, String toLocation, LocalDate departureDate);
    
    List<SearchHistory> findBySearchType(String searchType);
}
