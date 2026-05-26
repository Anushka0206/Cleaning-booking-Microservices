package com.booking.bookingservice.service;

import com.booking.bookingservice.integration.professionals.ProfessionalsClient;
import com.booking.bookingservice.integration.professionals.dto.CleanerDto;
import com.booking.bookingservice.integration.professionals.dto.VehicleDto;
import com.booking.common.model.dto.request.CustomPagingRequest;
import com.booking.common.model.dto.response.CustomPagingResponse;
import com.booking.common.model.pagination.CustomPaging;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProfessionalsGateway {


    private static final int REQUEST_PAGE_NUMBER = 1;
    private static final int REQUEST_PAGE_SIZE = 100;

    private final ProfessionalsClient professionalsClient;

    public ProfessionalsGateway(ProfessionalsClient professionalsClient) {
        this.professionalsClient = professionalsClient;
    }

    @Cacheable(cacheNames = "booking-vehicles-v3", sync = true)
    @CircuitBreaker(name = "professionals", fallbackMethod = "listVehiclesFallback")
    public List<VehicleDto> listVehicles() {
        CustomPagingResponse<VehicleDto> page =
                professionalsClient.listVehicles(defaultPagingRequest());
        return page.getContent();
    }

    @Cacheable(cacheNames = "booking-vehicle-cleaners-v3", key = "#vehicleId", sync = true)
    @CircuitBreaker(name = "professionals", fallbackMethod = "listCleanersByVehicleFallback")
    public List<String> listCleanerIdsByVehicle(String vehicleId) {
        CustomPagingResponse<CleanerDto> page =
                professionalsClient.listCleanersByVehicle(vehicleId, defaultPagingRequest());

        return page.getContent().stream()
                .map(CleanerDto::id)
                .toList();
    }

    private CustomPagingRequest defaultPagingRequest() {
        CustomPaging paging = CustomPaging.builder()
                .pageNumber(REQUEST_PAGE_NUMBER)
                .pageSize(REQUEST_PAGE_SIZE)
                .build();

        return CustomPagingRequest.builder()
                .pagination(paging)
                .build();
    }

    public List<VehicleDto> listVehiclesFallback(Throwable t) {
        throw new IllegalStateException("professionals-service is unavailable", t);
    }

    public List<String> listCleanersByVehicleFallback(String vehicleId, Throwable t) {
        throw new IllegalStateException(
                "professionals-service is unavailable (cleaners for vehicleId=" + vehicleId + ")", t
        );
    }

    /** Human-readable labels for availability UI (VEH-01, Cleaner 01-01, …). */
    public DisplayLabels loadDisplayLabels() {
        Map<String, String> vehicleNames = new HashMap<>();
        Map<String, String> cleanerNames = new HashMap<>();
        Map<String, String> cleanerPhones = new HashMap<>();
        for (VehicleDto vehicle : listVehicles()) {
            vehicleNames.put(vehicle.id(), formatVehicleLabel(vehicle));
            List<CleanerDto> cleaners = vehicle.cleaners();
            if (cleaners == null || cleaners.isEmpty()) {
                cleaners = professionalsClient.listCleanersByVehicle(vehicle.id(), defaultPagingRequest())
                        .getContent();
            }
            for (CleanerDto cleaner : cleaners) {
                cleanerNames.put(cleaner.id(), formatCleanerLabel(cleaner));
                if (cleaner.phone() != null && !cleaner.phone().isBlank()) {
                    cleanerPhones.put(cleaner.id(), cleaner.phone());
                }
            }
        }
        return new DisplayLabels(vehicleNames, cleanerNames, cleanerPhones);
    }

    private static String formatVehicleLabel(VehicleDto vehicle) {
        if (vehicle.code() != null && !vehicle.code().isBlank()) {
            return vehicle.code();
        }
        if (vehicle.licensePlate() != null && !vehicle.licensePlate().isBlank()) {
            return vehicle.licensePlate();
        }
        return "Vehicle";
    }

    private static String formatCleanerLabel(CleanerDto cleaner) {
        if (cleaner.fullName() != null && !cleaner.fullName().isBlank()) {
            return cleaner.fullName();
        }
        return cleaner.id();
    }

    public record DisplayLabels(
        Map<String, String> vehicleNames,
        Map<String, String> cleanerNames,
        Map<String, String> cleanerPhones
    ) {
        public String vehicleName(String vehicleId) {
            return vehicleNames.getOrDefault(vehicleId, vehicleId);
        }

        public String cleanerName(String cleanerId) {
            return cleanerNames.getOrDefault(cleanerId, cleanerId);
        }

        public String cleanerPhone(String cleanerId) {
            return cleanerPhones.getOrDefault(cleanerId, "");
        }
    }
}