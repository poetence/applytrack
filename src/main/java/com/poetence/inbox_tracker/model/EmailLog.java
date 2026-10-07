package com.poetence.inbox_tracker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "email_logs")
@Getter
@Setter
@NoArgsConstructor
public class EmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;

    @Column(name = "message_id", nullable = false, unique = true, length = 512)
    private String messageId;

    private String subject;

    private String sender;

    @Column(name = "received_at")
    private Instant receivedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private EmailCategory classification = EmailCategory.UNCLASSIFIED;

    @Column(name = "raw_snippet", columnDefinition = "TEXT")
    private String rawSnippet;
}