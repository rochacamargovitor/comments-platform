package com.rochacamargovitor.comments.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Comment {

    private final UUID id;
    private final String text;
    private final String author;
    private final Instant createdAt;

    public Comment(UUID id, String text, String author, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "Comment ID cannot be null");
        this.text = requireNonBlank(text, "Comment text cannot be blank");
        this.author = requireNonBlank(author, "Comment author cannot be blank");
        this.createdAt = Objects.requireNonNull(createdAt, "Comment creation date cannot be null");
    }

    private String requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }

        return value;
    }

    public UUID id() {
        return id;
    }

    public String text() {
        return text;
    }

    public String author() {
        return author;
    }

    public Instant createdAt() {
        return createdAt;
    }

}
