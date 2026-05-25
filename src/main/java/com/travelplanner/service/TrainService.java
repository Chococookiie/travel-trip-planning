package com.travelplanner.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelplanner.model.TrainCache;
import com.travelplanner.repository.TrainCacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Train Service for searching trains and managing train cache.
 */
@Service
@Slf4j
public class TrainService {
    
    @Autowired
    private TrainCacheRepository trainCacheRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Search for trains with caching logic.
     *
     * @param fromLocation the departure location
     * @param toLocation the arrival location
     * @param departureDate the departure date
     * @return list of train results
     */
    public List<Map<String, Object>> searchTrains(String fromLocation, String toLocation, LocalDate departureDate) {
        log.info("Searching for trains from {} to {} on {}", 
                fromLocation, toLocation, departureDate);
        
        // Check cache first
        Optional<TrainCache> cachedTrains = trainCacheRepository.findByFromLocationAndToLocationAndDepartureDate(
                fromLocation,
                toLocation,
                departureDate
        );
        
        if (cachedTrains.isPresent()) {
            TrainCache cache = cachedTrains.get();
            if (!cache.isExpired()) {
                log.info("Returning cached train results");
                return parseTrainCacheData(cache.getData());
            } else {
                log.debug("Cache expired, fetching fresh data");
            }
        }
        
        // Call external API if cache miss or expired
        List<Map<String, Object>> trains = callExternalTrainAPI(fromLocation, toLocation, departureDate);
        
        // Save/update cache
        String trainDataJson = serializeTrainData(trains);
        TrainCache trainCache = TrainCache.builder()
                .fromLocation(fromLocation)
                .toLocation(toLocation)
                .departureDate(departureDate)
                .data(trainDataJson)
                .build();
        
        trainCacheRepository.save(trainCache);
        log.info("Train cache updated with {} results", trains.size());
        
        return trains;
    }
    
    /**
     * Get train details by train ID.
     *
     * @param trainId the train ID
     * @return train details
     */
    public Map<String, Object> getTrainDetails(String trainId) {
        log.debug("Fetching train details for train id: {}", trainId);
        // TODO: Implement logic to fetch individual train details from cache or API
        return new HashMap<>();
    }
    
    /**
     * Call external train API.
     *
     * @param fromLocation the departure location
     * @param toLocation the arrival location
     * @param departureDate the departure date
     * @return list of train results
     */
    private List<Map<String, Object>> callExternalTrainAPI(String fromLocation, String toLocation, LocalDate departureDate) {
        log.debug("Calling external train API");
        // TODO: Integrate with external train API
        // For now, return mock data
        List<Map<String, Object>> mockTrains = new ArrayList<>();
        
        Map<String, Object> train = new HashMap<>();
        train.put("trainId", "TRN001");
        train.put("operator", "National Railways");
        train.put("fromLocation", fromLocation);
        train.put("toLocation", toLocation);
        train.put("departureTime", departureDate.atStartOfDay().plusHours(9));
        train.put("arrivalTime", departureDate.atStartOfDay().plusHours(14));
        train.put("price", 89.99);
        train.put("class", "Standard");
        train.put("duration", "5h 0m");
        train.put("stops", 3);
        
        mockTrains.add(train);
        log.info("Retrieved {} trains from external API", mockTrains.size());
        return mockTrains;
    }
    
    /**
     * Parse train cache data from JSON string.
     *
     * @param jsonData the JSON data
     * @return list of train results
     */
    private List<Map<String, Object>> parseTrainCacheData(String jsonData) {
        try {
            return objectMapper.readValue(jsonData, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
        } catch (Exception ex) {
            log.error("Error parsing train cache data: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Serialize train data to JSON string.
     *
     * @param trains the list of trains
     * @return JSON string representation
     */
    private String serializeTrainData(List<Map<String, Object>> trains) {
        try {
            return objectMapper.writeValueAsString(trains);
        } catch (Exception ex) {
            log.error("Error serializing train data: {}", ex.getMessage());
            return "[]";
        }
    }
}
