package com.system.application.shared.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CnpjValidatorTest {

    private final CnpjValidator validator = CnpjValidator.getInstance();

    @Test
    void deveRetornarTrueParaCnpjValido() {
        String cnpjValido = "04.252.011/0001-10";
        Boolean result = validator.isValid(cnpjValido);
        assertTrue(result);
    }

    @Test
    void deveRetornarFalseParaCnpjInvalido() {
        String cnpjInvalido = "04.252.011/0001-11";
        Boolean result = validator.isValid(cnpjInvalido);
        assertFalse(result);
    }

    @Test
    void deveRetornarFalseParaCnpjNulo() {
        Boolean result = validator.isValid(null);
        assertFalse(result);
    }

    @Test
    void deveRetornarFalseParaCnpjComMenosDe14Digitos() {
        String cnpjCurto = "1234567890123";
        Boolean result = validator.isValid(cnpjCurto);
        assertFalse(result);
    }

    @Test
    void deveRetornarFalseParaCnpjComMaisDe14Digitos() {
        String cnpjLongo = "123456789012345";
        Boolean result = validator.isValid(cnpjLongo);
        assertFalse(result);
    }

    @Test
    void deveRetornarFalseParaCnpjComDigitosRepetidos() {
        String cnpjRepetido = "11.111.111/1111-11";
        Boolean result = validator.isValid(cnpjRepetido);
        assertFalse(result);
    }

    @Test
    void deveIgnorarCaracteresNaoNumericos() {
        String cnpjComMascara = "04.252.011/0001-10";
        Boolean result = validator.isValid(cnpjComMascara);
        assertTrue(result);
    }

    @Test
    void deveRetornarFalseParaStringComLetras() {
        String cnpjComLetras = "AB.CDE.FGH/IJKL-MN";
        Boolean result = validator.isValid(cnpjComLetras);
        assertFalse(result);
    }
}
