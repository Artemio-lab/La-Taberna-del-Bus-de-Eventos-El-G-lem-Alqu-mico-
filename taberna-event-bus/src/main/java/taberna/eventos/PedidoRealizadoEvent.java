package taberna.eventos;

import taberna.bus.Evento;

import java.util.List;
import java.util.Objects;

/** Un cliente hizo un pedido. Inmutable: record + copia defensiva de la lista. */
public record PedidoRealizadoEvent(int mesaId, String pedidoId, List<String> items) implements Evento {

    public PedidoRealizadoEvent {
        if (mesaId <= 0) {
            throw new IllegalArgumentException("mesaId debe ser positivo");
        }
        Objects.requireNonNull(pedidoId, "pedidoId");
        items = List.copyOf(items); // lista inmodificable; lanza NPE si es null
        if (items.isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos un item");
        }
    }
}
