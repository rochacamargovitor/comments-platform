package com.rochacamargovitor.comments.infrastructure.persistence.repository;

import com.rochacamargovitor.comments.domain.model.Comment;
import com.rochacamargovitor.comments.infrastructure.persistence.entity.CommentEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void shouldSaveAndRetrieveComment() {
        UUID commentId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-11-13T12:00:00Z");

        Comment comment = new Comment(
                commentId,
                "I love programming.",
                "John",
                createdAt
        );

        CommentEntity entity = new CommentEntity(comment);

        commentRepository.save(entity);

        var savedEntity = commentRepository.findById(commentId);

        assertTrue(savedEntity.isPresent());

        Comment retrievedComment = savedEntity.get().toDomain();

        assertEquals(commentId, retrievedComment.id());
        assertEquals("I love programming.", retrievedComment.text());
        assertEquals("John", retrievedComment.author());
        assertEquals(createdAt, retrievedComment.createdAt());
    }

    @Test
    void shouldReturnEmptyWhenCommentDoesNotExist() {
        UUID commentId = UUID.randomUUID();

        var result = commentRepository.findById(commentId);

        assertFalse(result.isPresent());
    }

}
