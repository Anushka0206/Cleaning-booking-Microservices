package com.booking.professionalservice.integration.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "justlife.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class BookingEventsListener {

  private static final Logger log = LoggerFactory.getLogger(BookingEventsListener.class);

  private final CacheManager cacheManager;
  private final ObjectMapper objectMapper;

  public BookingEventsListener(CacheManager cacheManager, ObjectMapper objectMapper) {
    this.cacheManager = cacheManager;
    this.objectMapper = objectMapper;
  }

  @KafkaListener(
      topics = "${justlife.kafka.topics.booking-events:booking.events}",
      groupId = "${spring.kafka.consumer.group-id:professionals-service}"
  )
  public void onMessage(String payload) {
    // Event-driven hook: when bookings change, invalidate any cached “roster”/lookup endpoints.
    // We parse the JSON so malformed events will be rejected and sent to the Dead Letter Queue.
    try {
      BookingEventMessage event = objectMapper.readValue(payload, BookingEventMessage.class);
      if (event.type() == null || event.bookingId() == null) {
        throw new IllegalArgumentException("Missing required fields: type/bookingId");
      }
      log.info("Received booking event: type={} bookingId={} vehicleId={} startAt={} endAt={}",
          event.type(), event.bookingId(), event.vehicleId(), event.startAt(), event.endAt());
    } catch (Exception ex) {
      // Throwing here triggers the CommonErrorHandler which publishes to <topic>.dlt
      log.warn("Invalid booking event payload. Sending to DLQ. payload={}", payload, ex);
      throw new IllegalArgumentException("Invalid booking event payload", ex);
    }

    clear("professionals-vehicles");
    clear("professionals-cleaners-by-vehicle");
    clear("professionals-cleaners");
  }

  private void clear(String cacheName) {
    Cache cache = cacheManager.getCache(cacheName);
    if (cache != null) {
      cache.clear();
    }
  }
}
