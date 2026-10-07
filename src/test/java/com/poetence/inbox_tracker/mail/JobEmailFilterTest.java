package com.poetence.inbox_tracker.mail;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobEmailFilterTest {

    private final JobEmailFilter filter = new JobEmailFilter();

    private MailMessage mail(String sender, String subject, String preview) {
        return new MailMessage("id", subject, "Name", sender, Instant.now(), preview, "");
    }

    @Test
    void matchesApplicantTrackingSystemSenders() {
        assertTrue(filter.isJobRelated(mail("no-reply@greenhouse.io", "Hello", "Hi")));
    }

    @Test
    void matchesJobKeywordsInSubject() {
        assertTrue(filter.isJobRelated(mail("person@example.com", "Next steps for your internship application", "")));
    }

    @Test
    void ignoresUnrelatedMail() {
        assertFalse(filter.isJobRelated(mail("deals@store.com", "50% off this weekend", "Huge sale")));
    }

    @Test
    void handlesNullFields() {
        assertFalse(filter.isJobRelated(mail(null, null, null)));
    }
    @Test
    void ignoresLinkedInJobAlerts() {
        assertFalse(filter.isJobRelated(
                mail("jobalerts-noreply@linkedin.com", "Software Intern: 25 new jobs for you", "")));
    }
}
