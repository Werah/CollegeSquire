package com.collegesquire.validation;

public final class Cpf {

    private Cpf() {
    }

    public static boolean valido(String cpf) {

        if (cpf == null) {
            return false;
        }

        String d = cpf.replaceAll("\\D", "");

        if (d.length() != 11 || d.chars().distinct().count() == 1) {
            return false;
        }

        for (int t = 9; t < 11; t++) {
            int soma = 0;
            for (int i = 0; i < t; i++) {
                soma += (d.charAt(i) - '0') * (t + 1 - i);
            }
            int digito = ((soma * 10) % 11) % 10;
            if (digito != d.charAt(t) - '0') {
                return false;
            }
        }

        return true;
    }
}
