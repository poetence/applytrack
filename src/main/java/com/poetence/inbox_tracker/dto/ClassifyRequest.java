package com.poetence.inbox_tracker.dto;

import jakarta.validation.constraints.NotBlank;

public record ClassifyRequest(@NotBlank String subject, String sender, @NotBlank String body) {}