package com.booking.aiservice.service.knowledge;

/**
 * User-facing steps: always check availability before booking.
 */
public final class AvailabilityGuidance {

  private AvailabilityGuidance() {}

  public static String steps(String lang) {
    if ("hi".equals(lang)) {
      return """
          Pehle slots check karein (booking se pehle zaroori):
          1) Navbar → Availability → date choose karein → "Fetch slots"
          2) Green / free start times note karein (08:00–22:00; Friday band)
          3) Phir book karein:
             • Website: Services → package → Book Now → wahi date/time → "Check availability" → Confirm
             • Chat: book standard tomorrow 2pm (login first)
          Slot full ho to booking fail ho sakti hai — doosra time try karein.""";
    }
    return """
        Check availability before you book (important):
        1) Navbar → Availability → pick a date → click "Fetch slots"
        2) Note free start times shown (08:00–22:00; no Fridays)
        3) Then book:
           • Website: Services → package → Book Now → same date/time → "Check availability" → Confirm
           • Chat: book standard 2026-05-26 14:00
        If the slot is full, booking will fail — pick another time from Availability.""";
  }

  public static String shortReminder(String lang) {
    return "hi".equals(lang)
        ? "Tip: next time pehle Availability page se slots dekho, phir book karo."
        : "Tip: next time check the Availability page for slots before booking.";
  }

  public static String noSlotHint(String lang) {
    return "hi".equals(lang)
        ? " Availability page par doosra date/time dekho, phir dubara book karein."
        : " Open Availability, pick another date/time, then book again.";
  }

  public static String bookingHint(String lang) {
    return "hi".equals(lang)
        ? """
            Pehle slots dekho: slots tomorrow ya slots on 29
            Phir book: book standard tomorrow 2pm (login zaroori)
            Service: basic | standard | deep | premium | office | move"""
        : """
            First check slots: slots tomorrow or slots on 29
            Then book: book standard tomorrow 2pm (login required)
            Services: basic | standard | deep | premium | office | move""";
  }

  public static String loginRequiredHint(String lang) {
    return "hi".equals(lang)
        ? " Pehle website par Login karein, phir dubara book try karein."
        : " Please log in on the website first, then try booking again.";
  }
}
