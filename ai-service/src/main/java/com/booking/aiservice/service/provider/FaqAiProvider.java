package com.booking.aiservice.service.provider;

import java.util.List;

import com.booking.aiservice.model.dto.request.ChatHistoryItem;
import com.booking.aiservice.service.chat.ChatContextResolver;
import com.booking.aiservice.service.knowledge.AvailabilityGuidance;
import com.booking.aiservice.service.knowledge.LanguageHelper;

/**
 * Offline FAQ — no API key. Supports English and Hindi for common questions.
 */
public class FaqAiProvider implements AiProvider {

  @Override
  public String name() {
    return "faq";
  }

  @Override
  public String chat(String userMessage, String languageCode) {
    return chat(userMessage, languageCode, List.of());
  }

  @Override
  public String chat(String userMessage, String languageCode, List<ChatHistoryItem> history) {
    String lang = languageCode == null ? "en" : languageCode;
    String m = userMessage.toLowerCase();
    String context = ChatContextResolver.contextForParsing(userMessage, history).toLowerCase();

    if (asksIdentity(m)) {
      return identityReply(lang);
    }

    if (asksFeatures(m)) {
      return featuresReply(lang);
    }

    if (asksAvailability(m) || asksAvailability(context)) {
      return AvailabilityGuidance.steps(lang);
    }

    if (m.contains("2bhk") || m.contains("2 bhk") || m.contains("apartment") || m.contains("flat")) {
      return "hi".equals(lang)
          ? "2BHK: Deep Cleaning (4h, 2 pros, $129) ya Standard (2h, 2 pros, $89). Pehle Availability → slots dekho, phir Services → Book Now."
          : "For 2BHK: Deep Cleaning (4h, 2 pros, $129) or Standard (2h, 2 pros, $89). First Availability → check slots, then Services → Book Now.";
    }

    if (m.contains("friday") || m.contains("shukr") || m.contains("jummah")) {
      return "hi".equals(lang)
          ? "Friday par booking nahi. Availability mein doosra din choose karein, phir book karein."
          : "No bookings on Fridays. Pick another day on Availability, then book.";
    }

    if (m.contains("price") || m.contains("cost") || m.contains("kitna") || m.contains("how much")) {
      return "hi".equals(lang)
          ? "Prices: Basic $49, Standard $89, Deep $129, Premium $179, Office $69, Move $149. Book se pehle Availability page par slots check karein."
          : "Prices: Basic $49, Standard $89, Deep $129, Premium $179, Office $69, Move $149. Check Availability for slots before booking.";
    }

    if (asksMyBookings(m)) {
      return myBookingsReply(lang);
    }

    if (asksBooking(m)) {
      return bookingFlowReply(lang);
    }

    if (m.contains("what model") || m.contains("which model") || m.contains("kaun sa model")) {
      return "hi".equals(lang)
          ? "Abhi FAQ mode chal raha hai (OpenAI key invalid). Sahi key ke baad model: gpt-4o-mini (ai-service/.env mein OPENAI_MODEL)."
          : "Right now FAQ mode is active because the OpenAI key failed. With a valid key, the model is gpt-4o-mini (see OPENAI_MODEL in ai-service/.env).";
    }

    if (m.contains("hello") || m.contains("hi") || m.contains("namaste") || m.contains("help")) {
      return "hi".equals(lang)
          ? "Namaste! Main JustLife assistant hoon. Pehle Availability par slots dekho, phir booking — ya services/price pucho."
          : "Hello! I'm the JustLife assistant. Check Availability for slots first, then book — or ask about services and prices.";
    }

    return "hi".equals(lang)
        ? "Main JustLife assistant hoon. Booking se pehle Availability page par slots zaroor dekho. Services ya price bhi puch sakte ho."
        : "I'm the JustLife assistant. Always check the Availability page for slots before booking. Ask about services or prices too.";
  }

  private static boolean asksIdentity(String m) {
    return m.contains("who are you") || m.contains("what are you") || m.contains("tum kon")
        || m.contains("aap kon") || m.contains("your name") || m.contains("kon ho");
  }

  private static boolean asksFeatures(String m) {
    return m.contains("features") || m.contains("kya kya") || m.contains("website me")
        || m.contains("what can") || m.contains("kya hai");
  }

  private static boolean asksAvailability(String m) {
    return m.contains("availability") || m.contains("available") || m.contains("slot")
        || m.contains("time slot") || m.contains("samay") || m.contains("slots")
        || m.contains("free time") || m.contains("khali") || m.contains("kab mil")
        || m.contains("check slot") || m.contains("fetch slot");
  }

  private static boolean asksMyBookings(String m) {
    return m.contains("my booking") || m.contains("mere booking")
        || m.contains("cancel") || m.contains("reschedule");
  }

  private static boolean asksBooking(String m) {
    return m.contains("book") || m.contains("kaise book")
        || m.contains("confirm") || m.contains("appointment");
  }

  private static String myBookingsReply(String lang) {
    return "hi".equals(lang)
        ? "Chat: my bookings | cancel latest | reschedule latest tomorrow 2pm (login zaroori)"
        : "In chat: my bookings | cancel latest | reschedule latest tomorrow 2pm (login required)";
  }

  private static String identityReply(String lang) {
    return "hi".equals(lang)
        ? "Main JustLife Clean assistant hoon — pehle Availability par free slots, phir cleaning book karne mein guide karta hoon."
        : "I'm the JustLife Clean assistant — I guide you to check Availability for free slots first, then complete your cleaning booking.";
  }

  private static String featuresReply(String lang) {
    return "hi".equals(lang)
        ? "Features: Availability (slots pehle!), Services, Book + Check availability button, My Bookings, Login, AI chat."
        : "Features: Availability (check slots first!), Services, Book with Check availability, My Bookings, Login, AI chat.";
  }

  private static String bookingFlowReply(String lang) {
    return AvailabilityGuidance.steps(lang);
  }
}
