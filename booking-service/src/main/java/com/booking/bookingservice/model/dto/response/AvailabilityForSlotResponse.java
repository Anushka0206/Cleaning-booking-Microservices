package com.booking.bookingservice.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record AvailabilityForSlotResponse(
    LocalDateTime startAt,
    int durationHours,
    int professionalCount,
    List<VehicleCandidate> vehicles
) {
  public record VehicleCandidate(
      String vehicleId,
      String vehicleName,
      List<AvailableCleaner> availableCleaners
  ) {}

  public record AvailableCleaner(String cleanerId, String cleanerName, String phone) {}
}
