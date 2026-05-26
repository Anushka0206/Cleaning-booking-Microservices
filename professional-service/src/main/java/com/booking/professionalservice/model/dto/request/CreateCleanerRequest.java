package com.booking.professionalservice.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCleanerRequest(
    @NotBlank @Size(max = 255) String fullName,
    @Size(max = 32) String phone,
    @NotBlank String vehicleId
) {}
