package com.booking.aiservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.booking.aiservice.exception.AiException;
import java.util.List;

import com.booking.aiservice.model.dto.request.ChatHistoryItem;
import com.booking.aiservice.model.dto.request.ChatRequest;
import com.booking.aiservice.model.dto.response.ChatResponse;
import com.booking.aiservice.service.availability.ChatAvailabilityService;
import com.booking.aiservice.service.booking.ChatBookingService;
import com.booking.aiservice.service.manage.ChatManageBookingService;
import com.booking.aiservice.service.booking.ChatBookingService.ChatBookingOutcome;
import com.booking.aiservice.service.chat.ChatContextResolver;
import com.booking.aiservice.service.knowledge.LanguageHelper;
import com.booking.aiservice.service.provider.AiProvider;
import com.booking.aiservice.service.provider.FaqAiProvider;

@Service
public class AiChatService {

  private static final Logger log = LoggerFactory.getLogger(AiChatService.class);

  private final AiProvider activeProvider;
  private final FaqAiProvider faqFallback;
  private final ChatBookingService chatBookingService;
  private final ChatAvailabilityService chatAvailabilityService;
  private final ChatManageBookingService chatManageBookingService;

  public AiChatService(
      @Qualifier("activeAiProvider") AiProvider activeProvider,
      FaqAiProvider faqFallback,
      ChatBookingService chatBookingService,
      ChatAvailabilityService chatAvailabilityService,
      ChatManageBookingService chatManageBookingService
  ) {
    this.activeProvider = activeProvider;
    this.faqFallback = faqFallback;
    this.chatBookingService = chatBookingService;
    this.chatAvailabilityService = chatAvailabilityService;
    this.chatManageBookingService = chatManageBookingService;
  }

  public ChatResponse chat(ChatRequest request, String authorizationHeader) {
    String trimmed = ChatContextResolver.normalizeMessage(request.message().trim());
    String language = LanguageHelper.resolve(request.language(), trimmed);
    List<ChatHistoryItem> history = request.history() == null ? List.of() : request.history();
    String contextText = ChatContextResolver.contextForParsing(trimmed, history);

    var slotsReply = chatAvailabilityService.tryReplyWithSlots(trimmed, contextText, language);
    if (slotsReply.isPresent()) {
      return new ChatResponse(slotsReply.get(), "availability-api");
    }

    var manageReply = chatManageBookingService.tryHandle(trimmed, contextText, language, authorizationHeader);
    if (manageReply.isPresent()) {
      return new ChatResponse(manageReply.get(), "booking-manage");
    }

    var bookingOutcome = chatBookingService.tryBookFromChat(
        trimmed,
        contextText,
        language,
        authorizationHeader
    );
    if (bookingOutcome.isPresent()) {
      ChatBookingOutcome o = bookingOutcome.get();
      return new ChatResponse(o.reply(), o.provider(), o.booking());
    }

    String llmUserMessage = buildLlmUserMessage(trimmed, history);

    try {
      String reply = activeProvider.chat(llmUserMessage, language, history);
      return new ChatResponse(reply, activeProvider.name());
    } catch (AiException ex) {
      log.warn("AI provider {} failed, FAQ fallback: {}", activeProvider.name(), ex.getMessage());
      if ("faq".equals(activeProvider.name())) {
        throw ex;
      }
      String reply = faqFallback.chat(llmUserMessage, language, history);
      return new ChatResponse(reply, "faq");
    }
  }

  private static String buildLlmUserMessage(String trimmed, List<ChatHistoryItem> history) {
    String historyBlock = ChatContextResolver.formatHistoryForLlm(history);
    if (historyBlock.isBlank()) {
      return trimmed;
    }
    return historyBlock + "\n\nLatest user message: " + trimmed;
  }
}
