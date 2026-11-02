package taberna.dominio;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;

/** Catálogo de la taberna: permite saber si un item es bebida (barra) o comida (cocina). */
public final class Menu {

    private static final Set<String> BEBIDAS =
            Set.of("cerveza", "vino", "refresco", "agua", "hidromiel", "limonada");
    private static final Set<String> COMIDAS =
            Set.of("hamburguesa", "pizza", "alitas", "nachos", "tacos", "papas");

    private Menu() {
    }

    public static boolean esBebida(String item) {
        return BEBIDAS.contains(normalizar(item));
    }

    public static boolean esComida(String item) {
        return COMIDAS.contains(normalizar(item));
    }

    private static String normalizar(String texto) {
        String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinAcentos.trim().toLowerCase(Locale.ROOT);
    }
}
