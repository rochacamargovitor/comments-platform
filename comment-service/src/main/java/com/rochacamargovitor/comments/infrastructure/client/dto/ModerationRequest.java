package com.rochacamargovitor.comments.infrastructure.client.dto;

import java.util.UUID;

public record ModerationRequest(
        UUID commentId,
        String text
) {
}
