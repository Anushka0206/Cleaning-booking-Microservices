package com.booking.authservice.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booking.authservice.model.UserEntity;
import com.booking.authservice.model.UserRole;

public interface UserRepository extends JpaRepository<UserEntity, String> {
  Optional<UserEntity> findByEmailIgnoreCase(String email);
  boolean existsByEmailIgnoreCase(String email);
  List<UserEntity> findByCleanerIdInAndRole(Collection<String> cleanerIds, UserRole role);
}
