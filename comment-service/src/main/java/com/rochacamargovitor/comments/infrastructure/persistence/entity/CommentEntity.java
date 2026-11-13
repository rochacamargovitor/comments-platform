package com.rochacamargovitor.comments.infrastructure.persistence.entity;

import com.rochacamargovitor.comments.domain.model.Comment;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "comments")
public class CommentEntity {

    @Id
    private UUID id;

    @Lob
    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private Instant createdAt;

    protected CommentEntity() {
    }

    public CommentEntity(Comment comment) {
        this.id = comment.id();
        this.text = comment.text();
        this.author = comment.author();
        this.createdAt = comment.createdAt();
    }

    public Comment toDomain() {
        return new Comment(id, text, author, createdAt);
    }

    public UUID getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getAuthor() {
        return author;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

}
