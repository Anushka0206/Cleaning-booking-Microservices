package com.booking.bookingservice.model.dto.response;

public record AssignedCleanerResponse(
    String id,
    String fullName,
    String phone,
    String email
) {}
