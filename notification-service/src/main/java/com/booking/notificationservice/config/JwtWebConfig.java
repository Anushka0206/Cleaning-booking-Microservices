package com.booking.notificationservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.booking.common.security.JwtAuthFilter;

@Configuration
public class JwtWebConfig {

  @Bean
  FilterRegistrationBean<JwtAuthFilter> jwtAuthFilter(@Value("${justlife.jwt.secret}") String secret) {
    FilterRegistrationBean<JwtAuthFilter> bean = new FilterRegistrationBean<>();
    bean.setFilter(new JwtAuthFilter(secret, "/actuator/**"));
    bean.addUrlPatterns("/api/notifications", "/api/notifications/*");
    bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return bean;
  }
}
