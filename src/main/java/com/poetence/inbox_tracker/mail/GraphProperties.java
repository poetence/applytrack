package com.poetence.inbox_tracker.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.graph")
public record GraphProperties(String clientId, String authority, String tokenCacheFile) {}