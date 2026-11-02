package taberna.componentes;

import taberna.bus.BusDeEventos;
import taberna.bus.Suscriptor;
import taberna.eventos.BandaTocandoEvent;
import taberna.eventos.BebidaServidaEvent;
import taberna.eventos.ComidaPreparadaEvent;
import taberna.eventos.PedidoRealizadoEvent;
import taberna.util.Consola;

/** Suscriptor a una selección de eventos: muestra anuncios coloridos en consola. */
public final class PanelLED {

    private static final String ETIQUETA = "PANEL LED";

    private final BusDeEventos bus;

    // Se guardan como campos para poder desuscribirlos después (misma instancia).
    private final Suscriptor<PedidoRealizadoEvent> alPedido = e -> Consola.imprimir(Consola.MAGENTA_BRILLANTE,
            ETIQUETA, "¡Nuevo pedido en la Mesa " + e.mesaId() + "!");
    private final Suscriptor<BebidaServidaEvent> alBebida = e -> Consola.imprimir(Consola.CIAN_BRILLANTE,
            ETIQUETA, "¡Nueva bebida servida en la Mesa " + e.mesaId() + "! (" + e.bebida() + ")");
    private final Suscriptor<ComidaPreparadaEvent> alComida = e -> Consola.imprimir(Consola.AMARILLO_BRILLANTE,
            ETIQUETA, "¡" + capitalizar(e.plato()) + " lista para " + e.pedidoId() + "!");
    private final Suscriptor<BandaTocandoEvent> alBanda = e -> Consola.imprimir(Consola.VERDE,
            ETIQUETA, "♪ " + e.nombreBanda() + " - " + e.nombreCancion() + " ♪");

    public PanelLED(BusDeEventos bus) {
        this.bus = bus;
    }

    public void iniciar() {
        bus.suscribir(PedidoRealizadoEvent.class, alPedido);
        bus.suscribir(BebidaServidaEvent.class, alBebida);
        bus.suscribir(ComidaPreparadaEvent.class, alComida);
        bus.suscribir(BandaTocandoEvent.class, alBanda);
    }

    public void detener() {
        bus.desuscribir(PedidoRealizadoEvent.class, alPedido);
        bus.desuscribir(BebidaServidaEvent.class, alBebida);
        bus.desuscribir(ComidaPreparadaEvent.class, alComida);
        bus.desuscribir(BandaTocandoEvent.class, alBanda);
    }

    private static String capitalizar(String texto) {
        return texto.isEmpty() ? texto : Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }
}
