package com.poetence.inbox_tracker.repository;

import com.poetence.inbox_tracker.model.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
    List<EmailLog> findByApplicationIdOrderByReceivedAtDesc(Long applicationId);
    boolean existsByMessageId(String messageId);
    List<EmailLog> findTop50ByOrderByReceivedAtDesc();
}