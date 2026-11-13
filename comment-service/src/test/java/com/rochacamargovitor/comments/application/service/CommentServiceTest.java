
package com.rochacamargovitor.comments.application.service;

import com.rochacamargovitor.comments.application.exception.CommentNotFoundException;
import com.rochacamargovitor.comments.application.exception.ModerationRejectedException;
import com.rochacamargovitor.comments.domain.model.Comment;
import com.rochacamargovitor.comments.infrastructure.client.ModerationClient;
import com.rochacamargovitor.comments.infrastructure.client.dto.ModerationRequest;
import com.rochacamargovitor.comments.infrastructure.client.dto.ModerationResponse;
import com.rochacamargovitor.comments.infrastructure.persistence.entity.CommentEntity;
import com.rochacamargovitor.comments.infrastructure.persistence.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    private final CommentRepository commentRepository =
            mock(CommentRepository.class);

    private final ModerationClient moderationClient =
            mock(ModerationClient.class);

    private final CommentService commentService =
            new CommentService(commentRepository, moderationClient);

    @Test
    void shouldCreateAndPersistApprovedComment() {
        when(moderationClient.moderate(any(ModerationRequest.class)))
                .thenReturn(new ModerationResponse(true, null));

        when(commentRepository.save(any(CommentEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Comment result = commentService.create(
                "I love programming.",
                "John"
        );

        assertNotNull(result.id());
        assertEquals("I love programming.", result.text());
        assertEquals("John", result.author());
        assertNotNull(result.createdAt());

        verify(moderationClient).moderate(any(ModerationRequest.class));
        verify(commentRepository).save(any(CommentEntity.class));
    }

    @Test
    void shouldRejectCommentWithoutPersistingIt() {
        when(moderationClient.moderate(any(ModerationRequest.class)))
                .thenReturn(new ModerationResponse(
                        false,
                        "The comment contains prohibited language."
                ));

        ModerationRejectedException exception = assertThrows(
                ModerationRejectedException.class,
                () -> commentService.create("I hate this.", "John")
        );

        assertEquals(
                "The comment contains prohibited language.",
                exception.getMessage()
        );

        verify(moderationClient).moderate(any(ModerationRequest.class));
        verify(commentRepository, never()).save(any(CommentEntity.class));
    }

    @Test
    void shouldReturnCommentWhenItExists() {
        UUID commentId = UUID.randomUUID();

        Comment comment = new Comment(
                commentId,
                "I love programming.",
                "John",
                Instant.parse("2026-11-13T12:00:00Z")
        );

        when(commentRepository.findById(commentId))
                .thenReturn(Optional.of(new CommentEntity(comment)));

        Comment result = commentService.getById(commentId);

        assertEquals(commentId, result.id());
        assertEquals("I love programming.", result.text());
        assertEquals("John", result.author());

        verify(commentRepository).findById(commentId);
    }

    @Test
    void shouldThrowExceptionWhenCommentDoesNotExist() {
        UUID commentId = UUID.randomUUID();

        when(commentRepository.findById(commentId))
                .thenReturn(Optional.empty());

        assertThrows(
                CommentNotFoundException.class,
                () -> commentService.getById(commentId)
        );

        verify(commentRepository).findById(commentId);
    }

    @Test
    void shouldReturnPaginatedComments() {
        Comment firstComment = new Comment(
                UUID.randomUUID(),
                "First comment.",
                "John",
                Instant.parse("2026-11-13T10:00:00Z")
        );

        Comment secondComment = new Comment(
                UUID.randomUUID(),
                "Second comment.",
                "Mary",
                Instant.parse("2026-11-13T11:00:00Z")
        );

        Page<CommentEntity> entityPage = new PageImpl<>(
                List.of(
                        new CommentEntity(firstComment),
                        new CommentEntity(secondComment)
                ),
                PageRequest.of(0, 10),
                2
        );

        when(commentRepository.findAll(any(
                org.springframework.data.domain.Pageable.class
        ))).thenReturn(entityPage);

        Page<Comment> result = commentService.findAll(0, 10);

        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getContent().size());
        assertEquals("First comment.", result.getContent().get(0).text());

        verify(commentRepository).findAll(any(
                org.springframework.data.domain.Pageable.class
        ));
    }

}
