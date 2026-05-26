package com.booking.bookingservice.integration.professionals.dto;

import java.io.Serializable;

public record CleanerDto(
    String id,
    String fullName,
    String phone,
    String vehicleId,
    String vehicleName
) implements Serializable {}
