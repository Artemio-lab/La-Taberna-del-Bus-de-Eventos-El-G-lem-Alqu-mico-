package taberna.componentes;

import taberna.bus.BusDeEventos;
import taberna.bus.Suscriptor;
import taberna.eventos.BandaTocandoEvent;
import taberna.util.Consola;

/** EXTENSIÓN 2: cambia las luces del escenario según la canción. Sin tocar código existente. */
public final class LucesEscenicas implements Suscriptor<BandaTocandoEvent> {

    private final BusDeEventos bus;

    public LucesEscenicas(BusDeEventos bus) {
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
        String modo = evento.duracionSegundos() >= 240 ? "luces estroboscópicas" : "luces cálidas";
        Consola.imprimir(Consola.AMARILLO_BRILLANTE, "LUCES", "Escenario en modo " + modo);
    }
}
