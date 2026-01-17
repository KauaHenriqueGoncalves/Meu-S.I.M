package com.system.application.domain.user.dto;

import com.system.application.shared.exception.CpfInvalidException;
import com.system.application.shared.util.CpfValidator;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.CsvSource;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Set;

class UserRequestTest {
    private final Validator validator;
    private final CpfValidator cpfValidator = mock(CpfValidator.class);

    public UserRequestTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        when(cpfValidator.isValid(anyString())).thenReturn(true);
    }

    @Test
    void shouldCreateUserRequestDto_WithValidData() {
        String validUsername = "john.doe";
        String validEmail = "john.doe@example.com";
        String validPassword = "SecurePass123";
        String validCpf = "90656982055";
        String validPhoneNumber = "+55 11 99999-9999";

        UserRequest dto = new UserRequest(
            validUsername,
            validEmail,
            validPassword,
            validCpf,
            validPhoneNumber
        );

        assertNotNull(dto);
        assertEquals(validUsername, dto.username());
        assertEquals(validEmail, dto.email());
        assertEquals(validPassword, dto.password());
        assertEquals(validCpf, dto.cpf());
        assertEquals(validPhoneNumber, dto.phoneNumber());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    void shouldFail_WhenUsernameIsBlankOrNull(String invalidUsername) {
        UserRequest dto = new UserRequest(
            invalidUsername,
            "test@example.com",
            "Password123",
            "90656982055",
            "+55 11 99999-9999"
        );

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("Username can't be")));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    @ValueSource(strings = {"invalid-email", "test@", "@domain.com", "test@.com"})
    void shouldFail_WhenEmailIsInvalid(String invalidEmail) {
        UserRequest dto = new UserRequest(
            "john.doe",
            invalidEmail,
            "Password123",
            "90656982055",
            "+55 11 99999-9999"
        );

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFail_WhenEmailExceeds255Characters() {
        String longEmail = "a".repeat(250) + "@example.com";

        UserRequest dto = new UserRequest(
            "john.doe",
            longEmail,
            "Password123",
            "90656982055",
            "+55 11 99999-9999"
        );

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("Email is up to 255")));
    }

    @Test
    void shouldFail_WhenPasswordIsTooShort() {
        UserRequest dto = new UserRequest(
            "john.doe",
            "test@example.com",
            "short",
            "90656982055",
            "+55 11 99999-9999"
        );

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("Password must be between 8 and 20")));
    }

    @Test
    void shouldFail_WhenPasswordIsTooLong() {
        String longPassword = "a".repeat(21);

        UserRequest dto = new UserRequest(
            "john.doe",
            "test@example.com",
            longPassword,
            "90656982055",
            "+55 11 99999-9999"
        );

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("Password must be between 8 and 20")));
    }

    @Test
    void shouldThrowException_WhenCpfIsInvalidAccordingToValidator() {
        String invalidCpf = "00000000000";
        when(cpfValidator.isValid(invalidCpf)).thenReturn(false);
        CpfInvalidException exception = assertThrows(CpfInvalidException.class, () -> {
            new UserRequest(
                "john.doe",
                "test@example.com",
                "Password123",
                invalidCpf,
                "+55 11 99999-9999"
            );
        });
        assertEquals("Cpf must be valid!", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
        "john.doe, john.doe@example.com, SecurePass123, 90656982055, +55 11 99999-9999",
        "jane.smith, jane@domain.com, AnotherPass456, 90656982055, 11988887777"
    })
    void shouldAccept_ValidCombinations(String username, String email, String password, String cpf, String phoneNumber) {
        UserRequest dto = new UserRequest(username, email, password, cpf, phoneNumber);
        Set<ConstraintViolation<UserRequest>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), 
            "Should have no violations for valid data. Violations: " + violations);
    }

    @Test
    void shouldHaveCorrectToString() {
        UserRequest dto = new UserRequest(
            "john.doe",
            "john@example.com",
            "Password123",
            "90656982055",
            "+55 11 99999-9999"
        );
        String toStringResult = dto.toString();
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("john.doe"));
        assertTrue(toStringResult.contains("john@example.com"));
        assertTrue(toStringResult.contains("Password123"));
    }

    @Test
    void shouldHaveProperEqualsAndHashCode() {
        UserRequest dto1 = new UserRequest(
            "john.doe",
            "john@example.com",
            "Password123",
            "90656982055",
            "+55 11 99999-9999"
        );
        UserRequest dto2 = new UserRequest(
            "john.doe",
            "john@example.com",
            "Password123",
            "90656982055",
            "+55 11 99999-9999"
        );
        UserRequest dto3 = new UserRequest(
            "jane.doe",
            "jane@example.com",
            "Password456",
            "90656982055",
            "+55 11 88888-8888"
        );
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }
}