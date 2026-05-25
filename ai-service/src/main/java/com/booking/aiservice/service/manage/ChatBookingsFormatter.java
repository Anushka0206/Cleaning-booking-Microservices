package com.booking.aiservice.service.manage;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import com.booking.aiservice.integration.BookingApiClient.BookingSummary;

public final class ChatBookingsFormatter {

  private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  private ChatBookingsFormatter() {}

  public static String formatList(String lang, List<BookingSummary> bookings) {
    if (bookings == null || bookings.isEmpty()) {
      return "hi".equals(lang)
          ? "Aapki koi booking nahi mili. Pehle book karein: book standard tomorrow 2pm"
          : "No bookings found. Create one: book standard tomorrow 2pm";
    }

    List<BookingSummary> sorted = bookings.stream()
        .sorted(Comparator.comparing(BookingSummary::startAt).reversed())
        .toList();

    StringBuilder sb = new StringBuilder();
    if ("hi".equals(lang)) {
      sb.append("Aapki bookings (").append(sorted.size()).append("):\n");
    } else {
      sb.append("Your bookings (").append(sorted.size()).append("):\n");
    }

    int shown = 0;
    for (BookingSummary b : sorted) {
      if (shown >= 8) {
        sb.append("…\n");
        break;
      }
      sb.append("• ")
          .append(b.status())
          .append(" — ")
          .append(b.startAt().format(DT))
          .append(" (")
          .append(b.durationHours())
          .append("h)\n  ID: ")
          .append(b.id())
          .append('\n');
      shown++;
    }

    if ("hi".equals(lang)) {
      sb.append("\nCancel: cancel latest ya cancel booking <ID>\n");
      sb.append("Reschedule: reschedule <ID> tomorrow 2pm");
    } else {
      sb.append("\nCancel: cancel latest or cancel booking <ID>\n");
      sb.append("Reschedule: reschedule <ID> tomorrow 2pm");
    }
    return sb.toString().trim();
  }
}
