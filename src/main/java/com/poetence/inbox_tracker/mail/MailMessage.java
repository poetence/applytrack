package com.poetence.inbox_tracker.mail;

import java.time.Instant;

public record MailMessage(
        String id,
        String subject,
        String senderName,
        String senderAddress,
        Instant receivedAt,
        String preview,
        String body
) {}