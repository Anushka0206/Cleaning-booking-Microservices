package com.booking.notificationservice.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.booking.notificationservice.service.NotificationAppService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class BookingEventsListener {

  private static final Logger log = LoggerFactory.getLogger(BookingEventsListener.class);

  private final NotificationAppService notificationAppService;
  private final ObjectMapper objectMapper;

  public BookingEventsListener(NotificationAppService notificationAppService, ObjectMapper objectMapper) {
    this.notificationAppService = notificationAppService;
    this.objectMapper = objectMapper;
  }

  @KafkaListener(topics = "${justlife.kafka.booking-events-topic}")
  public void onMessage(String payload) {
    try {
      JsonNode root = objectMapper.readTree(payload);
      String type = root.path("type").asText("");
      log.info("Received booking event type={} bookingId={}", type, root.path("bookingId").asText("?"));
      if ("BOOKING_CREATED".equals(type) || "BOOKING_RESCHEDULED".equals(type)) {
        notificationAppService.notifyCleanersAssigned(root, type);
      } else if ("BOOKING_CANCELLED".equals(type)) {
        notificationAppService.notifyCleanersCancelled(root);
      } else {
        log.warn("Ignoring unknown booking event type: {}", type);
      }
    } catch (Exception ex) {
      log.error("Failed to process booking event payload={}", payload, ex);
    }
  }
}
