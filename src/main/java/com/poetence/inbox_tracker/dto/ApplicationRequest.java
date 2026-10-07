package com.poetence.inbox_tracker.dto;

import com.poetence.inbox_tracker.model.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record ApplicationRequest(
        @NotBlank String company,
        @NotBlank String role,
        ApplicationStatus status,
        Instant deadline
) {}