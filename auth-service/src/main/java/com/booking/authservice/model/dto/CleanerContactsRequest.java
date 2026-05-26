package com.booking.authservice.model.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CleanerContactsRequest(
    @NotEmpty List<String> cleanerIds
) {}
