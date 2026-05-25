package com.booking.aiservice.service.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.booking.aiservice.model.dto.request.ChatHistoryItem;

/**
 * Merges the current message with recent chat turns so parsers can understand
 * short follow-ups like "tomorrow", "29", or "14:00" after a slots reply.
 */
public final class ChatContextResolver {

  private static final int MAX_HISTORY = 8;
  private static final Pattern EMBEDDED_BOOK =
      Pattern.compile("(?is).*\\bbook(?:\\s+karo|\\s+karein)?\\b\\s+(.+)$");
  private static final Pattern EMBEDDED_CANCEL =
      Pattern.compile("(?is).*\\bcancel(?:\\s+booking)?\\b\\s+(.+)$");
  private static final Pattern EMBEDDED_RESCHEDULE =
      Pattern.compile("(?is).*\\breschedule(?:\\s+booking)?\\b\\s+(.+)$");

  private ChatContextResolver() {}

  public static String normalizeMessage(String message) {
    if (message == null) {
      return "";
    }
    String trimmed = message.trim();
    if (trimmed.isEmpty()) {
      return trimmed;
    }
    String lower = trimmed.toLowerCase(Locale.ROOT);
    if (lower.startsWith("book") || lower.startsWith("cancel") || lower.startsWith("reschedule")) {
      return trimmed;
    }
    Matcher book = EMBEDDED_BOOK.matcher(trimmed);
    if (book.matches()) {
      return "book " + book.group(1).trim();
    }
    Matcher cancel = EMBEDDED_CANCEL.matcher(trimmed);
    if (cancel.matches()) {
      return "cancel " + cancel.group(1).trim();
    }
    Matcher reschedule = EMBEDDED_RESCHEDULE.matcher(trimmed);
    if (reschedule.matches()) {
      return "reschedule " + reschedule.group(1).trim();
    }
    return trimmed;
  }

  /** Text used by rule-based slot/booking parsers (current + recent turns). */
  public static String contextForParsing(String message, List<ChatHistoryItem> history) {
    String normalized = normalizeMessage(message);
    List<String> parts = new ArrayList<>();
    parts.add(normalized);
    for (ChatHistoryItem item : recentHistory(history)) {
      if (item.content() != null && !item.content().isBlank()) {
        parts.add(item.content().trim());
      }
    }
    return String.join("\n", parts);
  }

  /** Prior turns formatted for the LLM (oldest first, excludes the current message). */
  public static List<ChatHistoryItem> recentHistory(List<ChatHistoryItem> history) {
    if (history == null || history.isEmpty()) {
      return List.of();
    }
    int from = Math.max(0, history.size() - MAX_HISTORY);
    return Collections.unmodifiableList(history.subList(from, history.size()));
  }

  public static String formatHistoryForLlm(List<ChatHistoryItem> history) {
    if (history == null || history.isEmpty()) {
      return "";
    }
    StringBuilder sb = new StringBuilder("Recent conversation:\n");
    for (ChatHistoryItem item : recentHistory(history)) {
      String role = "user".equalsIgnoreCase(item.role()) ? "User" : "Assistant";
      sb.append(role).append(": ").append(item.content().trim()).append("\n");
    }
    return sb.toString().trim();
  }
}
