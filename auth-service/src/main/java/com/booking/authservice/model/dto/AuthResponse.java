package com.booking.authservice.model.dto;

public record AuthResponse(
    String token,
    String userId,
    String email,
    String fullName,
    String phone,
    String address,
    String role,
    String cleanerId
) {}
