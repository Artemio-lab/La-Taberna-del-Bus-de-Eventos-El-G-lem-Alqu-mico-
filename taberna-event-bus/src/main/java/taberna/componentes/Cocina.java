package taberna.componentes;

import taberna.bus.BusDeEventos;
import taberna.bus.Suscriptor;
import taberna.dominio.Menu;
import taberna.eventos.ComidaPreparadaEvent;
import taberna.eventos.PedidoRealizadoEvent;
import taberna.util.Consola;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Suscriptor y publicador: escucha {@link PedidoRealizadoEvent}, cocina los platos
 * (5 segundos cada uno) y publica un {@link ComidaPreparadaEvent}.
 */
public final class Cocina implements Suscriptor<PedidoRealizadoEvent> {

    public static final int SEGUNDOS_POR_PLATO = 5;

    private final BusDeEventos bus;
    private final ScheduledExecutorService reloj;

    public Cocina(BusDeEventos bus, ScheduledExecutorService reloj) {
        this.bus = bus;
        this.reloj = reloj;
    }

    public void iniciar() {
        bus.suscribir(PedidoRealizadoEvent.class, this);
    }

    public void detener() {
        bus.desuscribir(PedidoRealizadoEvent.class, this);
    }

    @Override
    public void alRecibir(PedidoRealizadoEvent pedido) {
        for (String item : pedido.items()) {
            if (Menu.esComida(item)) {
                Consola.imprimir(Consola.AMARILLO, "COCINA",
                        "Cocinando " + item + " para " + pedido.pedidoId() + "...");
                reloj.schedule(() -> {
                    Consola.imprimir(Consola.AMARILLO, "COCINA", "Plato listo: " + item + " (" + pedido.pedidoId() + ")");
                    bus.publicar(new ComidaPreparadaEvent(pedido.pedidoId(), item));
                }, SEGUNDOS_POR_PLATO, TimeUnit.SECONDS);
            }
        }
    }
}
