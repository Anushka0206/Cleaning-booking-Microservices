package com.booking.aiservice.service.provider;

import java.util.List;

import com.booking.aiservice.model.dto.request.ChatHistoryItem;

/**
 * Pluggable AI backend (OpenAI or offline FAQ).
 */
public interface AiProvider {

  String name();

  String chat(String userMessage, String languageCode);

  default String chat(String userMessage, String languageCode, List<ChatHistoryItem> history) {
    return chat(userMessage, languageCode);
  }
}
