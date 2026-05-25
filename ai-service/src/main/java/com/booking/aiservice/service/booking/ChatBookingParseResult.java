package com.booking.aiservice.service.booking;

import java.time.LocalDateTime;

public sealed interface ChatBookingParseResult {

  record NotBooking() implements ChatBookingParseResult {}

  record Incomplete(String hint) implements ChatBookingParseResult {}

  record Ready(ChatBookingPackage pkg, LocalDateTime startAt) implements ChatBookingParseResult {}
}
