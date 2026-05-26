package com.booking.bookingservice.model.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AvailabilityByDateResponse(
    LocalDate date,
    List<VehicleAvailability> vehicles
) {
  public record VehicleAvailability(
      String vehicleId,
      String vehicleName,
      List<CleanerAvailability> cleaners
  ) {}

  public record CleanerAvailability(
      String cleanerId,
      String cleanerName,
      String phone,
      List<LocalTime> startTimes2h,
      List<LocalTime> startTimes4h
  ) {}
}
