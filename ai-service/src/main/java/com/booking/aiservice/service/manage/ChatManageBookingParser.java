package com.booking.aiservice.service.manage;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.booking.aiservice.service.chat.ChatDateParser;

/**
 * Parses my-bookings, cancel, and reschedule chat commands.
 */
public final class ChatManageBookingParser {

  private static final Pattern UUID =
      Pattern.compile(
          "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");

  private static final Pattern MY_BOOKINGS =
      Pattern.compile(
          "(?i)^(my\\s+bookings?|mere\\s+bookings?|show\\s+my\\s+bookings?|list\\s+my\\s+bookings?|booking\\s+list)$");

  private static final Pattern CANCEL =
      Pattern.compile("(?i)^cancel(?:\\s+booking)?\\s+(.+)$");

  private static final Pattern RESCHEDULE =
      Pattern.compile("(?i)^reschedule(?:\\s+booking)?\\s+(.+)$");

  private ChatManageBookingParser() {}

  public static ChatManageParseResult parse(String message, String contextText, String languageCode) {
    String lang = languageCode == null ? "en" : languageCode;
    if (message == null || message.isBlank()) {
      return new ChatManageParseResult.NotManage();
    }
    String current = message.trim();
    String combined = contextText == null ? current : current + "\n" + contextText;

    if (MY_BOOKINGS.matcher(current).matches()
        || looksLikeMyBookings(current.toLowerCase(Locale.ROOT))) {
      return new ChatManageParseResult.ListBookings();
    }

    Matcher cancel = CANCEL.matcher(current);
    if (cancel.matches()) {
      return resolveBookingAction(cancel.group(1).trim(), combined, lang, Action.CANCEL);
    }

    Matcher reschedule = RESCHEDULE.matcher(current);
    if (reschedule.matches()) {
      return resolveReschedule(reschedule.group(1).trim(), combined, lang);
    }

    return new ChatManageParseResult.NotManage();
  }

  private static boolean looksLikeMyBookings(String lower) {
    return (lower.contains("my booking") || lower.contains("mere booking"))
        && !lower.contains("cancel")
        && !lower.contains("reschedule");
  }

  private static ChatManageParseResult resolveBookingAction(
      String rest,
      String combined,
      String lang,
      Action action
  ) {
    Optional<String> id = extractBookingRef(rest, combined);
    if (id.isEmpty()) {
      return new ChatManageParseResult.Incomplete(hintNeedId(lang, action));
    }
    if (action == Action.CANCEL) {
      return new ChatManageParseResult.Cancel(id.get());
    }
    return new ChatManageParseResult.NotManage();
  }

  private static ChatManageParseResult resolveReschedule(String rest, String combined, String lang) {
    Optional<String> id = extractBookingRef(rest, combined);
    if (id.isEmpty()) {
      return new ChatManageParseResult.Incomplete(hintNeedId(lang, Action.RESCHEDULE));
    }

    String withoutId = stripBookingRef(rest);
    var date = ChatDateParser.parseDateWithContext(withoutId, combined);
    var time = ChatDateParser.parseTimeWithContext(withoutId, combined);
    if (date.isEmpty() || time.isEmpty()) {
      return new ChatManageParseResult.Incomplete(hintNeedDateTime(lang));
    }
    LocalDateTime startAt = LocalDateTime.of(date.get(), time.get());
    return new ChatManageParseResult.Reschedule(id.get(), startAt);
  }

  private static Optional<String> extractBookingRef(String text, String combined) {
    Matcher m = UUID.matcher(text);
    if (m.find()) {
      return Optional.of(m.group());
    }
    String lower = text.toLowerCase(Locale.ROOT);
    if (lower.contains("latest") || lower.contains("last") || lower.contains("recent")) {
      return Optional.of("latest");
    }
    m = UUID.matcher(combined);
    if (m.find()) {
      return Optional.of(m.group());
    }
    return Optional.empty();
  }

  private static String stripBookingRef(String text) {
    String stripped = UUID.matcher(text).replaceFirst("").trim();
    stripped = stripped.replaceAll("(?i)\\b(latest|last|recent)\\b", "").trim();
    return stripped;
  }

  private static String hintNeedId(String lang, Action action) {
    if (action == Action.CANCEL) {
      return "hi".equals(lang)
          ? "Kaun si booking cancel karni hai? Try: cancel booking <ID> ya cancel latest (pehle my bookings)"
          : "Which booking to cancel? Try: cancel booking <ID> or cancel latest (use my bookings first)";
    }
    return "hi".equals(lang)
        ? "Kaun si booking reschedule? Try: reschedule <ID> tomorrow 2pm"
        : "Which booking? Try: reschedule <ID> tomorrow 2pm";
  }

  private static String hintNeedDateTime(String lang) {
    return "hi".equals(lang)
        ? "Naya date/time batao: reschedule <ID> tomorrow 2pm"
        : "Add new date & time: reschedule <ID> tomorrow 2pm";
  }

  private enum Action { CANCEL, RESCHEDULE }
}
