package com.rochacamargovitor.comments.application.exception;

public class ModerationRejectedException extends RuntimeException {

    public ModerationRejectedException(String reason) {
        super(reason);
    }

}
