package com.system.core.application.shared.exception;

public final class ConstraintViolationException extends DatabaseException {
    public ConstraintViolationException(String message) {
        super(message);
    }
}
