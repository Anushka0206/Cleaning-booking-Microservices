package com.booking.bookingservice.service;

import com.booking.bookingservice.integration.auth.AuthClient;
import com.booking.bookingservice.integration.auth.dto.CleanerContactDto;
import com.booking.bookingservice.integration.auth.dto.CleanerContactsRequest;
import com.booking.bookingservice.model.dto.response.AssignedCleanerResponse;
import com.booking.bookingservice.model.dto.response.BookingResponse;
import com.booking.bookingservice.model.entity.BookingCleanerEntity;
import com.booking.bookingservice.model.entity.BookingEntity;
import com.booking.bookingservice.model.mapper.BookingMapper;
import com.booking.common.model.dto.response.CustomResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BookingResponseFactory {

  private final BookingMapper bookingMapper;
  private final ProfessionalsGateway professionalsGateway;
  private final AuthClient authClient;

  public BookingResponseFactory(
      BookingMapper bookingMapper,
      ProfessionalsGateway professionalsGateway,
      AuthClient authClient
  ) {
    this.bookingMapper = bookingMapper;
    this.professionalsGateway = professionalsGateway;
    this.authClient = authClient;
  }

  public BookingResponse toResponse(BookingEntity entity) {
    BookingResponse base = bookingMapper.map(entity);
    List<String> cleanerIds = entity.getCleaners().stream()
        .map(BookingCleanerEntity::getCleanerId)
        .distinct()
        .toList();

    ProfessionalsGateway.DisplayLabels labels = professionalsGateway.loadDisplayLabels();
    Map<String, CleanerContactDto> contactsByCleanerId = lookupContacts(cleanerIds);

    List<AssignedCleanerResponse> assigned = cleanerIds.stream()
        .map(id -> toAssignedCleaner(id, labels, contactsByCleanerId.get(id)))
        .toList();

    return new BookingResponse(
        base.id(),
        base.startAt(),
        base.endAt(),
        base.durationHours(),
        base.vehicleId(),
        labels.vehicleName(base.vehicleId()),
        base.status(),
        base.userId(),
        base.customerName(),
        base.customerPhone(),
        base.customerAddress(),
        assigned
    );
  }

  private AssignedCleanerResponse toAssignedCleaner(
      String cleanerId,
      ProfessionalsGateway.DisplayLabels labels,
      CleanerContactDto contact
  ) {
    String name = labels.cleanerName(cleanerId);
    if (contact != null && contact.fullName() != null && !contact.fullName().isBlank()) {
      name = contact.fullName();
    }
    String phone = labels.cleanerPhone(cleanerId);
    if (phone == null || phone.isBlank()) {
      phone = contact != null ? nullToEmpty(contact.phone()) : "";
    }
    String email = contact != null ? nullToEmpty(contact.email()) : "";
    return new AssignedCleanerResponse(cleanerId, name, phone, email);
  }

  private Map<String, CleanerContactDto> lookupContacts(List<String> cleanerIds) {
    if (cleanerIds.isEmpty()) {
      return Collections.emptyMap();
    }
    try {
      CustomResponse<List<CleanerContactDto>> response =
          authClient.cleanerContacts(new CleanerContactsRequest(cleanerIds));
      List<CleanerContactDto> list = response != null && response.getResponse() != null
          ? response.getResponse()
          : List.of();
      return list.stream()
          .filter(c -> c.cleanerId() != null)
          .collect(Collectors.toMap(CleanerContactDto::cleanerId, Function.identity(), (a, b) -> a));
    } catch (Exception ignored) {
      return Collections.emptyMap();
    }
  }

  private static String nullToEmpty(String value) {
    return value == null ? "" : value;
  }
}
