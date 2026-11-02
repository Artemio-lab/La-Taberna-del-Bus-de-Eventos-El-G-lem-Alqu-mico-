package taberna.componentes;

import taberna.bus.BusDeEventos;
import taberna.eventos.BandaTocandoEvent;
import taberna.util.Consola;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Publicador: toca una canción del repertorio cada X segundos y publica un
 * {@link BandaTocandoEvent}. No sabe quién la escucha (ni si alguien lo hace).
 */
public final class Banda {

    public record Cancion(String nombre, int duracionSegundos) {
    }

    private final BusDeEventos bus;
    private final ScheduledExecutorService reloj;
    private final String nombre;
    private final List<Cancion> repertorio;
    private int siguiente = 0;
    private ScheduledFuture<?> tarea;

    public Banda(BusDeEventos bus, ScheduledExecutorService reloj, String nombre, List<Cancion> repertorio) {
        if (repertorio.isEmpty()) {
            throw new IllegalArgumentException("El repertorio no puede estar vacío");
        }
        this.bus = bus;
        this.reloj = reloj;
        this.nombre = nombre;
        this.repertorio = List.copyOf(repertorio);
    }

    public void iniciar(long retardoInicialSeg, long cadaSegundos) {
        tarea = reloj.scheduleAtFixedRate(this::tocarSiguiente, retardoInicialSeg, cadaSegundos, TimeUnit.SECONDS);
    }

    public void detener() {
        if (tarea != null) {
            tarea.cancel(false);
        }
    }

    private void tocarSiguiente() {
        try {
            Cancion cancion = repertorio.get(siguiente++ % repertorio.size());
            Consola.imprimir(Consola.VERDE, "BANDA",
                    nombre + " empieza a tocar \"" + cancion.nombre() + "\" (" + cancion.duracionSegundos() + " s)");
            bus.publicar(new BandaTocandoEvent(nombre, cancion.nombre(), cancion.duracionSegundos()));
        } catch (RuntimeException e) {
            // Un fallo puntual no debe cancelar las siguientes canciones.
            Consola.imprimir(Consola.ROJO, "BANDA", "Error al tocar: " + e.getMessage());
        }
    }
}
