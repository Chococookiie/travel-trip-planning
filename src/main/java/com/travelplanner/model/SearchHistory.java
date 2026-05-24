package com.travelplanner.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "search_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "search_type", nullable = false)
    private String searchType; // 'flight' or 'train'
    
    @Column(name = "from_location", nullable = false)
    private String fromLocation;
    
    @Column(name = "to_location", nullable = false)
    private String toLocation;
    
    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;
    
    @Column(name = "return_date")
    private LocalDate returnDate;
    
    @Column(name = "passenger_count")
    private Integer passengerCount;
    
    @Column(name = "search_results", columnDefinition = "jsonb")
    private String searchResults; // JSON data
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
