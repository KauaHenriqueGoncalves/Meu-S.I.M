package com.system.core.application.shared.exception;

public final class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}
