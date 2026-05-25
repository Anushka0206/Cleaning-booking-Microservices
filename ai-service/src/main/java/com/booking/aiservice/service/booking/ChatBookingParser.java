package com.booking.aiservice.service.booking;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.booking.aiservice.service.chat.ChatDateParser;
import com.booking.aiservice.service.knowledge.AvailabilityGuidance;

/**
 * Parses chat commands such as: {@code book standard 2026-05-26 14:00}
 * or {@code book deep tomorrow 10am}.
 */
public final class ChatBookingParser {

  private static final Pattern BOOK_PREFIX =
      Pattern.compile("(?i)^(book(?:\\s+karo|\\s+karein)?)\\s+(.+)$");

  private ChatBookingParser() {}

  public static ChatBookingParseResult parse(String message, String contextText, String languageCode) {
    String lang = languageCode == null ? "en" : languageCode;
    if (message == null || message.isBlank()) {
      return new ChatBookingParseResult.NotBooking();
    }
    String current = message.trim();
    Matcher m = BOOK_PREFIX.matcher(current);
    if (!m.matches()) {
      return new ChatBookingParseResult.NotBooking();
    }
    String rest = m.group(2).trim();
    if (rest.isEmpty()) {
      return incompleteHint(lang);
    }

    Optional<ChatBookingPackage> pkg = ChatBookingCatalog.match(rest);
    if (pkg.isEmpty() && contextText != null) {
      pkg = ChatBookingCatalog.match(contextText);
    }
    if (pkg.isEmpty()) {
      return incompleteHint(lang);
    }

    var date = ChatDateParser.parseDateWithContext(rest, contextText);
    var time = ChatDateParser.parseTimeWithContext(rest, contextText);

    if (date.isEmpty() || time.isEmpty()) {
      return incompleteHint(lang);
    }

    LocalDateTime startAt = LocalDateTime.of(date.get(), time.get());
    return new ChatBookingParseResult.Ready(pkg.get(), startAt);
  }

  private static ChatBookingParseResult.Incomplete incompleteHint(String lang) {
    return new ChatBookingParseResult.Incomplete(AvailabilityGuidance.bookingHint(lang));
  }

}
