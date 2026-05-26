package com.booking.professionalservice.model.dto.request;

import java.io.Serializable;

public record CleanerDto(
    String id,
    String fullName,
    String phone,
    String vehicleId,
    String vehicleName
) implements Serializable {}
