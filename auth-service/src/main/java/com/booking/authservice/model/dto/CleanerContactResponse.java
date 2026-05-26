package com.booking.authservice.model.dto;

public record CleanerContactResponse(
    String cleanerId,
    String email,
    String phone,
    String fullName
) {}
