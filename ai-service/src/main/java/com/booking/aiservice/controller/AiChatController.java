package com.booking.aiservice.controller;

import com.booking.aiservice.model.dto.request.ChatRequest;
import com.booking.aiservice.model.dto.response.ChatResponse;
import com.booking.aiservice.service.AiChatService;
import com.booking.common.model.dto.response.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AI Chat", description = "Cleaning booking assistant")
@RestController
@RequestMapping("/api/ai")
public class AiChatController {

  private final AiChatService aiChatService;

  public AiChatController(AiChatService aiChatService) {
    this.aiChatService = aiChatService;
  }

  @Operation(
      summary = "Chat with AI assistant",
      description = "OpenAI ChatGPT or offline FAQ — see ai-service/.env"
  )
  @PostMapping("/chat")
  public CustomResponse<ChatResponse> chat(
      @Valid @RequestBody ChatRequest request,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization
  ) {
    return CustomResponse.successOf(aiChatService.chat(request, authorization));
  }
}
