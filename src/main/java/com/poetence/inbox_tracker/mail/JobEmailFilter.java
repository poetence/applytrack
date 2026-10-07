package com.poetence.inbox_tracker.mail;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class JobEmailFilter {

    private static final List<String> SENDER_HINTS = List.of(
            "greenhouse", "lever.co", "workday", "myworkday", "icims", "smartrecruiters",
            "ashbyhq", "jobvite", "taleo", "successfactors", "hackerrank", "codesignal",
            "hirevue", "codility", "handshake", "recruit", "talent", "careers", "noreply-jobs");

    private static final List<String> TEXT_HINTS = List.of(
            "internship", "intern ", "application", "applying", "interview", "assessment",
            "coding challenge", "online assessment", "offer", "recruiter", "your candidacy",
            "next steps", "software engineer", "position");

    /** Favors recall: a false positive just gets classified as OTHER later. */
    public boolean isJobRelated(MailMessage m) {
        String sender = lower(m.senderAddress());
        for (String hint : SENDER_HINTS) {
            if (sender.contains(hint)) {
                return true;
            }
        }
        String text = lower(m.subject()) + " " + lower(m.preview());
        for (String hint : TEXT_HINTS) {
            if (text.contains(hint)) {
                return true;
            }
        }
        return false;
    }

    private static String lower(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT);
    }
}