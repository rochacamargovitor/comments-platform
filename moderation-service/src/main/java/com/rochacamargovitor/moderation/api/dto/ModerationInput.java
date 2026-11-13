package com.rochacamargovitor.moderation.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ModerationInput(
        @NotNull UUID commentId,
        @NotBlank String text
) {
}
