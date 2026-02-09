package com.system.application.shared.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CpfValidatorTest {

    private final CpfValidator validator = CpfValidator.getInstance();

    @Test
    void deveRetornarTrueParaCpfValido() {
        String cpfValido = "529.982.247-25";
        Boolean result = validator.isValid(cpfValido);
        assertTrue(result);
    }

    @Test
    void deveRetornarFalseParaCpfInvalido() {
        String cpfInvalido = "529.982.247-26";
        Boolean result = validator.isValid(cpfInvalido);
        assertFalse(result);
    }

    @Test
    void deveRetornarFalseParaCpfNulo() {
        Boolean result = validator.isValid(null);
        assertFalse(result);
    }

    @Test
    void deveRetornarFalseParaCpfComMenosDe11Digitos() {
        String cpfCurto = "1234567890";
        Boolean result = validator.isValid(cpfCurto);
        assertFalse(result);
    }

    @Test
    void deveRetornarFalseParaCpfComMaisDe11Digitos() {
        String cpfLongo = "123456789012";
        Boolean result = validator.isValid(cpfLongo);
        assertFalse(result);
    }

    @Test
    void deveRetornarFalseParaCpfComDigitosRepetidos() {
        String cpfRepetido = "111.111.111-11";
        Boolean result = validator.isValid(cpfRepetido);
        assertFalse(result);
    }

    @Test
    void deveIgnorarCaracteresNaoNumericos() {
        String cpfComMascara = "529.982.247-25";
        Boolean result = validator.isValid(cpfComMascara);
        assertTrue(result);
    }

    @Test
    void deveRetornarFalseParaCpfComLetras() {
        String cpfComLetras = "ABC.DEF.GHI-JK";
        Boolean result = validator.isValid(cpfComLetras);
        assertFalse(result);
    }
}

