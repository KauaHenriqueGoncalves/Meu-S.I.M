package com.system.application.modules.identify.legalguardian.dto;

import com.system.application.modules.identity.legalguardian.dto.UpdateLegalGuardianRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UpdateLegalGuardianRequest")
public class UpdateLegalGuardianRequestTest {
    private Validator validator;

    private String username;
    private String email;
    private String phoneNumber;
    private String address;
    private Boolean isActive;
    private String degreeOfKinship;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();

        username        = "Maria Silva";
        email           = "maria@email.com";
        phoneNumber     = "81999990000";
        address         = "Rua B, 200";
        isActive        = true;
        degreeOfKinship = "Mãe";
    }

    private Set<ConstraintViolation<UpdateLegalGuardianRequest>> validate(
            String u, String e, String ph, String ad, Boolean active, String dok
    ) {
        return validator.validate(new UpdateLegalGuardianRequest(u, e, ph, ad, active, dok));
    }

    private void assertViolationOnField(
            Set<ConstraintViolation<UpdateLegalGuardianRequest>> violations,
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
            var violations = validate(username, email, phoneNumber, address, isActive, degreeOfKinship);
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("username")
    final class Username {
        @Test
        @DisplayName("deve falhar quando username for null")
        void shouldFail_whenNull() {
            var violations = validate(null, email, phoneNumber, address, isActive, degreeOfKinship);
            assertViolationOnField(violations, "username", "Nome não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando username for blank")
        void shouldFail_whenBlank() {
            var violations = validate("   ", email, phoneNumber, address, isActive, degreeOfKinship);
            assertViolationOnField(violations, "username", "Nome não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando username ultrapassar 100 caracteres")
        void shouldFail_whenTooLong() {
            var violations = validate("A".repeat(101), email, phoneNumber, address, isActive, degreeOfKinship);
            assertViolationOnField(violations, "username", "Nome deve ser menor que 100 caracteres");
        }

        @Test
        @DisplayName("deve falhar quando username tiver espaço no início")
        void shouldFail_whenLeadingSpace() {
            var violations = validate(" Maria", email, phoneNumber, address, isActive, degreeOfKinship);
            assertViolationOnField(violations, "username", "Must not contain leading or trailing spaces");
        }

        @Test
        @DisplayName("deve falhar quando username tiver espaço no final")
        void shouldFail_whenTrailingSpace() {
            var violations = validate("Maria ", email, phoneNumber, address, isActive, degreeOfKinship);
            assertViolationOnField(violations, "username", "Must not contain leading or trailing spaces");
        }

        @Test
        @DisplayName("deve passar quando username tiver exatamente 100 caracteres")
        void shouldPass_whenExactly100Chars() {
            var violations = validate("A".repeat(100), email, phoneNumber, address, isActive, degreeOfKinship);
            assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("username"));
        }
    }

    @Nested
    @DisplayName("email")
    final class Email {
        @Test
        @DisplayName("deve falhar quando email for null")
        void shouldFail_whenNull() {
            var violations = validate(username, null, phoneNumber, address, isActive, degreeOfKinship);
            assertViolationOnField(violations, "email", "Email não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando email for blank")
        void shouldFail_whenBlank() {
            var violations = validate(username, "   ", phoneNumber, address, isActive, degreeOfKinship);
            assertViolationOnField(violations, "email", "Email não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando email tiver formato inválido")
        void shouldFail_whenInvalidFormat() {
            var violations = validate(username, "nao-e-um-email", phoneNumber, address, isActive, degreeOfKinship);
            assertViolationOnField(violations, "email", "Formato do Email incorreto");
        }

        @Test
        @DisplayName("deve falhar quando email ultrapassar 255 caracteres")
        void shouldFail_whenTooLong() {
            String longo = "a".repeat(246) + "@email.com"; // 256 chars
            var violations = validate(username, longo, phoneNumber, address, isActive, degreeOfKinship);
            assertViolationOnField(violations, "email", "Email deve ser menor que 255 caracteres");
        }
    }

    @Nested
    @DisplayName("phoneNumber")
    final class PhoneNumber {
        @Test
        @DisplayName("deve falhar quando phoneNumber for null")
        void shouldFail_whenNull() {
            var violations = validate(username, email, null, address, isActive, degreeOfKinship);
            assertViolationOnField(violations, "phoneNumber", "Número de telefone não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando phoneNumber for blank")
        void shouldFail_whenBlank() {
            var violations = validate(username, email, "   ", address, isActive, degreeOfKinship);
            assertViolationOnField(violations, "phoneNumber", "Número de telefone não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando phoneNumber ultrapassar 20 caracteres")
        void shouldFail_whenTooLong() {
            var violations = validate(username, email, "1".repeat(21), address, isActive, degreeOfKinship);
            assertViolationOnField(violations, "phoneNumber", "Número de telefone deve ser menor que 20 caracteres");
        }

        @Test
        @DisplayName("deve falhar quando phoneNumber tiver espaço no início")
        void shouldFail_whenLeadingSpace() {
            var violations = validate(username, email, " 81999990000", address, isActive, degreeOfKinship);
            assertViolationOnField(violations, "phoneNumber", "Must not contain leading or trailing spaces");
        }

        @Test
        @DisplayName("deve falhar quando phoneNumber tiver espaço no final")
        void shouldFail_whenTrailingSpace() {
            var violations = validate(username, email, "81999990000 ", address, isActive, degreeOfKinship);
            assertViolationOnField(violations, "phoneNumber", "Must not contain leading or trailing spaces");
        }

        @Test
        @DisplayName("deve passar quando phoneNumber tiver exatamente 20 caracteres")
        void shouldPass_whenExactly20Chars() {
            var violations = validate(username, email, "1".repeat(20), address, isActive, degreeOfKinship);
            assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("phoneNumber"));
        }
    }

    @Nested
    @DisplayName("address")
    final class Address {
        @Test
        @DisplayName("deve falhar quando address for null")
        void shouldFail_whenNull() {
            var violations = validate(username, email, phoneNumber, null, isActive, degreeOfKinship);
            assertViolationOnField(violations, "address", "Endereço não pode ser nulo");
        }

        @Test
        @DisplayName("deve falhar quando address ultrapassar 100 caracteres")
        void shouldFail_whenTooLong() {
            var violations = validate(username, email, phoneNumber, "A".repeat(101), isActive, degreeOfKinship);
            assertViolationOnField(violations, "address", "Endereço deve ser menor que 100 caracteres");
        }

        @Test
        @DisplayName("deve falhar quando address tiver espaço no início")
        void shouldFail_whenLeadingSpace() {
            var violations = validate(username, email, phoneNumber, " Rua B", isActive, degreeOfKinship);
            assertViolationOnField(violations, "address", "Must not contain leading or trailing spaces");
        }

        @Test
        @DisplayName("deve falhar quando address tiver espaço no final")
        void shouldFail_whenTrailingSpace() {
            var violations = validate(username, email, phoneNumber, "Rua B ", isActive, degreeOfKinship);
            assertViolationOnField(violations, "address", "Must not contain leading or trailing spaces");
        }

        @Test
        @DisplayName("deve passar quando address tiver exatamente 100 caracteres")
        void shouldPass_whenExactly100Chars() {
            var violations = validate(username, email, phoneNumber, "A".repeat(100), isActive, degreeOfKinship);
            assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("address"));
        }
    }

    @Nested
    @DisplayName("isActive")
    final class IsActive {
        @Test
        @DisplayName("deve falhar quando isActive for null")
        void shouldFail_whenNull() {
            var violations = validate(username, email, phoneNumber, address, null, degreeOfKinship);
            assertViolationOnField(violations, "isActive", "Campo de status deve ser informado");
        }

        @Test
        @DisplayName("deve passar quando isActive for false")
        void shouldPass_whenFalse() {
            var violations = validate(username, email, phoneNumber, address, false, degreeOfKinship);
            assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("isActive"));
        }
    }

    @Nested
    @DisplayName("degreeOfKinship")
    final class DegreeOfKinship {
        @Test
        @DisplayName("deve falhar quando degreeOfKinship for null")
        void shouldFail_whenNull() {
            var violations = validate(username, email, phoneNumber, address, isActive, null);
            assertViolationOnField(violations, "degreeOfKinship", "Nível de Parentesco não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando degreeOfKinship for blank")
        void shouldFail_whenBlank() {
            var violations = validate(username, email, phoneNumber, address, isActive, "   ");
            assertViolationOnField(violations, "degreeOfKinship", "Nível de Parentesco não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando degreeOfKinship ultrapassar 30 caracteres")
        void shouldFail_whenTooLong() {
            var violations = validate(username, email, phoneNumber, address, isActive, "A".repeat(31));
            assertViolationOnField(violations, "degreeOfKinship", "Nível de Parentesco deve ter entre 3 e 30 caracteres");
        }

        @Test
        @DisplayName("deve falhar quando degreeOfKinship tiver espaço no início")
        void shouldFail_whenLeadingSpace() {
            var violations = validate(username, email, phoneNumber, address, isActive, " Mãe");
            assertViolationOnField(violations, "degreeOfKinship", "Must not contain leading or trailing spaces");
        }

        @Test
        @DisplayName("deve falhar quando degreeOfKinship tiver espaço no final")
        void shouldFail_whenTrailingSpace() {
            var violations = validate(username, email, phoneNumber, address, isActive, "Mãe ");
            assertViolationOnField(violations, "degreeOfKinship", "Must not contain leading or trailing spaces");
        }

        @Test
        @DisplayName("deve passar quando degreeOfKinship tiver exatamente 30 caracteres")
        void shouldPass_whenExactly30Chars() {
            var violations = validate(username, email, phoneNumber, address, isActive, "A".repeat(30));
            assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("degreeOfKinship"));
        }
    }
}
