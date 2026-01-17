package com.system.application.shared.exception.handler;

import com.system.application.shared.exception.CnpjInvalidException;
import com.system.application.shared.exception.CpfInvalidException;
import com.system.application.shared.exception.EntityAlreadyExistsException;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public final class GlobalExpectionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError)error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(errors);
    }

    @ExceptionHandler(CpfInvalidException.class)
    public ResponseEntity<Map<String, String>> handleCpfInvalidException(CpfInvalidException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("cpf", ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(errors);
    }

    @ExceptionHandler(NotFoundObjectException.class)
    public ResponseEntity<StandardError> handleNotFoundException(NotFoundObjectException ex,
                                                                 HttpServletRequest request) {
        String message = "Not found";
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

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<StandardError> handleEntityAlreadyExistsException(EntityAlreadyExistsException ex,
                                                                            HttpServletRequest request) {
        String message = "Conflicting Entity";
        HttpStatus status = HttpStatus.CONFLICT;
        StandardError standardError = new StandardError(
                Instant.now(),
                status.value(),
                message,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(standardError);
    }

    @ExceptionHandler(CnpjInvalidException.class)
    public ResponseEntity<Map<String, String>> handleCnpjInvalidException(CnpjInvalidException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("cnpj", ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(errors);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<StandardError>  handleBadCredentialsException(BadCredentialsException ex,
                                                                        HttpServletRequest request) {
        String message = "Bad credentials";
        HttpStatus status = HttpStatus.UNAUTHORIZED;
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
