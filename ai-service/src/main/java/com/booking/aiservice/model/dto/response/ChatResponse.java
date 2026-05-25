package com.booking.aiservice.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * AI reply returned to the frontend (inside {@link com.booking.common.model.dto.response.CustomResponse}).
 */
public record ChatResponse(
    String reply,
    String provider,
    @JsonInclude(JsonInclude.Include.NON_NULL) ChatBookingInfo booking
) {
  public ChatResponse(String reply, String provider) {
    this(reply, provider, null);
  }
}
