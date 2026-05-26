package com.booking.bookingservice.integration.auth;

import com.booking.bookingservice.integration.auth.dto.CleanerContactDto;
import com.booking.bookingservice.integration.auth.dto.CleanerContactsRequest;
import com.booking.common.model.dto.response.CustomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "auth-service", path = "/api/auth")
public interface AuthClient {

  @PostMapping("/cleaners/contacts")
  CustomResponse<List<CleanerContactDto>> cleanerContacts(@RequestBody CleanerContactsRequest request);
}
