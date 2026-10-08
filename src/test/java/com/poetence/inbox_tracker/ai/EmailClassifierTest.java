package com.poetence.inbox_tracker.ai;

import com.poetence.inbox_tracker.mail.MailMessage;
import com.poetence.inbox_tracker.model.EmailCategory;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmailClassifierTest {

    private final MailMessage mail = new MailMessage("id1", "Interview invitation", "Recruiter",
            "jobs@example.com", Instant.parse("2026-10-01T12:00:00Z"), "preview", "Please pick a time.");

    private EmailClassifier classifier(AtomicInteger calls, IntFunction<EmailClassification> behavior) {
        return new EmailClassifier(null, new EmailTextCleaner(), 0) {
            @Override
            EmailClassification callModel(String emailText) {
                return behavior.apply(calls.incrementAndGet());
            }
        };
    }

    private EmailClassification of(EmailCategory category) {
        return new EmailClassification(category, "Epic", "SWE Intern", null, 0.9);
    }

    @Test
    void returnsModelResultOnFirstTry() {
        AtomicInteger calls = new AtomicInteger();
        EmailClassification result = classifier(calls, n -> of(EmailCategory.INTERVIEW)).classify(mail);

        assertEquals(EmailCategory.INTERVIEW, result.category());
        assertEquals(1, calls.get());
    }

    @Test
    void retriesAfterFailureThenSucceeds() {
        AtomicInteger calls = new AtomicInteger();
        EmailClassification result = classifier(calls, n -> {
            if (n == 1) {
                throw new RuntimeException("rate limited");
            }
            return of(EmailCategory.ASSESSMENT);
        }).classify(mail);

        assertEquals(EmailCategory.ASSESSMENT, result.category());
        assertEquals(2, calls.get());
    }

    @Test
    void fallsBackToUnclassifiedAfterAllAttemptsFail() {
        AtomicInteger calls = new AtomicInteger();
        EmailClassification result = classifier(calls, n -> {
            throw new RuntimeException("timeout");
        }).classify(mail);

        assertEquals(EmailCategory.UNCLASSIFIED, result.category());
        assertEquals(EmailClassifier.MAX_ATTEMPTS, calls.get());
    }

    @Test
    void treatsNullResultAsFailure() {
        AtomicInteger calls = new AtomicInteger();
        EmailClassification result = classifier(calls, n -> null).classify(mail);

        assertEquals(EmailCategory.UNCLASSIFIED, result.category());
    }
}