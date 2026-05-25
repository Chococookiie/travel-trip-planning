package com.travelplanner.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelplanner.dto.FlightSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Amadeus API Client for integrating with Amadeus Flight Search API.
 */
@Component
@Slf4j
public class AmadeusApiClient {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Value("${api.flight.amadeus.base-url:https://test.api.amadeus.com}")
    private String baseUrl;
    
    @Value("${api.flight.amadeus.api-key:}")
    private String apiKey;
    
    @Value("${api.flight.amadeus.api-secret:}")
    private String apiSecret;
    
    /**
     * Search flights using Amadeus API.
     *
     * @param from the departure IATA code
     * @param to the arrival IATA code
     * @param date the departure date
     * @return list of flight responses
     */
    public List<FlightSearchResponse> searchFlights(String from, String to, LocalDate date) {
        log.info("Searching flights via Amadeus API: from={}, to={}, date={}", from, to, date);
        
        try {
            // TODO: Implement actual Amadeus API authentication and call
            // Current implementation returns mock data for development
            
            String url = String.format("%s/v2/shopping/flight-offers?originLocationCode=%s&destinationLocationCode=%s&departureDate=%s&adults=1",
                    baseUrl, from, to, date);
            
            log.debug("Calling Amadeus API: {}", url);
            
            // TODO: Add authentication headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + getAccessToken());
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                return parseFlightResponse(jsonResponse);
            }
        } catch (Exception ex) {
            log.error("Error calling Amadeus API: {}", ex.getMessage());
        }
        
        return new ArrayList<>();
    }
    
    /**
     * Parse Amadeus API response and convert to FlightSearchResponse objects.
     *
     * @param response the API response as JSON
     * @return list of flight responses
     */
    private List<FlightSearchResponse> parseFlightResponse(JsonNode response) {
        log.debug("Parsing Amadeus API response");
        List<FlightSearchResponse> flights = new ArrayList<>();
        
        try {
            if (response.has("data")) {
                JsonNode flightOffers = response.get("data");
                
                for (JsonNode offer : flightOffers) {
                    FlightSearchResponse flight = FlightSearchResponse.builder()
                            .flightId(offer.get("id").asText())
                            .airline(offer.get("validatingAirlineCodes").get(0).asText())
                            .price(new BigDecimal(offer.get("price").get("grandTotal").asText()))
                            .duration(offer.get("itineraries").get(0).get("duration").asText())
                            .stops(offer.get("itineraries").get(0).get("segments").size() - 1)
                            .build();
                    
                    flights.add(flight);
                }
            }
        } catch (Exception ex) {
            log.error("Error parsing Amadeus response: {}", ex.getMessage());
        }
        
        log.info("Parsed {} flights from Amadeus API", flights.size());
        return flights;
    }
    
    /**
     * Get access token for Amadeus API authentication.
     * TODO: Implement proper OAuth2 flow for Amadeus API.
     *
     * @return access token
     */
    private String getAccessToken() {
        // TODO: Implement OAuth2 authentication flow
        // For now, return placeholder
        log.warn("Using placeholder access token. Implement proper OAuth2 authentication.");
        return "placeholder-token";
    }
}
