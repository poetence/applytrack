package com.poetence.inbox_tracker.ai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailTextCleanerTest {

    private final EmailTextCleaner cleaner = new EmailTextCleaner();

    @Test
    void cutsQuotedReplyAtOnWroteLine() {
        String body = "Thanks for applying!\n\nOn Mon, Oct 5, 2026 at 3:00 PM Jane <j@x.com> wrote:\n> earlier text";
        assertEquals("Thanks for applying!", cleaner.clean(body));
    }

    @Test
    void removesQuotedLines() {
        assertEquals("Hello\nWorld", cleaner.clean("Hello\n> quoted\nWorld"));
    }

    @Test
    void cutsSignatureDelimiter() {
        assertEquals("Please schedule.", cleaner.clean("Please schedule.\n-- \nJane Doe\nRecruiter"));
    }

    @Test
    void capsLength() {
        assertTrue(cleaner.clean("a".repeat(10_000)).length() <= EmailTextCleaner.MAX_CHARS);
    }

    @Test
    void handlesNull() {
        assertEquals("", cleaner.clean(null));
    }
}