package com.system.application.modules.identify.user.dto;

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
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("UserRequest")
class UserRequestTest {
    private Validator validator;

    private static final String CPF_VALIDO = "52998224725";

    private String username;
    private String email;
    private String password;
    private String cpf;
    private String phoneNumber;
    private String address;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        username = "João Silva";
        email = "joao@email.com";
        password = "senha123";
        cpf = CPF_VALIDO;
        phoneNumber = "81999990000";
        address = "Rua A, 100";
    }

    private Set<ConstraintViolation<UserRequest>> validate(
            String u, String e, String p, String c, String ph, String ad
    ) {
        return validator.validate(new UserRequest(u, e, p, c, ph, ad));
    }

    private void assertNoViolations(Set<ConstraintViolation<UserRequest>> violations) {
        assertThat(violations).isEmpty();
    }

    private void assertViolationOnField(
            Set<ConstraintViolation<UserRequest>> violations,
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
            var violations = validate(username, email, password, cpf, phoneNumber, address);
            assertNoViolations(violations);
        }

        @Test
        @DisplayName("deve converter email para lowercase automaticamente")
        void shouldConvertEmailToLowercase() {
            UserRequest request = new UserRequest(username, "JOAO@EMAIL.COM", password, cpf, phoneNumber, address);
            assertThat(request.email()).isEqualTo("joao@email.com");
        }

        @Test
        @DisplayName("address pode ser string vazia (apenas @NotNull)")
        void shouldAcceptEmptyAddress() {
            var violations = validate(username, email, password, cpf, phoneNumber, "");
            assertNoViolations(violations);
        }
    }

    @Nested
    @DisplayName("username")
    final class Username {
        @Test
        @DisplayName("deve falhar quando username for blank")
        void shouldFail_whenBlank() {
            var violations = validate("   ", email, password, cpf, phoneNumber, address);
            assertViolationOnField(violations, "username", "Nome não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando username for null")
        void shouldFail_whenNull() {
            var violations = validate(null, email, password, cpf, phoneNumber, address);
            assertViolationOnField(violations, "username", "Nome não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando username tiver espaço no início")
        void shouldFail_whenLeadingSpace() {
            var violations = validate(" João", email, password, cpf, phoneNumber, address);
            assertViolationOnField(violations, "username", "Must not contain leading or trailing spaces");
        }

        @Test
        @DisplayName("deve falhar quando username tiver espaço no final")
        void shouldFail_whenTrailingSpace() {
            var violations = validate("João ", email, password, cpf, phoneNumber, address);
            assertViolationOnField(violations, "username", "Must not contain leading or trailing spaces");
        }

        @Test
        @DisplayName("deve falhar quando username ultrapassar 100 caracteres")
        void shouldFail_whenExceeds100Chars() {
            String longo = "A".repeat(101);
            var violations = validate(longo, email, password, cpf, phoneNumber, address);
            assertViolationOnField(violations, "username", "Nome deve ser menor que 100 caracteres");
        }

        @Test
        @DisplayName("deve passar quando username tiver exatamente 100 caracteres")
        void shouldPass_whenExactly100Chars() {
            String exato = "A".repeat(100);
            var violations = validate(exato, email, password, cpf, phoneNumber, address);
            assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("username"));
        }
    }

    @Nested
    @DisplayName("templates/email")
    final class Email {
        @Test
        @DisplayName("deve falhar quando email for blank")
        void shouldFail_whenBlank() {
            var violations = validate(username, "   ", password, cpf, phoneNumber, address);
            assertViolationOnField(violations, "email", "Email não pode ser vazio");
        }

        @Test
        @DisplayName("deve lançar NullPointerException quando email for null")
        void shouldThrow_whenNull() {
            assertThatThrownBy(() -> validate(username, null, password, cpf, phoneNumber, address))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("deve falhar quando email tiver formato inválido")
        void shouldFail_whenInvalidFormat() {
            var violations = validate(username, "nao-e-um-email", password, cpf, phoneNumber, address);
            assertViolationOnField(violations, "email", "Formato do Email incorreto");
        }

        @Test
        @DisplayName("deve falhar quando email ultrapassar 255 caracteres")
        void shouldFail_whenExceeds255Chars() {
            String longo = "a".repeat(246) + "@email.com"; // 256 chars
            var violations = validate(username, longo, password, cpf, phoneNumber, address);
            assertViolationOnField(violations, "email", "Email deve ser menor que 255 caracteres");
        }
    }

    @Nested
    @DisplayName("password")
    final class Password {
        @Test
        @DisplayName("deve falhar quando password for blank")
        void shouldFail_whenBlank() {
            var violations = validate(username, email, "   ", cpf, phoneNumber, address);
            assertViolationOnField(violations, "password", "Senha não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando password for null")
        void shouldFail_whenNull() {
            var violations = validate(username, email, null, cpf, phoneNumber, address);
            assertViolationOnField(violations, "password", "Senha não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando password tiver menos de 8 caracteres")
        void shouldFail_whenTooShort() {
            var violations = validate(username, email, "abc12", cpf, phoneNumber, address);
            assertViolationOnField(violations, "password", "Senha deve ser entre 8 e 20 caracteres");
        }

        @Test
        @DisplayName("deve falhar quando password ultrapassar 20 caracteres")
        void shouldFail_whenTooLong() {
            var violations = validate(username, email, "A".repeat(21), cpf, phoneNumber, address);
            assertViolationOnField(violations, "password", "Senha deve ser entre 8 e 20 caracteres");
        }

        @Test
        @DisplayName("deve falhar quando password tiver espaço no início")
        void shouldFail_whenLeadingSpace() {
            var violations = validate(username, email, " senha123", cpf, phoneNumber, address);
            assertViolationOnField(violations, "password", "Must not contain leading or trailing spaces");
        }

        @Test
        @DisplayName("deve falhar quando password tiver espaço no final")
        void shouldFail_whenTrailingSpace() {
            var violations = validate(username, email, "senha123 ", cpf, phoneNumber, address);
            assertViolationOnField(violations, "password", "Must not contain leading or trailing spaces");
        }

        @Test
        @DisplayName("deve passar quando password tiver exatamente 8 caracteres")
        void shouldPass_whenExactly8Chars() {
            var violations = validate(username, email, "senha123", cpf, phoneNumber, address);
            assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("password"));
        }

        @Test
        @DisplayName("deve passar quando password tiver exatamente 20 caracteres")
        void shouldPass_whenExactly20Chars() {
            var violations = validate(username, email, "A".repeat(20), cpf, phoneNumber, address);
            assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("password"));
        }
    }

    @Nested
    @DisplayName("cpf")
    final class Cpf {
        @Test
        @DisplayName("deve falhar quando cpf for blank")
        void shouldFail_whenBlank() {
            var violations = validate(username, email, password, "   ", phoneNumber, address);
            assertViolationOnField(violations, "cpf", "Cpf não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando cpf for null")
        void shouldFail_whenNull() {
            var violations = validate(username, email, password, null, phoneNumber, address);
            assertViolationOnField(violations, "cpf", "Cpf não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando cpf tiver menos de 11 caracteres")
        void shouldFail_whenTooShort() {
            var violations = validate(username, email, password, "1234567890", phoneNumber, address);
            assertViolationOnField(violations, "cpf", "Cpf deve ter 11 caracteres");
        }

        @Test
        @DisplayName("deve falhar quando cpf tiver mais de 11 caracteres")
        void shouldFail_whenTooLong() {
            var violations = validate(username, email, password, "123456789012", phoneNumber, address);
            assertViolationOnField(violations, "cpf", "Cpf deve ter 11 caracteres");
        }

        @Test
        @DisplayName("deve falhar quando cpf tiver 11 dígitos mas for inválido (todos iguais)")
        void shouldFail_whenAllSameDigits() {
            var violations = validate(username, email, password, "11111111111", phoneNumber, address);
            assertViolationOnField(violations, "cpf", "Cpf deve ser válido");
        }

        @Test
        @DisplayName("deve falhar quando cpf tiver dígitos verificadores incorretos")
        void shouldFail_whenWrongCheckDigits() {
            var violations = validate(username, email, password, "52998224700", phoneNumber, address);
            assertViolationOnField(violations, "cpf", "Cpf deve ser válido");
        }

        @Test
        @DisplayName("deve passar quando cpf for válido")
        void shouldPass_whenValid() {
            var violations = validate(username, email, password, CPF_VALIDO, phoneNumber, address);
            assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("cpf"));
        }
    }

    @Nested
    @DisplayName("phoneNumber")
    final class PhoneNumber {
        @Test
        @DisplayName("deve falhar quando phoneNumber for blank")
        void shouldFail_whenBlank() {
            var violations = validate(username, email, password, cpf, "   ", address);
            assertViolationOnField(violations, "phoneNumber", "Número de telefone não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando phoneNumber for null")
        void shouldFail_whenNull() {
            var violations = validate(username, email, password, cpf, null, address);
            assertViolationOnField(violations, "phoneNumber", "Número de telefone não pode ser vazio");
        }

        @Test
        @DisplayName("deve falhar quando phoneNumber ultrapassar 20 caracteres")
        void shouldFail_whenExceeds20Chars() {
            var violations = validate(username, email, password, cpf, "1".repeat(21), address);
            assertViolationOnField(violations, "phoneNumber", "Número de telefone deve ser menor que 20 caracteres");
        }

        @Test
        @DisplayName("deve falhar quando phoneNumber tiver espaço no início")
        void shouldFail_whenLeadingSpace() {
            var violations = validate(username, email, password, cpf, " 81999990000", address);
            assertViolationOnField(violations, "phoneNumber", "Must not contain leading or trailing spaces");
        }

        @Test
        @DisplayName("deve falhar quando phoneNumber tiver espaço no final")
        void shouldFail_whenTrailingSpace() {
            var violations = validate(username, email, password, cpf, "81999990000 ", address);
            assertViolationOnField(violations, "phoneNumber", "Must not contain leading or trailing spaces");
        }

        @Test
        @DisplayName("deve passar quando phoneNumber tiver exatamente 20 caracteres")
        void shouldPass_whenExactly20Chars() {
            var violations = validate(username, email, password, cpf, "1".repeat(20), address);
            assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("phoneNumber"));
        }
    }

    @Nested
    @DisplayName("address")
    final class Address {
        @Test
        @DisplayName("deve falhar quando address for null")
        void shouldFail_whenNull() {
            var violations = validate(username, email, password, cpf, phoneNumber, null);
            assertViolationOnField(violations, "address", "Endereço não pode ser nulo");
        }

        @Test
        @DisplayName("deve falhar quando address ultrapassar 100 caracteres")
        void shouldFail_whenExceeds100Chars() {
            var violations = validate(username, email, password, cpf, phoneNumber, "A".repeat(101));
            assertViolationOnField(violations, "address", "Endereço deve ser menor que 100 caracteres");
        }

        @Test
        @DisplayName("deve falhar quando address tiver espaço no início")
        void shouldFail_whenLeadingSpace() {
            var violations = validate(username, email, password, cpf, phoneNumber, " Rua A");
            assertViolationOnField(violations, "address", "Must not contain leading or trailing spaces");
        }

        @Test
        @DisplayName("deve falhar quando address tiver espaço no final")
        void shouldFail_whenTrailingSpace() {
            var violations = validate(username, email, password, cpf, phoneNumber, "Rua A ");
            assertViolationOnField(violations, "address", "Must not contain leading or trailing spaces");
        }

        @Test
        @DisplayName("deve passar quando address tiver exatamente 100 caracteres")
        void shouldPass_whenExactly100Chars() {
            var violations = validate(username, email, password, cpf, phoneNumber, "A".repeat(100));
            assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("address"));
        }
    }
}
