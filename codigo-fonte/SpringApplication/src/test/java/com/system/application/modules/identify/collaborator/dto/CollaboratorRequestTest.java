package com.system.application.modules.identify.collaborator.dto;

import com.system.application.modules.identity.collaborator.dto.CollaboratorRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CollaboratorRequest")
public class CollaboratorRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private Set<ConstraintViolation<CollaboratorRequest>> validate(
            LocalDate dateOfBirth, String specialty, String workload
    ) {
        return validator.validate(new CollaboratorRequest(dateOfBirth, specialty, workload));
    }

    private void assertViolationOnField(
            Set<ConstraintViolation<CollaboratorRequest>> violations,
            String field, String expectedMessage
    ) {
        assertThat(violations)
                .anyMatch(v ->
                        v.getPropertyPath().toString().equals(field)
                                && v.getMessage().equals(expectedMessage)
                );
    }

    @Nested
    @DisplayName("dados válidos")
    final class DadosValidos {
        @Test
        @DisplayName("deve passar sem violações quando todos os campos forem válidos")
        void shouldPassWithNoViolations() {
            var violations = validate(LocalDate.of(1990, 5, 10), "Matemática", "8h");
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("dateOfBirth")
    final class DateOfBirth {
        @Test
        @DisplayName("deve falhar quando dateOfBirth for null")
        void shouldFail_whenNull() {
            var violations = validate(null, "Matemática", "8h");
            assertViolationOnField(violations, "dateOfBirth", "Data de nascimento é necessário informar");
        }

        @Test
        @DisplayName("deve falhar quando dateOfBirth for uma data futura")
        void shouldFail_whenFutureDate() {
            var violations = validate(LocalDate.now().plusDays(1), "Matemática", "8h");
            assertViolationOnField(violations, "dateOfBirth", "Data de nascimento deve ser no passado");
        }

        @Test
        @DisplayName("deve falhar quando dateOfBirth for hoje")
        void shouldFail_whenToday() {
            var violations = validate(LocalDate.now(), "Matemática", "8h");
            assertViolationOnField(violations, "dateOfBirth", "Data de nascimento deve ser no passado");
        }

        @Test
        @DisplayName("deve passar quando dateOfBirth for uma data no passado")
        void shouldPass_whenPastDate() {
            var violations = validate(LocalDate.now().minusDays(1), "Matemática", "8h");
            assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("dateOfBirth"));
        }
    }

    @Nested
    @DisplayName("specialty")
    final class Specialty {
        @Test
        @DisplayName("deve falhar quando specialty for null")
        void shouldFail_whenNull() {
            var violations = validate(LocalDate.of(1990, 5, 10), null, "8h");
            assertViolationOnField(violations, "specialty", "Especialidade não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando specialty for blank")
        void shouldFail_whenBlank() {
            var violations = validate(LocalDate.of(1990, 5, 10), "   ", "8h");
            assertViolationOnField(violations, "specialty", "Especialidade não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando specialty tiver menos de 3 caracteres")
        void shouldFail_whenTooShort() {
            var violations = validate(LocalDate.of(1990, 5, 10), "Ma", "8h");
            assertViolationOnField(violations, "specialty", "Especialidade deve ter entre 3 e 30 caracteres");
        }

        @Test
        @DisplayName("deve falhar quando specialty ultrapassar 30 caracteres")
        void shouldFail_whenTooLong() {
            var violations = validate(LocalDate.of(1990, 5, 10), "A".repeat(31), "8h");
            assertViolationOnField(violations, "specialty", "Especialidade deve ter entre 3 e 30 caracteres");
        }

        @Test
        @DisplayName("deve falhar quando specialty tiver espaço no início")
        void shouldFail_whenLeadingSpace() {
            var violations = validate(LocalDate.of(1990, 5, 10), " Matemática", "8h");
            assertViolationOnField(violations, "specialty", "Must not contain leading or trailing spaces");
        }

        @Test
        @DisplayName("deve falhar quando specialty tiver espaço no final")
        void shouldFail_whenTrailingSpace() {
            var violations = validate(LocalDate.of(1990, 5, 10), "Matemática ", "8h");
            assertViolationOnField(violations, "specialty", "Must not contain leading or trailing spaces");
        }

        @Test
        @DisplayName("deve passar quando specialty tiver exatamente 30 caracteres")
        void shouldPass_whenExactly30Chars() {
            var violations = validate(LocalDate.of(1990, 5, 10), "A".repeat(30), "8h");
            assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("specialty"));
        }
    }

    @Nested
    @DisplayName("workload")
    final class Workload {
        @Test
        @DisplayName("deve falhar quando workload for null")
        void shouldFail_whenNull() {
            var violations = validate(LocalDate.of(1990, 5, 10), "Matemática", null);
            assertViolationOnField(violations, "workload", "Carga horária não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando workload for blank")
        void shouldFail_whenBlank() {
            var violations = validate(LocalDate.of(1990, 5, 10), "Matemática", "   ");
            assertViolationOnField(violations, "workload", "Carga horária não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando workload não seguir o formato esperado")
        void shouldFail_whenInvalidFormat() {
            var violations = validate(LocalDate.of(1990, 5, 10), "Matemática", "8horas");
            assertViolationOnField(violations, "workload",
                    "Carga horária deve estar no formato '8h', '12h', etc.");
        }

        @Test
        @DisplayName("deve falhar quando workload não tiver o sufixo 'h'")
        void shouldFail_whenMissingSuffix() {
            var violations = validate(LocalDate.of(1990, 5, 10), "Matemática", "8");
            assertViolationOnField(violations, "workload",
                    "Carga horária deve estar no formato '8h', '12h', etc.");
        }

        @Test
        @DisplayName("deve passar com workload de 1 dígito seguido de 'h'")
        void shouldPass_whenSingleDigitH() {
            var violations = validate(LocalDate.of(1990, 5, 10), "Matemática", "8h");
            assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("workload"));
        }

        @Test
        @DisplayName("deve passar com workload de 2 dígitos seguidos de 'h'")
        void shouldPass_whenDoubleDigitH() {
            var violations = validate(LocalDate.of(1990, 5, 10), "Matemática", "12h");
            assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("workload"));
        }

        @Test
        @DisplayName("deve falhar quando workload tiver 3 dígitos (fora do padrão)")
        void shouldFail_whenTripleDigit() {
            var violations = validate(LocalDate.of(1990, 5, 10), "Matemática", "100h");
            assertViolationOnField(violations, "workload",
                    "Carga horária deve estar no formato '8h', '12h', etc.");
        }
    }
}
