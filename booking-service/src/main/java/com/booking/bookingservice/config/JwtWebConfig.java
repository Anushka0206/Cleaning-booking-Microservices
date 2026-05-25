package com.booking.bookingservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.booking.common.security.JwtAuthFilter;

@Configuration
public class JwtWebConfig {

  @Bean
  FilterRegistrationBean<JwtAuthFilter> jwtAuthFilter(
      @Value("${justlife.jwt.secret}") String jwtSecret
  ) {
    FilterRegistrationBean<JwtAuthFilter> bean = new FilterRegistrationBean<>();
    bean.setFilter(new JwtAuthFilter(
        jwtSecret,
        "/api/availability",
        "/api/availability/**",
        "/actuator/**",
        "/swagger-ui/**",
        "/v3/api-docs/**"
    ));
    bean.addUrlPatterns("/api/bookings", "/api/bookings/*");
    bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return bean;
  }
}
