package com.travelplanner.repository;

import com.travelplanner.model.FlightCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface FlightCacheRepository extends JpaRepository<FlightCache, Long> {
    
    Optional<FlightCache> findByFromLocationAndToLocationAndDepartureDate(
        String fromLocation, String toLocation, LocalDate departureDate);
    
    void deleteByExpiresAtBefore(java.time.LocalDateTime expiresAt);
}
