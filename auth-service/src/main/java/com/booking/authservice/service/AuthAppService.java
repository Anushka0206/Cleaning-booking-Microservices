package com.booking.authservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.booking.authservice.config.JwtProperties;
import java.util.List;

import com.booking.authservice.model.UserEntity;
import com.booking.authservice.model.UserRole;
import com.booking.authservice.model.dto.AuthResponse;
import com.booking.authservice.model.dto.CleanerContactResponse;
import com.booking.authservice.model.dto.CleanerContactsRequest;
import com.booking.authservice.model.dto.LoginRequest;
import com.booking.authservice.integration.CleanerProfileResponse;
import com.booking.authservice.integration.CreateCleanerRequest;
import com.booking.authservice.integration.ProfessionalsRegistrationClient;
import com.booking.authservice.model.dto.RegisterCleanerRequest;
import com.booking.authservice.model.dto.RegisterRequest;
import com.booking.authservice.repository.UserRepository;
import com.booking.common.model.dto.response.CustomResponse;
import com.booking.common.security.AuthUserPrincipal;
import com.booking.common.security.JwtSupport;

@Service
public class AuthAppService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtProperties jwtProperties;
  private final ProfessionalsRegistrationClient professionalsClient;

  public AuthAppService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      JwtProperties jwtProperties,
      ProfessionalsRegistrationClient professionalsClient
  ) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtProperties = jwtProperties;
    this.professionalsClient = professionalsClient;
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
    return toAuthResponse(user, null);
  }

  public AuthResponse registerCleaner(RegisterCleanerRequest req) {
    if (userRepository.existsByEmailIgnoreCase(req.email())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
    }
    CustomResponse<CleanerProfileResponse> created = professionalsClient.registerCleaner(
        new CreateCleanerRequest(req.fullName().trim(), req.phone().trim(), req.vehicleId())
    );
    CleanerProfileResponse profile = created.getResponse();
    if (profile == null || profile.id() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not create cleaner profile");
    }
    UserEntity user = new UserEntity();
    user.setEmail(req.email().trim().toLowerCase());
    user.setPasswordHash(passwordEncoder.encode(req.password()));
    user.setFullName(req.fullName().trim());
    user.setPhone(req.phone().trim());
    user.setRole(UserRole.CLEANER);
    user.setCleanerId(profile.id());
    userRepository.save(user);
    return toAuthResponse(user, profile.vehicleName());
  }

  public List<CleanerContactResponse> cleanerContacts(CleanerContactsRequest req) {
    return userRepository.findByCleanerIdInAndRole(req.cleanerIds(), UserRole.CLEANER).stream()
        .map(u -> new CleanerContactResponse(
            u.getCleanerId(),
            u.getEmail(),
            u.getPhone(),
            u.getFullName()
        ))
        .toList();
  }

  public AuthResponse login(LoginRequest req) {
    UserEntity user = userRepository.findByEmailIgnoreCase(req.email().trim())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
    if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }
    return toAuthResponse(user, null);
  }

  private AuthResponse toAuthResponse(UserEntity user, String vehicleName) {
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
        user.getCleanerId(),
        vehicleName
    );
  }
}
