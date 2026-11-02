package taberna.eventos;

import taberna.bus.Evento;

import java.util.Objects;

/** La banda empezó a tocar una canción. La duración está en segundos. */
public record BandaTocandoEvent(String nombreBanda, String nombreCancion, int duracionSegundos) implements Evento {

    public BandaTocandoEvent {
        Objects.requireNonNull(nombreBanda, "nombreBanda");
        Objects.requireNonNull(nombreCancion, "nombreCancion");
        if (duracionSegundos <= 0) {
            throw new IllegalArgumentException("La duración debe ser positiva");
        }
    }
}
