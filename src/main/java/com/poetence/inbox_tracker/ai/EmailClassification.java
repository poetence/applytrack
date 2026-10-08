package com.poetence.inbox_tracker.ai;

import com.poetence.inbox_tracker.model.EmailCategory;

public record EmailClassification(
        EmailCategory category,
        String company,
        String role,
        String deadline,
        Double confidence
        ) {
    public static EmailClassification unclassified() {
        return new EmailClassification(EmailCategory.UNCLASSIFIED, null, null, null, 0.0);
    }
}
