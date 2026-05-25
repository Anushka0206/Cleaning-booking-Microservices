package com.booking.aiservice.service.availability;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

class ChatAvailabilityParserTest {

  @Test
  void parsesSlotsOnDate() {
    var result = ChatAvailabilityParser.parse("avalaible slots on 2026-05-26", "", "en");
    assertInstanceOf(ChatAvailabilityParseResult.Ready.class, result);
  }

  @Test
  void bookOnDateWithoutServiceIsAvailability() {
    var result = ChatAvailabilityParser.parse("book on 2026-05-26", "", "en");
    assertInstanceOf(ChatAvailabilityParseResult.Ready.class, result);
  }

  @Test
  void howToCheckWithoutDateNeedsDate() {
    var result = ChatAvailabilityParser.parse("availability kaise check karu?", "", "hi");
    assertInstanceOf(ChatAvailabilityParseResult.NeedDate.class, result);
  }

  @Test
  void shortDateFollowUpUsesContext() {
    var result = ChatAvailabilityParser.parse(
        "tomorrow",
        "Which date? slots on 2026-05-26",
        "en"
    );
    assertInstanceOf(ChatAvailabilityParseResult.Ready.class, result);
  }
}
