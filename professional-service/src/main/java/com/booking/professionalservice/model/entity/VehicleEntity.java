package com.booking.professionalservice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "vehicle")
@Getter
@Setter
public class VehicleEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "ID")
  private String id;

  @Column(nullable = false, unique = true)
  private String code;

  @Column(name = "license_plate", nullable = false, unique = true)
  private String licensePlate;

  public VehicleEntity() {}

  public VehicleEntity(String code, String licensePlate) {
    this.code = code;
    this.licensePlate = licensePlate;
  }

  public String getId() { return id; }
  public String getCode() { return code; }
  public String getLicensePlate() { return licensePlate; }
}
