package com.poetence.inbox_tracker.controller;

import com.poetence.inbox_tracker.ai.EmailClassification;
import com.poetence.inbox_tracker.ai.EmailClassifier;
import com.poetence.inbox_tracker.dto.ClassifyRequest;
import com.poetence.inbox_tracker.mail.MailMessage;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/classify")
public class ClassifyController {

    private final EmailClassifier classifier;

    public ClassifyController(EmailClassifier classifier) {
        this.classifier = classifier;
    }

    @PostMapping
    public EmailClassification classify(@Valid @RequestBody ClassifyRequest req) {
        MailMessage mail = new MailMessage("manual", req.subject(), null, req.sender(),
                Instant.now(), req.body(), req.body());
        return classifier.classify(mail);
    }
}