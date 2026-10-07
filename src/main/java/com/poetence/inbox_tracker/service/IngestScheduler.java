package com.poetence.inbox_tracker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@ConditionalOnProperty(name = "app.ingest.enabled", havingValue = "true")
@Slf4j
public class IngestScheduler {

    private final EmailIngestionService ingestion;

    public IngestScheduler(EmailIngestionService ingestion) {
        this.ingestion = ingestion;
    }

    @Scheduled(fixedDelayString = "${app.ingest.interval:PT15M}")
    public void run() {
        try {
            ingestion.ingest();
        } catch (RuntimeException e) {
            log.warn("Scheduled ingestion failed: {}", e.getMessage());
        }
    }
}