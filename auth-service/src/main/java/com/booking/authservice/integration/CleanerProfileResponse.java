package com.booking.authservice.integration;

public record CleanerProfileResponse(
    String id,
    String fullName,
    String phone,
    String vehicleId,
    String vehicleName
) {}
