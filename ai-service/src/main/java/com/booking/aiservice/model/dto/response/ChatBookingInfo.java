package com.booking.aiservice.model.dto.response;

/**
 * Booking created via chat — frontend saves to local My Bookings list.
 */
public record ChatBookingInfo(
    String id,
    String startAt,
    String endAt,
    int durationHours,
    String vehicleId,
    String serviceId,
    String serviceName,
    int price,
    String date,
    String time
) {}
