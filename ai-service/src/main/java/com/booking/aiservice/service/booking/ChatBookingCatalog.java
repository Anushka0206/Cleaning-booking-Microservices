package com.booking.aiservice.service.booking;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class ChatBookingCatalog {

  private static final List<ChatBookingPackage> PACKAGES = List.of(
      new ChatBookingPackage("basic-2h", "Basic Home Cleaning", 2, 1, 49, "basic", "basic home"),
      new ChatBookingPackage("standard-2h", "Standard Cleaning (2 Pros)", 2, 2, 89, "standard", "standard cleaning"),
      new ChatBookingPackage("deep-4h", "Deep Cleaning", 4, 2, 129, "deep", "deep cleaning"),
      new ChatBookingPackage("premium-4h", "Premium Team Clean", 4, 3, 179, "premium", "premium team"),
      new ChatBookingPackage("office-2h", "Small Office Cleaning", 2, 1, 69, "office", "office cleaning"),
      new ChatBookingPackage("move-4h", "Move-in / Move-out", 4, 2, 149, "move", "move-in", "move in", "move-out", "move out")
  );

  private ChatBookingCatalog() {}

  public static List<ChatBookingPackage> all() {
    return PACKAGES;
  }

  public static Optional<ChatBookingPackage> match(String text) {
    String lower = text.toLowerCase();
    return PACKAGES.stream()
        .flatMap(pkg -> Arrays.stream(pkg.keywords()).map(k -> new Object[] { k, pkg }))
        .sorted(Comparator.comparingInt(a -> -((String) a[0]).length()))
        .filter(a -> lower.contains((String) a[0]))
        .map(a -> (ChatBookingPackage) a[1])
        .findFirst();
  }

  public static String packagesHint() {
    return "basic | standard | deep | premium | office | move";
  }
}
