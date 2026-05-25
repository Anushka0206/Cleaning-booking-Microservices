package com.booking.aiservice.service.manage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.booking.aiservice.integration.BookingApiClient;
import com.booking.aiservice.integration.BookingApiClient.BookingSummary;
import com.booking.aiservice.service.knowledge.AvailabilityGuidance;

@Service
public class ChatManageBookingService {

  private final BookingApiClient bookingApiClient;

  public ChatManageBookingService(BookingApiClient bookingApiClient) {
    this.bookingApiClient = bookingApiClient;
  }

  public Optional<String> tryHandle(String message, String contextText, String languageCode, String bearerToken) {
    ChatManageParseResult parsed = ChatManageBookingParser.parse(message, contextText, languageCode);
    if (parsed instanceof ChatManageParseResult.NotManage) {
      return Optional.empty();
    }
    if (parsed instanceof ChatManageParseResult.Incomplete inc) {
      return Optional.of(inc.hint());
    }

    if (bearerToken == null || bearerToken.isBlank()) {
      return Optional.of(loginHint(languageCode));
    }

    try {
      if (parsed instanceof ChatManageParseResult.ListBookings) {
        List<BookingSummary> list = bookingApiClient.listMyBookings(bearerToken);
        return Optional.of(ChatBookingsFormatter.formatList(languageCode, list));
      }
      if (parsed instanceof ChatManageParseResult.Cancel cancel) {
        var resolved = resolveBookingId(cancel.bookingRef(), bearerToken, languageCode);
        if (resolved.error().isPresent()) {
          return Optional.of(resolved.error().get());
        }
        BookingSummary result = bookingApiClient.cancelBooking(resolved.id().get(), bearerToken);
        return Optional.of(successCancel(languageCode, result));
      }
      if (parsed instanceof ChatManageParseResult.Reschedule res) {
        var resolved = resolveBookingId(res.bookingRef(), bearerToken, languageCode);
        if (resolved.error().isPresent()) {
          return Optional.of(resolved.error().get());
        }
        String bookingId = resolved.id().get();
        int duration = resolveDurationHours(bookingId, bearerToken);
        BookingSummary result = bookingApiClient.rescheduleBooking(
            bookingId,
            res.newStartAt(),
            duration,
            bearerToken
        );
        return Optional.of(successReschedule(languageCode, result));
      }
    } catch (Exception ex) {
      String detail = bookingApiClient.extractErrorMessage(ex);
      return Optional.of(fail(languageCode, detail));
    }
    return Optional.empty();
  }

  private record ResolvedBookingId(Optional<String> id, Optional<String> error) {}

  private ResolvedBookingId resolveBookingId(String ref, String bearerToken, String lang) {
    if (!"latest".equalsIgnoreCase(ref)) {
      return new ResolvedBookingId(Optional.of(ref), Optional.empty());
    }
    List<BookingSummary> list = bookingApiClient.listMyBookings(bearerToken);
    Optional<String> latest = list.stream()
        .filter(b -> !"CANCELLED".equalsIgnoreCase(b.status()))
        .max(Comparator.comparing(BookingSummary::startAt))
        .map(BookingSummary::id);
    if (latest.isPresent()) {
      return new ResolvedBookingId(latest, Optional.empty());
    }
    return new ResolvedBookingId(
        Optional.empty(),
        Optional.of("hi".equals(lang)
            ? "Koi active booking nahi — pehle my bookings dekho."
            : "No active booking found — try my bookings first.")
    );
  }

  private int resolveDurationHours(String bookingId, String bearerToken) {
    return bookingApiClient.listMyBookings(bearerToken).stream()
        .filter(b -> bookingId.equals(b.id()))
        .map(BookingSummary::durationHours)
        .findFirst()
        .orElse(2);
  }

  private static String loginHint(String lang) {
    return "hi".equals(lang)
        ? "My bookings / cancel / reschedule ke liye pehle login karein."
        : "Please log in to see, cancel, or reschedule your bookings."
        + AvailabilityGuidance.loginRequiredHint(lang);
  }

  private static String successCancel(String lang, BookingSummary b) {
    return "hi".equals(lang)
        ? "Booking cancel ho gayi. ID: " + b.id() + " — " + b.startAt() + " (" + b.status() + ")"
        : "Booking cancelled. ID: " + b.id() + " — " + b.startAt() + " (" + b.status() + ")";
  }

  private static String successReschedule(String lang, BookingSummary b) {
    return "hi".equals(lang)
        ? "Booking reschedule ho gayi! Naya time: " + b.startAt() + " (ID: " + b.id() + ")"
        : "Booking rescheduled! New time: " + b.startAt() + " (ID: " + b.id() + ")";
  }

  private static String fail(String lang, String detail) {
    return "hi".equals(lang)
        ? "Action fail: " + detail
        : "Could not complete: " + detail;
  }
}
