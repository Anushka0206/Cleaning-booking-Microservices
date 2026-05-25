package com.booking.common.security;

/**
 * Authenticated user from JWT (customer or cleaner).
 */
public record AuthUserPrincipal(
    String userId,
    String email,
    String role,
    String cleanerId,
    String name,
    String phone,
    String address
) {
  public boolean isCustomer() {
    return "CUSTOMER".equalsIgnoreCase(role);
  }

  public boolean isCleaner() {
    return "CLEANER".equalsIgnoreCase(role);
  }
}
