package com.booking.authservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "justlife.jwt")
public record JwtProperties(String secret, long expirationHours) {}
