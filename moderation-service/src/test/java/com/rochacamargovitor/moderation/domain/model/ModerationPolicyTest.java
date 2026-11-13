package com.rochacamargovitor.moderation.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModerationPolicyTest {

    private final ModerationPolicy policy = new ModerationPolicy();

    @Test
    void shouldApproveCommentWithoutProhibitedWords() {
        ModerationDecision decision = policy.evaluate("I love programming.");

        assertTrue(decision.approved());
        assertNull(decision.reason());
    }

    @Test
    void shouldRejectCommentContainingProhibitedWord() {
        ModerationDecision decision = policy.evaluate("I hate this.");

        assertFalse(decision.approved());
        assertEquals("The comment contains prohibited language.", decision.reason());
    }

    @Test
    void shouldIgnoreLetterCaseWhenCheckingProhibitedWords() {
        ModerationDecision decision = policy.evaluate("YOU ARE AN IDIOT.");

        assertFalse(decision.approved());
    }

    @Test
    void shouldRecognizeProhibitedWordsWithPunctuation() {
        ModerationDecision decision = policy.evaluate("What an insult!");

        assertFalse(decision.approved());
    }

    @Test
    void shouldNotRejectWordsContainingProhibitedTermsAsSubstrings() {
        ModerationDecision decision = policy.evaluate("This is a hateful situation.");

        assertTrue(decision.approved());
    }

    @Test
    void shouldThrowExceptionWhenTextIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> policy.evaluate(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenTextIsBlank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> policy.evaluate("   ")
        );
    }

}
