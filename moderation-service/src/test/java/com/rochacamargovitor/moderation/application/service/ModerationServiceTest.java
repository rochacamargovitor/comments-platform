package com.rochacamargovitor.moderation.application.service;

import com.rochacamargovitor.moderation.domain.model.ModerationDecision;
import com.rochacamargovitor.moderation.domain.model.ModerationPolicy;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ModerationServiceTest {

    private final ModerationService moderationService =
            new ModerationService(new ModerationPolicy());

    @Test
    void shouldApproveCommentWithoutProhibitedWords() {
        UUID commentId = UUID.randomUUID();

        ModerationDecision decision = moderationService.moderate(
                commentId,
                "I love programming."
        );

        assertTrue(decision.approved());
        assertNull(decision.reason());
    }

    @Test
    void shouldRejectCommentContainingProhibitedWord() {
        UUID commentId = UUID.randomUUID();

        ModerationDecision decision = moderationService.moderate(
                commentId,
                "I hate this."
        );

        assertFalse(decision.approved());
        assertEquals("The comment contains prohibited language.", decision.reason());
    }

    @Test
    void shouldThrowExceptionWhenCommentIdIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> moderationService.moderate(
                        null,
                        "I love programming."
                )
        );
    }

}
