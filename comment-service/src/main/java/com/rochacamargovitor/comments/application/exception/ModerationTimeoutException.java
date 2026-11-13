package com.rochacamargovitor.comments.application.exception;

public class ModerationTimeoutException extends ModerationServiceException {

    public ModerationTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

}
