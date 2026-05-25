package com.booking.aiservice.service.manage;

import java.time.LocalDateTime;

public sealed interface ChatManageParseResult {

  record NotManage() implements ChatManageParseResult {}

  record ListBookings() implements ChatManageParseResult {}

  record Incomplete(String hint) implements ChatManageParseResult {}

  record Cancel(String bookingRef) implements ChatManageParseResult {}

  record Reschedule(String bookingRef, LocalDateTime newStartAt) implements ChatManageParseResult {}
}
