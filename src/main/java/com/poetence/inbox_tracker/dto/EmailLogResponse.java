package com.poetence.inbox_tracker.dto;

import com.poetence.inbox_tracker.model.EmailCategory;
import com.poetence.inbox_tracker.model.EmailLog;

import java.time.Instant;

public record EmailLogResponse(
        Long id,
        String messageId,
        String subject,
        String sender,
        Instant receivedAt,
        EmailCategory classification,
        String rawSnippet
) {
    public static EmailLogResponse from(EmailLog e) {
        return new EmailLogResponse(e.getId(), e.getMessageId(), e.getSubject(),
                e.getSender(), e.getReceivedAt(), e.getClassification(), e.getRawSnippet());
    }
}