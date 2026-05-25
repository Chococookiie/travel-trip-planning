package com.travelplanner.controller;

import com.travelplanner.dto.FlightSearchRequest;
import com.travelplanner.dto.FlightSearchResponse;
import com.travelplanner.service.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for FlightController.
 */
@SpringBootTest
@AutoConfigureMockMvc
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightService flightService;

    private List<FlightSearchResponse> flightResponses;

    @BeforeEach
    void setUp() {
        FlightSearchResponse flight1 = FlightSearchResponse.builder()
                .flightId("FL001")
                .airline("United Airlines")
                .departureTime("10:00 AM")
                .arrivalTime("2:00 PM")
                .duration("4 hours")
                .price(new BigDecimal("250.00"))
                .currency("USD")
                .stops(0)
                .build();

        FlightSearchResponse flight2 = FlightSearchResponse.builder()
                .flightId("FL002")
                .airline("Delta Airlines")
                .departureTime("11:30 AM")
                .arrivalTime("4:30 PM")
                .duration("4 hours 30 minutes")
                .price(new BigDecimal("220.00"))
                .currency("USD")
                .stops(1)
                .build();

        flightResponses = new ArrayList<>();
        flightResponses.add(flight1);
        flightResponses.add(flight2);
    }

    @Test
    void testSearchFlights_Success() throws Exception {
        when(flightService.searchFlights(any(FlightSearchRequest.class)))
                .thenReturn(flightResponses);

        mockMvc.perform(get("/api/flights/search")
                .param("from", "JFK")
                .param("to", "LAX")
                .param("departureDate", LocalDate.now().plusDays(7).toString())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].flightId").value("FL001"))
                .andExpect(jsonPath("$[0].price").value(250.00))
                .andExpect(jsonPath("$[1].flightId").value("FL002"))
                .andExpect(jsonPath("$[1].price").value(220.00));
    }

    @Test
    void testSearchFlights_MissingParameters() throws Exception {
        mockMvc.perform(get("/api/flights/search")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSearchFlights_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/flights/search")
                .param("from", "JFK")
                .param("to", "LAX")
                .param("departureDate", LocalDate.now().plusDays(7).toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
