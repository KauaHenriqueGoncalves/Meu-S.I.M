package com.system.application.modules.identify.legalguardian.dto;

import com.system.application.modules.identity.legalguardian.dto.CreateLegalGuardianRequest;
import com.system.application.modules.identity.legalguardian.dto.LegalGuardianRequest;
import com.system.application.modules.identity.user.dto.UserRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CreateLegalGuardianRequest")
public class CreateLegalGuardianRequestTest {
    private Validator validator;

    private static final String CPF_VALIDO = "52998224725";

    private UserRequest validUserRequest;
    private LegalGuardianRequest validLegalGuardianRequest;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();

        validUserRequest = new UserRequest(
                "Maria Silva",
                "maria@email.com",
                "senha123",
                CPF_VALIDO,
                "81999990000",
                "Rua B, 200"
        );

        validLegalGuardianRequest = new LegalGuardianRequest("Mãe");
    }

    private Set<ConstraintViolation<CreateLegalGuardianRequest>> validate(
            UserRequest userRequest, LegalGuardianRequest legalGuardianRequest
    ) {
        return validator.validate(new CreateLegalGuardianRequest(userRequest, legalGuardianRequest));
    }

    private void assertViolationOnField(
            Set<ConstraintViolation<CreateLegalGuardianRequest>> violations,
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
        @DisplayName("deve passar sem violações quando ambos os campos forem válidos")
        void shouldPassWithNoViolations() {
            var violations = validate(validUserRequest, validLegalGuardianRequest);
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("userRequest")
    final class UserRequestField {
        @Test
        @DisplayName("deve falhar quando userRequest for null")
        void shouldFail_whenNull() {
            var violations = validate(null, validLegalGuardianRequest);
            assertViolationOnField(violations, "userRequest", "não deve ser nulo");
        }

        @Test
        @DisplayName("deve propagar violação quando username do userRequest for blank")
        void shouldPropagateViolation_whenUsernameIsBlank() {
            UserRequest invalid = new UserRequest(
                    "   ",
                    "maria@email.com",
                    "senha123",
                    CPF_VALIDO,
                    "81999990000",
                    "Rua B, 200"
            );
            var violations = validate(invalid, validLegalGuardianRequest);
            assertThat(violations)
                    .anyMatch(v -> v.getPropertyPath().toString().contains("username"));
        }

        @Test
        @DisplayName("deve propagar violação quando email do userRequest for inválido")
        void shouldPropagateViolation_whenEmailIsInvalid() {
            UserRequest invalid = new UserRequest(
                    "Maria Silva",
                    "nao-e-email",
                    "senha123",
                    CPF_VALIDO,
                    "81999990000",
                    "Rua B, 200"
            );
            var violations = validate(invalid, validLegalGuardianRequest);
            assertThat(violations)
                    .anyMatch(v -> v.getPropertyPath().toString().contains("email"));
        }

        @Test
        @DisplayName("deve propagar violação quando cpf do userRequest for inválido")
        void shouldPropagateViolation_whenCpfIsInvalid() {
            UserRequest invalid = new UserRequest(
                    "Maria Silva",
                    "maria@email.com",
                    "senha123",
                    "11111111111",
                    "81999990000",
                    "Rua B, 200"
            );
            var violations = validate(invalid, validLegalGuardianRequest);
            assertThat(violations)
                    .anyMatch(v -> v.getPropertyPath().toString().contains("cpf"));
        }
    }

    @Nested
    @DisplayName("legalGuardianRequest")
    final class LegalGuardianRequestField {
        @Test
        @DisplayName("deve falhar quando legalGuardianRequest for null")
        void shouldFail_whenNull() {
            var violations = validate(validUserRequest, null);
            assertViolationOnField(violations, "legalGuardianRequest", "não deve ser nulo");
        }

        @Test
        @DisplayName("deve propagar violação quando degreeOfKinship for blank")
        void shouldPropagateViolation_whenDegreeOfKinshipIsBlank() {
            LegalGuardianRequest invalid = new LegalGuardianRequest("   ");
            var violations = validate(validUserRequest, invalid);
            assertThat(violations)
                    .anyMatch(v -> v.getPropertyPath().toString().contains("degreeOfKinship"));
        }

        @Test
        @DisplayName("deve propagar violação quando degreeOfKinship tiver menos de 3 caracteres")
        void shouldPropagateViolation_whenDegreeOfKinshipTooShort() {
            LegalGuardianRequest invalid = new LegalGuardianRequest("Av");
            var violations = validate(validUserRequest, invalid);
            assertThat(violations)
                    .anyMatch(v -> v.getPropertyPath().toString().contains("degreeOfKinship"));
        }
    }
}
