package com.booking.authservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.booking.authservice.config.JwtProperties;
import com.booking.authservice.model.UserEntity;
import com.booking.authservice.model.UserRole;
import com.booking.authservice.model.dto.AuthResponse;
import com.booking.authservice.model.dto.LoginRequest;
import com.booking.authservice.model.dto.RegisterRequest;
import com.booking.authservice.repository.UserRepository;
import com.booking.common.security.AuthUserPrincipal;
import com.booking.common.security.JwtSupport;

@Service
public class AuthAppService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtProperties jwtProperties;

  public AuthAppService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      JwtProperties jwtProperties
  ) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtProperties = jwtProperties;
  }

  public AuthResponse register(RegisterRequest req) {
    if (userRepository.existsByEmailIgnoreCase(req.email())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
    }
    UserEntity user = new UserEntity();
    user.setEmail(req.email().trim().toLowerCase());
    user.setPasswordHash(passwordEncoder.encode(req.password()));
    user.setFullName(req.fullName().trim());
    user.setPhone(req.phone());
    user.setAddress(req.address());
    user.setRole(UserRole.CUSTOMER);
    userRepository.save(user);
    return toAuthResponse(user);
  }

  public AuthResponse login(LoginRequest req) {
    UserEntity user = userRepository.findByEmailIgnoreCase(req.email().trim())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
    if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }
    return toAuthResponse(user);
  }

  private AuthResponse toAuthResponse(UserEntity user) {
    AuthUserPrincipal principal = new AuthUserPrincipal(
        user.getId(),
        user.getEmail(),
        user.getRole().name(),
        user.getCleanerId(),
        user.getFullName(),
        user.getPhone(),
        user.getAddress()
    );
    String token = JwtSupport.createToken(
        jwtProperties.secret(),
        jwtProperties.expirationHours(),
        principal
    );
    return new AuthResponse(
        token,
        user.getId(),
        user.getEmail(),
        user.getFullName(),
        user.getPhone(),
        user.getAddress(),
        user.getRole().name(),
        user.getCleanerId()
    );
  }
}
