package com.poetence.inbox_tracker.controller;

import com.poetence.inbox_tracker.dto.EmailLogResponse;
import com.poetence.inbox_tracker.dto.IngestResult;
import com.poetence.inbox_tracker.service.EmailIngestionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class IngestController {

    private final EmailIngestionService ingestion;

    public IngestController(EmailIngestionService ingestion) {
        this.ingestion = ingestion;
    }

    @PostMapping("/ingest")
    public IngestResult ingest() {
        return ingestion.ingest();
    }

    @GetMapping("/emails")
    public List<EmailLogResponse> recentEmails() {
        return ingestion.recent();
    }
}