package com.booking.notificationservice.model.dto;

import java.time.Instant;
import java.time.LocalDateTime;

public record NotificationResponse(
    String id,
    String bookingId,
    String eventType,
    String message,
    String customerName,
    String customerPhone,
    String customerAddress,
    LocalDateTime slotStartAt,
    Integer durationHours,
    String slotLabel,
    boolean read,
    Instant createdAt
) {}
