package com.booking.authservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booking.authservice.model.dto.AuthResponse;
import com.booking.authservice.model.dto.LoginRequest;
import com.booking.authservice.model.dto.RegisterRequest;
import com.booking.authservice.service.AuthAppService;
import com.booking.common.model.dto.response.CustomResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthAppService authAppService;

  public AuthController(AuthAppService authAppService) {
    this.authAppService = authAppService;
  }

  @PostMapping("/register")
  public CustomResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    return CustomResponse.createdOf(authAppService.register(request));
  }

  @PostMapping("/login")
  public CustomResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    return CustomResponse.successOf(authAppService.login(request));
  }
}
