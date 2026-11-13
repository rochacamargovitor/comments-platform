package com.rochacamargovitor.moderation.domain.model;

public record ModerationDecision(
        boolean approved,
        String reason
) {

    public ModerationDecision {
        if (approved && reason != null) {
            throw new IllegalArgumentException(
                    "An approved moderation decision cannot have a rejection reason"
            );
        }

        if (!approved && (reason == null || reason.isBlank())) {
            throw new IllegalArgumentException(
                    "A rejected moderation decision must have a reason"
            );
        }
    }

}
