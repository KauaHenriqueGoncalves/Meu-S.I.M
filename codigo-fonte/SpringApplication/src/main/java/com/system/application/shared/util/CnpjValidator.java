package com.system.application.shared.util;

public final class CnpjValidator {
    private static CnpjValidator instance = null;

    public static CnpjValidator getInstance() {
        if (instance == null) {
            instance = new CnpjValidator();
        }
        return instance;
    }

    public Boolean isValid(String cnpj) {
        if (cnpj == null) return false;
        cnpj = cnpj.replaceAll("\\D", "");
        if (cnpj.length() != 14) return false;
        if (cnpj.matches("(\\d)\\1{13}")) return false;
        try {
            int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int soma = 0;
            for (int i = 0; i < 12; i++) {
                soma += Character.getNumericValue(cnpj.charAt(i)) * pesos1[i];
            }
            int resto = soma % 11;
            int digito1 = (resto < 2) ? 0 : 11 - resto;
            soma = 0;
            for (int i = 0; i < 13; i++) {
                soma += Character.getNumericValue(cnpj.charAt(i)) * pesos2[i];
            }
            resto = soma % 11;
            int digito2 = (resto < 2) ? 0 : 11 - resto;
            return digito1 == Character.getNumericValue(cnpj.charAt(12))
                && digito2 == Character.getNumericValue(cnpj.charAt(13));
        } catch (Exception e) {
            return false;
        }
    }
}
