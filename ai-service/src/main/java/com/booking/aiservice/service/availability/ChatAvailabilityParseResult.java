package com.booking.aiservice.service.availability;

import java.time.LocalDate;

public sealed interface ChatAvailabilityParseResult {

  record NotQuery() implements ChatAvailabilityParseResult {}

  record NeedDate(String hint) implements ChatAvailabilityParseResult {}

  record Ready(LocalDate date) implements ChatAvailabilityParseResult {}
}
