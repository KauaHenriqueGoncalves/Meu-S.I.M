package com.system.application.modules.identify.collaborator.dto;

import com.system.application.modules.identity.collaborator.dto.UpdateCollaboratorRequest;
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

@DisplayName("UpdateCollaboratorRequest")
public class UpdateCollaboratorRequestTest {
    private Validator validator;

    private String username;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String address;
    private Boolean isActive;
    private String specialty;
    private String workload;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();

        username    = "Carlos Lima";
        email       = "carlos@email.com";
        phoneNumber = "81999990000";
        dateOfBirth = LocalDate.of(1990, 5, 10);
        address     = "Rua C, 300";
        isActive    = true;
        specialty   = "Matemática";
        workload    = "8h";
    }

    private Set<ConstraintViolation<UpdateCollaboratorRequest>> validate(
            String u, String e, String ph, LocalDate dob, String ad, Boolean active, String sp, String wl
    ) {
        return validator.validate(new UpdateCollaboratorRequest(u, e, ph, dob, ad, active, sp, wl));
    }

    private void assertViolationOnField(
            Set<ConstraintViolation<UpdateCollaboratorRequest>> violations,
            String field,
            String expectedMessage
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
            var violations = validate(username, email, phoneNumber, dateOfBirth, address, isActive, specialty, workload);
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("username")
    final class Username {
        @Test
        @DisplayName("deve falhar quando username for null")
        void shouldFail_whenNull() {
            var violations = validate(null, email, phoneNumber, dateOfBirth, address, isActive, specialty, workload);
            assertViolationOnField(violations, "username", "Nome não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando username for blank")
        void shouldFail_whenBlank() {
            var violations = validate("   ", email, phoneNumber, dateOfBirth, address, isActive, specialty, workload);
            assertViolationOnField(violations, "username", "Nome não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando username ultrapassar 100 caracteres")
        void shouldFail_whenTooLong() {
            var violations = validate("A".repeat(101), email, phoneNumber, dateOfBirth, address, isActive, specialty, workload);
            assertViolationOnField(violations, "username", "Nome deve ser menor que 100 caracteres");
        }

        @Test
        @DisplayName("deve falhar quando username tiver espaço no início")
        void shouldFail_whenLeadingSpace() {
            var violations = validate(" Carlos", email, phoneNumber, dateOfBirth, address, isActive, specialty, workload);
            assertViolationOnField(violations, "username", "Must not contain leading or trailing spaces");
        }

        @Test
        @DisplayName("deve falhar quando username tiver espaço no final")
        void shouldFail_whenTrailingSpace() {
            var violations = validate("Carlos ", email, phoneNumber, dateOfBirth, address, isActive, specialty, workload);
            assertViolationOnField(violations, "username", "Must not contain leading or trailing spaces");
        }
    }

    @Nested
    @DisplayName("email")
    final class Email {
        @Test
        @DisplayName("deve falhar quando email for null")
        void shouldFail_whenNull() {
            var violations = validate(username, null, phoneNumber, dateOfBirth, address, isActive, specialty, workload);
            assertViolationOnField(violations, "email", "Email não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando email for blank")
        void shouldFail_whenBlank() {
            var violations = validate(username, "   ", phoneNumber, dateOfBirth, address, isActive, specialty, workload);
            assertViolationOnField(violations, "email", "Email não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando email tiver formato inválido")
        void shouldFail_whenInvalidFormat() {
            var violations = validate(username, "nao-e-email", phoneNumber, dateOfBirth, address, isActive, specialty, workload);
            assertViolationOnField(violations, "email", "Formato do Email incorreto");
        }

        @Test
        @DisplayName("deve falhar quando email ultrapassar 255 caracteres")
        void shouldFail_whenTooLong() {
            String longo = "a".repeat(246) + "@email.com";
            var violations = validate(username, longo, phoneNumber, dateOfBirth, address, isActive, specialty, workload);
            assertViolationOnField(violations, "email", "Email deve ser menor que 255 caracteres");
        }
    }

    @Nested
    @DisplayName("phoneNumber")
    final class PhoneNumber {
        @Test
        @DisplayName("deve falhar quando phoneNumber for null")
        void shouldFail_whenNull() {
            var violations = validate(username, email, null, dateOfBirth, address, isActive, specialty, workload);
            assertViolationOnField(violations, "phoneNumber", "Número de telefone não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando phoneNumber for blank")
        void shouldFail_whenBlank() {
            var violations = validate(username, email, "   ", dateOfBirth, address, isActive, specialty, workload);
            assertViolationOnField(violations, "phoneNumber", "Número de telefone não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando phoneNumber ultrapassar 20 caracteres")
        void shouldFail_whenTooLong() {
            var violations = validate(username, email, "1".repeat(21), dateOfBirth, address, isActive, specialty, workload);
            assertViolationOnField(violations, "phoneNumber", "Número de telefone deve ser menor que 20 caracteres");
        }

        @Test
        @DisplayName("deve falhar quando phoneNumber tiver espaço no início")
        void shouldFail_whenLeadingSpace() {
            var violations = validate(username, email, " 81999990000", dateOfBirth, address, isActive, specialty, workload);
            assertViolationOnField(violations, "phoneNumber", "Must not contain leading or trailing spaces");
        }

        @Test
        @DisplayName("deve falhar quando phoneNumber tiver espaço no final")
        void shouldFail_whenTrailingSpace() {
            var violations = validate(username, email, "81999990000 ", dateOfBirth, address, isActive, specialty, workload);
            assertViolationOnField(violations, "phoneNumber", "Must not contain leading or trailing spaces");
        }
    }

    @Nested
    @DisplayName("dateOfBirth")
    final class DateOfBirth {
        @Test
        @DisplayName("deve falhar quando dateOfBirth for null")
        void shouldFail_whenNull() {
            var violations = validate(username, email, phoneNumber, null, address, isActive, specialty, workload);
            assertViolationOnField(violations, "dateOfBirth", "Data de nascimento é necessário informar");
        }

        @Test
        @DisplayName("deve falhar quando dateOfBirth for uma data futura")
        void shouldFail_whenFutureDate() {
            var violations = validate(username, email, phoneNumber, LocalDate.now().plusDays(1), address, isActive, specialty, workload);
            assertViolationOnField(violations, "dateOfBirth", "Data de nascimento deve ser no passado");
        }

        @Test
        @DisplayName("deve falhar quando dateOfBirth for hoje")
        void shouldFail_whenToday() {
            var violations = validate(username, email, phoneNumber, LocalDate.now(), address, isActive, specialty, workload);
            assertViolationOnField(violations, "dateOfBirth", "Data de nascimento deve ser no passado");
        }
    }

    @Nested
    @DisplayName("address")
    final class Address {
        @Test
        @DisplayName("deve falhar quando address for null")
        void shouldFail_whenNull() {
            var violations = validate(username, email, phoneNumber, dateOfBirth, null, isActive, specialty, workload);
            assertViolationOnField(violations, "address", "Endereço não pode ser nulo");
        }

        @Test
        @DisplayName("deve falhar quando address ultrapassar 100 caracteres")
        void shouldFail_whenTooLong() {
            var violations = validate(username, email, phoneNumber, dateOfBirth, "A".repeat(101), isActive, specialty, workload);
            assertViolationOnField(violations, "address", "Endereço deve ser menor que 100 caracteres");
        }

        @Test
        @DisplayName("deve falhar quando address tiver espaço no início")
        void shouldFail_whenLeadingSpace() {
            var violations = validate(username, email, phoneNumber, dateOfBirth, " Rua C", isActive, specialty, workload);
            assertViolationOnField(violations, "address", "Must not contain leading or trailing spaces");
        }

        @Test
        @DisplayName("deve falhar quando address tiver espaço no final")
        void shouldFail_whenTrailingSpace() {
            var violations = validate(username, email, phoneNumber, dateOfBirth, "Rua C ", isActive, specialty, workload);
            assertViolationOnField(violations, "address", "Must not contain leading or trailing spaces");
        }
    }

    @Nested
    @DisplayName("isActive")
    final class IsActive {
        @Test
        @DisplayName("deve falhar quando isActive for null")
        void shouldFail_whenNull() {
            var violations = validate(username, email, phoneNumber, dateOfBirth, address, null, specialty, workload);
            assertViolationOnField(violations, "isActive", "Campo de status deve ser informado");
        }

        @Test
        @DisplayName("deve passar quando isActive for false")
        void shouldPass_whenFalse() {
            var violations = validate(username, email, phoneNumber, dateOfBirth, address, false, specialty, workload);
            assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("isActive"));
        }
    }

    @Nested
    @DisplayName("specialty")
    final class Specialty {
        @Test
        @DisplayName("deve falhar quando specialty for null")
        void shouldFail_whenNull() {
            var violations = validate(username, email, phoneNumber, dateOfBirth, address, isActive, null, workload);
            assertViolationOnField(violations, "specialty", "Especialidade não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando specialty for blank")
        void shouldFail_whenBlank() {
            var violations = validate(username, email, phoneNumber, dateOfBirth, address, isActive, "   ", workload);
            assertViolationOnField(violations, "specialty", "Especialidade não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando specialty tiver menos de 3 caracteres")
        void shouldFail_whenTooShort() {
            var violations = validate(username, email, phoneNumber, dateOfBirth, address, isActive, "Ma", workload);
            assertViolationOnField(violations, "specialty", "Especialidade deve ter entre 3 e 30 caracteres");
        }

        @Test
        @DisplayName("deve falhar quando specialty ultrapassar 30 caracteres")
        void shouldFail_whenTooLong() {
            var violations = validate(username, email, phoneNumber, dateOfBirth, address, isActive, "A".repeat(31), workload);
            assertViolationOnField(violations, "specialty", "Especialidade deve ter entre 3 e 30 caracteres");
        }

        @Test
        @DisplayName("deve falhar quando specialty tiver espaço no início")
        void shouldFail_whenLeadingSpace() {
            var violations = validate(username, email, phoneNumber, dateOfBirth, address, isActive, " Matemática", workload);
            assertViolationOnField(violations, "specialty", "Must not contain leading or trailing spaces");
        }

        @Test
        @DisplayName("deve falhar quando specialty tiver espaço no final")
        void shouldFail_whenTrailingSpace() {
            var violations = validate(username, email, phoneNumber, dateOfBirth, address, isActive, "Matemática ", workload);
            assertViolationOnField(violations, "specialty", "Must not contain leading or trailing spaces");
        }
    }

    @Nested
    @DisplayName("workload")
    final class Workload {
        @Test
        @DisplayName("deve falhar quando workload for null")
        void shouldFail_whenNull() {
            var violations = validate(username, email, phoneNumber, dateOfBirth, address, isActive, specialty, null);
            assertViolationOnField(violations, "workload", "Carga horária é obrigatória");
        }

        @Test
        @DisplayName("deve falhar quando workload for blank")
        void shouldFail_whenBlank() {
            var violations = validate(username, email, phoneNumber, dateOfBirth, address, isActive, specialty, "   ");
            assertViolationOnField(violations, "workload", "Carga horária é obrigatória");
        }

        @Test
        @DisplayName("deve falhar quando workload não seguir o formato esperado")
        void shouldFail_whenInvalidFormat() {
            var violations = validate(username, email, phoneNumber, dateOfBirth, address, isActive, specialty, "8horas");
            assertViolationOnField(violations, "workload",
                    "Carga horária deve estar no formato '8h', '12h', etc.");
        }

        @Test
        @DisplayName("deve falhar quando workload tiver 3 dígitos (fora do padrão)")
        void shouldFail_whenTripleDigit() {
            var violations = validate(username, email, phoneNumber, dateOfBirth, address, isActive, specialty, "100h");
            assertViolationOnField(violations, "workload",
                    "Carga horária deve estar no formato '8h', '12h', etc.");
        }

        @Test
        @DisplayName("deve passar com workload de 1 dígito seguido de 'h'")
        void shouldPass_whenSingleDigitH() {
            var violations = validate(username, email, phoneNumber, dateOfBirth, address, isActive, specialty, "8h");
            assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("workload"));
        }

        @Test
        @DisplayName("deve passar com workload de 2 dígitos seguidos de 'h'")
        void shouldPass_whenDoubleDigitH() {
            var violations = validate(username, email, phoneNumber, dateOfBirth, address, isActive, specialty, "12h");
            assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("workload"));
        }
    }
}
