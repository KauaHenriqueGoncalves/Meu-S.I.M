package com.system.application.shared.exception.handler;

import com.system.application.shared.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.DateTimeException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public final class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
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
    public ResponseEntity<Map<String, String>> handleCpfInvalidException(
            CpfInvalidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        errors.put("cpf", ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(errors);
    }

    @ExceptionHandler(NotFoundObjectException.class)
    public ResponseEntity<StandardError> handleNotFoundException(
            NotFoundObjectException ex,
            HttpServletRequest request
    ) {
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
    public ResponseEntity<StandardError> handleEntityAlreadyExistsException(
            EntityAlreadyExistsException ex,
            HttpServletRequest request
    ) {
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
    public ResponseEntity<Map<String, String>> handleCnpjInvalidException(
            CnpjInvalidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        errors.put("cnpj", ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(errors);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<StandardError> handleBadCredentialsException(
            BadCredentialsException ex,
            HttpServletRequest request
    ) {
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

    @ExceptionHandler(DateTimeException.class)
    public ResponseEntity<StandardError> handleDateTimeException(
            DateTimeException ex,
            HttpServletRequest request
    ) {
        String message = "DateTime is wrong!";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError error = new StandardError(
                Instant.now(),
                status.value(),
                message,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardError> handlerIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        String message = "Argument is wrong!";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError error = new StandardError(
                Instant.now(),
                status.value(),
                message,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StandardError> handlerAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        String message = "Access denied!";
        HttpStatus status = HttpStatus.FORBIDDEN;
        StandardError error = new StandardError(
                Instant.now(),
                status.value(),
                message,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardError> handlerBusinessException(
            BusinessException e,
            HttpServletRequest request
    ) {
        String message = "Business Exeception";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError error = new StandardError(
                Instant.now(),
                status.value(),
                message,
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(PaymentGatewayException.class)
    public ResponseEntity<StandardError> handlerPaymentGatewayException(
            PaymentGatewayException e,
            HttpServletRequest request
    ) {
        String message = "Payment Gateway Exeception";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError error = new StandardError(
                Instant.now(),
                status.value(),
                message,
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.CONFLICT;
        String errorMessage = "Violação de integridade de dados";
        // Extrair a mensagem da exceção
        String rootMessage = ex.getRootCause() != null
                ? ex.getRootCause().getMessage()
                : ex.getMessage();
        // Analisar a mensagem para identificar o tipo de violação
        if (rootMessage != null) {
            if (rootMessage.contains("duplicate key") ||
                    rootMessage.contains("23505")) { // Código SQL para unique violation
                errorMessage = extractConstraintMessage(rootMessage);
            } else if (rootMessage.contains("violates foreign key constraint") ||
                    rootMessage.contains("23503")) { // Código SQL para foreign key violation
                errorMessage = "Violação de chave estrangeira. O registro referenciado não existe.";
            }
        }
        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Conflito",
                errorMessage,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    private String extractConstraintMessage(String dbMessage) {
        // Exemplo de mensagem do PostgreSQL:
        // ERROR: duplicate key value violates unique constraint "uk_email"
        // Detalhe: Key (email)=(usuario@email.com) already exists.
        if (dbMessage.contains("Detalhe:")) {
            String[] parts = dbMessage.split("Detalhe:");
            if (parts.length > 1) {
                String detail = parts[1].trim();
                // Mapear campos para mensagens amigáveis
                if (detail.contains("(email)=")) {
                    return "Este email já está cadastrado.";
                } else if (detail.contains("(phone_number)=")) {
                    return "Este número de telefone já está cadastrado.";
                } else if (detail.contains("(cpf)=")) {
                    return "Este CPF já está cadastrado.";
                } else if (detail.contains("(cnpj)=")) {
                    return "Este CPF já está cadastrado.";
                }
            }
        }
        return "Dado duplicado. O valor informado já existe no sistema.";
    }
}
