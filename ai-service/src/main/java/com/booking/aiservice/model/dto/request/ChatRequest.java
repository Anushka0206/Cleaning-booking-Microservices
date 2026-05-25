package com.booking.aiservice.model.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Incoming chat message from the React assistant.
 */
public record ChatRequest(
    @NotBlank(message = "message is required")
    @Size(max = 2000, message = "message must be at most 2000 characters")
    String message,

    /** auto | en | hi | ar | ur — optional reply language */
    String language,

    /** Recent turns (current message is sent separately). Used for follow-ups like "tomorrow" or "29". */
    @Valid
    List<ChatHistoryItem> history
) {
  public ChatRequest(String message, String language) {
    this(message, language, List.of());
  }
}
