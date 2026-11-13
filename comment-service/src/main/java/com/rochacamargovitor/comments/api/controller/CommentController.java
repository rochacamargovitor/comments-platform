package com.rochacamargovitor.comments.api.controller;

import com.rochacamargovitor.comments.api.dto.CommentInput;
import com.rochacamargovitor.comments.api.dto.CommentOutput;
import com.rochacamargovitor.comments.api.dto.CommentPageOutput;
import com.rochacamargovitor.comments.application.service.CommentService;
import com.rochacamargovitor.comments.domain.model.Comment;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentOutput> create(
            @Valid @RequestBody CommentInput input
    ) {
        Comment comment = commentService.create(
                input.text(),
                input.author()
        );

        CommentOutput output = new CommentOutput(
                comment.id(),
                comment.text(),
                comment.author(),
                comment.createdAt()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(output);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentOutput> getById(
            @PathVariable UUID id
    ) {
        Comment comment = commentService.getById(id);

        CommentOutput output = new CommentOutput(
                comment.id(),
                comment.text(),
                comment.author(),
                comment.createdAt()
        );

        return ResponseEntity.ok(output);
    }

    @GetMapping
    public ResponseEntity<CommentPageOutput> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page must be non-negative and size must be between 1 and 100."
            );
        }

        Page<Comment> comments = commentService.findAll(page, size);

        CommentPageOutput output = new CommentPageOutput(
                comments.getNumber(),
                comments.getSize(),
                comments.getTotalElements(),
                comments.getTotalPages(),
                comments.getContent().stream()
                        .map(comment -> new CommentOutput(
                                comment.id(),
                                comment.text(),
                                comment.author(),
                                comment.createdAt()
                        ))
                        .toList()
        );

        return ResponseEntity.ok(output);
    }

}
