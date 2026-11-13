package com.rochacamargovitor.comments.api.dto;

import java.time.Instant;
import java.util.UUID;

public record CommentOutput(
        UUID id,
        String text,
        String author,
        Instant createdAt
) {
}
