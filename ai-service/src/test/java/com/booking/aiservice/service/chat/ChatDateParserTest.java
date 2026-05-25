package com.booking.aiservice.service.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

class ChatDateParserTest {

  @Test
  void parsesTomorrowAndDayOnly() {
    LocalDate tomorrow = LocalDate.now().plusDays(1);
    assertEquals(tomorrow, ChatDateParser.parseDate("slots tomorrow").orElseThrow());
    assertTrue(ChatDateParser.parseDate("29").isPresent());
  }

  @Test
  void parsesTimeWithContext() {
    assertEquals(
        LocalTime.of(14, 0),
        ChatDateParser.parseTimeWithContext("book standard 2pm", "slots on 2026-05-29").orElseThrow()
    );
  }

  @Test
  void dateFromContextWhenCurrentMessageIsShort() {
    LocalDate fromContext = ChatDateParser.parseDateWithContext(
        "tomorrow",
        "slots on 2026-05-29"
    ).orElseThrow();
    assertEquals(LocalDate.now().plusDays(1), fromContext);
  }
}
