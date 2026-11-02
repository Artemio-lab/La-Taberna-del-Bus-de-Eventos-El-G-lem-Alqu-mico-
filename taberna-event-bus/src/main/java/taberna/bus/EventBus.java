package taberna.bus;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

/**
 * Implementación del Event Bus.
 *
 * <ul>
 *   <li><b>Dinámico:</b> los suscriptores pueden entrar y salir en cualquier momento.</li>
 *   <li><b>Thread-safe:</b> registro con {@link ConcurrentHashMap} y {@link CopyOnWriteArrayList}.</li>
 *   <li><b>Asíncrono:</b> cada notificación se entrega en un hilo del bus, de modo que un
 *       suscriptor lento no bloquea al publicador ni a los demás suscriptores.</li>
 *   <li><b>Aislado ante fallos:</b> una excepción en un suscriptor no afecta a los demás.</li>
 *   <li><b>Polimórfico:</b> un suscriptor de {@code Evento.class} recibe TODOS los eventos.</li>
 * </ul>
 */
public final class EventBus implements BusDeEventos, AutoCloseable {

    private final Map<Class<? extends Evento>, List<Suscriptor<?>>> registro = new ConcurrentHashMap<>();
    private final ExecutorService despachador;
    private final BiConsumer<Evento, Throwable> manejadorDeErrores;
    private volatile boolean cerrado = false;

    public EventBus() {
        this(Executors.newCachedThreadPool(new FabricaDeHilos()),
                (evento, error) -> System.err.println(
                        "[EventBus] Error al entregar " + evento + ": " + error));
    }

    public EventBus(ExecutorService despachador, BiConsumer<Evento, Throwable> manejadorDeErrores) {
        this.despachador = Objects.requireNonNull(despachador);
        this.manejadorDeErrores = Objects.requireNonNull(manejadorDeErrores);
    }

    @Override
    public <E extends Evento> void suscribir(Class<E> tipoDeEvento, Suscriptor<? super E> suscriptor) {
        Objects.requireNonNull(tipoDeEvento, "tipoDeEvento");
        Objects.requireNonNull(suscriptor, "suscriptor");
        CopyOnWriteArrayList<Suscriptor<?>> lista = (CopyOnWriteArrayList<Suscriptor<?>>)
                registro.computeIfAbsent(tipoDeEvento, t -> new CopyOnWriteArrayList<>());
        lista.addIfAbsent(suscriptor); // evita suscripciones duplicadas
    }

    @Override
    public <E extends Evento> boolean desuscribir(Class<E> tipoDeEvento, Suscriptor<? super E> suscriptor) {
        Objects.requireNonNull(tipoDeEvento, "tipoDeEvento");
        Objects.requireNonNull(suscriptor, "suscriptor");
        List<Suscriptor<?>> lista = registro.get(tipoDeEvento);
        return lista != null && lista.remove(suscriptor);
    }

    @Override
    public void publicar(Evento evento) {
        Objects.requireNonNull(evento, "evento");
        if (cerrado) {
            throw new IllegalStateException("El EventBus ya fue cerrado");
        }
        for (Map.Entry<Class<? extends Evento>, List<Suscriptor<?>>> entrada : registro.entrySet()) {
            if (entrada.getKey().isInstance(evento)) {
                for (Suscriptor<?> suscriptor : entrada.getValue()) {
                    entregar(evento, suscriptor);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void entregar(Evento evento, Suscriptor<?> suscriptor) {
        try {
            despachador.execute(() -> {
                try {
                    ((Suscriptor<Evento>) suscriptor).alRecibir(evento);
                } catch (Throwable error) {
                    manejadorDeErrores.accept(evento, error);
                }
            });
        } catch (RejectedExecutionException cierreEnCurso) {
            manejadorDeErrores.accept(evento, cierreEnCurso);
        }
    }

    /** Deja de aceptar eventos y espera a que se entreguen los que ya estaban en curso. */
    @Override
    public void close() {
        cerrado = true;
        despachador.shutdown();
        try {
            if (!despachador.awaitTermination(10, TimeUnit.SECONDS)) {
                despachador.shutdownNow();
            }
        } catch (InterruptedException e) {
            despachador.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static final class FabricaDeHilos implements ThreadFactory {
        private final AtomicInteger numero = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable tarea) {
            Thread hilo = new Thread(tarea, "event-bus-" + numero.getAndIncrement());
            hilo.setDaemon(true);
            return hilo;
        }
    }
}
