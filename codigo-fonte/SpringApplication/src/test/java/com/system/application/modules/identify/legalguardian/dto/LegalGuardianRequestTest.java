package com.system.application.modules.identify.legalguardian.dto;

import com.system.application.modules.identity.legalguardian.dto.LegalGuardianRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LegalGuardianRequest")
public class LegalGuardianRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private Set<ConstraintViolation<LegalGuardianRequest>> validate(String degreeOfKinship) {
        return validator.validate(new LegalGuardianRequest(degreeOfKinship));
    }

    private void assertViolationOnField(
            Set<ConstraintViolation<LegalGuardianRequest>> violations,
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
        @DisplayName("deve passar sem violações quando degreeOfKinship for válido")
        void shouldPassWithNoViolations() {
            var violations = validate("Mãe");
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("deve passar quando degreeOfKinship tiver exatamente 3 caracteres")
        void shouldPass_whenExactly3Chars() {
            var violations = validate("Pai");
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("deve passar quando degreeOfKinship tiver exatamente 30 caracteres")
        void shouldPass_whenExactly30Chars() {
            var violations = validate("A".repeat(30));
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("degreeOfKinship")
    final class DegreeOfKinship {
        @Test
        @DisplayName("deve falhar quando degreeOfKinship for null")
        void shouldFail_whenNull() {
            var violations = validate(null);
            assertViolationOnField(violations, "degreeOfKinship", "Nível de Parentesco não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando degreeOfKinship for blank")
        void shouldFail_whenBlank() {
            var violations = validate("   ");
            assertViolationOnField(violations, "degreeOfKinship", "Nível de Parentesco não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando degreeOfKinship tiver menos de 3 caracteres")
        void shouldFail_whenTooShort() {
            var violations = validate("Av");
            assertViolationOnField(violations, "degreeOfKinship", "Nível de Parentesco deve ter entre 3 e 30 caracteres");
        }

        @Test
        @DisplayName("deve falhar quando degreeOfKinship ultrapassar 30 caracteres")
        void shouldFail_whenTooLong() {
            var violations = validate("A".repeat(31));
            assertViolationOnField(violations, "degreeOfKinship", "Nível de Parentesco deve ter entre 3 e 30 caracteres");
        }

        @Test
        @DisplayName("deve falhar quando degreeOfKinship tiver espaço no início")
        void shouldFail_whenLeadingSpace() {
            var violations = validate(" Mãe");
            assertViolationOnField(violations, "degreeOfKinship", "Must not contain leading or trailing spaces");
        }

        @Test
        @DisplayName("deve falhar quando degreeOfKinship tiver espaço no final")
        void shouldFail_whenTrailingSpace() {
            var violations = validate("Mãe ");
            assertViolationOnField(violations, "degreeOfKinship", "Must not contain leading or trailing spaces");
        }
    }
}
