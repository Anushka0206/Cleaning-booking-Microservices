package com.booking.bookingservice.controller;

import com.booking.bookingservice.base.AbstractRestControllerTest;
import com.booking.bookingservice.model.entity.BookingEntity;
import com.booking.bookingservice.model.enums.BookingStatus;
import com.booking.bookingservice.service.BookingAppService;
import com.booking.bookingservice.service.BookingResponseFactory;
import com.booking.common.security.AuthUserPrincipal;
import com.booking.common.security.JwtSupport;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookingControllerTest extends AbstractRestControllerTest {

    @MockitoBean
    private BookingAppService bookingAppService;

    @MockitoBean
    private BookingResponseFactory bookingResponseFactory;

    @Test
    void create_givenValidRequest_whenPostApiBookings_thenReturnsOkAndCallsServiceAndMapper() throws Exception {

        // Given
        LocalDateTime startAt = LocalDateTime.of(2026, 2, 25, 10, 0);

        BookingEntity createdBooking = new BookingEntity(
                startAt,
                startAt.plusHours(2),
                2,
                "veh-1",
                BookingStatus.ACTIVE
        );
        ReflectionTestUtils.setField(createdBooking, "id", "booking-1");

        // When
        when(bookingAppService.create(startAt, 2, 2, any())).thenReturn(createdBooking);
        when(bookingResponseFactory.toResponse(createdBooking)).thenReturn(null);

        String requestBody = createBookingRequestBody("2026-02-25T10:00:00", 2, 2);
        String token = JwtSupport.createToken(
            "test-secret-for-unit-tests-min-32-chars-long!!",
            1,
            new AuthUserPrincipal("user-1", "c@test.com", "CUSTOMER", null, "Test", "+1", "Addr")
        );

        // Then
        MvcResult mvcResult = mockMvc.perform(
                        post("/api/bookings")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        JsonNode json = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertThat(json.isObject()).isTrue();

        // Verify
        verify(bookingAppService, times(1)).create(startAt, 2, 2, any());
        verify(bookingResponseFactory, times(1)).toResponse(createdBooking);
        verifyNoMoreInteractions(bookingAppService, bookingResponseFactory);

    }

    @Test
    void create_givenInvalidRequest_whenPostApiBookings_thenReturnsBadRequest() throws Exception {

        // Given
        ObjectNode root = objectMapper.createObjectNode();
        root.put("durationHours", 2);
        root.put("professionalCount", 2);

        // When
        mockMvc.perform(
                        post("/api/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(root))
                )
                // Then
                .andExpect(status().isBadRequest());

        // Verify
        verifyNoInteractions(bookingAppService, bookingResponseFactory);

    }

    @Test
    void update_givenValidRequest_whenPutApiBookings_thenReturnsOkAndCallsServiceAndMapper() throws Exception {

        // Given
        String bookingId = "booking-1";
        LocalDateTime newStartAt = LocalDateTime.of(2026, 2, 26, 14, 0);

        BookingEntity updatedBooking = new BookingEntity(
                newStartAt,
                newStartAt.plusHours(4),
                4,
                "veh-2",
                BookingStatus.ACTIVE
        );
        ReflectionTestUtils.setField(updatedBooking, "id", bookingId);

        // When
        when(bookingAppService.rescheduleForCustomer(eq(bookingId), eq(newStartAt), eq(4), any()))
            .thenReturn(updatedBooking);
        when(bookingResponseFactory.toResponse(updatedBooking)).thenReturn(null);

        String requestBody = updateBookingRequestBody("2026-02-26T14:00:00", 4);
        String token = JwtSupport.createToken(
            "test-secret-for-unit-tests-min-32-chars-long!!",
            1,
            new AuthUserPrincipal("user-1", "c@test.com", "CUSTOMER", null, "Test", "+1", "Addr")
        );

        // Then
        MvcResult mvcResult = mockMvc.perform(
                        put("/api/bookings/{bookingId}", bookingId)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        JsonNode json = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertThat(json.isObject()).isTrue();


        // Verify
        verify(bookingAppService, times(1)).rescheduleForCustomer(eq(bookingId), eq(newStartAt), eq(4), any());
        verify(bookingResponseFactory, times(1)).toResponse(updatedBooking);
        verifyNoMoreInteractions(bookingAppService, bookingResponseFactory);

    }

    @Test
    void update_givenInvalidRequest_whenPutApiBookings_thenReturnsBadRequest() throws Exception {

        // Given
        String bookingId = "booking-1";

        ObjectNode root = objectMapper.createObjectNode();
        root.put("newDurationHours", 4);

        // When
        mockMvc.perform(
                        put("/api/bookings/{bookingId}", bookingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(root))
                )
                // Then
                .andExpect(status().isBadRequest());

        // Verify
        verifyNoInteractions(bookingAppService, bookingResponseFactory);

    }

    private String createBookingRequestBody(String startAt, int durationHours, int professionalCount) throws Exception {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("startAt", startAt);
        root.put("durationHours", durationHours);
        root.put("professionalCount", professionalCount);
        return objectMapper.writeValueAsString(root);
    }

    private String updateBookingRequestBody(String newStartAt, int newDurationHours) throws Exception {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("newStartAt", newStartAt);
        root.put("newDurationHours", newDurationHours);
        return objectMapper.writeValueAsString(root);
    }

}