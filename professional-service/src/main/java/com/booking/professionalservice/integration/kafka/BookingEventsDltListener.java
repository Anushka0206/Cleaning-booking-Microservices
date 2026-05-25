package com.booking.professionalservice.integration.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

/**
 * Listener for the Dead Letter Topic (DLT / DLQ).
 *
 * <p>This is intentionally simple: it logs the failed message so it can be
 * inspected via logs and Kafdrop. In real systems you'd store it in a DB,
 * trigger alerts, or provide a replay mechanism.</p>
 */
@Service
@ConditionalOnProperty(name = "justlife.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class BookingEventsDltListener {

  private static final Logger log = LoggerFactory.getLogger(BookingEventsDltListener.class);

  @KafkaListener(
      topics = "${justlife.kafka.topics.booking-events-dlt:${justlife.kafka.topics.booking-events:booking.events}.dlt}",
      groupId = "professionals-service-dlt"
  )
  public void onDlt(
      String payload,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
      @Header(KafkaHeaders.OFFSET) long offset
  ) {
    log.error("DLQ message received: topic={} partition={} offset={} payload={}", topic, partition, offset, payload);
  }
}
