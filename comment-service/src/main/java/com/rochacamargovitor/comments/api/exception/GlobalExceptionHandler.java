package com.rochacamargovitor.comments.api.exception;

import com.rochacamargovitor.comments.api.dto.ErrorResponse;
import com.rochacamargovitor.comments.application.exception.CommentNotFoundException;
import com.rochacamargovitor.comments.application.exception.ModerationRejectedException;
import com.rochacamargovitor.comments.application.exception.ModerationServiceException;
import com.rochacamargovitor.comments.application.exception.ModerationTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ModerationRejectedException.class)
    public ResponseEntity<ErrorResponse> handleModerationRejected(
            ModerationRejectedException exception
    ) {
        ErrorResponse error = new ErrorResponse(
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(error);
    }


    @ExceptionHandler(ModerationTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleModerationTimeout(
            ModerationTimeoutException exception
    ) {
        ErrorResponse error = new ErrorResponse(
                "Moderation service timed out."
        );

        return ResponseEntity
                .status(HttpStatus.GATEWAY_TIMEOUT)
                .body(error);
    }

    @ExceptionHandler(ModerationServiceException.class)
    public ResponseEntity<ErrorResponse> handleModerationServiceError(
            ModerationServiceException exception
    ) {
        ErrorResponse error = new ErrorResponse(
                "Moderation service is unavailable."
        );

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(error);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCommentNotFound(
            CommentNotFoundException exception
    ) {
        ErrorResponse error = new ErrorResponse(
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException exception
    ) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(exception.getMessage()));
    }

}
