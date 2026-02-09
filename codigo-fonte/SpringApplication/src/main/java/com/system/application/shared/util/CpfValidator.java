package com.system.application.shared.util;

public final class CpfValidator {
    private CpfValidator() {}

    public static CpfValidator getInstance() {
        return new CpfValidator();
    }

    public Boolean isValid(String cpf) {
        if (cpf == null) return false;
        cpf = cpf.replaceAll("\\D", "");
        if (cpf.length() != 11) return false;
        if (cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += (cpf.charAt(i) - '0') * (10 - i);
            }

            int digito1 = 11 - (soma % 11);
            digito1 = (digito1 >= 10) ? 0 : digito1;

            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += (cpf.charAt(i) - '0') * (11 - i);
            }

            int digito2 = 11 - (soma % 11);
            digito2 = (digito2 >= 10) ? 0 : digito2;

            return digito1 == (cpf.charAt(9) - '0')
                && digito2 == (cpf.charAt(10) - '0');

        } catch (NumberFormatException e) {
            return false;
        }
    }
}