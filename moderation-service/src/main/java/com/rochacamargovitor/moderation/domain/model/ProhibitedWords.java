package com.rochacamargovitor.moderation.domain.model;

import java.util.List;

public final class ProhibitedWords {

    private ProhibitedWords() {
    }

    public static final List<String> VALUES = List.of(
            "hate",
            "idiot",
            "stupid",
            "offensive",
            "insult"
    );

}
