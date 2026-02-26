package com.system.core.application.shared.exception;

public final class NotFoundObjectException extends RuntimeException {
    public NotFoundObjectException(String message) {
        super(message);
    }
}
