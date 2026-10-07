package com.poetence.inbox_tracker.service;

import com.poetence.inbox_tracker.dto.IngestResult;
import com.poetence.inbox_tracker.mail.JobEmailFilter;
import com.poetence.inbox_tracker.mail.MailClient;
import com.poetence.inbox_tracker.mail.MailMessage;
import com.poetence.inbox_tracker.model.EmailLog;
import com.poetence.inbox_tracker.repository.EmailLogRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailIngestionServiceTest {

    private final MailClient mail = mock(MailClient.class);
    private final EmailLogRepository logs = mock(EmailLogRepository.class);
    private final EmailIngestionService service =
            new EmailIngestionService(mail, new JobEmailFilter(), logs, 14);

    private MailMessage jobMail(String id) {
        return new MailMessage(id, "Interview invitation", "Recruiter", "jobs@greenhouse.io",
                Instant.now(), "Please pick a time", "");
    }

    @Test
    void savesNewJobEmails() {
        when(mail.fetchSince(any())).thenReturn(List.of(jobMail("a"), jobMail("b")));
        when(logs.existsByMessageId(any())).thenReturn(false);

        IngestResult result = service.ingest();

        assertEquals(2, result.saved());
        verify(logs, org.mockito.Mockito.times(2)).save(any(EmailLog.class));
    }

    @Test
    void skipsMessagesAlreadyStored() {
        when(mail.fetchSince(any())).thenReturn(List.of(jobMail("a")));
        when(logs.existsByMessageId("a")).thenReturn(true);

        IngestResult result = service.ingest();

        assertEquals(1, result.matched());
        assertEquals(0, result.saved());
        verify(logs, never()).save(any());
    }
}