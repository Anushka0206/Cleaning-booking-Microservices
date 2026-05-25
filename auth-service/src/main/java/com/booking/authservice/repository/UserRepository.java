package com.booking.authservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booking.authservice.model.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, String> {
  Optional<UserEntity> findByEmailIgnoreCase(String email);
  boolean existsByEmailIgnoreCase(String email);
}
