package com.travelplanner.repository;

import com.travelplanner.model.PriceAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PriceAlertRepository extends JpaRepository<PriceAlert, Long> {
    
    List<PriceAlert> findByUserId(Long userId);
    
    List<PriceAlert> findByUserIdAndIsActiveTrue(Long userId);
    
    List<PriceAlert> findByIsActiveTrue();
    
    List<PriceAlert> findBySearchId(Long searchId);
    
    List<PriceAlert> findBySearchIdAndIsActiveTrue(Long searchId);
}
