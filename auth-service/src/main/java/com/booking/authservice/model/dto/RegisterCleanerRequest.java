package com.booking.authservice.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterCleanerRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 6, max = 72) String password,
    @NotBlank String fullName,
    @NotBlank @Size(min = 8, max = 32) String phone,
    @NotBlank String vehicleId
) {}
