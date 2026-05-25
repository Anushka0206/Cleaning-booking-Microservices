package com.booking.aiservice.service.manage;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

class ChatManageBookingParserTest {

  @Test
  void parsesMyBookings() {
    var r = ChatManageBookingParser.parse("my bookings", "", "en");
    assertInstanceOf(ChatManageParseResult.ListBookings.class, r);
  }

  @Test
  void parsesCancelLatest() {
    var r = ChatManageBookingParser.parse("cancel latest", "", "en");
    assertInstanceOf(ChatManageParseResult.Cancel.class, r);
  }

  @Test
  void parsesRescheduleWithDateTime() {
    var r = ChatManageBookingParser.parse("reschedule latest tomorrow 2pm", "", "en");
    assertInstanceOf(ChatManageParseResult.Reschedule.class, r);
  }
}
