package com.travelplanner.repository;

import com.travelplanner.model.TrainCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TrainCacheRepository extends JpaRepository<TrainCache, Long> {
    
    Optional<TrainCache> findByFromLocationAndToLocationAndDepartureDate(
        String fromLocation, String toLocation, LocalDate departureDate);
    
    void deleteByExpiresAtBefore(java.time.LocalDateTime expiresAt);
}
