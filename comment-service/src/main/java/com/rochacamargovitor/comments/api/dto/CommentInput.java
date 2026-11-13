package com.rochacamargovitor.comments.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentInput(
        @NotBlank String text,
        @NotBlank String author
) {
}
