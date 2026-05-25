package com.booking.aiservice.service.booking;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

class ChatBookingParserTest {

  @Test
  void parsesStructuredBookCommand() {
    var result = ChatBookingParser.parse("book standard 2026-06-10 14:00", "", "en");
    assertInstanceOf(ChatBookingParseResult.Ready.class, result);
  }

  @Test
  void ignoresNonBookMessages() {
    var result = ChatBookingParser.parse("how to book a service?", "", "en");
    assertInstanceOf(ChatBookingParseResult.NotBooking.class, result);
  }
}
