package com.rochacamargovitor.moderation.domain.model;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public final class ModerationPolicy {

    private static final List<Pattern> PROHIBITED_PATTERNS =
            ProhibitedWords.VALUES.stream()
                    .map(word -> Pattern.compile(
                            "(?iu)(?<![\\p{L}\\p{N}_])"
                                    + Pattern.quote(word)
                                    + "(?![\\p{L}\\p{N}_])"
                    ))
                    .toList();

    private static final String REJECTION_REASON =
            "The comment contains prohibited language.";

    public ModerationDecision evaluate(String text) {
        Objects.requireNonNull(text, "Comment text cannot be null");

        if (text.isBlank()) {
            throw new IllegalArgumentException(
                    "Comment text cannot be blank"
            );
        }

        boolean containsProhibitedWord = PROHIBITED_PATTERNS.stream()
                .anyMatch(pattern -> pattern.matcher(text).find());

        if (containsProhibitedWord) {
            return new ModerationDecision(false, REJECTION_REASON);
        }

        return new ModerationDecision(true, null);
    }

}
