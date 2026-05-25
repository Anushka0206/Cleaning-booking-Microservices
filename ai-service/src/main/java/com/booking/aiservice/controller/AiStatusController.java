package com.booking.aiservice.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.booking.aiservice.config.AiEnvHelper;
import com.booking.aiservice.config.AiProperties;
import com.booking.aiservice.exception.AiException;
import com.booking.aiservice.service.provider.AiProvider;
import com.booking.common.model.dto.response.CustomResponse;

@RestController
@RequestMapping("/api/ai")
public class AiStatusController {

  private final AiProperties properties;
  private final AiProvider activeProvider;

  public AiStatusController(
      AiProperties properties,
      @Qualifier("activeAiProvider") AiProvider activeProvider
  ) {
    this.properties = properties;
    this.activeProvider = activeProvider;
  }

  @GetMapping("/status")
  public CustomResponse<Map<String, Object>> status(
      @RequestParam(defaultValue = "false") boolean probe
  ) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("configuredProvider", AiEnvHelper.providerMode(properties));
    body.put("activeProvider", activeProvider.name());
    body.put("openaiKeyConfigured", AiEnvHelper.isOpenAiKeyConfigured(properties));
    body.put("openaiModel", AiEnvHelper.openAiModel(properties));
    body.put("supportedProviders", "faq, openai");

    if ("faq".equals(activeProvider.name())) {
      body.put("openaiApiWorking", "n/a");
      body.put("hint", "Set AI_PROVIDER=openai and OPENAI_API_KEY in ai-service/.env");
    } else if (probe) {
      try {
        activeProvider.chat("Reply with exactly: OK", "en");
        body.put("openaiApiWorking", true);
      } catch (AiException ex) {
        body.put("openaiApiWorking", false);
        body.put("openaiError", ex.getMessage());
      }
    } else {
      body.put("openaiApiWorking", "unknown");
      body.put("hint", "Use ?probe=true to test OpenAI");
    }

    return CustomResponse.successOf(body);
  }
}
