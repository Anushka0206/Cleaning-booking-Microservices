package com.booking.aiservice.service.availability;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import com.fasterxml.jackson.databind.JsonNode;

public final class ChatAvailabilityFormatter {

  private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
  private static final int MAX_TIMES_SHOWN = 24;

  private ChatAvailabilityFormatter() {}

  public static String format(String lang, LocalDate date, JsonNode vehiclesNode) {
    TreeSet<String> times2h = new TreeSet<>();
    TreeSet<String> times4h = new TreeSet<>();

    if (vehiclesNode != null && vehiclesNode.isArray()) {
      for (JsonNode vehicle : vehiclesNode) {
        JsonNode cleaners = vehicle.path("cleaners");
        if (!cleaners.isArray()) {
          continue;
        }
        for (JsonNode cleaner : cleaners) {
          collectTimes(cleaner.path("startTimes2h"), times2h);
          collectTimes(cleaner.path("startTimes4h"), times4h);
        }
      }
    }

    String dateStr = date.format(DATE_FMT);
    if (times2h.isEmpty() && times4h.isEmpty()) {
      return "hi".equals(lang)
          ? dateStr + " par koi free slot nahi (sab booked ya off-hours). Doosri date try karein."
          : "No free slots on " + dateStr + " (all booked or outside hours). Try another date.";
    }

    String line2h = formatTimeList(times2h);
    String line4h = formatTimeList(times4h);

    if ("hi".equals(lang)) {
      return """
          Live slots (%s) — API se:
          • 2 ghante (Standard/Basic): %s
          • 4 ghante (Deep/Premium): %s
          Book: book standard %s 14:00 (apna time choose karein upar se)"""
          .formatted(dateStr, line2h, line4h, dateStr);
    }
    return """
        Live slots for %s (from API):
        • 2-hour jobs (Basic/Standard): %s
        • 4-hour jobs (Deep/Premium): %s
        To book: book standard %s 14:00 (pick a time from above)"""
        .formatted(dateStr, line2h, line4h, dateStr);
  }

  private static void collectTimes(JsonNode array, TreeSet<String> target) {
    if (!array.isArray()) {
      return;
    }
    for (JsonNode t : array) {
      String raw = t.asText();
      if (raw == null || raw.isBlank()) {
        continue;
      }
      target.add(normalizeTime(raw));
    }
  }

  private static String normalizeTime(String raw) {
    if (raw.length() >= 5) {
      return raw.substring(0, 5);
    }
    return raw;
  }

  private static String formatTimeList(TreeSet<String> times) {
    if (times.isEmpty()) {
      return "none";
    }
    List<String> list = new ArrayList<>(times);
    if (list.size() <= MAX_TIMES_SHOWN) {
      return String.join(", ", list);
    }
    return String.join(", ", list.subList(0, MAX_TIMES_SHOWN))
        + " … (+" + (list.size() - MAX_TIMES_SHOWN) + " more)";
  }
}
