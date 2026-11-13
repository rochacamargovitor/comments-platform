package com.rochacamargovitor.comments.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommentTest {

    @Test
    void shouldCreateCommentWhenAllFieldsAreValid() {
        UUID id = UUID.randomUUID();
        String text = "This is a valid comment.";
        String author = "John";
        Instant createdAt = Instant.parse("2026-11-12T15:00:00Z");

        Comment comment = new Comment(id, text, author, createdAt);

        assertEquals(id, comment.id());
        assertEquals(text, comment.text());
        assertEquals(author, comment.author());
        assertEquals(createdAt, comment.createdAt());
    }

    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> new Comment(
                        null,
                        "Valid comment",
                        "John",
                        Instant.now()
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenTextIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Comment(
                        UUID.randomUUID(),
                        null,
                        "John",
                        Instant.now()
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenTextIsBlank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Comment(
                        UUID.randomUUID(),
                        "   ",
                        "John",
                        Instant.now()
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenAuthorIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Comment(
                        UUID.randomUUID(),
                        "Valid comment",
                        null,
                        Instant.now()
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenAuthorIsBlank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Comment(
                        UUID.randomUUID(),
                        "Valid comment",
                        "   ",
                        Instant.now()
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenCreatedAtIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> new Comment(
                        UUID.randomUUID(),
                        "Valid comment",
                        "John",
                        null
                )
        );
    }

}
