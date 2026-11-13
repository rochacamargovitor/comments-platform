
package com.rochacamargovitor.comments.application.service;

import com.rochacamargovitor.comments.application.exception.CommentNotFoundException;
import com.rochacamargovitor.comments.application.exception.ModerationRejectedException;
import com.rochacamargovitor.comments.domain.model.Comment;
import com.rochacamargovitor.comments.infrastructure.client.ModerationClient;
import com.rochacamargovitor.comments.infrastructure.client.dto.ModerationRequest;
import com.rochacamargovitor.comments.infrastructure.client.dto.ModerationResponse;
import com.rochacamargovitor.comments.infrastructure.persistence.entity.CommentEntity;
import com.rochacamargovitor.comments.infrastructure.persistence.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ModerationClient moderationClient;

    public CommentService(
            CommentRepository commentRepository,
            ModerationClient moderationClient
    ) {
        this.commentRepository = Objects.requireNonNull(commentRepository);
        this.moderationClient = Objects.requireNonNull(moderationClient);
    }

    public Comment create(String text, String author) {
        UUID commentId = UUID.randomUUID();

        ModerationRequest request = new ModerationRequest(
                commentId,
                text
        );

        ModerationResponse moderationResponse =
                moderationClient.moderate(request);

        if (!moderationResponse.approved()) {
            throw new ModerationRejectedException(
                    moderationResponse.reason()
            );
        }

        Comment comment = new Comment(
                commentId,
                text,
                author,
                Instant.now()
        );

        CommentEntity entity = new CommentEntity(comment);

        CommentEntity savedEntity = commentRepository.save(entity);

        return savedEntity.toDomain();
    }

    public Comment getById(UUID commentId) {
        CommentEntity entity = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        return entity.toDomain();
    }

    public Page<Comment> findAll(int page, int size) {
        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return commentRepository.findAll(pageable)
                .map(CommentEntity::toDomain);
    }

}
