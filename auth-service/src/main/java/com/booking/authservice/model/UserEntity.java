package com.booking.authservice.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_user")
public class UserEntity {

  @Id
  @Column(length = 36)
  private String id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(name = "password_hash", nullable = false, length = 100)
  private String passwordHash;

  @Column(name = "full_name", nullable = false)
  private String fullName;

  private String phone;

  private String address;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private UserRole role;

  @Column(name = "cleaner_id", length = 36)
  private String cleanerId;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  public UserEntity() {}

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
  public String getEmail() { return email; }
  public String getPasswordHash() { return passwordHash; }
  public String getFullName() { return fullName; }
  public String getPhone() { return phone; }
  public String getAddress() { return address; }
  public UserRole getRole() { return role; }
  public String getCleanerId() { return cleanerId; }

  public void setEmail(String email) { this.email = email; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
  public void setFullName(String fullName) { this.fullName = fullName; }
  public void setPhone(String phone) { this.phone = phone; }
  public void setAddress(String address) { this.address = address; }
  public void setRole(UserRole role) { this.role = role; }
}
