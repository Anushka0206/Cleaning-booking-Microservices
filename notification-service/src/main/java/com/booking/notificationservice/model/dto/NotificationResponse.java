package com.booking.notificationservice.model.dto;

import java.time.Instant;

public record NotificationResponse(
    String id,
    String bookingId,
    String eventType,
    String message,
    String customerName,
    String customerPhone,
    String customerAddress,
    boolean read,
    Instant createdAt
) {}
