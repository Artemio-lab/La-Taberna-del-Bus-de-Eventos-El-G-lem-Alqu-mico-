package taberna.util;

import java.util.Locale;

/**
 * Salida de consola coordinada: cada línea se imprime de forma atómica (los hilos no se
 * mezclan), lleva el tiempo transcurrido desde el arranque y un color por componente.
 */
public final class Consola {

    public static final String RESET = "\u001B[0m";
    public static final String NEGRITA = "\u001B[1m";
    public static final String ROJO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARILLO = "\u001B[33m";
    public static final String AZUL = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CIAN = "\u001B[36m";
    public static final String GRIS = "\u001B[90m";
    public static final String MAGENTA_BRILLANTE = "\u001B[95m";
    public static final String CIAN_BRILLANTE = "\u001B[96m";
    public static final String AMARILLO_BRILLANTE = "\u001B[93m";

    private static final long INICIO = System.nanoTime();
    private static final Object CERROJO = new Object();

    private Consola() {
    }

    public static void imprimir(String color, String componente, String mensaje) {
        double segundos = (System.nanoTime() - INICIO) / 1_000_000_000.0;
        String linea = String.format(Locale.ROOT, "%s[%6.2fs] %-12s %s%s",
                color, segundos, "[" + componente + "]", mensaje, RESET);
        synchronized (CERROJO) {
            System.out.println(linea);
        }
    }

    public static void titulo(String texto) {
        synchronized (CERROJO) {
            System.out.println();
            System.out.println(NEGRITA + AMARILLO + "=== " + texto + " ===" + RESET);
        }
    }
}
