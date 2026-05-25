package com.booking.bookingservice.model.entity;

import com.booking.bookingservice.model.enums.BookingStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "booking")
public class BookingEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "ID")
  private String id;

  @Column(name = "start_at", nullable = false)
  private LocalDateTime startAt;

  @Column(name = "end_at", nullable = false)
  private LocalDateTime endAt;

  @Column(name = "duration_hours", nullable = false)
  private int durationHours;

  @Column(name = "vehicle_id", nullable = false)
  private String vehicleId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BookingStatus status;

  @Column(name = "user_id", length = 36)
  private String userId;

  @Column(name = "customer_name")
  private String customerName;

  @Column(name = "customer_phone", length = 32)
  private String customerPhone;

  @Column(name = "customer_address", length = 512)
  private String customerAddress;

  @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<BookingCleanerEntity> cleaners = new ArrayList<>();

  protected BookingEntity() {}

  public BookingEntity(LocalDateTime startAt, LocalDateTime endAt, int durationHours, String vehicleId, BookingStatus status) {
    this.startAt = startAt;
    this.endAt = endAt;
    this.durationHours = durationHours;
    this.vehicleId = vehicleId;
    this.status = status;
  }

  public String getId() { return id; }
  public LocalDateTime getStartAt() { return startAt; }
  public LocalDateTime getEndAt() { return endAt; }
  public int getDurationHours() { return durationHours; }
  public String getVehicleId() { return vehicleId; }
  public BookingStatus getStatus() { return status; }
  public String getUserId() { return userId; }
  public String getCustomerName() { return customerName; }
  public String getCustomerPhone() { return customerPhone; }
  public String getCustomerAddress() { return customerAddress; }
  public List<BookingCleanerEntity> getCleaners() { return cleaners; }

  public void assignCustomer(String userId, String customerName, String customerPhone, String customerAddress) {
    this.userId = userId;
    this.customerName = customerName;
    this.customerPhone = customerPhone;
    this.customerAddress = customerAddress;
  }

  public void cancel() {
    this.status = BookingStatus.CANCELLED;
  }

  public void reschedule(LocalDateTime newStartAt, LocalDateTime newEndAt, int newDurationHours, String newVehicleId) {
    this.startAt = newStartAt;
    this.endAt = newEndAt;
    this.durationHours = newDurationHours;
    this.vehicleId = newVehicleId;
  }

}
