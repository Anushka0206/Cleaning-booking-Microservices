package com.booking.aiservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.booking.aiservice.service.provider.AiProvider;

@Component
public class AiStartupLogger implements ApplicationRunner {

  private static final Logger log = LoggerFactory.getLogger(AiStartupLogger.class);

  private final AiProperties properties;
  private final AiProvider activeProvider;

  public AiStartupLogger(
      AiProperties properties,
      @Qualifier("activeAiProvider") AiProvider activeProvider
  ) {
    this.properties = properties;
    this.activeProvider = activeProvider;
  }

  @Override
  public void run(ApplicationArguments args) {
    log.info(
        "AI mode={} active={} openaiKeyConfigured={}",
        AiEnvHelper.providerMode(properties),
        activeProvider.name(),
        AiEnvHelper.isOpenAiKeyConfigured(properties)
    );
    if ("openai".equals(AiEnvHelper.providerMode(properties))) {
      String key = AiEnvHelper.openAiApiKey(properties);
      if (key.isEmpty()) {
        log.warn("AI_PROVIDER=openai but OPENAI_API_KEY missing in ai-service/.env");
      } else if (key.contains("PASTE_") || key.contains("your-key")) {
        log.warn("OPENAI_API_KEY is still a placeholder in ai-service/.env — save file and restart ai-service");
      } else {
        log.info("OpenAI key loaded (prefix {}...)", key.length() >= 7 ? key.substring(0, 7) : "short");
      }
    }
  }
}
