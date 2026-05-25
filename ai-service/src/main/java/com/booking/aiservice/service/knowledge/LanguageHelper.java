package com.booking.aiservice.service.knowledge;

/**
 * Picks reply language: explicit UI choice, or auto-detect from user text.
 */
public final class LanguageHelper {

  private LanguageHelper() {}

  public static String resolve(String requestedLanguage, String userMessage) {
    if (requestedLanguage != null && !requestedLanguage.isBlank()
        && !"auto".equalsIgnoreCase(requestedLanguage.trim())) {
      return requestedLanguage.trim().toLowerCase();
    }
    return detectFromText(userMessage);
  }

  public static String detectFromText(String text) {
    if (text == null || text.isBlank()) {
      return "en";
    }
    if (text.codePoints().anyMatch(cp -> cp >= 0x0600 && cp <= 0x06FF)) {
      return "ar";
    }
    if (text.codePoints().anyMatch(cp -> cp >= 0x0900 && cp <= 0x097F)) {
      return "hi";
    }
    String lower = text.toLowerCase();
    if (lower.contains("kaise") || lower.contains("kya") || lower.contains("kitna")
        || lower.contains("booking") && lower.contains("kaise")) {
      return "hi";
    }
    return "en";
  }

  public static String languageInstruction(String languageCode) {
    return switch (languageCode) {
      case "hi" -> "Reply in Hindi (simple, helpful Hindi).";
      case "ar" -> "Reply in Modern Standard Arabic.";
      case "ur" -> "Reply in Urdu.";
      case "es" -> "Reply in Spanish.";
      case "fr" -> "Reply in French.";
      default -> "Reply in English.";
    };
  }
}
