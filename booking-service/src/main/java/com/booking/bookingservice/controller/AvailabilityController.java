package com.booking.bookingservice.controller;

import com.booking.bookingservice.model.dto.response.AvailabilityByDateResponse;
import com.booking.bookingservice.model.dto.response.AvailabilityForSlotResponse;
import com.booking.bookingservice.service.AvailabilityService;
import com.booking.bookingservice.service.ProfessionalsGateway;
import com.booking.common.model.dto.response.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Tag(name = "Availability", description = "APIs for checking vehicle/cleaner availability")
@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

  private final AvailabilityService availabilityService;
  private final ProfessionalsGateway professionalsGateway;

  public AvailabilityController(
      AvailabilityService availabilityService,
      ProfessionalsGateway professionalsGateway
  ) {
    this.availabilityService = availabilityService;
    this.professionalsGateway = professionalsGateway;
  }

  @Operation(
          summary = "Availability by date",
          description = "Returns availability for each vehicle and its cleaners for the given date."
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "200",
                  description = "Availability retrieved successfully",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = CustomResponse.class)
                  )
          ),
          @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
          @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping
  public CustomResponse<AvailabilityByDateResponse> availabilityByDate(
          @Parameter(description = "Date to check", example = "2026-02-25", required = true)
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
  ) {
      
      Map<String, Map<String, Map<Integer, List<LocalTime>>>> availability = availabilityService.availabilityByDate(date);
      ProfessionalsGateway.DisplayLabels labels = professionalsGateway.loadDisplayLabels();

      List<AvailabilityByDateResponse.VehicleAvailability> vehicles = availability.entrySet().stream()
              .map(vEntry -> new AvailabilityByDateResponse.VehicleAvailability(
                      vEntry.getKey(),
                      labels.vehicleName(vEntry.getKey()),
                      vEntry.getValue().entrySet().stream()
                              .map(cEntry -> new AvailabilityByDateResponse.CleanerAvailability(
                                      cEntry.getKey(),
                                      labels.cleanerName(cEntry.getKey()),
                                      labels.cleanerPhone(cEntry.getKey()),
                                      cEntry.getValue().getOrDefault(2, Collections.emptyList()),
                                      cEntry.getValue().getOrDefault(4, Collections.emptyList())
                              ))
                              .toList()
              ))
              .toList();

      return CustomResponse.successOf(new AvailabilityByDateResponse(date, vehicles));
  }

  @Operation(
          summary = "Availability for a slot",
          description = "Returns vehicle candidates and available cleaners for the given slot."
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "200",
                  description = "Slot availability retrieved successfully",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = CustomResponse.class)
                  )
          ),
          @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
          @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("/slot")
  public CustomResponse<AvailabilityForSlotResponse> availabilityForSlot(
          @Parameter(description = "Slot start time", example = "2026-02-25T10:00:00", required = true)
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startAt,

          @Parameter(description = "Duration in hours (2 or 4)", example = "2", required = true)
          @RequestParam @Min(2) @Max(4) int durationHours,

          @Parameter(description = "Number of professionals required (1..3)", example = "2")
          @RequestParam(defaultValue = "1") @Min(1) @Max(3) int professionalCount
  ) {
      ProfessionalsGateway.DisplayLabels labels = professionalsGateway.loadDisplayLabels();
      List<AvailabilityForSlotResponse.VehicleCandidate> vehicles = availabilityService.vehicles().stream()
              .map(v -> {
                var availableIds = availabilityService.availableCleanersFor(v.id(), startAt, durationHours);
                var cleaners = availableIds.stream()
                    .map(id -> new AvailabilityForSlotResponse.AvailableCleaner(
                        id,
                        labels.cleanerName(id),
                        labels.cleanerPhone(id)
                    ))
                    .toList();
                return new AvailabilityForSlotResponse.VehicleCandidate(
                    v.id(),
                    labels.vehicleName(v.id()),
                    cleaners
                );
              })
              .filter(v -> v.availableCleaners().size() >= professionalCount)
              .toList();

      return CustomResponse.successOf(
            new AvailabilityForSlotResponse(startAt, durationHours, professionalCount, vehicles)
    );
  }
}