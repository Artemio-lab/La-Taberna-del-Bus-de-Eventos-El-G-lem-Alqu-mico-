package taberna.eventos;

import taberna.bus.Evento;

import java.util.Objects;

/** La barra sirvió una bebida en una mesa. */
public record BebidaServidaEvent(int mesaId, String bebida) implements Evento {

    public BebidaServidaEvent {
        if (mesaId <= 0) {
            throw new IllegalArgumentException("mesaId debe ser positivo");
        }
        Objects.requireNonNull(bebida, "bebida");
    }
}
