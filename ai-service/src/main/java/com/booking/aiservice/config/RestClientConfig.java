package com.booking.aiservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

  /** Plain client for external APIs (OpenAI). */
  @Bean
  public RestClient restClient() {
    return RestClient.builder().build();
  }

  /**
   * Direct booking-service URL (avoids {@code @LoadBalanced} breaking Eureka registration).
   */
  @Bean
  RestClient bookingRestClient(
      @Value("${justlife.booking.base-url:http://localhost:8082}") String bookingBaseUrl
  ) {
    return RestClient.builder().baseUrl(bookingBaseUrl).build();
  }
}
