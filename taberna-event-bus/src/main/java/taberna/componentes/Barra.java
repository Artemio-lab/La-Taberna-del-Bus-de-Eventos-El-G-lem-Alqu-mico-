package taberna.componentes;

import taberna.bus.BusDeEventos;
import taberna.bus.Suscriptor;
import taberna.dominio.Menu;
import taberna.eventos.BebidaServidaEvent;
import taberna.eventos.PedidoRealizadoEvent;
import taberna.util.Consola;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Suscriptor y publicador: escucha {@link PedidoRealizadoEvent}, prepara las bebidas
 * (2 segundos cada una) y publica un {@link BebidaServidaEvent}.
 */
public final class Barra implements Suscriptor<PedidoRealizadoEvent> {

    public static final int SEGUNDOS_POR_BEBIDA = 2;

    private final BusDeEventos bus;
    private final ScheduledExecutorService reloj;

    public Barra(BusDeEventos bus, ScheduledExecutorService reloj) {
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
            if (Menu.esBebida(item)) {
                Consola.imprimir(Consola.CIAN, "BARRA",
                        "Preparando " + item + " para la Mesa " + pedido.mesaId() + "...");
                reloj.schedule(() -> {
                    Consola.imprimir(Consola.CIAN, "BARRA", "Bebida lista: " + item + " (Mesa " + pedido.mesaId() + ")");
                    bus.publicar(new BebidaServidaEvent(pedido.mesaId(), item));
                }, SEGUNDOS_POR_BEBIDA, TimeUnit.SECONDS);
            }
        }
    }
}
