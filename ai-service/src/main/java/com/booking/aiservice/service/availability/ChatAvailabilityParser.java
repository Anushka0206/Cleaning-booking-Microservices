package com.booking.aiservice.service.availability;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

import com.booking.aiservice.service.chat.ChatDateParser;

/**
 * Detects slot/availability questions with an optional date.
 */
public final class ChatAvailabilityParser {

  private static final Pattern BOOK_PREFIX =
      Pattern.compile("(?i)^book(?:\\s+karo|\\s+karein)?\\s+(.+)$");

  private ChatAvailabilityParser() {}

  public static ChatAvailabilityParseResult parse(
      String message,
      String contextText,
      String languageCode
  ) {
    String lang = languageCode == null ? "en" : languageCode;
    if (message == null || message.isBlank()) {
      return new ChatAvailabilityParseResult.NotQuery();
    }
    String trimmed = message.trim();
    String lower = trimmed.toLowerCase(Locale.ROOT);

    if (ChatDateParser.looksLikeDateOnlyMessage(trimmed)) {
      Optional<LocalDate> onlyDate = ChatDateParser.parseDate(trimmed);
      if (onlyDate.isPresent()) {
        return dateResult(onlyDate.get(), lang);
      }
    }

    if (!looksLikeAvailabilityQuery(lower, trimmed, contextText)) {
      return new ChatAvailabilityParseResult.NotQuery();
    }

    Optional<LocalDate> date = ChatDateParser.parseDateWithContext(trimmed, contextText);
    if (date.isEmpty()) {
      return new ChatAvailabilityParseResult.NeedDate(needDateHint(lang));
    }

    return dateResult(date.get(), lang);
  }

  private static ChatAvailabilityParseResult dateResult(LocalDate date, String lang) {

    if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
      return new ChatAvailabilityParseResult.NeedDate(fridayHint(lang));
    }

    return new ChatAvailabilityParseResult.Ready(date);
  }

  private static boolean looksLikeAvailabilityQuery(String lower, String trimmed, String contextText) {
    if (hasAvailabilityKeywords(lower)) {
      return true;
    }
    if (ChatDateParser.parseDateWithContext(trimmed, contextText).isPresent() && mentionsSlots(lower)) {
      return true;
    }
    var book = BOOK_PREFIX.matcher(trimmed);
    if (book.matches()) {
      String rest = book.group(1).toLowerCase(Locale.ROOT);
      return ChatDateParser.parseDateWithContext(rest, contextText).isPresent()
          && ChatDateParser.parseTimeWithContext(rest, contextText).isEmpty()
          && !rest.contains("standard")
          && !rest.contains("basic")
          && !rest.contains("deep")
          && !rest.contains("premium")
          && !rest.contains("office")
          && !rest.contains("move");
    }
    return false;
  }

  private static boolean hasAvailabilityKeywords(String lower) {
    return lower.contains("availability")
        || lower.contains("availabil")
        || lower.contains("avalaible")
        || lower.contains("available")
        || lower.contains("slot")
        || lower.contains("slots")
        || lower.contains("free time")
        || lower.contains("free slot")
        || lower.contains("khali")
        || lower.contains("fetch slot")
        || lower.contains("check slot")
        || lower.contains("kab free")
        || lower.contains("kab mil")
        || lower.contains("samay");
  }

  private static boolean mentionsSlots(String lower) {
    return lower.contains("slot") || lower.contains("free") || lower.contains("available")
        || lower.contains("avail") || lower.contains("on 20");
  }

  private static String needDateHint(String lang) {
    return "hi".equals(lang)
        ? "Kis din ke slots? Try: slots tomorrow, slots on 29, ya sirf 2026-05-29"
        : "Which day? Try: slots tomorrow, slots on 29, or just send the date (e.g. 29 or 2026-05-29)";
  }

  private static String fridayHint(String lang) {
    return "hi".equals(lang)
        ? "Friday par slot nahi. Doosra din bhejo — jaise slots on 26 ya slots tomorrow"
        : "No slots on Friday. Pick another day — e.g. slots tomorrow or slots on 26";
  }
}
