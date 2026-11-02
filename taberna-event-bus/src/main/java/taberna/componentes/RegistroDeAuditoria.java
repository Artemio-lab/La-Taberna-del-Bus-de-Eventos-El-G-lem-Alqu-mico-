package taberna.componentes;

import taberna.bus.BusDeEventos;
import taberna.bus.Evento;
import taberna.bus.Suscriptor;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * EXTENSIÓN 3: se suscribe a {@link Evento} (TODOS los eventos) y cuenta cuántos pasaron
 * por el bus. Se enteraría también de eventos que se creen en el futuro.
 */
public final class RegistroDeAuditoria implements Suscriptor<Evento> {

    private final BusDeEventos bus;
    private final Map<String, LongAdder> conteo = new ConcurrentHashMap<>();

    public RegistroDeAuditoria(BusDeEventos bus) {
        this.bus = bus;
    }

    public void iniciar() {
        bus.suscribir(Evento.class, this);
    }

    public void detener() {
        bus.desuscribir(Evento.class, this);
    }

    @Override
    public void alRecibir(Evento evento) {
        conteo.computeIfAbsent(evento.getClass().getSimpleName(), k -> new LongAdder()).increment();
    }

    public Map<String, Long> resumen() {
        Map<String, Long> ordenado = new TreeMap<>();
        conteo.forEach((tipo, total) -> ordenado.put(tipo, total.sum()));
        return ordenado;
    }
}
