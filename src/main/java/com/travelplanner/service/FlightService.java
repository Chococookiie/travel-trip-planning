package com.travelplanner.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelplanner.dto.FlightSearchRequest;
import com.travelplanner.dto.FlightSearchResponse;
import com.travelplanner.model.FlightCache;
import com.travelplanner.repository.FlightCacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Flight Service for searching flights and managing flight cache.
 */
@Service
@Slf4j
public class FlightService {
    
    @Autowired
    private FlightCacheRepository flightCacheRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Search for flights with caching logic.
     *
     * @param request the flight search request
     * @return list of flight search responses
     */
    public List<FlightSearchResponse> searchFlights(FlightSearchRequest request) {
        log.info("Searching for flights from {} to {} on {}", 
                request.getFromLocation(), request.getToLocation(), request.getDepartureDate());
        
        // Check cache first
        Optional<FlightCache> cachedFlights = flightCacheRepository.findByFromLocationAndToLocationAndDepartureDate(
                request.getFromLocation(),
                request.getToLocation(),
                request.getDepartureDate()
        );
        
        if (cachedFlights.isPresent()) {
            FlightCache cache = cachedFlights.get();
            if (!cache.isExpired()) {
                log.info("Returning cached flight results");
                return parseFlightCacheData(cache.getData());
            } else {
                log.debug("Cache expired, fetching fresh data");
            }
        }
        
        // Call external API if cache miss or expired
        List<FlightSearchResponse> flights = callExternalFlightAPI(request);
        
        // Save/update cache
        String flightDataJson = serializeFlightData(flights);
        FlightCache flightCache = FlightCache.builder()
                .fromLocation(request.getFromLocation())
                .toLocation(request.getToLocation())
                .departureDate(request.getDepartureDate())
                .data(flightDataJson)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();
        
        flightCacheRepository.save(flightCache);
        log.info("Flight cache updated with {} results", flights.size());
        
        return flights;
    }
    
    /**
     * Get flight details by flight ID.
     *
     * @param flightId the flight ID
     * @return flight search response
     */
    public FlightSearchResponse getFlightDetails(String flightId) {
        log.debug("Fetching flight details for flight id: {}", flightId);
        // TODO: Implement logic to fetch individual flight details from cache or API
        return FlightSearchResponse.builder().build();
    }
    
    /**
     * Call external flight API (Amadeus).
     *
     * @param request the flight search request
     * @return list of flight search responses
     */
    private List<FlightSearchResponse> callExternalFlightAPI(FlightSearchRequest request) {
        log.debug("Calling external flight API");
        // TODO: Integrate with Amadeus API client
        // For now, return mock data
        List<FlightSearchResponse> mockFlights = new ArrayList<>();
        
        FlightSearchResponse flight = FlightSearchResponse.builder()
                .flightId("AF123")
                .airline("Air France")
                .fromLocation(request.getFromLocation())
                .toLocation(request.getToLocation())
                .departureTime(request.getDepartureDate().atStartOfDay().plusHours(8))
                .arrivalTime(request.getDepartureDate().atStartOfDay().plusHours(12))
                .price(new java.math.BigDecimal("299.99"))
                .cabin(request.getCabin() != null ? request.getCabin() : "economy")
                .duration("4h 0m")
                .stops(0)
                .build();
        
        mockFlights.add(flight);
        log.info("Retrieved {} flights from external API", mockFlights.size());
        return mockFlights;
    }
    
    /**
     * Parse flight cache data from JSON string.
     *
     * @param jsonData the JSON data
     * @return list of flight search responses
     */
    private List<FlightSearchResponse> parseFlightCacheData(String jsonData) {
        try {
            return objectMapper.readValue(jsonData, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, FlightSearchResponse.class));
        } catch (Exception ex) {
            log.error("Error parsing flight cache data: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Serialize flight data to JSON string.
     *
     * @param flights the list of flights
     * @return JSON string representation
     */
    private String serializeFlightData(List<FlightSearchResponse> flights) {
        try {
            return objectMapper.writeValueAsString(flights);
        } catch (Exception ex) {
            log.error("Error serializing flight data: {}", ex.getMessage());
            return "[]";
        }
    }
}
