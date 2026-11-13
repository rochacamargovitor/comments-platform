package com.rochacamargovitor.comments.application.exception;

import java.util.UUID;

public class CommentNotFoundException extends RuntimeException {

    public CommentNotFoundException(UUID commentId) {
        super("Comment not found. ID: " + commentId);
    }

}
