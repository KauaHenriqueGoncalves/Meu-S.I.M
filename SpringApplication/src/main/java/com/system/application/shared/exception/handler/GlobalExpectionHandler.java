package com.system.application.shared.exception.handler;

import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExpectionHandler {
    @ExceptionHandler(NotFoundObjectException.class)
    public ResponseEntity<StandardError> handleNotFoundException(NotFoundObjectException ex,
                                                                 HttpServletRequest request) {
        String message = "Controller not found Object";
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError standardError = new StandardError(
                Instant.now(),
                status.value(),
                message,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(standardError);
    }
}
