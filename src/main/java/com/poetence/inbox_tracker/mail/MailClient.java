package com.poetence.inbox_tracker.mail;

import java.time.Instant;
import java.util.List;

public interface MailClient {
    List<MailMessage> fetchSince(Instant since);
}