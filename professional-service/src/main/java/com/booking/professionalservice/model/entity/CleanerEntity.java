package com.booking.professionalservice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cleaner")
@Getter
@Setter
public class CleanerEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "ID")
  private String id;

  @Column(name = "full_name", nullable = false)
  private String fullName;

  @Column(name = "phone", length = 32)
  private String phone;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "vehicle_id", nullable = false)
  private VehicleEntity vehicle;

  public CleanerEntity() {}

  public CleanerEntity(String fullName, VehicleEntity vehicle) {
    this.fullName = fullName;
    this.vehicle = vehicle;
  }

  public String getId() { return id; }
  public String getFullName() { return fullName; }
  public String getPhone() { return phone; }
  public VehicleEntity getVehicle() { return vehicle; }

  public void setPhone(String phone) { this.phone = phone; }
}
