package taberna.componentes;

import taberna.bus.BusDeEventos;
import taberna.bus.Suscriptor;
import taberna.eventos.BandaTocandoEvent;
import taberna.util.Consola;

/**
 * EXTENSIÓN 1: dispara humo cuando suena la canción épica.
 * Se agregó sin modificar Banda, EventBus ni ningún otro componente.
 */
public final class SistemaDeHumo implements Suscriptor<BandaTocandoEvent> {

    public static final String CANCION_EPICA = "Through the Fire and Flames";

    private final BusDeEventos bus;

    public SistemaDeHumo(BusDeEventos bus) {
        this.bus = bus;
    }

    public void iniciar() {
        bus.suscribir(BandaTocandoEvent.class, this);
    }

    public void detener() {
        bus.desuscribir(BandaTocandoEvent.class, this);
    }

    @Override
    public void alRecibir(BandaTocandoEvent evento) {
        if (CANCION_EPICA.equalsIgnoreCase(evento.nombreCancion())) {
            Consola.imprimir(Consola.ROJO, "HUMO", "¡Canción épica detectada! Activando máquinas de humo...");
        }
    }
}
