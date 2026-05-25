package com.booking.aiservice.service.knowledge;

/**
 * Static catalog aligned with the React {@code services.js} list.
 * Used as system context for OpenAI and the built-in FAQ provider.
 */
public final class ServiceCatalogKnowledge {

  private ServiceCatalogKnowledge() {}

  public static final String CATALOG = """
      JustLife cleaning services (UI catalog):
      1. Basic Home Cleaning — 2h, 1 professional, $49. Small apartments.
      2. Standard Cleaning (2 Pros) — 2h, 2 professionals, $89. Medium homes, same vehicle team.
      3. Deep Cleaning — 4h, 2 professionals, $129. Detailed clean, events, seasonal.
      4. Premium Team Clean — 4h, 3 professionals, $179. Large homes.
      5. Small Office Cleaning — 2h, 1 professional, $69.
      6. Move-in / Move-out — 4h, 2 professionals, $149. Empty property.

      Booking rules:
      - Working hours 08:00–22:00. No bookings on Fridays.
      - Duration only 2 or 4 hours. Professionals per booking: 1, 2, or 3 (same vehicle).
      - REQUIRED ORDER for users: (1) check slots FIRST, (2) then book.
      - Chat live slots: "slots tomorrow", "slots on 29", or user sends just "29" after asking for slots.
      - Availability UI: Navbar → Availability → date → Fetch slots (same API).
      - Booking form also has "Check availability" before Confirm.
      - Chat booking (login required): book <service> <day/time> — use simple dates
        Examples: book standard tomorrow 2pm | book deep on 29 10am
        Services: basic, standard, deep, premium, office, move
      - My bookings (login): my bookings — lists IDs and times from API
      - Cancel (login): cancel latest | cancel booking <ID>
      - Reschedule (login): reschedule latest tomorrow 2pm | reschedule <ID> on 29 3pm
      - Users often reply in short follow-ups ("tomorrow", "29", "2pm") — use recent chat context.
      - 2BHK / medium apartment: recommend Deep Cleaning (4h, 2 pros) or Standard (2h, 2 pros).
      """;

  public static String systemPrompt(String languageCode) {
    String lang = LanguageHelper.languageInstruction(languageCode);
    return """
        You are JustLife Clean — a home cleaning booking assistant for this website.
        Introduce yourself briefly when asked who you are.
        """ + lang + """
        Be short, friendly, and easy to understand. Help with services, prices, availability, and booking.
        Accept casual dates: tomorrow, on 29, 26/05/2026 — do not insist on YYYY-MM-DD only.
        Use the recent conversation when the latest message is brief (e.g. only a date or time).
        Never invent prices or services outside the catalog.
        CRITICAL: When user asks how to book or wants a slot, remind them to check slots first (Availability page or chat: slots tomorrow), then book. Chat booking needs login.
        Website features: Home, Services, Book form, Availability page (check slots first), My Bookings, Login, this chat.
        Do not mention API keys or FAQ mode to the user.

        """ + CATALOG;
  }

  public static String systemPrompt() {
    return systemPrompt("en");
  }
}
