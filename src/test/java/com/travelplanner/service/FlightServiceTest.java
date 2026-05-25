package com.travelplanner.service;

import com.travelplanner.dto.FlightSearchRequest;
import com.travelplanner.dto.FlightSearchResponse;
import com.travelplanner.model.FlightCache;
import com.travelplanner.repository.FlightCacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FlightService.
 */
@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FlightCacheRepository flightCacheRepository;

    @InjectMocks
    private FlightService flightService;

    private FlightSearchRequest searchRequest;
    private FlightCache cachedFlights;

    @BeforeEach
    void setUp() {
        searchRequest = FlightSearchRequest.builder()
                .fromLocation("JFK")
                .toLocation("LAX")
                .departureDate(LocalDate.now().plusDays(7))
                .build();

        cachedFlights = FlightCache.builder()
                .id(1L)
                .fromLocation("JFK")
                .toLocation("LAX")
                .departureDate(LocalDate.now().plusDays(7))
                .data("{\"flights\": []}")
                .cachedAt(LocalDateTime.now().minusHours(1))
                .expiresAt(LocalDateTime.now().plusHours(23))
                .build();
    }

    @Test
    void testSearchFlights_CacheHit() {
        when(flightCacheRepository.findByFromLocationAndToLocationAndDepartureDate(
                anyString(), anyString(), any(LocalDate.class)))
                .thenReturn(Optional.of(cachedFlights));

        List<FlightSearchResponse> result = flightService.searchFlights(searchRequest);

        assertNotNull(result);
        verify(flightCacheRepository, times(1))
                .findByFromLocationAndToLocationAndDepartureDate(anyString(), anyString(), any(LocalDate.class));
    }

    @Test
    void testSearchFlights_CacheMiss() {
        when(flightCacheRepository.findByFromLocationAndToLocationAndDepartureDate(
                anyString(), anyString(), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        List<FlightSearchResponse> result = flightService.searchFlights(searchRequest);

        assertNotNull(result);
        verify(flightCacheRepository, times(1))
                .findByFromLocationAndToLocationAndDepartureDate(anyString(), anyString(), any(LocalDate.class));
    }
}
