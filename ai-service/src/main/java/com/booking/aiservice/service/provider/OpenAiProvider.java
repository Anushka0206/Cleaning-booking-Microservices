package com.booking.aiservice.service.provider;

import org.springframework.web.client.RestClient;

import java.util.List;

import com.booking.aiservice.config.AiEnvHelper;
import com.booking.aiservice.config.AiProperties;
import com.booking.aiservice.exception.AiException;
import com.booking.aiservice.model.dto.request.ChatHistoryItem;
import com.booking.aiservice.service.knowledge.ServiceCatalogKnowledge;

/**
 * OpenAI ChatGPT API (paid — needs OPENAI_API_KEY in .env).
 */
public class OpenAiProvider implements AiProvider {

  private final AiProperties properties;
  private final RestClient restClient;

  public OpenAiProvider(AiProperties properties, RestClient restClient) {
    this.properties = properties;
    this.restClient = restClient;
  }

  @Override
  public String name() {
    return "openai";
  }

  @Override
  public String chat(String userMessage, String languageCode) {
    return chat(userMessage, languageCode, List.of());
  }

  @Override
  public String chat(String userMessage, String languageCode, List<ChatHistoryItem> history) {
    String apiKey = AiEnvHelper.openAiApiKey(properties);
    if (apiKey.isEmpty()) {
      throw new AiException("OPENAI_API_KEY missing in ai-service/.env");
    }
    String model = AiEnvHelper.openAiModel(properties);
    String baseUrl = properties.getOpenai().getBaseUrl();
    String system = ServiceCatalogKnowledge.systemPrompt(languageCode);
    return LlmChatClient.chat(restClient, baseUrl, apiKey, model, system, userMessage, history);
  }
}
