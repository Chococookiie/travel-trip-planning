package com.travelplanner.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "train_cache")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainCache {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "from_location", nullable = false)
    private String fromLocation;
    
    @Column(name = "to_location", nullable = false)
    private String toLocation;
    
    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;
    
    @Column(name = "data", columnDefinition = "jsonb", nullable = false)
    private String data; // JSON train data
    
    @Column(name = "cached_at", nullable = false, updatable = false)
    private LocalDateTime cachedAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @PrePersist
    protected void onCreate() {
        cachedAt = LocalDateTime.now();
        // Set expiration to 24 hours from now
        expiresAt = LocalDateTime.now().plusHours(24);
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
