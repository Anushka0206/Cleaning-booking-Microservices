package com.booking.professionalservice.integration.kafka;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Local copy of the event contract published by booking-service.
 *
 * <p>We keep this as a small, stable DTO so the consumer can validate and
 * deserialize events. If a message is malformed, it will be routed to the DLQ.</p>
 */
public record BookingEventMessage(
    BookingEventType type,
    UUID bookingId,
    UUID vehicleId,
    OffsetDateTime startAt,
    OffsetDateTime endAt,
    int durationHours,
    List<UUID> cleanerIds,
    OffsetDateTime occurredAt,
    String customerUserId,
    String customerName,
    String customerPhone,
    String customerAddress
) {
}
