package taberna.componentes;

import taberna.bus.BusDeEventos;
import taberna.eventos.PedidoRealizadoEvent;
import taberna.util.Consola;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/** Publicador: recibe pedidos de los clientes y publica un {@link PedidoRealizadoEvent}. */
public final class SistemaDePedidos {

    private final BusDeEventos bus;
    private final AtomicInteger contador = new AtomicInteger(122); // el primer pedido es pedido_123

    public SistemaDePedidos(BusDeEventos bus) {
        this.bus = bus;
    }

    public String realizarPedido(int mesaId, String... items) {
        String pedidoId = "pedido_" + contador.incrementAndGet();
        Consola.imprimir(Consola.AZUL, "PEDIDOS",
                "Mesa " + mesaId + " pide " + List.of(items) + " -> " + pedidoId);
        bus.publicar(new PedidoRealizadoEvent(mesaId, pedidoId, List.of(items)));
        return pedidoId;
    }
}
