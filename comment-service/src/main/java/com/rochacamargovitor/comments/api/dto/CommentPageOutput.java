package com.rochacamargovitor.comments.api.dto;

import java.util.List;

public record CommentPageOutput(
        int page,
        int size,
        long totalElements,
        int totalPages,
        List<CommentOutput> content
) {
}
