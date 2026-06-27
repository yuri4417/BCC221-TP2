package ufop.poo.tp2.util;

public class FormatarNumero {
    public static String comQuatroCasas(double valor) {
        return String.format("%.4f", valor);
    }
    
    public static String comoPercentual(double valor) {
        return String.format("%.2f%%", valor);
    }
    
    public static String comDuasCasas(double valor) {
        return String.format("%.2f", valor);
    }
}