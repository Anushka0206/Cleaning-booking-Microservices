package com.booking.aiservice.config;

public final class AiEnvHelper {

  private AiEnvHelper() {}

  public static String providerMode(AiProperties properties) {
    String fromFileOrEnv = envOrProperty("AI_PROVIDER");
    if (fromFileOrEnv != null && !fromFileOrEnv.isBlank()) {
      return fromFileOrEnv.trim().toLowerCase();
    }
    if (properties.getProvider() != null && !properties.getProvider().isBlank()) {
      return properties.getProvider().trim().toLowerCase();
    }
    return "faq";
  }

  public static String openAiApiKey(AiProperties properties) {
    // Prefer .env / terminal (system property) over config-server empty defaults
    return firstNonBlank(envOrProperty("OPENAI_API_KEY"), properties.getOpenai().getApiKey());
  }

  public static String openAiModel(AiProperties properties) {
    return firstNonBlank(envOrProperty("OPENAI_MODEL"), properties.getOpenai().getModel(), "gpt-4o-mini");
  }

  public static boolean isOpenAiKeyConfigured(AiProperties properties) {
    return !openAiApiKey(properties).isEmpty();
  }

  private static String envOrProperty(String key) {
    return firstNonBlank(System.getProperty(key), System.getenv(key));
  }

  private static String firstNonBlank(String... values) {
    for (String v : values) {
      if (v != null && !v.isBlank()) {
        return v.trim();
      }
    }
    return "";
  }
}
