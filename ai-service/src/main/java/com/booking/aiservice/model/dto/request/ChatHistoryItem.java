package com.booking.aiservice.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** One prior turn from the React chat widget (user or assistant). */
public record ChatHistoryItem(
    @NotBlank
    @Pattern(regexp = "user|assistant")
    String role,

    @NotBlank
    @Size(max = 2000)
    String content
) {}
