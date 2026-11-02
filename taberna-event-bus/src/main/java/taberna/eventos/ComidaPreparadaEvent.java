package taberna.eventos;

import taberna.bus.Evento;

import java.util.Objects;

/** La cocina terminó de preparar un plato de un pedido. */
public record ComidaPreparadaEvent(String pedidoId, String plato) implements Evento {

    public ComidaPreparadaEvent {
        Objects.requireNonNull(pedidoId, "pedidoId");
        Objects.requireNonNull(plato, "plato");
    }
}
