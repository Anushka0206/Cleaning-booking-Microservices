package com.booking.notificationservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booking.notificationservice.model.NotificationEntity;

public interface NotificationRepository extends JpaRepository<NotificationEntity, String> {
  List<NotificationEntity> findByCleanerIdOrderByCreatedAtDesc(String cleanerId);
}
