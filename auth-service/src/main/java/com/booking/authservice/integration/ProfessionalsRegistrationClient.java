package com.booking.authservice.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.booking.common.model.dto.response.CustomResponse;

@FeignClient(name = "professionals-service", path = "/api/v1")
public interface ProfessionalsRegistrationClient {

  @PostMapping("/cleaners/register")
  CustomResponse<CleanerProfileResponse> registerCleaner(@RequestBody CreateCleanerRequest request);
}
