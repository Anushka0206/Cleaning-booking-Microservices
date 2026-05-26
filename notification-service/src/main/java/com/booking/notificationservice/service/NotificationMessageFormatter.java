package com.booking.notificationservice.service;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class NotificationMessageFormatter {

  private static final DateTimeFormatter SLOT_FMT =
      DateTimeFormatter.ofPattern("EEE d MMM · HH:mm", Locale.ENGLISH);

  private NotificationMessageFormatter() {}

  public static LocalDateTime parseSlotStart(JsonNode event) {
    String raw = event.path("startAt").asText(null);
    if (raw == null || raw.isBlank()) {
      return null;
    }
    try {
      if (raw.contains("T")) {
        return OffsetDateTime.parse(raw).toLocalDateTime();
      }
      return LocalDateTime.parse(raw);
    } catch (Exception ignored) {
      return null;
    }
  }

  public static int parseDurationHours(JsonNode event) {
    return event.path("durationHours").asInt(0);
  }

  public static String formatSlot(LocalDateTime start, int durationHours) {
    if (start == null) {
      return "time not set";
    }
    String base = SLOT_FMT.format(start);
    if (durationHours > 0) {
      return base + " · " + durationHours + "h job";
    }
    return base;
  }

  public static String assignedMessage(JsonNode event, String type) {
    LocalDateTime start = parseSlotStart(event);
    int hours = parseDurationHours(event);
    String slot = formatSlot(start, hours);
    String customer = safe(textOrNull(event, "customerName"));
    String phone = safe(textOrNull(event, "customerPhone"));
    String address = safe(textOrNull(event, "customerAddress"));
    String verb = "BOOKING_RESCHEDULED".equals(type) ? "Rescheduled" : "New booking";
    return verb + " — your slot: " + slot
        + ". Customer: " + customer + ", phone: " + phone + ", address: " + address;
  }

  private static String textOrNull(JsonNode event, String field) {
    JsonNode n = event.path(field);
    return n.isMissingNode() || n.isNull() ? null : n.asText();
  }

  private static String safe(String v) {
    return v == null || v.isBlank() ? "—" : v;
  }
}
