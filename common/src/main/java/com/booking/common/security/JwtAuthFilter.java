package com.booking.common.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Validates Bearer JWT and sets {@link #AUTH_ATTRIBUTE} on the request.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

  private final String jwtSecret;
  private final List<String> publicPatterns;
  private final AntPathMatcher matcher = new AntPathMatcher();

  public JwtAuthFilter(String jwtSecret, String... publicPatterns) {
    this.jwtSecret = jwtSecret;
    this.publicPatterns = List.of(publicPatterns);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    for (String pattern : publicPatterns) {
      if (matcher.match(pattern, path)) {
        return true;
      }
    }
    return false;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header == null || !header.startsWith("Bearer ")) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing Bearer token");
      return;
    }
    String token = header.substring(7);
    var user = JwtSupport.parseToken(jwtSecret, token);
    if (user.isEmpty()) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
      return;
    }
    request.setAttribute(JwtSupport.AUTH_ATTRIBUTE, user.get());
    filterChain.doFilter(request, response);
  }

  public static AuthUserPrincipal requireUser(HttpServletRequest request) {
    Object attr = request.getAttribute(JwtSupport.AUTH_ATTRIBUTE);
    if (attr instanceof AuthUserPrincipal principal) {
      return principal;
    }
    throw new IllegalStateException("Not authenticated");
  }
}
