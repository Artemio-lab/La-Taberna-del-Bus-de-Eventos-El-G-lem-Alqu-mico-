package taberna.componentes;

import taberna.bus.BusDeEventos;
import taberna.bus.Suscriptor;
import taberna.eventos.BandaTocandoEvent;
import taberna.util.Consola;

/** Suscriptor: ajusta el ecualizador cada vez que la banda toca una canción. */
public final class SistemaDeSonido implements Suscriptor<BandaTocandoEvent> {

    private final BusDeEventos bus;

    public SistemaDeSonido(BusDeEventos bus) {
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
        Consola.imprimir(Consola.GRIS, "SONIDO", "Ajustando ecualizador para: " + evento.nombreCancion());
    }
}
