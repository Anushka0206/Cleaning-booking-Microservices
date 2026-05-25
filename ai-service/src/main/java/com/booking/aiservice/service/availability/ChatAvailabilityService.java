package com.booking.aiservice.service.availability;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.booking.aiservice.integration.BookingApiClient;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class ChatAvailabilityService {

  private final BookingApiClient bookingApiClient;

  public ChatAvailabilityService(BookingApiClient bookingApiClient) {
    this.bookingApiClient = bookingApiClient;
  }

  public Optional<String> tryReplyWithSlots(String message, String contextText, String languageCode) {
    ChatAvailabilityParseResult parsed = ChatAvailabilityParser.parse(message, contextText, languageCode);
    if (parsed instanceof ChatAvailabilityParseResult.NotQuery) {
      return Optional.empty();
    }
    if (parsed instanceof ChatAvailabilityParseResult.NeedDate need) {
      return Optional.of(need.hint());
    }

    ChatAvailabilityParseResult.Ready ready = (ChatAvailabilityParseResult.Ready) parsed;
    try {
      JsonNode body = bookingApiClient.fetchAvailabilityByDate(ready.date());
      JsonNode response = body.path("response");
      String formatted = ChatAvailabilityFormatter.format(languageCode, ready.date(), response.path("vehicles"));
      return Optional.of(formatted);
    } catch (Exception ex) {
      String detail = bookingApiClient.extractErrorMessage(ex);
      String reply = "hi".equals(languageCode)
          ? "Slots load nahi ho paye: " + detail + " Booking-service (8082) chalu hai?"
          : "Could not load slots: " + detail + " Is booking-service running on 8082?";
      return Optional.of(reply);
    }
  }
}
