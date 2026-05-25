package com.booking.aiservice.service.chat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ChatDateParser {

  private static final Pattern DATE_ISO = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})");
  private static final Pattern DATE_DD_MM_YYYY =
      Pattern.compile("(?i)\\b(\\d{1,2})[./-](\\d{1,2})[./-](\\d{4})\\b");
  private static final Pattern DATE_DD_MM =
      Pattern.compile("(?i)\\b(\\d{1,2})[./-](\\d{1,2})\\b(?!\\s*[./-]\\s*\\d)");
  private static final Pattern DATE_ON_DAY =
      Pattern.compile("(?i)\\b(?:on|for|date)\\s+(\\d{1,2})(?:st|nd|rd|th)?\\b");
  private static final Pattern DATE_DAY_MONTH =
      Pattern.compile(
          "(?i)\\b(\\d{1,2})(?:st|nd|rd|th)?\\s+(jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|"
              + "apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:t(?:ember)?)?|"
              + "oct(?:ober)?|nov(?:ember)?|dec(?:ember)?)\\b");
  private static final Pattern DATE_MONTH_DAY =
      Pattern.compile(
          "(?i)\\b(jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|"
              + "jul(?:y)?|aug(?:ust)?|sep(?:t(?:ember)?)?|oct(?:ober)?|nov(?:ember)?|"
              + "dec(?:ember)?)\\s+(\\d{1,2})(?:st|nd|rd|th)?\\b");
  private static final Pattern DATE_DAY_ONLY =
      Pattern.compile("^(?:\\s*(?:slots?|available|free)?\\s*)?(\\d{1,2})(?:st|nd|rd|th)?\\s*$");
  private static final Pattern TIME_24 = Pattern.compile("\\b([01]?\\d|2[0-3]):([0-5]\\d)\\b");
  private static final Pattern TIME_12 =
      Pattern.compile("(?i)\\b(1[0-2]|0?[1-9])(?::([0-5]\\d))?\\s*(am|pm)\\b");
  private static final Pattern TIME_HOUR_ONLY =
      Pattern.compile("(?i)\\b(?:at\\s+)?(\\d{1,2})\\s*(am|pm)\\b");

  private ChatDateParser() {}

  public static Optional<LocalDate> parseDate(String text) {
    return parseDateFrom(text, true);
  }

  public static Optional<LocalDate> parseDateWithContext(String current, String context) {
    Optional<LocalDate> fromCurrent = parseDateFrom(current, true);
    if (fromCurrent.isPresent()) {
      return fromCurrent;
    }
    return parseDateFrom(context, false);
  }

  public static Optional<LocalTime> parseTime(String text) {
    return parseTimeFrom(text, true);
  }

  public static Optional<LocalTime> parseTimeWithContext(String current, String context) {
    Optional<LocalTime> fromCurrent = parseTimeFrom(current, true);
    if (fromCurrent.isPresent()) {
      return fromCurrent;
    }
    return parseTimeFrom(context, false);
  }

  public static boolean looksLikeDateOnlyMessage(String text) {
    if (text == null || text.isBlank()) {
      return false;
    }
    String trimmed = text.trim();
    if (parseDateFrom(trimmed, true).isEmpty()) {
      return false;
    }
    String lower = trimmed.toLowerCase(Locale.ROOT);
    return !lower.contains("book ")
        && !lower.startsWith("book")
        && trimmed.length() <= 40;
  }

  private static Optional<LocalDate> parseDateFrom(String text, boolean allowDayOnly) {
    if (text == null || text.isBlank()) {
      return Optional.empty();
    }

    Matcher iso = DATE_ISO.matcher(text);
    if (iso.find()) {
      return safeParseIso(iso.group(1));
    }

    Matcher dmy = DATE_DD_MM_YYYY.matcher(text);
    if (dmy.find()) {
      return safeDate(
          Integer.parseInt(dmy.group(1)),
          Integer.parseInt(dmy.group(2)),
          Integer.parseInt(dmy.group(3)));
    }

    Matcher dm = DATE_DD_MM.matcher(text);
    if (dm.find()) {
      int year = LocalDate.now().getYear();
      return safeDate(
          Integer.parseInt(dm.group(1)),
          Integer.parseInt(dm.group(2)),
          year);
    }

    Matcher onDay = DATE_ON_DAY.matcher(text);
    if (onDay.find()) {
      return dayInSmartMonth(Integer.parseInt(onDay.group(1)));
    }

    Matcher dayMonth = DATE_DAY_MONTH.matcher(text);
    if (dayMonth.find()) {
      return safeDate(
          Integer.parseInt(dayMonth.group(1)),
          monthNumber(dayMonth.group(2)),
          LocalDate.now().getYear());
    }

    Matcher monthDay = DATE_MONTH_DAY.matcher(text);
    if (monthDay.find()) {
      return safeDate(
          Integer.parseInt(monthDay.group(2)),
          monthNumber(monthDay.group(1)),
          LocalDate.now().getYear());
    }

    String lower = text.toLowerCase(Locale.ROOT);
    LocalDate today = LocalDate.now();
    if (lower.contains("day after tomorrow") || lower.contains("parso")) {
      return Optional.of(today.plusDays(2));
    }
    if (lower.contains("tomorrow") || lower.contains("kal")) {
      return Optional.of(today.plusDays(1));
    }
    if (lower.contains("today") || lower.contains("aaj")) {
      return Optional.of(today);
    }

    if (allowDayOnly) {
      for (String line : text.split("\\R")) {
        Matcher dayOnly = DATE_DAY_ONLY.matcher(line.trim());
        if (dayOnly.matches()) {
          return dayInSmartMonth(Integer.parseInt(dayOnly.group(1)));
        }
      }
    }

    return Optional.empty();
  }

  private static Optional<LocalTime> parseTimeFrom(String text, boolean allowHourOnly) {
    if (text == null || text.isBlank()) {
      return Optional.empty();
    }
    Matcher t24 = TIME_24.matcher(text);
    if (t24.find()) {
      return Optional.of(
          LocalTime.of(Integer.parseInt(t24.group(1)), Integer.parseInt(t24.group(2))));
    }
    Matcher t12 = TIME_12.matcher(text);
    if (t12.find()) {
      return Optional.of(toLocalTime(t12));
    }
    if (allowHourOnly) {
      Matcher hourOnly = TIME_HOUR_ONLY.matcher(text);
      if (hourOnly.find()) {
        int hour = Integer.parseInt(hourOnly.group(1));
        String ampm = hourOnly.group(2).toLowerCase(Locale.ROOT);
        if ("pm".equals(ampm) && hour < 12) {
          hour += 12;
        }
        if ("am".equals(ampm) && hour == 12) {
          hour = 0;
        }
        return Optional.of(LocalTime.of(hour, 0));
      }
    }
    return Optional.empty();
  }

  private static LocalTime toLocalTime(Matcher t12) {
    int hour = Integer.parseInt(t12.group(1));
    int minute = t12.group(2) == null ? 0 : Integer.parseInt(t12.group(2));
    String ampm = t12.group(3).toLowerCase(Locale.ROOT);
    if ("pm".equals(ampm) && hour < 12) {
      hour += 12;
    }
    if ("am".equals(ampm) && hour == 12) {
      hour = 0;
    }
    return LocalTime.of(hour, minute);
  }

  private static Optional<LocalDate> dayInSmartMonth(int day) {
    LocalDate today = LocalDate.now();
    Optional<LocalDate> candidate = safeDate(day, today.getMonthValue(), today.getYear());
    if (candidate.isEmpty()) {
      return Optional.empty();
    }
    LocalDate date = candidate.get();
    if (!date.isBefore(today)) {
      return Optional.of(date);
    }
    Optional<LocalDate> nextMonth = safeDate(day, today.getMonthValue() + 1, today.getYear());
    return nextMonth.isPresent() ? nextMonth : Optional.of(date.plusMonths(1));
  }

  private static Optional<LocalDate> safeParseIso(String iso) {
    try {
      return Optional.of(LocalDate.parse(iso, DateTimeFormatter.ISO_LOCAL_DATE));
    } catch (DateTimeParseException ex) {
      return Optional.empty();
    }
  }

  private static Optional<LocalDate> safeDate(int day, int month, int year) {
    try {
      if (month > 12) {
        int tmp = day;
        day = month;
        month = tmp;
      }
      return Optional.of(LocalDate.of(year, month, day));
    } catch (Exception ex) {
      return Optional.empty();
    }
  }

  private static int monthNumber(String token) {
    String t = token.toLowerCase(Locale.ROOT).substring(0, 3);
    for (Month m : Month.values()) {
      if (m.name().toLowerCase(Locale.ROOT).startsWith(t)) {
        return m.getValue();
      }
    }
    throw new IllegalArgumentException("Unknown month: " + token);
  }
}
