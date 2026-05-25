package com.booking.professionalservice.repository;

import com.booking.professionalservice.model.entity.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<VehicleEntity, String> {}
