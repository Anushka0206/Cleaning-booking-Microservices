package com.booking.aiservice.service.booking;

/**
 * Maps chat keywords to booking API fields (aligned with frontend {@code services.js}).
 */
public record ChatBookingPackage(
    String id,
    String name,
    int durationHours,
    int professionalCount,
    int price,
    String... keywords
) {}
