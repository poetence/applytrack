package com.poetence.inbox_tracker.ai;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class EmailTextCleaner {
    static final int MAX_CHARS = 3000;
    //Starts of forwarded messages/quoted reply
    private static final Pattern CUT_MARKER = Pattern.compile(
            "(?m)^(On .{0,200} wrote:|-{2,} ?Original Message ?-{2,}|-- )\\s*$");

    public String clean(String body) {
        if (body == null) {
            return "";
        }
        String text = body.replace("\r\n", "\n");
        Matcher m = CUT_MARKER.matcher(text);
        if (m.find()) {
            text = text.substring(0, m.start());
        }
        text = text.lines()
                .filter(line -> !line.stripLeading().startsWith(">"))
                .collect(Collectors.joining("\n"));
        text = text.replaceAll("[ \\t]+", " ").replaceAll("\n{3,}", "\n\n").trim();
        return text.length() > MAX_CHARS ? text.substring(0, MAX_CHARS) : text;
    }
}

