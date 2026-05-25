package com.booking.aiservice.integration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class BookingApiClient {

  private final RestClient bookingRestClient;
  private final ObjectMapper objectMapper;

  public BookingApiClient(RestClient bookingRestClient, ObjectMapper objectMapper) {
    this.bookingRestClient = bookingRestClient;
    this.objectMapper = objectMapper;
  }

  public record BookingCreated(
      String id,
      LocalDateTime startAt,
      LocalDateTime endAt,
      int durationHours,
      String vehicleId
  ) {}

  public record BookingSummary(
      String id,
      LocalDateTime startAt,
      LocalDateTime endAt,
      int durationHours,
      String vehicleId,
      String status
  ) {}

  public JsonNode fetchAvailabilityByDate(LocalDate date) {
    String json = bookingRestClient.get()
        .uri(uriBuilder -> uriBuilder.path("/api/availability").queryParam("date", date.toString()).build())
        .retrieve()
        .body(String.class);
    return parseJson(json);
  }

  public BookingCreated create(
      LocalDateTime startAt,
      int durationHours,
      int professionalCount,
      String bearerToken
  ) {
    Map<String, Object> body = Map.of(
        "startAt", startAt.toString(),
        "durationHours", durationHours,
        "professionalCount", professionalCount
    );

    String json = withAuth(
        bookingRestClient.post()
            .uri("/api/bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .body(body),
        bearerToken
    ).retrieve().body(String.class);

    return toCreated(parseBookingSummaryNode(parseJson(json).path("response")));
  }

  public List<BookingSummary> listMyBookings(String bearerToken) {
    String json = withAuth(bookingRestClient.get().uri("/api/bookings/me"), bearerToken)
        .retrieve()
        .body(String.class);
    JsonNode arr = parseJson(json).path("response");
    List<BookingSummary> list = new ArrayList<>();
    if (!arr.isArray()) {
      return list;
    }
    for (JsonNode node : arr) {
      list.add(parseBookingSummaryNode(node));
    }
    return list;
  }

  public BookingSummary cancelBooking(String bookingId, String bearerToken) {
    String json = withAuth(
        bookingRestClient.post().uri("/api/bookings/{id}/cancel", bookingId),
        bearerToken
    ).retrieve().body(String.class);
    return parseBookingSummaryNode(parseJson(json).path("response"));
  }

  public BookingSummary rescheduleBooking(
      String bookingId,
      LocalDateTime newStartAt,
      int newDurationHours,
      String bearerToken
  ) {
    Map<String, Object> body = Map.of(
        "newStartAt", newStartAt.toString(),
        "newDurationHours", newDurationHours
    );
    String json = withAuth(
        bookingRestClient.put()
            .uri("/api/bookings/{id}", bookingId)
            .contentType(MediaType.APPLICATION_JSON)
            .body(body),
        bearerToken
    ).retrieve().body(String.class);
    return parseBookingSummaryNode(parseJson(json).path("response"));
  }

  public String extractErrorMessage(Exception ex) {
    if (ex instanceof HttpClientErrorException http) {
      try {
        JsonNode root = objectMapper.readTree(http.getResponseBodyAsString());
        if (root.has("detail")) {
          return root.get("detail").asText();
        }
        if (root.has("title")) {
          return root.get("title").asText();
        }
      } catch (Exception ignored) {
        // fall through
      }
      return http.getStatusCode() + ": " + http.getStatusText();
    }
    return ex.getMessage() != null ? ex.getMessage() : "Booking request failed";
  }

  private static RestClient.RequestHeadersSpec<?> withAuth(
      RestClient.RequestHeadersSpec<?> request,
      String bearerToken
  ) {
    if (bearerToken != null && !bearerToken.isBlank()) {
      String header = bearerToken.startsWith("Bearer ") ? bearerToken : "Bearer " + bearerToken;
      return request.header("Authorization", header);
    }
    return request;
  }

  private JsonNode parseJson(String json) {
    try {
      return objectMapper.readTree(json);
    } catch (Exception e) {
      throw new IllegalStateException("Could not parse JSON: " + e.getMessage(), e);
    }
  }

  private BookingCreated toCreated(BookingSummary summary) {
    return new BookingCreated(
        summary.id(),
        summary.startAt(),
        summary.endAt(),
        summary.durationHours(),
        summary.vehicleId()
    );
  }

  private BookingSummary parseBookingSummaryNode(JsonNode r) {
    if (r.isMissingNode() || r.isNull()) {
      throw new IllegalStateException("Empty booking response");
    }
    return new BookingSummary(
        r.path("id").asText(),
        LocalDateTime.parse(r.path("startAt").asText()),
        LocalDateTime.parse(r.path("endAt").asText()),
        r.path("durationHours").asInt(),
        r.path("vehicleId").asText(""),
        r.path("status").asText("ACTIVE")
    );
  }
}
