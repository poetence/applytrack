package com.poetence.inbox_tracker.service;

import com.poetence.inbox_tracker.dto.EmailLogResponse;
import com.poetence.inbox_tracker.dto.IngestResult;
import com.poetence.inbox_tracker.mail.JobEmailFilter;
import com.poetence.inbox_tracker.mail.MailClient;
import com.poetence.inbox_tracker.mail.MailMessage;
import com.poetence.inbox_tracker.model.EmailCategory;
import com.poetence.inbox_tracker.model.EmailLog;
import com.poetence.inbox_tracker.repository.EmailLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
public class EmailIngestionService {

    private final MailClient mail;
    private final JobEmailFilter filter;
    private final EmailLogRepository logs;
    private final int lookbackDays;

    public EmailIngestionService(MailClient mail,
                                 JobEmailFilter filter,
                                 EmailLogRepository logs,
                                 @Value("${app.ingest.lookback-days:14}") int lookbackDays) {
        this.mail = mail;
        this.filter = filter;
        this.logs = logs;
        this.lookbackDays = lookbackDays;
    }

    // Deliberately not @Transactional: each save commits on its own,
    // so one bad message can't roll back the rest of the batch.
    public IngestResult ingest() {
        Instant since = Instant.now().minus(lookbackDays, ChronoUnit.DAYS);
        List<MailMessage> fetched = mail.fetchSince(since);

        int matched = 0;
        int saved = 0;
        for (MailMessage m : fetched) {
            if (!filter.isJobRelated(m)) {
                continue;
            }
            matched++;
            if (logs.existsByMessageId(m.id())) {
                continue;
            }
            EmailLog entry = new EmailLog();
            entry.setMessageId(m.id());
            entry.setSubject(truncate(m.subject(), 500));
            entry.setSender(truncate(m.senderAddress(), 255));
            entry.setReceivedAt(m.receivedAt());
            entry.setClassification(EmailCategory.UNCLASSIFIED);
            entry.setRawSnippet(truncate(m.preview(), 500));
            try {
                logs.save(entry);
                saved++;
            } catch (DataIntegrityViolationException e) {
                log.debug("Message already stored (concurrent run): {}", m.id());
            }
        }
        log.info("Ingestion finished: fetched={}, matched={}, saved={}", fetched.size(), matched, saved);
        return new IngestResult(fetched.size(), matched, saved);
    }

    public List<EmailLogResponse> recent() {
        return logs.findTop50ByOrderByReceivedAtDesc().stream().map(EmailLogResponse::from).toList();
    }

    private static String truncate(String s, int max) {
        if (s == null) {
            return null;
        }
        return s.length() <= max ? s : s.substring(0, max);
    }
}