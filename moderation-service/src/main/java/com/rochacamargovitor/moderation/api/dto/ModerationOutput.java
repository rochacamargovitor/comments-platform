package com.rochacamargovitor.moderation.api.dto;

public record ModerationOutput(
        boolean approved,
        String reason
) {
}
