package com.travelplanner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightSearchResponse {
    
    private String flightId;
    
    private String airline;
    
    private String fromLocation;
    
    private String toLocation;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime departureTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime arrivalTime;
    
    private String duration;
    
    private Integer stops;
    
    private BigDecimal price;
    
    private String currency;
    
    private String cabin;
    
    private Boolean directFlight;
    
    private String bookingUrl;
}
