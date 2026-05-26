package com.booking.bookingservice.integration.auth.dto;

public record CleanerContactDto(
    String cleanerId,
    String email,
    String phone,
    String fullName
) {}
