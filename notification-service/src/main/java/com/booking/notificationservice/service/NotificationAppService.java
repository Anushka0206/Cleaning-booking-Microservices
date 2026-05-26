package com.booking.notificationservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.booking.notificationservice.model.NotificationEntity;
import com.booking.notificationservice.repository.NotificationRepository;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class NotificationAppService {

  private final NotificationRepository notificationRepository;

  public NotificationAppService(NotificationRepository notificationRepository) {
    this.notificationRepository = notificationRepository;
  }

  @Transactional
  public void notifyCleanersAssigned(JsonNode event, String type) {
    String bookingId = event.path("bookingId").asText();
    String customerName = textOrNull(event, "customerName");
    String customerPhone = textOrNull(event, "customerPhone");
    String customerAddress = textOrNull(event, "customerAddress");
    var slotStart = NotificationMessageFormatter.parseSlotStart(event);
    int durationHours = NotificationMessageFormatter.parseDurationHours(event);
    JsonNode cleaners = event.path("cleanerIds");
    if (!cleaners.isArray()) {
      return;
    }
    for (JsonNode c : cleaners) {
      NotificationEntity n = new NotificationEntity();
      n.setCleanerId(c.asText());
      n.setBookingId(bookingId);
      n.setEventType(type);
      n.setCustomerName(customerName);
      n.setCustomerPhone(customerPhone);
      n.setCustomerAddress(customerAddress);
      n.setSlotStartAt(slotStart);
      n.setDurationHours(durationHours > 0 ? durationHours : null);
      n.setMessage(NotificationMessageFormatter.assignedMessage(event, type));
      notificationRepository.save(n);
    }
  }

  @Transactional
  public void notifyCleanersCancelled(JsonNode event) {
    String bookingId = event.path("bookingId").asText();
    JsonNode cleaners = event.path("cleanerIds");
    if (!cleaners.isArray()) {
      return;
    }
    for (JsonNode c : cleaners) {
      NotificationEntity n = new NotificationEntity();
      n.setCleanerId(c.asText());
      n.setBookingId(bookingId);
      n.setEventType("BOOKING_CANCELLED");
      n.setMessage("Booking " + bookingId + " was cancelled. Slot is free for other jobs.");
      notificationRepository.save(n);
    }
  }

  private static String textOrNull(JsonNode event, String field) {
    JsonNode n = event.path(field);
    return n.isMissingNode() || n.isNull() ? null : n.asText();
  }

  private static String safe(String v) {
    return v == null || v.isBlank() ? "—" : v;
  }
}
