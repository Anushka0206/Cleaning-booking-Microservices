package com.booking.professionalservice.integration.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Kafka Dead Letter Queue (DLQ) setup.
 *
 * <p>Any exception thrown from a @KafkaListener will be handled by this
 * CommonErrorHandler. After a small retry, the record is published to a
 * dedicated DLT topic (DLQ) so we don't block the consumer group.</p>
 */
@Configuration
public class KafkaDlqConfig {

  private static final Logger log = LoggerFactory.getLogger(KafkaDlqConfig.class);

  @Bean
  public CommonErrorHandler kafkaCommonErrorHandler(
          KafkaTemplate<Object, Object> kafkaTemplate,
          Environment env
  ) {
    String configuredDltTopic = env.getProperty("justlife.kafka.topics.booking-events-dlt");

    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
            kafkaTemplate,
            (record, ex) -> {
              String dltTopic = (configuredDltTopic != null && !configuredDltTopic.isBlank())
                      ? configuredDltTopic
                      : record.topic() + ".dlt";
              return new TopicPartition(dltTopic, record.partition());
            }
    );

    // Retry twice with 1s interval, then publish to DLT (DLQ)
    DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2L));
    handler.addNotRetryableExceptions(IllegalArgumentException.class, JsonProcessingException.class);

    handler.setRetryListeners((record, ex, deliveryAttempt) -> {
      log.warn("Kafka listener failed (attempt {}): topic={} partition={} offset={} key={} error={}",
              deliveryAttempt,
              record.topic(),
              record.partition(),
              record.offset(),
              record.key(),
              ex.toString());
    });

    return handler;
  }
}
