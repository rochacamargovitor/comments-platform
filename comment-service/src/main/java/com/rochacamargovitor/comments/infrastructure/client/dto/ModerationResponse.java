package com.rochacamargovitor.comments.infrastructure.client.dto;

public record ModerationResponse(
        boolean approved,
        String reason
) {
}
