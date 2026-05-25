package com.booking.bookingservice.controller;

import com.booking.bookingservice.base.AbstractRestControllerTest;
import com.booking.bookingservice.integration.professionals.dto.VehicleDto;
import com.booking.bookingservice.service.AvailabilityService;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AvailabilityControllerTest extends AbstractRestControllerTest {

    @MockitoBean
    private AvailabilityService availabilityService;

    @Test
    void availabilityByDate_givenValidDate_whenGetApiAvailability_thenReturnsOkAndCallsService() throws Exception {

        // Given
        LocalDate date = LocalDate.of(2026, 2, 25);

        Map<String, Map<String, Map<Integer, List<LocalTime>>>> availability = new LinkedHashMap<>();
        availability.put(
                "veh-1",
                Map.of(
                        "cl-1", Map.of(
                                2, List.of(LocalTime.of(8, 0), LocalTime.of(10, 0)),
                                4, List.of(LocalTime.of(8, 0))
                        ),
                        "cl-2", Map.of(
                                2, List.of(LocalTime.of(12, 0)),
                                4, List.of(LocalTime.of(14, 0))
                        )
                )
        );

        when(availabilityService.availabilityByDate(date)).thenReturn(availability);

        // When
        MvcResult mvcResult = mockMvc.perform(
                        get("/api/availability")
                                .param("date", "2026-02-25")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        JsonNode json = objectMapper.readTree(mvcResult.getResponse().getContentAsString());

        assertThat(json.toString()).contains("2026-02-25");
        assertThat(json.toString()).contains("veh-1");
        assertThat(json.toString()).contains("cl-1");
        assertThat(json.toString()).contains("cl-2");

        verify(availabilityService, times(1)).availabilityByDate(date);
        verifyNoMoreInteractions(availabilityService);
    }

    @Test
    void availabilityByDate_givenMissingDate_whenGetApiAvailability_thenReturnsBadRequest() throws Exception {

        // When
        mockMvc.perform(
                        get("/api/availability")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isBadRequest());

        verifyNoInteractions(availabilityService);
    }

    @Test
    void availabilityForSlot_givenValidParams_whenEnoughCleaners_thenReturnsFilteredVehicles() throws Exception {

        // Given
        LocalDateTime startAt = LocalDateTime.of(2026, 2, 25, 10, 0);

        VehicleDto vehicle1 = mock(VehicleDto.class);
        VehicleDto vehicle2 = mock(VehicleDto.class);

        when(vehicle1.id()).thenReturn("veh-1");
        when(vehicle2.id()).thenReturn("veh-2");

        when(availabilityService.vehicles()).thenReturn(List.of(vehicle1, vehicle2));
        when(availabilityService.availableCleanersFor("veh-1", startAt, 2))
                .thenReturn(List.of("cl-1", "cl-2"));
        when(availabilityService.availableCleanersFor("veh-2", startAt, 2))
                .thenReturn(List.of("cl-3"));

        // When
        MvcResult mvcResult = mockMvc.perform(
                        get("/api/availability/slot")
                                .param("startAt", "2026-02-25T10:00:00")
                                .param("durationHours", "2")
                                .param("professionalCount", "2")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        JsonNode json = objectMapper.readTree(mvcResult.getResponse().getContentAsString());

        assertThat(json.toString()).contains("2026-02-25T10:00:00");
        assertThat(json.toString()).contains("veh-1");
        assertThat(json.toString()).contains("cl-1");
        assertThat(json.toString()).contains("cl-2");
        assertThat(json.toString()).doesNotContain("veh-2");

        verify(availabilityService, times(1)).vehicles();
        verify(availabilityService, times(1)).availableCleanersFor("veh-1", startAt, 2);
        verify(availabilityService, times(1)).availableCleanersFor("veh-2", startAt, 2);
        verifyNoMoreInteractions(availabilityService);
    }

    @Test
    void availabilityForSlot_givenInvalidProfessionalCount_whenGetApiAvailabilitySlot_thenReturnsBadRequest() throws Exception {

        // When
        mockMvc.perform(
                        get("/api/availability/slot")
                                .param("startAt", "2026-02-25T10:00:00")
                                .param("durationHours", "2")
                                .param("professionalCount", "0")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isBadRequest());

        verifyNoInteractions(availabilityService);
    }

}