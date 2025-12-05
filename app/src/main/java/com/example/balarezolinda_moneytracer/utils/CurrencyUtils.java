package com.example.balarezolinda_moneytracer.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtils {

    // Devuelve formato monetario local (Ej: $ 12.50)
    public static String format(double value) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        return format.format(value);
    }

    // Permite quitar símbolo si lo necesitas
    public static String formatWithoutSymbol(double value) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        String result = format.format(value);

        // Quita símbolos como $, USD, etc
        return result.replaceAll("[^0-9.,-]", "");
    }
}
