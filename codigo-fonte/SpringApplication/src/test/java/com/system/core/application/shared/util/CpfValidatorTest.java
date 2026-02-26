package com.system.core.application.shared.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CpfValidatorTest {
    private CpfValidator validator;

    @BeforeEach
    void setUp() {
        validator = CpfValidator.getInstance();
    }

    @Test
    @DisplayName("getInstance() deve retornar instância não nula")
    void testGetInstanceNotNull() {
        assertNotNull(CpfValidator.getInstance());
    }

    @Test
    @DisplayName("getInstance() deve retornar instâncias diferentes a cada chamada")
    void testGetInstanceReturnsDifferentInstances() {
        CpfValidator a = CpfValidator.getInstance();
        CpfValidator b = CpfValidator.getInstance();
        assertNotSame(a, b);
    }

    @Test
    @DisplayName("CPF válido sem formatação deve retornar true")
    void testCpfValidoSemFormatacao() {
        assertTrue(validator.isValid("52998224725"));
    }

    @Test
    @DisplayName("CPF válido com pontos e traço deve retornar true")
    void testCpfValidoComMascara() {
        assertTrue(validator.isValid("529.982.247-25"));
    }

    @Test
    @DisplayName("CPF válido com espaços deve retornar true")
    void testCpfValidoComEspacos() {
        assertTrue(validator.isValid("529 982 247 25"));
    }

    @ParameterizedTest
    @DisplayName("Múltiplos CPFs válidos devem retornar true")
    @ValueSource(strings = {
            "11144477735",
            "52998224725",
            "71428793860",
            "58974338017"
    })
    void testMultiplosCpfsValidos(String cpf) {
        assertTrue(validator.isValid(cpf));
    }

    @Test
    @DisplayName("CPF nulo deve retornar false")
    void testCpfNulo() {
        assertFalse(validator.isValid(null));
    }

    @Test
    @DisplayName("CPF vazio deve retornar false")
    void testCpfVazio() {
        assertFalse(validator.isValid(""));
    }

    @Test
    @DisplayName("CPF com menos de 11 dígitos deve retornar false")
    void testCpfCurto() {
        assertFalse(validator.isValid("1234567890"));
    }

    @Test
    @DisplayName("CPF com mais de 11 dígitos deve retornar false")
    void testCpfLongo() {
        assertFalse(validator.isValid("123456789012"));
    }

    @Test
    @DisplayName("CPF com dígito verificador incorreto deve retornar false")
    void testCpfComDigitoVerificadorErrado() {
        assertFalse(validator.isValid("52998224700"));
    }

    @ParameterizedTest
    @DisplayName("CPFs com todos os dígitos iguais (sequências) devem retornar false")
    @ValueSource(strings = {
            "00000000000",
            "11111111111",
            "22222222222",
            "33333333333",
            "44444444444",
            "55555555555",
            "66666666666",
            "77777777777",
            "88888888888",
            "99999999999"
    })
    void testCpfSequenciaRepetida(String cpf) {
        assertFalse(validator.isValid(cpf));
    }

    @Test
    @DisplayName("CPF com apenas letras deve retornar false")
    void testCpfApenasLetras() {
        assertFalse(validator.isValid("abcdefghijk"));
    }

    @Test
    @DisplayName("CPF com letras e números misturados (11 dígitos após limpar) deve ser validado corretamente")
    void testCpfComLetrasENumeros() {
        // "529.ABC.247-25" -> após replaceAll("\\D","") -> "52924725" -> 8 dígitos -> false
        assertFalse(validator.isValid("529.ABC.247-25"));
    }

    @Test
    @DisplayName("CPF com formatação mas dígitos errados deve retornar false")
    void testCpfFormatadoInvalido() {
        assertFalse(validator.isValid("111.444.777-36"));
    }

    @Test
    @DisplayName("isValid() deve retornar Boolean (não primitivo) — compatível com null safety")
    void testRetornoEBoolean() {
        Boolean resultado = validator.isValid("52998224725");
        assertInstanceOf(Boolean.class, resultado);
    }

    @Test
    @DisplayName("isValid() para CPF inválido deve retornar Boolean false")
    void testRetornoFalseEBoolean() {
        Boolean resultado = validator.isValid(null);
        assertNotNull(resultado);
        assertFalse(resultado);
    }
}

