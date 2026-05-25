package com.booking.bookingservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booking.bookingservice.model.dto.request.CreateBookingRequest;
import com.booking.bookingservice.model.dto.request.UpdateBookingRequest;
import com.booking.bookingservice.model.dto.response.BookingResponse;
import com.booking.bookingservice.model.entity.BookingEntity;
import com.booking.bookingservice.model.mapper.BookingMapper;
import com.booking.bookingservice.service.BookingAppService;
import com.booking.common.model.dto.response.CustomResponse;
import com.booking.common.security.AuthUserPrincipal;
import com.booking.common.security.JwtAuthFilter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Tag(name = "Bookings", description = "APIs for creating and updating bookings")
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

  private final BookingAppService bookingAppService;
  private final BookingMapper bookingMapper;

  public BookingController(BookingAppService bookingAppService, BookingMapper bookingMapper) {
    this.bookingAppService = bookingAppService;
    this.bookingMapper = bookingMapper;
  }

  @Operation(summary = "Create booking (login required)")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "201", description = "Booking created",
                  content = @Content(schema = @Schema(implementation = CustomResponse.class))),
          @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
          @ApiResponse(responseCode = "409", description = "Booking rule violation", content = @Content)
  })
  @PostMapping
  public CustomResponse<BookingResponse> create(
      HttpServletRequest httpRequest,
      @Valid @RequestBody CreateBookingRequest req
  ) {
    AuthUserPrincipal user = JwtAuthFilter.requireUser(httpRequest);
    if (!user.isCustomer()) {
      throw new org.springframework.web.server.ResponseStatusException(
          org.springframework.http.HttpStatus.FORBIDDEN, "Only customers can create bookings");
    }
    BookingEntity created = bookingAppService.create(
        req.startAt(), req.durationHours(), req.professionalCount(), user);
    return CustomResponse.createdOf(bookingMapper.map(created));
  }

  @Operation(summary = "My bookings")
  @GetMapping("/me")
  public CustomResponse<List<BookingResponse>> myBookings(HttpServletRequest httpRequest) {
    AuthUserPrincipal user = JwtAuthFilter.requireUser(httpRequest);
    List<BookingResponse> list = bookingAppService.findByUser(user.userId()).stream()
        .map(bookingMapper::map)
        .toList();
    return CustomResponse.successOf(list);
  }

  @Operation(summary = "Cleaner cancels assigned booking (frees slot)")
  @PostMapping("/{bookingId}/cleaner-cancel")
  public CustomResponse<BookingResponse> cleanerCancel(
      HttpServletRequest httpRequest,
      @PathVariable String bookingId
  ) {
    AuthUserPrincipal user = JwtAuthFilter.requireUser(httpRequest);
    if (!user.isCleaner() || user.cleanerId() == null) {
      throw new org.springframework.web.server.ResponseStatusException(
          org.springframework.http.HttpStatus.FORBIDDEN, "Only cleaners can cancel assignments");
    }
    BookingEntity cancelled = bookingAppService.cancelByCleaner(bookingId, user.cleanerId());
    return CustomResponse.successOf(bookingMapper.map(cancelled));
  }

  @Operation(summary = "Customer cancels own booking")
  @PostMapping("/{bookingId}/cancel")
  public CustomResponse<BookingResponse> customerCancel(
      HttpServletRequest httpRequest,
      @PathVariable String bookingId
  ) {
    AuthUserPrincipal user = JwtAuthFilter.requireUser(httpRequest);
    if (!user.isCustomer()) {
      throw new org.springframework.web.server.ResponseStatusException(
          org.springframework.http.HttpStatus.FORBIDDEN, "Only customers can cancel their bookings");
    }
    BookingEntity cancelled = bookingAppService.cancelByCustomer(bookingId, user.userId());
    return CustomResponse.successOf(bookingMapper.map(cancelled));
  }

  @Operation(summary = "Reschedule booking (owner only)")
  @PutMapping("/{bookingId}")
  public CustomResponse<BookingResponse> update(
      HttpServletRequest httpRequest,
      @PathVariable String bookingId,
      @Valid @RequestBody UpdateBookingRequest req
  ) {
    AuthUserPrincipal user = JwtAuthFilter.requireUser(httpRequest);
    BookingEntity updatedBookingEntity = bookingAppService.rescheduleForCustomer(
        bookingId,
        req.newStartAt(),
        req.newDurationHours(),
        user.userId()
    );
    return CustomResponse.successOf(bookingMapper.map(updatedBookingEntity));
  }
}
