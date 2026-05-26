package com.booking.notificationservice.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "cleaner_notification")
public class NotificationEntity {

  @Id
  @Column(length = 36)
  private String id;

  @Column(name = "cleaner_id", nullable = false, length = 36)
  private String cleanerId;

  @Column(name = "booking_id", nullable = false, length = 36)
  private String bookingId;

  @Column(name = "event_type", nullable = false, length = 40)
  private String eventType;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String message;

  @Column(name = "customer_name")
  private String customerName;

  @Column(name = "customer_phone", length = 32)
  private String customerPhone;

  @Column(name = "customer_address", length = 512)
  private String customerAddress;

  @Column(name = "read_flag", nullable = false)
  private boolean read;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "slot_start_at")
  private LocalDateTime slotStartAt;

  @Column(name = "duration_hours")
  private Integer durationHours;

  public NotificationEntity() {}

  @PrePersist
  void prePersist() {
    if (id == null) {
      id = UUID.randomUUID().toString();
    }
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }

  public String getId() { return id; }
  public String getCleanerId() { return cleanerId; }
  public String getBookingId() { return bookingId; }
  public String getEventType() { return eventType; }
  public String getMessage() { return message; }
  public String getCustomerName() { return customerName; }
  public String getCustomerPhone() { return customerPhone; }
  public String getCustomerAddress() { return customerAddress; }
  public boolean isRead() { return read; }
  public Instant getCreatedAt() { return createdAt; }
  public LocalDateTime getSlotStartAt() { return slotStartAt; }
  public Integer getDurationHours() { return durationHours; }

  public void setCleanerId(String cleanerId) { this.cleanerId = cleanerId; }
  public void setBookingId(String bookingId) { this.bookingId = bookingId; }
  public void setEventType(String eventType) { this.eventType = eventType; }
  public void setMessage(String message) { this.message = message; }
  public void setCustomerName(String customerName) { this.customerName = customerName; }
  public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
  public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }
  public void setRead(boolean read) { this.read = read; }
  public void setSlotStartAt(LocalDateTime slotStartAt) { this.slotStartAt = slotStartAt; }
  public void setDurationHours(Integer durationHours) { this.durationHours = durationHours; }
}
