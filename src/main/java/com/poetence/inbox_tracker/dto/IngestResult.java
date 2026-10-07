package com.poetence.inbox_tracker.dto;

public record IngestResult(int fetched, int matched, int saved) { }