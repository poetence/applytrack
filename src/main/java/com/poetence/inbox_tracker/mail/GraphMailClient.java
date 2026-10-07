package com.poetence.inbox_tracker.mail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class GraphMailClient implements MailClient {

    private static final String BASE_URL = "https://graph.microsoft.com/v1.0";
    private static final String PREFER = "IdType=\"ImmutableId\", outlook.body-content-type=\"text\"";
    private static final int MAX_PAGES = 10;
    private static final int MAX_BODY_CHARS = 8000;

    private final GraphTokenProvider tokens;
    private final RestClient http = RestClient.builder().baseUrl(BASE_URL).build();

    public GraphMailClient(GraphTokenProvider tokens) {
        this.tokens = tokens;
    }

    @Override
    public List<MailMessage> fetchSince(Instant since) {
        String token = tokens.getAccessToken();
        List<MailMessage> result = new ArrayList<>();

        GraphPage page = http.get()
                .uri(b -> b.path("/me/mailFolders/inbox/messages")
                        .queryParam("$top", 50)
                        .queryParam("$select", "id,subject,from,receivedDateTime,bodyPreview,body")
                        .queryParam("$orderby", "receivedDateTime desc")
                        .queryParam("$filter", "receivedDateTime ge {since}")
                        .build(since.toString()))
                .headers(h -> authHeaders(h, token))
                .retrieve()
                .body(GraphPage.class);

        int pages = 1;
        while (page != null) {
            collect(page, result);
            if (page.nextLink() == null || pages >= MAX_PAGES) {
                break;
            }
            page = http.get()
                    .uri(URI.create(page.nextLink()))
                    .headers(h -> authHeaders(h, token))
                    .retrieve()
                    .body(GraphPage.class);
            pages++;
        }
        return result;
    }

    private void authHeaders(HttpHeaders headers, String token) {
        headers.setBearerAuth(token);
        headers.add("Prefer", PREFER);
    }

    private void collect(GraphPage page, List<MailMessage> out) {
        if (page.value() == null) {
            return;
        }
        for (GraphMessage m : page.value()) {
            String name = null;
            String address = null;
            if (m.from() != null && m.from().emailAddress() != null) {
                name = m.from().emailAddress().name();
                address = m.from().emailAddress().address();
            }
            String body = (m.body() == null || m.body().content() == null) ? "" : m.body().content();
            if (body.length() > MAX_BODY_CHARS) {
                body = body.substring(0, MAX_BODY_CHARS);
            }
            Instant received = m.receivedDateTime() == null ? null : Instant.parse(m.receivedDateTime());
            out.add(new MailMessage(m.id(), m.subject(), name, address, received, m.bodyPreview(), body));
        }
    }

    // JSON shapes returned by Microsoft Graph. Unknown fields are ignored.
    @JsonIgnoreProperties(ignoreUnknown = true)
    record GraphPage(List<GraphMessage> value, @JsonProperty("@odata.nextLink") String nextLink) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record GraphMessage(String id, String subject, GraphRecipient from, String receivedDateTime,
                        String bodyPreview, GraphBody body) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record GraphRecipient(GraphEmailAddress emailAddress) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record GraphEmailAddress(String name, String address) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record GraphBody(String content) {}
}
