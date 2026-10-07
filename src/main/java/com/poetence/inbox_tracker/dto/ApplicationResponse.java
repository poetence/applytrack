package com.poetence.inbox_tracker.dto;

import com.poetence.inbox_tracker.model.Application;
import com.poetence.inbox_tracker.model.ApplicationStatus;

import java.time.Instant;

public record ApplicationResponse(
        Long id,
        String company,
        String role,
        ApplicationStatus status,
        Instant deadline,
        Instant createdAt,
        Instant updatedAt
) {
    public static ApplicationResponse from(Application a) {
        return new ApplicationResponse(a.getId(), a.getCompany(), a.getRole(),
                a.getStatus(), a.getDeadline(), a.getCreatedAt(), a.getUpdatedAt());
    }
}