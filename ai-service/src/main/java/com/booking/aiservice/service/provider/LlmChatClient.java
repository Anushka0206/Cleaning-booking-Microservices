package com.booking.aiservice.service.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.client.RestClient;

import com.booking.aiservice.exception.AiException;
import com.booking.aiservice.model.dto.request.ChatHistoryItem;
import com.booking.aiservice.service.chat.ChatContextResolver;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * OpenAI Chat Completions API.
 */
public final class LlmChatClient {

  private LlmChatClient() {}

  public static String chat(
      RestClient restClient,
      String baseUrl,
      String apiKey,
      String model,
      String systemPrompt,
      String userMessage,
      List<ChatHistoryItem> history
  ) {
    String url = baseUrl.endsWith("/") ? baseUrl + "chat/completions" : baseUrl + "/chat/completions";

    List<Map<String, String>> messages = new ArrayList<>();
    messages.add(Map.of("role", "system", "content", systemPrompt));
    for (ChatHistoryItem item : ChatContextResolver.recentHistory(history)) {
      String role = "user".equalsIgnoreCase(item.role()) ? "user" : "assistant";
      messages.add(Map.of("role", role, "content", item.content().trim()));
    }
    messages.add(Map.of("role", "user", "content", userMessage));

    Map<String, Object> body = Map.of(
        "model", model,
        "temperature", 0.7,
        "messages", messages
    );

    try {
      ChatResponse response = restClient.post()
          .uri(url)
          .header("Content-Type", "application/json")
          .header("Authorization", "Bearer " + apiKey)
          .body(body)
          .retrieve()
          .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (req, res) -> {
            String errBody = "";
            try {
              errBody = new String(res.getBody().readAllBytes());
            } catch (Exception ignored) {
              // ignore
            }
            throw new AiException(model + " HTTP " + res.getStatusCode().value() + ": " + errBody);
          })
          .body(ChatResponse.class);

      if (response == null
          || response.choices == null
          || response.choices.isEmpty()
          || response.choices.getFirst().message == null
          || response.choices.getFirst().message.content == null
          || response.choices.getFirst().message.content.isBlank()) {
        throw new AiException("Empty response from " + model);
      }
      return response.choices.getFirst().message.content.trim();
    } catch (AiException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new AiException("LLM call failed: " + ex.getMessage(), ex);
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  static class ChatResponse {
    public List<Choice> choices;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  static class Choice {
    public Message message;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  static class Message {
    public String content;
  }
}
