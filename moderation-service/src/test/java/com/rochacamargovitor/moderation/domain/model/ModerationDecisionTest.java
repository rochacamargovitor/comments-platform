package com.rochacamargovitor.moderation.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ModerationDecisionTest {

    @Test
    void shouldCreateApprovedDecisionWithoutReason() {
        ModerationDecision decision = new ModerationDecision(true, null);

        assertTrue(decision.approved());
        assertNull(decision.reason());
    }

    @Test
    void shouldCreateRejectedDecisionWithReason() {
        String reason = "The comment contains prohibited content.";

        ModerationDecision decision = new ModerationDecision(false, reason);

        assertFalse(decision.approved());
        assertEquals(reason, decision.reason());
    }

    @Test
    void shouldThrowExceptionWhenApprovedDecisionHasReason() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ModerationDecision(true, "Some reason")
        );
    }

    @Test
    void shouldThrowExceptionWhenRejectedDecisionHasNullReason() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ModerationDecision(false, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenRejectedDecisionHasBlankReason() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ModerationDecision(false, "   ")
        );
    }

}
