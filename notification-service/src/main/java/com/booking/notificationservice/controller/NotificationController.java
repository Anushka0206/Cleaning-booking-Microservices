package com.booking.notificationservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booking.common.model.dto.response.CustomResponse;
import com.booking.common.security.AuthUserPrincipal;
import com.booking.common.security.JwtAuthFilter;
import com.booking.notificationservice.model.NotificationEntity;
import com.booking.notificationservice.model.dto.NotificationResponse;
import com.booking.notificationservice.repository.NotificationRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

  private final NotificationRepository notificationRepository;

  public NotificationController(NotificationRepository notificationRepository) {
    this.notificationRepository = notificationRepository;
  }

  @GetMapping("/mine")
  public CustomResponse<List<NotificationResponse>> mine(HttpServletRequest request) {
    AuthUserPrincipal user = JwtAuthFilter.requireUser(request);
    if (!user.isCleaner() || user.cleanerId() == null) {
      throw new org.springframework.web.server.ResponseStatusException(
          org.springframework.http.HttpStatus.FORBIDDEN, "Cleaner account required");
    }
    List<NotificationResponse> list = notificationRepository
        .findByCleanerIdOrderByCreatedAtDesc(user.cleanerId())
        .stream()
        .map(this::toDto)
        .toList();
    return CustomResponse.successOf(list);
  }

  @PostMapping("/{id}/read")
  public CustomResponse<NotificationResponse> markRead(
      HttpServletRequest request,
      @PathVariable String id
  ) {
    AuthUserPrincipal user = JwtAuthFilter.requireUser(request);
    NotificationEntity n = notificationRepository.findById(id)
        .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
            org.springframework.http.HttpStatus.NOT_FOUND, "Notification not found"));
    if (!user.cleanerId().equals(n.getCleanerId())) {
      throw new org.springframework.web.server.ResponseStatusException(
          org.springframework.http.HttpStatus.FORBIDDEN, "Not your notification");
    }
    n.setRead(true);
    notificationRepository.save(n);
    return CustomResponse.successOf(toDto(n));
  }

  private NotificationResponse toDto(NotificationEntity n) {
    return new NotificationResponse(
        n.getId(),
        n.getBookingId(),
        n.getEventType(),
        n.getMessage(),
        n.getCustomerName(),
        n.getCustomerPhone(),
        n.getCustomerAddress(),
        n.isRead(),
        n.getCreatedAt()
    );
  }
}
