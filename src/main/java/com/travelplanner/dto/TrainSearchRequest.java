package com.travelplanner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainSearchRequest {
    
    @NotBlank(message = "From location is required")
    private String fromLocation;
    
    @NotBlank(message = "To location is required")
    private String toLocation;
    
    @NotNull(message = "Departure date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate departureDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnDate;
    
    @NotNull(message = "Passenger count is required")
    @Positive(message = "Passenger count must be greater than 0")
    private Integer passengerCount;
    
    private String trainClass; // economy, first, business
}
