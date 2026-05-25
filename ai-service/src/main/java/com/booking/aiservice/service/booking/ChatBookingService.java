package com.booking.aiservice.service.booking;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.booking.aiservice.integration.BookingApiClient;
import com.booking.aiservice.integration.BookingApiClient.BookingCreated;
import com.booking.aiservice.model.dto.response.ChatBookingInfo;
import com.booking.aiservice.service.knowledge.AvailabilityGuidance;
@Service
public class ChatBookingService {

  private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
  private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

  private final BookingApiClient bookingApiClient;

  public ChatBookingService(BookingApiClient bookingApiClient) {
    this.bookingApiClient = bookingApiClient;
  }

  public Optional<ChatBookingOutcome> tryBookFromChat(
      String message,
      String contextText,
      String languageCode,
      String bearerToken
  ) {
    ChatBookingParseResult parsed = ChatBookingParser.parse(message, contextText, languageCode);
    if (parsed instanceof ChatBookingParseResult.NotBooking) {
      return Optional.empty();
    }
    if (parsed instanceof ChatBookingParseResult.Incomplete inc) {
      return Optional.of(ChatBookingOutcome.help(inc.hint(), "chat-booking"));
    }

    ChatBookingParseResult.Ready ready = (ChatBookingParseResult.Ready) parsed;
    ChatBookingPackage pkg = ready.pkg();
    LocalDateTime startAt = ready.startAt();

    if (bearerToken == null || bearerToken.isBlank()) {
      String reply = "hi".equals(languageCode)
          ? "Booking ke liye pehle login karein." + AvailabilityGuidance.loginRequiredHint(languageCode)
          : "Please log in before booking." + AvailabilityGuidance.loginRequiredHint(languageCode);
      return Optional.of(ChatBookingOutcome.help(reply, "chat-booking"));
    }

    try {
      BookingCreated created = bookingApiClient.create(
          startAt,
          pkg.durationHours(),
          pkg.professionalCount(),
          bearerToken
      );
      String reply = successReply(languageCode, pkg, created);
      ChatBookingInfo info = new ChatBookingInfo(
          created.id(),
          created.startAt().toString(),
          created.endAt().toString(),
          created.durationHours(),
          created.vehicleId(),
          pkg.id(),
          pkg.name(),
          pkg.price(),
          startAt.format(DATE_FMT),
          startAt.format(TIME_FMT)
      );
      return Optional.of(new ChatBookingOutcome(reply, "chat-booking", info));
    } catch (Exception ex) {
      String detail = bookingApiClient.extractErrorMessage(ex);
      String extra = detail.contains("401") || detail.contains("UNAUTHORIZED")
          ? AvailabilityGuidance.loginRequiredHint(languageCode)
          : AvailabilityGuidance.noSlotHint(languageCode);
      String reply = "hi".equals(languageCode)
          ? "Booking fail: " + detail + "." + extra
          : "Booking failed: " + detail + "." + extra;
      return Optional.of(ChatBookingOutcome.help(reply, "chat-booking"));
    }
  }

  private static String successReply(String lang, ChatBookingPackage pkg, BookingCreated b) {
    if ("hi".equals(lang)) {
      return "Booking confirm! ID: " + b.id() + " — " + pkg.name() + " "
          + b.startAt().format(DATE_FMT) + " " + b.startAt().format(TIME_FMT)
          + ". My Bookings dekho. " + AvailabilityGuidance.shortReminder(lang);
    }
    return "Booking confirmed! ID: " + b.id() + " — " + pkg.name() + " on "
        + b.startAt().format(DATE_FMT) + " at " + b.startAt().format(TIME_FMT)
        + ". See My Bookings. " + AvailabilityGuidance.shortReminder(lang);
  }

  public record ChatBookingOutcome(String reply, String provider, ChatBookingInfo booking) {
    static ChatBookingOutcome help(String reply, String provider) {
      return new ChatBookingOutcome(reply, provider, null);
    }
  }
}
