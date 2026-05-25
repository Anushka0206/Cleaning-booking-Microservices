package com.booking.common.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public final class JwtSupport {

  public static final String AUTH_ATTRIBUTE = "authUser";

  private JwtSupport() {}

  public static String createToken(String secret, long expirationHours, AuthUserPrincipal user) {
    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    Instant now = Instant.now();
    return Jwts.builder()
        .subject(user.userId())
        .claim("email", user.email())
        .claim("role", user.role())
        .claim("cleanerId", user.cleanerId())
        .claim("name", user.name())
        .claim("phone", user.phone())
        .claim("address", user.address())
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plus(expirationHours, ChronoUnit.HOURS)))
        .signWith(key)
        .compact();
  }

  public static Optional<AuthUserPrincipal> parseToken(String secret, String token) {
    try {
      SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
      Claims claims = Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(token)
          .getPayload();
      return Optional.of(new AuthUserPrincipal(
          claims.getSubject(),
          claims.get("email", String.class),
          claims.get("role", String.class),
          claims.get("cleanerId", String.class),
          claims.get("name", String.class),
          claims.get("phone", String.class),
          claims.get("address", String.class)
      ));
    } catch (Exception ex) {
      return Optional.empty();
    }
  }
}
